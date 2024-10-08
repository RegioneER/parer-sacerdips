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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the ORG_AMBIENTE database table.
 *
 */
@Entity
@Table(name = "ORG_AMBIENTE", schema = "SACER")
public class OrgAmbiente implements Serializable {

    private static final long serialVersionUID = 1L;

    private long idAmbiente;
    private String dsAmbiente;
    private String nmAmbiente;
    private Date dtFinVal;
    private Date dtIniVal;
    private BigDecimal idEnteConserv;
    private BigDecimal idEnteGestore;
    private List<OrgEnte> orgEntes;

    public OrgAmbiente() {
    }

    @Id
    // @SequenceGenerator(name = "ORG_AMBIENTE_IDAMBIENTE_GENERATOR", sequenceName = "SORG_AMBIENTE", allocationSize =
    // 1, schema = "SACER")
    // @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ORG_AMBIENTE_IDAMBIENTE_GENERATOR")
    @Column(name = "ID_AMBIENTE")
    public long getIdAmbiente() {
        return this.idAmbiente;
    }

    public void setIdAmbiente(long idAmbiente) {
        this.idAmbiente = idAmbiente;
    }

    @Column(name = "DS_AMBIENTE")
    public String getDsAmbiente() {
        return this.dsAmbiente;
    }

    public void setDsAmbiente(String dsAmbiente) {
        this.dsAmbiente = dsAmbiente;
    }

    @Column(name = "NM_AMBIENTE")
    public String getNmAmbiente() {
        return this.nmAmbiente;
    }

    public void setNmAmbiente(String nmAmbiente) {
        this.nmAmbiente = nmAmbiente;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_FIN_VAL")
    public Date getDtFinVal() {
        return this.dtFinVal;
    }

    public void setDtFinVal(Date dtFinVal) {
        this.dtFinVal = dtFinVal;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_INI_VAL")
    public Date getDtIniVal() {
        return this.dtIniVal;
    }

    public void setDtIniVal(Date dtIniVal) {
        this.dtIniVal = dtIniVal;
    }

    @Column(name = "ID_ENTE_CONSERV")
    public java.math.BigDecimal getIdEnteConserv() {
        return this.idEnteConserv;
    }

    public void setIdEnteConserv(java.math.BigDecimal idEnteConserv) {
        this.idEnteConserv = idEnteConserv;
    }

    @Column(name = "ID_ENTE_GESTORE")
    public java.math.BigDecimal getIdEnteGestore() {
        return this.idEnteGestore;
    }

    public void setIdEnteGestore(java.math.BigDecimal idEnteGestore) {
        this.idEnteGestore = idEnteGestore;
    }

    // bi-directional many-to-one association to OrgEnte
    @OneToMany(mappedBy = "orgAmbiente")
    public List<OrgEnte> getOrgEntes() {
        return this.orgEntes;
    }

    public void setOrgEntes(List<OrgEnte> orgEntes) {
        this.orgEntes = orgEntes;
    }

    public OrgEnte addOrgEnte(OrgEnte orgEnte) {
        getOrgEntes().add(orgEnte);
        orgEnte.setOrgAmbiente(this);

        return orgEnte;
    }

    public OrgEnte removeOrgEnte(OrgEnte orgEnte) {
        getOrgEntes().remove(orgEnte);
        orgEnte.setOrgAmbiente(null);

        return orgEnte;
    }

}
