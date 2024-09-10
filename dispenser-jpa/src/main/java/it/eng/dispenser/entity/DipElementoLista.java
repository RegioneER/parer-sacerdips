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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * The persistent class for the DIP_ELEMENTO_LISTA database table.
 *
 */
@Entity
@Table(name = "DIP_ELEMENTO_LISTA")
@NamedQuery(name = "DipElementoLista.findByNMRicerca", query = "SELECT d FROM DipElementoLista d JOIN FETCH d.dipAttribRisultato WHERE d.dipAttribRisultato.dipRicerca.nmRicerca = :nmRicerca ORDER BY d.niNumOrdine")
public class DipElementoLista implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "DIP_ELEMENTO_LISTA_IDELEMENTOLISTA_GENERATOR", sequenceName = "SDIP_ELEMENTO_LISTA", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DIP_ELEMENTO_LISTA_IDELEMENTOLISTA_GENERATOR")
    @Column(name = "ID_ELEMENTO_LISTA")
    private long idElementoLista;

    @Column(name = "DS_ELEMENTO_LISTA")
    private String dsElementoLista;

    @Column(name = "FL_VISIBILE", columnDefinition = "char(1)")
    private String flVisibile;

    @Column(name = "NI_NUM_ORDINE")
    private BigDecimal niNumOrdine;

    // bi-directional many-to-one association to DipAttribRisultato
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ATTRIB_RISULTATO")
    private DipAttribRisultato dipAttribRisultato;

    public DipElementoLista() {
    }

    public long getIdElementoLista() {
        return this.idElementoLista;
    }

    public void setIdElementoLista(long idElementoLista) {
        this.idElementoLista = idElementoLista;
    }

    public String getDsElementoLista() {
        return this.dsElementoLista;
    }

    public void setDsElementoLista(String dsElementoLista) {
        this.dsElementoLista = dsElementoLista;
    }

    public String getFlVisibile() {
        return this.flVisibile;
    }

    public void setFlVisibile(String flVisibile) {
        this.flVisibile = flVisibile;
    }

    public BigDecimal getNiNumOrdine() {
        return this.niNumOrdine;
    }

    public void setNiNumOrdine(BigDecimal niNumOrdine) {
        this.niNumOrdine = niNumOrdine;
    }

    public DipAttribRisultato getDipAttribRisultato() {
        return this.dipAttribRisultato;
    }

    public void setDipAttribRisultato(DipAttribRisultato dipAttribRisultato) {
        this.dipAttribRisultato = dipAttribRisultato;
    }

}
