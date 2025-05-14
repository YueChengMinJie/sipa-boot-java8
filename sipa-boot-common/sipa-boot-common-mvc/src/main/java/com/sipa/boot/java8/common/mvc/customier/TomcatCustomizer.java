package com.sipa.boot.java8.common.mvc.customier;

import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;

import com.sipa.boot.java8.common.log.Log;
import com.sipa.boot.java8.common.log.LogFactory;

/**
 * @author caszhou
 * @date 2021/7/28
 */
public class TomcatCustomizer implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {
    private static final Log LOGGER = LogFactory.get(TomcatCustomizer.class);

    @Override
    @SuppressWarnings("rawtypes")
    public void customize(TomcatServletWebServerFactory factory) {
        factory.addConnectorCustomizers(connector -> {
            AbstractHttp11Protocol protocol = (AbstractHttp11Protocol)connector.getProtocolHandler();
            LOGGER.info("#######################################################################################");
            LOGGER.info("########################## TomcatCustomizer start #####################################");
            LOGGER.info("#######################################################################################");
            LOGGER.info("# custom maxKeepAliveRequests {}", protocol.getMaxKeepAliveRequests());
            LOGGER.info("# keepalive timeout: {} ms", protocol.getKeepAliveTimeout());
            LOGGER.info("# connection timeout: {} ms", protocol.getConnectionTimeout());
            LOGGER.info("# max connections: {}", protocol.getMaxConnections());
            LOGGER.info("#######################################################################################");
            LOGGER.info("########################## TomcatCustomizer end #######################################");
            LOGGER.info("#######################################################################################");
        });
    }
}
