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

import java.sql.Timestamp;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.StringUtils;

import it.eng.dispenser.entity.DipLogJob;
import it.eng.dispenser.util.ejb.AppServerInstance;

/**
 * Helper per scrivere i log dei job su Dispenser.
 *
 * @author Moretti_Lu
 *
 */
@Stateless(mappedName = "JobHelper")
@LocalBean
public class JobHelper {

    @PersistenceContext(unitName = "DispenserJPA")
    private EntityManager em;

    @EJB
    private AppServerInstance appServerInstance;

    private DipLogJob writeLogJob(String jobName, String opType, String descr) {
        Date now = new Date();
        Timestamp date = new Timestamp(now.getTime());

        DipLogJob dipLogJob = new DipLogJob();
        dipLogJob.setDtEvento(date);
        dipLogJob.setNmJob(jobName);
        dipLogJob.setTiEvento(opType);
        dipLogJob.setCdIndServer(appServerInstance.getName());

        if (descr != null) {
            dipLogJob.setDsErrore(StringUtils.abbreviate(descr, 1024));
        }

        DipLogJob logJob = em.merge(dipLogJob);
        em.flush();
        return logJob;
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public DipLogJob writeAtomicLogJob(String jobName, String opType) {
        return writeLogJob(jobName, opType, null);
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public DipLogJob writeAtomicLogJob(String jobName, String opType, String descr) {
        return writeLogJob(jobName, opType, descr);
    }
}
