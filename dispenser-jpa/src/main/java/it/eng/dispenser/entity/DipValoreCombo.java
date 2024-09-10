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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * The persistent class for the DIP_VALORE_COMBO database table.
 *
 */
@Entity
@Table(name = "DIP_VALORE_COMBO")
@NamedQuery(name = "DipValoreCombo.findAll", query = "SELECT d FROM DipValoreCombo d")
public class DipValoreCombo implements Serializable {
    private static final long serialVersionUID = 1L;
    private long idValoreCombo;
    private String cdValoreCombo;
    private String nmValoreCombo;
    private DipCampoRicerca dipCampoRicerca;

    public DipValoreCombo() {
    }

    @Id
    @SequenceGenerator(name = "DIP_VALORE_COMBO_IDVALORECOMBO_GENERATOR", sequenceName = "SDIP_VALORE_COMBO", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DIP_VALORE_COMBO_IDVALORECOMBO_GENERATOR")
    @Column(name = "ID_VALORE_COMBO")
    public long getIdValoreCombo() {
        return this.idValoreCombo;
    }

    public void setIdValoreCombo(long idValoreCombo) {
        this.idValoreCombo = idValoreCombo;
    }

    @Column(name = "CD_VALORE_COMBO")
    public String getCdValoreCombo() {
        return this.cdValoreCombo;
    }

    public void setCdValoreCombo(String cdValoreCombo) {
        this.cdValoreCombo = cdValoreCombo;
    }

    @Column(name = "NM_VALORE_COMBO")
    public String getNmValoreCombo() {
        return this.nmValoreCombo;
    }

    public void setNmValoreCombo(String nmValoreCombo) {
        this.nmValoreCombo = nmValoreCombo;
    }

    // bi-directional many-to-one association to DipCampoRicerca
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CAMPO_RICERCA")
    public DipCampoRicerca getDipCampoRicerca() {
        return this.dipCampoRicerca;
    }

    public void setDipCampoRicerca(DipCampoRicerca dipCampoRicerca) {
        this.dipCampoRicerca = dipCampoRicerca;
    }

}
