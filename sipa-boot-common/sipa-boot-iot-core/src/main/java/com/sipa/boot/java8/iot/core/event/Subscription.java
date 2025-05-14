package com.sipa.boot.java8.iot.core.event;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.util.Assert;

import com.sipa.boot.java8.iot.core.dict.base.Dict;
import com.sipa.boot.java8.iot.core.dict.base.IEnumDict;
import com.sipa.boot.java8.iot.core.util.TopicUtils;

/**
 * @author caszhou
 * @date 2021/9/26
 */
public class Subscription implements Serializable {
    private static final long serialVersionUID = -6849794470754667710L;

    private final String subscriber;

    private final String[] topics;

    private final EFeature[] features;

    private Runnable doOnSubscribe;

    public Subscription(String subscriber, String[] topics, EFeature[] features, Runnable doOnSubscribe) {
        this.subscriber = subscriber;
        this.topics = topics;
        this.features = features;
        this.doOnSubscribe = doOnSubscribe;
    }

    public static Subscription of(String subscriber, String... topic) {
        return Subscription.builder().subscriberId(subscriber).topics(topic).build();
    }

    public static Subscription of(String subscriber, String[] topic, EFeature... features) {
        return Subscription.builder().subscriberId(subscriber).topics(topic).features(features).build();
    }

    public static Subscription of(String subscriber, String topic, EFeature... features) {
        return Subscription.builder().subscriberId(subscriber).topics(topic).features(features).build();
    }

    public Subscription copy(EFeature... newFeatures) {
        return new Subscription(subscriber, topics, newFeatures, null);
    }

    public Subscription onSubscribe(Runnable sub) {
        this.doOnSubscribe = sub;
        return this;
    }

    public boolean hasFeature(EFeature feature) {
        return feature.in(this.features);
    }

    public String getSubscriber() {
        return subscriber;
    }

    public String[] getTopics() {
        return topics;
    }

    public EFeature[] getFeatures() {
        return features;
    }

    public Runnable getDoOnSubscribe() {
        return doOnSubscribe;
    }

    @Dict("subscription-feature")
    public enum EFeature implements IEnumDict<String> {
        // 如果相同的订阅者, 只有一个订阅者收到消息
        shared("shared"),
        // 订阅本地消息
        local("订阅本地消息"),
        // 订阅来自代理的消息
        broker("订阅代理消息");

        private final String text;

        EFeature(String text) {
            this.text = text;
        }

        @Override
        public String getValue() {
            return name();
        }

        @Override
        public String getText() {
            return text;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String subscriber;

        private final Set<String> topics = new HashSet<>();

        private final Set<EFeature> features = new HashSet<>();

        private Runnable doOnSubscribe;

        public Builder randomSubscriberId() {
            return subscriberId(UUID.randomUUID().toString());
        }

        public Builder subscriberId(String id) {
            this.subscriber = id;
            return this;
        }

        public Builder topics(String... topics) {
            return topics(Arrays.asList(topics));
        }

        public Builder topics(Collection<String> topics) {
            this.topics.addAll(
                topics.stream().flatMap(topic -> TopicUtils.expand(topic).stream()).collect(Collectors.toSet()));
            return this;
        }

        public Builder features(EFeature... features) {
            this.features.addAll(Arrays.asList(features));
            return this;
        }

        public Builder doOnSubscribe(Runnable runnable) {
            this.doOnSubscribe = runnable;
            return this;
        }

        public Builder justLocal() {
            this.features.clear();
            return features(EFeature.local);
        }

        public Builder justBroker() {
            this.features.clear();
            return features(EFeature.broker);
        }

        public Builder local() {
            return features(EFeature.local);
        }

        public Builder broker() {
            return features(EFeature.broker);
        }

        public Builder shared() {
            return features(EFeature.shared);
        }

        public Subscription build() {
            if (features.isEmpty()) {
                local();
            }
            Assert.notEmpty(topics, "topic cannot be empty");
            Assert.hasText(subscriber, "subscriber cannot be empty");
            return new Subscription(subscriber, topics.toArray(new String[0]), features.toArray(new EFeature[0]),
                doOnSubscribe);
        }
    }
}
