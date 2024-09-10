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
import javax.persistence.*;
import java.util.Date;

/**
 * The persistent class for the APL_NOTA_RILASCIO database table.
 *
 */
@Entity
@Table(name = "APL_NOTA_RILASCIO", schema = "SACER_IAM")
public class SIAplNotaRilascio implements Serializable {
    private static final long serialVersionUID = 1L;
    private long idNotaRilascio;
    private String cdVersione;
    private Date dtVersione;
    private Date dtIniVal;
    private Date dtFineVal;
    private String dsEvidenza;
    private String blNota;
    private SIAplApplic siAplApplic;

    public SIAplNotaRilascio() {
    }

    @Id
    // @SequenceGenerator(name = "APL_NOTA_RILASCIO_IDNOTARILASCIO_GENERATOR", sequenceName = "SAPL_NOTA_RILASCIO",
    // allocationSize = 1, schema = "SACER_IAM")
    // @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "APL_NOTA_RILASCIO_IDNOTARILASCIO_GENERATOR")
    @Column(name = "ID_NOTA_RILASCIO")
    public long getIdNotaRilascio() {
        return this.idNotaRilascio;
    }

    public void setIdNotaRilascio(long idNotaRilascio) {
        this.idNotaRilascio = idNotaRilascio;
    }

    @Column(name = "CD_VERSIONE")
    public String getCdVersione() {
        return this.cdVersione;
    }

    public void setCdVersione(String cdVersione) {
        this.cdVersione = cdVersione;
    }

    @Column(name = "DS_EVIDENZA")
    public String getDsEvidenza() {
        return this.dsEvidenza;
    }

    public void setDsEvidenza(String dsEvidenza) {
        this.dsEvidenza = dsEvidenza;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_VERSIONE")
    public Date getDtVersione() {
        return this.dtVersione;
    }

    public void setDtVersione(Date dtVersione) {
        this.dtVersione = dtVersione;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_INI_VAL")
    public Date getDtIniVal() {
        return this.dtIniVal;
    }

    public void setDtIniVal(Date dtIniVal) {
        this.dtIniVal = dtIniVal;
    }

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DT_FINE_VAL")
    public Date getDtFineVal() {
        return this.dtFineVal;
    }

    public void setDtFineVal(Date dtFineVal) {
        this.dtFineVal = dtFineVal;
    }

    @Lob
    @Column(name = "BL_NOTA")
    public String getBlNota() {
        return this.blNota;
    }

    public void setBlNota(String blNota) {
        this.blNota = blNota;
    }

    // bi-directional many-to-one association to AplApplic
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_APPLIC")
    public SIAplApplic getSiAplApplic() {
        return this.siAplApplic;
    }

    public void setSiAplApplic(SIAplApplic siAplApplic) {
        this.siAplApplic = siAplApplic;
    }

}
