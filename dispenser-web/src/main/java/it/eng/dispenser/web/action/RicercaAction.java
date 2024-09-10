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

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import it.eng.dispenser.entity.DipRicerca;
import it.eng.dispenser.slite.gen.Application;
import it.eng.dispenser.web.bo.RicercaBO;
import it.eng.dispenser.web.form.RicercheLoader;
import it.eng.dispenser.web.ricerca.validator.IValidator;
import it.eng.dispenser.util.Constants;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.SessionManager;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.table.BaseTable;
import it.eng.spagoLite.security.profile.Pagina;

import java.sql.Timestamp;

public class RicercaAction extends RicercaAbstractAction {

    private static final Logger logger = LoggerFactory.getLogger(RicercaAction.class);
    private static final int RICERCA_PAGE_SIZE = 100;

    @Autowired
    private RicercaBO ricercaBo;
    @Autowired
    private RicercheLoader ricercheLoader;
    @Autowired
    private ApplicationContext springContext;

    @Override
    public void initOnClick() throws EMFError {
        // CARICAMENTO
        boolean isAuth = checkAuthRic(getRequest().getParameter(Constants.REQ_HTTP_NOME_RIC_PARAM));
        if (isAuth) {
            setRicercaSelezionata(getRequest().getParameter(Constants.REQ_HTTP_NOME_RIC_PARAM));
            setVoceMenuSelezionata();
            forwardToPublisher(Application.Publisher.RICERCA);
        }

    }

    private boolean checkAuthRic(String nmRicerca) {
        DipRicerca r;
        if ((r = ricercheLoader.getRicerca(nmRicerca)) == null) {
            return false;
        }
        String menuEntry = r.getNmEntryMenu();
        Pagina p = (Pagina) getUser().getProfile().getChild(getLastPublisher());
        if (p != null && p.getChild(menuEntry) != null) {
            return true;
        } else {
            logger.debug("Utente " + getUser().getUsername() + " non autorizzato all'esecuzione del metodo " + menuEntry
                    + " in pagina " + getLastPublisher());
            getMessageBox().addFatal("Utente " + getUser().getUsername()
                    + " non autorizzato all'esecuzione dell'azione " + menuEntry + " in pagina " + getLastPublisher());
            return false;
        }

    }

    @Override
    public void loadDettaglio() throws EMFError {
        if (getNavigationEvent().equals(NE_DETTAGLIO_VIEW) || getNavigationEvent().equals(NE_NEXT)
                || getNavigationEvent().equals(NE_PREV)) {
            try {
                BaseRow row = ricercaBo.caricaDettaglioUd(
                        getForm().getRicercaList().getTable().getCurrentRow().getBigDecimal("id_unita_doc"),
                        getRicercaSelezionata(),
                        getForm().getRicercaList().getTable().getCurrentRow().getBigDecimal("id_strut").longValue());
                getForm().getFormDettaglio().copyFromBean(row);
            } catch (SQLException e) {
                throw new EMFError("Errore SQL: ", e);
            }
            forwardToPublisher(Application.Publisher.DETTAGLIO);
        }
    }

    @Override
    public void undoDettaglio() throws EMFError {
        // TODO Auto-generated method stub

    }

    @Override
    public void insertDettaglio() throws EMFError {
        // TODO Auto-generated method stub

    }

    @Override
    public void saveDettaglio() throws EMFError {
        // TODO Auto-generated method stub

    }

    @Override
    public void dettaglioOnClick() throws EMFError {

    }

    @Override
    public void elencoOnClick() throws EMFError {
        goBack();

    }

    @Override
    protected String getDefaultPublsherName() {
        // TODO Auto-generated method stub
        return Application.Publisher.RICERCA;
    }

    @Override
    public void reloadAfterGoBack(String publisherName) {
        // TODO Auto-generated method stub

    }

    @Override
    public String getControllerName() {
        // TODO Auto-generated method stub
        return Application.Actions.RICERCA;
    }

    public void ricerca() throws EMFError, SQLException {
        // TODO Auto-generated method stub
        getForm().getFormRicerca().post(getRequest());
        // VALIDARE LA FORM

        /* Se non e' stato configurato il validatore custom carica quello di default */
        IValidator valid = null;
        if (springContext.containsBean(getRicercaSelezionata() + "Validator")) {
            valid = getRicercaSelezionataValidator();
        } else {
            valid = getRicercaDefault();
        }
        BaseTable res = new BaseTable();
        if (getForm().getFormRicerca().validate(getMessageBox())
                && valid.validate(getForm().getFormRicerca(), getMessageBox())) {
            // ESEGUIRE LA RICERCA
            res = ricercaBo.eseguiRicerca(getForm().getFormRicerca(), getRicercaSelezionata());
        }
        getForm().getRicercaList().setTable(res);
        getForm().getRicercaList().getTable().setPageSize(RICERCA_PAGE_SIZE);
        getForm().getRicercaList().getTable().first();
        forwardToPublisher(Application.Publisher.RICERCA);

    }

    public void pulisci() throws EMFError {
        SessionManager.setForm(getSession(), newForm());
        forwardToPublisher(Application.Publisher.RICERCA);
    }

    private String getRicercaSelezionata() {
        return (String) getSession().getAttribute(Constants.SESSIONE_NOME_RIC);
    }

    private void setRicercaSelezionata(String nomeRicerca) {
        getSession().setAttribute(Constants.SESSIONE_NOME_RIC, nomeRicerca);
    }

    private IValidator getRicercaSelezionataValidator() {
        return (IValidator) springContext.getBean(getRicercaSelezionata() + "Validator");

    }

    private IValidator getRicercaDefault() {
        return (IValidator) springContext.getBean("DefaultValidator");

    }

    public void ultimaDataSyncro() throws JSONException {

        Timestamp res = ricercaBo.getUltimaDataSyncro(getRicercaSelezionata());
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String date = (res != null) ? sdf.format(res) : null;
        JSONObject obj = new JSONObject().put("ultimaDataSyncro", date);
        redirectToAjax(obj);

    }

    /*
     * Torna alla pagina JSP il numero di records estratti nella ricerca
     */
    public void numeroRecordsEstratti() throws JSONException {
        JSONObject obj = new JSONObject().put("maxRecordsPerQuery", RicercaBO.RICERCA_MAX_RESULTS);
        if (getForm().getRicercaList().getTable() != null) {
            obj.put("numeroRecordsEstratti", getForm().getRicercaList().getTable().size());
        } else {
            obj.put("numeroRecordsEstratti", 0);
        }
        redirectToAjax(obj);
    }

    public void customLogic(String... parameters) {
        String str = "";
        if (parameters != null) {

            for (int i = 0; i < parameters.length; i++) {
                if (i == 0) {
                    str = "?operation";
                }
                str += "__" + parameters[i];
            }

        }
        redirectToAction(getRicercaSelezionata() + ".html", str, getForm());
    }

    public void setVoceMenuSelezionata() {
        getUser().getMenu().reset();
        getUser().getMenu().select(ricercaBo.getNmEntryMenuRicerca(getRicercaSelezionata()));
    }

    /*
     * Ridefinisco questo metodo in modo che le superclassi sappiano che la Action è relativa a delle pagine gestite
     * dinamicamente con una jsp sola e quindi anteporranno alla normale destination (es. /ricerca/ricerca) il suffisso
     * ["nomeOrganizzazione"] dove il contenuto di nomeorganizzazione viene restituito da questo metodo.
     */
    @Override
    protected String getNomeOrganizzazione(String destination) {
        switch (destination) {
        case Application.Publisher.RICERCA:
        case Application.Publisher.DETTAGLIO:
            return getRicercaSelezionata();
        default:
            return null;
        }
    }

}
