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
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * The persistent class for the DIP_ELEMENTO_DETTAGLIO database table.
 *
 */
@Entity
@Table(name = "DIP_ELEMENTO_DETTAGLIO")
public class DipElementoDettaglio implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "DIP_ELEMENTO_DETTAGLIO_IDELEMENTODETTAGLIO_GENERATOR", sequenceName = "SDIP_ELEMENTO_DETTAGLIO", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DIP_ELEMENTO_DETTAGLIO_IDELEMENTODETTAGLIO_GENERATOR")
    @Column(name = "ID_ELEMENTO_DETTAGLIO")
    private long idElementoDettaglio;

    @Column(name = "DS_ELEMENTO_DETTAGLIO")
    private String dsElementoDettaglio;

    @Column(name = "NI_COLONNA_ELEMENTO")
    private BigDecimal niColonnaElemento;

    @Column(name = "NI_RIGA_ELEMENTO")
    private BigDecimal niRigaElemento;

    @Column(name = "FL_VISIBILE", columnDefinition = "char(1)")
    private String flVisibile;

    // bi-directional many-to-one association to DipAttribRisultato
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_ATTRIB_RISULTATO")
    private DipAttribRisultato dipAttribRisultato;

    // bi-directional many-to-one association to DipGruppoDettaglio
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_GRUPPO_DETTAGLIO")
    private DipGruppoDettaglio dipGruppoDettaglio;

    public DipElementoDettaglio() {
    }

    public long getIdElementoDettaglio() {
        return this.idElementoDettaglio;
    }

    public void setIdElementoDettaglio(long idElementoDettaglio) {
        this.idElementoDettaglio = idElementoDettaglio;
    }

    public String getDsElementoDettaglio() {
        return this.dsElementoDettaglio;
    }

    public void setDsElementoDettaglio(String dsElementoDettaglio) {
        this.dsElementoDettaglio = dsElementoDettaglio;
    }

    public BigDecimal getNiColonnaElemento() {
        return this.niColonnaElemento;
    }

    public void setNiColonnaElemento(BigDecimal niColonnaElemento) {
        this.niColonnaElemento = niColonnaElemento;
    }

    public BigDecimal getNiRigaElemento() {
        return this.niRigaElemento;
    }

    public void setNiRigaElemento(BigDecimal niRigaElemento) {
        this.niRigaElemento = niRigaElemento;
    }

    public DipAttribRisultato getDipAttribRisultato() {
        return this.dipAttribRisultato;
    }

    public void setDipAttribRisultato(DipAttribRisultato dipAttribRisultato) {
        this.dipAttribRisultato = dipAttribRisultato;
    }

    public DipGruppoDettaglio getDipGruppoDettaglio() {
        return this.dipGruppoDettaglio;
    }

    public void setDipGruppoDettaglio(DipGruppoDettaglio dipGruppoDettaglio) {
        this.dipGruppoDettaglio = dipGruppoDettaglio;
    }

    public String getFlVisibile() {
        return flVisibile;
    }

    public void setFlVisibile(String flVisibile) {
        this.flVisibile = flVisibile;
    }

}
