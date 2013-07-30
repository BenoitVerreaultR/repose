package com.rackspace.repose.service.configuration;

import com.rackspace.papi.service.context.ServletContextAware;

public interface ExternalConfigurationService {

    public void startServer(int port);

    public void stopServer();

}
