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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package it.eng.parer.dispenser.util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.lang.NonNull;

import it.eng.dispenser.entity.constraint.ConstDipParamApplic;

/**
 *
 * @author Iacolucci_M
 */
public class DataSourcePropertiesFactoryBean implements FactoryBean<Properties> {

    private static final Logger log = LoggerFactory.getLogger(DataSourcePropertiesFactoryBean.class);

    private final Properties props = new Properties();

    public void setDataSource(@NonNull DataSource dataSource) {

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        String sql = "SELECT NM_PARAM_APPLIC, DS_VALORE_PARAM_APPLIC FROM DIP_PARAM_APPLIC";
        jdbcTemplate.query(sql, new ResultSetExtractor<Object>() {

            @Override
            public Object extractData(@NonNull ResultSet rs) throws SQLException, DataAccessException {
                while (rs.next()) {
                    props.put(rs.getString("NM_PARAM_APPLIC"), rs.getString("DS_VALORE_PARAM_APPLIC"));
                    log.debug("Props da DB: [{}]-[{}]", rs.getString("NM_PARAM_APPLIC"),
                            rs.getString("DS_VALORE_PARAM_APPLIC"));
                }
                /*
                 * Settaggio valori di default se assenti su db
                 */
                String valore = null;
                valore = (String) props.get(ConstDipParamApplic.NmParamApplic.CACHE_DELAY_POLLING.name());
                if (valore == null || valore.trim().equals("")) {
                    props.put(ConstDipParamApplic.NmParamApplic.CACHE_DELAY_POLLING.name(), "60000");
                }
                valore = (String) props.get(ConstDipParamApplic.NmParamApplic.SERVER_NAME_SYSTEM_PROPERTY.name());
                if (valore == null || valore.trim().equals("")) {
                    props.put(ConstDipParamApplic.NmParamApplic.SERVER_NAME_SYSTEM_PROPERTY.name(), "jboss.node.name");
                }

                return props;
            }
        });
    }

    @Override
    public Properties getObject() throws Exception {
        return props;
    }

    @Override
    public Class<?> getObjectType() {
        return Properties.class;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    public String getProperty(String property) {
        return getProperty(property, null);
    }

    public String getProperty(String property, String defaultValue) {
        String str = null;
        try {
            Properties p = this.getObject();
            str = p.getProperty(property, defaultValue);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return str;
    }

}
