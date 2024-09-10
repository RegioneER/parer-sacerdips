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
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * The persistent class for the DIP_DEF_ATTRIBUTO database table.
 *
 */
@Entity
@Table(name = "DIP_DEF_ATTRIBUTO")
public class DipDefAttributo implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "DIP_DEF_ATTRIBUTO_IDDEFATTRIBUTO_GENERATOR", sequenceName = "SDIP_DEF_ATTRIBUTO", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DIP_DEF_ATTRIBUTO_IDDEFATTRIBUTO_GENERATOR")
    @Column(name = "ID_DEF_ATTRIBUTO")
    private long idDefAttributo;

    @Column(name = "ID_ATTRIB_DATI_SPEC")
    private BigDecimal idAttribDatiSpec;

    @Column(name = "ID_STRUT")
    private BigDecimal idStrut;

    @Column(name = "TI_DEF_ATTRIB")
    private String tiDefAttrib;

    @Column(name = "NM_DATO_PROFILO")
    private String nmDatoProfilo;

    @Column(name = "CD_QUERY_CALC")
    private String cdQueryCalc;

    // bi-directional many-to-one association to DipMetaColonnaRicerca
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_ATTRIB_RISULTATO")
    private DipAttribRisultato dipAttribRisultato;

    public DipDefAttributo() {
    }

    public BigDecimal getIdAttribDatiSpec() {
        return this.idAttribDatiSpec;
    }

    public void setIdAttribDatiSpec(BigDecimal idAttribDatiSpec) {
        this.idAttribDatiSpec = idAttribDatiSpec;
    }

    public BigDecimal getIdStrut() {
        return this.idStrut;
    }

    public void setIdStrut(BigDecimal idStrut) {
        this.idStrut = idStrut;
    }

    public long getIdDefAttributo() {
        return idDefAttributo;
    }

    public void setIdDefAttributo(long idDefAttributo) {
        this.idDefAttributo = idDefAttributo;
    }

    public String getTiDefAttrib() {
        return tiDefAttrib;
    }

    public void setTiDefAttrib(String tiDefAttrib) {
        this.tiDefAttrib = tiDefAttrib;
    }

    public DipAttribRisultato getDipAttribRisultato() {
        return dipAttribRisultato;
    }

    public void setDipAttribRisultato(DipAttribRisultato dipAttribRisultato) {
        this.dipAttribRisultato = dipAttribRisultato;
    }

    public String getNmDatoProfilo() {
        return nmDatoProfilo;
    }

    public void setNmDatoProfilo(String nmDatoProfilo) {
        this.nmDatoProfilo = nmDatoProfilo;
    }

    public String getCdQueryCalc() {
        return cdQueryCalc;
    }

    public void setCdQueryCalc(String cdQueryCalc) {
        this.cdQueryCalc = cdQueryCalc;
    }

}
