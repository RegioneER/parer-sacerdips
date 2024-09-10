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

package it.eng.dispenser.job.helper;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.dispenser.entity.DecJobFoto;
import it.eng.dispenser.entity.DipDecJob;
import it.eng.dispenser.helper.GenericHelper;
import it.eng.dispenser.slite.gen.viewbean.LogVVisLastSchedRowBean;
import it.eng.dispenser.util.Transform;
import it.eng.dispenser.viewEntity.LogVVisLastSched;
import it.eng.spagoCore.error.EMFError;

@SuppressWarnings("unchecked")
@Stateless
@LocalBean
public class GestioneJobHelper extends GenericHelper {

    public GestioneJobHelper() {
    }

    @PersistenceContext
    private EntityManager entityManager;

    private static final Logger log = LoggerFactory.getLogger(GestioneJobHelper.class);
    public static final String STATO_IN_ESECUZIONE = "IN_ESECUZIONE";

    /**
     * Restituisce un rowbean contenente le informazioni sull'ultima schedulazione di un determinato job
     *
     * @param nmJob
     *            nome job
     *
     * @return row bean {@link LogVVisLastSchedRowBean}
     *
     */
    public LogVVisLastSchedRowBean getLogVVisLastSched(String nmJob) {
        String queryStr = "SELECT u FROM LogVVisLastSched u WHERE u.nmJob = :nmJob";
        Query query = entityManager.createQuery(queryStr);
        query.setParameter("nmJob", nmJob);
        List<LogVVisLastSched> listaLog = query.getResultList();
        LogVVisLastSchedRowBean logRowBean = new LogVVisLastSchedRowBean();
        try {
            if (listaLog != null && !listaLog.isEmpty()) {
                logRowBean = (LogVVisLastSchedRowBean) Transform.entity2RowBean(listaLog.get(0));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }

        return logRowBean;
    }

    public void bulkDeleteDecJobFoto() {
        String queryStr = "DELETE FROM DecJobFoto jobFoto ";
        Query query = getEntityManager().createQuery(queryStr);
        query.executeUpdate();
    }

    public void copyToFoto() {
        String queryStr = "SELECT job FROM DipDecJob job WHERE job.dsJob IS NOT NULL "
                + "AND (job.tiStatoTimer IN ('ATTIVO', 'INATTIVO') OR job.tiStatoTimer IS NULL) ";
        Query query = getEntityManager().createQuery(queryStr);
        List<DipDecJob> jobList = query.getResultList();

        Calendar c = Calendar.getInstance();
        Date dataOdierna = c.getTime();

        for (DipDecJob job : jobList) {
            DecJobFoto jobFoto = new DecJobFoto();
            jobFoto.setIdJobFoto(job.getIdJob());
            jobFoto.setCdSchedDayofmonthFoto(job.getCdSchedDayofmonth());
            jobFoto.setCdSchedDayofweekFoto(job.getCdSchedDayofweek());
            jobFoto.setCdSchedHourFoto(job.getCdSchedHour());
            jobFoto.setCdSchedMinuteFoto(job.getCdSchedMinute());
            jobFoto.setCdSchedMonthFoto(job.getCdSchedMonth());
            jobFoto.setDsJobFoto(job.getDsJob());
            jobFoto.setDtProssimaAttivazioneFoto(job.getDtProssimaAttivazione());
            jobFoto.setFlDataAccurataFoto(job.getFlDataAccurata());
            jobFoto.setNmJobFoto(job.getNmJob());
            jobFoto.setNmNodoAssegnatoFoto(job.getNmNodoAssegnato());
            jobFoto.setTiSchedJobFoto(job.getTiSchedJob());
            jobFoto.setTiScopoJobFoto(job.getTiScopoJob());
            jobFoto.setTiStatoTimerFoto(job.getTiStatoTimer());
            jobFoto.setDtJobFoto(dataOdierna);
            jobFoto.setNmAmbitoFoto(job.getNmAmbito());
            jobFoto.setNiOrdExecFoto(job.getNiOrdExec());
            getEntityManager().persist(jobFoto);
        }

    }

    public void disabilitaAllJobs() {
        String queryStr = "UPDATE DipDecJob job SET job.tiStatoTimer = 'INATTIVO', "
                + "job.dtProssimaAttivazione = NULL, " + "job.flDataAccurata = NULL " + "WHERE job.dsJob IS NOT NULL ";
        Query query = getEntityManager().createQuery(queryStr);
        query.executeUpdate();
    }

    public boolean isDecJobFotoEmpty() {
        String queryStr = "SELECT COUNT(jobFoto) FROM DecJobFoto jobFoto ";
        Query query = getEntityManager().createQuery(queryStr);
        return (Long) query.getSingleResult() == 0L;
    }

    public boolean areAllJobsDisattivati() {
        String queryStr = "SELECT COUNT(job) FROM DipDecJob job WHERE job.dsJob IS NOT NULL ";
        Query query = getEntityManager().createQuery(queryStr);
        Long numJob = (Long) query.getSingleResult();

        String queryStr2 = "SELECT COUNT(job) FROM DipDecJob job WHERE job.dtProssimaAttivazione IS NULL AND job.dsJob IS NOT NULL ";
        Query query2 = getEntityManager().createQuery(queryStr2);
        Long numJobFoto = (Long) query2.getSingleResult();

        return Objects.equals(numJob, numJobFoto);
    }

    public Date getDataLastFotoJob() {
        String queryStr = "SELECT jobFoto.dtJobFoto FROM DecJobFoto jobFoto ";
        Query query = getEntityManager().createQuery(queryStr);
        return (Date) query.getResultList().get(0);
    }

    public int getNumJobFoto() {
        String queryStr = "SELECT count(u) from DecJobFoto u WHERE u.dsJobFoto IS NOT NULL ";
        Query query = getEntityManager().createQuery(queryStr);
        return ((Long) query.getSingleResult()).intValue();
    }

    public int getNumJobFotoAttivi() {
        String queryStr = "SELECT count(u) from DecJobFoto u WHERE u.dtProssimaAttivazioneFoto IS NOT NULL ";
        Query query = getEntityManager().createQuery(queryStr);
        return ((Long) query.getSingleResult()).intValue();
    }

    public int[] getNumJobRimossiPresenti() {
        // Attenzione: non basta fare la differenza, perchè potrei aver rimosso un job
        // e aggiunto un altro nuovo, ma il rimosso deve figurare! Quindi li devo confrontare per nome
        String queryStr = "SELECT u.nmJob from DipDecJob u WHERE u.dsJob IS NOT NULL ";
        Query query = getEntityManager().createQuery(queryStr);
        List<String> jobList = query.getResultList();

        String queryStr2 = "SELECT u.nmJobFoto from DecJobFoto u ";
        Query query2 = getEntityManager().createQuery(queryStr2);
        List<String> jobFotoList = query2.getResultList();

        Set<String> jobSet = new HashSet<>(jobList);
        Set<String> jobFotoSet = new HashSet<>(jobFotoList);
        // Job non presenti nella foto, ma in DIP_DEC_JOB
        jobSet.removeAll(jobFotoSet);

        Set<String> jobSet2 = new HashSet<>(jobList);
        Set<String> jobFotoSet2 = new HashSet<>(jobFotoList);
        // Job presenti nella foto, ma NON in DIP_DEC_JOB
        jobFotoSet2.removeAll(jobSet2);

        int[] numJob = new int[2];
        numJob[0] = jobSet.size();
        numJob[1] = jobFotoSet2.size();

        return numJob;

    }

    public Object[] getNomiJobRimossiPresenti() {
        // Attenzione: non basta fare la differenza, perchè potrei aver rimosso un job
        // e aggiunto un altro nuovo, ma il rimosso deve figurare! Quindi li devo confrontare per nome
        String queryStr = "SELECT u.nmJob from DipDecJob u WHERE u.dsJob IS NOT NULL ";
        Query query = getEntityManager().createQuery(queryStr);
        List<String> jobList = query.getResultList();

        String queryStr2 = "SELECT u.nmJobFoto from DecJobFoto u WHERE u.dsJobFoto IS NOT NULL ";
        Query query2 = getEntityManager().createQuery(queryStr2);
        List<String> jobFotoList = query2.getResultList();

        Set<String> jobSet = new HashSet<>(jobList);
        Set<String> jobFotoSet = new HashSet<>(jobFotoList);
        // Job non presenti nella foto, ma in DIP_DEC_JOB
        jobSet.removeAll(jobFotoSet);

        Set<String> jobSet2 = new HashSet<>(jobList);
        Set<String> jobFotoSet2 = new HashSet<>(jobFotoList);
        // Job presenti nella foto, ma NON in DIP_DEC_JOB
        jobFotoSet2.removeAll(jobSet2);

        Object[] nmJob = new Object[2];
        nmJob[0] = jobSet;
        nmJob[1] = jobFotoSet2;

        return nmJob;

    }

    public List<String> getAmbitoJob() {
        String queryStr = "SELECT DISTINCT u.nmAmbito from DipDecJob u " + "ORDER BY u.nmAmbito";
        Query query = getEntityManager().createQuery(queryStr);
        return query.getResultList();

    }

    public Object[] getInfoJob() {
        String queryStr = "SELECT count(u) from DipDecJob u WHERE u.dsJob IS NOT NULL ";
        Query query = getEntityManager().createQuery(queryStr);

        Long numTot = (Long) query.getSingleResult();

        String queryStr2 = "SELECT count(u) from DipDecJob u WHERE u.dtProssimaAttivazione IS NOT NULL AND u.dsJob IS NOT NULL "
                + "AND (u.tiStatoTimer IN ('ATTIVO', 'INATTIVO') OR u.tiStatoTimer IS NULL) ";
        Query query2 = getEntityManager().createQuery(queryStr2);

        Long numTot2 = (Long) query2.getSingleResult();

        Object[] obj = new Object[2];
        obj[0] = numTot;
        obj[1] = numTot2;

        return obj;
    }

    public List<Object[]> getDecJobList(String nmAmbito, String dsJob, List<String> tiStatoList) throws EMFError {
        String whereWord = "AND ";
        StringBuilder queryStr = new StringBuilder(
                "SELECT u.nm_job, u.ds_Job, u.dt_Prossima_Attivazione, lastSched.nm_job as nm_job_last, lastSched.dt_evento_ini, lastSched.fl_job_attivo, lastSched.last_exec_ok, u.nm_ambito, u.ni_ord_exec, u.ti_stato_timer, u.ti_sched_job FROM Dip_Dec_Job u LEFT JOIN Log_V_Vis_Last_Sched lastSched "
                        + "ON(u.nm_job = lastSched.nm_job) " + "WHERE u.ds_Job IS NOT NULL ");

        if (StringUtils.isNotBlank(nmAmbito)) {
            queryStr.append(whereWord).append("u.nm_ambito = ?1 ");
            whereWord = "AND ";
        }

        if (StringUtils.isNotBlank(dsJob)) {
            queryStr.append(whereWord).append("UPPER (u.ds_Job) LIKE ?2 ");
            whereWord = "AND ";
        }

        if (tiStatoList != null && !tiStatoList.isEmpty()) {
            if (tiStatoList.contains("ATTIVO") && !tiStatoList.contains("DISATTIVO")) {
                queryStr.append(whereWord).append(
                        "u.dt_Prossima_Attivazione IS NOT NULL AND (u.ti_Stato_Timer IN ('ATTIVO') OR u.ti_Stato_Timer IS NULL) ");
                if (tiStatoList.contains(STATO_IN_ESECUZIONE)) {
                    queryStr.append("OR lastSched.fl_Job_Attivo = '1' ");
                }
            } else if (!tiStatoList.contains("ATTIVO") && tiStatoList.contains("DISATTIVO")) {
                queryStr.append(whereWord)
                        .append("(u.dt_Prossima_Attivazione IS NULL OR u.ti_Stato_Timer IN ('ESECUZIONE_SINGOLA')) ");
                if (tiStatoList.contains(STATO_IN_ESECUZIONE)) {
                    queryStr.append("OR lastSched.fl_Job_Attivo = '1' ");
                }
            } else if (tiStatoList.contains(STATO_IN_ESECUZIONE)) {
                queryStr.append("AND lastSched.fl_Job_Attivo = '1' ");
            }
        }
        queryStr.append("ORDER BY u.nm_ambito, u.ni_ord_exec, u.nm_job ");

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createNativeQuery(queryStr.toString());

        if (StringUtils.isNotBlank(nmAmbito)) {
            query.setParameter(1, nmAmbito);
        }

        if (StringUtils.isNotBlank(dsJob)) {
            query.setParameter(2, "%" + dsJob.toUpperCase() + "%");
        }

        return query.getResultList();
    }

    public List<Object[]> getDecJobListPerAmm() {
        String queryStr = "SELECT u.nm_job, u.ds_Job, u.dt_Prossima_Attivazione, lastSched.nm_job as nm_job_last, lastSched.dt_evento_ini, lastSched.fl_job_attivo, u.ti_stato_timer FROM Dip_Dec_Job u LEFT JOIN Log_V_Vis_Last_Sched lastSched "
                + "ON(u.nm_job = lastSched.nm_job) WHERE u.ds_Job IS NOT NULL ORDER BY u.nm_job ";

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createNativeQuery(queryStr);

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        return query.getResultList();

    }

    public List<Object[]> getDecJobFotoListPerAmm() {
        String queryStr = "SELECT u.nm_job_foto, u.ds_Job_foto, u.dt_Prossima_Attivazione_foto, lastSched.nm_job, lastSched.dt_evento_ini, lastSched.fl_job_attivo, u.ti_stato_timer_foto FROM Dec_Job_Foto u LEFT JOIN Log_V_Vis_Last_Sched lastSched "
                + "ON(u.nm_job_foto = lastSched.nm_job) WHERE u.ds_Job_foto IS NOT NULL ORDER BY u.nm_job_foto ";

        // CREO LA QUERY ATTRAVERSO L'ENTITY MANAGER
        Query query = getEntityManager().createNativeQuery(queryStr);

        // ESEGUO LA QUERY E PIAZZO I RISULTATI IN UNA LISTA
        return query.getResultList();

    }
}
