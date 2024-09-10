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
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * The persistent class for the QRY_DOC_BY_VL_META_NUM database table.
 *
 */
@Entity
@Table(name = "QRY_DOC_BY_VL_META_NUM")
public class QryDocByVlMetaNum implements Serializable {
    private static final long serialVersionUID = 1L;

    @Column(name = "DL_VALORE")
    private BigDecimal dlValore;

    @EmbeddedId
    private QryDocByVlMetaNumId qryDocByVlMetaNumId;

    public QryDocByVlMetaNum() {
    }

    public BigDecimal getDlValore() {
        return this.dlValore;
    }

    public void setDlValore(BigDecimal dlValore) {
        this.dlValore = dlValore;
    }

    public QryDocByVlMetaNumId getQryDocByVlMetaNumId() {
        return qryDocByVlMetaNumId;
    }

    public void setQryDocByVlMetaNumId(QryDocByVlMetaNumId qryDocByVlMetaNumId) {
        this.qryDocByVlMetaNumId = qryDocByVlMetaNumId;
    }

}
