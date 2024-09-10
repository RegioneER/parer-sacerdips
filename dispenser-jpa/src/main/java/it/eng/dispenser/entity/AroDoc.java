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
 * The persistent class for the ARO_DOC database table.
 *
 */
@Entity
@Table(name = "ARO_DOC", schema = "SACER")
public class AroDoc implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    // @SequenceGenerator(name = "ARO_DOC_IDDOC_GENERATOR", sequenceName = "SARO_DOC", schema = "SACER", allocationSize
    // = 1)
    // @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ARO_DOC_IDDOC_GENERATOR")
    @Column(name = "ID_DOC")
    private long idDoc;

    @Column(name = "CD_KEY_DOC_VERS")
    private String cdKeyDocVers;

    @Column(name = "DL_DOC")
    private String dlDoc;

    @Column(name = "DS_AUTORE_DOC")
    private String dsAutoreDoc;

    @Column(name = "DS_MSG_ESITO_VERIF_FIRME")
    private String dsMsgEsitoVerifFirme;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_ANNUL")
    private Date dtAnnul;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CREAZIONE")
    private Date dtCreazione;

    @Column(name = "FL_DOC_FIRMATO", columnDefinition = "char(1)")
    private String flDocFirmato;

    @Column(name = "FL_DOC_FISC", columnDefinition = "char(1)")
    private String flDocFisc;

    @Column(name = "FL_FORZA_ACCETTAZIONE", columnDefinition = "char(1)")
    private String flForzaAccettazione;

    @Column(name = "FL_FORZA_CONSERVAZIONE", columnDefinition = "char(1)")
    private String flForzaConservazione;

    @Column(name = "ID_ELENCO_VERS")
    private BigDecimal idElencoVers;

    @Column(name = "ID_STRUT")
    private BigDecimal idStrut;

    @Column(name = "ID_TIPO_DOC")
    private BigDecimal idTipoDoc;

    @Column(name = "NM_SISTEMA_MIGRAZ")
    private String nmSistemaMigraz;

    @Column(name = "NT_ANNUL")
    private String ntAnnul;

    @Column(name = "NT_DOC")
    private String ntDoc;

    @Column(name = "PG_DOC")
    private BigDecimal pgDoc;

    @Column(name = "TI_ANNUL")
    private String tiAnnul;

    @Column(name = "TI_CONSERVAZIONE")
    private String tiConservazione;

    @Column(name = "TI_CREAZIONE")
    private String tiCreazione;

    @Column(name = "TI_DOC")
    private String tiDoc;

    @Column(name = "TI_ESITO_VERIF_FIRME")
    private String tiEsitoVerifFirme;

    @Column(name = "TI_STATO_DOC")
    private String tiStatoDoc;

    @Column(name = "TI_STATO_DOC_ELENCO_VERS")
    private String tiStatoDocElencoVers;

    // bi-directional many-to-one association to AroUnitaDoc
    @ManyToOne
    @JoinColumn(name = "ID_UNITA_DOC")
    private AroUnitaDoc aroUnitaDoc;

    // bi-directional many-to-one association to AroUsoXsdDatiSpec
    @OneToMany(mappedBy = "aroDoc")
    private List<AroUsoXsdDatiSpec> aroUsoXsdDatiSpecs;

    public AroDoc() {
    }

    public long getIdDoc() {
        return this.idDoc;
    }

    public void setIdDoc(long idDoc) {
        this.idDoc = idDoc;
    }

    public String getCdKeyDocVers() {
        return this.cdKeyDocVers;
    }

    public void setCdKeyDocVers(String cdKeyDocVers) {
        this.cdKeyDocVers = cdKeyDocVers;
    }

    public String getDlDoc() {
        return this.dlDoc;
    }

    public void setDlDoc(String dlDoc) {
        this.dlDoc = dlDoc;
    }

    public String getDsAutoreDoc() {
        return this.dsAutoreDoc;
    }

    public void setDsAutoreDoc(String dsAutoreDoc) {
        this.dsAutoreDoc = dsAutoreDoc;
    }

    public String getDsMsgEsitoVerifFirme() {
        return this.dsMsgEsitoVerifFirme;
    }

    public void setDsMsgEsitoVerifFirme(String dsMsgEsitoVerifFirme) {
        this.dsMsgEsitoVerifFirme = dsMsgEsitoVerifFirme;
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

    public String getFlDocFirmato() {
        return this.flDocFirmato;
    }

    public void setFlDocFirmato(String flDocFirmato) {
        this.flDocFirmato = flDocFirmato;
    }

    public String getFlDocFisc() {
        return this.flDocFisc;
    }

    public void setFlDocFisc(String flDocFisc) {
        this.flDocFisc = flDocFisc;
    }

    public String getFlForzaAccettazione() {
        return this.flForzaAccettazione;
    }

    public void setFlForzaAccettazione(String flForzaAccettazione) {
        this.flForzaAccettazione = flForzaAccettazione;
    }

    public String getFlForzaConservazione() {
        return this.flForzaConservazione;
    }

    public void setFlForzaConservazione(String flForzaConservazione) {
        this.flForzaConservazione = flForzaConservazione;
    }

    public BigDecimal getIdElencoVers() {
        return this.idElencoVers;
    }

    public void setIdElencoVers(BigDecimal idElencoVers) {
        this.idElencoVers = idElencoVers;
    }

    public BigDecimal getIdStrut() {
        return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
        this.idStrut = idStrut;
    }

    public BigDecimal getIdTipoDoc() {
        return this.idTipoDoc;
    }

    public void setIdTipoDoc(BigDecimal idTipoDoc) {
        this.idTipoDoc = idTipoDoc;
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

    public String getNtDoc() {
        return this.ntDoc;
    }

    public void setNtDoc(String ntDoc) {
        this.ntDoc = ntDoc;
    }

    public BigDecimal getPgDoc() {
        return this.pgDoc;
    }

    public void setPgDoc(BigDecimal pgDoc) {
        this.pgDoc = pgDoc;
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

    public String getTiCreazione() {
        return this.tiCreazione;
    }

    public void setTiCreazione(String tiCreazione) {
        this.tiCreazione = tiCreazione;
    }

    public String getTiDoc() {
        return this.tiDoc;
    }

    public void setTiDoc(String tiDoc) {
        this.tiDoc = tiDoc;
    }

    public String getTiEsitoVerifFirme() {
        return this.tiEsitoVerifFirme;
    }

    public void setTiEsitoVerifFirme(String tiEsitoVerifFirme) {
        this.tiEsitoVerifFirme = tiEsitoVerifFirme;
    }

    public String getTiStatoDoc() {
        return this.tiStatoDoc;
    }

    public void setTiStatoDoc(String tiStatoDoc) {
        this.tiStatoDoc = tiStatoDoc;
    }

    public String getTiStatoDocElencoVers() {
        return this.tiStatoDocElencoVers;
    }

    public void setTiStatoDocElencoVers(String tiStatoDocElencoVers) {
        this.tiStatoDocElencoVers = tiStatoDocElencoVers;
    }

    public AroUnitaDoc getAroUnitaDoc() {
        return this.aroUnitaDoc;
    }

    public void setAroUnitaDoc(AroUnitaDoc aroUnitaDoc) {
        this.aroUnitaDoc = aroUnitaDoc;
    }

    public List<AroUsoXsdDatiSpec> getAroUsoXsdDatiSpecs() {
        return this.aroUsoXsdDatiSpecs;
    }

    public void setAroUsoXsdDatiSpecs(List<AroUsoXsdDatiSpec> aroUsoXsdDatiSpecs) {
        this.aroUsoXsdDatiSpecs = aroUsoXsdDatiSpecs;
    }

    public AroUsoXsdDatiSpec addAroUsoXsdDatiSpec(AroUsoXsdDatiSpec aroUsoXsdDatiSpec) {
        getAroUsoXsdDatiSpecs().add(aroUsoXsdDatiSpec);
        aroUsoXsdDatiSpec.setAroDoc(this);

        return aroUsoXsdDatiSpec;
    }

    public AroUsoXsdDatiSpec removeAroUsoXsdDatiSpec(AroUsoXsdDatiSpec aroUsoXsdDatiSpec) {
        getAroUsoXsdDatiSpecs().remove(aroUsoXsdDatiSpec);
        aroUsoXsdDatiSpec.setAroDoc(null);

        return aroUsoXsdDatiSpec;
    }

}
