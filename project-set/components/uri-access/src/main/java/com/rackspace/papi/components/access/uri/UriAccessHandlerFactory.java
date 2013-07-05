package com.rackspace.papi.components.access.uri;

import com.rackspace.papi.commons.config.manager.UpdateListener;
import com.rackspace.papi.components.access.uri.config.HttpMethod;
import com.rackspace.papi.components.access.uri.config.UriAccessConfig;
import com.rackspace.papi.components.access.uri.config.UriAccessConfigHelper;
import com.rackspace.papi.filter.logic.AbstractConfiguredFilterHandlerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UriAccessHandlerFactory extends AbstractConfiguredFilterHandlerFactory<UriAccessHandler> {

    private static final Logger LOG = LoggerFactory.getLogger(UriAccessHandlerFactory.class);
    private UriAccessConfig config;
    private UriAccessConfigHelper helper;

    public UriAccessHandlerFactory() {
    }

    @Override
    protected Map<Class, UpdateListener<?>> getListeners() {
        return new HashMap<Class, UpdateListener<?>>() {
            {
                put(UriAccessConfig.class, new UriAccessConfigurationListener());
            }
        };
    }

    @Override
    protected UriAccessHandler buildHandler() {
        if (!this.isInitialized()) {
            return null;
        }
        return new UriAccessHandler(helper);
    }

    private class UriAccessConfigurationListener implements UpdateListener<UriAccessConfig> {

        private boolean isInitialized = false;

        @Override
        public void configurationUpdated(UriAccessConfig configurationObject) {
            config = configurationObject;
            helper = new UriAccessConfigHelper(config);
            isInitialized = true;
        }

        @Override
        public boolean isInitialized() {
            return isInitialized;
        }
    }

}
