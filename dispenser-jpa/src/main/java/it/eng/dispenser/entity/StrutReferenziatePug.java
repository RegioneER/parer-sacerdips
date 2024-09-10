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
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * The persistent class for the STRUT_REFERENZIATE_PUG database table.
 *
 */
@Entity
@Table(name = "STRUT_REFERENZIATE_PUG")
public class StrutReferenziatePug implements Serializable {

    private static final long serialVersionUID = 1L;

    private long idStrutReferenziatePug;
    private BigDecimal idStrutFittizia;
    private BigDecimal idStrutReale;

    public StrutReferenziatePug() {
    }

    @Id
    @Column(name = "ID_STRUT_REFERENZIATE_PUG")
    public long getIdStrutReferenziatePug() {
        return this.idStrutReferenziatePug;
    }

    public void setIdStrutReferenziatePug(long idStrutReferenziatePug) {
        this.idStrutReferenziatePug = idStrutReferenziatePug;
    }

    @Column(name = "ID_STRUT_FITTIZIA")
    public BigDecimal getIdStrutFittizia() {
        return this.idStrutFittizia;
    }

    public void setIdStrutFittizia(BigDecimal idStrutFittizia) {
        this.idStrutFittizia = idStrutFittizia;
    }

    @Column(name = "ID_STRUT_REALE")
    public BigDecimal getIdStrutReale() {
        return this.idStrutReale;
    }

    public void setIdStrutReale(BigDecimal idStrutReale) {
        this.idStrutReale = idStrutReale;
    }

}
