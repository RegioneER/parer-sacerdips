<%@page import="it.eng.dispenser.slite.gen.form.GestioneJobForm"%>
<%@ include file="../../include.jsp"%>


<sl:html>
	<sl:head title="Gestione job" />

	<sl:body>
		<sl:header showChangeOrganizationBtn="false" />

		<sl:menu />

		<sl:content>
			<slf:messageBox />
            <sl:newLine skipLine="true"/>
            <sl:contentTitle title="<%= GestioneJobForm.DESCRIPTION %>"/>

            <slf:fieldSet legend="Scarico dati per ricerche da Sacer" >
                <slf:lblField name="<%=GestioneJobForm.AllineaMetadatiJob.ATTIVO %>" colSpan="3" controlWidth="w20"/>
                <slf:lblField name="<%=GestioneJobForm.AllineaMetadatiJob.DT_START_JOB %>" colSpan="3" />
                <slf:lblField name="<%=GestioneJobForm.AllineaMetadatiJob.DT_NEXT_ACTIVATION %>" colSpan="3" />
                <sl:newLine />
                <sl:pulsantiera>
                    <slf:lblField name="<%=GestioneJobForm.AllineaMetadatiJob.START_ALLINEA_METADATI %>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.AllineaMetadatiJob.STOP_ALLINEA_METADATI %>" colSpan="2" />
                    <slf:lblField name="<%=GestioneJobForm.AllineaMetadatiJob.START_ONCE_ALLINEA_METADATI %>" colSpan="2" position="right"/>
                </sl:pulsantiera>
                <slf:lblField name ="<%=GestioneJobForm.AllineaMetadatiJob.FL_DATA_ACCURATA%>" colSpan="4"/>
            </slf:fieldSet>
			

		</sl:content>
		<sl:footer />
	</sl:body>
</sl:html>