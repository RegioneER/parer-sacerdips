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
 * The persistent class for the DIP_VALORE_PREDEFINITO database table.
 *
 */
@Entity
@Table(name = "DIP_VALORE_PREDEFINITO")
public class DipValorePredefinito implements Serializable {
    private static final long serialVersionUID = 1L;
    private long idValorePredefinito;
    private String dlValore;
    private BigDecimal idRegistroUnitaDoc;
    private DipFiltroRicerca dipFiltroRicerca;

    public DipValorePredefinito() {
    }

    @Id
    @SequenceGenerator(name = "DIP_VALORE_PREDEFINITO_IDVALOREPREDEFINITO_GENERATOR", sequenceName = "SDIP_VALORE_PREDEFINITO", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DIP_VALORE_PREDEFINITO_IDVALOREPREDEFINITO_GENERATOR")
    @Column(name = "ID_VALORE_PREDEFINITO")
    public long getIdValorePredefinito() {
        return this.idValorePredefinito;
    }

    public void setIdValorePredefinito(long idValorePredefinito) {
        this.idValorePredefinito = idValorePredefinito;
    }

    @Column(name = "DL_VALORE")
    public String getDlValore() {
        return this.dlValore;
    }

    public void setDlValore(String dlValore) {
        this.dlValore = dlValore;
    }

    @Column(name = "ID_REGISTRO_UNITA_DOC")
    public BigDecimal getIdRegistroUnitaDoc() {
        return this.idRegistroUnitaDoc;
    }

    public void setIdRegistroUnitaDoc(BigDecimal idRegistroUnitaDoc) {
        this.idRegistroUnitaDoc = idRegistroUnitaDoc;
    }

    // bi-directional many-to-one association to DipFiltroRicerca
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_FILTRO_RICERCA")
    public DipFiltroRicerca getDipFiltroRicerca() {
        return this.dipFiltroRicerca;
    }

    public void setDipFiltroRicerca(DipFiltroRicerca dipFiltroRicerca) {
        this.dipFiltroRicerca = dipFiltroRicerca;
    }

}
