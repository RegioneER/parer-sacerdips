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
