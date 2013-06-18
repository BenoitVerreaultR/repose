package com.rackspace.papi.service.context.impl;

import com.rackspace.papi.commons.config.manager.UpdateListener;
import com.rackspace.papi.commons.util.proxy.RequestProxyService;
import com.rackspace.papi.commons.util.thread.DestroyableThreadWrapper;
import com.rackspace.papi.commons.util.thread.Poller;
import com.rackspace.papi.model.SystemModel;
import com.rackspace.papi.service.config.ConfigurationService;
import com.rackspace.papi.service.context.ContextAdapter;
import com.rackspace.papi.service.context.ServletContextAware;
import com.rackspace.papi.service.context.ServletContextHelper;
import com.rackspace.papi.service.event.common.EventService;
import com.rackspace.papi.service.healthcheck.HealthCheckTask;
import com.rackspace.papi.service.threading.ThreadingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import java.net.URL;

@Component("healthCheckServiceContext")
public class HealthCheckServiceContext implements ServletContextAware {

    public static final String SERVICE_NAME = "powerapi:/services/healthcheck";

    private SystemModel config;
    private final ConfigurationService configurationManager;
    private final PowerApiConfigListener configListener;
    private HealthCheckTask task;
    private DestroyableThreadWrapper healthCheckThread;

    @Autowired
    public HealthCheckServiceContext(
            @Qualifier("configurationManager") ConfigurationService configurationManager)
    {
        this.configListener = new PowerApiConfigListener();
        this.configurationManager = configurationManager;
    }

    private class PowerApiConfigListener implements UpdateListener<SystemModel> {
        private boolean initialized = false;

        @Override
        public void configurationUpdated(SystemModel configurationObject) {
            config = configurationObject;
            task.setSystemModel(config);
            initialized = true;
        }

        @Override
        public boolean isInitialized() {
            return initialized;
        }
    }

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {

        ServletContext ctx = servletContextEvent.getServletContext();
        ServletContextHelper helper = ServletContextHelper.getInstance(ctx);
        ContextAdapter ca = helper.getPowerApiContext();

        ThreadingService threadingService = ca.threadingService();
        RequestProxyService requestProxyService = ca.requestProxyService();
        EventService eventService = ca.eventService();

        task = new HealthCheckTask(eventService, requestProxyService);
        final Poller poller = new Poller(task, 60000);
        healthCheckThread = new DestroyableThreadWrapper(threadingService.newThread(poller, "Health Check Thread"), poller);

        URL xsdURL = getClass().getResource("/META-INF/schema/system-model/system-model.xsd");
        configurationManager.subscribeTo("system-model.cfg.xml", xsdURL, configListener, SystemModel.class);

        healthCheckThread.start();

    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if (configurationManager != null) {
            configurationManager.unsubscribeFrom("system-model.cfg.xml", configListener);
        }
    }
}
