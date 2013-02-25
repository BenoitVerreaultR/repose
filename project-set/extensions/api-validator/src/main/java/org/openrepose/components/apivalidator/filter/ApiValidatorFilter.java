package org.openrepose.components.apivalidator.filter;

import com.rackspace.papi.filter.FilterConfigHelper;
import com.rackspace.papi.filter.logic.impl.FilterLogicHandlerDelegate;
import com.rackspace.papi.jmx.ConfigurationInformation;
import com.rackspace.papi.service.config.ConfigurationService;
import com.rackspace.papi.service.context.ServletContextHelper;
import com.rackspace.papi.servlet.InitParameter;
import java.io.IOException;
import java.net.URL;
import java.util.Date;
import javax.servlet.*;
import org.openrepose.components.apivalidator.servlet.config.ValidatorConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiValidatorFilter implements Filter {

    private static final Logger LOG = LoggerFactory.getLogger(ApiValidatorFilter.class);
    private static final String DEFAULT_CONFIG = "validator.cfg.xml";
    private String config;
    private ApiValidatorHandlerFactory handlerFactory;
    private ConfigurationService  configurationManager;
    private ConfigurationInformation configurationInformation;


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        ApiValidatorHandler handler = handlerFactory.newHandler();
        if (handler == null) {
            throw new ServletException("Unable to build validator handler");
        }
        handler.setFilterChain(chain);
        new FilterLogicHandlerDelegate(request, response, chain).doFilter(handler);
    }

    @Override
    public void destroy() {
        configurationManager.unsubscribeFrom(config, handlerFactory);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        
        final String configProp = InitParameter.POWER_API_CONFIG_DIR.getParameterName();
        final ServletContext ctx = filterConfig.getServletContext();
        final String configurationRoot = System.getProperty(configProp, ctx.getInitParameter(configProp));
        configurationManager = ServletContextHelper.getInstance(filterConfig.getServletContext()).getPowerApiContext().configurationService();
        config = new FilterConfigHelper(filterConfig).getFilterConfig(DEFAULT_CONFIG);
        LOG.info("Initializing filter using config " + config);
        handlerFactory = new ApiValidatorHandlerFactory( configurationManager, configurationRoot, config);
        URL xsdURL = getClass().getResource("/META-INF/schema/config/validator-configuration.xsd");
        configurationManager.subscribeTo(filterConfig.getFilterName(),config,xsdURL, handlerFactory, ValidatorConfiguration.class);
        
       }
}
