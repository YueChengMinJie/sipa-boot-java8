package com.sipa.boot.java8.common.ribbon.predicate;

import java.util.Map;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Maps;
import com.netflix.niws.loadbalancer.DiscoveryEnabledServer;
import com.sipa.boot.java8.common.archs.canary.context.RibbonFilterContextHolder;
import com.sipa.boot.java8.common.constants.SipaBootCommonConstants;
import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;
import com.sipa.boot.java8.common.utils.JsonUtils;

/**
 * A default implementation of {@link DiscoveryEnabledServer} that matches the instance against the attributes
 * registered through
 *
 * @author Xiajie Zhou
 * @see DiscoveryEnabledPredicate
 */
public class MetadataAwarePredicate extends DiscoveryEnabledPredicate {
    private static final Log LOGGER = LogFactory.get(MetadataAwarePredicate.class);

    private String eurekaInstanceMetadataCanary;

    public MetadataAwarePredicate() {
    }

    public MetadataAwarePredicate(String eurekaInstanceMetadataCanary) {
        this.eurekaInstanceMetadataCanary = eurekaInstanceMetadataCanary;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean apply(DiscoveryEnabledServer server) {
        final String canary =
            RibbonFilterContextHolder.getCurrentContext().get(SipaBootCommonConstants.Canary.METADATA);

        final Map<String, String> metadata = server.getInstanceInfo().getMetadata();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.info("Metadata [{}]", JsonUtils.writeValueAsString(metadata));
        }

        final Map<String, String> attributes = Maps.newHashMap();
        if (StringUtils.isNotBlank(canary)) {
            attributes.put(SipaBootCommonConstants.Canary.METADATA, canary);
        } else if (StringUtils.isNotBlank(eurekaInstanceMetadataCanary)) {
            attributes.put(SipaBootCommonConstants.Canary.METADATA, eurekaInstanceMetadataCanary);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.info("Attributes [{}]", JsonUtils.writeValueAsString(attributes));
        }

        boolean rs = true;
        if (MapUtils.isNotEmpty(metadata) && MapUtils.isNotEmpty(attributes)) {
            rs = isPrimaryServer(metadata) || isPerfectCanary(attributes) || canaryIsNotAlways(metadata);
        } else if (MapUtils.isNotEmpty(metadata) && MapUtils.isEmpty(attributes)) {
            rs = isPrimaryServer(metadata) || canaryIsNotAlways(metadata);
        }
        if (LOGGER.isDebugEnabled()) {
            LOGGER.info("Result [{}]", String.valueOf(rs));
        }

        return rs;
    }

    private boolean canaryIsNotAlways(Map<String, String> metadata) {
        return !SipaBootCommonConstants.Canary.ALWAYS.equals(metadata.get(SipaBootCommonConstants.Canary.METADATA));
    }

    private boolean isPerfectCanary(Map<String, String> attributes) {
        return StringUtils.isNotBlank(attributes.get(SipaBootCommonConstants.Canary.METADATA));
    }

    private boolean isPrimaryServer(Map<String, String> metadata) {
        return !metadata.containsKey(SipaBootCommonConstants.Canary.METADATA);
    }
}
