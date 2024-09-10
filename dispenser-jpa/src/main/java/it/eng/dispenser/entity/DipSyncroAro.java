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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the DIP_SYNCRO_ARO database table.
 *
 */
@Entity
@Table(name = "DIP_SYNCRO_ARO")
public class DipSyncroAro implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "DIP_SYNCRO_ARO_IDSYNCROARO_GENERATOR", sequenceName = "SDIP_SYNCRO_ARO", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DIP_SYNCRO_ARO_IDSYNCROARO_GENERATOR")
    @Column(name = "ID_SYNCRO_ARO")
    private long idSyncroAro;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_ULTIMA_DATA_ELAB")
    private Date dtUltimaDataElab;

    @Column(name = "ID_STRUT")
    private BigDecimal idStrut;

    @Column(name = "NI_DOC_ANNUL")
    private BigDecimal niDocAnnul;

    @Column(name = "NI_DOC_SINCRO")
    private BigDecimal niDocSincro;

    @Column(name = "NI_UD_ANNUL")
    private BigDecimal niUdAnnul;

    @Column(name = "NI_UD_SINCRO")
    private BigDecimal niUdSincro;

    public DipSyncroAro() {
    }

    public long getIdSyncroAro() {
        return this.idSyncroAro;
    }

    public void setIdSyncroAro(long idSyncroAro) {
        this.idSyncroAro = idSyncroAro;
    }

    public Date getDtUltimaDataElab() {
        return this.dtUltimaDataElab;
    }

    public void setDtUltimaDataElab(Date dtUltimaDataElab) {
        this.dtUltimaDataElab = dtUltimaDataElab;
    }

    public BigDecimal getIdStrut() {
        return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
        this.idStrut = idStrut;
    }

    public BigDecimal getNiDocAnnul() {
        return this.niDocAnnul;
    }

    public void setNiDocAnnul(BigDecimal niDocAnnul) {
        this.niDocAnnul = niDocAnnul;
    }

    public BigDecimal getNiDocSincro() {
        return this.niDocSincro;
    }

    public void setNiDocSincro(BigDecimal niDocSincro) {
        this.niDocSincro = niDocSincro;
    }

    public BigDecimal getNiUdAnnul() {
        return this.niUdAnnul;
    }

    public void setNiUdAnnul(BigDecimal niUdAnnul) {
        this.niUdAnnul = niUdAnnul;
    }

    public BigDecimal getNiUdSincro() {
        return this.niUdSincro;
    }

    public void setNiUdSincro(BigDecimal niUdSincro) {
        this.niUdSincro = niUdSincro;
    }

}
