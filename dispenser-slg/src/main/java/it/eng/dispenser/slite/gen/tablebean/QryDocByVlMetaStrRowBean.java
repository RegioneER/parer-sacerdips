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

import it.eng.dispenser.entity.QryDocByVlMetaStr;
import it.eng.dispenser.entity.QryDocByVlMetaStrId;
import it.eng.spagoLite.db.base.JEEBaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.oracle.bean.column.TableDescriptor;

/**
 * RowBean per la tabella Qry_Doc_By_Vl_Meta_Str
 *
 */
public class QryDocByVlMetaStrRowBean extends BaseRow implements JEEBaseRowInterface {

    /*
     * @Generated( value = "it.eg.dbtool.db.oracle.beangen.Oracle4JPAClientBeanGen$TableBeanWriter", comments =
     * "This class was generated by OraTool", date = "Wednesday, 27 May 2015 11:25" )
     */

    private static final long serialVersionUID = 1L;
    public static QryDocByVlMetaStrTableDescriptor TABLE_DESCRIPTOR = new QryDocByVlMetaStrTableDescriptor();

    public QryDocByVlMetaStrRowBean() {
        super();
    }

    public TableDescriptor getTableDescriptor() {
        return TABLE_DESCRIPTOR;
    }

    // getter e setter
    public BigDecimal getIdAttribDatiSpec() {
        return getBigDecimal("id_attrib_dati_spec");
    }

    public void setIdAttribDatiSpec(BigDecimal idAttribDatiSpec) {
        setObject("id_attrib_dati_spec", idAttribDatiSpec);
    }

    public BigDecimal getIdDoc() {
        return getBigDecimal("id_doc");
    }

    public void setIdDoc(BigDecimal idDoc) {
        setObject("id_doc", idDoc);
    }

    public BigDecimal getIdUnitaDoc() {
        return getBigDecimal("id_unita_doc");
    }

    public void setIdUnitaDoc(BigDecimal idUnitaDoc) {
        setObject("id_unita_doc", idUnitaDoc);
    }

    public String getDlPrefissoValore() {
        return getString("dl_prefisso_valore");
    }

    public void setDlPrefissoValore(String dlPrefissoValore) {
        setObject("dl_prefisso_valore", dlPrefissoValore);
    }

    public String getDlValore() {
        return getString("dl_valore");
    }

    public void setDlValore(String dlValore) {
        setObject("dl_valore", dlValore);
    }

    @Override
    public void entityToRowBean(Object obj) {
        QryDocByVlMetaStr entity = (QryDocByVlMetaStr) obj;
        this.setIdAttribDatiSpec(entity.getQryDocByVlMetaStrId().getIdAttribDatiSpec());
        this.setIdDoc(entity.getQryDocByVlMetaStrId().getIdDoc());
        this.setIdUnitaDoc(entity.getQryDocByVlMetaStrId().getIdUnitaDoc());
        this.setDlPrefissoValore(entity.getDlPrefissoValore());
        this.setDlValore(entity.getDlValore());
    }

    @Override
    public QryDocByVlMetaStr rowBeanToEntity() {
        QryDocByVlMetaStrId id = new QryDocByVlMetaStrId();
        id.setIdAttribDatiSpec(this.getIdAttribDatiSpec());
        id.setIdDoc(this.getIdDoc());
        id.setIdUnitaDoc(this.getIdUnitaDoc());
        QryDocByVlMetaStr entity = new QryDocByVlMetaStr();
        entity.setQryDocByVlMetaStrId(id);
        entity.setDlPrefissoValore(this.getDlPrefissoValore());
        entity.setDlValore(this.getDlValore());
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
