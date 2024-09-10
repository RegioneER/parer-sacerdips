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

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import it.eng.parer.jboss.timer.common.JobTable;
import it.eng.parer.jboss.timer.common.entity.JobDefinition;

/**
 * The persistent class for the DEC_JOB database table.
 *
 * @author fioravanti_f
 */
/* DEC_JOB */
@Entity
@Table(name = "DIP_DEC_JOB")
public class DipDecJob extends JobDefinition implements JobTable {

    private static final long serialVersionUID = 1L;
    private Long idJob;
    private String nmJob;
    private String tiSchedJob;
    private String tiScopoJob;
    private BigDecimal niOrdExec;
    private String nmAmbito;
    private String dsJob;

    public DipDecJob() {
        // Do nothing
    }

    @Id
    @SequenceGenerator(name = "DEC_JOB_IDJOB_GENERATOR", sequenceName = "SDIP_DEC_JOB", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DEC_JOB_IDJOB_GENERATOR")
    @Column(name = "ID_JOB")
    @Override
    public Long getIdJob() {
        return idJob;
    }

    @Override
    public void setIdJob(Long idJob) {
        this.idJob = idJob;
    }

    @Column(name = "NM_JOB")
    @Override
    public String getNmJob() {
        return nmJob;
    }

    @Override
    public void setNmJob(String nmJob) {
        this.nmJob = nmJob;
    }

    @Column(name = "DS_JOB")
    public String getDsJob() {
        return this.dsJob;
    }

    public void setDsJob(String dsJob) {
        this.dsJob = dsJob;
    }

    @Column(name = "NM_AMBITO")
    public String getNmAmbito() {
        return this.nmAmbito;
    }

    public void setNmAmbito(String nmAmbito) {
        this.nmAmbito = nmAmbito;
    }

    @Column(name = "NI_ORD_EXEC")
    public BigDecimal getNiOrdExec() {
        return this.niOrdExec;
    }

    public void setNiOrdExec(BigDecimal niOrdExec) {
        this.niOrdExec = niOrdExec;
    }

    @Column(name = "TI_SCHED_JOB")
    @Override
    public String getTiSchedJob() {
        return tiSchedJob;
    }

    @Override
    public void setTiSchedJob(String tiSchedJob) {
        this.tiSchedJob = tiSchedJob;
    }

    @Column(name = "TI_SCOPO_JOB")
    @Override
    public String getTiScopoJob() {
        return tiScopoJob;
    }

    @Override
    public void setTiScopoJob(String tiScopoJob) {
        this.tiScopoJob = tiScopoJob;
    }
}
