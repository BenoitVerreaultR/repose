package com.rackspace.papi.components.identity.header;



import com.rackspace.papi.commons.util.http.PowerApiHeader;
import com.rackspace.papi.commons.util.servlet.http.ReadableHttpServletResponse;
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

        private static String TOKEN_HEADER = "X-USER";
        private static String QUALITY_VALUE = ";q=0.5";
        private static String URI1 = "/some/uri/";
        private static String GROUP1 = "1234";
        private static String TOKEN1 = "1234";
        private static String GROUP2 = "4321";
        private static String TOKEN2 = "4321";
        private HttpServletRequest request;
        private ReadableHttpServletResponse response;
        private HeaderTokenIdentityHandler handler;

        @Before
        public void setUp() {

            tokenToGroup = new HashMap<String, String>();
            tokenToGroup.put(TOKEN1, GROUP1);
            tokenToGroup.put(TOKEN2, GROUP2);
            
            handler = new HeaderTokenIdentityHandler(tokenToGroup, TOKEN_HEADER, QUALITY_VALUE);
            request = mock(HttpServletRequest.class);
            response = mock(ReadableHttpServletResponse.class);

        }

        @Test
        public void shouldSetTheUserHeaderToTheRegexResult() {
            when(request.getRequestURI()).thenReturn(URI1);

            FilterDirector result = handler.handleRequest(request, response);

            Set<String> values = result.requestHeaderManager().headersToAdd().get(PowerApiHeader.USER.toString().toLowerCase());
            assertFalse("Should have " + PowerApiHeader.USER.toString() + " header set.", values == null || values.isEmpty());

            String userName = values.iterator().next();

            assertEquals("Should find user name in header", GROUP1 + QUALITY_VALUE, userName);
        }

        @Test
        public void shouldSetTheUserHeaderToThe2ndRegexResult() {
            when(request.getRequestURI()).thenReturn(URI1);

            FilterDirector result = handler.handleRequest(request, response);

            Set<String> values = result.requestHeaderManager().headersToAdd().get(PowerApiHeader.USER.toString().toLowerCase());
            assertFalse("Should have " + PowerApiHeader.USER.toString() + " header set.", values == null || values.isEmpty());

            String userName = values.iterator().next();

            assertEquals("Should find user name in header", GROUP1 + QUALITY_VALUE, userName);
        }

        @Test
        public void shouldNotHaveUserHeader() {
            when(request.getRequestURI()).thenReturn(URI1);

            FilterDirector result = handler.handleRequest(request, response);

            Set<String> values = result.requestHeaderManager().headersToAdd().get(PowerApiHeader.USER.toString().toLowerCase());
            assertTrue("Should not have " + PowerApiHeader.USER.toString() + " header set.", values == null || values.isEmpty());

        }
    }
}
