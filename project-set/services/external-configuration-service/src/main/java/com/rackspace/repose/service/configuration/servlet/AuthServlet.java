package com.rackspace.repose.service.configuration.servlet;

import com.rackspace.papi.components.identity.token.header.config.HeaderMapping;
import com.rackspace.papi.components.identity.token.header.config.HeaderTokenIdentityConfig;
import com.rackspace.papi.service.config.ConfigurationService;
import com.rackspace.repose.service.configuration.context.HeaderTokenIdentityConfigListener;
import com.rackspace.repose.service.configuration.helper.AuthHelper;
import com.rackspace.repose.service.configuration.helper.ConfigHelper;
import com.rackspace.repose.service.configuration.helper.HeaderHelper;
import com.rackspace.repose.service.configuration.resource.Auth;

import java.util.Iterator;

public class AuthServlet extends BaseServlet<Auth> {

    @Override
    protected void updateResource(Auth oldResource, Auth newResource) {
        HeaderTokenIdentityConfigListener configListener = ConfigHelper.getHeaderTokenIdentityConfigListener();
        HeaderTokenIdentityConfig config = configListener.getConfig();

        for (HeaderMapping headerMapping : config.getHeaders().getHeader()) {
            if (headerMapping.getId().equals(oldResource.getId())) {
                HeaderHelper.update(headerMapping, newResource);
                break;
            }
        }

        configListener.save();
    }

    @Override
    protected void createResource(Auth resource) {
        HeaderTokenIdentityConfigListener configListener = ConfigHelper.getHeaderTokenIdentityConfigListener();
        configListener.getConfig().getHeaders().getHeader().add(HeaderHelper.fromAuth(resource));
        configListener.save();
    }

    @Override
    protected void deleteResource(String resourceId) {
        HeaderTokenIdentityConfigListener configListener = ConfigHelper.getHeaderTokenIdentityConfigListener();
        Iterator<HeaderMapping> iterator = configListener.getConfig().getHeaders().getHeader().iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getId().equals(resourceId)) {
                iterator.remove();
                break;
            }
        }
        configListener.save();
    }

}
