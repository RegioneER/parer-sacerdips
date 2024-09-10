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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the VRS_SESSIONE_VERS database table.
 *
 */
@Entity
@Table(name = "VRS_SESSIONE_VERS", schema = "SACER")
public class VrsSessioneVers implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "ID_SESSIONE_VERS")
    private long idSessioneVers;

    @Column(name = "ID_UNITA_DOC")
    private long idUnitaDoc;

    @Column(name = "ID_STRUT")
    private long idStrut;

    @Column(name = "TI_SESSIONE_VERS")
    private String tiSessioneVers;

    @Column(name = "TI_STATO_SESSIONE_VERS")
    private String tiStatoSessioneVers;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_APERTURA")
    private Date dtApertura;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_CHIUSURA")
    private Date dtChiusura;

    public VrsSessioneVers() {
    }

    public long getIdSessioneVers() {
        return idSessioneVers;
    }

    public void setIdSessioneVers(long idSessioneVers) {
        this.idSessioneVers = idSessioneVers;
    }

    public long getIdUnitaDoc() {
        return idUnitaDoc;
    }

    public void setIdUnitaDoc(long idUnitaDoc) {
        this.idUnitaDoc = idUnitaDoc;
    }

    public long getIdStrut() {
        return idStrut;
    }

    public void setIdStrut(long idStrut) {
        this.idStrut = idStrut;
    }

    public Date getDtApertura() {
        return dtApertura;
    }

    public void setDtApertura(Date dtApertura) {
        this.dtApertura = dtApertura;
    }

    public Date getDtChiusura() {
        return dtChiusura;
    }

    public void setDtChiusura(Date dtChiusura) {
        this.dtChiusura = dtChiusura;
    }

    public String getTiSessioneVers() {
        return tiSessioneVers;
    }

    public void setTiSessioneVers(String tiSessioneVers) {
        this.tiSessioneVers = tiSessioneVers;
    }

    public String getTiStatoSessioneVers() {
        return tiStatoSessioneVers;
    }

    public void setTiStatoSessioneVers(String tiStatoSessioneVers) {
        this.tiStatoSessioneVers = tiStatoSessioneVers;
    }

}
