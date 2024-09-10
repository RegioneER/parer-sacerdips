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

package it.eng.dispenser.helper;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import it.eng.dispenser.entity.StrutReferenziatePug;
import it.eng.dispenser.grantedEntity.DecTipoUnitaDoc;

/**
 *
 * @author iacolucci_m
 */
@SuppressWarnings("unchecked")
@Stateless(mappedName = "DispenserHelper")
@LocalBean
public class DispenserHelper extends GenericHelper {

    @PersistenceContext(unitName = "DispenserJPA")
    private EntityManager entityManager;

    public BigDecimal getStrutFittiziaByStrutReale(BigDecimal idStrutReale) {
        BigDecimal idStrutFittizia = null;
        Query q = entityManager
                .createQuery("SELECT u FROM StrutReferenziatePug u WHERE u.idStrutReale = :idStrutReale");
        q.setParameter("idStrutReale", idStrutReale);
        List<StrutReferenziatePug> l = q.getResultList();
        if (l != null && l.size() > 0) {
            StrutReferenziatePug strutReferenziatePug = l.iterator().next();
            idStrutFittizia = strutReferenziatePug.getIdStrutFittizia();
        }
        return idStrutFittizia;
    }

    public DecTipoUnitaDoc getDecTipoUnitaDocByStrutAndNome(BigDecimal idStrut, String nmTipoUnitaDoc) {
        DecTipoUnitaDoc decTipoUnitaDoc = null;
        Query q = entityManager.createQuery(
                "SELECT u FROM DecTipoUnitaDoc u WHERE u.idStrut = :idStrut AND u.nmTipoUnitaDoc=:nmTipoUnitaDoc");
        q.setParameter("idStrut", idStrut);
        q.setParameter("nmTipoUnitaDoc", nmTipoUnitaDoc);
        List<DecTipoUnitaDoc> l = q.getResultList();
        if (l != null && l.size() > 0) {
            decTipoUnitaDoc = l.iterator().next();
        }
        return decTipoUnitaDoc;
    }

    /*
     * Estrae tutte le UD (DISTINCT) della struttura passata aventi come valore dati specifici quello passato ma
     * escudendo la UD passata come parametro.
     */
    public Set<BigDecimal> findUdsByDlPrefissoValoreString(BigDecimal idStrut, String dlPrefissoValore,
            BigDecimal idUnitaDoc) {
        Query q = getEntityManager().createQuery("SELECT DISTINCT q.qryUdByVlMetaStrId.idUnitaDoc "
                + "FROM QryUdByVlMetaStr q, AroUnitaDoc ud WHERE q.qryUdByVlMetaStrId.idUnitaDoc=ud.idUnitaDoc "
                + "AND  ud.orgStrut.idStrut=:idStrut AND q.dlPrefissoValore=:dlPrefissoValore AND ud.idUnitaDoc<>:idUnitaDoc");
        q.setParameter("idStrut", idStrut.longValueExact());
        q.setParameter("idUnitaDoc", idUnitaDoc.longValueExact());
        q.setParameter("dlPrefissoValore", dlPrefissoValore);
        List<BigDecimal> l = q.getResultList();
        return new HashSet<>(l);
    }
}
