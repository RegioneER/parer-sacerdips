<%@ page import="it.eng.dispenser.web.form.DynamicSpagoLiteForm"
	pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>
<sl:html>
<sl:head title="${ricercheLoader.ricerche[sessionScope['###_NOME_RICERCA']].dsRicerca}" />
<script type="text/javascript" src="<c:url value="/js/dips-common.js"/>" ></script>
<script type="text/javascript" src="<c:url value="/js/dips-${sessionScope['###_NOME_RICERCA']}.js"/>" ></script>
<script type="text/javascript" src="<c:url value="/js/dips-startup.js"/>" ></script>
<sl:body>
	<sl:header  showChangeOrganizationBtn="false" />
	<sl:menu />
	<sl:content>
		<slf:messageBox />
		<sl:contentTitle title="${ricercheLoader.ricerche[sessionScope['###_NOME_RICERCA']].dsRicerca}" 
                    codiceMenu="${ricercheLoader.ricerche[sessionScope['###_NOME_RICERCA']].nmEntryMenu}" 
                    codiceOrganizzazione="${sessionScope['###_NOME_RICERCA']}"/>
		<sl:newLine skipLine="true" />
		<slf:fieldSet borderHidden="true">
			<c:forEach
				items="${ricercheLoader.ricerche[sessionScope['###_NOME_RICERCA']].dipGruppoCampiOrdinati}"
				var="gruppoCampo">
				<c:if test="${gruppoCampo.niColonnaGruppo == 0 }">
					<sl:newLine />
					<slf:section name="FILTR_${gruppoCampo.nmGruppo}"
						styleClass="importantContainer w100">
						<%@include file="section_f.jspf"%>
					</slf:section>
				</c:if>
				<c:if test="${gruppoCampo.niColonnaGruppo == 1 }">
					<sl:newLine />
						<slf:section name="FILTR_${gruppoCampo.nmGruppo}"
							styleClass="importantContainer containerLeft w50">
							<%@include file="section_h.jspf"%>
						</slf:section>
				</c:if>
				<c:if test="${gruppoCampo.niColonnaGruppo == 2 }">
					<slf:section name="FILTR_${gruppoCampo.nmGruppo}"
						styleClass="importantContainer containerRight w50">
						<%@include file="section_h.jspf"%>
					</slf:section>
				</c:if>
			</c:forEach>
		</slf:fieldSet>
		<sl:newLine skipLine="true"/>
		<sl:pulsantiera>
			<slf:lblField name="<%=DynamicSpagoLiteForm.FormRicerca.RICERCA%>" codiceOrganizzazione="${sessionScope['###_NOME_RICERCA']}" />
			<slf:lblField name="<%=DynamicSpagoLiteForm.FormRicerca.PULISCI%>" codiceOrganizzazione="${sessionScope['###_NOME_RICERCA']}" />
		</sl:pulsantiera>
		<sl:newLine skipLine="true"/>
		<slf:list  name="<%= DynamicSpagoLiteForm.RicercaList.NAME%>" codiceOrganizzazione="${sessionScope['###_NOME_RICERCA']}" />
        <slf:listNavBar  name="<%= DynamicSpagoLiteForm.RicercaList.NAME%>" />
	</sl:content>
	<sl:footer />
	<div id="anchorRicerca"></div>
</sl:body>
</sl:html>
