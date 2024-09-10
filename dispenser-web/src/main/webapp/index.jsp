<%-- 
    Document   : index
    Created on : 10-nov-2016, 16.59.38
    Author     : Snidero_L
    Copiato da : saceriam
--%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%@page session="false" %>
<%
    String redirectURL = response.encodeRedirectURL("Login.html");
    response.setStatus(303);
    response.addHeader("Pragma", "no-cache");
    response.addHeader("Cache-Control", "no-cache");    
    response.addHeader("Cache-Control", "no-store");
    response.addHeader("Cache-Control", "must-revalidate");
    response.setHeader("Location", redirectURL);
%>
