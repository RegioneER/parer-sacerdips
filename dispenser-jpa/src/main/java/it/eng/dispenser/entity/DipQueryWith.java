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

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
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
 *
 * @author Iacolucci_M
 */
@Entity
@Table(name = "DIP_QUERY_WITH")
@NamedQuery(name = "DipQueryWith.findByDipRicercaOrdered", query = "SELECT d FROM DipQueryWith d WHERE d.dipRicerca = :dipRicerca ORDER BY d.niOrdWith")
public class DipQueryWith implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "DIP_QUERY_WITH_IDQUERYWITH_GENERATOR", sequenceName = "SDIP_QUERY_WITH", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DIP_QUERY_WITH_IDQUERYWITH_GENERATOR")
    @Column(name = "ID_QUERY_WITH")
    private Long idQueryWith;

    @Column(name = "NM_QUERY_WITH")
    private String nmQueryWith;

    @Column(name = "DL_QUERY_WITH")
    private String dlQueryWith;

    @Column(name = "NI_ORD_WITH")
    private BigDecimal niOrdWith;

    @JoinColumn(name = "ID_RICERCA", referencedColumnName = "ID_RICERCA")
    @ManyToOne(optional = false)
    private DipRicerca dipRicerca;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "idQueryWith")
    private List<DipUsoWith> dipUsoWithList;

    public DipQueryWith() {
    }

    public DipQueryWith(Long idQueryWith) {
        this.idQueryWith = idQueryWith;
    }

    public DipQueryWith(Long idQueryWith, String nmQueryWith, String dlQueryWith) {
        this.idQueryWith = idQueryWith;
        this.nmQueryWith = nmQueryWith;
        this.dlQueryWith = dlQueryWith;
    }

    public Long getIdQueryWith() {
        return idQueryWith;
    }

    public void setIdQueryWith(Long idQueryWith) {
        this.idQueryWith = idQueryWith;
    }

    public String getNmQueryWith() {
        return nmQueryWith;
    }

    public void setNmQueryWith(String nmQueryWith) {
        this.nmQueryWith = nmQueryWith;
    }

    public String getDlQueryWith() {
        return dlQueryWith;
    }

    public void setDlQueryWith(String dlQueryWith) {
        this.dlQueryWith = dlQueryWith;
    }

    public DipRicerca getIdRicerca() {
        return dipRicerca;
    }

    public void setIdRicerca(DipRicerca dipRicerca) {
        this.dipRicerca = dipRicerca;
    }

    public List<DipUsoWith> getDipUsoWithList() {
        return dipUsoWithList;
    }

    public void setDipUsoWithList(List<DipUsoWith> dipUsoWithList) {
        this.dipUsoWithList = dipUsoWithList;
    }

    public BigDecimal getNiOrdWith() {
        return niOrdWith;
    }

    public void setNiOrdWith(BigDecimal niOrdWith) {
        this.niOrdWith = niOrdWith;
    }

}
