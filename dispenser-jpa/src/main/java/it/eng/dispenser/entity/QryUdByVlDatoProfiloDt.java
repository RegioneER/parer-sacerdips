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
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * The persistent class for the QRY_UD_BY_VL_DATO_PROFILO_DT database table.
 *
 */
@Entity
@Table(name = "QRY_UD_BY_VL_DATO_PROFILO_DT")
public class QryUdByVlDatoProfiloDt implements Serializable {

    private static final long serialVersionUID = 1L;

    @Column(name = "DL_VALORE")
    private Timestamp dlValore;

    @Column(name = "ID_TIPO_UNITA_DOC")
    private BigDecimal idTipoUnitaDoc;

    @EmbeddedId
    private QryUdByVlDatoProfiloDtId qryUdByVlDatoProfiloDtId;

    public QryUdByVlDatoProfiloDt() {
    }

    public Timestamp getDlValore() {
        return this.dlValore;
    }

    public void setDlValore(Timestamp dlValore) {
        this.dlValore = dlValore;
    }

    public BigDecimal getIdTipoUnitaDoc() {
        return idTipoUnitaDoc;
    }

    public void setIdTipoUnitaDoc(BigDecimal idTipoUnitaDoc) {
        this.idTipoUnitaDoc = idTipoUnitaDoc;
    }

    public QryUdByVlDatoProfiloDtId getQryUdByVlDatoProfiloDtId() {
        return qryUdByVlDatoProfiloDtId;
    }

    public void setQryUdByVlDatoProfiloDtId(QryUdByVlDatoProfiloDtId qryUdByVlDatoProfiloDtId) {
        this.qryUdByVlDatoProfiloDtId = qryUdByVlDatoProfiloDtId;
    }

}
