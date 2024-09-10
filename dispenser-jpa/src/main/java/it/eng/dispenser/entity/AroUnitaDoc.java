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
 * The persistent class for the ARO_UNITA_DOC database table.
 *
 */
@Entity
@Table(name = "ARO_UNITA_DOC", schema = "SACER")
public class AroUnitaDoc implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID_UNITA_DOC")
    private long idUnitaDoc;

    @Column(name = "AA_KEY_UNITA_DOC")
    private BigDecimal aaKeyUnitaDoc;

    @Column(name = "CD_FASCIC_PRINC")
    private String cdFascicPrinc;

    @Column(name = "CD_KEY_UNITA_DOC")
    private String cdKeyUnitaDoc;

    @Column(name = "CD_REGISTRO_KEY_UNITA_DOC")
    private String cdRegistroKeyUnitaDoc;

    @Column(name = "CD_SOTTOFASCIC_PRINC")
    private String cdSottofascicPrinc;

    @Column(name = "DL_OGGETTO_UNITA_DOC")
    private String dlOggettoUnitaDoc;

    @Column(name = "DS_CLASSIF_PRINC")
    private String dsClassifPrinc;

    @Column(name = "DS_KEY_ORD")
    private String dsKeyOrd;

    @Column(name = "DS_MSG_ESITO_VERIF_FIRME")
    private String dsMsgEsitoVerifFirme;

    @Column(name = "DS_OGGETTO_FASCIC_PRINC")
    private String dsOggettoFascicPrinc;

    @Column(name = "DS_OGGETTO_SOTTOFASCIC_PRINC")
    private String dsOggettoSottofascicPrinc;

    @Column(name = "DS_UFF_COMP_UNITA_DOC")
    private String dsUffCompUnitaDoc;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_ANNUL")
    private Date dtAnnul;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CREAZIONE")
    private Date dtCreazione;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_REG_UNITA_DOC")
    private Date dtRegUnitaDoc;

    @Column(name = "FL_CARTACEO", columnDefinition = "char(1)")
    private String flCartaceo;

    @Column(name = "FL_FORZA_ACCETTAZIONE", columnDefinition = "char(1)")
    private String flForzaAccettazione;

    @Column(name = "FL_FORZA_COLLEGAMENTO", columnDefinition = "char(1)")
    private String flForzaCollegamento;

    @Column(name = "FL_FORZA_CONSERVAZIONE", columnDefinition = "char(1)")
    private String flForzaConservazione;

    @Column(name = "FL_UNITA_DOC_FIRMATO", columnDefinition = "char(1)")
    private String flUnitaDocFirmato;

    @Column(name = "ID_ELENCO_VERS")
    private BigDecimal idElencoVers;

    @Column(name = "ID_REGISTRO_UNITA_DOC")
    private BigDecimal idRegistroUnitaDoc;

    @Column(name = "ID_SUB_STRUT")
    private BigDecimal idSubStrut;

    @Column(name = "ID_TIPO_UNITA_DOC")
    private BigDecimal idTipoUnitaDoc;

    @Column(name = "ID_USER_VERS")
    private BigDecimal idUserVers;

    @Column(name = "NI_ALLEG")
    private BigDecimal niAlleg;

    @Column(name = "NI_ANNESSI")
    private BigDecimal niAnnessi;

    @Column(name = "NI_ANNOT")
    private BigDecimal niAnnot;

    @Column(name = "NM_SISTEMA_MIGRAZ")
    private String nmSistemaMigraz;

    @Column(name = "NT_ANNUL")
    private String ntAnnul;

    @Column(name = "NT_UNITA_DOC")
    private String ntUnitaDoc;

    @Column(name = "PG_UNITA_DOC")
    private BigDecimal pgUnitaDoc;

    @Column(name = "TI_ANNUL")
    private String tiAnnul;

    @Column(name = "TI_CONSERVAZIONE")
    private String tiConservazione;

    @Column(name = "TI_ESITO_VERIF_FIRME")
    private String tiEsitoVerifFirme;

    @Column(name = "TI_STATO_CONSERVAZIONE")
    private String tiStatoConservazione;

    @Column(name = "TI_STATO_UD_ELENCO_VERS")
    private String tiStatoUdElencoVers;

    // bi-directional many-to-one association to AroDoc
    @OneToMany(mappedBy = "aroUnitaDoc")
    private List<AroDoc> aroDocs;

    // bi-directional many-to-one association to OrgStrut
    @ManyToOne
    @JoinColumn(name = "ID_STRUT")
    private OrgStrut orgStrut;

    // bi-directional many-to-one association to AroUsoXsdDatiSpec
    @OneToMany(mappedBy = "aroUnitaDoc")
    private List<AroUsoXsdDatiSpec> aroUsoXsdDatiSpecs;

    public AroUnitaDoc() {
    }

    public long getIdUnitaDoc() {
        return this.idUnitaDoc;
    }

    public void setIdUnitaDoc(long idUnitaDoc) {
        this.idUnitaDoc = idUnitaDoc;
    }

    public BigDecimal getAaKeyUnitaDoc() {
        return this.aaKeyUnitaDoc;
    }

    public void setAaKeyUnitaDoc(BigDecimal aaKeyUnitaDoc) {
        this.aaKeyUnitaDoc = aaKeyUnitaDoc;
    }

    public String getCdFascicPrinc() {
        return this.cdFascicPrinc;
    }

    public void setCdFascicPrinc(String cdFascicPrinc) {
        this.cdFascicPrinc = cdFascicPrinc;
    }

    public String getCdKeyUnitaDoc() {
        return this.cdKeyUnitaDoc;
    }

    public void setCdKeyUnitaDoc(String cdKeyUnitaDoc) {
        this.cdKeyUnitaDoc = cdKeyUnitaDoc;
    }

    public String getCdRegistroKeyUnitaDoc() {
        return this.cdRegistroKeyUnitaDoc;
    }

    public void setCdRegistroKeyUnitaDoc(String cdRegistroKeyUnitaDoc) {
        this.cdRegistroKeyUnitaDoc = cdRegistroKeyUnitaDoc;
    }

    public String getCdSottofascicPrinc() {
        return this.cdSottofascicPrinc;
    }

    public void setCdSottofascicPrinc(String cdSottofascicPrinc) {
        this.cdSottofascicPrinc = cdSottofascicPrinc;
    }

    public String getDlOggettoUnitaDoc() {
        return this.dlOggettoUnitaDoc;
    }

    public void setDlOggettoUnitaDoc(String dlOggettoUnitaDoc) {
        this.dlOggettoUnitaDoc = dlOggettoUnitaDoc;
    }

    public String getDsClassifPrinc() {
        return this.dsClassifPrinc;
    }

    public void setDsClassifPrinc(String dsClassifPrinc) {
        this.dsClassifPrinc = dsClassifPrinc;
    }

    public String getDsKeyOrd() {
        return this.dsKeyOrd;
    }

    public void setDsKeyOrd(String dsKeyOrd) {
        this.dsKeyOrd = dsKeyOrd;
    }

    public String getDsMsgEsitoVerifFirme() {
        return this.dsMsgEsitoVerifFirme;
    }

    public void setDsMsgEsitoVerifFirme(String dsMsgEsitoVerifFirme) {
        this.dsMsgEsitoVerifFirme = dsMsgEsitoVerifFirme;
    }

    public String getDsOggettoFascicPrinc() {
        return this.dsOggettoFascicPrinc;
    }

    public void setDsOggettoFascicPrinc(String dsOggettoFascicPrinc) {
        this.dsOggettoFascicPrinc = dsOggettoFascicPrinc;
    }

    public String getDsOggettoSottofascicPrinc() {
        return this.dsOggettoSottofascicPrinc;
    }

    public void setDsOggettoSottofascicPrinc(String dsOggettoSottofascicPrinc) {
        this.dsOggettoSottofascicPrinc = dsOggettoSottofascicPrinc;
    }

    public String getDsUffCompUnitaDoc() {
        return this.dsUffCompUnitaDoc;
    }

    public void setDsUffCompUnitaDoc(String dsUffCompUnitaDoc) {
        this.dsUffCompUnitaDoc = dsUffCompUnitaDoc;
    }

    public Date getDtAnnul() {
        return this.dtAnnul;
    }

    public void setDtAnnul(Date dtAnnul) {
        this.dtAnnul = dtAnnul;
    }

    public Date getDtCreazione() {
        return this.dtCreazione;
    }

    public void setDtCreazione(Date dtCreazione) {
        this.dtCreazione = dtCreazione;
    }

    public Date getDtRegUnitaDoc() {
        return this.dtRegUnitaDoc;
    }

    public void setDtRegUnitaDoc(Date dtRegUnitaDoc) {
        this.dtRegUnitaDoc = dtRegUnitaDoc;
    }

    public String getFlCartaceo() {
        return this.flCartaceo;
    }

    public void setFlCartaceo(String flCartaceo) {
        this.flCartaceo = flCartaceo;
    }

    public String getFlForzaAccettazione() {
        return this.flForzaAccettazione;
    }

    public void setFlForzaAccettazione(String flForzaAccettazione) {
        this.flForzaAccettazione = flForzaAccettazione;
    }

    public String getFlForzaCollegamento() {
        return this.flForzaCollegamento;
    }

    public void setFlForzaCollegamento(String flForzaCollegamento) {
        this.flForzaCollegamento = flForzaCollegamento;
    }

    public String getFlForzaConservazione() {
        return this.flForzaConservazione;
    }

    public void setFlForzaConservazione(String flForzaConservazione) {
        this.flForzaConservazione = flForzaConservazione;
    }

    public String getFlUnitaDocFirmato() {
        return this.flUnitaDocFirmato;
    }

    public void setFlUnitaDocFirmato(String flUnitaDocFirmato) {
        this.flUnitaDocFirmato = flUnitaDocFirmato;
    }

    public BigDecimal getIdElencoVers() {
        return this.idElencoVers;
    }

    public void setIdElencoVers(BigDecimal idElencoVers) {
        this.idElencoVers = idElencoVers;
    }

    public BigDecimal getIdRegistroUnitaDoc() {
        return this.idRegistroUnitaDoc;
    }

    public void setIdRegistroUnitaDoc(BigDecimal idRegistroUnitaDoc) {
        this.idRegistroUnitaDoc = idRegistroUnitaDoc;
    }

    public BigDecimal getIdSubStrut() {
        return this.idSubStrut;
    }

    public void setIdSubStrut(BigDecimal idSubStrut) {
        this.idSubStrut = idSubStrut;
    }

    public BigDecimal getIdTipoUnitaDoc() {
        return this.idTipoUnitaDoc;
    }

    public void setIdTipoUnitaDoc(BigDecimal idTipoUnitaDoc) {
        this.idTipoUnitaDoc = idTipoUnitaDoc;
    }

    public BigDecimal getIdUserVers() {
        return this.idUserVers;
    }

    public void setIdUserVers(BigDecimal idUserVers) {
        this.idUserVers = idUserVers;
    }

    public BigDecimal getNiAlleg() {
        return this.niAlleg;
    }

    public void setNiAlleg(BigDecimal niAlleg) {
        this.niAlleg = niAlleg;
    }

    public BigDecimal getNiAnnessi() {
        return this.niAnnessi;
    }

    public void setNiAnnessi(BigDecimal niAnnessi) {
        this.niAnnessi = niAnnessi;
    }

    public BigDecimal getNiAnnot() {
        return this.niAnnot;
    }

    public void setNiAnnot(BigDecimal niAnnot) {
        this.niAnnot = niAnnot;
    }

    public String getNmSistemaMigraz() {
        return this.nmSistemaMigraz;
    }

    public void setNmSistemaMigraz(String nmSistemaMigraz) {
        this.nmSistemaMigraz = nmSistemaMigraz;
    }

    public String getNtAnnul() {
        return this.ntAnnul;
    }

    public void setNtAnnul(String ntAnnul) {
        this.ntAnnul = ntAnnul;
    }

    public String getNtUnitaDoc() {
        return this.ntUnitaDoc;
    }

    public void setNtUnitaDoc(String ntUnitaDoc) {
        this.ntUnitaDoc = ntUnitaDoc;
    }

    public BigDecimal getPgUnitaDoc() {
        return this.pgUnitaDoc;
    }

    public void setPgUnitaDoc(BigDecimal pgUnitaDoc) {
        this.pgUnitaDoc = pgUnitaDoc;
    }

    public String getTiAnnul() {
        return this.tiAnnul;
    }

    public void setTiAnnul(String tiAnnul) {
        this.tiAnnul = tiAnnul;
    }

    public String getTiConservazione() {
        return this.tiConservazione;
    }

    public void setTiConservazione(String tiConservazione) {
        this.tiConservazione = tiConservazione;
    }

    public String getTiEsitoVerifFirme() {
        return this.tiEsitoVerifFirme;
    }

    public void setTiEsitoVerifFirme(String tiEsitoVerifFirme) {
        this.tiEsitoVerifFirme = tiEsitoVerifFirme;
    }

    public String getTiStatoConservazione() {
        return this.tiStatoConservazione;
    }

    public void setTiStatoConservazione(String tiStatoConservazione) {
        this.tiStatoConservazione = tiStatoConservazione;
    }

    public String getTiStatoUdElencoVers() {
        return this.tiStatoUdElencoVers;
    }

    public void setTiStatoUdElencoVers(String tiStatoUdElencoVers) {
        this.tiStatoUdElencoVers = tiStatoUdElencoVers;
    }

    public List<AroDoc> getAroDocs() {
        return this.aroDocs;
    }

    public void setAroDocs(List<AroDoc> aroDocs) {
        this.aroDocs = aroDocs;
    }

    public AroDoc addAroDoc(AroDoc aroDoc) {
        getAroDocs().add(aroDoc);
        aroDoc.setAroUnitaDoc(this);

        return aroDoc;
    }

    public AroDoc removeAroDoc(AroDoc aroDoc) {
        getAroDocs().remove(aroDoc);
        aroDoc.setAroUnitaDoc(null);

        return aroDoc;
    }

    public OrgStrut getOrgStrut() {
        return this.orgStrut;
    }

    public void setOrgStrut(OrgStrut orgStrut) {
        this.orgStrut = orgStrut;
    }

    public List<AroUsoXsdDatiSpec> getAroUsoXsdDatiSpecs() {
        return this.aroUsoXsdDatiSpecs;
    }

    public void setAroUsoXsdDatiSpecs(List<AroUsoXsdDatiSpec> aroUsoXsdDatiSpecs) {
        this.aroUsoXsdDatiSpecs = aroUsoXsdDatiSpecs;
    }

    public AroUsoXsdDatiSpec addAroUsoXsdDatiSpec(AroUsoXsdDatiSpec aroUsoXsdDatiSpec) {
        getAroUsoXsdDatiSpecs().add(aroUsoXsdDatiSpec);
        aroUsoXsdDatiSpec.setAroUnitaDoc(this);

        return aroUsoXsdDatiSpec;
    }

    public AroUsoXsdDatiSpec removeAroUsoXsdDatiSpec(AroUsoXsdDatiSpec aroUsoXsdDatiSpec) {
        getAroUsoXsdDatiSpecs().remove(aroUsoXsdDatiSpec);
        aroUsoXsdDatiSpec.setAroUnitaDoc(null);

        return aroUsoXsdDatiSpec;
    }

}
