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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

/**
 *
 * @author Iacolucci_M
 */
public class CustomPlaceholder extends PropertySourcesPlaceholderConfigurer {

    // public static final String CONST_DISPENSER_CACHE_FIXED_DELAY_POLLING = "dispenser.cache.fixedDelayPolling";
    // public static final String CONST_DISPENSER_SYSTEM_PROPERTY_SERVER_NAME = "dispenser.system.property.server.name";

    public CustomPlaceholder() {
        this(null);
    }

    public CustomPlaceholder(Properties props) {
        Map<String, Object> loadedSettings = new HashMap<String, Object>();
        MutablePropertySources mutablePropertySources = new MutablePropertySources();
        if (props != null) {
            Enumeration<Object> en = props.keys();
            while (en.hasMoreElements()) {
                Object nextElement = en.nextElement();
                loadedSettings.put(nextElement.toString(), props.get(nextElement));
            }
        }
        /*
         * Settaggio valori di default se assenti su db
         */
        /*
         * String valore=null;
         * valore=(String)loadedSettings.get(ConstDipParamApplic.NmParamApplic.CACHE_DELAY_POLLING.name()); if
         * (valore==null||valore.trim().equals("")) {
         * loadedSettings.put(ConstDipParamApplic.NmParamApplic.CACHE_DELAY_POLLING.name(), "30000"); }
         * valore=(String)loadedSettings.get(ConstDipParamApplic.NmParamApplic.SERVER_NAME_SYSTEM_PROPERTY.name()); if
         * (valore==null||valore.trim().equals("")) {
         * loadedSettings.put(ConstDipParamApplic.NmParamApplic.SERVER_NAME_SYSTEM_PROPERTY.name(),
         * "com.sun.aas.instanceName"); }
         */
        mutablePropertySources.addFirst(new MapPropertySource("custom", loadedSettings));
        setPropertySources(mutablePropertySources);
    }
}
