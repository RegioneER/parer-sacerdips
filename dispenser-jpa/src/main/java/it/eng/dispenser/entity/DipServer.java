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

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * The persistent class for the DIP_SERVER database table.
 *
 */
@Entity
@Table(name = "DIP_SERVER")
@NamedQuery(name = "DipServer.findByServerName", query = "SELECT d FROM DipServer d WHERE d.cdServer = :cdServer")
public class DipServer implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @SequenceGenerator(name = "DIP_SERVER_IDSERVER_GENERATOR", sequenceName = "SDIP_SERVER", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "DIP_SERVER_IDSERVER_GENERATOR")
    @Column(name = "ID_SERVER")
    private long idServer;

    @Column(name = "CD_SERVER")
    private String cdServer;

    @Column(name = "FL_DA_ALLINEARE", columnDefinition = "char(1)")
    private String flDaAllineare;

    public DipServer() {
    }

    public long getIdServer() {
        return this.idServer;
    }

    public void setIdServer(long idServer) {
        this.idServer = idServer;
    }

    public String getCdServer() {
        return this.cdServer;
    }

    public void setCdServer(String cdServer) {
        this.cdServer = cdServer;
    }

    public String getFlDaAllineare() {
        return this.flDaAllineare;
    }

    public void setFlDaAllineare(String flDaAllineare) {
        this.flDaAllineare = flDaAllineare;
    }

}
