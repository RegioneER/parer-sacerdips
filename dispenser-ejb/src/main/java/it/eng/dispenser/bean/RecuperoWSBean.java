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

package it.eng.dispenser.bean;

import java.io.Serializable;
import java.math.BigDecimal;

public class RecuperoWSBean implements Serializable {

    private static final long serialVersionUID = 1L;
    private String ambiente;
    private String ente;
    private String struttura;
    private String registro;
    private BigDecimal anno;
    private String numero;
    private String idDocumento;
    private BigDecimal ordinePresentazioneComponente;
    private String tipoDocumento;

    public RecuperoWSBean(String ambiente, String ente, String struttura, String registro, BigDecimal anno,
            String numero) {
        this.ambiente = ambiente;
        this.ente = ente;
        this.struttura = struttura;
        this.registro = registro;
        this.anno = anno;
        this.numero = numero;
    }

    public RecuperoWSBean(String ambiente, String ente, String struttura, String registro, BigDecimal anno,
            String numero, String idDocumento, BigDecimal ordinePresentazioneComponente, String tipoDocumento) {
        this(ambiente, ente, struttura, registro, anno, numero);
        this.idDocumento = idDocumento;
        this.ordinePresentazioneComponente = ordinePresentazioneComponente;
        this.tipoDocumento = tipoDocumento;
    }

    public String getAmbiente() {
        return ambiente;
    }

    public void setAmbiente(String ambiente) {
        this.ambiente = ambiente;
    }

    public String getEnte() {
        return ente;
    }

    public void setEnte(String ente) {
        this.ente = ente;
    }

    public String getStruttura() {
        return struttura;
    }

    public void setStruttura(String struttura) {
        this.struttura = struttura;
    }

    public String getRegistro() {
        return registro;
    }

    public void setRegistro(String registro) {
        this.registro = registro;
    }

    public BigDecimal getAnno() {
        return anno;
    }

    public void setAnno(BigDecimal anno) {
        this.anno = anno;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getIdDocumento() {
        return idDocumento;
    }

    public void setIdDocumento(String idDocumento) {
        this.idDocumento = idDocumento;
    }

    public BigDecimal getOrdinePresentazioneComponente() {
        return ordinePresentazioneComponente;
    }

    public void setOrdinePresentazioneComponente(BigDecimal ordinePresentazioneComponente) {
        this.ordinePresentazioneComponente = ordinePresentazioneComponente;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }
}
