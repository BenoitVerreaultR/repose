package com.rackspace.repose.service.configuration.context;


import com.rackspace.repose.service.configuration.helper.ConfigHelper;
import com.rackspace.repose.service.configuration.helper.PolicyHelper;
import com.rackspace.repose.service.configuration.resource.Policy;
import com.rackspace.repose.service.ratelimit.config.ConfiguredLimitGroup;
import com.rackspace.repose.service.ratelimit.config.RateLimitingConfiguration;

import java.util.List;

public class RateLimitConfigListener extends ConfigurationUpdater<RateLimitingConfiguration> {
    private static final String CONFIG_NAME = "rate-limiting.cfg.xml";

    @Override
    public String getConfigName() {
        return CONFIG_NAME;
    }

    @Override
    public void configUpdated() {
        updatePolicyFromLimitGroups(config.getLimitGroup());
    }

    private void updatePolicyFromLimitGroups(List<ConfiguredLimitGroup> limitGroups) {
        for (ConfiguredLimitGroup limitGroup : limitGroups) {
            Policy policy = policyById.get(limitGroup.getId());
            if (policy == null) {
                continue;
            }
            PolicyHelper.update(policy, limitGroup.getLimit().get(0));
        }
    }
}
