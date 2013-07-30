package com.rackspace.repose.service.configuration.servlet;

import com.rackspace.papi.components.access.uri.config.Access;
import com.rackspace.papi.components.access.uri.config.AccessGroup;
import com.rackspace.papi.components.access.uri.config.UriAccessConfig;
import com.rackspace.papi.model.Filter;
import com.rackspace.papi.model.ReposeCluster;
import com.rackspace.papi.model.SystemModel;
import com.rackspace.repose.service.configuration.context.DestinationRouterConfigListener;
import com.rackspace.repose.service.configuration.context.SystemModelConfigListener;
import com.rackspace.repose.service.configuration.context.UriAccessConfigListener;
import com.rackspace.repose.service.configuration.helper.AccessGroupHelper;
import com.rackspace.repose.service.configuration.helper.ConfigHelper;
import com.rackspace.repose.service.configuration.helper.FilterHelper;
import com.rackspace.repose.service.configuration.helper.TargetHelper;
import com.rackspace.repose.service.configuration.resource.Api;
import org.openrepose.components.routing.servlet.config.DestinationRouterConfiguration;

import java.util.Iterator;

public class ApiServlet extends BaseServlet<Api> {

    @Override
    protected void updateResource(Api oldResource, Api newResource) {
        SystemModelConfigListener systemModelConfigListener = ConfigHelper.getSystemModelConfigListener();
        SystemModel config = systemModelConfigListener.getConfig();

        UriAccessConfigListener uriAccessConfigListener = ConfigHelper.getUriAccessConfigListener();
        UriAccessConfig uriAccessConfig = uriAccessConfigListener.getConfig();

        for (Filter filter : config.getReposeCluster().get(0).getFilters().getFilter()) {
            if (FilterHelper.DESTINATION_ROUTER.equals(filter.getName()) && oldResource.getId().equals(filter.getId())) {
                FilterHelper.update(filter, newResource);
                break;
            }
        }

        // update the api inside the uri access configs
        for (AccessGroup accessGroup : uriAccessConfig.getAccessGroup()) {
            for (Access access : accessGroup.getAccess()) {
                if (access.getId().equals(oldResource.getId())) {
                    AccessGroupHelper.update(access, newResource);
                    break;
                }
            }
        }

        systemModelConfigListener.registerApiListener(newResource, true);
        systemModelConfigListener.save();
        uriAccessConfigListener.save();
    }

    @Override
    protected void createResource(Api resource) {
        SystemModelConfigListener configListener = ConfigHelper.getSystemModelConfigListener();
        SystemModel config = configListener.getConfig();
        config.getReposeCluster().get(0).getFilters().getFilter().add(FilterHelper.fromApi(resource));
        configListener.registerApiListener(resource, true);
        configListener.save();
    }

    @Override
    protected void deleteResource(String resourceId) {
        SystemModelConfigListener systemModelConfigListener = ConfigHelper.getSystemModelConfigListener();
        SystemModel systemModelConfig = systemModelConfigListener.getConfig();

        UriAccessConfigListener uriAccessConfigListener = ConfigHelper.getUriAccessConfigListener();
        UriAccessConfig uriAccessConfig = uriAccessConfigListener.getConfig();

        // we only bother about removing the api from the system model config, the other file will be managed when the
        // system model config updates
        ReposeCluster reposeCluster = systemModelConfig.getReposeCluster().get(0);
        Iterator<Filter> filterIterator = reposeCluster.getFilters().getFilter().iterator();
        while (filterIterator.hasNext()) {
            Filter filter = filterIterator.next();
            if (FilterHelper.DESTINATION_ROUTER.equals(filter.getName()) && resourceId.equals(filter.getId())) {
                filterIterator.remove();
                break;
            }
        }

        // remove the api from access groups.
        for (AccessGroup accessGroup : uriAccessConfig.getAccessGroup()) {
            Iterator<Access> accessIterator = accessGroup.getAccess().iterator();
            while (accessIterator.hasNext()) {
                Access access = accessIterator.next();
                if (access.getId().equals(resourceId)) {
                    accessIterator.remove();
                    break;
                }
            }
        }

        systemModelConfigListener.save();
        uriAccessConfigListener.save();
    }

}
