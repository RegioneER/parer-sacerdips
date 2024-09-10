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
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the ORG_STRUT database table.
 *
 */
@Entity
@Table(name = "ORG_STRUT", schema = "SACER")
public class OrgStrut implements Serializable {

    private static final long serialVersionUID = 1L;

    private long idStrut;
    private String nmStrut;
    private String cdIpa;
    private String dlNoteStrut;
    private String dsStrut;
    private String cdStrutNormaliz;
    private Date dtIniVal;
    private Date dtFineVal;
    private Date dtIniValStrut;
    private Date dtFineValStrut;
    private String flTemplate;
    private BigDecimal idCategStrut;

    public OrgStrut() {
    }

    @Id
    // @SequenceGenerator(name = "ORG_STRUT_IDSTRUT_GENERATOR", sequenceName = "SORG_STRUT", allocationSize = 1, schema
    // = "SACER")
    // @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ORG_STRUT_IDSTRUT_GENERATOR")
    @Column(name = "ID_STRUT")
    public long getIdStrut() {
        return this.idStrut;
    }

    public void setIdStrut(long idStrut) {
        this.idStrut = idStrut;
    }

    @Column(name = "NM_STRUT")
    public String getNmStrut() {
        return this.nmStrut;
    }

    public void setNmStrut(String nmStrut) {
        this.nmStrut = nmStrut;
    }

    @Column(name = "CD_IPA")
    public String getCdIpa() {
        return cdIpa;
    }

    public void setCdIpa(String cdIpa) {
        this.cdIpa = cdIpa;
    }

    @Column(name = "DL_NOTE_STRUT")
    public String getDlNoteStrut() {
        return this.dlNoteStrut;
    }

    public void setDlNoteStrut(String dlNoteStrut) {
        this.dlNoteStrut = dlNoteStrut;
    }

    @Column(name = "DS_STRUT")
    public String getDsStrut() {
        return this.dsStrut;
    }

    public void setDsStrut(String dsStrut) {
        this.dsStrut = dsStrut;
    }

    @Column(name = "CD_STRUT_NORMALIZ")
    public String getCdStrutNormaliz() {
        return this.cdStrutNormaliz;
    }

    public void setCdStrutNormaliz(String cdStrutNormaliz) {
        this.cdStrutNormaliz = cdStrutNormaliz;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_INI_VAL")
    public Date getDtIniVal() {
        return dtIniVal;
    }

    public void setDtIniVal(Date dtIniVal) {
        this.dtIniVal = dtIniVal;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_FINE_VAL")
    public Date getDtFineVal() {
        return dtFineVal;
    }

    public void setDtFineVal(Date dtFineVal) {
        this.dtFineVal = dtFineVal;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_INI_VAL_STRUT")
    public Date getDtIniValStrut() {
        return dtIniValStrut;
    }

    public void setDtIniValStrut(Date dtIniValStrut) {
        this.dtIniValStrut = dtIniValStrut;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_FINE_VAL_STRUT")
    public Date getDtFineValStrut() {
        return dtFineValStrut;
    }

    public void setDtFineValStrut(Date dtFineValStrut) {
        this.dtFineValStrut = dtFineValStrut;
    }

    @Column(name = "FL_TEMPLATE", columnDefinition = "char(1)")
    public String getFlTemplate() {
        return this.flTemplate;
    }

    public void setFlTemplate(String flTemplate) {
        this.flTemplate = flTemplate;
    }

    @Column(name = "ID_CATEG_STRUT")
    public BigDecimal getIdCategStrut() {
        return this.idCategStrut;
    }

    public void setIdCategStrut(BigDecimal idCategStrut) {
        this.idCategStrut = idCategStrut;
    }

    @OneToMany(mappedBy = "orgStrut")
    public List<AroUnitaDoc> getAroUnitaDocs() {
        return this.aroUnitaDocs;
    }

    public void setAroUnitaDocs(List<AroUnitaDoc> aroUnitaDocs) {
        this.aroUnitaDocs = aroUnitaDocs;
    }

    public AroUnitaDoc addAroUnitaDoc(AroUnitaDoc aroUnitaDoc) {
        getAroUnitaDocs().add(aroUnitaDoc);
        aroUnitaDoc.setOrgStrut(this);

        return aroUnitaDoc;
    }

    public AroUnitaDoc removeAroUnitaDoc(AroUnitaDoc aroUnitaDoc) {
        getAroUnitaDocs().remove(aroUnitaDoc);
        aroUnitaDoc.setOrgStrut(null);

        return aroUnitaDoc;
    }

    @OneToMany(mappedBy = "orgStrut")
    public List<DecAttribDatiSpec> getDecAttribDatiSpecs() {
        return this.decAttribDatiSpecs;
    }

    public void setDecAttribDatiSpecs(List<DecAttribDatiSpec> decAttribDatiSpecs) {
        this.decAttribDatiSpecs = decAttribDatiSpecs;
    }

    public DecAttribDatiSpec addDecAttribDatiSpec(DecAttribDatiSpec decAttribDatiSpec) {
        getDecAttribDatiSpecs().add(decAttribDatiSpec);
        decAttribDatiSpec.setOrgStrut(this);

        return decAttribDatiSpec;
    }

    public DecAttribDatiSpec removeDecAttribDatiSpec(DecAttribDatiSpec decAttribDatiSpec) {
        getDecAttribDatiSpecs().remove(decAttribDatiSpec);
        decAttribDatiSpec.setOrgStrut(null);

        return decAttribDatiSpec;
    }

    @ManyToOne
    @JoinColumn(name = "ID_ENTE")
    public OrgEnte getOrgEnte() {
        return this.orgEnte;
    }

    public void setOrgEnte(OrgEnte orgEnte) {
        this.orgEnte = orgEnte;
    }

    @OneToMany(mappedBy = "orgStrut")
    public List<AroRichAnnulVer> getAroRichAnnulVers() {
        return this.aroRichAnnulVers;
    }

    public void setAroRichAnnulVers(List<AroRichAnnulVer> aroRichAnnulVers) {
        this.aroRichAnnulVers = aroRichAnnulVers;
    }

    public AroRichAnnulVer addAroRichAnnulVer(AroRichAnnulVer aroRichAnnulVer) {
        getAroRichAnnulVers().add(aroRichAnnulVer);
        aroRichAnnulVer.setOrgStrut(this);
        return aroRichAnnulVer;
    }

    public AroRichAnnulVer removeAroRichAnnulVer(AroRichAnnulVer aroRichAnnulVer) {
        getAroRichAnnulVers().remove(aroRichAnnulVer);
        aroRichAnnulVer.setOrgStrut(null);

        return aroRichAnnulVer;
    }

    // bi-directional many-to-one association to AroUnitaDoc
    private List<AroUnitaDoc> aroUnitaDocs;

    // bi-directional many-to-one association to DecAttribDatiSpec
    private List<DecAttribDatiSpec> decAttribDatiSpecs;

    // bi-directional many-to-one association to OrgEnte
    private OrgEnte orgEnte;

    // bi-directional many-to-one association to AroRichAnnulVer
    private List<AroRichAnnulVer> aroRichAnnulVers;

}
