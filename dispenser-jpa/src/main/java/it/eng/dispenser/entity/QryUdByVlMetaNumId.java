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
public class QryUdByVlMetaNumId implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "ID_ATTRIB_DATI_SPEC")
    private BigDecimal idAttribDatiSpec;
    @Column(name = "ID_UNITA_DOC")
    private BigDecimal idUnitaDoc;

    public QryUdByVlMetaNumId() {
    }

    public BigDecimal getIdAttribDatiSpec() {
        return this.idAttribDatiSpec;
    }

    public void setIdAttribDatiSpec(BigDecimal idAttribDatiSpec) {
        this.idAttribDatiSpec = idAttribDatiSpec;
    }

    public BigDecimal getIdUnitaDoc() {
        return this.idUnitaDoc;
    }

    public void setIdUnitaDoc(BigDecimal idUnitaDoc) {
        this.idUnitaDoc = idUnitaDoc;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + Objects.hashCode(this.idAttribDatiSpec);
        hash = 41 * hash + Objects.hashCode(this.idUnitaDoc);
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
        final QryUdByVlMetaNumId other = (QryUdByVlMetaNumId) obj;
        if (!Objects.equals(this.idAttribDatiSpec, other.idAttribDatiSpec)) {
            return false;
        }
        if (!Objects.equals(this.idUnitaDoc, other.idUnitaDoc)) {
            return false;
        }
        return true;
    }

}
