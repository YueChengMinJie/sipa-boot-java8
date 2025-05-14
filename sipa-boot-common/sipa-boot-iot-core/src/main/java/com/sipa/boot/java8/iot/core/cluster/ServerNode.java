package com.sipa.boot.java8.iot.core.cluster;

import java.io.Serializable;
import java.util.Map;
import java.util.Optional;

import com.sipa.boot.java8.iot.core.bean.FastBeanCopier;

/**
 * @author caszhou
 * @date 2021/10/2
 */
public class ServerNode implements Serializable {
    private static final long serialVersionUID = -6849794470754667710L;

    private String id;

    private String name;

    private String host;

    private Map<String, Object> tags;

    private boolean leader;

    private long uptime;

    private long lastKeepAlive;

    public ServerNode() {}

    public ServerNode(String id, String name, String host, Map<String, Object> tags, boolean leader, long uptime,
        long lastKeepAlive) {
        this.id = id;
        this.name = name;
        this.host = host;
        this.tags = tags;
        this.leader = leader;
        this.uptime = uptime;
        this.lastKeepAlive = lastKeepAlive;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Map<String, Object> getTags() {
        return tags;
    }

    public void setTags(Map<String, Object> tags) {
        this.tags = tags;
    }

    public boolean isLeader() {
        return leader;
    }

    public void setLeader(boolean leader) {
        this.leader = leader;
    }

    public long getUptime() {
        return uptime;
    }

    public void setUptime(long uptime) {
        this.uptime = uptime;
    }

    public long getLastKeepAlive() {
        return lastKeepAlive;
    }

    public void setLastKeepAlive(long lastKeepAlive) {
        this.lastKeepAlive = lastKeepAlive;
    }

    public boolean hasTag(String tag) {
        return tags != null && tags.containsKey(tag);
    }

    public Optional<Object> getTag(String tag) {
        return Optional.ofNullable(tags).map(t -> t.get(tag));
    }

    public boolean isSame(ServerNode another) {
        return id.equals(another.getId());
    }

    public ServerNode copy() {
        return FastBeanCopier.copy(this, new ServerNode());
    }

    public static final class ServerNodeBuilder {
        private String id;

        private String name;

        private String host;

        private Map<String, Object> tags;

        private boolean leader;

        private long uptime;

        private long lastKeepAlive;

        private ServerNodeBuilder() {}

        public static ServerNodeBuilder aServerNode() {
            return new ServerNodeBuilder();
        }

        public ServerNodeBuilder withId(String id) {
            this.id = id;
            return this;
        }

        public ServerNodeBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public ServerNodeBuilder withHost(String host) {
            this.host = host;
            return this;
        }

        public ServerNodeBuilder withTags(Map<String, Object> tags) {
            this.tags = tags;
            return this;
        }

        public ServerNodeBuilder withLeader(boolean leader) {
            this.leader = leader;
            return this;
        }

        public ServerNodeBuilder withUptime(long uptime) {
            this.uptime = uptime;
            return this;
        }

        public ServerNodeBuilder withLastKeepAlive(long lastKeepAlive) {
            this.lastKeepAlive = lastKeepAlive;
            return this;
        }

        public ServerNode build() {
            ServerNode serverNode = new ServerNode();
            serverNode.setId(id);
            serverNode.setName(name);
            serverNode.setHost(host);
            serverNode.setTags(tags);
            serverNode.setLeader(leader);
            serverNode.setUptime(uptime);
            serverNode.setLastKeepAlive(lastKeepAlive);
            return serverNode;
        }
    }
}
