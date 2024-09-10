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

public abstract class Metadato implements Serializable {
    private static final long serialVersionUID = 1L;

    private BigDecimal metadatoID;
    private BigDecimal udID;
    private String type;
    private String value;

    Metadato(long metadatoID, long udID, String value, String type) {
        this(new BigDecimal(metadatoID), new BigDecimal(udID), value, type);
    }

    Metadato(BigDecimal metadatoID, BigDecimal udID, String value, String type) {
        this.metadatoID = metadatoID;
        this.udID = udID;
        this.value = value;
        this.type = type;
    }

    BigDecimal getMetadatoID() {
        return this.metadatoID;
    }

    public BigDecimal getUdID() {
        return this.udID;
    }

    public String getValue() {
        return this.value;
    }

    public String getType() {
        return this.type;
    }
}
