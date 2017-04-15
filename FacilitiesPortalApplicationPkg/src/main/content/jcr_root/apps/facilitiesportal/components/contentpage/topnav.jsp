<%@include file="/libs/foundation/global.jsp"%><%
%><%@ page import="java.util.Iterator,
         com.day.cq.wcm.api.PageFilter"%><%
     /* Create a new Page object using the path of the current page */    
    // String listroot = properties.get("listroot", currentPage.getPath());
    String listroot = "/content/GWSforms/en/";
     Page rootPage = pageManager.getPage(listroot);
     /* iterate through the child pages and gather properties */
     if (rootPage != null) {
         Iterator<Page> children = rootPage.listChildren(new PageFilter(request));
         %><ul id="menu"><%
         while (children.hasNext()) {
             Page child = children.next();
             String title = child.getTitle() == null ? child.getName() : child.getTitle();
             %>
<li><a href="<%= child.getPath() %>.html"><b><%= title %></b></a></li>
             <%
         }
        %></ul><%
     }
 %>