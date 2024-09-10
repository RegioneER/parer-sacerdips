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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.dispenser.ws.rest.monitoraggio.ejb;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.dispenser.entity.DipDecJob;
import it.eng.dispenser.entity.DipLogJob;
import it.eng.dispenser.entity.constraint.ConstDipLogJob;
import it.eng.dispenser.util.Constants;
import it.eng.dispenser.ws.dto.RispostaControlli;
import it.eng.dispenser.ws.utils.MessaggiWSBundle;
import it.eng.parer.jboss.timer.service.JbossTimerEjb;

/**
 *
 * @author fioravanti_f
 */
@SuppressWarnings("unchecked")
@Stateless(mappedName = "ControlliMonitor")
@LocalBean
@TransactionAttribute(value = TransactionAttributeType.REQUIRED)
public class ControlliMonitor {

    @EJB(mappedName = "java:app/JbossTimerWrapper-ejb/JbossTimerEjb")
    private JbossTimerEjb timerManager;

    private static final Logger log = LoggerFactory.getLogger(ControlliMonitor.class.getName());

    @PersistenceContext(unitName = "DispenserJPA")
    private EntityManager entityManager;

    public RispostaControlli leggiUltimaChiamataWS() {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        List<Date> lstDate = null;

        try {
            String queryStr = "select max(lj.dtEvento) " + "from DipLogJob lj " + "where lj.nmJob = :nmJob "
                    + "and lj.tiEvento = :tiEvento ";

            javax.persistence.Query query = entityManager.createQuery(queryStr);
            query.setParameter("nmJob", Constants.JobEnum.WS_MONITORAGGIO_STATUS.name());
            query.setParameter("tiEvento", ConstDipLogJob.tiEvento.FINE_ESECUZIONE.name());
            lstDate = query.getResultList();
            if (lstDate != null && lstDate.size() > 0 && lstDate.get(0) != null) {
                rispostaControlli.setrDate(lstDate.get(0));
            } else {
                rispostaControlli.setrDate(this.sottraiUnGiorno(new Date()));
            }
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliMonitor.leggiUltimaChiamataWS " + e.getMessage()));
            log.error("Eccezione nella lettura della tabella dei log dei job", e);
        }
        return rispostaControlli;
    }

    public RispostaControlli leggiElencoJob() {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        List<DipDecJob> lstJobs = null;

        try {
            String queryStr = "select t from DipDecJob t " + "where t.tiSchedJob = 'STANDARD' "
                    + "or t.tiSchedJob = 'NO_TIMER'";
            javax.persistence.Query query = entityManager.createQuery(queryStr);
            lstJobs = query.getResultList();
            rispostaControlli.setrObject(lstJobs);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliMonitor.leggiElencoJob " + e.getMessage()));
            log.error("Eccezione nella lettura  della tabella dei job", e);
        }

        return rispostaControlli;
    }

    public RispostaControlli recNuovaEsecuzioneTimer(String jobName) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);

        try {
            Date tmpdaDate = timerManager.getDataProssimaAttivazione(jobName);

            rispostaControlli.setrDate(tmpdaDate);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliMonitor.recNuovaEsecuzioneTimer " + e.getMessage()));
            log.error("Eccezione nell'accesso al manager dei job", e);
        }

        return rispostaControlli;
    }

    public RispostaControlli leggiUltimaRegistrazione(String jobName) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        List<DipLogJob> lstJob;
        List<Date> lstDate = null;
        try {
            // leggo la data dell'ultimo inizio di attività del job
            String queryStr = "select max(lj.dtEvento) " + "from DipLogJob lj " + "where lj.nmJob = :nmJob "
                    + "and lj.tiEvento = :tiEvento ";

            javax.persistence.Query query = entityManager.createQuery(queryStr);
            query.setParameter("nmJob", jobName);
            query.setParameter("tiEvento", ConstDipLogJob.tiEvento.INIZIO_ESECUZIONE.name());
            lstDate = query.getResultList();
            if (lstDate != null && lstDate.size() > 0 && lstDate.get(0) != null) {
                Date dataInizio = lstDate.get(0);
                // cerco la data fine job (o errore nel job)
                // successiva o contemporanea all'inizio
                queryStr = "select j from DipLogJob j " + "where j.dtEvento >= :dataInizio "
                        + "and (j.tiEvento = :tiRegLogJob1 " + "or j.tiEvento = :tiRegLogJob2) "
                        + "and j.nmJob = :nmJob ";

                query = entityManager.createQuery(queryStr);
                query.setParameter("nmJob", jobName);
                query.setParameter("tiRegLogJob1", ConstDipLogJob.tiEvento.FINE_ESECUZIONE.name());
                query.setParameter("tiRegLogJob2", ConstDipLogJob.tiEvento.ERRORE.name());
                query.setParameter("dataInizio", dataInizio);
                lstJob = query.getResultList();
                if (lstJob != null && lstJob.size() > 0) {
                    // se l'ho trovata, rendo la data e il tipo di schedulazione
                    rispostaControlli.setrDate(lstJob.get(0).getDtEvento());
                    rispostaControlli.setrObject(ConstDipLogJob.tiEvento.valueOf(lstJob.get(0).getTiEvento()));
                } else {
                    // altrimenti, rendo la data dell'inizio schedulazione (il job è in corso)
                    rispostaControlli.setrDate(dataInizio);
                    rispostaControlli.setrObject(ConstDipLogJob.tiEvento.INIZIO_ESECUZIONE);
                }
            } else {
                // se il job non ha mai "girato", rendo una condizione
                // di job terminato correttamente, che non
                // produrrà allarmi in sede di valutazione del
                // monitoraggio
                rispostaControlli.setrDate(new Date(0));
                rispostaControlli.setrObject(ConstDipLogJob.tiEvento.FINE_ESECUZIONE);
            }
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliMonitor.leggiUltimaRegistrazione " + e.getMessage()));
            log.error("Eccezione nella lettura della tabella dei log dei job", e);
        }

        return rispostaControlli;
    }

    public RispostaControlli leggiAllarmiInIntervallo(String jobName, Date dataInizio, Date ultimaAttivitaDelJob) {
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);
        Long numAllarmi;

        try {
            String queryStr = "select count(j) from DipLogJob j " + "where j.dtEvento >= :dataInizio "
                    + "and j.dtEvento < :ultimaAttivita " + "and j.tiEvento = :tiEvento " + "and j.nmJob = :nmJob ";

            javax.persistence.Query query = entityManager.createQuery(queryStr);
            query.setParameter("nmJob", jobName);
            query.setParameter("tiEvento", ConstDipLogJob.tiEvento.ERRORE.name());
            query.setParameter("dataInizio", dataInizio);
            query.setParameter("ultimaAttivita", ultimaAttivitaDelJob);
            numAllarmi = (Long) query.getSingleResult();
            rispostaControlli.setrLong(numAllarmi);
            rispostaControlli.setrBoolean(true);
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione ControlliMonitor.leggiAllarmiInIntervallo " + e.getMessage()));
            log.error("Eccezione nella lettura della tabella dei log dei job ", e);
        }

        return rispostaControlli;
    }

    public boolean controllaStatoDbOracle() {
        boolean resp = false;
        try {
            String queryStr = "select 1 from dual";
            javax.persistence.Query query = entityManager.createNativeQuery(queryStr);
            BigDecimal r = (BigDecimal) query.getSingleResult();
            if (r.longValue() == 1L) {
                resp = true;
            }
        } catch (Exception e) {
            log.error("Problema nella connessione al db Oracle: ", e);
        }
        return resp;
    }

    public Date sottraiUnGiorno(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return cal.getTime();
    }
}
