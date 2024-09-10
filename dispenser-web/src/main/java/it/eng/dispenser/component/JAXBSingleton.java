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

package it.eng.dispenser.component;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import it.eng.parer.ws.xml.versReqStato.Recupero;
import it.eng.parer.ws.xml.versRespStato.StatoConservazione;

@Component
public class JAXBSingleton {

    private static final Logger log = LoggerFactory.getLogger(JAXBSingleton.class);

    private JAXBContext contextStatoConservazione;
    private JAXBContext contextRecupero;

    @PostConstruct
    public void postConstruct() {
        try {
            contextStatoConservazione = JAXBContext.newInstance(StatoConservazione.class);
            contextRecupero = JAXBContext.newInstance(Recupero.class);
        } catch (JAXBException e) {
            log.error("Errore nella configurazione di JAXB", e);
        }
    }

    public JAXBContext getContextStatoConservazione() {
        return contextStatoConservazione;
    }

    public JAXBContext getContextRecupero() {
        return contextRecupero;
    }
}
