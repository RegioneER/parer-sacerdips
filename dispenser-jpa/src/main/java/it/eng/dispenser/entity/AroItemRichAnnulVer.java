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
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * The persistent class for the ARO_ITEM_RICH_ANNUL_VERS database table.
 *
 */
@Entity
@Table(name = "ARO_ITEM_RICH_ANNUL_VERS", schema = "SACER")
public class AroItemRichAnnulVer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    // @SequenceGenerator(name = "ARO_ITEM_RICH_ANNUL_VERS_IDITEMRICHANNULVERS_GENERATOR", sequenceName =
    // "SARO_ITEM_RICH_ANNUL_VERS", schema = "SACER")
    // @GeneratedValue(strategy = GenerationType.SEQUENCE, generator =
    // "ARO_ITEM_RICH_ANNUL_VERS_IDITEMRICHANNULVERS_GENERATOR")
    @Column(name = "ID_ITEM_RICH_ANNUL_VERS")
    private long idItemRichAnnulVers;

    @Column(name = "AA_KEY_UNITA_DOC")
    private BigDecimal aaKeyUnitaDoc;

    @Column(name = "CD_KEY_UNITA_DOC")
    private String cdKeyUnitaDoc;

    @Column(name = "CD_REGISTRO_KEY_UNITA_DOC")
    private String cdRegistroKeyUnitaDoc;

    @Column(name = "ID_STRUT")
    private BigDecimal idStrut;

    @Column(name = "PG_ITEM_RICH_ANNUL_VERS")
    private BigDecimal pgItemRichAnnulVers;

    @Column(name = "TI_ITEM_RICH_ANNUL_VERS")
    private String tiItemRichAnnulVers;

    @Column(name = "TI_STATO_ITEM")
    private String tiStatoItem;

    // bi-directional many-to-one association to AroUnitaDoc
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_UNITA_DOC")
    private AroUnitaDoc aroUnitaDoc;

    // bi-directional many-to-one association to AroRichAnnulVer
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_RICH_ANNUL_VERS")
    private AroRichAnnulVer aroRichAnnulVer;

    public AroItemRichAnnulVer() {
    }

    public long getIdItemRichAnnulVers() {
        return this.idItemRichAnnulVers;
    }

    public void setIdItemRichAnnulVers(long idItemRichAnnulVers) {
        this.idItemRichAnnulVers = idItemRichAnnulVers;
    }

    public BigDecimal getAaKeyUnitaDoc() {
        return this.aaKeyUnitaDoc;
    }

    public void setAaKeyUnitaDoc(BigDecimal aaKeyUnitaDoc) {
        this.aaKeyUnitaDoc = aaKeyUnitaDoc;
    }

    public String getCdKeyUnitaDoc() {
        return this.cdKeyUnitaDoc;
    }

    public void setCdKeyUnitaDoc(String cdKeyUnitaDoc) {
        this.cdKeyUnitaDoc = cdKeyUnitaDoc;
    }

    public String getCdRegistroKeyUnitaDoc() {
        return this.cdRegistroKeyUnitaDoc;
    }

    public void setCdRegistroKeyUnitaDoc(String cdRegistroKeyUnitaDoc) {
        this.cdRegistroKeyUnitaDoc = cdRegistroKeyUnitaDoc;
    }

    public BigDecimal getIdStrut() {
        return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
        this.idStrut = idStrut;
    }

    public BigDecimal getPgItemRichAnnulVers() {
        return this.pgItemRichAnnulVers;
    }

    public void setPgItemRichAnnulVers(BigDecimal pgItemRichAnnulVers) {
        this.pgItemRichAnnulVers = pgItemRichAnnulVers;
    }

    public String getTiItemRichAnnulVers() {
        return this.tiItemRichAnnulVers;
    }

    public void setTiItemRichAnnulVers(String tiItemRichAnnulVers) {
        this.tiItemRichAnnulVers = tiItemRichAnnulVers;
    }

    public String getTiStatoItem() {
        return this.tiStatoItem;
    }

    public void setTiStatoItem(String tiStatoItem) {
        this.tiStatoItem = tiStatoItem;
    }

    public AroRichAnnulVer getAroRichAnnulVer() {
        return this.aroRichAnnulVer;
    }

    public void setAroRichAnnulVer(AroRichAnnulVer aroRichAnnulVer) {
        this.aroRichAnnulVer = aroRichAnnulVer;
    }

    public AroUnitaDoc getAroUnitaDoc() {
        return this.aroUnitaDoc;
    }

    public void setAroUnitaDoc(AroUnitaDoc aroUnitaDoc) {
        this.aroUnitaDoc = aroUnitaDoc;
    }

}
