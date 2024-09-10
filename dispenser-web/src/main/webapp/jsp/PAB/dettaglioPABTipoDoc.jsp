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
