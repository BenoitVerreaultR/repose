package com.rackspace.papi.components.identity.header;


import com.rackspace.papi.components.identity.token.header.config.HeaderTokenIdentityConfig;
import com.rackspace.papi.filter.FilterConfigHelper;
import com.rackspace.papi.filter.logic.impl.FilterLogicHandlerDelegate;
import com.rackspace.papi.service.config.ConfigurationService;
import com.rackspace.papi.service.context.ServletContextHelper;

import javax.servlet.*;
import java.io.IOException;
import java.net.URL;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeaderTokenIdentityFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(HeaderTokenIdentityFilter.class);
    private static final String DEFAULT_CONFIG = "header-token-identity.cfg.xml";
    private String config;
    private HeaderTokenIdentityHandlerFactory handlerFactory;
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
        handlerFactory = new HeaderTokenIdentityHandlerFactory();
        URL xsdURL = getClass().getResource("/META-INF/schema/config/header-token-identity-configuration.xsd");
        configurationManager.subscribeTo(filterConfig.getFilterName(),config,xsdURL, handlerFactory, HeaderTokenIdentityConfig.class);
    }
}
