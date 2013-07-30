package com.rackspace.repose.service.configuration.context;


import com.rackspace.papi.components.access.uri.config.AccessGroup;
import com.rackspace.papi.components.access.uri.config.UriAccessConfig;
import com.rackspace.repose.service.configuration.helper.PolicyHelper;
import com.rackspace.repose.service.configuration.resource.Policy;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UriAccessConfigListener extends ConfigurationUpdater<UriAccessConfig> {
    private static final String CONFIG_NAME = "uri-access.cfg.xml";

    @Override
    public String getConfigName() {
        return CONFIG_NAME;
    }

    @Override
    public void configUpdated() {
        updatePolicyFromAccessGroups(config.getAccessGroup());
    }

    private void updatePolicyFromAccessGroups(List<AccessGroup> accessGroups) {
        // backup the content
        Map<String, Policy> oldPolicy = new HashMap<String, Policy>();
        oldPolicy.putAll(policyById);
        policyById.clear();

        // update the policy map with objects still in the file
        for (AccessGroup accessGroup : accessGroups) {
            Policy policy = oldPolicy.remove(accessGroup.getId());
            if (policy == null) {
                policy = PolicyHelper.fromAccessGroup(accessGroup);
            } else {
                PolicyHelper.update(policy, accessGroup);
            }
            policyById.put(policy.getId(), policy);
        }

    }
}