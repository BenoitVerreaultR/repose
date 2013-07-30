package com.rackspace.repose.service.configuration.context;

import com.rackspace.papi.components.identity.token.header.config.HeaderMapping;
import com.rackspace.papi.components.identity.token.header.config.HeaderTokenIdentityConfig;
import com.rackspace.repose.service.configuration.helper.AuthHelper;
import com.rackspace.repose.service.configuration.helper.ConfigHelper;
import com.rackspace.repose.service.configuration.resource.Auth;

import java.util.List;

public class HeaderTokenIdentityConfigListener extends ConfigurationUpdater<HeaderTokenIdentityConfig> {
    private static final String CONFIG_NAME = "header-token-identity.cfg.xml";

    @Override
    public String getConfigName() {
        return CONFIG_NAME;
    }

    @Override
    public void configUpdated() {
        updateAuthFromHeaderMappings(config.getHeaders().getHeader());
    }

    private void updateAuthFromHeaderMappings(List<HeaderMapping> headerMappings) {
        authById.clear();
        for (HeaderMapping headerMapping : headerMappings) {
            Auth auth = AuthHelper.fromHeaderMapping(headerMapping);
            authById.put(auth.getId(), auth);
        }
    }

}