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
package it.eng.dispenser.web.util;

import org.springframework.beans.factory.annotation.Autowired;

import it.eng.dispenser.entity.constraint.ConstDipParamApplic;
import it.eng.parer.dispenser.util.DataSourcePropertiesFactoryBean;
import it.eng.spagoLite.actions.application.ApplicationBaseProperties;
import it.eng.spagoLite.actions.application.IApplicationBasePropertiesSevice;

/**
 *
 * @author Iacolucci_M
 *
 *         Implementazione che fornisce al framework SpagoLite i dati essenziali dell'applicazione per poter utilizzare
 *         l'Help on line da IAM
 */
public class ApplicationBasePropertiesSeviceImpl implements IApplicationBasePropertiesSevice {

    @Autowired
    private DataSourcePropertiesFactoryBean applicationProperties;

    @Override
    public ApplicationBaseProperties getApplicationBaseProperties() {
        String nmApplic = applicationProperties.getProperty(ConstDipParamApplic.NmParamApplic.NM_APPLIC.name());
        String user = applicationProperties.getProperty(ConstDipParamApplic.NmParamApplic.USERID_RECUP_INFO.name());
        String password = applicationProperties.getProperty(ConstDipParamApplic.NmParamApplic.PSW_RECUP_INFO.name());
        String url = applicationProperties.getProperty(ConstDipParamApplic.NmParamApplic.URL_RECUP_HELP.name());
        // String url="http://localhost:8080/saceriam/rest/recuperoHelp.json";

        ApplicationBaseProperties prop = new ApplicationBaseProperties(nmApplic, user, password, url);

        return prop;
    }

}
