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

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
//import org.apache.commons.collections.CollectionUtils;
//import org.apache.commons.collections.Predicate;
//import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import it.eng.dispenser.grantedEntity.SIAplApplic;
import it.eng.dispenser.grantedEntity.SIAplNotaRilascio;
import it.eng.dispenser.slite.gen.tablebean.SIAplApplicRowBean;
import it.eng.dispenser.slite.gen.tablebean.SIAplNotaRilascioRowBean;
import it.eng.dispenser.slite.gen.tablebean.SIAplNotaRilascioTableBean;
import it.eng.dispenser.util.Transform;
import it.eng.spagoCore.error.EMFError;

@Component
public class NoteRilascioBO {

    @PersistenceContext
    private EntityManager em;

    private Logger log = LoggerFactory.getLogger(NoteRilascioBO.class.getName());

    @SuppressWarnings("unchecked")
    @Transactional
    public SIAplNotaRilascioTableBean getAplNoteRilascioTableBean(String nmApplic) throws EMFError {
        SIAplNotaRilascioTableBean noteRilascioTableBean = new SIAplNotaRilascioTableBean();

        String queryStr = "SELECT applic FROM SIAplApplic applic WHERE applic.nmApplic = :nomeappl";
        Query q1 = em.createQuery(queryStr);
        q1.setParameter("nomeappl", nmApplic);
        SIAplApplic applic = (SIAplApplic) q1.getSingleResult();
        long idApplic = applic.getIdApplic();

        queryStr = "SELECT notaRilascio FROM SIAplNotaRilascio notaRilascio "
                + "WHERE notaRilascio.siAplApplic.idApplic = :idApplic " + "ORDER BY notaRilascio.dtVersione DESC";
        Query q2 = em.createQuery(queryStr);
        q2.setParameter("idApplic", idApplic);
        List<SIAplNotaRilascio> list = q2.getResultList();
        try {
            if (!list.isEmpty()) {
                for (SIAplNotaRilascio notaRilascio : list) {
                    SIAplNotaRilascioRowBean row = new SIAplNotaRilascioRowBean();
                    row = (SIAplNotaRilascioRowBean) Transform.entity2RowBean(notaRilascio);
                    row.setString("nm_applic", notaRilascio.getSiAplApplic().getNmApplic());
                    noteRilascioTableBean.add(row);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return noteRilascioTableBean;
    }

    public SIAplApplicRowBean getAplApplicRowBean(BigDecimal idApplic) {
        SIAplApplicRowBean applicRowBean = new SIAplApplicRowBean();
        SIAplApplic applic = em.find(SIAplApplic.class, idApplic.longValue());
        try {
            if (applic != null) {
                applicRowBean = (SIAplApplicRowBean) Transform.entity2RowBean(applic);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return applicRowBean;
    }

    @SuppressWarnings("unchecked")
    public SIAplNotaRilascioTableBean getAplNoteRilascioPrecTableBean(BigDecimal idApplic, BigDecimal idNotaRilascio,
            Date dtVersione) {
        SIAplNotaRilascioTableBean noteRilascioPrecTableBean = new SIAplNotaRilascioTableBean();

        String queryStr = "SELECT notaRilascio FROM SIAplNotaRilascio notaRilascio "
                + "JOIN notaRilascio.siAplApplic applic " + "WHERE notaRilascio.idNotaRilascio != :idNotaRilascio "
                + "AND applic.idApplic = :idApplic ";
        Query query = em.createQuery(queryStr);
        query.setParameter("idNotaRilascio", idNotaRilascio.longValue());
        query.setParameter("idApplic", idApplic.longValue());
        List<SIAplNotaRilascio> noteRilascioPrecList = query.getResultList();
        CollectionUtils.filter(noteRilascioPrecList, object -> (object).getDtVersione().compareTo(dtVersione) < 0);
        try {
            if (noteRilascioPrecList != null && !noteRilascioPrecList.isEmpty()) {
                noteRilascioPrecTableBean = (SIAplNotaRilascioTableBean) Transform
                        .entities2TableBean(noteRilascioPrecList);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return noteRilascioPrecTableBean;
    }

    @Transactional
    public SIAplNotaRilascioRowBean getAplNotaRilascioRowBean(BigDecimal idNotaRilascio) throws EMFError {
        SIAplNotaRilascioRowBean notaRilascioRowBean = new SIAplNotaRilascioRowBean();
        if (idNotaRilascio != null) {
            SIAplNotaRilascio notaRilascio = em.find(SIAplNotaRilascio.class, idNotaRilascio.longValue());
            if (notaRilascio != null) {
                try {
                    notaRilascioRowBean = (SIAplNotaRilascioRowBean) Transform.entity2RowBean(notaRilascio);
                    notaRilascioRowBean.setString("nm_applic", notaRilascio.getSiAplApplic().getNmApplic());
                } catch (Exception e) {
                    log.error("Errore durante il recupero della nota rilascio " + ExceptionUtils.getRootCauseMessage(e),
                            e);
                }
            }
        }
        return notaRilascioRowBean;
    }
}
