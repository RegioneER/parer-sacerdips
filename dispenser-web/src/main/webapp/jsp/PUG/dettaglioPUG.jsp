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