<%@ page import="it.eng.dispenser.slite.gen.form.GestioneJobForm" pageEncoding="UTF-8"%>
<%@ include file="../../include.jsp"%>

<sl:html>
    <sl:head  title="Monitoraggio - Lista Schedulazioni Job" >

    </sl:head>
    <sl:body>
        <sl:header changeOrganizationBtnDescription="Cambia versatore"/>
        <sl:menu showChangePasswordBtn="true" />
        <sl:content>
            <slf:messageBox  />
            <sl:contentTitle title="Lista Schedulazioni Job"/>
            <c:if test="${sessionScope.backToRicercaJob != null}">
            <slf:fieldBarDetailTag name="<%=GestioneJobForm.FiltriJobSchedulati.NAME%>" hideBackButton="false"/> 
            <sl:newLine />
            </c:if> 
            <slf:fieldSet  borderHidden="false">
                <!-- piazzo i campi del filtro di ricerca -->
                <slf:lblField name="<%=GestioneJobForm.FiltriJobSchedulati.NM_JOB%>" colSpan="4"/>
                <sl:newLine />
                <slf:lblField name="<%=GestioneJobForm.FiltriJobSchedulati.DT_REG_LOG_JOB_DA%>" colSpan="1" controlWidth="w70" />                
                <slf:doubleLblField name="<%=GestioneJobForm.FiltriJobSchedulati.ORE_DT_REG_LOG_JOB_DA%>" name2="<%=GestioneJobForm.FiltriJobSchedulati.MINUTI_DT_REG_LOG_JOB_DA%>" controlWidth="w20" controlWidth2="w20" labelWidth="w5" colSpan="1" />
                <slf:lblField name="<%=GestioneJobForm.FiltriJobSchedulati.DT_REG_LOG_JOB_A%>" colSpan="1" controlWidth="w70"  />
                <slf:doubleLblField name="<%=GestioneJobForm.FiltriJobSchedulati.ORE_DT_REG_LOG_JOB_A%>" name2="<%=GestioneJobForm.FiltriJobSchedulati.MINUTI_DT_REG_LOG_JOB_A%>" controlWidth="w20" controlWidth2="w20" labelWidth="w5" colSpan="1" />
                <sl:newLine />
            </slf:fieldSet>
            <sl:newLine skipLine="true" />
            <sl:pulsantiera>
                <slf:lblField name="<%=GestioneJobForm.FiltriJobSchedulati.RICERCA_JOB_SCHEDULATI%>" colSpan="1" controlWidth="w70" />
                <slf:lblField name="<%=GestioneJobForm.FiltriJobSchedulati.PULISCI_JOB_SCHEDULATI%>" colSpan="1" controlWidth="w70" />    
            </sl:pulsantiera>
            <sl:newLine skipLine="true" />
            <slf:fieldSet  borderHidden="false">
                <!-- piazzo i campi del risultato -->
                <slf:lblField name="<%=GestioneJobForm.StatoJob.ATTIVO%>" width="w20" labelWidth="w70" controlWidth="w30"/>
                <slf:lblField name="<%=GestioneJobForm.StatoJob.DT_REG_LOG_JOB_INI%>" width="w20" labelWidth="w10" />
                <sl:newLine />
                <slf:lblField name="<%=GestioneJobForm.StatoJob.DT_PROSSIMA_ATTIVAZIONE%>" labelWidth="w40"  width = "w50" />
                <sl:newLine />
            </slf:fieldSet>
            <sl:newLine skipLine="true" />

            <!--  piazzo la lista con i risultati -->
            <slf:list   name="<%= GestioneJobForm.JobSchedulatiList.NAME%>" />
            <slf:listNavBar  name="<%= GestioneJobForm.JobSchedulatiList.NAME%>" />

        </sl:content>
        <sl:footer />
    </sl:body>
</sl:html>