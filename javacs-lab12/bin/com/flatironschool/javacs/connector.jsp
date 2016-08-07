<%@ page import="java.io.*" %>

<%
out.println("hello");
String name = request.getParameter("searchterm");
out.println(name);
%>
