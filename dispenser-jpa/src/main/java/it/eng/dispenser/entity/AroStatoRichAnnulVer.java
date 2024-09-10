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
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the ARO_STATO_RICH_ANNUL_VERS database table.
 *
 */
@Entity
@Table(name = "ARO_STATO_RICH_ANNUL_VERS", schema = "SACER")
public class AroStatoRichAnnulVer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    // @SequenceGenerator(name = "ARO_STATO_RICH_ANNUL_VERS_IDSTATORICHANNULVERS_GENERATOR", sequenceName =
    // "SARO_STATO_RICH_ANNUL_VERS", schema = "SACER")
    // @GeneratedValue(strategy = GenerationType.SEQUENCE, generator =
    // "ARO_STATO_RICH_ANNUL_VERS_IDSTATORICHANNULVERS_GENERATOR")
    @Column(name = "ID_STATO_RICH_ANNUL_VERS")
    private long idStatoRichAnnulVers;

    @Column(name = "DS_NOTA_RICH_ANNUL_VERS")
    private String dsNotaRichAnnulVers;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_REG_STATO_RICH_ANNUL_VERS")
    private Date dtRegStatoRichAnnulVers;

    @Column(name = "ID_USER_IAM")
    private BigDecimal idUserIam;

    @Column(name = "PG_STATO_RICH_ANNUL_VERS")
    private BigDecimal pgStatoRichAnnulVers;

    @Column(name = "TI_STATO_RICH_ANNUL_VERS")
    private String tiStatoRichAnnulVers;

    // bi-directional many-to-one association to AroRichAnnulVer
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_RICH_ANNUL_VERS")
    private AroRichAnnulVer aroRichAnnulVer;

    public AroStatoRichAnnulVer() {
    }

    public long getIdStatoRichAnnulVers() {
        return this.idStatoRichAnnulVers;
    }

    public void setIdStatoRichAnnulVers(long idStatoRichAnnulVers) {
        this.idStatoRichAnnulVers = idStatoRichAnnulVers;
    }

    public String getDsNotaRichAnnulVers() {
        return this.dsNotaRichAnnulVers;
    }

    public void setDsNotaRichAnnulVers(String dsNotaRichAnnulVers) {
        this.dsNotaRichAnnulVers = dsNotaRichAnnulVers;
    }

    public Date getDtRegStatoRichAnnulVers() {
        return this.dtRegStatoRichAnnulVers;
    }

    public void setDtRegStatoRichAnnulVers(Date dtRegStatoRichAnnulVers) {
        this.dtRegStatoRichAnnulVers = dtRegStatoRichAnnulVers;
    }

    public BigDecimal getIdUserIam() {
        return this.idUserIam;
    }

    public void setIdUserIam(BigDecimal idUserIam) {
        this.idUserIam = idUserIam;
    }

    public BigDecimal getPgStatoRichAnnulVers() {
        return this.pgStatoRichAnnulVers;
    }

    public void setPgStatoRichAnnulVers(BigDecimal pgStatoRichAnnulVers) {
        this.pgStatoRichAnnulVers = pgStatoRichAnnulVers;
    }

    public String getTiStatoRichAnnulVers() {
        return this.tiStatoRichAnnulVers;
    }

    public void setTiStatoRichAnnulVers(String tiStatoRichAnnulVers) {
        this.tiStatoRichAnnulVers = tiStatoRichAnnulVers;
    }

    public AroRichAnnulVer getAroRichAnnulVer() {
        return this.aroRichAnnulVer;
    }

    public void setAroRichAnnulVer(AroRichAnnulVer aroRichAnnulVer) {
        this.aroRichAnnulVer = aroRichAnnulVer;
    }

}
