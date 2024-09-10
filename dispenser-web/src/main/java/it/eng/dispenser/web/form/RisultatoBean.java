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

package it.eng.dispenser.web.form;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RisultatoBean {

    private Map<Long, List<String>> metadati = new HashMap<Long, List<String>>();
    private Map<Long, List<String>> datiProfilo = new HashMap<Long, List<String>>();
    private Map<Long, List<String>> queryCalcolo = new HashMap<Long, List<String>>();
    private Set<Long> listaStrutture = new HashSet<Long>();
    private List<String> orderByList = new ArrayList<String>();
    private int maxResults;

    public List<String> getMetadatiByIdStrut(Long idStrut) {
        return metadati.get(idStrut);
    }

    public List<String> getDatiProfiloByIdStrut(Long idStrut) {
        return datiProfilo.get(idStrut);
    }

    public List<String> getQueryCalcoloByIdStrut(Long idStrut) {
        return queryCalcolo.get(idStrut);
    }

    public Set<Long> getListaStrutture() {
        return listaStrutture;
    }

    public List<String> getOrderByList() {
        return orderByList;
    }

    public void putMetadato(Long idStrut, String value) {
        List<String> md;
        if ((md = metadati.get(idStrut)) == null) {
            md = new ArrayList<String>();
            md.add(value);
            metadati.put(idStrut, md);
        } else {
            md.add(value);
        }
    }

    public void putMetadatoUnico(Long idStrut, String value) {
        List<String> md;
        if ((md = metadati.get(idStrut)) == null) {
            md = new ArrayList<String>();
            md.add(value);
            metadati.put(idStrut, md);
        } else {
            // Lo inserisce solo una volta!
            if (!md.contains(value)) {
                md.add(value);
            }
        }
    }

    public void putDatoProfilo(Long idStrut, String value) {
        List<String> dp;
        if ((dp = datiProfilo.get(idStrut)) == null) {
            dp = new ArrayList<String>();
            dp.add(value);
            datiProfilo.put(idStrut, dp);
        } else {
            dp.add(value);
        }
    }

    public void putQueryCalcolo(Long idStrut, String value) {
        List<String> dp;
        if ((dp = queryCalcolo.get(idStrut)) == null) {
            dp = new ArrayList<String>();
            dp.add(value);
            queryCalcolo.put(idStrut, dp);
        } else {
            dp.add(value);
        }
    }

    public boolean addIdStruttura(Long e) {
        return listaStrutture.add(e);
    }

    public boolean addOrderbyClause(String e) {
        return orderByList.add(e);
    }

    public int getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

}
