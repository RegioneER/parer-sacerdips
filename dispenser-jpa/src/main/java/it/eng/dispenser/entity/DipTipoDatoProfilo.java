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
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author Iacolucci_M
 */
@Entity
@Table(name = "DIP_TIPO_DATO_PROFILO")
@NamedQuery(name = "DipTipoDatoProfilo.findByDtIstituzEqualOrSmallerAndOrigine", query = "SELECT d FROM DipTipoDatoProfilo d WHERE d.dtIstituz <= :dtIstituz AND d.tiOrigineDatoProfilo = :origine")
public class DipTipoDatoProfilo implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum TipoConversione {
        TIPO_UD, ENTE_STRUTTURA
    }

    @Id
    @Column(name = "ID_TIPO_DATO_PROFILO")
    private BigDecimal idTipoDatoProfilo;

    @Column(name = "NM_TIPO_DATO_PROFILO")
    private String nmTipoDatoProfilo;

    @Column(name = "TI_ORIGINE_DATO_PROFILO")
    private String tiOrigineDatoProfilo;

    @Column(name = "DT_ISTITUZ")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dtIstituz;

    @Column(name = "NM_COLONNA_DB")
    private String nmColonnaDb;

    @Column(name = "TI_DATO_PROFILO")
    private String tiDatoProfilo;

    @Column(name = "TIPO_CONVERSIONE")
    @Enumerated(EnumType.STRING)
    private TipoConversione tipoConversione;

    public DipTipoDatoProfilo() {
    }

    public DipTipoDatoProfilo(BigDecimal idTipoDatoProfilo) {
        this.idTipoDatoProfilo = idTipoDatoProfilo;
    }

    public DipTipoDatoProfilo(BigDecimal idTipoDatoProfilo, String nmTipoDatoProfilo, String tiOrigineDatoProfilo,
            Date dtIstituz, String nmColonnaDb, String tiDatoProfilo) {
        this.idTipoDatoProfilo = idTipoDatoProfilo;
        this.nmTipoDatoProfilo = nmTipoDatoProfilo;
        this.tiOrigineDatoProfilo = tiOrigineDatoProfilo;
        this.dtIstituz = dtIstituz;
        this.nmColonnaDb = nmColonnaDb;
        this.tiDatoProfilo = tiDatoProfilo;
    }

    public BigDecimal getIdTipoDatoProfilo() {
        return idTipoDatoProfilo;
    }

    public void setIdTipoDatoProfilo(BigDecimal idTipoDatoProfilo) {
        this.idTipoDatoProfilo = idTipoDatoProfilo;
    }

    public String getNmTipoDatoProfilo() {
        return nmTipoDatoProfilo;
    }

    public void setNmTipoDatoProfilo(String nmTipoDatoProfilo) {
        this.nmTipoDatoProfilo = nmTipoDatoProfilo;
    }

    public String getTiOrigineDatoProfilo() {
        return tiOrigineDatoProfilo;
    }

    public void setTiOrigineDatoProfilo(String tiOrigineDatoProfilo) {
        this.tiOrigineDatoProfilo = tiOrigineDatoProfilo;
    }

    public Date getDtIstituz() {
        return dtIstituz;
    }

    public void setDtIstituz(Date dtIstituz) {
        this.dtIstituz = dtIstituz;
    }

    public String getNmColonnaDb() {
        return nmColonnaDb;
    }

    public void setNmColonnaDb(String nmColonnaDb) {
        this.nmColonnaDb = nmColonnaDb;
    }

    public String getTiDatoProfilo() {
        return tiDatoProfilo;
    }

    public void setTiDatoProfilo(String tiDatoProfilo) {
        this.tiDatoProfilo = tiDatoProfilo;
    }

    public TipoConversione getTipoConversione() {
        return tipoConversione;
    }

    public void setTipoConversione(TipoConversione tipoConversione) {
        this.tipoConversione = tipoConversione;
    }

}
