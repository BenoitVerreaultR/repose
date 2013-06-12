package com.rackspace.papi.components.identity.header;

import com.rackspace.papi.commons.util.StringUtilities;
import com.rackspace.papi.commons.util.http.PowerApiHeader;
import com.rackspace.papi.commons.util.servlet.http.ReadableHttpServletResponse;
import com.rackspace.papi.filter.logic.FilterAction;
import com.rackspace.papi.filter.logic.FilterDirector;
import com.rackspace.papi.filter.logic.HeaderManager;
import com.rackspace.papi.filter.logic.common.AbstractFilterLogicHandler;
import com.rackspace.papi.filter.logic.impl.FilterDirectorImpl;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

public class HeaderTokenIdentityHandler extends AbstractFilterLogicHandler {
   
   private final String quality;
   private final String tokenHeader;
   private final Map<String, String> tokenToGroup;

   public HeaderTokenIdentityHandler(Map<String, String> tokenToGroup, String tokenHeader, String quality) {
      this.quality = quality;
      this.tokenHeader = tokenHeader;
      this.tokenToGroup = tokenToGroup;
   }

   @Override
   public FilterDirector handleRequest(HttpServletRequest request, ReadableHttpServletResponse response) {

      final FilterDirector filterDirector = new FilterDirectorImpl();
      final HeaderManager headerManager = filterDirector.requestHeaderManager();
      filterDirector.setFilterAction(FilterAction.PASS);

      String token = request.getHeader(tokenHeader);
      String group = tokenToGroup.get(token);

      if (group != null) {
         headerManager.appendHeader(PowerApiHeader.USER.toString(), group + quality);
         headerManager.appendHeader(PowerApiHeader.GROUPS.toString(), group + quality);
      }

      return filterDirector;
   }
}
