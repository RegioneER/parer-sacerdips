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

package it.eng.dispenser.web.util;

import java.util.Collection;
import java.util.SortedMap;
import java.util.TreeMap;

import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.table.BaseTable;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;

public class ComboGetter {

    public static final String CAMPO_REGISTRO_AG = "registro_ag";
    public static final String CAMPO_TI_GESTIONE_PARAM = "ti_gestione_param";
    public static final String CAMPO_TIPOLOGIA = "tipologia";
    public static final String CAMPO_VALORE = "valore";
    public static final String CAMPO_FLAG = "flag";
    public static final String CAMPO_NOME = "nome";
    public static final String CAMPO_ANNO = "anno";

    private ComboGetter() {
        throw new IllegalStateException("Utility class");
    }

    /*
     * GESTIONE DECODEMAP GENERICHE
     */
    @SafeVarargs
    public static <T extends Enum<?>> DecodeMap getMappaSortedGenericEnum(String key, T... enumerator) {
        BaseTable bt = new BaseTable();
        DecodeMap mappa = new DecodeMap();
        for (T mod : sortEnum(enumerator)) {
            bt.add(createKeyValueBaseRow(key, mod.name()));
        }
        mappa.populatedMap(bt, key, key);
        return mappa;
    }

    private static BaseRow createKeyValueBaseRow(String key, String value) {

        BaseRow br = new BaseRow();
        br.setString(key, value);
        return br;
    }

    public static <T extends Enum<?>> Collection<T> sortEnum(T[] enumValues) {
        SortedMap<String, T> map = new TreeMap<>();
        for (T val : enumValues) {
            map.put(val.name(), val);
        }
        return map.values();
    }
}
