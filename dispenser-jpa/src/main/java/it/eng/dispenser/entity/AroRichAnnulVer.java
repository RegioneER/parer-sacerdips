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
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the ARO_RICH_ANNUL_VERS database table.
 *
 */
@Entity
@Table(name = "ARO_RICH_ANNUL_VERS", schema = "SACER")
public class AroRichAnnulVer implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    // @SequenceGenerator(name = "ARO_RICH_ANNUL_VERS_IDRICHANNULVERS_GENERATOR", sequenceName = "SARO_RICH_ANNUL_VERS",
    // schema = "SACER")
    // @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ARO_RICH_ANNUL_VERS_IDRICHANNULVERS_GENERATOR")
    @Column(name = "ID_RICH_ANNUL_VERS")
    private long idRichAnnulVers;

    @Column(name = "CD_RICH_ANNUL_VERS")
    private String cdRichAnnulVers;

    @Column(name = "DS_RICH_ANNUL_VERS")
    private String dsRichAnnulVers;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CREAZIONE_RICH_ANNUL_VERS")
    private Date dtCreazioneRichAnnulVers;

    @Column(name = "FL_IMMEDIATA", columnDefinition = "char(1)")
    private String flImmediata;

    /* Attributo inserito a mano per ovviare alla mancanza della Foreign key */
    // bi-directional one-to-one association to AroStatoRichAnnulVer
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_STATO_RICH_ANNUL_VERS_COR")
    private AroStatoRichAnnulVer aroStatoRichAnnulVersCor;

    /*
     * @Column(name="ID_STATO_RICH_ANNUL_VERS_COR") private BigDecimal idStatoRichAnnulVersCor;
     */
    @Column(name = "NT_RICH_ANNUL_VERS")
    private String ntRichAnnulVers;

    @Column(name = "TI_CREAZIONE_RICH_ANNUL_VERS")
    private String tiCreazioneRichAnnulVers;

    // bi-directional many-to-one association to AroItemRichAnnulVer
    @OneToMany(mappedBy = "aroRichAnnulVer")
    private List<AroItemRichAnnulVer> aroItemRichAnnulVers;

    // bi-directional many-to-one association to OrgStrut
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_STRUT")
    private OrgStrut orgStrut;

    // bi-directional many-to-one association to AroStatoRichAnnulVer
    @OneToMany(mappedBy = "aroRichAnnulVer")
    private List<AroStatoRichAnnulVer> aroStatoRichAnnulVers;

    public AroRichAnnulVer() {
    }

    public long getIdRichAnnulVers() {
        return this.idRichAnnulVers;
    }

    public void setIdRichAnnulVers(long idRichAnnulVers) {
        this.idRichAnnulVers = idRichAnnulVers;
    }

    public String getCdRichAnnulVers() {
        return this.cdRichAnnulVers;
    }

    public void setCdRichAnnulVers(String cdRichAnnulVers) {
        this.cdRichAnnulVers = cdRichAnnulVers;
    }

    public String getDsRichAnnulVers() {
        return this.dsRichAnnulVers;
    }

    public void setDsRichAnnulVers(String dsRichAnnulVers) {
        this.dsRichAnnulVers = dsRichAnnulVers;
    }

    public Date getDtCreazioneRichAnnulVers() {
        return this.dtCreazioneRichAnnulVers;
    }

    public void setDtCreazioneRichAnnulVers(Date dtCreazioneRichAnnulVers) {
        this.dtCreazioneRichAnnulVers = dtCreazioneRichAnnulVers;
    }

    public String getFlImmediata() {
        return this.flImmediata;
    }

    public void setFlImmediata(String flImmediata) {
        this.flImmediata = flImmediata;
    }

    /*
     * public BigDecimal getIdStatoRichAnnulVersCor() { return this.idStatoRichAnnulVersCor; }
     *
     * public void setIdStatoRichAnnulVersCor(BigDecimal idStatoRichAnnulVersCor) { this.idStatoRichAnnulVersCor =
     * idStatoRichAnnulVersCor; }
     */
    /* Metodi inseriti a mano per ovviare alla mancanza della Foreign key */
    public AroStatoRichAnnulVer getAroStatoRichAnnulVersCor() {
        return this.aroStatoRichAnnulVersCor;
    }

    public void setAroStatoRichAnnulVersCor(AroStatoRichAnnulVer aroStatoRichAnnulVer) {
        this.aroStatoRichAnnulVersCor = aroStatoRichAnnulVer;
    }

    public String getNtRichAnnulVers() {
        return this.ntRichAnnulVers;
    }

    public void setNtRichAnnulVers(String ntRichAnnulVers) {
        this.ntRichAnnulVers = ntRichAnnulVers;
    }

    public String getTiCreazioneRichAnnulVers() {
        return this.tiCreazioneRichAnnulVers;
    }

    public void setTiCreazioneRichAnnulVers(String tiCreazioneRichAnnulVers) {
        this.tiCreazioneRichAnnulVers = tiCreazioneRichAnnulVers;
    }

    public List<AroItemRichAnnulVer> getAroItemRichAnnulVers() {
        return this.aroItemRichAnnulVers;
    }

    public void setAroItemRichAnnulVers(List<AroItemRichAnnulVer> aroItemRichAnnulVers) {
        this.aroItemRichAnnulVers = aroItemRichAnnulVers;
    }

    public AroItemRichAnnulVer addAroItemRichAnnulVer(AroItemRichAnnulVer aroItemRichAnnulVer) {
        getAroItemRichAnnulVers().add(aroItemRichAnnulVer);
        aroItemRichAnnulVer.setAroRichAnnulVer(this);

        return aroItemRichAnnulVer;
    }

    public AroItemRichAnnulVer removeAroItemRichAnnulVer(AroItemRichAnnulVer aroItemRichAnnulVer) {
        getAroItemRichAnnulVers().remove(aroItemRichAnnulVer);
        aroItemRichAnnulVer.setAroRichAnnulVer(null);

        return aroItemRichAnnulVer;
    }

    public OrgStrut getOrgStrut() {
        return this.orgStrut;
    }

    public void setOrgStrut(OrgStrut orgStrut) {
        this.orgStrut = orgStrut;
    }

    public List<AroStatoRichAnnulVer> getAroStatoRichAnnulVers() {
        return this.aroStatoRichAnnulVers;
    }

    public void setAroStatoRichAnnulVers(List<AroStatoRichAnnulVer> aroStatoRichAnnulVers) {
        this.aroStatoRichAnnulVers = aroStatoRichAnnulVers;
    }

    public AroStatoRichAnnulVer addAroStatoRichAnnulVer(AroStatoRichAnnulVer aroStatoRichAnnulVer) {
        getAroStatoRichAnnulVers().add(aroStatoRichAnnulVer);
        aroStatoRichAnnulVer.setAroRichAnnulVer(this);

        return aroStatoRichAnnulVer;
    }

    public AroStatoRichAnnulVer removeAroStatoRichAnnulVer(AroStatoRichAnnulVer aroStatoRichAnnulVer) {
        getAroStatoRichAnnulVers().remove(aroStatoRichAnnulVer);
        aroStatoRichAnnulVer.setAroRichAnnulVer(null);

        return aroStatoRichAnnulVer;
    }

}
