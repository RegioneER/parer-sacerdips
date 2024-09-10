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
import java.util.Date;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlID;

/**
 * The persistent class for the DEC_TIPO_DOC database table.
 *
 */
@Entity
@Cacheable(true)
@Table(name = "DEC_TIPO_DOC", schema = "SACER")
public class DecTipoDoc implements Serializable {

    private static final long serialVersionUID = 1L;
    private long idTipoDoc;
    private String dlNoteTipoDoc;
    private String dsTipoDoc;
    private Date dtIstituz;
    private Date dtSoppres;
    private String flTipoDocPrincipale;
    private String nmTipoDoc;
    private OrgStrut orgStrut;

    public DecTipoDoc() {
    }

    @Id
    // @SequenceGenerator(name = "DEC_TIPO_DOC_IDTIPODOC_GENERATOR", sequenceName = "SDEC_TIPO_DOC", allocationSize = 1,
    // schema = "SACER")
    // @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DEC_TIPO_DOC_IDTIPODOC_GENERATOR")
    @Column(name = "ID_TIPO_DOC")
    @XmlID
    public long getIdTipoDoc() {
        return this.idTipoDoc;
    }

    public void setIdTipoDoc(long idTipoDoc) {
        this.idTipoDoc = idTipoDoc;
    }

    @Column(name = "DL_NOTE_TIPO_DOC")
    public String getDlNoteTipoDoc() {
        return this.dlNoteTipoDoc;
    }

    public void setDlNoteTipoDoc(String dlNoteTipoDoc) {
        this.dlNoteTipoDoc = dlNoteTipoDoc;
    }

    @Column(name = "DS_TIPO_DOC")
    public String getDsTipoDoc() {
        return this.dsTipoDoc;
    }

    public void setDsTipoDoc(String dsTipoDoc) {
        this.dsTipoDoc = dsTipoDoc;
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

    @Column(name = "FL_TIPO_DOC_PRINCIPALE", columnDefinition = "char(1)")
    public String getFlTipoDocPrincipale() {
        return this.flTipoDocPrincipale;
    }

    public void setFlTipoDocPrincipale(String flTipoDocPrincipale) {
        this.flTipoDocPrincipale = flTipoDocPrincipale;
    }

    @Column(name = "NM_TIPO_DOC")
    public String getNmTipoDoc() {
        return this.nmTipoDoc;
    }

    public void setNmTipoDoc(String nmTipoDoc) {
        this.nmTipoDoc = nmTipoDoc;
    }

    // bi-directional many-to-one association to OrgStrut
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_STRUT")
    public OrgStrut getOrgStrut() {
        return this.orgStrut;
    }

    public void setOrgStrut(OrgStrut orgStrut) {
        this.orgStrut = orgStrut;
    }

}
