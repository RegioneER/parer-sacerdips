<%@ page import="it.eng.dispenser.web.form.DynamicSpagoLiteForm"
	pageEncoding="UTF-8"%>
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
		<slf:messageBox />
		<sl:contentTitle title="Dettaglio" codiceMenu="${ricercheLoader.ricerche[sessionScope['###_NOME_RICERCA']].nmEntryMenu}" 
                                 codiceOrganizzazione="${sessionScope['###_NOME_RICERCA']}"/>
		<sl:newLine skipLine="true" />
		<slf:listNavBarDetail name="<%= DynamicSpagoLiteForm.RicercaList.NAME%>" />  
		<slf:fieldSet  borderHidden="true">
			<c:forEach
				items="${ricercheLoader.ricerche[sessionScope['###_NOME_RICERCA']].dipGruppoDettaglios}"
				var="gruppoDettaglio">
				<c:if test="${gruppoDettaglio.niColonnaGruppo == 0 }">
					<sl:newLine />
					<slf:section name="DETT_${gruppoDettaglio.nmGruppoDettaglio}"
						styleClass="importantContainer w100">
						<%@include file="section_f.jspf"%>
					</slf:section>
				</c:if>
				<c:if test="${gruppoDettaglio.niColonnaGruppo == 1 }">
					<sl:newLine />
						<slf:section name="DETT_${gruppoDettaglio.nmGruppoDettaglio}"
							styleClass="importantContainer containerLeft w50">
							<%@include file="section_h.jspf"%>
						</slf:section>
				</c:if>
				<c:if test="${gruppoDettaglio.niColonnaGruppo == 2 }">
					<slf:section name="DETT_${gruppoDettaglio.nmGruppoDettaglio}"
						styleClass="importantContainer containerRight w50">
						<%@include file="section_h.jspf"%>
					</slf:section>
				</c:if>


			</c:forEach>
			
			<input type="hidden" name="riga" value="" />
		</slf:fieldSet>		
		
		
	</sl:content>
	<sl:footer />
	<div id="anchorDettaglio"></div>
</sl:body>
</sl:html>
