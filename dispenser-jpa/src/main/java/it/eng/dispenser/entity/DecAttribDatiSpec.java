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
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 * The persistent class for the DEC_ATTRIB_DATI_SPEC database table.
 *
 */
@Entity
@Table(name = "DEC_ATTRIB_DATI_SPEC", schema = "SACER")
public class DecAttribDatiSpec implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    // @SequenceGenerator(name = "DEC_ATTRIB_DATI_SPEC_IDATTRIBDATISPEC_GENERATOR", sequenceName =
    // "SDEC_ATTRIB_DATI_SPEC", allocationSize = 1, schema = "SACER")
    // @GeneratedValue(strategy = GenerationType.SEQUENCE, generator =
    // "DEC_ATTRIB_DATI_SPEC_IDATTRIBDATISPEC_GENERATOR")
    @Column(name = "ID_ATTRIB_DATI_SPEC")
    private long idAttribDatiSpec;

    @Column(name = "DS_ATTRIB_DATI_SPEC")
    private String dsAttribDatiSpec;

    @Column(name = "ID_TIPO_COMP_DOC")
    private java.math.BigDecimal idTipoCompDoc;

    @Column(name = "ID_TIPO_DOC")
    private java.math.BigDecimal idTipoDoc;

    @Column(name = "ID_TIPO_UNITA_DOC")
    private java.math.BigDecimal idTipoUnitaDoc;

    @Column(name = "NM_ATTRIB_DATI_SPEC")
    private String nmAttribDatiSpec;

    @Column(name = "NM_SISTEMA_MIGRAZ")
    private String nmSistemaMigraz;

    @Column(name = "TI_ATTRIB_DATI_SPEC")
    private String tiAttribDatiSpec;

    @Column(name = "TI_ENTITA_SACER")
    private String tiEntitaSacer;

    @Column(name = "TI_USO_ATTRIB")
    private String tiUsoAttrib;

    // bi-directional many-to-one association to AroValoreAttribDatiSpec
    @OneToMany(mappedBy = "decAttribDatiSpec")
    private List<AroValoreAttribDatiSpec> aroValoreAttribDatiSpecs;

    // bi-directional many-to-one association to OrgStrut
    @ManyToOne
    @JoinColumn(name = "ID_STRUT")
    private OrgStrut orgStrut;

    public DecAttribDatiSpec() {
    }

    public long getIdAttribDatiSpec() {
        return this.idAttribDatiSpec;
    }

    public void setIdAttribDatiSpec(long idAttribDatiSpec) {
        this.idAttribDatiSpec = idAttribDatiSpec;
    }

    public String getDsAttribDatiSpec() {
        return this.dsAttribDatiSpec;
    }

    public void setDsAttribDatiSpec(String dsAttribDatiSpec) {
        this.dsAttribDatiSpec = dsAttribDatiSpec;
    }

    public java.math.BigDecimal getIdTipoCompDoc() {
        return this.idTipoCompDoc;
    }

    public void setIdTipoCompDoc(java.math.BigDecimal idTipoCompDoc) {
        this.idTipoCompDoc = idTipoCompDoc;
    }

    public java.math.BigDecimal getIdTipoDoc() {
        return this.idTipoDoc;
    }

    public void setIdTipoDoc(java.math.BigDecimal idTipoDoc) {
        this.idTipoDoc = idTipoDoc;
    }

    public java.math.BigDecimal getIdTipoUnitaDoc() {
        return this.idTipoUnitaDoc;
    }

    public void setIdTipoUnitaDoc(java.math.BigDecimal idTipoUnitaDoc) {
        this.idTipoUnitaDoc = idTipoUnitaDoc;
    }

    public String getNmAttribDatiSpec() {
        return this.nmAttribDatiSpec;
    }

    public void setNmAttribDatiSpec(String nmAttribDatiSpec) {
        this.nmAttribDatiSpec = nmAttribDatiSpec;
    }

    public String getNmSistemaMigraz() {
        return this.nmSistemaMigraz;
    }

    public void setNmSistemaMigraz(String nmSistemaMigraz) {
        this.nmSistemaMigraz = nmSistemaMigraz;
    }

    public String getTiAttribDatiSpec() {
        return this.tiAttribDatiSpec;
    }

    public void setTiAttribDatiSpec(String tiAttribDatiSpec) {
        this.tiAttribDatiSpec = tiAttribDatiSpec;
    }

    public String getTiEntitaSacer() {
        return this.tiEntitaSacer;
    }

    public void setTiEntitaSacer(String tiEntitaSacer) {
        this.tiEntitaSacer = tiEntitaSacer;
    }

    public String getTiUsoAttrib() {
        return this.tiUsoAttrib;
    }

    public void setTiUsoAttrib(String tiUsoAttrib) {
        this.tiUsoAttrib = tiUsoAttrib;
    }

    public List<AroValoreAttribDatiSpec> getAroValoreAttribDatiSpecs() {
        return this.aroValoreAttribDatiSpecs;
    }

    public void setAroValoreAttribDatiSpecs(List<AroValoreAttribDatiSpec> aroValoreAttribDatiSpecs) {
        this.aroValoreAttribDatiSpecs = aroValoreAttribDatiSpecs;
    }

    public AroValoreAttribDatiSpec addAroValoreAttribDatiSpec(AroValoreAttribDatiSpec aroValoreAttribDatiSpec) {
        getAroValoreAttribDatiSpecs().add(aroValoreAttribDatiSpec);
        aroValoreAttribDatiSpec.setDecAttribDatiSpec(this);

        return aroValoreAttribDatiSpec;
    }

    public AroValoreAttribDatiSpec removeAroValoreAttribDatiSpec(AroValoreAttribDatiSpec aroValoreAttribDatiSpec) {
        getAroValoreAttribDatiSpecs().remove(aroValoreAttribDatiSpec);
        aroValoreAttribDatiSpec.setDecAttribDatiSpec(null);

        return aroValoreAttribDatiSpec;
    }

    public OrgStrut getOrgStrut() {
        return this.orgStrut;
    }

    public void setOrgStrut(OrgStrut orgStrut) {
        this.orgStrut = orgStrut;
    }

}
