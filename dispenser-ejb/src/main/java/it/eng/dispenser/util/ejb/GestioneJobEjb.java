/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna
 * <p/>
 * This program is free software: you can redistribute it and/or modify it under the terms of
 * the GNU Affero General Public License as published by the Free Software Foundation,
 * either version 3 of the License, or (at your option) any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see <https://www.gnu.org/licenses/>.
 */

package it.eng.dispenser.util.ejb;

import it.eng.dispenser.entity.DipDecJob;
import it.eng.dispenser.entity.DecJobFoto;
import it.eng.dispenser.job.helper.GestioneJobHelper;
import it.eng.dispenser.slite.gen.form.GestioneJobForm;
import it.eng.dispenser.util.Constants;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.table.BaseTable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Gilioli_P
 */
@Stateless
@LocalBean
public class GestioneJobEjb {

    private static final Logger LOGGER = LoggerFactory.getLogger(GestioneJobEjb.class);
    private static final String STATO_JOB = "stato_job";
    private static final String NM_JOB = "nm_job";
    private static final String DS_JOB = "ds_job";
    private static final String DT_PROSSIMA_ATTIVAZIONE = "dt_prossima_attivazione";
    private static final String DT_ULTIMA_ESECUZIONE = "dt_ultima_esecuzione";
    private static final String OPERAZIONE_JOB_START = "operazione_job_start";
    private static final String OPERAZIONE_JOB_SINGLE = "operazione_job_single";
    private static final String OPERAZIONE_JOB_STOP = "operazione_job_stop";

    @EJB
    GestioneJobHelper helper;

    public void salvaFoto() {
        // Cancello tutti i record di DEC_JOB_FOTO
        helper.bulkDeleteDecJobFoto();
        // Copio tutti i record di DEC_JOB in DEC_JOB_FOTO
        helper.copyToFoto();
    }

    public void disabilitaAllJobs() {
        // Disattivo tutti i jobs
        helper.disabilitaAllJobs();
    }

    public boolean isDecJobFotoEmpty() {
        return helper.isDecJobFotoEmpty();
    }

    public boolean areAllJobsDisattivati() {
        return helper.areAllJobsDisattivati();
    }

    @SuppressWarnings("unchecked")
    public void ripristinaFotoGestioneJob() {
        // Ripristino solo lo stato, se i dati di schedulazione sono non nulli,
        // altrimenti non ripristino lo stato.
        List<DecJobFoto> jobFotoList = helper.getEntityManager().createNamedQuery("DecJobFoto.findAll").getResultList();
        for (DecJobFoto jobFoto : jobFotoList) {
            // Se il job nella foto risulta schedulato
            if (jobFoto.getDtProssimaAttivazioneFoto() != null) {
                DipDecJob job = helper.getEntityManager().find(DipDecJob.class, jobFoto.getIdJobFoto());
                // Ripristino lo stato di schedulato (ATTIVO) solo se nel frattempo non ho annullato
                // i parametri di schedulazione
                if (job.getCdSchedHour() != null) {
                    job.setTiStatoTimer(Constants.StatoTimer.ATTIVO.name());
                }
            }
        }
    }

    public BaseRow getInfoJobFotoRowBean() {
        // Recupero la data di ultima foto
        Date dataLastFoto = helper.getDataLastFotoJob();
        // Recupero il numero totale di job presenti nella foto
        int numJobFoto = helper.getNumJobFoto();
        // Recupero il numero di job attivi presenti nella foto
        int numJobAttivi = helper.getNumJobFotoAttivi();
        // 1) Recupero il numero di job "nuovi" in DEC_JOB (e non in foto)
        // 2) Recupero il numero di job in foto, eliminati da DEC_JOB
        int[] numJob = helper.getNumJobRimossiPresenti();
        int numJobNuovi = numJob[0];
        int numJobSoloFoto = numJob[1];
        BaseRow row = new BaseRow();
        row.setBigDecimal("ni_tot_job_presenti2", BigDecimal.valueOf(numJobFoto));
        row.setBigDecimal("ni_tot_job_attivi2", BigDecimal.valueOf(numJobAttivi));
        row.setBigDecimal("ni_tot_job_disattivi2", BigDecimal.valueOf((long) numJobFoto - numJobAttivi));
        row.setBigDecimal("ni_tot_job_nuovi2", BigDecimal.valueOf(numJobNuovi));
        row.setBigDecimal("ni_tot_job_solo_foto", BigDecimal.valueOf(numJobSoloFoto));
        row.setTimestamp("last_job_foto", new Timestamp(dataLastFoto.getTime()));
        return row;
    }

    public Object[] getInfoJobFotoNomiJobRowBean() {
        return helper.getNomiJobRimossiPresenti();
    }

    public BaseTable getAmbitoJob() {
        List<String> ambitoList = helper.getAmbitoJob();
        BaseTable ambitoTableBean = new BaseTable();
        try {
            for (String ambito : ambitoList) {
                BaseRow ambitoRowBean = new BaseRow();
                ambitoRowBean.setString("nm_ambito", ambito);
                ambitoTableBean.add(ambitoRowBean);
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return ambitoTableBean;
    }

    public BaseRow getInfoJobRowBean() {
        Object[] obj = helper.getInfoJob();
        Long numTot = (Long) obj[0];
        Long numTot2 = (Long) obj[1];
        BaseRow row = new BaseRow();
        row.setTimestamp("dt_stato_job", new Timestamp(new Date().getTime()));
        row.setBigDecimal("ni_tot_job_presenti", BigDecimal.valueOf(numTot));
        row.setBigDecimal("ni_tot_job_attivi", BigDecimal.valueOf(numTot2));
        row.setBigDecimal("ni_tot_job_disattivi", BigDecimal.valueOf(numTot - numTot2));
        return row;
    }

    public BaseTable getDecJobTableBean(GestioneJobForm.GestioneJobRicercaFiltri filtri) throws EMFError {
        BaseTable jobTableBean = new BaseTable();
        List<Object[]> listaJob = helper.getDecJobList(filtri.getNm_ambito().parse(), filtri.getDs_job().parse(),
                filtri.getTi_stato_job().parse());
        try {
            if (listaJob != null && !listaJob.isEmpty()) {
                for (Object[] job : listaJob) {
                    boolean attivo = false;
                    BaseRow row = new BaseRow();
                    row.setString(NM_JOB, ((String) job[0]));
                    row.setString(DS_JOB, (String) job[1]);
                    if (job[2] != null) {
                        row.setTimestamp(DT_PROSSIMA_ATTIVAZIONE, new Timestamp(((Date) job[2]).getTime()));
                        if ((String) job[9] == null || (((String) job[9]).equals(Constants.StatoTimer.ATTIVO.name())
                                || ((String) job[9]).equals(Constants.StatoTimer.DISATTIVO.name()))) {
                            attivo = true;
                        }
                    }
                    // Data ultima esecuzione
                    if (job[4] != null) {
                        row.setTimestamp(DT_ULTIMA_ESECUZIONE, new Timestamp(((Date) job[4]).getTime()));
                    }

                    if (attivo) {
                        row.setString(STATO_JOB, Constants.StatoTimer.ATTIVO.name());
                        row.setString(OPERAZIONE_JOB_START, "0");
                        row.setString(OPERAZIONE_JOB_SINGLE, "0");
                        row.setString(OPERAZIONE_JOB_STOP, "1");
                    } else if (job[5] != null && ((Character) job[5]).toString().equals("1")) {
                        row.setString(STATO_JOB, Constants.StatoTimer.IN_ESECUZIONE.name());
                        row.setString(OPERAZIONE_JOB_START, "0");
                        row.setString(OPERAZIONE_JOB_STOP, "0");
                        row.setString(OPERAZIONE_JOB_SINGLE, "0");
                    } else {
                        row.setString(STATO_JOB, Constants.StatoTimer.DISATTIVO.name());
                        row.setString(OPERAZIONE_JOB_STOP, "0");
                        row.setString(OPERAZIONE_JOB_SINGLE, "1");
                        row.setString(OPERAZIONE_JOB_START, "1");
                    }

                    if (((String) job[10]).equals("NO_TIMER")) {
                        row.setString(OPERAZIONE_JOB_START, "0");
                        row.setString(OPERAZIONE_JOB_STOP, "0");
                    }
                    row.setString("ti_sched_job", (String) job[10]);

                    // Ultima esecuzione OK
                    if (job[6] != null) {
                        row.setString("last_exec_ok", ((Character) job[6]).toString());
                    }

                    row.setString("job_selezionati", "0");

                    row.setString("nm_ambito", (String) job[7]);
                    row.setBigDecimal("ni_ord_exec", (BigDecimal) job[8]);

                    jobTableBean.add(row);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return jobTableBean;
    }

    public BaseTable getDecJobTableBeanPerAmm() {
        List<Object[]> listaJob = helper.getDecJobListPerAmm();
        return populateBaseTableJob(listaJob);
    }

    public BaseTable getDecJobFotoTableBeanPerAmm() {
        List<Object[]> listaJob = helper.getDecJobFotoListPerAmm();
        return populateBaseTableJobFoto(listaJob);
    }

    public BaseTable populateBaseTableJob(List<Object[]> listaJob) {
        BaseTable jobTableBean = new BaseTable();
        try {
            if (listaJob != null && !listaJob.isEmpty()) {
                for (Object[] job : listaJob) {
                    boolean attivo = false;
                    BaseRow row = new BaseRow();
                    row.setString(NM_JOB, ((String) job[0]));
                    row.setString(DS_JOB, (String) job[1]);
                    if (job[2] != null) {
                        row.setTimestamp(DT_PROSSIMA_ATTIVAZIONE, new Timestamp(((Date) job[2]).getTime()));
                        if ((String) job[6] == null || (((String) job[6]).equals(Constants.StatoTimer.ATTIVO.name())
                                || ((String) job[6]).equals(Constants.StatoTimer.DISATTIVO.name()))) {
                            attivo = true;
                        }
                    }
                    // Data ultima esecuzione
                    if (job[4] != null) {
                        row.setTimestamp(DT_ULTIMA_ESECUZIONE, new Timestamp(((Date) job[4]).getTime()));
                    }

                    if (attivo) {
                        row.setString(STATO_JOB, Constants.StatoTimer.ATTIVO.name());
                    } else if (job[5] != null && ((Character) job[5]).toString().equals("1")) {
                        row.setString(STATO_JOB, Constants.StatoTimer.IN_ESECUZIONE.name());

                    } else {
                        row.setString(STATO_JOB, Constants.StatoTimer.DISATTIVO.name());

                    }

                    jobTableBean.add(row);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return jobTableBean;
    }

    public BaseTable populateBaseTableJobFoto(List<Object[]> listaJob) {
        BaseTable jobTableBean = new BaseTable();
        try {
            if (listaJob != null && !listaJob.isEmpty()) {
                for (Object[] job : listaJob) {
                    boolean attivo = false;
                    BaseRow row = new BaseRow();
                    row.setString(NM_JOB, ((String) job[0]));
                    row.setString(DS_JOB, (String) job[1]);
                    if (job[2] != null) {
                        row.setTimestamp(DT_PROSSIMA_ATTIVAZIONE, new Timestamp(((Date) job[2]).getTime()));
                        if ((String) job[6] == null || (((String) job[6]).equals(Constants.StatoTimer.ATTIVO.name())
                                || ((String) job[6]).equals(Constants.StatoTimer.DISATTIVO.name()))) {
                            attivo = true;
                        }
                    }
                    // Data ultima esecuzione
                    if (job[4] != null) {
                        row.setTimestamp(DT_ULTIMA_ESECUZIONE, new Timestamp(((Date) job[4]).getTime()));
                    }

                    if (attivo) {
                        row.setString(STATO_JOB, Constants.StatoTimer.ATTIVO.name());

                    } else if (job[5] != null && ((Character) job[5]).toString().equals("1")) {
                        row.setString(STATO_JOB, Constants.StatoTimer.DISATTIVO.name());

                    } else {
                        row.setString(STATO_JOB, Constants.StatoTimer.DISATTIVO.name());

                    }

                    jobTableBean.add(row);
                }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }

        return jobTableBean;
    }

}
