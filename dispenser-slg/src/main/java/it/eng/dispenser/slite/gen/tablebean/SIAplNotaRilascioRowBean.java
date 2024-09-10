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
import java.sql.Timestamp;

import it.eng.dispenser.grantedEntity.SIAplApplic;
import it.eng.dispenser.grantedEntity.SIAplNotaRilascio;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * RowBean per la tabella Apl_Nota_Rilascio
 *
 */
public class SIAplNotaRilascioRowBean extends BaseRow implements JEEBaseRowInterface {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$TableBeanWriter", comments =
     * "This class was generated by OraTool", date = "Wednesday, 26 June 2019 11:18" )
     */

    private static final long serialVersionUID = 1L;
    public static SIAplNotaRilascioTableDescriptor TABLE_DESCRIPTOR = new SIAplNotaRilascioTableDescriptor();

    public SIAplNotaRilascioRowBean() {
        super();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    // getter e setter
    public BigDecimal getIdNotaRilascio() {
        return getBigDecimal("id_nota_rilascio");
    }

    public void setIdNotaRilascio(BigDecimal idNotaRilascio) {
        setObject("id_nota_rilascio", idNotaRilascio);
    }

    public BigDecimal getIdApplic() {
        return getBigDecimal("id_applic");
    }

    public void setIdApplic(BigDecimal idApplic) {
        setObject("id_applic", idApplic);
    }

    public String getCdVersione() {
        return getString("cd_versione");
    }

    public void setCdVersione(String cdVersione) {
        setObject("cd_versione", cdVersione);
    }

    public Timestamp getDtVersione() {
        return getTimestamp("dt_versione");
    }

    public void setDtVersione(Timestamp dtVersione) {
        setObject("dt_versione", dtVersione);
    }

    public Timestamp getDtIniVal() {
        return getTimestamp("dt_ini_val");
    }

    public void setDtIniVal(Timestamp dtIniVal) {
        setObject("dt_ini_val", dtIniVal);
    }

    public Timestamp getDtFineVal() {
        return getTimestamp("dt_fine_val");
    }

    public void setDtFineVal(Timestamp dtFineVal) {
        setObject("dt_fine_val", dtFineVal);
    }

    public String getDsEvidenza() {
        return getString("ds_evidenza");
    }

    public void setDsEvidenza(String dsEvidenza) {
        setObject("ds_evidenza", dsEvidenza);
    }

    public String getBlNota() {
        return getString("bl_nota");
    }

    public void setBlNota(String blNota) {
        setObject("bl_nota", blNota);
    }

    @Override
    public void entityToRowBean(Object obj) {
        SIAplNotaRilascio entity = (SIAplNotaRilascio) obj;
        this.setIdNotaRilascio(new BigDecimal(entity.getIdNotaRilascio()));
        if (entity.getSiAplApplic() != null) {
            this.setIdApplic(new BigDecimal(entity.getSiAplApplic().getIdApplic()));
        }
        this.setCdVersione(entity.getCdVersione());
        if (entity.getDtVersione() != null) {
            this.setDtVersione(new Timestamp(entity.getDtVersione().getTime()));
        }
        if (entity.getDtIniVal() != null) {
            this.setDtIniVal(new Timestamp(entity.getDtIniVal().getTime()));
        }
        if (entity.getDtFineVal() != null) {
            this.setDtFineVal(new Timestamp(entity.getDtFineVal().getTime()));
        }
        this.setDsEvidenza(entity.getDsEvidenza());
        this.setBlNota(entity.getBlNota());
    }

    @Override
    public SIAplNotaRilascio rowBeanToEntity() {
        SIAplNotaRilascio entity = new SIAplNotaRilascio();
        if (this.getIdNotaRilascio() != null) {
            entity.setIdNotaRilascio(this.getIdNotaRilascio().longValue());
        }
        if (this.getIdApplic() != null) {
            if (entity.getSiAplApplic() == null) {
                entity.setSiAplApplic(new SIAplApplic());
            }
            entity.getSiAplApplic().setIdApplic(this.getIdApplic().longValue());
        }
        entity.setCdVersione(this.getCdVersione());
        entity.setDtVersione(this.getDtVersione());
        entity.setDtIniVal(this.getDtIniVal());
        entity.setDtFineVal(this.getDtFineVal());
        entity.setDsEvidenza(this.getDsEvidenza());
        entity.setBlNota(this.getBlNota());
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
