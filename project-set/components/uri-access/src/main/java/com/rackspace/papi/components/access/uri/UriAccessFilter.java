package com.rackspace.papi.components.access.uri;


import com.rackspace.papi.components.access.uri.config.UriAccessConfig;
import com.rackspace.papi.filter.FilterConfigHelper;
import com.rackspace.papi.filter.logic.impl.FilterLogicHandlerDelegate;
import com.rackspace.papi.service.config.ConfigurationService;
import com.rackspace.papi.service.context.ServletContextHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import java.io.IOException;
import java.net.URL;

public class UriAccessFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(UriAccessFilter.class);
    private static final String DEFAULT_CONFIG = "uri-access.cfg.xml";
    private String config;
    private UriAccessHandlerFactory handlerFactory;
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
        handlerFactory = new UriAccessHandlerFactory();
        URL xsdURL = getClass().getResource("/META-INF/schema/config/uri-access-configuration.xsd");
        configurationManager.subscribeTo(filterConfig.getFilterName(), config, xsdURL, handlerFactory, UriAccessConfig.class);
    }
}
