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
package it.eng.dispenser.ws.rest.ejb;

import it.eng.dispenser.entity.IamUser;
import it.eng.dispenser.ws.dto.IWSDesc;
import it.eng.dispenser.ws.dto.RispostaControlli;
import it.eng.dispenser.ws.utils.MessaggiWSBundle;
import it.eng.spagoLite.security.User;
import it.eng.spagoLite.security.auth.PwdUtil;
import java.util.Date;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Stateless(mappedName = "ControlliRestWS")
@LocalBean
@TransactionAttribute(value = TransactionAttributeType.REQUIRES_NEW)
public class ControlliRestWS {

    private static final Logger log = LoggerFactory.getLogger(ControlliRestWS.class.getName());

    @PersistenceContext(unitName = "DispenserJPA")
    private EntityManager entityManager;

    public RispostaControlli checkCredenzialiEAuth(String loginName, String password, String indirizzoIP,
            IWSDesc descrizione) {
        User utente = null;
        String LOGIN_FALLITO_MSG = "Username e/o password errate/a";
        RispostaControlli rispostaControlli;
        rispostaControlli = new RispostaControlli();
        rispostaControlli.setrBoolean(false);

        log.info("Indirizzo IP del chiamante: {}", indirizzoIP);

        if (loginName == null || loginName.isEmpty()) {
            rispostaControlli.setCodErr(MessaggiWSBundle.MON_AUTH_001);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.MON_AUTH_001));
            return rispostaControlli;
        }

        try {
            IamUser iamUser;
            String queryStr = "select iu from IamUser iu where iu.nmUserid = :nmUseridIn";
            javax.persistence.Query query = entityManager.createQuery(queryStr, IamUser.class);
            query.setParameter("nmUseridIn", loginName);
            iamUser = (IamUser) query.getSingleResult();
            byte[] salt = PwdUtil.decodeUFT8Base64String(iamUser.getCdSalt());
            String pwd = PwdUtil.encodePBKDF2Password(salt, password);
            if (pwd.equals(iamUser.getCdPsw())) {
                Date ora = new Date();
                if (ora.after(iamUser.getDtScadPsw())) {
                    // utente scaduto
                    rispostaControlli.setCodErr(MessaggiWSBundle.MON_AUTH_002);
                    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.MON_AUTH_002, loginName));
                } else if (iamUser.getFlAttivo() == null || !iamUser.getFlAttivo().equals("1")) {
                    // utente non attivo
                    rispostaControlli.setCodErr(MessaggiWSBundle.MON_AUTH_003);
                    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.MON_AUTH_003, loginName));
                } else if (iamUser.getTipoUser() == null || !iamUser.getTipoUser().equals("AUTOMA")) {
                    // utente non autorizzato
                    rispostaControlli.setCodErr(MessaggiWSBundle.MON_AUTH_004);
                    rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.MON_AUTH_004, loginName,
                            descrizione.getNomeWs()));
                } else {
                    utente = new User();
                    utente.setUsername(loginName);
                    utente.setIdUtente(iamUser.getIdUserIam());
                    rispostaControlli.setrObject(utente);
                    rispostaControlli.setrBoolean(true);
                }
            } else {
                // login fallito, password errata
                rispostaControlli.setCodErr(MessaggiWSBundle.MON_AUTH_005);
                rispostaControlli
                        .setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.MON_AUTH_005, LOGIN_FALLITO_MSG));
            }
        } catch (NoResultException e) {
            // login fallito, utente non esistente
            rispostaControlli.setCodErr(MessaggiWSBundle.MON_AUTH_005);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.MON_AUTH_005, LOGIN_FALLITO_MSG));
            return rispostaControlli;
        } catch (Exception e) {
            rispostaControlli.setCodErr(MessaggiWSBundle.ERR_666);
            rispostaControlli.setDsErr(MessaggiWSBundle.getString(MessaggiWSBundle.ERR_666,
                    "Eccezione nella fase di autenticazione del EJB " + e.getMessage()));
            log.error("Eccezione nella fase di autenticazione del EJB ", e);
        }

        return rispostaControlli;
    }
}
