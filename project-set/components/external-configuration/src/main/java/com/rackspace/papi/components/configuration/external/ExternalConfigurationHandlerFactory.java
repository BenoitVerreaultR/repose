package com.rackspace.papi.components.configuration.external;

import com.rackspace.papi.commons.config.manager.UpdateListener;
import com.rackspace.papi.components.configuration.external.config.ExternalConfigurationConfig;
import com.rackspace.papi.filter.logic.AbstractConfiguredFilterHandlerFactory;
import com.rackspace.papi.service.config.ConfigurationService;
import com.rackspace.repose.service.configuration.ExternalConfigurationService;
import com.rackspace.repose.service.configuration.ExternalConfigurationServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ExternalConfigurationHandlerFactory extends AbstractConfiguredFilterHandlerFactory<ExternalConfigurationHandler> {

    private static final Logger LOG = LoggerFactory.getLogger(ExternalConfigurationHandlerFactory.class);

    private ExternalConfigurationConfig config;
    private ExternalConfigurationService service;

    public ExternalConfigurationHandlerFactory(ConfigurationService configurationManager) {
        service = ExternalConfigurationServiceFactory.getExternalConfigurationService(configurationManager);
    }

    @Override
    protected ExternalConfigurationHandler buildHandler() {
        if (!this.isInitialized()) {
            return null;
        }
        return new ExternalConfigurationHandler();
    }

    @Override
    protected Map<Class, UpdateListener<?>> getListeners() {
        return new HashMap<Class, UpdateListener<?>>() {
            {
                put(ExternalConfigurationConfig.class, new ExternalConfigurationConfigurationListener());
            }
        };
    }

    private class ExternalConfigurationConfigurationListener implements UpdateListener<ExternalConfigurationConfig> {

        private boolean isInitialized = false;

        @Override
        public void configurationUpdated(ExternalConfigurationConfig configurationObject) {
            config = configurationObject;
            service.startServer(config.getPort());
            isInitialized = true;
        }

        @Override
        public boolean isInitialized() {
            return isInitialized;
        }

    }

}
