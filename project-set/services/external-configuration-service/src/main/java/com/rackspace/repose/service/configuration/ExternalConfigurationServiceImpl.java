package com.rackspace.repose.service.configuration;

import com.rackspace.papi.service.config.ConfigurationService;
import com.rackspace.repose.service.configuration.context.ConfigurationServletContextListener;
import com.rackspace.repose.service.configuration.servlet.ApiServlet;
import com.rackspace.repose.service.configuration.servlet.AuthServlet;
import com.rackspace.repose.service.configuration.servlet.PolicyServlet;
import com.rackspace.repose.service.configuration.servlet.ServiceClusterServlet;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;

public class ExternalConfigurationServiceImpl implements ExternalConfigurationService {

    private static final Logger LOG = org.slf4j.LoggerFactory.getLogger(ExternalConfigurationServiceImpl.class);
    private ConfigurationServletContextListener contextManager;
    private Server server;
    private boolean started;

    public ExternalConfigurationServiceImpl(ConfigurationService configurationManager) {
        contextManager = new ConfigurationServletContextListener(configurationManager);
    }

    @Override
    public void startServer(int port) {

        if (started)
            return;

        server = new Server();

        SelectChannelConnector conn = new SelectChannelConnector();
        conn.setPort(port);

        server.setConnectors(new Connector[]{conn});

        final ServletContextHandler rootContext = buildRootContext(server);
        rootContext.addServlet(new ServletHolder(new ApiServlet()), "/api/*");
        rootContext.addServlet(new ServletHolder(new AuthServlet()), "/auth/*");
        rootContext.addServlet(new ServletHolder(new PolicyServlet()), "/policy/*");
        rootContext.addServlet(new ServletHolder(new ServiceClusterServlet()), "/servicecluster/*");
        server.setHandler(rootContext);
        try {
            server.start();
        } catch (Exception e) {
            LOG.error("Failed to start configuration server", e);
        }

        started = true;
    }

    @Override
    public void stopServer() {

        if (!started)
            return;

        try {
            server.stop();
        } catch (Exception e) {
            LOG.error("Failed to stop configuration server", e);
        }

        started = false;
        server = null;
    }

    private ServletContextHandler buildRootContext(Server serverReference) {
        final ServletContextHandler servletContext = new ServletContextHandler(serverReference, "/");
        servletContext.addEventListener(contextManager);
        return servletContext;
    }

}
