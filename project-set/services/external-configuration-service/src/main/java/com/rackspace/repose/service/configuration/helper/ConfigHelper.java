package com.rackspace.repose.service.configuration.helper;

import com.rackspace.papi.components.access.uri.config.UriAccessConfig;
import com.rackspace.papi.components.identity.token.header.config.HeaderTokenIdentityConfig;
import com.rackspace.papi.service.config.ConfigurationService;
import com.rackspace.repose.service.configuration.context.*;
import com.rackspace.repose.service.configuration.resource.Api;
import com.rackspace.repose.service.ratelimit.config.RateLimitingConfiguration;
import org.openrepose.components.routing.servlet.config.DestinationRouterConfiguration;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ConfigHelper {

    private static SystemModelConfigListener systemModelConfigListener;
    private static Map<String, DestinationRouterConfigListener> destinationRouterConfigListenerMap;
    private static HeaderTokenIdentityConfigListener headerTokenIdentityConfigListener;
    private static UriAccessConfigListener uriAccessConfigListener;
    private static RateLimitConfigListener rateLimitConfigListener;

    static {
        destinationRouterConfigListenerMap = new HashMap<String, DestinationRouterConfigListener>();
    }

    public static SystemModelConfigListener getSystemModelConfigListener() {
        return systemModelConfigListener;
    }

    public static void setSystemModelConfigListener(SystemModelConfigListener systemModelConfigListener) {
        ConfigHelper.systemModelConfigListener = systemModelConfigListener;
    }

    public static DestinationRouterConfigListener getDestinationRouterConfigListener(String name) {
        return destinationRouterConfigListenerMap.get(name);
    }

    public static void putDestinationRouterConfigListener(String name, DestinationRouterConfigListener config) {
        ConfigHelper.destinationRouterConfigListenerMap.put(name, config);
    }

    public static DestinationRouterConfigListener removeDestinationRouterConfig(String name) {
        return destinationRouterConfigListenerMap.remove(name);
    }

    public static HeaderTokenIdentityConfigListener getHeaderTokenIdentityConfigListener() {
        return headerTokenIdentityConfigListener;
    }

    public static void setHeaderTokenIdentityConfigListener(HeaderTokenIdentityConfigListener headerTokenIdentityConfigListener) {
        ConfigHelper.headerTokenIdentityConfigListener = headerTokenIdentityConfigListener;
    }

    public static UriAccessConfigListener getUriAccessConfigListener() {
        return uriAccessConfigListener;
    }

    public static void setUriAccessConfigListener(UriAccessConfigListener uriAccessConfigListener) {
        ConfigHelper.uriAccessConfigListener = uriAccessConfigListener;
    }

    public static RateLimitConfigListener getRateLimitConfigListener() {
        return rateLimitConfigListener;
    }

    public static void setRateLimitConfigListener(RateLimitConfigListener rateLimitConfigListener) {
        ConfigHelper.rateLimitConfigListener = rateLimitConfigListener;
    }

    public static Collection<DestinationRouterConfigListener> getDestinationRouterConfigListener() {
        return ConfigHelper.destinationRouterConfigListenerMap.values();
    }

}
