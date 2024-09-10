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
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * The persistent class for the DIP_RICERCA database table.
 *
 */
@Entity
@Table(name = "DIP_RICERCA")
@NamedQuery(name = "DipRicerca.findAll", query = "SELECT d FROM DipRicerca d")
@NamedQuery(name = "DipRicerca.findAllFetchField", query = "SELECT d FROM DipRicerca d JOIN d.dipGruppoCampis gruppo JOIN gruppo.dipCampoRicercas campo")
public class DipRicerca implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "DIP_RICERCA_IDRICERCA_GENERATOR", sequenceName = "SDIP_RICERCA", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DIP_RICERCA_IDRICERCA_GENERATOR")
    @Column(name = "ID_RICERCA")
    private long idRicerca;

    @Column(name = "DS_RICERCA")
    private String dsRicerca;

    @Column(name = "NI_RES_LIMIT")
    private BigDecimal niResLimit;

    @Column(name = "NM_ENTRY_MENU")
    private String nmEntryMenu;

    @Column(name = "NM_RICERCA")
    private String nmRicerca;

    @Column(name = "TI_TIPO_PRESENTAZIONE")
    private String tiTipoPresentazione;

    @Column(name = "TI_TIPO_RECUPERO")
    private String tiTipoRecupero;

    @Column(name = "TI_STATO_RICERCA")
    private String tiStatoRicerca;

    // bi-directional many-to-one association to DipAttribRisultato
    @OneToMany(mappedBy = "dipRicerca")
    private List<DipAttribRisultato> dipAttribRisultatos;

    // bi-directional many-to-one association to DipGruppoDettaglio
    @OneToMany(mappedBy = "dipRicerca", fetch = FetchType.EAGER)
    @OrderBy("niRigaGruppo asc, niColonnaGruppo asc")
    private List<DipGruppoDettaglio> dipGruppoDettaglios;

    // bi-directional many-to-one association to DipCombinazioneRicerca
    @OneToMany(mappedBy = "dipRicerca")
    private List<DipCombinazioneRicerca> dipCombinazioneRicercas;

    // bi-directional many-to-one association to DipGruppoCampi
    @OneToMany(mappedBy = "dipRicerca", fetch = FetchType.EAGER)
    // @OrderBy("niRigaGruppo asc, niColonnaGruppo asc")
    private Set<DipGruppoCampi> dipGruppoCampis;

    public DipRicerca() {
    }

    public long getIdRicerca() {
        return this.idRicerca;
    }

    public void setIdRicerca(long idRicerca) {
        this.idRicerca = idRicerca;
    }

    public String getDsRicerca() {
        return this.dsRicerca;
    }

    public void setDsRicerca(String dsRicerca) {
        this.dsRicerca = dsRicerca;
    }

    public BigDecimal getNiResLimit() {
        return this.niResLimit;
    }

    public void setNiResLimit(BigDecimal niResLimit) {
        this.niResLimit = niResLimit;
    }

    public String getNmEntryMenu() {
        return this.nmEntryMenu;
    }

    public void setNmEntryMenu(String nmEntryMenu) {
        this.nmEntryMenu = nmEntryMenu;
    }

    public String getNmRicerca() {
        return this.nmRicerca;
    }

    public void setNmRicerca(String nmRicerca) {
        this.nmRicerca = nmRicerca;
    }

    public String getTiTipoPresentazione() {
        return this.tiTipoPresentazione;
    }

    public void setTiTipoPresentazione(String tiTipoPresentazione) {
        this.tiTipoPresentazione = tiTipoPresentazione;
    }

    public String getTiTipoRecupero() {
        return this.tiTipoRecupero;
    }

    public void setTiStatoRicerca(String tiStatoRicerca) {
        this.tiStatoRicerca = tiStatoRicerca;
    }

    public String getTiStatoRicerca() {
        return this.tiStatoRicerca;
    }

    public void setTiTipoRecupero(String tiTipoRecupero) {
        this.tiTipoRecupero = tiTipoRecupero;
    }

    public List<DipAttribRisultato> getDipAttribRisultatos() {
        return this.dipAttribRisultatos;
    }

    public void setDipAttribRisultatos(List<DipAttribRisultato> dipAttribRisultatos) {
        this.dipAttribRisultatos = dipAttribRisultatos;
    }

    public List<DipGruppoDettaglio> getDipGruppoDettaglios() {
        return this.dipGruppoDettaglios;
    }

    public void setDipGruppoDettaglios(List<DipGruppoDettaglio> dipGruppoDettaglios) {
        this.dipGruppoDettaglios = dipGruppoDettaglios;
    }

    public List<DipCombinazioneRicerca> getDipCombinazioneRicercas() {
        return this.dipCombinazioneRicercas;
    }

    public void setDipCombinazioneRicercas(List<DipCombinazioneRicerca> dipCombinazioneRicercas) {
        this.dipCombinazioneRicercas = dipCombinazioneRicercas;
    }

    private Set<DipGruppoCampi> getDipGruppoCampis() {
        return this.dipGruppoCampis;
    }

    public List<DipGruppoCampi> getDipGruppoCampiOrdinati() {
        Comparator<DipGruppoCampi> compareByRigaColonna = Comparator.comparing(DipGruppoCampi::getNiRigaGruppo)
                .thenComparing(DipGruppoCampi::getNiColonnaGruppo);

        List<DipGruppoCampi> sorted = new ArrayList<>(this.dipGruppoCampis).stream().sorted(compareByRigaColonna)
                .collect(Collectors.toList());
        return sorted;
    }

    public void setDipGruppoCampis(Set<DipGruppoCampi> dipGruppoCampis) {
        this.dipGruppoCampis = dipGruppoCampis;
    }

}
