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
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The persistent class for the ARO_USO_XSD_DATI_SPEC database table.
 *
 */
@Entity
@Table(name = "ARO_USO_XSD_DATI_SPEC", schema = "SACER")
public class AroUsoXsdDatiSpec implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    // @SequenceGenerator(name = "ARO_USO_XSD_DATI_SPEC_IDUSOXSDDATISPEC_GENERATOR", sequenceName =
    // "SARO_USO_XSD_DATI_SPEC", allocationSize = 1, schema = "SACER")
    // @GeneratedValue(strategy = GenerationType.SEQUENCE, generator =
    // "ARO_USO_XSD_DATI_SPEC_IDUSOXSDDATISPEC_GENERATOR")
    @Column(name = "ID_USO_XSD_DATI_SPEC")
    private long idUsoXsdDatiSpec;

    @Column(name = "ID_COMP_DOC")
    private BigDecimal idCompDoc;

    @Column(name = "ID_STRUT")
    private BigDecimal idStrut;

    @Column(name = "ID_XSD_DATI_SPEC")
    private BigDecimal idXsdDatiSpec;

    @Column(name = "TI_ENTITA_SACER")
    private String tiEntitaSacer;

    @Column(name = "TI_USO_XSD")
    private String tiUsoXsd;

    // bi-directional many-to-one association to AroDoc
    @ManyToOne
    @JoinColumn(name = "ID_DOC")
    private AroDoc aroDoc;

    // bi-directional many-to-one association to AroUnitaDoc
    @ManyToOne
    @JoinColumn(name = "ID_UNITA_DOC")
    private AroUnitaDoc aroUnitaDoc;

    // bi-directional many-to-one association to AroValoreAttribDatiSpec
    @OneToMany(mappedBy = "aroUsoXsdDatiSpec")
    private List<AroValoreAttribDatiSpec> aroValoreAttribDatiSpecs;

    public AroUsoXsdDatiSpec() {
    }

    public long getIdUsoXsdDatiSpec() {
        return this.idUsoXsdDatiSpec;
    }

    public void setIdUsoXsdDatiSpec(long idUsoXsdDatiSpec) {
        this.idUsoXsdDatiSpec = idUsoXsdDatiSpec;
    }

    public BigDecimal getIdCompDoc() {
        return this.idCompDoc;
    }

    public void setIdCompDoc(BigDecimal idCompDoc) {
        this.idCompDoc = idCompDoc;
    }

    public BigDecimal getIdStrut() {
        return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
        this.idStrut = idStrut;
    }

    public BigDecimal getIdXsdDatiSpec() {
        return this.idXsdDatiSpec;
    }

    public void setIdXsdDatiSpec(BigDecimal idXsdDatiSpec) {
        this.idXsdDatiSpec = idXsdDatiSpec;
    }

    public String getTiEntitaSacer() {
        return this.tiEntitaSacer;
    }

    public void setTiEntitaSacer(String tiEntitaSacer) {
        this.tiEntitaSacer = tiEntitaSacer;
    }

    public String getTiUsoXsd() {
        return this.tiUsoXsd;
    }

    public void setTiUsoXsd(String tiUsoXsd) {
        this.tiUsoXsd = tiUsoXsd;
    }

    public AroDoc getAroDoc() {
        return this.aroDoc;
    }

    public void setAroDoc(AroDoc aroDoc) {
        this.aroDoc = aroDoc;
    }

    public AroUnitaDoc getAroUnitaDoc() {
        return this.aroUnitaDoc;
    }

    public void setAroUnitaDoc(AroUnitaDoc aroUnitaDoc) {
        this.aroUnitaDoc = aroUnitaDoc;
    }

    public List<AroValoreAttribDatiSpec> getAroValoreAttribDatiSpecs() {
        return this.aroValoreAttribDatiSpecs;
    }

    public void setAroValoreAttribDatiSpecs(List<AroValoreAttribDatiSpec> aroValoreAttribDatiSpecs) {
        this.aroValoreAttribDatiSpecs = aroValoreAttribDatiSpecs;
    }

    public AroValoreAttribDatiSpec addAroValoreAttribDatiSpec(AroValoreAttribDatiSpec aroValoreAttribDatiSpec) {
        getAroValoreAttribDatiSpecs().add(aroValoreAttribDatiSpec);
        aroValoreAttribDatiSpec.setAroUsoXsdDatiSpec(this);

        return aroValoreAttribDatiSpec;
    }

    public AroValoreAttribDatiSpec removeAroValoreAttribDatiSpec(AroValoreAttribDatiSpec aroValoreAttribDatiSpec) {
        getAroValoreAttribDatiSpecs().remove(aroValoreAttribDatiSpec);
        aroValoreAttribDatiSpec.setAroUsoXsdDatiSpec(null);

        return aroValoreAttribDatiSpec;
    }

}
