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

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;

import it.eng.spagoLite.db.base.BaseTableInterface;

/**
 *
 * @author DiLorenzo_F
 */
public class NavigatorBeanManager implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<NavigatorBean> navigatorBeanStack;

    private static final String NAVIGATOR_BEAN_SESSION_MANAGER = "NAV_SESS_MANAGER";

    /*
     * Restituisce il BeanManager dalla sessione e lo crea se nullo
     */
    public static NavigatorBeanManager getNavigatorBeanManager(HttpSession session) {
        NavigatorBeanManager nbm = (NavigatorBeanManager) session.getAttribute(NAVIGATOR_BEAN_SESSION_MANAGER);
        if (nbm == null) {
            nbm = new NavigatorBeanManager();
            session.setAttribute(NAVIGATOR_BEAN_SESSION_MANAGER, nbm);
        }
        return nbm;
    }

    public NavigatorBeanManager() {
        navigatorBeanStack = new ArrayList<>();
    }

    public List<NavigatorBean> pushNavigatorBeanStack(BigDecimal idObjectDetail1, BigDecimal idObjectDetail2,
            String sourceList, BaseTableInterface<?> sourceTable, int currentRowIndex, int pageSize, int level) {
        navigatorBeanStack.add(new NavigatorBean(idObjectDetail1, idObjectDetail2, sourceList, sourceTable,
                currentRowIndex, pageSize, level));
        return navigatorBeanStack;
    }

    public NavigatorBean popNavigatorBeanStack() {
        NavigatorBean last = null;
        if (navigatorBeanStack != null && !navigatorBeanStack.isEmpty()) {
            last = navigatorBeanStack.remove(navigatorBeanStack.size() - 1);
        }
        return last;
    }

    public NavigatorBean getLastNavigatorBeanStack() {
        NavigatorBean last = null;
        if (navigatorBeanStack != null && !navigatorBeanStack.isEmpty()) {
            last = navigatorBeanStack.get(navigatorBeanStack.size() - 1);
        }
        return last;
    }

    public void resetNavigatorBeanStack() {
        navigatorBeanStack = new ArrayList<>();
    }

    public List<NavigatorBean> getNavigatorBeanStack() {
        return navigatorBeanStack;
    }

    public class NavigatorBean implements Serializable {

        private static final long serialVersionUID = 1L;
        BigDecimal idObject1;
        BigDecimal idObject2;
        String sourceList;
        BaseTableInterface<?> sourceTable;
        int currentRowIndex;
        int pageSize;
        int level = 1;

        public NavigatorBean(BigDecimal idObject1, BigDecimal idObject2, String sourceList,
                BaseTableInterface<?> sourceTable, int currentRowIndex, int pageSize, int level) {
            this.idObject1 = idObject1;
            this.idObject2 = idObject2;
            this.sourceTable = sourceTable;
            this.sourceList = sourceList;
            this.currentRowIndex = currentRowIndex;
            this.pageSize = pageSize;
            this.level = level;
        }

        public BigDecimal getIdObject1() {
            return idObject1;
        }

        public void setIdObject1(BigDecimal idObject1) {
            this.idObject1 = idObject1;
        }

        public BigDecimal getIdObject2() {
            return idObject2;
        }

        public void setIdObject2(BigDecimal idObject2) {
            this.idObject2 = idObject2;
        }

        public String getSourceList() {
            return sourceList;
        }

        public void setSourceList(String sourceList) {
            this.sourceList = sourceList;
        }

        public BaseTableInterface<?> getSourceTable() {
            return sourceTable;
        }

        public void setSourceTable(BaseTableInterface<?> sourceTable) {
            this.sourceTable = sourceTable;
        }

        public int getCurrentRowIndex() {
            return currentRowIndex;
        }

        public void setCurrentRowIndex(int currentRowIndex) {
            this.currentRowIndex = currentRowIndex;
        }

        public int getPageSize() {
            return pageSize;
        }

        public void setPageSize(int pageSize) {
            this.pageSize = pageSize;
        }

        public void addLevel() {
            this.level++;
        }

        public int getLevel() {
            return level;
        }

    }
}
