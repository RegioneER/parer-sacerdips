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

package it.eng.dispenser.web.action;

import org.springframework.beans.factory.annotation.Autowired;

import it.eng.dispenser.web.form.DynamicSpagoLiteForm;
import it.eng.dispenser.web.form.RicercheLoader;
import it.eng.dispenser.util.Constants;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.actions.form.ListAction;
import it.eng.spagoLite.form.fields.Field;
import it.eng.spagoLite.form.fields.Fields;
import it.eng.spagoLite.form.list.List;
import it.eng.spagoLite.form.wizard.Wizard;
import it.eng.spagoLite.form.wizard.WizardElement;
import it.eng.spagoLite.security.User;

public abstract class RicercaAbstractAction extends ListAction<DynamicSpagoLiteForm, User> {

    @Autowired
    protected RicercheLoader formloader;

    @Override
    public DynamicSpagoLiteForm newForm() {
        String nomeRicerca = getRequest().getParameter(Constants.REQ_HTTP_NOME_RIC_PARAM) != null
                ? getRequest().getParameter(Constants.REQ_HTTP_NOME_RIC_PARAM)
                : (String) getSession().getAttribute(Constants.SESSIONE_NOME_RIC);
        return formloader.getSpagoLiteForm(nomeRicerca);
    }

    public abstract void initOnClick() throws EMFError;

    /**
     * Manager per le operazioni di tipo "update"
     *
     * @param field
     *            elementi di tipo {@link Field}
     *
     * @throws EMFError
     *             errore generico
     */
    public void update(Fields<Field> field) throws EMFError {
        if (getForm().getFormRicerca().equals(field)) {
            updateFormRicerca();
        } else {
            return;
        }
    }

    /**
     * Manager per le operazioni di tipo "select"
     *
     * @param field
     *            elementi di tipo {@link Field}
     *
     * @throws EMFError
     *             errore generico
     */
    public void select(Fields<Field> field) throws EMFError {
        if (getForm().getFormRicerca().equals(field)) {
            selectFormRicerca();
        } else {
            return;
        }
    }

    /**
     * Manager per le operazioni di tipo "delete"
     *
     * @param field
     *            elementi di tipo {@link Field}
     *
     * @throws EMFError
     *             errore generico
     */
    public void delete(Fields<Field> field) throws EMFError {
        if (getForm().getFormRicerca().equals(field)) {
            deleteFormRicerca();
        } else {
            return;
        }
    }

    // Operazioni disponibili per la lista FormRicerca
    public void updateFormRicerca() throws EMFError {
    }

    public void selectFormRicerca() throws EMFError {
    }

    public void deleteFormRicerca() throws EMFError {
    }

    /**
     * Manager per le operazioni di tipo "update"
     *
     * @param list
     *            elementi di tipo {@link Field}
     *
     * @throws EMFError
     *             errore generico
     */
    public void update(List<?> list) throws EMFError {
        if (getForm().getRicercaList().equals(list)) {
            updateRicercaList();
        } else {
            return;
        }
    }

    /**
     * Manager per le operazioni di tipo "select"
     *
     * @param list
     *            lista elementi generici
     *
     * @throws EMFError
     *             errore generico
     */
    public void select(List<?> list) throws EMFError {
        if (getForm().getRicercaList().equals(list)) {
            selectRicercaList();
        } else {
            return;
        }
    }

    /**
     * Manager per le operazioni di tipo "delete"
     *
     * @param list
     *            lista elementi generici
     *
     * @throws EMFError
     *             errore generico
     */
    public void delete(List<?> list) throws EMFError {
        if (getForm().getRicercaList().equals(list)) {
            deleteRicercaList();
        } else {
            return;
        }
    }

    /**
     * Manager per le operazioni di tipo "postLazyLoad"
     *
     * @param list
     *            lista elementi generici
     *
     * @throws EMFError
     *             errore generico
     */
    public void postLazyLoad(List<?> list) throws EMFError {
        if (getForm().getRicercaList().equals(list)) {
            postLazyLoadRicercaList();
        } else {
            return;
        }
    }

    // Operazioni disponibili per la lista RicercaList
    public void updateRicercaList() throws EMFError {
    }

    public void selectRicercaList() throws EMFError {
    }

    public void deleteRicercaList() throws EMFError {
    }

    public void postLazyLoadRicercaList() throws EMFError {
    }

    /**
     * Manager per le operazioni di tipo "filterInactiveRecords"
     *
     * @param list
     *            lista elementi generici
     *
     * @throws EMFError
     *             errore generico
     */
    public void filterInactiveRecords(List<?> list) throws EMFError {
        if (getForm().getRicercaList().equals(list)) {
            filterInactiveRecordsRicercaList();
        } else {
            return;
        }
    }

    public void filterInactiveRecordsRicercaList() throws EMFError {
    }

    public final void wizard(Wizard wizard, WizardElement element, Wizard.WizardNavigation wizardNavigation)
            throws EMFError {
    }
}
