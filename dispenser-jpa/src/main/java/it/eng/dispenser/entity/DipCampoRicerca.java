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
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * The persistent class for the DIP_CAMPO_RICERCA database table.
 *
 */
@Entity
@Table(name = "DIP_CAMPO_RICERCA")
public class DipCampoRicerca implements Serializable {
    private static final long serialVersionUID = 1L;

    private long idCampoRicerca;
    private String dsCampo;
    private BigDecimal niColonnaCampo;
    private BigDecimal niRigaCampo;
    private String nmCampo;
    private String tiDatoCampo;
    private DipGruppoCampi dipGruppoCampi;
    private List<DipFiltroRicerca> dipFiltroRicercas;
    private List<DipValoreCombo> dipValoreCombos;

    public DipCampoRicerca() {
    }

    @Id
    @SequenceGenerator(name = "DIP_CAMPO_RICERCA_IDCAMPORICERCA_GENERATOR", sequenceName = "SDIP_CAMPO_RICERCA", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DIP_CAMPO_RICERCA_IDCAMPORICERCA_GENERATOR")
    @Column(name = "ID_CAMPO_RICERCA")
    public long getIdCampoRicerca() {
        return this.idCampoRicerca;
    }

    public void setIdCampoRicerca(long idCampoRicerca) {
        this.idCampoRicerca = idCampoRicerca;
    }

    @Column(name = "DS_CAMPO")
    public String getDsCampo() {
        return this.dsCampo;
    }

    public void setDsCampo(String dsCampo) {
        this.dsCampo = dsCampo;
    }

    @Column(name = "NI_COLONNA_CAMPO")
    public BigDecimal getNiColonnaCampo() {
        return this.niColonnaCampo;
    }

    public void setNiColonnaCampo(BigDecimal niColonnaCampo) {
        this.niColonnaCampo = niColonnaCampo;
    }

    @Column(name = "NI_RIGA_CAMPO")
    public BigDecimal getNiRigaCampo() {
        return this.niRigaCampo;
    }

    public void setNiRigaCampo(BigDecimal niRigaCampo) {
        this.niRigaCampo = niRigaCampo;
    }

    @Column(name = "NM_CAMPO")
    public String getNmCampo() {
        return this.nmCampo;
    }

    public void setNmCampo(String nmCampo) {
        this.nmCampo = nmCampo;
    }

    @Column(name = "TI_DATO_CAMPO")
    public String getTiDatoCampo() {
        return this.tiDatoCampo;
    }

    public void setTiDatoCampo(String tiDatoCampo) {
        this.tiDatoCampo = tiDatoCampo;
    }

    // bi-directional many-to-one association to DipGruppoCampi
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_GRUPPO_CAMPI")
    public DipGruppoCampi getDipGruppoCampi() {
        return this.dipGruppoCampi;
    }

    public void setDipGruppoCampi(DipGruppoCampi dipGruppoCampi) {
        this.dipGruppoCampi = dipGruppoCampi;
    }

    // bi-directional many-to-one association to DipFiltroRicerca
    @OneToMany(mappedBy = "dipCampoRicerca")
    public List<DipFiltroRicerca> getDipFiltroRicercas() {
        return this.dipFiltroRicercas;
    }

    public void setDipFiltroRicercas(List<DipFiltroRicerca> dipFiltroRicercas) {
        this.dipFiltroRicercas = dipFiltroRicercas;
    }

    public DipFiltroRicerca addDipFiltroRicerca(DipFiltroRicerca dipFiltroRicerca) {
        getDipFiltroRicercas().add(dipFiltroRicerca);
        dipFiltroRicerca.setDipCampoRicerca(this);

        return dipFiltroRicerca;
    }

    public DipFiltroRicerca removeDipFiltroRicerca(DipFiltroRicerca dipFiltroRicerca) {
        getDipFiltroRicercas().remove(dipFiltroRicerca);
        dipFiltroRicerca.setDipCampoRicerca(null);

        return dipFiltroRicerca;
    }

    // bi-directional many-to-one association to DipValoreCombo
    @OneToMany(mappedBy = "dipCampoRicerca")
    public List<DipValoreCombo> getDipValoreCombos() {
        return this.dipValoreCombos;
    }

    public void setDipValoreCombos(List<DipValoreCombo> dipValoreCombos) {
        this.dipValoreCombos = dipValoreCombos;
    }

    public DipValoreCombo addDipValoreCombo(DipValoreCombo dipValoreCombo) {
        getDipValoreCombos().add(dipValoreCombo);
        dipValoreCombo.setDipCampoRicerca(this);

        return dipValoreCombo;
    }

    public DipValoreCombo removeDipValoreCombo(DipValoreCombo dipValoreCombo) {
        getDipValoreCombos().remove(dipValoreCombo);
        dipValoreCombo.setDipCampoRicerca(null);

        return dipValoreCombo;
    }

}
