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

<%@ page import="it.eng.dispenser.web.form.DynamicSpagoLiteForm" pageEncoding="UTF-8"%>
<%@ page import="it.eng.dispenser.slite.gen.form.PUGForm" %>
<%@ include file="../../include.jsp"%>

<sl:html>
<sl:head title="${ricercheLoader.ricerche[sessionScope['###_NOME_RICERCA']].dsRicerca}" />
<script type="text/javascript" src="<c:url value="/js/dips-common.js"/>" ></script>
<script type="text/javascript" src="<c:url value="/js/dips-${sessionScope['###_NOME_RICERCA']}.js"/>" ></script>
<script type="text/javascript" src="<c:url value="/js/dips-startup.js"/>" ></script>
<sl:body>
	<sl:header showChangeOrganizationBtn="false" />
	<sl:menu />
	<sl:content>
            <!-- Bloco navigazione e dettagli comuni -->
            <%@include file="../dettaglio/sezioneDettaglioComune.jspf"%>

            <slf:lblField name="<%=PUGForm.PugButtonList.SCARICA_STRUMENTO%>" codiceOrganizzazione="${sessionScope['###_NOME_RICERCA']}" colSpan="2"/>
            <sl:newLine skipLine="true"/>
            <!--  piazzo le liste con i risultati -->
            <slf:section name="<%=PUGForm.ListaDocumentiSection.NAME%>" styleClass="noborder w100">
                <slf:listNavBar name="<%= PUGForm.DocumentiUDList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%=PUGForm.DocumentiUDList.NAME%>" codiceOrganizzazione="${sessionScope['###_NOME_RICERCA']}" />
                <slf:listNavBar  name="<%= PUGForm.DocumentiUDList.NAME%>" />
            </slf:section>
            <sl:newLine skipLine="true"/>
            <slf:section name="<%=PUGForm.ListaStrumentiCollegatiSection.NAME%>" styleClass="noborder w100">
                <slf:listNavBar name="<%= PUGForm.StrumentiCollegatiList.NAME%>" pageSizeRelated="true"/>
                <slf:list name="<%=PUGForm.StrumentiCollegatiList.NAME%>" codiceOrganizzazione="${sessionScope['###_NOME_RICERCA']}" />
                <slf:listNavBar  name="<%= PUGForm.StrumentiCollegatiList.NAME%>" />
            </slf:section>
	</sl:content>
	<sl:footer />
	<div id="anchorDettaglio"></div>
</sl:body>
</sl:html>
