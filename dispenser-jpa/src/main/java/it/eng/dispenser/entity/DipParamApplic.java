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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * The persistent class for the DIP_PARAM_APPLIC database table.
 *
 */
@Entity
@Table(name = "DIP_PARAM_APPLIC")
public class DipParamApplic implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "DIP_PARAM_APPLIC_IDPARAMAPPLIC_GENERATOR", sequenceName = "SDIP_PARAM_APPLIC", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DIP_PARAM_APPLIC_IDPARAMAPPLIC_GENERATOR")
    @Column(name = "ID_PARAM_APPLIC")
    private long idParamApplic;

    @Column(name = "DS_PARAM_APPLIC")
    private String dsParamApplic;

    @Column(name = "DS_VALORE_PARAM_APPLIC")
    private String dsValoreParamApplic;

    @Column(name = "NM_PARAM_APPLIC")
    private String nmParamApplic;

    @Column(name = "TI_PARAM_APPLIC")
    private String tiParamApplic;

    public DipParamApplic() {
    }

    public long getIdParamApplic() {
        return this.idParamApplic;
    }

    public void setIdParamApplic(long idParamApplic) {
        this.idParamApplic = idParamApplic;
    }

    public String getDsParamApplic() {
        return this.dsParamApplic;
    }

    public void setDsParamApplic(String dsParamApplic) {
        this.dsParamApplic = dsParamApplic;
    }

    public String getDsValoreParamApplic() {
        return this.dsValoreParamApplic;
    }

    public void setDsValoreParamApplic(String dsValoreParamApplic) {
        this.dsValoreParamApplic = dsValoreParamApplic;
    }

    public String getNmParamApplic() {
        return this.nmParamApplic;
    }

    public void setNmParamApplic(String nmParamApplic) {
        this.nmParamApplic = nmParamApplic;
    }

    public String getTiParamApplic() {
        return this.tiParamApplic;
    }

    public void setTiParamApplic(String tiParamApplic) {
        this.tiParamApplic = tiParamApplic;
    }

}
