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
 * The persistent class for the ORG_ENTE database table.
 *
 */
@Entity
@Table(name = "ORG_ENTE", schema = "SACER")
public class OrgEnte implements Serializable {

    private static final long serialVersionUID = 1L;

    private long idEnte;
    private String dsEnte;
    private BigDecimal idCategEnte;
    private String nmEnte;
    private String cdEnteNormaliz;
    private String tipoDefTemplateEnte;
    private Date dtFinValAppartAmbiente;
    private Date dtIniValAppartAmbiente;

    private OrgAmbiente orgAmbiente;
    private List<OrgStrut> orgStruts;

    public OrgEnte() {
    }

    @Id
    // @SequenceGenerator(name = "ORG_ENTE_IDENTE_GENERATOR", sequenceName = "SORG_ENTE", allocationSize = 1, schema =
    // "SACER")
    // @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ORG_ENTE_IDENTE_GENERATOR")
    @Column(name = "ID_ENTE")
    public long getIdEnte() {
        return this.idEnte;
    }

    public void setIdEnte(long idEnte) {
        this.idEnte = idEnte;
    }

    @Column(name = "DS_ENTE")
    public String getDsEnte() {
        return this.dsEnte;
    }

    public void setDsEnte(String dsEnte) {
        this.dsEnte = dsEnte;
    }

    @Column(name = "ID_CATEG_ENTE")
    public BigDecimal getIdCategEnte() {
        return this.idCategEnte;
    }

    public void setIdCategEnte(BigDecimal idCategEnte) {
        this.idCategEnte = idCategEnte;
    }

    @Column(name = "NM_ENTE")
    public String getNmEnte() {
        return this.nmEnte;
    }

    public void setNmEnte(String nmEnte) {
        this.nmEnte = nmEnte;
    }

    @Column(name = "CD_ENTE_NORMALIZ")
    public String getCdEnteNormaliz() {
        return this.cdEnteNormaliz;
    }

    public void setCdEnteNormaliz(String cdEnteNormaliz) {
        this.cdEnteNormaliz = cdEnteNormaliz;
    }

    @Column(name = "TIPO_DEF_TEMPLATE_ENTE")
    public String getTipoDefTemplateEnte() {
        return this.tipoDefTemplateEnte;
    }

    public void setTipoDefTemplateEnte(String tipoDefTemplateEnte) {
        this.tipoDefTemplateEnte = tipoDefTemplateEnte;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_FIN_VAL_APPART_AMBIENTE")
    public Date getDtFinValAppartAmbiente() {
        return this.dtFinValAppartAmbiente;
    }

    public void setDtFinValAppartAmbiente(Date dtFinValAppartAmbiente) {
        this.dtFinValAppartAmbiente = dtFinValAppartAmbiente;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_INI_VAL_APPART_AMBIENTE")
    public Date getDtIniValAppartAmbiente() {
        return this.dtIniValAppartAmbiente;
    }

    public void setDtIniValAppartAmbiente(Date dtIniValAppartAmbiente) {
        this.dtIniValAppartAmbiente = dtIniValAppartAmbiente;
    }

    // bi-directional many-to-one association to OrgAmbiente
    @ManyToOne
    @JoinColumn(name = "ID_AMBIENTE")
    public OrgAmbiente getOrgAmbiente() {
        return this.orgAmbiente;
    }

    public void setOrgAmbiente(OrgAmbiente orgAmbiente) {
        this.orgAmbiente = orgAmbiente;
    }

    // bi-directional many-to-one association to OrgStrut
    @OneToMany(mappedBy = "orgEnte")
    public List<OrgStrut> getOrgStruts() {
        return this.orgStruts;
    }

    public void setOrgStruts(List<OrgStrut> orgStruts) {
        this.orgStruts = orgStruts;
    }

    public OrgStrut addOrgStrut(OrgStrut orgStrut) {
        getOrgStruts().add(orgStrut);
        orgStrut.setOrgEnte(this);

        return orgStrut;
    }

    public OrgStrut removeOrgStrut(OrgStrut orgStrut) {
        getOrgStruts().remove(orgStrut);
        orgStrut.setOrgEnte(null);

        return orgStrut;
    }

}
