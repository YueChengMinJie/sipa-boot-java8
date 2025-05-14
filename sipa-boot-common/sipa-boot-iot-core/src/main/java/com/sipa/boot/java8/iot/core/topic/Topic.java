package com.sipa.boot.java8.iot.core.topic;

import static com.sipa.boot.java8.common.constants.SipaBootCommonConstants.*;

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;

import javax.annotation.Nonnull;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

import com.sipa.boot.java8.common.archs.cache.Caches;
import com.sipa.boot.java8.iot.core.util.TopicUtils;

import reactor.core.publisher.Flux;

/**
 * @author caszhou
 * @date 2021/10/3
 */
public final class Topic<T> {
    private static final AntPathMatcher MATCHER = new AntPathMatcher() {
        @Nonnull
        @Override
        protected String[] tokenizePath(@Nonnull String path) {
            return TopicUtils.split(path);
        }
    };

    private final Topic<T> parent;

    private String part;

    private volatile String topic;

    private volatile String[] topics;

    private final int depth;

    private final ConcurrentMap<String, Topic<T>> child = Caches.newCache();

    private final ConcurrentMap<T, AtomicInteger> subscribers = Caches.newCache();

    static {
        MATCHER.setCachePatterns(true);
        MATCHER.setCaseSensitive(true);
    }

    public static <T> Topic<T> createRoot() {
        return new Topic<>(null, SLASH);
    }

    public Topic<T> append(String topic) {
        if (topic.equals(SLASH) || topic.equals(BLANK)) {
            return this;
        }
        return getOrDefault(topic, Topic::new);
    }

    private Topic(Topic<T> parent, String part) {
        if (StringUtils.isEmpty(part) || part.equals(SLASH)) {
            this.part = BLANK;
        } else if (part.contains(SLASH)) {
            this.ofTopic(part);
        } else {
            this.part = part;
        }

        this.parent = parent;

        if (null != parent) {
            this.depth = parent.depth + 1;
        } else {
            this.depth = 0;
        }
    }

    public String[] getTopics() {
        if (topics != null) {
            return topics;
        }
        return topics = TopicUtils.split(getTopic());
    }

    public String getTopic() {
        if (topic == null) {
            Topic<T> parent = getParent();
            StringBuilder builder = new StringBuilder();
            if (parent != null) {
                String parentTopic = parent.getTopic();
                builder.append(parentTopic).append(parentTopic.equals(SLASH) ? BLANK : SLASH);
            } else {
                builder.append(SLASH);
            }
            return topic = builder.append(part).toString();
        }
        return topic;
    }

    public T getSubscriberOrSubscribe(Supplier<T> supplier) {
        if (subscribers.size() > 0) {
            return subscribers.keySet().iterator().next();
        }
        synchronized (this) {
            if (subscribers.size() > 0) {
                return subscribers.keySet().iterator().next();
            }
            T sub = supplier.get();
            subscribe(sub);
            return sub;
        }
    }

    public Set<T> getSubscribers() {
        return subscribers.keySet();
    }

    public boolean subscribed(T subscriber) {
        return subscribers.containsKey(subscriber);
    }

    @SafeVarargs
    public final void subscribe(T... subscribers) {
        for (T subscriber : subscribers) {
            this.subscribers.computeIfAbsent(subscriber, i -> new AtomicInteger()).incrementAndGet();
        }
    }

    @SafeVarargs
    public final List<T> unsubscribe(T... subscribers) {
        List<T> unsub = new ArrayList<>();
        for (T subscriber : subscribers) {
            this.subscribers.computeIfPresent(subscriber, (k, v) -> {
                if (v.decrementAndGet() <= 0) {
                    unsub.add(k);
                    return null;
                }
                return v;
            });
        }
        return unsub;
    }

    public void unsubscribe(Predicate<T> predicate) {
        for (Map.Entry<T, AtomicInteger> entry : this.subscribers.entrySet()) {
            if (predicate.test(entry.getKey()) && entry.getValue().decrementAndGet() <= 0) {
                this.subscribers.remove(entry.getKey());
            }
        }
    }

    public void unsubscribeAll() {
        this.subscribers.clear();
    }

    public Collection<Topic<T>> getChildren() {
        return child.values();
    }

    private void ofTopic(String topic) {
        String[] parts = topic.split(SLASH, 2);
        this.part = parts[0];
        if (parts.length > 1) {
            Topic<T> part = new Topic<>(this, parts[1]);
            this.child.put(part.part, part);
        }
    }

    private Topic<T> getOrDefault(String topic, BiFunction<Topic<T>, String, Topic<T>> mapping) {
        if (topic.startsWith(SLASH)) {
            topic = topic.substring(1);
        }
        String[] parts = topic.split(SLASH);
        Topic<T> part = child.computeIfAbsent(parts[0], t -> mapping.apply(this, t));
        for (int i = 1; i < parts.length && part != null; i++) {
            Topic<T> parent = part;
            part = part.child.computeIfAbsent(parts[i], t -> mapping.apply(parent, t));
        }
        return part;
    }

    public Optional<Topic<T>> getTopic(String topic) {
        return Optional.ofNullable(getOrDefault(topic, ((topicPart, s) -> null)));
    }

    public Flux<Topic<T>> findTopic(String topic) {
        if (!topic.startsWith(SLASH)) {
            topic = SLASH + topic;
        }
        return find(topic, this);
    }

    @Override
    public String toString() {
        return "topic: " + getTopic() + ", subscribers: " + subscribers.size() + ", children: " + child.size();
    }

    private boolean match(String[] pars) {
        return TopicUtils.match(getTopics(), pars) || TopicUtils.match(pars, getTopics());
    }

    public static <T> Flux<Topic<T>> find(String topic, Topic<T> topicPart) {
        return Flux.create(sink -> {
            ArrayDeque<Topic<T>> cache = new ArrayDeque<>(128);
            cache.add(topicPart);

            String[] topicParts = TopicUtils.split(topic);
            String nextPart = null;
            while (!cache.isEmpty() && !sink.isCancelled()) {
                Topic<T> part = cache.poll();
                if (part == null) {
                    break;
                }
                if (part.match(topicParts)) {
                    sink.next(part);
                }
                // 订阅了如 /device/**/event/*
                if (part.part.equals(DOUBLE_ASTERISK)) {
                    Topic<T> tmp = null;
                    for (int i = part.depth; i < topicParts.length; i++) {
                        tmp = part.child.get(topicParts[i]);
                        if (tmp != null) {
                            cache.add(tmp);
                        }
                    }
                    if (null != tmp) {
                        continue;
                    }
                }
                if (DOUBLE_ASTERISK.equals(nextPart) || ASTERISK.equals(nextPart)) {
                    cache.addAll(part.child.values());
                    continue;
                }
                Topic<T> next = part.child.get(DOUBLE_ASTERISK);
                if (next != null) {
                    cache.add(next);
                }
                next = part.child.get(ASTERISK);
                if (next != null) {
                    cache.add(next);
                }
                if (part.depth + 1 >= topicParts.length) {
                    continue;
                }
                nextPart = topicParts[part.depth + 1];
                if (nextPart.equals(ASTERISK) || nextPart.equals(DOUBLE_ASTERISK)) {
                    cache.addAll(part.child.values());
                    continue;
                }
                next = part.child.get(nextPart);
                if (next != null) {
                    cache.add(next);
                }
            }
            sink.complete();
        });
    }

    public long getTotalTopic() {
        long total = child.size();
        for (Topic<T> tTopic : getChildren()) {
            total += tTopic.getTotalTopic();
        }
        return total;
    }

    public long getTotalSubscriber() {
        long total = subscribers.size();
        for (Topic<T> tTopic : getChildren()) {
            total += tTopic.getTotalTopic();
        }
        return total;
    }

    public Flux<Topic<T>> getAllSubscriber() {
        List<Flux<Topic<T>>> all = new ArrayList<>();

        all.add(Flux.fromIterable(this.getChildren()));

        for (Topic<T> tTopic : getChildren()) {
            all.add(tTopic.getAllSubscriber());
        }
        return Flux.concat(all);
    }

    public void clean() {
        unsubscribeAll();
        getChildren().forEach(Topic::clean);
        child.clear();
    }

    public Topic<T> getParent() {
        return parent;
    }

    private void setPart(String part) {
        this.part = part;
    }

    private void setTopic(String topic) {
        this.topic = topic;
    }

    private void setTopics(String[] topics) {
        this.topics = topics;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Topic<?> topic = (Topic<?>)o;

        return new EqualsBuilder().append(part, topic.part).isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37).append(part).toHashCode();
    }
}
