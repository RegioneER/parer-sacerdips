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

package it.eng.dispenser.slite.gen.tablebean;

import java.sql.Types;
import java.util.LinkedHashMap;
import java.util.Map;

import it.eng.spagoLite.db.oracle.bean.column.ColumnDescriptor;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * @author Sloth
 *
 *         Bean per la tabella Dip_Campo_Ricerca
 *
 */
public class DipCampoRicercaTableDescriptor extends TableDescriptor {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$TableBeanWriter", comments =
     * "This class was generated by OraTool", date = "Wednesday, 27 May 2015 11:25" )
     */

    public static final String SELECT = "Select * from Dip_Campo_Ricerca /**/";
    public static final String TABLE_NAME = "Dip_Campo_Ricerca";
    public static final String COL_ID_CAMPO_RICERCA = "id_campo_ricerca";
    public static final String COL_NM_LABEL_CAMPO = "nm_label_campo";
    public static final String COL_ID_GRUPPO_CAMPI = "id_gruppo_campi";
    public static final String COL_TI_DATO_CAMPO = "ti_dato_campo";
    public static final String COL_DS_DESCRIZIONE_CAMPO = "ds_descrizione_campo";
    public static final String COL_NI_RIGA_CAMPO = "ni_riga_campo";
    public static final String COL_NI_COLONNA_CAMPO = "ni_colonna_campo";

    private static Map<String, ColumnDescriptor> map = new LinkedHashMap<String, ColumnDescriptor>();

    static {
        map.put(COL_ID_CAMPO_RICERCA, new ColumnDescriptor(COL_ID_CAMPO_RICERCA, Types.DECIMAL, 22, true));
        map.put(COL_NM_LABEL_CAMPO, new ColumnDescriptor(COL_NM_LABEL_CAMPO, Types.VARCHAR, 100, false));
        map.put(COL_ID_GRUPPO_CAMPI, new ColumnDescriptor(COL_ID_GRUPPO_CAMPI, Types.DECIMAL, 22, false));
        map.put(COL_TI_DATO_CAMPO, new ColumnDescriptor(COL_TI_DATO_CAMPO, Types.VARCHAR, 20, false));
        map.put(COL_DS_DESCRIZIONE_CAMPO, new ColumnDescriptor(COL_DS_DESCRIZIONE_CAMPO, Types.VARCHAR, 254, false));
        map.put(COL_NI_RIGA_CAMPO, new ColumnDescriptor(COL_NI_RIGA_CAMPO, Types.DECIMAL, 22, false));
        map.put(COL_NI_COLONNA_CAMPO, new ColumnDescriptor(COL_NI_COLONNA_CAMPO, Types.DECIMAL, 22, false));
    }

    public Map<String, ColumnDescriptor> getColumnMap() {
        return map;
    }

    public String getTableName() {
        return TABLE_NAME;
    }

    public String getStatement() {
        return SELECT;
    }

}
