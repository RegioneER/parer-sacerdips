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

package it.eng.dispenser.entity.constraint;

public class ConstDipParamApplic {

    public enum NmParamApplic {
        USERID_RECUP_INFO, URL_RECUP_AUTOR_USER, URL_RECUP_HELP, URL_RECUP_AIP, PSW_RECUP_INFO, VERSIONE_XML_RECUP_UD,
        URL_RECUP_UD, TIMEOUT_RECUP_UD, USERID_RECUP_UD, PSW_RECUP_UD, URL_RECUP_COMP_TRASF, NM_APPLIC,
        URL_MODIFICA_PASSWORD, CACHE_DELAY_POLLING, SERVER_NAME_SYSTEM_PROPERTY, NUM_GIORNI_ESPONI_SCAD_PSW,
        URL_BACK_ASSOCIAZIONE_UTENTE_CF, URL_ASSOCIAZIONE_UTENTE_CF
    }

    public enum TiParamApplic {
        IAM, SACER_RECUP
    }
}
