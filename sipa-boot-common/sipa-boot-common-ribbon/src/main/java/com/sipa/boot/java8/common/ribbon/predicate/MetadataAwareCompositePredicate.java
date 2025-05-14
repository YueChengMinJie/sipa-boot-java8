package com.sipa.boot.java8.common.ribbon.predicate;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Lists;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.loadbalancer.AbstractServerPredicate;
import com.netflix.loadbalancer.PredicateKey;
import com.netflix.loadbalancer.Server;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;

/**
 * @author zhouxiajie
 * @date 2021/5/20
 */
public class MetadataAwareCompositePredicate extends AbstractServerPredicate {
    private static final Log LOGGER = LogFactory.get(MetadataAwareCompositePredicate.class);

    private AbstractServerPredicate delegate;

    private final List<AbstractServerPredicate> fallbacks = Lists.newArrayList();

    private int minimalFilteredServers = 1;

    private float minimalFilteredPercentage = 0;

    /**
     * can change to list to adapt contain all
     */
    private String filterKey = StringUtils.EMPTY;

    @Override
    public boolean apply(@Nullable PredicateKey input) {
        return delegate.apply(input);
    }

    public static class Builder {
        private final MetadataAwareCompositePredicate toBuild;

        Builder(AbstractServerPredicate primaryPredicate) {
            toBuild = new MetadataAwareCompositePredicate();
            toBuild.delegate = primaryPredicate;
        }

        Builder(AbstractServerPredicate... primaryPredicates) {
            toBuild = new MetadataAwareCompositePredicate();
            Predicate<PredicateKey> chain = Predicates.and(primaryPredicates);
            toBuild.delegate = AbstractServerPredicate.ofKeyPredicate(chain);
        }

        public MetadataAwareCompositePredicate.Builder addFallbackPredicate(AbstractServerPredicate fallback) {
            toBuild.fallbacks.add(fallback);
            return this;
        }

        public MetadataAwareCompositePredicate.Builder
            setFallbackThresholdAsMinimalFilteredNumberOfServers(int number) {
            toBuild.minimalFilteredServers = number;
            return this;
        }

        public MetadataAwareCompositePredicate.Builder setFallbackThresholdAsMinimalFilteredPercentage(float percent) {
            toBuild.minimalFilteredPercentage = percent;
            return this;
        }

        public MetadataAwareCompositePredicate.Builder setFilterKey(String filterKey) {
            toBuild.filterKey = filterKey;
            return this;
        }

        public MetadataAwareCompositePredicate build() {
            return toBuild;
        }
    }

    public static MetadataAwareCompositePredicate.Builder withPredicates(AbstractServerPredicate... primaryPredicates) {
        return new MetadataAwareCompositePredicate.Builder(primaryPredicates);
    }

    public static MetadataAwareCompositePredicate.Builder withPredicate(AbstractServerPredicate primaryPredicate) {
        return new MetadataAwareCompositePredicate.Builder(primaryPredicate);
    }

    /**
     * Get the filtered servers from primary predicate, and if the number of the filtered servers are not enough, trying
     * the fallback predicates
     */
    @Override
    public List<Server> getEligibleServers(List<Server> servers, Object loadBalancerKey) {
        List<Server> results = super.getEligibleServers(servers, loadBalancerKey);
        Iterator<AbstractServerPredicate> i = fallbacks.iterator();
        while (!(results.size() >= minimalFilteredServers
            && results.size() > (int)(servers.size() * minimalFilteredPercentage)) && i.hasNext()) {
            AbstractServerPredicate predicate = i.next();
            results = predicate.getEligibleServers(servers, loadBalancerKey);
        }

        if (LOGGER.isDebugEnabled()) {
            LOGGER.info("[{}] servers into filter.", results.size());
        }
        List<Server> canaryResults = results.stream().filter(server -> {
            if (Objects.nonNull(server) && server instanceof DiscoveryEnabledServer) {
                DiscoveryEnabledServer des = (DiscoveryEnabledServer)server;
                InstanceInfo instanceInfo = des.getInstanceInfo();
                if (Objects.nonNull(instanceInfo)) {
                    Map<String, String> metadata = instanceInfo.getMetadata();
                    return MapUtils.isNotEmpty(metadata) && metadata.containsKey(filterKey);
                }
            }
            return false;
        }).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(canaryResults)) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.info("[{}] servers out filter.", canaryResults.size());
            }
            results = canaryResults;
        }

        return results;
    }
}
