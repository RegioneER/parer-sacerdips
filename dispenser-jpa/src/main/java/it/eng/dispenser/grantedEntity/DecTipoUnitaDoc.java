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

package it.eng.dispenser.grantedEntity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the DEC_TIPO_UNITA_DOC database table.
 *
 */
@Entity
@Table(name = "DEC_TIPO_UNITA_DOC", schema = "SACER")
public class DecTipoUnitaDoc implements Serializable {

    private static final long serialVersionUID = 1L;
    private long idTipoUnitaDoc;
    private String cdSerie;
    private String cdSerieDaCreare;
    private String dlNoteTipoUd;
    private String dsSerieDaCreare;
    private String dsTipoSerieDaCreare;
    private String dsTipoUnitaDoc;
    private Date dtIstituz;
    private Date dtSoppres;
    private String flCreaTipoSerieStandard;
    private String flVersManuale;
    private String nmTipoSerieDaCreare;
    private String nmTipoUnitaDoc;
    private String tiSaveFile;
    private BigDecimal idStrut;

    public DecTipoUnitaDoc() {
    }

    @Id
    // @SequenceGenerator(name = "DEC_TIPO_UNITA_DOC_IDTIPOUNITADOC_GENERATOR", sequenceName = "SDEC_TIPO_UNITA_DOC",
    // allocationSize = 1)
    // @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DEC_TIPO_UNITA_DOC_IDTIPOUNITADOC_GENERATOR")
    @Column(name = "ID_TIPO_UNITA_DOC")
    public long getIdTipoUnitaDoc() {
        return this.idTipoUnitaDoc;
    }

    public void setIdTipoUnitaDoc(long idTipoUnitaDoc) {
        this.idTipoUnitaDoc = idTipoUnitaDoc;
    }

    @Column(name = "CD_SERIE")
    public String getCdSerie() {
        return this.cdSerie;
    }

    public void setCdSerie(String cdSerie) {
        this.cdSerie = cdSerie;
    }

    @Column(name = "CD_SERIE_DA_CREARE")
    public String getCdSerieDaCreare() {
        return this.cdSerieDaCreare;
    }

    public void setCdSerieDaCreare(String cdSerieDaCreare) {
        this.cdSerieDaCreare = cdSerieDaCreare;
    }

    @Column(name = "DL_NOTE_TIPO_UD")
    public String getDlNoteTipoUd() {
        return this.dlNoteTipoUd;
    }

    public void setDlNoteTipoUd(String dlNoteTipoUd) {
        this.dlNoteTipoUd = dlNoteTipoUd;
    }

    @Column(name = "DS_SERIE_DA_CREARE")
    public String getDsSerieDaCreare() {
        return this.dsSerieDaCreare;
    }

    public void setDsSerieDaCreare(String dsSerieDaCreare) {
        this.dsSerieDaCreare = dsSerieDaCreare;
    }

    @Column(name = "DS_TIPO_SERIE_DA_CREARE")
    public String getDsTipoSerieDaCreare() {
        return this.dsTipoSerieDaCreare;
    }

    public void setDsTipoSerieDaCreare(String dsTipoSerieDaCreare) {
        this.dsTipoSerieDaCreare = dsTipoSerieDaCreare;
    }

    @Column(name = "DS_TIPO_UNITA_DOC")
    public String getDsTipoUnitaDoc() {
        return this.dsTipoUnitaDoc;
    }

    public void setDsTipoUnitaDoc(String dsTipoUnitaDoc) {
        this.dsTipoUnitaDoc = dsTipoUnitaDoc;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_ISTITUZ")
    public Date getDtIstituz() {
        return this.dtIstituz;
    }

    public void setDtIstituz(Date dtIstituz) {
        this.dtIstituz = dtIstituz;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_SOPPRES")
    public Date getDtSoppres() {
        return this.dtSoppres;
    }

    public void setDtSoppres(Date dtSoppres) {
        this.dtSoppres = dtSoppres;
    }

    @Column(name = "FL_CREA_TIPO_SERIE_STANDARD", columnDefinition = "char(1)")
    public String getFlCreaTipoSerieStandard() {
        return this.flCreaTipoSerieStandard;
    }

    public void setFlCreaTipoSerieStandard(String flCreaTipoSerieStandard) {
        this.flCreaTipoSerieStandard = flCreaTipoSerieStandard;
    }

    @Column(name = "FL_VERS_MANUALE", columnDefinition = "char(1)")
    public String getFlVersManuale() {
        return this.flVersManuale;
    }

    public void setFlVersManuale(String flVersManuale) {
        this.flVersManuale = flVersManuale;
    }

    @Column(name = "NM_TIPO_SERIE_DA_CREARE")
    public String getNmTipoSerieDaCreare() {
        return this.nmTipoSerieDaCreare;
    }

    public void setNmTipoSerieDaCreare(String nmTipoSerieDaCreare) {
        this.nmTipoSerieDaCreare = nmTipoSerieDaCreare;
    }

    @Column(name = "NM_TIPO_UNITA_DOC")
    public String getNmTipoUnitaDoc() {
        return this.nmTipoUnitaDoc;
    }

    public void setNmTipoUnitaDoc(String nmTipoUnitaDoc) {
        this.nmTipoUnitaDoc = nmTipoUnitaDoc;
    }

    @Column(name = "TI_SAVE_FILE")
    public String getTiSaveFile() {
        return this.tiSaveFile;
    }

    public void setTiSaveFile(String tiSaveFile) {
        this.tiSaveFile = tiSaveFile;
    }

    @Column(name = "ID_STRUT")
    public BigDecimal getIdStrut() {
        return idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
        this.idStrut = idStrut;
    }

}
