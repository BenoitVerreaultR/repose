package com.rackspace.papi.components.identity.header;

import com.rackspace.papi.commons.config.manager.UpdateListener;
import com.rackspace.papi.commons.util.StringUtilities;
import com.rackspace.papi.components.identity.token.header.config.HeaderTokenIdentityConfig;
import com.rackspace.papi.components.identity.token.header.config.HeaderMapping;
import com.rackspace.papi.filter.logic.AbstractConfiguredFilterHandlerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class HeaderTokenIdentityHandlerFactory extends AbstractConfiguredFilterHandlerFactory<HeaderTokenIdentityHandler> {

    private static final Logger LOG = LoggerFactory.getLogger(HeaderTokenIdentityHandlerFactory.class);

    private Map<String, String> tokenToGroup = new HashMap<String, String>();
    private HeaderTokenIdentityConfig config;
    private String tokenHeader;

    public HeaderTokenIdentityHandlerFactory() {
    }

    @Override
    protected Map<Class, UpdateListener<?>> getListeners() {
        return new HashMap<Class, UpdateListener<?>>() {
            {
                put(HeaderTokenIdentityConfig.class, new HeaderTokenIdentityConfigurationListener());
            }
        };
    }

    private class HeaderTokenIdentityConfigurationListener implements UpdateListener<HeaderTokenIdentityConfig> {

        private boolean isInitialized = false;

        @Override
        public void configurationUpdated(HeaderTokenIdentityConfig configurationObject) {

            config = configurationObject;
            tokenToGroup.clear();

            for (HeaderMapping headerMapping : config.getHeaders().getHeader()) {
                if (tokenToGroup.containsKey(headerMapping.getToken())) {
                    LOG.warn("Multiple identical tokens, first group will be overriden " + headerMapping.getToken());
                }
                tokenToGroup.put(headerMapping.getToken(), headerMapping.getGroup());
            }

            tokenHeader = config.getTokenHeader();

            isInitialized = true;
        }

        @Override
        public boolean isInitialized() {
            return isInitialized;
        }
    }

    @Override
    protected HeaderTokenIdentityHandler buildHandler() {
        if (!this.isInitialized()) {
            return null;
        }
        return new HeaderTokenIdentityHandler(tokenToGroup, tokenHeader);
    }

}
