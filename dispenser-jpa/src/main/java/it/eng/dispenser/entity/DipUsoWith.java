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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 *
 * @author Iacolucci_M
 */
@Entity
@Table(name = "DIP_USO_WITH")
@NamedQuery(name = "DipUsoWith.findByDipDefAttributo", query = "SELECT d FROM DipUsoWith d WHERE d.idDefAttributo = :DipDefAttributo")
public class DipUsoWith implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "DIP_USO_WITH_IDUSOWITH_GENERATOR", sequenceName = "SDIP_USO_WITH", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DIP_USO_WITH_IDUSOWITH_GENERATOR")
    @Column(name = "ID_USO_WITH")
    private long idUsoWith;

    @JoinColumn(name = "ID_QUERY_WITH", referencedColumnName = "ID_QUERY_WITH")
    @ManyToOne(optional = false)
    private DipQueryWith idQueryWith;

    @JoinColumn(name = "ID_DEF_ATTRIBUTO", referencedColumnName = "ID_DEF_ATTRIBUTO")
    @ManyToOne(optional = false)
    private DipDefAttributo idDefAttributo;

    public DipUsoWith() {
    }

    public DipUsoWith(long idUsoWith) {
        this.idUsoWith = idUsoWith;
    }

    public long getIdUsoWith() {
        return idUsoWith;
    }

    public void setIdUsoWith(long idUsoWith) {
        this.idUsoWith = idUsoWith;
    }

    public DipQueryWith getIdQueryWith() {
        return idQueryWith;
    }

    public void setIdQueryWith(DipQueryWith idQueryWith) {
        this.idQueryWith = idQueryWith;
    }

    public DipDefAttributo getIdDefAttributo() {
        return idDefAttributo;
    }

    public void setIdDefAttributo(DipDefAttributo idDefAttributo) {
        this.idDefAttributo = idDefAttributo;
    }

}
