<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="it.eng.dispenser.slite.gen.form.PievesestinaForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="REFERTI COLLEGATI">
    	<script src="<c:url value='/js/help/inithighlightingjs.js' />" type="text/javascript"></script>    
    </sl:head>
    <script type="text/javascript" src="<c:url value="/js/dips-common.js"/>" ></script>
    <script type="text/javascript" src="<c:url value="/js/dips-${sessionScope['###_NOME_RICERCA']}.js"/>" ></script>
    <script type="text/javascript" src="<c:url value="/js/dips-startup.js"/>" ></script>
    <sl:body>
        <sl:header />
        <sl:menu />
        <sl:content>
            <slf:messageBox/>
            <sl:newLine skipLine="true"/>
            <sl:contentTitle title="REFERTI COLLEGATI" codiceMenu="${ricercheLoader.ricerche[sessionScope['###_NOME_RICERCA']].nmEntryMenu}" />
            <slf:fieldBarDetailTag name="<%= PievesestinaForm.Back.NAME%>" />  
            <h2>Referti collegati al referto ID <%= ((PievesestinaForm)pageContext.getSession().getAttribute("###_FORM_CONTAINER")).getRefertiCollegatiList().getTable().getRow(0).getString("cdud") %> del <%= ((PievesestinaForm)pageContext.getSession().getAttribute("###_FORM_CONTAINER")).getRefertiCollegatiList().getTable().getRow(0).getString("dataRefertoString") %></h2>
            <sl:newLine skipLine="true"/>
            <slf:list name="<%=PievesestinaForm.RefertiCollegatiList.NAME%>" />
        </sl:content>
        <sl:footer />
        <%--<div id="anchorRefColl"></div>--%>
    </sl:body>
</sl:html>
