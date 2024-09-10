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

package it.eng.dispenser.util;

import java.util.Calendar;
import java.util.Date;

public class Constants {

    public static final int PREFIX_INDEX_LENGTH = 1500;

    protected static final Date DATE_INITIAL_PARER;
    protected static final Date DATE_ANNUL_INIT;

    public static final String DATE_FORMAT = "dd/MM/yyyy";

    static {
        Calendar cal = Calendar.getInstance();
        cal.set(2011, 12, 1);
        DATE_INITIAL_PARER = cal.getTime();

        // Init value of the field ARO_UNITA_DOC.DT_ANNUL
        cal.set(2444, 12, 31);
        DATE_ANNUL_INIT = cal.getTime();
    }

    // JOB CONTANTS
    public static final String DATE_FORMAT_JOB = "dd/MM/yyyy HH:mm:ss";

    public static final String SACER_DIPS = "SACER_DIPS";

    public static final String ENTITY_PACKAGE_NAME = "it.eng.dispenser.entity";
    public static final String GRANTED_ENTITY_PACKAGE_NAME = "it.eng.dispenser.grantedEntity";
    public static final String ROWBEAN_PACKAGE_NAME = "it.eng.dispenser.slite.gen.tablebean";
    public static final String VIEWROWBEAN_PACKAGE_NAME = "it.eng.dispenser.slite.gen.viewbean";
    public static final String REQ_HTTP_NOME_RIC_PARAM = "r";

    public static final String SESSIONE_NOME_RIC = "###_NOME_RICERCA";
    public static final String SESSIONE_FORM_RIC = "###_FORM_RICERCA";

    public enum DOWNLOAD_ATTRS {
        DOWNLOAD_ACTION, DOWNLOAD_FILENAME, DOWNLOAD_FILEPATH, DOWNLOAD_DELETEFILE, DOWNLOAD_CONTENTTYPE
    }

    public enum JobEnum {
        SACER_SYNCRO, WS_MONITORAGGIO_STATUS
    }

    public enum NomiJob {

        SACER_SYNCRO;

        public static NomiJob[] getEnums(NomiJob... vals) {
            return vals;
        }

        public static NomiJob[] getComboSchedulazioniJob() {
            return getEnums(SACER_SYNCRO);
        }
    }

    public enum StatoTimer {
        ATTIVO, DISATTIVO, IN_ESECUZIONE;
    }
}
