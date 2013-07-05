package com.rackspace.papi.components.identity.header;



import com.rackspace.papi.commons.util.http.HttpStatusCode;
import com.rackspace.papi.commons.util.http.PowerApiHeader;
import com.rackspace.papi.commons.util.servlet.http.ReadableHttpServletResponse;
import com.rackspace.papi.filter.logic.FilterAction;
import com.rackspace.papi.filter.logic.FilterDirector;

import java.util.*;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.RunWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(Enclosed.class)
public class HeaderTokenIdentityHandlerTest {

    public static class WhenHandlingRequests {

        private Map<String, String> tokenToGroup;

        private static String TOKEN_HEADER = "X-TOKEN";
        private static String URI1 = "/some/uri/";
        private static String GROUP1 = "Group1";
        private static String TOKEN1 = "1111";
        private static String GROUP2 = "Group2";
        private static String TOKEN2 = "2222";
        private static String INVALID_TOKEN = "3333";
        private HttpServletRequest request;
        private ReadableHttpServletResponse response;
        private HeaderTokenIdentityHandler handler;

        @Before
        public void setUp() {

            tokenToGroup = new HashMap<String, String>();
            tokenToGroup.put(TOKEN1, GROUP1);
            tokenToGroup.put(TOKEN2, GROUP2);
            
            handler = new HeaderTokenIdentityHandler(tokenToGroup, TOKEN_HEADER);
            request = mock(HttpServletRequest.class);
            response = mock(ReadableHttpServletResponse.class);

        }

        @Test
        public void shouldSetTheUserHeaderToTheFirstGroup() {
            when(request.getRequestURI()).thenReturn(URI1);
            when(request.getHeader(TOKEN_HEADER)).thenReturn(TOKEN1);

            FilterDirector result = handler.handleRequest(request, response);

            Set<String> values = result.requestHeaderManager().headersToAdd().get(PowerApiHeader.USER.toString().toLowerCase());
            assertFalse("Should have " + PowerApiHeader.USER.toString() + " header set.", values == null || values.isEmpty());

            String header = values.iterator().next();

            assertEquals(PowerApiHeader.USER.toString() + " must be what is expected ", GROUP1, header);

        }

        @Test
        public void shouldReturnUnauthorizedOnNoTokens() {

            when(request.getRequestURI()).thenReturn(URI1);

            FilterDirector result = handler.handleRequest(request, response);

            assertTrue("Expect a return filter action", result.getFilterAction().equals(FilterAction.RETURN));
            assertTrue("Expect an 401-unauthorized return code", result.getResponseStatus().equals(HttpStatusCode.UNAUTHORIZED));

        }

        @Test
        public void shouldReturnUnauthorizedOnInvalidToken() {

            when(request.getRequestURI()).thenReturn(URI1);
            when(request.getHeader(TOKEN_HEADER)).thenReturn(INVALID_TOKEN);

            FilterDirector result = handler.handleRequest(request, response);

            assertTrue("Expect a return filter action", result.getFilterAction().equals(FilterAction.RETURN));
            assertTrue("Expect an 401-unauthorized return code", result.getResponseStatus().equals(HttpStatusCode.UNAUTHORIZED));

        }

    }
}
