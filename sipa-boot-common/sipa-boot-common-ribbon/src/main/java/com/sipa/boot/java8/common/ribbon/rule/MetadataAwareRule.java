package com.sipa.boot.java8.common.ribbon.rule;

import com.sipa.boot.java8.common.ribbon.predicate.DiscoveryEnabledPredicate;
import com.sipa.boot.java8.common.ribbon.predicate.MetadataAwarePredicate;

/**
 * A metadata aware {@link DiscoveryEnabledRule} implementation.
 *
 * @author Xiajie Zhou
 * @see DiscoveryEnabledRule
 * @see MetadataAwarePredicate
 */
public class MetadataAwareRule extends DiscoveryEnabledRule {
    /**
     * Creates new instance of {@link MetadataAwareRule}.
     */
    public MetadataAwareRule() {
        this(new MetadataAwarePredicate());
    }

    /**
     * Creates new instance of {@link MetadataAwareRule}.
     */
    public MetadataAwareRule(String eurekaInstanceMetadataCanary) {
        this(new MetadataAwarePredicate(eurekaInstanceMetadataCanary));
    }

    /**
     * Creates new instance of {@link MetadataAwareRule} with specific predicate.
     *
     * @param predicate
     *            the predicate, can't be {@code null}
     * @throws IllegalArgumentException
     *             if predicate is {@code null}
     */
    public MetadataAwareRule(DiscoveryEnabledPredicate predicate) {
        super(predicate);
    }
}
