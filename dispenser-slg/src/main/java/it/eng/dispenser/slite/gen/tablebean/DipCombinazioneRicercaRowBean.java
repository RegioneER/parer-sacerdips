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

package it.eng.dispenser.slite.gen.tablebean;

import java.math.BigDecimal;

import it.eng.dispenser.entity.DipCombinazioneRicerca;
import it.eng.dispenser.entity.DipRicerca;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * RowBean per la tabella Dip_Combinazione_Ricerca
 *
 */
public class DipCombinazioneRicercaRowBean extends BaseRow implements JEEBaseRowInterface {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$TableBeanWriter", comments =
     * "This class was generated by OraTool", date = "Wednesday, 27 May 2015 11:25" )
     */

    private static final long serialVersionUID = 1L;
    public static DipCombinazioneRicercaTableDescriptor TABLE_DESCRIPTOR = new DipCombinazioneRicercaTableDescriptor();

    public DipCombinazioneRicercaRowBean() {
        super();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    // getter e setter
    public BigDecimal getIdCombinazioneRicerca() {
        return getBigDecimal("id_combinazione_ricerca");
    }

    public void setIdCombinazioneRicerca(BigDecimal idCombinazioneRicerca) {
        setObject("id_combinazione_ricerca", idCombinazioneRicerca);
    }

    public BigDecimal getIdRicerca() {
        return getBigDecimal("id_ricerca");
    }

    public void setIdRicerca(BigDecimal idRicerca) {
        setObject("id_ricerca", idRicerca);
    }

    public String getNmNomeCombinazione() {
        return getString("nm_nome_combinazione");
    }

    public void setNmNomeCombinazione(String nmNomeCombinazione) {
        setObject("nm_nome_combinazione", nmNomeCombinazione);
    }

    public BigDecimal getIdStrut() {
        return getBigDecimal("id_strut");
    }

    public void setIdStrut(BigDecimal idStrut) {
        setObject("id_strut", idStrut);
    }

    public BigDecimal getIdTipoDoc() {
        return getBigDecimal("id_tipo_doc");
    }

    public void setIdTipoDoc(BigDecimal idTipoDoc) {
        setObject("id_tipo_doc", idTipoDoc);
    }

    public BigDecimal getIdTipoUnitaDoc() {
        return getBigDecimal("id_tipo_unita_doc");
    }

    public void setIdTipoUnitaDoc(BigDecimal idTipoUnitaDoc) {
        setObject("id_tipo_unita_doc", idTipoUnitaDoc);
    }

    @Override
    public void entityToRowBean(Object obj) {
        DipCombinazioneRicerca entity = (DipCombinazioneRicerca) obj;
        this.setIdCombinazioneRicerca(new BigDecimal(entity.getIdCombinazioneRicerca()));
        if (entity.getDipRicerca() != null) {
            this.setIdRicerca(new BigDecimal(entity.getDipRicerca().getIdRicerca()));
        }
        this.setNmNomeCombinazione(entity.getNmNomeCombinazione());
        this.setIdStrut(entity.getIdStrut());
        this.setIdTipoDoc(entity.getIdTipoDoc());
        this.setIdTipoUnitaDoc(entity.getIdTipoUnitaDoc());
    }

    @Override
    public DipCombinazioneRicerca rowBeanToEntity() {
        DipCombinazioneRicerca entity = new DipCombinazioneRicerca();
        if (this.getIdCombinazioneRicerca() != null) {
            entity.setIdCombinazioneRicerca(this.getIdCombinazioneRicerca().longValue());
        }
        if (this.getIdRicerca() != null) {
            if (entity.getDipRicerca() == null) {
                entity.setDipRicerca(new DipRicerca());
            }
            entity.getDipRicerca().setIdRicerca(this.getIdRicerca().longValue());
        }
        entity.setNmNomeCombinazione(this.getNmNomeCombinazione());
        entity.setIdStrut(this.getIdStrut());
        entity.setIdTipoDoc(this.getIdTipoDoc());
        entity.setIdTipoUnitaDoc(this.getIdTipoUnitaDoc());
        return entity;
    }

    // gestione della paginazione
    public void setRownum(Integer rownum) {
        setObject("rownum", rownum);
    }

    public Integer getRownum() {
        return Integer.parseInt(getObject("rownum").toString());
    }

    public void setRnum(Integer rnum) {
        setObject("rnum", rnum);
    }

    public Integer getRnum() {
        return Integer.parseInt(getObject("rnum").toString());
    }

    public void setNumrecords(Integer numRecords) {
        setObject("numrecords", numRecords);
    }

    public Integer getNumrecords() {
        return Integer.parseInt(getObject("numrecords").toString());
    }

}
