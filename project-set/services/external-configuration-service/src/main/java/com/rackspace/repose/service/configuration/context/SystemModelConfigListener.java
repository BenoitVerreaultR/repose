package com.rackspace.repose.service.configuration.context;

import com.rackspace.papi.commons.util.StringUtilities;
import com.rackspace.papi.model.*;
import com.rackspace.repose.service.configuration.helper.*;
import com.rackspace.repose.service.configuration.resource.Api;
import com.rackspace.repose.service.configuration.resource.ServiceCluster;
import org.openrepose.components.routing.servlet.config.DestinationRouterConfiguration;
import org.slf4j.Logger;

import javax.servlet.ServletContextEvent;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SystemModelConfigListener extends ConfigurationUpdater<SystemModel> {
    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(SystemModelConfigListener.class);
    private static final String CONFIG_NAME = "system-model.cfg.xml";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        super.contextInitialized(sce);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        super.contextDestroyed(sce);
        for (DestinationRouterConfigListener listener : ConfigHelper.getDestinationRouterConfigListener()) {
            listener.unsubscribe();
        }
    }

    @Override
    public String getConfigName() {
        return CONFIG_NAME;
    }

    @Override
    public void configUpdated() {
        ReposeCluster reposeCluster = config.getReposeCluster().get(0);
        updateServiceClusterFromReposeCluster(reposeCluster);
        updateServiceClustersFromServiceCluster(config.getServiceCluster());
        updateApiFromReposeCluster(reposeCluster);
        initialized = true;
    }

    private void updateApiFromReposeCluster(ReposeCluster reposeCluster) {
        // create a new map that will hold api in the updated configuration objects.
        Map<String, Api> oldApiById = new HashMap<String, Api>();
        oldApiById.putAll(apiById);
        apiById.clear();
        apiByName.clear();

        for (Filter filter : reposeCluster.getFilters().getFilter()) {
            // only process destination router filters.
            if (!StringUtilities.nullSafeEqualsIgnoreCase(FilterHelper.DESTINATION_ROUTER, filter.getName()))
                continue;

            // update current api object or create a new one
            boolean register = false;
            Api api = oldApiById.remove(filter.getId());
            if (api == null) {
                api = ApiHelper.fromFilter(filter);
                register = true;
            } else {
                // the api already existed, check the name of the api changed, if so update subscription
                String oldApiName = api.getName();
                String newApiName = ApiHelper.extractName(filter);
                if (!StringUtilities.nullSafeEqualsIgnoreCase(oldApiName, newApiName)) {
                    unregisterApiListener(oldApiName);
                    register = true;
                }
                ApiHelper.update(api, filter);
            }

            // add the updated api to new collections
            apiById.put(api.getId(), api);
            apiByName.put(api.getName(), api);

            // register the api if it was created or updated
            if (register) {
                registerApiListener(api, false);
            }
        }

        // remove old listener of api that aren't in the configuration anymore.
        for (Api api : oldApiById.values()) {
            unregisterApiListener(api.getName());
        }

    }

    private void updateServiceClusterFromReposeCluster(ReposeCluster reposeCluster) {
        serviceClusterById.clear();
        for (DestinationCluster destination : reposeCluster.getDestinations().getTarget()) {
            ServiceCluster serviceCluster = ServiceClusterHelper.fromDestination(destination);
            serviceClusterById.put(destination.getId(), serviceCluster);
        }
    }

    private void updateServiceClustersFromServiceCluster(List<Cluster> clusters) {
        for (Cluster cluster : clusters) {
            ServiceCluster serviceCluster = serviceClusterById.get(cluster.getId());
            if (serviceCluster == null) {
                LOG.error("could not find service cluster to update configuration");
                continue;
            }
            ServiceClusterHelper.update(serviceCluster, cluster);
        }
    }

    public void registerApiListener(Api api, boolean saveToDisk) {
        DestinationRouterConfigListener configListener = new DestinationRouterConfigListener(api, configurationManager);
        ConfigHelper.putDestinationRouterConfigListener(api.getName(), configListener);

        if (saveToDisk) {
            DestinationRouterConfiguration destinationRouterConfiguration = new DestinationRouterConfiguration();
            destinationRouterConfiguration.setTarget(TargetHelper.fromApi(api));
            configListener.configurationUpdated(destinationRouterConfiguration);
            configListener.save();
        }

        configurationManager.subscribeTo(configListener.getConfigName(), configListener, configListener.getParameterClass());
    }

    private void unregisterApiListener(String apiName) {
        DestinationRouterConfigListener configListener = ConfigHelper.removeDestinationRouterConfig(apiName);
        configurationManager.unsubscribeFrom(configListener.getConfigName(), configListener);
        configListener.delete();
    }

    public Map<String, Api> getApiById() {
        return apiById;
    }

}
