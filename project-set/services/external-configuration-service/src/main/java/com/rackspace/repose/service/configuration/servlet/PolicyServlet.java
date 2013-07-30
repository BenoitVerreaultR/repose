package com.rackspace.repose.service.configuration.servlet;

import com.rackspace.papi.components.access.uri.config.AccessGroup;
import com.rackspace.papi.components.access.uri.config.UriAccessConfig;
import com.rackspace.repose.service.configuration.context.RateLimitConfigListener;
import com.rackspace.repose.service.configuration.context.SystemModelConfigListener;
import com.rackspace.repose.service.configuration.context.UriAccessConfigListener;
import com.rackspace.repose.service.configuration.helper.AccessGroupHelper;
import com.rackspace.repose.service.configuration.helper.ConfigHelper;
import com.rackspace.repose.service.configuration.helper.ConfiguredLimitGroupHelper;
import com.rackspace.repose.service.configuration.resource.Api;
import com.rackspace.repose.service.configuration.resource.Policy;
import com.rackspace.repose.service.ratelimit.config.ConfiguredLimitGroup;
import com.rackspace.repose.service.ratelimit.config.RateLimitingConfiguration;

import java.util.Iterator;
import java.util.Map;

public class PolicyServlet extends BaseServlet<Policy> {

    @Override
    protected void updateResource(Policy oldResource, Policy newResource) {

        // get needed configs
        SystemModelConfigListener systemModelConfigListener = ConfigHelper.getSystemModelConfigListener();
        Map<String,Api> apiById = systemModelConfigListener.getApiById();

        UriAccessConfigListener uriAccessConfigListener = ConfigHelper.getUriAccessConfigListener();
        UriAccessConfig config = uriAccessConfigListener.getConfig();

        RateLimitConfigListener rateLimitConfigListener = ConfigHelper.getRateLimitConfigListener();
        RateLimitingConfiguration rateLimitConfig = rateLimitConfigListener.getConfig();

        // update the access group.
        Iterator<AccessGroup> accessGroupIterator = config.getAccessGroup().iterator();
        while (accessGroupIterator.hasNext()) {
            AccessGroup accessGroup = accessGroupIterator.next();
            if (accessGroup.getId().equals(oldResource.getId())) {
                AccessGroupHelper.update(accessGroup, newResource);
                AccessGroupHelper.update(accessGroup, apiById);
                break;
            }
        }

        // update the rate limit group.
        Iterator<ConfiguredLimitGroup> limitGroupIterator = rateLimitConfig.getLimitGroup().iterator();
        while (limitGroupIterator.hasNext()) {
            ConfiguredLimitGroup limitGroup = limitGroupIterator.next();
            if (limitGroup.getId().equals(oldResource.getId())) {
                ConfiguredLimitGroupHelper.update(limitGroup, newResource);
                break;
            }
        }

        uriAccessConfigListener.save();
        rateLimitConfigListener.save();
    }

    @Override
    protected void createResource(Policy resource) {

        // get needed configs
        SystemModelConfigListener systemModelConfigListener = ConfigHelper.getSystemModelConfigListener();
        UriAccessConfigListener uriAccessConfigListener = ConfigHelper.getUriAccessConfigListener();
        UriAccessConfig config = uriAccessConfigListener.getConfig();

        // add the access group
        AccessGroup accessGroup = AccessGroupHelper.fromPolicy(resource, systemModelConfigListener.getApiById());
        config.getAccessGroup().add(accessGroup);

        // add the rate limit
        RateLimitConfigListener rateLimitConfigListener = ConfigHelper.getRateLimitConfigListener();
        RateLimitingConfiguration rateLimitConfig = rateLimitConfigListener.getConfig();
        ConfiguredLimitGroup limitGroup = ConfiguredLimitGroupHelper.fromPolicy(resource);
        rateLimitConfig.getLimitGroup().add(limitGroup);

        uriAccessConfigListener.save();
        rateLimitConfigListener.save();
    }

    @Override
    protected void deleteResource(String resourceId) {

        // get needed configs
        UriAccessConfigListener uriAccessConfigListener = ConfigHelper.getUriAccessConfigListener();
        UriAccessConfig config = uriAccessConfigListener.getConfig();

        RateLimitConfigListener rateLimitConfigListener = ConfigHelper.getRateLimitConfigListener();
        RateLimitingConfiguration rateLimitConfig = rateLimitConfigListener.getConfig();

        // remove access group
        Iterator<AccessGroup> accessGroupIterator = config.getAccessGroup().iterator();
        while (accessGroupIterator.hasNext()) {
            AccessGroup accessGroup = accessGroupIterator.next();
            if (accessGroup.getId().equals(resourceId)) {
                accessGroupIterator.remove();
                break;
            }
        }

        // remove rate limit group
        Iterator<ConfiguredLimitGroup> rateLimitGroupIterator = rateLimitConfig.getLimitGroup().iterator();
        while (rateLimitGroupIterator.hasNext()) {
            ConfiguredLimitGroup limitGroup = rateLimitGroupIterator.next();
            if (limitGroup.getId().equals(resourceId)) {
                rateLimitGroupIterator.remove();
                break;
            }
        }

        rateLimitConfigListener.save();
        uriAccessConfigListener.save();
    }

}
