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

package it.eng.dispenser.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

@Embeddable
public class QryUdByVlDatoProfiloDtId implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "ID_STRUT")
    private BigDecimal idStrut;
    @Column(name = "ID_UNITA_DOC")
    private BigDecimal idUnitaDoc;
    @Column(name = "TI_DATO_PROFILO")
    private String tiDatoProfilo;

    public QryUdByVlDatoProfiloDtId() {
    }

    public BigDecimal getIdStrut() {
        return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
        this.idStrut = idStrut;
    }

    public BigDecimal getIdUnitaDoc() {
        return this.idUnitaDoc;
    }

    public void setIdUnitaDoc(BigDecimal idUnitaDoc) {
        this.idUnitaDoc = idUnitaDoc;
    }

    public String getTiDatoProfilo() {
        return this.tiDatoProfilo;
    }

    public void setTiDatoProfilo(String tiDatoProfilo) {
        this.tiDatoProfilo = tiDatoProfilo;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + Objects.hashCode(this.idStrut);
        hash = 11 * hash + Objects.hashCode(this.idUnitaDoc);
        hash = 11 * hash + Objects.hashCode(this.tiDatoProfilo);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final QryUdByVlDatoProfiloDtId other = (QryUdByVlDatoProfiloDtId) obj;
        if (!Objects.equals(this.tiDatoProfilo, other.tiDatoProfilo)) {
            return false;
        }
        if (!Objects.equals(this.idStrut, other.idStrut)) {
            return false;
        }
        if (!Objects.equals(this.idUnitaDoc, other.idUnitaDoc)) {
            return false;
        }
        return true;
    }

}
