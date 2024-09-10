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

import it.eng.dispenser.grantedEntity.SIAplApplic;
import it.eng.dispenser.grantedEntity.SLLogLoginUser;
import it.eng.dispenser.grantedEntity.UsrUser;
import it.eng.spagoLite.security.User;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author fioravanti_f
 */
@SuppressWarnings("unchecked")
@Stateless(mappedName = "LoginLogHelper")
@LocalBean
public class LoginLogHelper {

    private static final Logger log = LoggerFactory.getLogger(LoginLogHelper.class);
    @PersistenceContext(unitName = "DispenserJPA")
    private EntityManager entityManager;
    @EJB
    private AppServerInstance appServerInstance;

    public enum TipiEvento {
        LOGIN, LOGOUT
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void writeLogEvento(User user, String indIpClient, TipiEvento tipoEvento) {

        try {
            SIAplApplic tmpAplApplic;
            String queryStr = "select t from SIAplApplic t " + "where t.nmApplic = :nmApplic ";
            javax.persistence.Query query = entityManager.createQuery(queryStr, SIAplApplic.class);
            query.setParameter("nmApplic", "SACER_DIPS");
            tmpAplApplic = (SIAplApplic) query.getSingleResult();

            String localServerName = appServerInstance.getName();

            SLLogLoginUser tmpLLogLoginUser = new SLLogLoginUser();
            tmpLLogLoginUser.setsIAplApplic(tmpAplApplic);
            tmpLLogLoginUser.setNmUserid(user.getUsername());
            tmpLLogLoginUser.setCdIndIpClient(indIpClient);
            tmpLLogLoginUser.setCdIndServer(localServerName);
            tmpLLogLoginUser.setDtEvento(new Date());
            tmpLLogLoginUser.setTipoEvento(tipoEvento.name());
            // Modifica per lo SPID
            if (user.getUserType() != null) {
                tmpLLogLoginUser.setTipoUtenteAuth(user.getUserType().name());
                tmpLLogLoginUser.setCdIdEsterno(user.getExternalId());
            }
            // ---

            entityManager.persist(tmpLLogLoginUser);
            entityManager.flush();

        } catch (Exception e) {
            log.error("Eccezione nel log dell'evento login/logout (writeLogEvento) ", e);
            throw new RuntimeException(e);
        }
    }

    public UsrUser findUser(String username) {
        Query q = entityManager.createQuery("SELECT u FROM UsrUser u WHERE u.nmUserid = :username");
        q.setParameter("username", username);
        return (UsrUser) q.getSingleResult();
    }

    /* Introdotta per lo SPID **/
    public List<UsrUser> findByCodiceFiscale(String codiceFiscale) throws NoResultException {
        Query q = entityManager.createQuery(
                "SELECT u FROM UsrUser u WHERE (u.cdFisc = :codiceFiscaleL OR u.cdFisc = :codiceFiscaleU) AND u.flAttivo='1'");
        q.setParameter("codiceFiscaleL", codiceFiscale.toLowerCase());
        q.setParameter("codiceFiscaleU", codiceFiscale.toUpperCase());
        return q.getResultList();
    }

    /*
     * Introdotto per l'itegrazione con SPID Puglia dove a fronte del codice fiscale arrivato da SPID andiamo a cercare
     * sulla usruser un utente avente come username il codice fiscale ignorando il case.
     */
    public List<UsrUser> findUtentiPerUsernameCaseInsensitive(String username) {
        Query q = entityManager
                .createQuery("SELECT u FROM UsrUser u WHERE lower(u.nmUserid) = :username  AND u.flAttivo='1'");
        q.setParameter("username", username.toLowerCase());
        return q.getResultList();
    }

}
