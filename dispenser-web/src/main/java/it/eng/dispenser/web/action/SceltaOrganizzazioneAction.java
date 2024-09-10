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

package it.eng.dispenser.web.action;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import it.eng.dispenser.entity.constraint.ConstDipParamApplic;
import it.eng.dispenser.grantedEntity.UsrUser;
import it.eng.dispenser.slite.gen.Application;
import it.eng.dispenser.util.ejb.LoginLogHelper;
import it.eng.dispenser.web.security.DispenserAuthenticator;
import it.eng.dispenser.web.util.AuditSessionListener;
import it.eng.parer.dispenser.util.DataSourcePropertiesFactoryBean;
import it.eng.parer.sacerlog.ejb.helper.SacerLogHelper;
import it.eng.parer.sacerlog.entity.constraint.ConstLogEventoLoginUser;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.actions.ActionBase;
import it.eng.spagoLite.security.User;
import it.eng.spagoLite.security.auth.PwdUtil;
import it.eng.util.EncryptionUtil;

public class SceltaOrganizzazioneAction extends ActionBase {

    private static final Logger log = LoggerFactory.getLogger(SceltaOrganizzazioneAction.class);

    @Autowired
    private DispenserAuthenticator authenticator;

    @EJB(mappedName = "java:app/Dispenser-ejb/LoginLogHelper")
    private LoginLogHelper loginLogHelper;
    @Autowired
    private DataSourcePropertiesFactoryBean applicationProperties;
    @EJB(mappedName = "java:app/sacerlog-ejb/SacerLogHelper")
    private SacerLogHelper sacerLogHelper;

    @Override
    public String getControllerName() {
        return Application.Actions.SCELTA_ORGANIZZAZIONE;
    }

    @Override
    protected String getDefaultPublsherName() {
        return null;
    }

    @Override
    public void process() throws EMFError {
        // loggo, se necessario, l'avvenuto login nell'applicativo
        User utente = (User) getUser();
        // MEV#23905 - Associazione utente SPID con anagrafica utenti
        if (utente.isUtenteDaAssociare()) {
            gestisciUtenteDaAssociare(utente);
        } else {
            // loggo, se necessario, l'avvenuto login nell'applicativo
            this.loginLogger(utente);
            try {
                authenticator.recuperoAutorizzazioni(getSession());
            } catch (Exception ex) {
                log.error("Errore durante il recupero delle autorizzazioni", ex);
                getMessageBox().addError("Errore durante il recupero delle autorizzazioni: " + ex.getMessage());
            }
            redirectToAction(it.eng.dispenser.slite.gen.Application.Actions.HOME + "?clearhistory=true");
        }
    }

    @Override
    protected boolean isAuthorized(String destination) {
        return true;
    }

    @Override
    public void reloadAfterGoBack(String publisherName) {
        //
    }

    private void loginLogger(User utente) {
        if (utente.getConfigurazione() == null && utente.getOrganizzazioneMap() == null) {
            // Ã¨ un login iniziale e non un ritorno sulla form per un cambio struttura.
            // Se fosse un cambio di struttura, queste variabili sarebbero valorizzate
            // poichÃ© riportano i dati relativi alla struttura su cui l'utente
            // sta operando.

            HttpServletRequest request = getRequest();
            String ipVers = request.getHeader("X-FORWARDED-FOR");
            if (ipVers == null || ipVers.isEmpty()) {
                ipVers = request.getRemoteAddr();
            }
            log.debug("Indirizzo da cui l'utente si connette: {}", ipVers);
            loginLogHelper.writeLogEvento(utente, ipVers, LoginLogHelper.TipiEvento.LOGIN);
            getSession().setAttribute(AuditSessionListener.CLIENT_IP_ADDRESS, ipVers);
        }
    }

    // MEV#23905 - Associazione utente SPID con anagrafica utenti
    private void gestisciUtenteDaAssociare(User utente) throws EMFError {
        this.freeze();
        String username = "NON_PRESENTE";
        /*
         * MEV#22913 - Logging accessi SPID non autorizzati In caso di utente SPID lo username non c'Ã¨ ancora perchÃ©
         * deve essere ancora associato Quindi si prende il suo codice fiscale se presente, altrimenti una stringa fissa
         * come username
         */
        if (utente.getCodiceFiscale() != null && !utente.getCodiceFiscale().isEmpty()) {
            username = utente.getCodiceFiscale().toUpperCase();
        }
        sacerLogHelper.insertEventoLoginUser(username, getIpClient(), new Date(),
                ConstLogEventoLoginUser.TipoEvento.BAD_CF.name(),
                "SACERDIPS - " + ConstLogEventoLoginUser.DS_EVENTO_BAD_CF_SPID, utente.getCognome(), utente.getNome(),
                utente.getCodiceFiscale(), utente.getExternalId(), utente.getEmail());
        String retURL = applicationProperties
                .getProperty(ConstDipParamApplic.NmParamApplic.URL_BACK_ASSOCIAZIONE_UTENTE_CF.name());
        String salt = Base64.encodeBase64URLSafeString(PwdUtil.generateSalt());
        byte[] cfCriptato = EncryptionUtil.aesCrypt(utente.getCodiceFiscale(), EncryptionUtil.Aes.BIT_256);
        String f = Base64.encodeBase64URLSafeString(cfCriptato);
        byte[] cogCriptato = EncryptionUtil.aesCrypt(utente.getCognome(), EncryptionUtil.Aes.BIT_256);
        String c = Base64.encodeBase64URLSafeString(cogCriptato);
        byte[] nomeCriptato = EncryptionUtil.aesCrypt(utente.getNome(), EncryptionUtil.Aes.BIT_256);
        String n = Base64.encodeBase64URLSafeString(nomeCriptato);

        String hmac = EncryptionUtil.getHMAC(retURL + ":" + utente.getCodiceFiscale() + ":" + salt);
        try {
            this.getResponse()
                    .sendRedirect(applicationProperties
                            .getProperty(ConstDipParamApplic.NmParamApplic.URL_ASSOCIAZIONE_UTENTE_CF.name()) + "?r="
                            + Base64.encodeBase64URLSafeString(retURL.getBytes()) + "&h=" + hmac + "&s=" + salt + "&f="
                            + f + "&c=" + c + "&n=" + n);
        } catch (IOException ex) {
            throw new EMFError("ERROR", "Errore nella sendRedirect verso Iam");
        }

    }

    public void backFromAssociation() throws EMFError {
        User user = (User) getUser();
        if (user.getCodiceFiscale() != null && !user.getCodiceFiscale().isEmpty()) {
            List<UsrUser> l = loginLogHelper.findByCodiceFiscale(user.getCodiceFiscale());
            if (l.size() == 1) {
                UsrUser us = l.iterator().next();
                user.setUtenteDaAssociare(false);
                user.setUsername(us.getNmUserid());
                user.setIdUtente(us.getIdUserIam());
                process();
                getMessageBox().addInfo("L'utente loggato Ã¨ stato ricondotto con successo all'utenza Parer.");
                return;
            }
        }
        /* Per sicurezza se qualcuno forza l'accesso con la URL senza provenire da IAM lo butto fuori! */
        log.error("Chiamata al metodo beckFromAssociation non autorizzata! Effettuo il logout forzato!");
        redirectToAction("Logout.html");
    }

}
