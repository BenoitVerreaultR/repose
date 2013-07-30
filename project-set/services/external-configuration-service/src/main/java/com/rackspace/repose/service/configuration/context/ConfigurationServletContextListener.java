package com.rackspace.repose.service.configuration.context;

import com.rackspace.papi.service.config.ConfigurationService;
import com.rackspace.repose.service.configuration.helper.ConfigHelper;
import com.rackspace.repose.service.configuration.resource.Api;
import com.rackspace.repose.service.configuration.resource.Auth;
import com.rackspace.repose.service.configuration.resource.Policy;
import com.rackspace.repose.service.configuration.resource.ServiceCluster;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.HashMap;

public class ConfigurationServletContextListener implements ServletContextListener {

    public static final String API = Api.class.getSimpleName().toLowerCase();
    public static final String AUTH = Auth.class.getSimpleName().toLowerCase();
    public static final String POLICY = Policy.class.getSimpleName().toLowerCase();
    public static final String SERVICE_CLUSTER = ServiceCluster.class.getSimpleName().toLowerCase();
    public static final String API_BY_NAME = "api_by_name";
    public static final String CONFIGURATION_MANAGER = "configuration_manager";
    private SystemModelConfigListener systemModelConfigListener;
    private HeaderTokenIdentityConfigListener headerTokenIdentityConfigListener;
    private UriAccessConfigListener uriAccessConfigListener;
    private RateLimitConfigListener rateLimitConfigListener;
    private ConfigurationService configurationManager;
    private boolean initialized = false;

    public ConfigurationServletContextListener(ConfigurationService configurationManager) {
        this.configurationManager = configurationManager;
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        if (initialized)
            return;

        // add attributes to context
        sce.getServletContext().setAttribute(CONFIGURATION_MANAGER, configurationManager);
        sce.getServletContext().setAttribute(API, new HashMap<String, Api>());
        sce.getServletContext().setAttribute(SERVICE_CLUSTER, new HashMap<String, ServiceCluster>());
        sce.getServletContext().setAttribute(AUTH, new HashMap<String, Auth>());
        sce.getServletContext().setAttribute(POLICY, new HashMap<String, Policy>());
        sce.getServletContext().setAttribute(API_BY_NAME, new HashMap<String, Api>());

        // create listeners
        systemModelConfigListener = new SystemModelConfigListener();
        headerTokenIdentityConfigListener = new HeaderTokenIdentityConfigListener();
        uriAccessConfigListener = new UriAccessConfigListener();
        rateLimitConfigListener = new RateLimitConfigListener();

        ConfigHelper.setSystemModelConfigListener(systemModelConfigListener);
        ConfigHelper.setHeaderTokenIdentityConfigListener(headerTokenIdentityConfigListener);
        ConfigHelper.setUriAccessConfigListener(uriAccessConfigListener);
        ConfigHelper.setRateLimitConfigListener(rateLimitConfigListener);

        systemModelConfigListener.contextInitialized(sce);
        headerTokenIdentityConfigListener.contextInitialized(sce);
        uriAccessConfigListener.contextInitialized(sce);
        rateLimitConfigListener.contextInitialized(sce);

        initialized = true;
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (!initialized)
            return;
        systemModelConfigListener.contextDestroyed(sce);
        headerTokenIdentityConfigListener.contextDestroyed(sce);
        uriAccessConfigListener.contextDestroyed(sce);
        rateLimitConfigListener.contextDestroyed(sce);
        initialized = false;
    }
}
