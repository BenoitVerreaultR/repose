package com.rackspace.repose.service.configuration;

import com.rackspace.papi.service.config.ConfigurationService;

public class ExternalConfigurationServiceFactory {

    private static ExternalConfigurationServiceImpl instance;

    public static ExternalConfigurationService getExternalConfigurationService(ConfigurationService configurationManager) {
        if (instance == null) {
            instance = new ExternalConfigurationServiceImpl(configurationManager);
        }
        return instance;
    }

}
