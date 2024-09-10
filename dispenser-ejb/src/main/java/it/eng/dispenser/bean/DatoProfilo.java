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

package it.eng.dispenser.bean;

import java.io.Serializable;
import java.math.BigDecimal;

public class DatoProfilo implements Serializable {

    private static final long serialVersionUID = 1L;

    private BigDecimal udID;
    private String type;
    private Object value;
    private String tipoDatoProfilo;
    private BigDecimal structID;
    private BigDecimal idTipoUnitaDoc;

    public DatoProfilo(long structID, String TipoDatoProfilo, long udID, Object value, String type,
            long idTipoUnitaDoc) {
        this(new BigDecimal(structID), TipoDatoProfilo, new BigDecimal(udID), value, type,
                new BigDecimal(idTipoUnitaDoc));
    }

    public DatoProfilo(BigDecimal structID, String TipoDatoProfilo, long udID, Object value, String type,
            BigDecimal idTipoUnitaDoc) {
        this(structID, TipoDatoProfilo, new BigDecimal(udID), value, type, idTipoUnitaDoc);
    }

    public DatoProfilo(BigDecimal structID, String TipoDatoProfilo, BigDecimal udID, Object value, String type,
            BigDecimal idTipoUnitaDoc) {
        this.udID = udID;
        this.value = value;
        this.type = type;
        this.structID = structID;
        this.tipoDatoProfilo = TipoDatoProfilo;
        this.idTipoUnitaDoc = idTipoUnitaDoc;
    }

    public BigDecimal getStructID() {
        return this.structID;
    }

    public String getTipoDatoProfilo() {
        return this.tipoDatoProfilo;
    }

    public BigDecimal getUdID() {
        return udID;
    }

    public String getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public BigDecimal getIdTipoUnitaDoc() {
        return idTipoUnitaDoc;
    }

}
