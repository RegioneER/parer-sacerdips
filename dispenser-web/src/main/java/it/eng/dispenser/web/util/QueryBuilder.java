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

package it.eng.dispenser.web.util;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Query;

public class QueryBuilder {

    private final StringBuilder sb = new StringBuilder();
    private final List<Object> params = new ArrayList<Object>();

    public void addQueryForIntersect(String query, Object... parametri) {
        if (sb.length() > 0) {
            sb.append(" INTERSECT ");
        }
        sb.append("(");
        sb.append(query);
        sb.append(")");
        for (Object param : parametri)
            params.add(param);
    }

    public void addQueryForUnion(String query) {
        if (sb.length() > 0) {
            sb.append(" UNION ");
        }
        sb.append("(");
        sb.append(query);
        sb.append(")");
    }

    public void addQueryForUnion(String query, Object... parametri) {
        if (sb.length() > 0) {
            sb.append(" UNION ");
        }
        sb.append("(");
        sb.append(query);
        sb.append(")");
        for (Object param : parametri)
            params.add(param);
    }

    /**
     * Add order by clause and wrap the query in a select * from (...) statement
     *
     * @param orderByCommaSepList
     *            order by list
     */
    public void addOrderByClause(String orderByCommaSepList) {
        sb.insert(0, "SELECT * FROM (");
        sb.append(") ORDER BY ");
        sb.append(orderByCommaSepList);
    }

    public void addLimitClauseOra12c(int maxrows) {
        sb.append(" FETCH FIRST ");
        sb.append(maxrows);
        sb.append(" ROWS ONLY");
    }

    public String returnQuery() {
        return sb.toString();
    }

    public void setQueryParams(Query q2) {
        for (int i = 0, j = 1; i < params.size(); i++, j++)
            q2.setParameter(j, params.get(i));

    }

}
