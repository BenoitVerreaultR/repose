package com.rackspace.papi.components.access.uri;

import com.rackspace.papi.commons.util.http.HttpStatusCode;
import com.rackspace.papi.commons.util.http.PowerApiHeader;
import com.rackspace.papi.commons.util.servlet.http.ReadableHttpServletResponse;
import com.rackspace.papi.components.access.uri.config.*;
import com.rackspace.papi.filter.logic.FilterAction;
import com.rackspace.papi.filter.logic.FilterDirector;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;

import javax.servlet.http.HttpServletRequest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Enclosed.class)
public class UriAccessHandlerTest {

    public static class WhenHandlingRequests {

        private static String URI1 = "/some/random/uri/";
        private static String REGEX1 = "/some/random/.*";
        private static String URI2 = "/some/other/uri/";
        private static String REGEX2 = ".*";
        private static String GROUP1 = "Group1";
        private static String GROUP2 = "Group2";
        private HttpServletRequest request;
        private ReadableHttpServletResponse response;
        private UriAccessHandler handler;
        private UriAccessConfigHelper helper;
        private UriAccessConfig config;

        @Before
        public void setUp() {

            helper = mock(UriAccessConfigHelper.class);
            request = mock(HttpServletRequest.class);
            response = mock(ReadableHttpServletResponse.class);

            config = new UriAccessConfig();
            AccessGroup accessGroup;
            Access access;

            accessGroup = new AccessGroup();
            config.getAccessGroup().add(accessGroup);
            access = new Access();
            access.setUri(URI1);
            access.setUriRegex(REGEX1);
            access.getHttpMethods().add(HttpMethod.GET);
            accessGroup.getAccess().add(new AccessWrapper(access));
            accessGroup.setGroup(GROUP1);
            accessGroup.setDefault(false);

            when(helper.getGroup(GROUP1)).thenReturn(accessGroup);

            accessGroup = new AccessGroup();
            config.getAccessGroup().add(accessGroup);
            accessGroup.setGroup(GROUP2);
            accessGroup.setDefault(true);

            when(helper.getGroup(GROUP2)).thenReturn(accessGroup);

            handler = new UriAccessHandler(helper);

        }

        @Test
        public void nonDefaultGroupWithAccessibleUrl() {

            when(request.getRequestURI()).thenReturn(URI1);
            when(request.getHeader(PowerApiHeader.GROUPS.toString())).thenReturn(GROUP1);
            when(request.getMethod()).thenReturn(HttpMethod.GET.toString());

            FilterDirector result = handler.handleRequest(request, response);

            assertTrue("Should receive a pass filter action", result.getFilterAction().equals(FilterAction.PASS));

        }

        @Test
        public void nonDefaultGroupWithInaccessibleUrl() {

            when(request.getRequestURI()).thenReturn(URI2);
            when(request.getHeader(PowerApiHeader.GROUPS.toString())).thenReturn(GROUP1);
            when(request.getMethod()).thenReturn(HttpMethod.GET.toString());

            FilterDirector result = handler.handleRequest(request, response);

            assertTrue("Should not have access to uri", result.getResponseStatus().equals(HttpStatusCode.UNAUTHORIZED));
            assertTrue("Validate filter action", result.getFilterAction().equals(FilterAction.RETURN));

        }

        @Test
        public void defaultGroupWithInaccessibleUrl() {

            when(request.getRequestURI()).thenReturn(URI1);
            when(request.getHeader(PowerApiHeader.GROUPS.toString())).thenReturn(GROUP2);
            when(request.getMethod()).thenReturn(HttpMethod.GET.toString());

            FilterDirector result = handler.handleRequest(request, response);

            assertTrue("Should not have access to uri", result.getResponseStatus().equals(HttpStatusCode.UNAUTHORIZED));
            assertTrue("Validate filter action", result.getFilterAction().equals(FilterAction.RETURN));

        }

    }

}
