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
package it.eng.dispenser.ws.utils;

import java.text.MessageFormat;
import java.util.ResourceBundle;

/**
 *
 * @author fioravanti_f
 */
public class MessaggiWSBundle {

    // Definisce le costanti utilizzate per il bundle dei messaggi di errore
    private static final String BUNDLE_NAME = "messaggi_ws";
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);
    public static final String DEFAULT_LOCALE = "it";
    // COSTANTI DI ERRORE
    // ERRORI IMPREVISTI
    public static final String ERR_666 = "666";

    // ERRORI DEI SERVIZI REST
    public static final String ERR_WS_CHECK = "ERR-WS-CHECK";
    public static final String MON_AUTH_001 = "MON-AUTH-001";
    public static final String MON_AUTH_002 = "MON-AUTH-002";
    public static final String MON_AUTH_003 = "MON-AUTH-003";
    public static final String MON_AUTH_004 = "MON-AUTH-004";
    public static final String MON_AUTH_005 = "MON-AUTH-005";

    public static String getString(String key) {
        return RESOURCE_BUNDLE.getString(key);
    }

    public static String getString(String key, Object... params) {
        return MessageFormat.format(RESOURCE_BUNDLE.getString(key), params);
    }
}
