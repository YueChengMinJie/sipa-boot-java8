package com.sipa.boot.java8.common.ribbon.rule;

import org.springframework.util.Assert;

import com.netflix.loadbalancer.AbstractServerPredicate;
import com.netflix.loadbalancer.AvailabilityPredicate;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.PredicateBasedRule;
import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;
import com.sipa.boot.java8.common.ribbon.predicate.DiscoveryEnabledPredicate;
import com.sipa.boot.java8.common.ribbon.predicate.MetadataAwareCompositePredicate;

/**
 * A simple {@link IRule} for matching the discovered server instances. The actual matching is being performed by the
 * registered instance of {@link DiscoveryEnabledPredicate} allowing to adjust the actual matching strategy.
 *
 * @author Xiajie Zhou
 * @see DiscoveryEnabledPredicate
 */
public abstract class DiscoveryEnabledRule extends PredicateBasedRule {
    private final MetadataAwareCompositePredicate predicate;

    /**
     * Creates new instance of {@link DiscoveryEnabledRule} class with specific predicate.
     *
     * @param discoveryEnabledPredicate
     *            the discovery enabled predicate, can't be null
     * @throws IllegalArgumentException
     *             if {@code discoveryEnabledPredicate} is {@code null}
     */
    public DiscoveryEnabledRule(DiscoveryEnabledPredicate discoveryEnabledPredicate) {
        Assert.notNull(discoveryEnabledPredicate, "Parameter 'discoveryEnabledPredicate' can't be null");
        this.predicate = createCompositePredicate(discoveryEnabledPredicate, new AvailabilityPredicate(this, null));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AbstractServerPredicate getPredicate() {
        return predicate;
    }

    /**
     * Creates the composite predicate with fallback strategies.
     *
     * @param discoveryEnabledPredicate
     *            the discovery service predicate
     * @param availabilityPredicate
     *            the availability predicate
     * @return the composite predicate
     */
    private MetadataAwareCompositePredicate createCompositePredicate(
        DiscoveryEnabledPredicate discoveryEnabledPredicate, AvailabilityPredicate availabilityPredicate) {
        return MetadataAwareCompositePredicate.withPredicates(discoveryEnabledPredicate, availabilityPredicate)
            .setFilterKey(SipaBootCommonConstants.Canary.METADATA)
            .build();
    }
}
