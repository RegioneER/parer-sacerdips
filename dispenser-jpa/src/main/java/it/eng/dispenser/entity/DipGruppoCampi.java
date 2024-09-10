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
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * The persistent class for the DIP_GRUPPO_CAMPI database table.
 *
 */
@Entity
@Table(name = "DIP_GRUPPO_CAMPI")
public class DipGruppoCampi implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "DIP_GRUPPO_CAMPI_IDGRUPPOCAMPI_GENERATOR", sequenceName = "SDIP_GRUPPO_CAMPI", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DIP_GRUPPO_CAMPI_IDGRUPPOCAMPI_GENERATOR")
    @Column(name = "ID_GRUPPO_CAMPI")
    private long idGruppoCampi;

    @Column(name = "DS_GRUPPO")
    private String dsGruppo;

    @Column(name = "NI_COLONNA_GRUPPO")
    private BigDecimal niColonnaGruppo;

    @Column(name = "NI_RIGA_GRUPPO")
    private BigDecimal niRigaGruppo;

    @Column(name = "NM_GRUPPO")
    private String nmGruppo;

    // bi-directional many-to-one association to DipCampoRicerca
    @OneToMany(mappedBy = "dipGruppoCampi", fetch = FetchType.EAGER)
    // @OrderBy("niRigaCampo, niColonnaCampo")
    private Set<DipCampoRicerca> dipCampoRicercas;

    // bi-directional many-to-one association to DipRicerca
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_RICERCA")
    private DipRicerca dipRicerca;

    public DipGruppoCampi() {
    }

    public long getIdGruppoCampi() {
        return this.idGruppoCampi;
    }

    public void setIdGruppoCampi(long idGruppoCampi) {
        this.idGruppoCampi = idGruppoCampi;
    }

    public String getDsGruppo() {
        return this.dsGruppo;
    }

    public void setDsGruppo(String dsGruppo) {
        this.dsGruppo = dsGruppo;
    }

    public BigDecimal getNiColonnaGruppo() {
        return this.niColonnaGruppo;
    }

    public void setNiColonnaGruppo(BigDecimal niColonnaGruppo) {
        this.niColonnaGruppo = niColonnaGruppo;
    }

    public BigDecimal getNiRigaGruppo() {
        return this.niRigaGruppo;
    }

    public void setNiRigaGruppo(BigDecimal niRigaGruppo) {
        this.niRigaGruppo = niRigaGruppo;
    }

    public String getNmGruppo() {
        return this.nmGruppo;
    }

    public void setNmGruppo(String nmGruppo) {
        this.nmGruppo = nmGruppo;
    }

    private Set<DipCampoRicerca> getDipCampoRicercas() {
        return this.dipCampoRicercas;
    }

    /**
     * Hibernate non ammette tipi List (e quindi ordinamento) su OneToMany Eager, quindi lo faccio esplicitamente
     *
     * @return lista con elementi di tipo {@link DipCampoRicerca} ordinata
     */

    public List<DipCampoRicerca> getDipCampoRicercaOrdinati() {
        Comparator<DipCampoRicerca> compareByRigaColonna = Comparator.comparing(DipCampoRicerca::getNiRigaCampo)
                .thenComparing(DipCampoRicerca::getNiColonnaCampo);

        List<DipCampoRicerca> sorted = new ArrayList<>(this.dipCampoRicercas).stream().sorted(compareByRigaColonna)
                .collect(Collectors.toList());
        return sorted;
    }

    public void setDipCampoRicercas(Set<DipCampoRicerca> dipCampoRicercas) {
        this.dipCampoRicercas = dipCampoRicercas;
    }

    public DipCampoRicerca addDipCampoRicerca(DipCampoRicerca dipCampoRicerca) {
        getDipCampoRicercas().add(dipCampoRicerca);
        dipCampoRicerca.setDipGruppoCampi(this);

        return dipCampoRicerca;
    }

    public DipCampoRicerca removeDipCampoRicerca(DipCampoRicerca dipCampoRicerca) {
        getDipCampoRicercas().remove(dipCampoRicerca);
        dipCampoRicerca.setDipGruppoCampi(null);

        return dipCampoRicerca;
    }

    public DipRicerca getDipRicerca() {
        return this.dipRicerca;
    }

    public void setDipRicerca(DipRicerca dipRicerca) {
        this.dipRicerca = dipRicerca;
    }

}
