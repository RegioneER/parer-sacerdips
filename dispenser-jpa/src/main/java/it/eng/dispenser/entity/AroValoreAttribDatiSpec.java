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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The persistent class for the ARO_VALORE_ATTRIB_DATI_SPEC database table.
 *
 */
@Entity
@Table(name = "ARO_VALORE_ATTRIB_DATI_SPEC", schema = "SACER")
public class AroValoreAttribDatiSpec implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    // @SequenceGenerator(name = "ARO_VALORE_ATTRIB_DATI_SPEC_IDVALOREATTRIBDATISPEC_GENERATOR", sequenceName =
    // "SARO_VALORE_ATTRIB_DATI_SPEC", allocationSize = 1, schema = "SACER")
    // @GeneratedValue(strategy = GenerationType.SEQUENCE, generator =
    // "ARO_VALORE_ATTRIB_DATI_SPEC_IDVALOREATTRIBDATISPEC_GENERATOR")
    @Column(name = "ID_VALORE_ATTRIB_DATI_SPEC")
    private long idValoreAttribDatiSpec;

    @Column(name = "DL_VALORE")
    private String dlValore;

    @Column(name = "ID_STRUT")
    private BigDecimal idStrut;

    // bi-directional many-to-one association to AroUsoXsdDatiSpec
    @ManyToOne
    @JoinColumn(name = "ID_USO_XSD_DATI_SPEC")
    private AroUsoXsdDatiSpec aroUsoXsdDatiSpec;

    // bi-directional many-to-one association to DecAttribDatiSpec
    @ManyToOne
    @JoinColumn(name = "ID_ATTRIB_DATI_SPEC")
    private DecAttribDatiSpec decAttribDatiSpec;

    public AroValoreAttribDatiSpec() {
    }

    public long getIdValoreAttribDatiSpec() {
        return this.idValoreAttribDatiSpec;
    }

    public void setIdValoreAttribDatiSpec(long idValoreAttribDatiSpec) {
        this.idValoreAttribDatiSpec = idValoreAttribDatiSpec;
    }

    public String getDlValore() {
        return this.dlValore;
    }

    public void setDlValore(String dlValore) {
        this.dlValore = dlValore;
    }

    public BigDecimal getIdStrut() {
        return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
        this.idStrut = idStrut;
    }

    public AroUsoXsdDatiSpec getAroUsoXsdDatiSpec() {
        return this.aroUsoXsdDatiSpec;
    }

    public void setAroUsoXsdDatiSpec(AroUsoXsdDatiSpec aroUsoXsdDatiSpec) {
        this.aroUsoXsdDatiSpec = aroUsoXsdDatiSpec;
    }

    public DecAttribDatiSpec getDecAttribDatiSpec() {
        return this.decAttribDatiSpec;
    }

    public void setDecAttribDatiSpec(DecAttribDatiSpec decAttribDatiSpec) {
        this.decAttribDatiSpec = decAttribDatiSpec;
    }

}
