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

package it.eng.dispenser.web.security;

import it.eng.dispenser.entity.constraint.ConstDipParamApplic;
import it.eng.integriam.client.util.UserUtil;
import it.eng.integriam.client.ws.IAMSoapClients;
import it.eng.integriam.client.ws.recauth.AuthWSException_Exception;
import it.eng.integriam.client.ws.recauth.RecuperoAutorizzazioni;
import it.eng.integriam.client.ws.recauth.RecuperoAutorizzazioniRisposta;
import it.eng.parer.dispenser.util.DataSourcePropertiesFactoryBean;
import it.eng.spagoLite.SessionManager;
import it.eng.spagoLite.security.User;
import it.eng.spagoLite.security.auth.Authenticator;

import javax.servlet.http.HttpSession;
import javax.xml.ws.WebServiceException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component("authenticator")
public class DispenserAuthenticator extends Authenticator {

    @Autowired
    private DataSourcePropertiesFactoryBean applicationProperties;

    /*
     * @EJB(mappedName = "java:app/Dispenser-ejb/LoginLogHelper") private LoginLogHelper loginLogHelper;
     */
    @Override
    protected String getAppName() {
        String nomeApplic = applicationProperties.getProperty(ConstDipParamApplic.NmParamApplic.NM_APPLIC.name());
        if (StringUtils.isBlank(nomeApplic)) {
            throw new IllegalStateException("Parametro nome applicazione assente su database");
        }
        return nomeApplic;
    }

    @Override
    public User recuperoAutorizzazioni(HttpSession httpSession) {
        User utente = (User) SessionManager.getUser(httpSession);
        RecuperoAutorizzazioni client = IAMSoapClients.recuperoAutorizzazioniClient(
                applicationProperties.getProperty(ConstDipParamApplic.NmParamApplic.USERID_RECUP_INFO.name()),
                applicationProperties.getProperty(ConstDipParamApplic.NmParamApplic.PSW_RECUP_INFO.name()),
                applicationProperties.getProperty(ConstDipParamApplic.NmParamApplic.URL_RECUP_AUTOR_USER.name()));
        if (client == null) {
            throw new WebServiceException("Non è stato possibile recuperare la lista delle autorizzazioni da SIAM");
        }
        RecuperoAutorizzazioniRisposta resp;
        try {
            resp = client.recuperoAutorizzazioniPerNome(utente.getUsername(), getAppName(), null);
        } catch (AuthWSException_Exception e) {
            throw new RuntimeException(e);
        }
        UserUtil.fillComponenti(utente, resp);
        SessionManager.setUser(httpSession, utente);
        return utente;
    }

}
