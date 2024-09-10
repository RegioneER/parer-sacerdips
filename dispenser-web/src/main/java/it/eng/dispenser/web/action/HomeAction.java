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
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import it.eng.dispenser.entity.constraint.ConstDipParamApplic;
import it.eng.dispenser.slite.gen.Application;
import it.eng.dispenser.slite.gen.action.HomeAbstractAction;
import it.eng.parer.dispenser.util.DataSourcePropertiesFactoryBean;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.form.fields.Field;
import it.eng.spagoLite.form.fields.Fields;
import it.eng.spagoLite.security.auth.PwdUtil;
import it.eng.util.EncryptionUtil;

public class HomeAction extends HomeAbstractAction {

    private static final Logger log = LoggerFactory.getLogger(HomeAction.class);

    @Autowired
    private DataSourcePropertiesFactoryBean applicationProperties;

    @Override
    public void initOnClick() throws EMFError {
        // not implemented
    }

    public void process() throws EMFError {
        getUser().getMenu().reset();
        try {
            findNews();
            if (getUser().getScadenzaPwd() != null) {
                Date now = new Date();
                Calendar cal = Calendar.getInstance();
                cal.setTime(getUser().getScadenzaPwd());
                int numGiorni = Integer.parseInt(applicationProperties
                        .getProperty(ConstDipParamApplic.NmParamApplic.NUM_GIORNI_ESPONI_SCAD_PSW.name()));
                cal.add(Calendar.DATE, -numGiorni);

                if (cal.getTime().before(now) && getUser().getScadenzaPwd().after(now)) {
                    long from = now.getTime();
                    long to = getUser().getScadenzaPwd().getTime();
                    long millisecondiFraDueDate = to - from;
                    // 1 giorno medio = 1000*60*60*24 ms = 86400000 ms
                    double diffGiorni = Math.round(millisecondiFraDueDate / 86400000.0);
                    getMessageBox().addError("Attenzione: la password scadr\u00E0 tra " + (int) diffGiorni
                            + " giorni. Si prega di modificarla al pi\u00F9 presto");
                }

            }
        } catch (Exception e) {
            log.error("Errore nel recupero delle news", e);
        }
        forwardToPublisher(getDefaultPublsherName());
    }

    @Override
    public String getControllerName() {
        return Application.Actions.HOME;
    }

    @Override
    protected String getDefaultPublsherName() {
        return Application.Publisher.HOME;
    }

    @Override
    public void loadDettaglio() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void undoDettaglio() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void saveDettaglio() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void dettaglioOnClick() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void elencoOnClick() throws EMFError {
        goBack();
    }

    public void changePwd() throws EMFError, IOException {
        this.freeze();
        StringBuilder sb = new StringBuilder();
        sb.append(getRequest().getScheme());
        sb.append("://");
        sb.append(getRequest().getServerName());
        sb.append(":");
        sb.append(getRequest().getServerPort());
        sb.append(getRequest().getContextPath());
        String retURL = sb.toString();
        String salt = Base64.encodeBase64URLSafeString(PwdUtil.generateSalt());
        String hmac = EncryptionUtil.getHMAC(retURL + ":" + salt);
        this.getResponse().sendRedirect(
                applicationProperties.getProperty(ConstDipParamApplic.NmParamApplic.URL_MODIFICA_PASSWORD.name())
                        + "?r=" + retURL + "&h=" + hmac + "&s=" + salt);
    }

    @Override
    public void insertDettaglio() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void update(Fields<Field> fields) throws EMFError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(Fields<Field> fields) throws EMFError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void reloadAfterGoBack(String publisherName) {
        //
    }

    private void findNews() {
        // not implemented

    }

    @Override
    public void mostraInformativa() throws EMFError {
        forwardToPublisher("/login/informativa");
    }

}
