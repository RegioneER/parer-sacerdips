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

package it.eng.dispenser.web.bo;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

import it.eng.dispenser.entity.DipParamApplic;
import it.eng.dispenser.slite.gen.form.GestioneJobForm;
import it.eng.dispenser.slite.gen.viewbean.LogVVisLastSchedRowBean;
import it.eng.dispenser.slite.gen.viewbean.MonVLisSchedJobRowBean;
import it.eng.dispenser.slite.gen.viewbean.MonVLisSchedJobTableBean;
import it.eng.dispenser.viewEntity.LogVVisLastSched;
import it.eng.dispenser.viewEntity.MonVLisSchedJob;
import it.eng.dispenser.util.Transform;
import it.eng.spagoCore.error.EMFError;
import java.util.Date;
import javax.persistence.TemporalType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component
public class JobBO {

    @PersistenceContext
    private EntityManager em;

    private Logger logger = LoggerFactory.getLogger(JobBO.class.getName());

    private static final String DATA_DA = "datada";
    private static final String DATA_A = "dataa";
    private static final String NM_JOB = "nmJob";

    /**
     * Restituisce un rowbean contenente le informazioni sull'ultima schedulazione di un determinato job
     *
     * @param nomeJob
     *            nome job
     *
     * @return entity bean {@link LogVVisLastSchedRowBean}
     */
    @SuppressWarnings("unchecked")
    public LogVVisLastSchedRowBean getLogVVisLastSched(String nomeJob) {
        String queryStr = "SELECT u FROM LogVVisLastSched u WHERE u.nmJob = '" + nomeJob + "'";
        Query query = em.createQuery(queryStr);
        List<LogVVisLastSched> listaLog = query.getResultList();
        LogVVisLastSchedRowBean logRowBean = new LogVVisLastSchedRowBean();
        try {
            if (listaLog != null && !listaLog.isEmpty()) {
                logRowBean = (LogVVisLastSchedRowBean) Transform.entity2RowBean(listaLog.get(0));
            }
        } catch (Exception e) {
            throw new IllegalStateException("Errore inaspettato nel record di ultima schedulazione job : "
                    + ExceptionUtils.getRootCauseMessage(e), e);
        }

        return logRowBean;
    }

    /**
     * Restituisce il nome dell'applicazione. Dovrebbe risiedere nello strato ejb ma al momento non esiste alcun config
     * helper per cui lo metto qui.
     *
     * @return nome dell'applicazione (SACER_DIPS)
     */
    public String getNomeApplicazione() {
        TypedQuery<DipParamApplic> query = em
                .createQuery("select d from DipParamApplic d where d.nmParamApplic= :nmApplic", DipParamApplic.class);
        query.setParameter("nmApplic", "NM_APPLIC");
        List<DipParamApplic> list = query.setMaxResults(1).getResultList();
        if (list.size() < 1) {
            return null;
        }
        return list.get(0).getDsValoreParamApplic();
    }

    /**
     * Metodo che restituisce un viewbean con i record trovati in base ai filtri di ricerca passati in ingresso
     *
     * @param filtriJS
     *            filtri job schedulati
     * @param dateValidate
     *            array con date da validare
     *
     * @return entity bean {@link MonVLisSchedJobTableBean}
     *
     * @throws EMFError
     *             errore generico
     */
    public MonVLisSchedJobTableBean getMonVLisSchedJobViewBean(GestioneJobForm.FiltriJobSchedulati filtriJS,
            Date[] dateValidate) throws EMFError {
        return getMonVLisSchedJobViewBean(dateValidate, filtriJS.getNm_job().parse());
    }

    /**
     * Metodo che restituisce un viewbean con i record trovati in base ai filtri di ricerca passati in ingresso
     *
     * @param dateValidate
     *            le date
     * @param nomeJob
     *            nome del job
     *
     * @return {@link MonVLisSchedJobTableBean} table bean per la UI
     */
    @SuppressWarnings("unchecked")
    public MonVLisSchedJobTableBean getMonVLisSchedJobViewBean(Date[] dateValidate, String nomeJob) {
        String whereWord = "WHERE ";
        StringBuilder queryStr = new StringBuilder("SELECT u FROM MonVLisSchedJob u ");
        // Inserimento nella query del filtro nome job
        if (nomeJob != null) {
            queryStr.append(whereWord).append("u.nmJob = :nmJob ");
            whereWord = "AND ";
        }
        Date dataOrarioDa = (dateValidate != null ? dateValidate[0] : null);
        Date dataOrarioA = (dateValidate != null ? dateValidate[1] : null);
        // Inserimento nella query del filtro data giÃ  impostato con data e ora
        if ((dataOrarioDa != null) && (dataOrarioA != null)) {
            queryStr.append(whereWord).append("(u.dtRegLogJobIni between :datada AND :dataa) ");
        }
        queryStr.append("ORDER BY u.dtRegLogJobIni DESC ");
        Query query = em.createQuery(queryStr.toString());

        if (nomeJob != null) {
            query.setParameter(NM_JOB, nomeJob);
        }

        if (dataOrarioDa != null && dataOrarioA != null) {
            query.setParameter(DATA_DA, dataOrarioDa, TemporalType.TIMESTAMP);
            query.setParameter(DATA_A, dataOrarioA, TemporalType.TIMESTAMP);
        }

        // eseguo la query e metto i risulati in una lista
        List<MonVLisSchedJob> listaSched = query.getResultList();
        MonVLisSchedJobTableBean schedTableBean = new MonVLisSchedJobTableBean();
        try {
            if (listaSched != null && !listaSched.isEmpty()) {
                schedTableBean = (MonVLisSchedJobTableBean) Transform.entities2TableBean(listaSched);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        // Creo un nuovo campo concatenandone altri giÃ  esistenti
        for (int i = 0; i < schedTableBean.size(); i++) {
            MonVLisSchedJobRowBean row = schedTableBean.getRow(i);
            if (row.getDtRegLogJobFine() != null) {
                String durata = row.getDurataGg() + "-" + row.getDurataOre() + ":" + row.getDurataMin() + ":"
                        + row.getDurataSec();
                row.setString("durata", durata);
            }
        }
        return schedTableBean;
    }

}
