package com.rackspace.papi.components.configuration.external;

import com.rackspace.papi.components.configuration.external.config.ExternalConfigurationConfig;
import com.rackspace.papi.filter.FilterConfigHelper;
import com.rackspace.papi.filter.logic.impl.FilterLogicHandlerDelegate;
import com.rackspace.papi.service.config.ConfigurationService;
import com.rackspace.papi.service.context.ServletContextHelper;
import com.rackspace.repose.service.configuration.ExternalConfigurationService;
import com.rackspace.repose.service.configuration.ExternalConfigurationServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import java.io.IOException;
import java.net.URL;

public class ExternalConfigurationFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(ExternalConfigurationFilter.class);
    private static final String DEFAULT_CONFIG = "external-config.cfg.xml";
    private String config;
    private ExternalConfigurationHandlerFactory handlerFactory;
    private ConfigurationService configurationManager;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        new FilterLogicHandlerDelegate(request, response, chain).doFilter(handlerFactory.newHandler());
    }

    @Override
    public void destroy() {
        configurationManager.unsubscribeFrom(config, handlerFactory);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        config = new FilterConfigHelper(filterConfig).getFilterConfig(DEFAULT_CONFIG);
        LOG.info("Initializing filter using config " + config);
        configurationManager = ServletContextHelper.getInstance(filterConfig.getServletContext()).getPowerApiContext().configurationService();
        handlerFactory = new ExternalConfigurationHandlerFactory(configurationManager);
        URL xsdURL = getClass().getResource("/META-INF/schema/config/external-config-configuration.xsd");
        configurationManager.subscribeTo(filterConfig.getFilterName(), config, xsdURL, handlerFactory, ExternalConfigurationConfig.class);
    }

}
