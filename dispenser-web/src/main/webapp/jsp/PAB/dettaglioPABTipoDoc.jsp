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
<%@ page import="it.eng.dispenser.slite.gen.form.PABForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head title="DETTAGLIO DEL TIPO DOCUMENTO">
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
            <sl:contentTitle title="Dettaglio del tipo documento" codiceMenu="${ricercheLoader.ricerche[sessionScope['###_NOME_RICERCA']].nmEntryMenu}" />
            <slf:fieldBarDetailTag name="<%=PABForm.Back.NAME%>" />  

            <slf:fieldSet borderHidden="true" style="display: none">
                <slf:lblField name="<%=PABForm.TipoDocumentoDetailFake.NM_TIPO_DOC%>" width="w100" controlWidth="w40"
			labelWidth="w40" />
                <sl:newLine />
            </slf:fieldSet>
            
            <sl:newLine skipLine="true"/>
            <slf:list name="<%=PABForm.CompDocList.NAME%>" codiceOrganizzazione="${sessionScope['###_NOME_RICERCA']}" />
            <slf:listNavBar name="<%=PABForm.CompDocList.NAME%>" />
        </sl:content>
        <sl:footer />
        <div id="anchorTipoDoc"></div>
    </sl:body>
</sl:html>
