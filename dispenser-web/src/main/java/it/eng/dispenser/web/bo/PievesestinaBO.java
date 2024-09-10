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

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.eng.dispenser.bean.RecuperoWSBean;

@Service
@Transactional
public class PievesestinaBO {

    @PersistenceContext
    private EntityManager em;

    public RecuperoWSBean getUdData(BigDecimal idStrut, BigDecimal idUnitaDoc) {
        Query q = em.createQuery("SELECT NEW it.eng.dispenser.bean.RecuperoWSBean("
                + "ambiente.nmAmbiente, ente.nmEnte, strut.nmStrut, ud.cdRegistroKeyUnitaDoc, ud.aaKeyUnitaDoc, ud.cdKeyUnitaDoc"
                + ") FROM AroUnitaDoc ud JOIN ud.orgStrut strut JOIN strut.orgEnte ente "
                + "JOIN ente.orgAmbiente ambiente WHERE ud.orgStrut.idStrut = :idStrut AND ud.idUnitaDoc = :idUnitaDoc");
        q.setParameter("idStrut", idStrut.longValue());
        q.setParameter("idUnitaDoc", idUnitaDoc.longValue());

        RecuperoWSBean obj = (RecuperoWSBean) q.getSingleResult();

        return obj;
    }
}
