package com.sipa.boot.java8.common.ribbon.predicate;

import javax.annotation.Nullable;

import com.netflix.loadbalancer.AbstractServerPredicate;
import com.netflix.loadbalancer.PredicateKey;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;

/**
 * A template method predicate to be applied to service discovered server instances. The predicate implementation of
 * this class need to implement the {@link #apply(DiscoveryEnabledServer)} method.
 *
 * @author Xiajie Zhou
 */
public abstract class DiscoveryEnabledPredicate extends AbstractServerPredicate {
    /**
     * {@inheritDoc}
     */
    @Override
    public boolean apply(@Nullable PredicateKey input) {
        if (input == null || !(input.getServer() instanceof DiscoveryEnabledServer)) {
            return true;
        }

        return apply((DiscoveryEnabledServer)input.getServer());
    }

    /**
     * Returns whether the specific {@link DiscoveryEnabledServer} matches this predicate.
     *
     * @param server
     *            the discovered server
     * @return whether the server matches the predicate
     */
    protected abstract boolean apply(DiscoveryEnabledServer server);
}
