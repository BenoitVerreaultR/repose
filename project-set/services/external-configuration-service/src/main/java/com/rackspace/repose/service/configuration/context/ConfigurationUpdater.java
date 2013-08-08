package com.rackspace.repose.service.configuration.context;

import com.rackspace.papi.commons.config.manager.UpdateListener;
import com.rackspace.papi.service.config.ConfigurationService;
import com.rackspace.repose.service.configuration.resource.Api;
import com.rackspace.repose.service.configuration.resource.Auth;
import com.rackspace.repose.service.configuration.resource.Policy;
import com.rackspace.repose.service.configuration.resource.ServiceCluster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.xml.bind.JAXBElement;
import java.io.File;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.Map;

public abstract class ConfigurationUpdater<T> implements UpdateListener<T>, ServletContextListener {

    private static Logger LOG = LoggerFactory.getLogger(ConfigurationUpdater.class);

    protected boolean initialized = false;
    protected T config;
    protected ConfigurationService configurationManager;
    protected Map<String, Api> apiById;
    protected Map<String, Api> apiByName;
    protected Map<String, Auth> authById;
    protected Map<String, Policy> policyById;
    protected Map<String, ServiceCluster> serviceClusterById;

    public T getConfig() {
        return config;
    }

    @Override
    public void configurationUpdated(T configurationObject) {
        config = configurationObject;
        configUpdated();
        initialized = true;
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        configurationManager = (ConfigurationService) sce.getServletContext().getAttribute(ConfigurationServletContextListener.CONFIGURATION_MANAGER);
        apiById = getAttribute(sce, ConfigurationServletContextListener.API);
        apiByName = getAttribute(sce, ConfigurationServletContextListener.API_BY_NAME);
        authById = getAttribute(sce, ConfigurationServletContextListener.AUTH);
        policyById = getAttribute(sce, ConfigurationServletContextListener.POLICY);
        serviceClusterById = getAttribute(sce, ConfigurationServletContextListener.SERVICE_CLUSTER);
        subscribe();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        unsubscribe();
    }

    public Class getParameterClass() {
        try {
            return (Class) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        } catch (Exception e) {
            return null;
        }
    }

    protected <T> T getAttribute(ServletContextEvent sce, String attribute) {
        return (T) sce.getServletContext().getAttribute(attribute);
    }

    public void subscribe() {
        configurationManager.subscribeTo(getConfigName(), this, getParameterClass());
    }

    public void unsubscribe() {
        configurationManager.unsubscribeFrom(getConfigName(), this);
    }

    public void save() {
        configurationManager.save(getConfigUri(), encapsulate(getConfig()));
    }

    /**
     * Deletes the associated config file.
     */
    public void delete() {
        try {
            File file = new File(getConfigUri());
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            LOG.error("Failed to delete the associated file", e);
        }
    }

    /**
     * Returns the file path of the config file.
     */
    private String configUri = null;
    public String getConfigUri() {
        if (configUri != null)
            return configUri;

        configUri = configurationManager.getResourceResolver().resolve(getConfigName()).name().replace("file:", "");
        return configUri;
    }

    /**
     * This is a big hack to load the ObjectFactory class associated with the config we want to save. This class is
     * necessary to load as it's the one that encapsulate the config object into a JAXBElement with proper headers
     * so that the config can be correctly written to disk.
     * @param config
     * @return
     */
    protected JAXBElement<T> encapsulate(Object config) {
        JAXBElement<T> returnValue = null;

        try {
            String configClass = config.getClass().getCanonicalName();
            String objectFactoryClass = configClass.replace(config.getClass().getSimpleName(), "ObjectFactory");
            Class clazz = Class.forName(objectFactoryClass);
            Object object = clazz.newInstance();
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.getParameterTypes().length > 0 && method.getParameterTypes()[0].getSimpleName().equals(config.getClass().getSimpleName())) {
                    returnValue = (JAXBElement<T>) method.invoke(object, config);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnValue;
    }

    public abstract String getConfigName();
    public abstract void configUpdated();

}

