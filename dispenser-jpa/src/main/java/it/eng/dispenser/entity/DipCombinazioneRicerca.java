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
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * The persistent class for the DIP_COMBINAZIONE_RICERCA database table.
 *
 */
@Entity
@Table(name = "DIP_COMBINAZIONE_RICERCA")
@NamedQuery(name = "DipCombinazioneRicerca.findAllByNameRic", query = "SELECT d FROM DipCombinazioneRicerca d JOIN d.dipRicerca ric WHERE ric.nmRicerca = :nmRicerca")
public class DipCombinazioneRicerca implements Serializable {

    private static final long serialVersionUID = 1L;

    private long idCombinazioneRicerca;
    private BigDecimal idStrut;
    private BigDecimal idTipoDoc;
    private BigDecimal idTipoUnitaDoc;
    private String nmNomeCombinazione;
    private DipRicerca dipRicerca;
    private List<DipFiltroRicerca> dipFiltroRicercas;

    public DipCombinazioneRicerca() {
    }

    @Id
    @SequenceGenerator(name = "DIP_COMBINAZIONE_RICERCA_IDCOMBINAZIONERICERCA_GENERATOR", sequenceName = "SDIP_COMBINAZIONE_RICERCA", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DIP_COMBINAZIONE_RICERCA_IDCOMBINAZIONERICERCA_GENERATOR")
    @Column(name = "ID_COMBINAZIONE_RICERCA")
    public long getIdCombinazioneRicerca() {
        return this.idCombinazioneRicerca;
    }

    public void setIdCombinazioneRicerca(long idCombinazioneRicerca) {
        this.idCombinazioneRicerca = idCombinazioneRicerca;
    }

    @Column(name = "ID_STRUT")
    public BigDecimal getIdStrut() {
        return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
        this.idStrut = idStrut;
    }

    @Column(name = "ID_TIPO_DOC")
    public BigDecimal getIdTipoDoc() {
        return this.idTipoDoc;
    }

    public void setIdTipoDoc(BigDecimal idTipoDoc) {
        this.idTipoDoc = idTipoDoc;
    }

    @Column(name = "ID_TIPO_UNITA_DOC")
    public BigDecimal getIdTipoUnitaDoc() {
        return this.idTipoUnitaDoc;
    }

    public void setIdTipoUnitaDoc(BigDecimal idTipoUnitaDoc) {
        this.idTipoUnitaDoc = idTipoUnitaDoc;
    }

    @Column(name = "NM_NOME_COMBINAZIONE")
    public String getNmNomeCombinazione() {
        return this.nmNomeCombinazione;
    }

    public void setNmNomeCombinazione(String nmNomeCombinazione) {
        this.nmNomeCombinazione = nmNomeCombinazione;
    }

    // bi-directional many-to-one association to DipRicerca
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_RICERCA")
    public DipRicerca getDipRicerca() {
        return this.dipRicerca;
    }

    public void setDipRicerca(DipRicerca dipRicerca) {
        this.dipRicerca = dipRicerca;
    }

    // bi-directional many-to-one association to DipFiltroRicerca
    @OneToMany(mappedBy = "dipCombinazioneRicerca")
    public List<DipFiltroRicerca> getDipFiltroRicercas() {
        return this.dipFiltroRicercas;
    }

    public void setDipFiltroRicercas(List<DipFiltroRicerca> dipFiltroRicercas) {
        this.dipFiltroRicercas = dipFiltroRicercas;
    }

    public DipFiltroRicerca addDipFiltroRicerca(DipFiltroRicerca dipFiltroRicerca) {
        getDipFiltroRicercas().add(dipFiltroRicerca);
        dipFiltroRicerca.setDipCombinazioneRicerca(this);

        return dipFiltroRicerca;
    }

    public DipFiltroRicerca removeDipFiltroRicerca(DipFiltroRicerca dipFiltroRicerca) {
        getDipFiltroRicercas().remove(dipFiltroRicerca);
        dipFiltroRicerca.setDipCombinazioneRicerca(null);

        return dipFiltroRicerca;
    }

}
