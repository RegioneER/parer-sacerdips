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
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * The persistent class for the DIP_ATTRIB_RISULTATO database table.
 *
 */
@Entity
@Table(name = "DIP_ATTRIB_RISULTATO")
@NamedQueries(value = {
        @NamedQuery(name = "DipAttribRisultato.findAllLista", query = "SELECT d FROM DipAttribRisultato d JOIN d.dipElementoLista del where d.dipRicerca.nmRicerca = :nmRicerca"),
        @NamedQuery(name = "DipAttribRisultato.findAllDettaglio", query = "SELECT d FROM DipAttribRisultato d JOIN d.dipElementoDettaglio ded where d.dipRicerca.nmRicerca = :nmRicerca") })
public class DipAttribRisultato implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "DIP_ATTRIB_RISULTATO_IDATTRIBRISULTATO_GENERATOR", sequenceName = "SDIP_ATTRIB_RISULTATO", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DIP_ATTRIB_RISULTATO_IDATTRIBRISULTATO_GENERATOR")
    @Column(name = "ID_ATTRIB_RISULTATO")
    private long idAttribRisultato;

    @Column(name = "FL_ORDERBY", columnDefinition = "char(1)")
    private String flOrderby;

    @Column(name = "NI_ORDERBY")
    private BigDecimal niOrderby;

    @Column(name = "NM_ATTRIBUTO")
    private String nmAttributo;

    @Column(name = "TI_DATO_ATTRIBUTO")
    private String tiDatoAttributo;

    @Column(name = "TI_ORDERBY")
    private String tiOrderby;

    // bi-directional many-to-one association to DipRicerca
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_RICERCA")
    private DipRicerca dipRicerca;

    // bi-directional many-to-one association to DipElementoDettaglio
    @OneToOne(mappedBy = "dipAttribRisultato")
    private DipElementoDettaglio dipElementoDettaglio;

    // bi-directional many-to-one association to DipElementoLista
    @OneToOne(mappedBy = "dipAttribRisultato", fetch = FetchType.EAGER)
    private DipElementoLista dipElementoLista;

    // bi-directional many-to-one association to DipColonnaRicerca
    @OneToMany(mappedBy = "dipAttribRisultato", fetch = FetchType.EAGER)
    private Set<DipDefAttributo> dipDefAttributos;

    public DipAttribRisultato() {
    }

    public long getIdAttribRisultato() {
        return this.idAttribRisultato;
    }

    public void setIdAttribRisultato(long idAttribRisultato) {
        this.idAttribRisultato = idAttribRisultato;
    }

    public String getFlOrderby() {
        return this.flOrderby;
    }

    public void setFlOrderby(String flOrderby) {
        this.flOrderby = flOrderby;
    }

    public BigDecimal getNiOrderby() {
        return this.niOrderby;
    }

    public void setNiOrderby(BigDecimal niOrderby) {
        this.niOrderby = niOrderby;
    }

    public String getNmAttributo() {
        return this.nmAttributo;
    }

    public void setNmAttributo(String nmAttributo) {
        this.nmAttributo = nmAttributo;
    }

    public String getTiDatoAttributo() {
        return this.tiDatoAttributo;
    }

    public void setTiDatoAttributo(String tiDatoAttributo) {
        this.tiDatoAttributo = tiDatoAttributo;
    }

    public String getTiOrderby() {
        return this.tiOrderby;
    }

    public void setTiOrderby(String tiOrderby) {
        this.tiOrderby = tiOrderby;
    }

    public DipRicerca getDipRicerca() {
        return this.dipRicerca;
    }

    public void setDipRicerca(DipRicerca dipRicerca) {
        this.dipRicerca = dipRicerca;
    }

    public DipElementoDettaglio getDipElementoDettaglio() {
        return this.dipElementoDettaglio;
    }

    public void setDipElementoDettaglio(DipElementoDettaglio dipElementoDettaglio) {
        this.dipElementoDettaglio = dipElementoDettaglio;
    }

    public DipElementoLista getDipElementoLista() {
        return this.dipElementoLista;
    }

    public void setDipElementoLista(DipElementoLista dipElementoLista) {
        this.dipElementoLista = dipElementoLista;
    }

    public Set<DipDefAttributo> getDipDefAttributos() {
        return dipDefAttributos;
    }

    public void setDipDefAttributos(Set<DipDefAttributo> dipDefAttributos) {
        this.dipDefAttributos = dipDefAttributos;
    }

}
