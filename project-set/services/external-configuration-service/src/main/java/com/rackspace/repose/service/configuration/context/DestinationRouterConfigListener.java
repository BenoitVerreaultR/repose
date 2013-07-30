package com.rackspace.repose.service.configuration.context;

import com.rackspace.papi.service.config.ConfigurationService;
import com.rackspace.repose.service.configuration.helper.ApiHelper;
import com.rackspace.repose.service.configuration.resource.Api;
import org.openrepose.components.routing.servlet.config.DestinationRouterConfiguration;

public class DestinationRouterConfigListener extends ConfigurationUpdater<DestinationRouterConfiguration> {

    private static final String CONFIG_FORMAT = "%s.cfg.xml";
    private final Api api;

    public DestinationRouterConfigListener(Api api, ConfigurationService configurationManager) {
        this.configurationManager = configurationManager;
        this.api = api;
    }

    @Override
    public String getConfigName() {
        return String.format(CONFIG_FORMAT, api.getName());
    }

    @Override
    public void configUpdated() {
        ApiHelper.update(api, config.getTarget());
    }

}