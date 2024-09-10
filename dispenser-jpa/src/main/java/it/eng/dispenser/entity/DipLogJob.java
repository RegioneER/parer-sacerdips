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
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * The persistent class for the DIP_LOG_JOB database table.
 *
 */
@Entity
@Table(name = "DIP_LOG_JOB")
public class DipLogJob implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "DIP_LOG_JOB_IDLOGJOB_GENERATOR", sequenceName = "SDIP_LOG_JOB", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DIP_LOG_JOB_IDLOGJOB_GENERATOR")
    @Column(name = "ID_LOG_JOB")
    private long idLogJob;

    @Column(name = "DS_ERRORE")
    private String dsErrore;

    @Column(name = "DT_EVENTO")
    private Timestamp dtEvento;

    @Column(name = "NM_JOB")
    private String nmJob;

    @Column(name = "TI_EVENTO")
    private String tiEvento;

    @Column(name = "CD_IND_SERVER")
    private String cdIndServer;

    public DipLogJob() {
    }

    public long getIdLogJob() {
        return this.idLogJob;
    }

    public void setIdLogJob(long idLogJob) {
        this.idLogJob = idLogJob;
    }

    public String getDsErrore() {
        return this.dsErrore;
    }

    public void setDsErrore(String dsErrore) {
        this.dsErrore = dsErrore;
    }

    public Timestamp getDtEvento() {
        return this.dtEvento;
    }

    public void setDtEvento(Timestamp dtEvento) {
        this.dtEvento = dtEvento;
    }

    public String getNmJob() {
        return this.nmJob;
    }

    public void setNmJob(String nmJob) {
        this.nmJob = nmJob;
    }

    public String getTiEvento() {
        return this.tiEvento;
    }

    public void setTiEvento(String tiEvento) {
        this.tiEvento = tiEvento;
    }

    public String getCdIndServer() {
        return cdIndServer;
    }

    public void setCdIndServer(String cdIndServer) {
        this.cdIndServer = cdIndServer;
    }

}
