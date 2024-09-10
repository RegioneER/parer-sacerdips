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

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import it.eng.dispenser.slite.gen.Application;
import it.eng.dispenser.slite.gen.action.NoteRilascioAbstractAction;
import it.eng.dispenser.slite.gen.form.NoteRilascioForm;
import it.eng.dispenser.slite.gen.tablebean.SIAplNotaRilascioRowBean;
import it.eng.dispenser.slite.gen.tablebean.SIAplNotaRilascioTableBean;
import it.eng.dispenser.slite.gen.tablebean.SIAplNotaRilascioTableDescriptor;
import it.eng.dispenser.web.bo.NoteRilascioBO;
import it.eng.dispenser.util.Constants;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.db.base.BaseTableInterface;
import it.eng.spagoLite.db.base.sorting.SortingRule;
import it.eng.spagoLite.form.base.BaseElements.Status;
import it.eng.spagoLite.form.base.BaseForm;
import it.eng.spagoLite.form.fields.Field;
import it.eng.spagoLite.form.fields.Fields;
import it.eng.spagoLite.form.fields.SingleValueField;
import it.eng.spagoLite.security.Secure;

/**
 *
 * @author DiLorenzo_F
 */
public class NoteRilascioAction extends NoteRilascioAbstractAction {

    private static Logger logger = LoggerFactory.getLogger(NoteRilascioAction.class.getName());

    @Autowired
    private NoteRilascioBO noteRilascioBO;

    @Override
    public void initOnClick() throws EMFError {
    }

    @Override
    public void insertDettaglio() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void update(Fields<Field> fields) throws EMFError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void delete(Fields<Field> fields) throws EMFError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void loadDettaglio() throws EMFError {
        String lista = getTableName();
        if (lista != null) {
            // Se carico il dettaglio della nota di rilascio
            if (lista.equals(getForm().getListaNoteRilascio().getName())) {
                // Imposto lista e dettaglio in modalitÃ  visualizzazione
                getForm().getListaNoteRilascio().setStatus(Status.view);
                // getForm().getListaNoteRilascio().setHideDeleteButton(true);
                getForm().getDettaglioNoteRilascio().setViewMode();

                // Carico le info del dettaglio NOTE DI RILASCIO
                if (!((SIAplNotaRilascioTableBean) getForm().getListaNoteRilascio().getTable()).isEmpty()) {
                    BigDecimal idNotaRilascio = ((SIAplNotaRilascioTableBean) getForm().getListaNoteRilascio()
                            .getTable()).getCurrentRow().getIdNotaRilascio();
                    SIAplNotaRilascioRowBean detailRow = noteRilascioBO.getAplNotaRilascioRowBean(idNotaRilascio);
                    getForm().getDettaglioNoteRilascio().copyFromBean(detailRow);

                    Calendar today = GregorianCalendar.getInstance();
                    today.set(Calendar.HOUR_OF_DAY, 0);
                    today.set(Calendar.MINUTE, 0);
                    today.set(Calendar.SECOND, 0);
                    today.set(Calendar.MILLISECOND, 0);

                    // if (getForm().getDettaglioNoteRilascio().getDt_fine_val().parse() == null ||
                    // (getForm().getDettaglioNoteRilascio().getDt_fine_val().parse() != null &&
                    // today.getTime().compareTo(getForm().getDettaglioNoteRilascio().getDt_fine_val().parse()) <= 0)
                    // && today.getTime().compareTo(getForm().getDettaglioNoteRilascio().getDt_ini_val().parse()) >= 0)
                    // {
                    getRequest().setAttribute("showNotaRilascio", true);
                    // } else {
                    // getRequest().setAttribute("infoDaMostrare", "Informazioni non disponibili");
                    // }
                    // Carico da DB la lista delle note di rilascio precedenti associate all'applicazione
                    SIAplNotaRilascioTableBean noteRilascioPrecTableBean = noteRilascioBO
                            .getAplNoteRilascioPrecTableBean(detailRow.getBigDecimal("id_applic"), idNotaRilascio,
                                    new Date(detailRow.getTimestamp("dt_versione").getTime()));
                    getForm().getNoteRilascioPrecedentiList().setTable(noteRilascioPrecTableBean);
                    getForm().getNoteRilascioPrecedentiList().getTable().setPageSize(10);
                    getForm().getNoteRilascioPrecedentiList().getTable()
                            .addSortingRule(SIAplNotaRilascioTableDescriptor.COL_DT_VERSIONE, SortingRule.DESC);
                    getForm().getNoteRilascioPrecedentiList().getTable().sort();
                    getForm().getNoteRilascioPrecedentiList().getTable().first();
                    // Disabilito i tasti sulla lista note di rilascio precedenti
                    getForm().getNoteRilascioPrecedentiList().setUserOperations(true, false, false, false);
                } else {
                    getRequest().setAttribute("infoDaMostrare", "Informazioni non disponibili");
                }
            }
        }
    }

    @Override
    public void undoDettaglio() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void saveDettaglio() throws EMFError {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public void dettaglioOnClick() throws EMFError {
        String lista = getTableName();
        if (lista != null) {
            if (getForm().getListaNoteRilascio().getName().equals(lista)) {
                forwardToPublisher(Application.Publisher.DETTAGLIO_NOTE_RILASCIO);
            } else if (getTableName().equals(getForm().getNoteRilascioPrecedentiList().getName())) {
                redirectToNoteRilascioPage();
            }
        }
    }

    @Override
    public void elencoOnClick() throws EMFError {
        goBack();
    }

    @Override
    protected String getDefaultPublsherName() {
        return Application.Publisher.DETTAGLIO_NOTE_RILASCIO;
    }

    @Override
    public void reloadAfterGoBack(String publisherName) {
        try {
            if (publisherName.equals(Application.Publisher.DETTAGLIO_NOTE_RILASCIO)) {
                Calendar today = GregorianCalendar.getInstance();
                today.set(Calendar.HOUR_OF_DAY, 0);
                today.set(Calendar.MINUTE, 0);
                today.set(Calendar.SECOND, 0);
                today.set(Calendar.MILLISECOND, 0);
                // if (getForm().getDettaglioNoteRilascio().getDt_fine_val().parse() == null ||
                // (getForm().getDettaglioNoteRilascio().getDt_fine_val().parse() != null &&
                // today.getTime().compareTo(getForm().getDettaglioNoteRilascio().getDt_fine_val().parse()) <= 0)
                // && today.getTime().compareTo(getForm().getDettaglioNoteRilascio().getDt_ini_val().parse()) >= 0) {
                getRequest().setAttribute("showNotaRilascio", true);
                // } else {
                // getRequest().setAttribute("infoDaMostrare", "Informazioni non disponibili");
                // }
                getSession().removeAttribute("isFromNotaRilascioPrec");
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    @Override
    public String getControllerName() {
        return Application.Actions.NOTE_RILASCIO;
    }

    @Secure(action = "Menu.Informazioni.NoteRilascioVersCor")
    public void ricercaNoteRilascioPage() throws EMFError {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.Informazioni.NoteRilascioVersCor");

        getForm().getListaNoteRilascio().clear();
        getForm().getListaNoteRilascio().setStatus(Status.view);

        // Azzero in sessione l'attributo che mi tiene conto della lista delle note di rilascio precedenti
        getSession().removeAttribute("isFromNotaRilascioPrec");

        ricercaNoteRilascio(Constants.SACER_DIPS);
    }

    /**
     * Ricerca note di rilascio in base all'applicazione passata in input
     *
     * @param nmApplic
     *            jome applicazione
     *
     * @throws EMFError
     *             errore generico
     */
    public void ricercaNoteRilascio(String nmApplic) throws EMFError {

        SIAplNotaRilascioTableBean noteRilascioTableBean = noteRilascioBO.getAplNoteRilascioTableBean(nmApplic);
        getForm().getListaNoteRilascio().setTable(noteRilascioTableBean);
        getForm().getListaNoteRilascio().getTable().first();
        getForm().getListaNoteRilascio().getTable().setPageSize(10);
        getForm().getListaNoteRilascio().setStatus(Status.view);
        getForm().getListaNoteRilascio().setHideDeleteButton(false);
        setTableName(getForm().getListaNoteRilascio().getName());
        loadDettaglio();
    }

    private void redirectToNoteRilascioPage() throws EMFError {
        NoteRilascioForm form = new NoteRilascioForm();
        form.getListaNoteRilascio()
                .setFilterValidRecords(getForm().getNoteRilascioPrecedentiList().isFilterValidRecords());
        form.getListaNoteRilascio().setUserOperations(true, false, false, false);
        getSession().setAttribute("isFromNotaRilascioPrec", true);
        redirectToPage(Application.Actions.NOTE_RILASCIO, form, form.getListaNoteRilascio().getName(),
                getForm().getNoteRilascioPrecedentiList().getTable(), getNavigationEvent());
    }

    @SuppressWarnings("unchecked")
    private void redirectToPage(final String action, BaseForm form, String listToPopulate, BaseTableInterface<?> table,
            String event) throws EMFError {
        ((it.eng.spagoLite.form.list.List<SingleValueField<?>>) form.getComponent(listToPopulate)).setTable(table);
        redirectToAction(action, "?operation=listNavigationOnClick&navigationEvent=" + event + "&table="
                + listToPopulate + "&riga=" + table.getCurrentRowIndex(), form);
    }
}
