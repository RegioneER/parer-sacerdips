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
package it.eng.dispenser.ws.rest.monitoraggio.ejb;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.dispenser.ws.dto.IRispostaWS;
import it.eng.dispenser.ws.dto.RispostaControlli;
import it.eng.dispenser.ws.rest.ejb.ControlliRestWS;
import it.eng.dispenser.ws.rest.monitoraggio.dto.RispostaWSStatusMonitor;
import it.eng.dispenser.ws.rest.monitoraggio.dto.StatusMonExt;
import it.eng.dispenser.ws.rest.monitoraggio.dto.rmonitor.HostMonitor;
import it.eng.dispenser.ws.utils.MessaggiWSBundle;
import it.eng.spagoLite.security.User;

/**
 *
 * @author fioravanti_f
 */
@Stateless(mappedName = "StatusMonitorSync")
@LocalBean
public class StatusMonitorSync {

    @EJB
    ControlliRestWS myControlliWs;

    @EJB
    StatusMonitorGen statusMonitorGen;

    private static final Logger log = LoggerFactory.getLogger(StatusMonitorSync.class.getName());

    public void initRispostaWs(RispostaWSStatusMonitor rispostaWs, StatusMonExt mon) {
        log.debug("sono nel metodo init");
        HostMonitor myEsito = new HostMonitor();

        rispostaWs.setSeverity(IRispostaWS.SeverityEnum.OK);
        rispostaWs.setErrorCode("");
        rispostaWs.setErrorMessage("");

        rispostaWs.setIstanzaEsito(myEsito);
        myEsito.setVersione(mon.getDescrizione().getVersione());

        // questo codice è identico al suo omologo in SACER
    }

    public void verificaCredenziali(String loginName, String password, String indirizzoIp,
            RispostaWSStatusMonitor rispostaWs, StatusMonExt mon) {
        RispostaControlli tmpRispostaControlli = null;

        // verifica le credenziali e se sono abilitato a chiamare questa funzione
        tmpRispostaControlli = myControlliWs.checkCredenzialiEAuth(loginName, password, indirizzoIp,
                mon.getDescrizione());
        if (!tmpRispostaControlli.isrBoolean()) {
            rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
            rispostaWs.setEsitoWsError(tmpRispostaControlli.getCodErr(), tmpRispostaControlli.getDsErr());
        }

        mon.setLoginName(loginName);
        mon.setUtente((User) tmpRispostaControlli.getrObject());
    }

    public void recuperaStatusGlobale(RispostaWSStatusMonitor rispostaWs, StatusMonExt mon) {

        if (mon.getUtente() == null) {
            rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
            rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666, "Errore: l'utente non è autenticato.");
            return;
        }

        try {
            statusMonitorGen.calcolaStatusGlobale(rispostaWs, mon);
        } catch (Exception e) {
            rispostaWs.setSeverity(IRispostaWS.SeverityEnum.ERROR);
            rispostaWs.setEsitoWsErrBundle(MessaggiWSBundle.ERR_666,
                    "Errore dell'EJB nella fase di generazione dello status " + e.getMessage());
            log.error("Errore dell'EJB nella fase di generazione dello status", e);
        }
    }

}
