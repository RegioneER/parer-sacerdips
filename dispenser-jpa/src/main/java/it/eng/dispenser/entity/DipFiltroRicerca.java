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
 * The persistent class for the DIP_FILTRO_RICERCA database table.
 *
 */
@Entity
@Table(name = "DIP_FILTRO_RICERCA")
public class DipFiltroRicerca implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "DIP_FILTRO_RICERCA_IDFILTRORICERCA_GENERATOR", sequenceName = "SDIP_FILTRO_RICERCA", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DIP_FILTRO_RICERCA_IDFILTRORICERCA_GENERATOR")
    @Column(name = "ID_FILTRO_RICERCA")
    private long idFiltroRicerca;

    @Column(name = "FL_STATICO", columnDefinition = "char")
    private String flStatico;

    @Column(name = "ID_ATTRIB_DATI_SPEC")
    private BigDecimal idAttribDatiSpec;

    @Column(name = "NM_FILTRO_RICERCA")
    private String nmFiltroRicerca;

    @Column(name = "TI_TIPO_FILTRO")
    private String tiTipoFiltro;

    @Column(name = "TI_DATO_FILTRO")
    private String tiDatoFiltro;

    // bi-directional many-to-one association to DipCampoRicerca
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_CAMPO_RICERCA")
    private DipCampoRicerca dipCampoRicerca;

    // bi-directional many-to-one association to DipCombinazioneRicerca
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_COMBINAZIONE_RICERCA")
    private DipCombinazioneRicerca dipCombinazioneRicerca;

    // bi-directional many-to-one association to DipValorePredefinito
    @OneToMany(mappedBy = "dipFiltroRicerca")
    private List<DipValorePredefinito> dipValorePredefinitos;

    public DipFiltroRicerca() {
    }

    public long getIdFiltroRicerca() {
        return this.idFiltroRicerca;
    }

    public void setIdFiltroRicerca(long idFiltroRicerca) {
        this.idFiltroRicerca = idFiltroRicerca;
    }

    public String getFlStatico() {
        return this.flStatico;
    }

    public void setFlStatico(String flStatico) {
        this.flStatico = flStatico;
    }

    public BigDecimal getIdAttribDatiSpec() {
        return this.idAttribDatiSpec;
    }

    public void setIdAttribDatiSpec(BigDecimal idAttribDatiSpec) {
        this.idAttribDatiSpec = idAttribDatiSpec;
    }

    public String getNmFiltroRicerca() {
        return this.nmFiltroRicerca;
    }

    public void setNmFiltroRicerca(String nmFiltroRicerca) {
        this.nmFiltroRicerca = nmFiltroRicerca;
    }

    public String getTiTipoFiltro() {
        return this.tiTipoFiltro;
    }

    public void setTiTipoFiltro(String tiTipoFiltro) {
        this.tiTipoFiltro = tiTipoFiltro;
    }

    public DipCampoRicerca getDipCampoRicerca() {
        return this.dipCampoRicerca;
    }

    public void setDipCampoRicerca(DipCampoRicerca dipCampoRicerca) {
        this.dipCampoRicerca = dipCampoRicerca;
    }

    public DipCombinazioneRicerca getDipCombinazioneRicerca() {
        return this.dipCombinazioneRicerca;
    }

    public void setDipCombinazioneRicerca(DipCombinazioneRicerca dipCombinazioneRicerca) {
        this.dipCombinazioneRicerca = dipCombinazioneRicerca;
    }

    public List<DipValorePredefinito> getDipValorePredefinitos() {
        return this.dipValorePredefinitos;
    }

    public void setDipValorePredefinitos(List<DipValorePredefinito> dipValorePredefinitos) {
        this.dipValorePredefinitos = dipValorePredefinitos;
    }

    public DipValorePredefinito addDipValorePredefinito(DipValorePredefinito dipValorePredefinito) {
        getDipValorePredefinitos().add(dipValorePredefinito);
        dipValorePredefinito.setDipFiltroRicerca(this);

        return dipValorePredefinito;
    }

    public DipValorePredefinito removeDipValorePredefinito(DipValorePredefinito dipValorePredefinito) {
        getDipValorePredefinitos().remove(dipValorePredefinito);
        dipValorePredefinito.setDipFiltroRicerca(null);

        return dipValorePredefinito;
    }

    public String getTiDatoFiltro() {
        return tiDatoFiltro;
    }

    public void setTiDatoFiltro(String tiDatoFiltro) {
        this.tiDatoFiltro = tiDatoFiltro;
    }

}
