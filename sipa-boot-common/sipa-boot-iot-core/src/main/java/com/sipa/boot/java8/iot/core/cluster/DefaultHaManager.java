package com.sipa.boot.java8.iot.core.cluster;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.redis.core.ReactiveHashOperations;
import org.springframework.data.redis.core.ReactiveRedisOperations;

import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;
import com.sipa.boot.java8.iot.core.cluster.base.IClusterManager;
import com.sipa.boot.java8.iot.core.cluster.base.IClusterTopic;
import com.sipa.boot.java8.iot.core.cluster.base.IHaManager;

import reactor.core.Disposable;
import reactor.core.publisher.EmitterProcessor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxProcessor;
import reactor.core.publisher.Mono;

/**
 * @author caszhou
 * @date 2021/10/2
 */
public class DefaultHaManager implements IHaManager {
    private static final Log log = LogFactory.get(DefaultHaManager.class);

    private final ServerNode current;

    private final String haName;

    private final IClusterTopic<ServerNode> offlineTopic;

    private final Map<String, ServerNode> allNode = new ConcurrentHashMap<>();

    private final IClusterTopic<ServerNode> keepalive;

    private final ReactiveHashOperations<String, String, ServerNode> inRedisNode;

    private final String allNodeHashKey;

    private final FluxProcessor<ServerNode, ServerNode> onlineProcessor = EmitterProcessor.create(false);

    private final FluxProcessor<ServerNode, ServerNode> offlineProcessor = EmitterProcessor.create(false);

    private volatile boolean started = false;

    public DefaultHaManager(String name, ServerNode current, IClusterManager clusterManager,
        ReactiveRedisOperations<String, ServerNode> operations) {
        this.haName = name;
        this.current = current.copy();
        this.current.setUptime(System.currentTimeMillis());
        this.current.setLeader(false);
        this.inRedisNode = operations.opsForHash();
        this.offlineTopic = clusterManager.getTopic("__ha_offline_topic:".concat(haName));
        this.keepalive = clusterManager.getTopic("__ha_keepalive:".concat(haName));
        this.allNodeHashKey = "__ha_all_node:".concat(haName);
    }

    public void checkAlive() {
        current.setLastKeepAlive(System.currentTimeMillis());

        inRedisNode.put(allNodeHashKey, current.getId(), current).subscribe();

        keepalive.publish(Mono.just(current)).subscribe();

        Map<String,
            ServerNode> maybeOffline = getAllNode().stream()
                .filter(node -> System.currentTimeMillis() - node.getLastKeepAlive() > TimeUnit.SECONDS.toMillis(30))
                .filter(node -> !node.isSame(current))
                .collect(Collectors.toMap(ServerNode::getId, Function.identity()));

        // 检查节点是否离线
        inRedisNode.keys(allNodeHashKey)
            .filter(maybeOffline::containsKey)
            .map(maybeOffline::get)
            .collectList()
            .filter(list -> !list.isEmpty())
            .flatMapMany(list -> inRedisNode.remove(allNodeHashKey, list.stream().map(ServerNode::getId).toArray())
                .thenMany(Flux.fromIterable(list)))
            .as(offlineTopic::publish)
            .subscribe();
    }

    private void electionLeader() {
        allNode.values()
            .stream()
            .peek(serverNode -> serverNode.setLeader(false))
            .min(Comparator.comparing(ServerNode::getUptime))
            .ifPresent(serverNode -> serverNode.setLeader(true));
    }

    public void shutdown() {
        inRedisNode.remove(allNodeHashKey, current.getId()).then(offlineTopic.publish(Mono.just(current))).block();
    }

    public synchronized void startup() {
        if (started) {
            return;
        }
        started = true;
        allNode.put(current.getId(), current);

        // 注册自己
        inRedisNode.put(allNodeHashKey, current.getId(), current)
            .flatMapMany(r -> inRedisNode.values(allNodeHashKey))
            .collectList()
            .doOnNext(node -> {
                for (ServerNode serverNode : node) {
                    serverNode.setLastKeepAlive(System.currentTimeMillis());
                    allNode.put(serverNode.getId(), serverNode);
                }
                electionLeader();
                Flux.interval(Duration.ZERO, Duration.ofSeconds(5)).doOnNext(i -> this.checkAlive()).subscribe();
            })
            .block();

        offlineTopic.subscribe().subscribe(serverNode -> {
            // 自己
            if (currentServer().isSame(serverNode)) {
                return;
            }
            if (allNode.remove(serverNode.getId()) != null) {
                log.debug("[{}]:server node [{}] offline", haName, serverNode.getId());
                // node offline
                inRedisNode.remove(allNodeHashKey, serverNode.getId()).subscribe();
                electionLeader();
                if (offlineProcessor.hasDownstreams()) {
                    offlineProcessor.onNext(serverNode);
                }
            }
        });
        // 其他节点定时发布
        keepalive.subscribe().subscribe(serverNode -> {
            // 自己
            if (currentServer().isSame(serverNode)) {
                return;
            }
            serverNode.setLastKeepAlive(System.currentTimeMillis());
            allNode.compute(serverNode.getId(), (id, node) -> {
                if (node != null) {
                    node.setLastKeepAlive(System.currentTimeMillis());
                    return node;
                }
                return null;
            });
            if (!allNode.containsKey(serverNode.getId())) {
                allNode.put(serverNode.getId(), serverNode);
                electionLeader();
                log.debug("[{}]:server node [{}] online", haName, serverNode.getId());
                // node join
                if (onlineProcessor.hasDownstreams()) {
                    onlineProcessor.onNext(serverNode);
                }
            }
        });
    }

    @Override
    public ServerNode currentServer() {
        return current;
    }

    @Override
    public Flux<ServerNode> subscribeServerOnline() {
        return onlineProcessor.filter(node -> !node.getId().equals(current.getId()));
    }

    @Override
    public Flux<ServerNode> subscribeServerOffline() {
        return offlineProcessor.filter(node -> !node.getId().equals(current.getId()));
    }

    @Override
    public List<ServerNode> getAllNode() {
        return new ArrayList<>(allNode.values());
    }

    @Override
    public Disposable doOnReBalance(Consumer<List<ServerNode>> runnable) {
        return () -> {
        };
    }
}
