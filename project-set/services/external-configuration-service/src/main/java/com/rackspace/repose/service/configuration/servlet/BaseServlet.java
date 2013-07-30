package com.rackspace.repose.service.configuration.servlet;

import com.rackspace.papi.commons.util.transform.json.JacksonJaxbTransform;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class BaseServlet<T> extends HttpServlet {

    private static final JacksonJaxbTransform transform = new JacksonJaxbTransform();
    private final Pattern regex;
    private Class clazz = null;

    public BaseServlet() {
        regex = Pattern.compile(String.format("/%s/(.*)", getParameterClassName()));
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            String id = extractIdFromUri(request.getRequestURI());

            // if no id is found, return list of resources.
            if (id == null || id.equals("")) {
                Collection<T> resources = getResources();
                createResponse(response, resources);
                return;
            }

            // return the specified resource if found.
            T resource = getResource(id);
            if (resource == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            createResponse(response, resource);
            return;
        } catch (IOException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            // check if we have an old resource with same id
            String id = extractIdFromUri(request.getRequestURI());
            T oldResource = getResource(id);

            // get the new resource data
            String data = request.getParameter("data");
            T newResource = extractData(data);

            if (newResource == null) {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            }

            // we update
            if (oldResource != null) {
                updateResource(oldResource, newResource);
                response.setStatus(HttpServletResponse.SC_OK);
                return;
            }

            // we create
            createResource(newResource);
            response.setStatus(HttpServletResponse.SC_CREATED);
        } catch (IOException e) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
    }

    @Override
    public void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {

            String id = extractIdFromUri(request.getRequestURI());

            // if no id is found, can't delete
            if (id == null || id.equals("")) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            T resource = getResource(id);

            if (resource == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            deleteResource(id);

        } catch (IOException exception) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }
    }

    protected void createResponse(HttpServletResponse response, Object resource) throws IOException {
        response.addHeader("Content-Type", "application/json; charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getOutputStream().println(transform.serialize(resource));
    }

    /**
     * Returns the class of the T class parameter.
     *
     * @return
     */
    private Class getParameterClass() {
        if (clazz != null)
            return clazz;
        try {
            return (Class) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        } catch (Exception e) {
            return null;
        }
    }

    private String getParameterClassName() {
        return getParameterClass().getSimpleName().toLowerCase();
    }

    private T extractData(String data) {
        return (T) transform.deserialize(data, getParameterClass());
    }

    private String extractIdFromUri(String uri) {
        Matcher match = regex.matcher(uri);
        if (match.matches() && match.groupCount() > 0) {
            return match.group(1);
        }
        return null;
    }

    private T getResource(String id) {
        Map<String, T> resources = (Map<String, T>) this.getServletContext().getAttribute(getParameterClassName());
        return resources.get(id);
    }

    private Collection<T> getResources() {
        Map<String, T> resources = (Map<String, T>) this.getServletContext().getAttribute(getParameterClassName());
        return resources.values();
    }

    protected abstract void updateResource(T oldResource, T newResource);

    protected abstract void createResource(T resource);

    protected abstract void deleteResource(String resourceId);

}
