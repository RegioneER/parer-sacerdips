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
 * The persistent class for the DIP_GRUPPO_DETTAGLIO database table.
 *
 */
@Entity
@Table(name = "DIP_GRUPPO_DETTAGLIO")
public class DipGruppoDettaglio implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "DIP_GRUPPO_DETTAGLIO_IDGRUPPODETTAGLIO_GENERATOR", sequenceName = "SDIP_GRUPPO_DETTAGLIO", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DIP_GRUPPO_DETTAGLIO_IDGRUPPODETTAGLIO_GENERATOR")
    @Column(name = "ID_GRUPPO_DETTAGLIO")
    private long idGruppoDettaglio;

    @Column(name = "DS_GRUPPO_DETTAGLIO")
    private String dsGruppoDettaglio;

    @Column(name = "NI_COLONNA_GRUPPO")
    private BigDecimal niColonnaGruppo;

    @Column(name = "NI_RIGA_GRUPPO")
    private BigDecimal niRigaGruppo;

    @Column(name = "NM_GRUPPO_DETTAGLIO")
    private String nmGruppoDettaglio;

    // bi-directional many-to-one association to DipElementoDettaglio
    @OneToMany(mappedBy = "dipGruppoDettaglio", fetch = FetchType.EAGER)
    // @OrderBy("niRigaElemento, niColonnaElemento")
    private Set<DipElementoDettaglio> dipElementoDettaglios;

    // bi-directional many-to-one association to DipRicerca
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_RICERCA")
    private DipRicerca dipRicerca;

    public DipGruppoDettaglio() {
    }

    public long getIdGruppoDettaglio() {
        return this.idGruppoDettaglio;
    }

    public void setIdGruppoDettaglio(long idGruppoDettaglio) {
        this.idGruppoDettaglio = idGruppoDettaglio;
    }

    public String getDsGruppoDettaglio() {
        return this.dsGruppoDettaglio;
    }

    public void setDsGruppoDettaglio(String dsGruppoDettaglio) {
        this.dsGruppoDettaglio = dsGruppoDettaglio;
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

    public String getNmGruppoDettaglio() {
        return this.nmGruppoDettaglio;
    }

    public void setNmGruppoDettaglio(String nmGruppoDettaglio) {
        this.nmGruppoDettaglio = nmGruppoDettaglio;
    }

    public List<DipElementoDettaglio> getDipElementoDettaglioOrdinati() {
        Comparator<DipElementoDettaglio> compareByRigaColonna = Comparator
                .comparing(DipElementoDettaglio::getNiRigaElemento)
                .thenComparing(DipElementoDettaglio::getNiColonnaElemento);

        List<DipElementoDettaglio> sorted = new ArrayList<>(this.dipElementoDettaglios).stream()
                .sorted(compareByRigaColonna).collect(Collectors.toList());
        return sorted;
    }

    private Set<DipElementoDettaglio> getDipElementoDettaglios() {
        return this.dipElementoDettaglios;
    }

    public void setDipElementoDettaglios(Set<DipElementoDettaglio> dipElementoDettaglios) {
        this.dipElementoDettaglios = dipElementoDettaglios;
    }

    public DipElementoDettaglio addDipElementoDettaglio(DipElementoDettaglio dipElementoDettaglio) {
        getDipElementoDettaglios().add(dipElementoDettaglio);
        dipElementoDettaglio.setDipGruppoDettaglio(this);

        return dipElementoDettaglio;
    }

    public DipElementoDettaglio removeDipElementoDettaglio(DipElementoDettaglio dipElementoDettaglio) {
        getDipElementoDettaglios().remove(dipElementoDettaglio);
        dipElementoDettaglio.setDipGruppoDettaglio(null);

        return dipElementoDettaglio;
    }

    public DipRicerca getDipRicerca() {
        return this.dipRicerca;
    }

    public void setDipRicerca(DipRicerca dipRicerca) {
        this.dipRicerca = dipRicerca;
    }

}
