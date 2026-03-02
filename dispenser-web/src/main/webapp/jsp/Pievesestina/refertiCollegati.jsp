<%--
 Engineering Ingegneria Informatica S.p.A.

 Copyright (C) 2023 Regione Emilia-Romagna
 <p/>
 This program is free software: you can redistribute it and/or modify it under the terms of
 the GNU Affero General Public License as published by the Free Software Foundation,
 either version 3 of the License, or (at your option) any later version.
 <p/>
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 See the GNU Affero General Public License for more details.
 <p/>
 You should have received a copy of the GNU Affero General Public License along with this program.
 If not, see <https://www.gnu.org/licenses/>.
 --%>

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
