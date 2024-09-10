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

import java.math.BigDecimal;

public class MetadatoDoc extends Metadato {
    private static final long serialVersionUID = 1L;

    private BigDecimal docID;

    public MetadatoDoc(long attrID, long docID, long udID, String value, String type) {
        super(attrID, udID, value, type);
        this.docID = new BigDecimal(docID);
    }

    public MetadatoDoc(BigDecimal attrID, BigDecimal docID, BigDecimal udID, String value, String type) {
        super(attrID, udID, value, type);
        this.docID = docID;
    }

    public BigDecimal getAttrID() {
        return getMetadatoID();
    }

    public BigDecimal getDocID() {
        return this.docID;
    }
}
