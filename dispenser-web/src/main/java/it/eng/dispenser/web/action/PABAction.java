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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.http.message.BasicNameValuePair;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import it.eng.dispenser.bean.RecuperoWSBean;
import it.eng.dispenser.component.JAXBSingleton;
import it.eng.dispenser.entity.constraint.ConstDipParamApplic;
import it.eng.dispenser.helper.DispenserHelper;
import it.eng.dispenser.slite.gen.Application;
import it.eng.dispenser.slite.gen.action.PABAbstractAction;
import it.eng.dispenser.slite.gen.form.PABForm;
import it.eng.dispenser.slite.gen.viewbean.AroVLisCompDocRowBean;
import it.eng.dispenser.slite.gen.viewbean.AroVLisCompDocTableBean;
import it.eng.dispenser.slite.gen.viewbean.AroVLisDocRowBean;
import it.eng.dispenser.slite.gen.viewbean.AroVLisDocTableBean;
import it.eng.dispenser.util.Constants;
import it.eng.dispenser.web.bo.PievesestinaBO;
import it.eng.dispenser.web.bo.RicercaBO;
import it.eng.dispenser.web.form.DynamicSpagoLiteForm;
import it.eng.dispenser.web.form.RicercheLoader;
import it.eng.dispenser.web.util.NavigatorBeanManager;
import it.eng.dispenser.ws.dto.EsitoConnessione;
import it.eng.dispenser.ws.dto.RichiestaWSInput;
import it.eng.dispenser.ws.dto.RichiestaWSInput.TipoRichiesta;
import it.eng.dispenser.ws.util.RichiestaWSClient;
import it.eng.parer.dispenser.util.DataSourcePropertiesFactoryBean;
import it.eng.parer.ws.xml.versReqStato.ChiaveType;
import it.eng.parer.ws.xml.versReqStato.Recupero;
import it.eng.parer.ws.xml.versReqStato.TokenFileNameType;
import it.eng.parer.ws.xml.versReqStato.VersatoreType;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.SessionManager;
import it.eng.spagoLite.actions.form.ListAction;
import it.eng.spagoLite.db.base.BaseRowInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.form.Component;
import it.eng.spagoLite.form.base.BaseElements.Status;
import it.eng.spagoLite.form.fields.Field;
import it.eng.spagoLite.form.fields.Fields;
import it.eng.spagoLite.form.fields.SingleValueField;

/**
 *
 * @author Iacolucci_M
 */
public class PABAction extends PABAbstractAction {

    @Autowired
    private PievesestinaBO pievesestinaBO;
    @Autowired
    private RicercaBO ricercaBo;
    @Autowired
    private RicercheLoader ricercheLoader;
    @Autowired
    private RichiestaWSClient richiestaWSClient;
    @Autowired
    private JAXBSingleton jaxbSingleton;
    @Autowired
    private DataSourcePropertiesFactoryBean applicationProperties;
    @EJB(mappedName = "java:app/Dispenser-ejb/DispenserHelper")
    private DispenserHelper dispenserHelper;

    private org.slf4j.Logger logger = LoggerFactory.getLogger(PABAction.class);

    private static final String ID_UNITA_DOC_NAME = "id_unita_doc";
    private static final String ID_STRUT_NAME = "id_strut";
    private static final String CODICE_INTERVENTO_NAME = "CODICE_INTERVENTO";

    @Override
    public void initOnClick() throws EMFError {
        // Non usato
    }

    @Override
    public void insertDettaglio() throws EMFError {
        // Non usato
    }

    @Override
    public void update(Fields<Field> fields) throws EMFError {
        // Non usato
    }

    @Override
    public void delete(Fields<Field> fields) throws EMFError {
        // Non usato
    }

    @SuppressWarnings("rawtypes")
    @Override
    public void loadDettaglio() throws EMFError {
        try {
            if (getNavigationEvent().equals(NE_DETTAGLIO_VIEW) || getNavigationEvent().equals(NE_NEXT)
                    || getNavigationEvent().equals(NE_PREV)) {
                if (getTableName().equals(getForm().getDocumentiUDList().getName())) {
                    /* Recupero i dati che mi servono */
                    BigDecimal idUd = null;
                    String nmTipoDoc = null;
                    AroVLisDocRowBean currentRow = (AroVLisDocRowBean) getForm().getDocumentiUDList().getTable()
                            .getCurrentRow();

                    idUd = (BigDecimal) currentRow.getObject(ID_UNITA_DOC_NAME);
                    nmTipoDoc = (String) currentRow.getObject("nm_tipo_doc");

                    getForm().getTipoDocumentoDetailFake().copyFromBean(currentRow);

                    // CARICARE LA LISTA DEI COMPONENTI
                    AroVLisCompDocTableBean compDoc = ricercaBo.getAroVLisCompDocTableBean(idUd, nmTipoDoc);
                    getForm().getCompDocList().setTable(compDoc);
                    getForm().getCompDocList().getTable().first();
                    getForm().getCompDocList().getTable().setPageSize(10);

                    forwardToPublisher(Application.Publisher.DETTAGLIO_PABTIPO_DOC);
                } else {
                    try {
                        logger.info("Caricamento dettaglio progetto pab");
                        // Salvo in sessione il "tipo" di lista dalla quale "proviene" l'ente, sarà RicercaList
                        String listName = DynamicSpagoLiteForm.getRicercaList(getForm()).getName();
                        // Prendendo i dati dalla form, popolo un oggetto List generico del tipo gestito dal framework
                        // naturalmente
                        it.eng.spagoLite.form.list.List formList = ((it.eng.spagoLite.form.list.List) getForm()
                                .getComponent(listName));
                        // Ricavo la riga corrente, recuperata dall'oggetto Table della lista
                        BaseRowInterface listRow = formList.getTable().getCurrentRow();
                        // Se nullo il relativo parametro, siamo per forza al primo livello di annidamento
                        BigDecimal level = getForm().getLevel().getLevel_progetto_pab().parse() != null
                                ? getForm().getLevel().getLevel_progetto_pab().parse() : BigDecimal.ONE;
                        // Carico il dettaglio strumento urbanistico
                        BigDecimal idUnitaDoc = listRow.getBigDecimal(ID_UNITA_DOC_NAME);
                        BaseRow row = ricercaBo.caricaDettaglioUd(idUnitaDoc, getRicercaSelezionata(),
                                DynamicSpagoLiteForm.getRicercaList(getForm()).getTable().getCurrentRow()
                                        .getBigDecimal(ID_STRUT_NAME).longValue());
                        DynamicSpagoLiteForm.getFormDettaglio(getForm()).copyFromBean(row);
                        //
                        AroVLisDocTableBean listDocUd = ricercaBo
                                .getAroVLisDocTableBean((BigDecimal) row.getBigDecimal(ID_UNITA_DOC_NAME));
                        int docUDPageSize = 10;
                        if (getForm().getDocumentiUDList().getTable() != null) {
                            docUDPageSize = getForm().getDocumentiUDList().getTable().getPageSize();
                        }
                        getForm().getDocumentiUDList().setTable(listDocUd);
                        getForm().getDocumentiUDList().getTable().setPageSize(docUDPageSize);
                        getForm().getDocumentiUDList().getTable().first();
                        // Se sto scorrendo orizzontalmente da un record all'altro
                        if (ListAction.NE_NEXT.equals(getNavigationEvent())
                                || ListAction.NE_PREV.equals(getNavigationEvent())) {
                            // Se l'ultimo record di navigazione è presente e il livello di annidamento è lo stesso
                            if (NavigatorBeanManager.getNavigatorBeanManager(getSession())
                                    .getLastNavigatorBeanStack() != null
                                    && level.intValue() == NavigatorBeanManager.getNavigatorBeanManager(getSession())
                                            .getLastNavigatorBeanStack().getLevel()) {
                                // Estraggo il record di navigazione
                                NavigatorBeanManager.getNavigatorBeanManager(getSession()).popNavigatorBeanStack();
                                // Rimango allo stesso livello perchè sto scorrendo orizzontalmente
                                getForm().getLevel().getLevel_progetto_pab().setValue(String.valueOf(level));
                            } // Altrimenti significa che devo aggiungere un livello di navigazione
                            else {
                                int nextLevel = NavigatorBeanManager.getNavigatorBeanManager(getSession())
                                        .getLastNavigatorBeanStack().getLevel() + 1;
                                getForm().getLevel().getLevel_progetto_pab().setValue(String.valueOf(nextLevel));
                            }
                        } // Se invece sto andando in un dettaglio di un collegamento, sto andando ad un successivo
                          // livello di profondità
                        else {
                            int nextLevel = NavigatorBeanManager.getNavigatorBeanManager(getSession())
                                    .getLastNavigatorBeanStack().getLevel() + 1;
                            getForm().getLevel().getLevel_progetto_pab().setValue(String.valueOf(nextLevel));
                        }

                        // Ora che ho "gestito" pocanzi i livelli di annidamento, mi concentro sul caricamento del
                        // dettaglio vero e proprio
                        if (getNavigationEvent().equals(ListAction.NE_DETTAGLIO_VIEW)
                                || getNavigationEvent().equals(ListAction.NE_NEXT)
                                || getNavigationEvent().equals(ListAction.NE_PREV)) {
                            // Inserisco nello stack di navigazione i dati del nuovo strumento urbanistico che sto per
                            // visualizzare:
                            // 1) id unita doc
                            // 2) nome lista di ricerca dinamica
                            // 3) Tabella della ricerca dinamica
                            // 5) Indice di riga della lista corrente
                            // 6) Page size della lista corrente
                            // 7) Livello di annidamento
                            NavigatorBeanManager.getNavigatorBeanManager(getSession()).pushNavigatorBeanStack(
                                    idUnitaDoc, listRow.getBigDecimal(ID_STRUT_NAME), formList.getName(),
                                    formList.getTable(), formList.getTable().getCurrentRowIndex(),
                                    formList.getTable().getPageSize(),
                                    getForm().getLevel().getLevel_progetto_pab().parse().intValue());
                        }
                    } catch (SQLException e) {
                        throw new EMFError("Errore SQL: ", e);
                    }
                }
            }
        } catch (EMFError ex) {
            getMessageBox().addError(ex.getDescription());
        }
    }

    @Override
    public void undoDettaglio() throws EMFError {
        // Non usato
    }

    @Override
    public void saveDettaglio() throws EMFError {
        // Non usato
    }

    @Override
    public void dettaglioOnClick() throws EMFError {
        if (getNavigationEvent().equals(ListAction.NE_DETTAGLIO_VIEW) || getNavigationEvent().equals(ListAction.NE_NEXT)
                || getNavigationEvent().equals(ListAction.NE_PREV)) {
            if (getTableName().equals(DynamicSpagoLiteForm.getRicercaList(getForm()).getName())) {
                getForm().getPabButtonList().getScaricaProgetto().setDisableHourGlass(true);
                getForm().getPabButtonList().getScaricaProgetto().setEditMode();
                getForm().getDocumentiUDList().setViewMode();
                getForm().getDocumentiUDList().setStatus(Status.view);
                getForm().getDocumentiUDList().setUserOperations(true, false, false, false);
                if (getNavigationEvent().equals(ListAction.NE_DETTAGLIO_VIEW)) {
                    SessionManager.addPrevExecutionToHistory(getSession(), false, true);
                }
                forwardToPublisher(Application.Publisher.DETTAGLIO_PAB);
            }
        }
    }

    @Override
    public void elencoOnClick() throws EMFError {
        goBack();
    }

    @Override
    protected String getDefaultPublsherName() {
        return Application.Publisher.DETTAGLIO_PAB;
    }

    @Override
    public void process() throws EMFError {
        // Non usato
    }

    @SuppressWarnings("unchecked")
    @Override
    public void reloadAfterGoBack(String publisherName) {
        try {
            if (publisherName.equals(Application.Publisher.DETTAGLIO_PAB)) {
                BigDecimal idUnitaDoc;
                BigDecimal idStrut;
                if (getLastPublisher().equals(publisherName)) {
                    // Recupero l'ultimo elemento dalla pila e lo rimuovo da essa
                    NavigatorBeanManager.NavigatorBean detailBean = NavigatorBeanManager
                            .getNavigatorBeanManager(getSession()).popNavigatorBeanStack();
                    // Dall'oggetto NavigatorDetailBean mi ricavo l'id unita doc e l'id strut
                    idUnitaDoc = detailBean != null ? detailBean.getIdObject1() : null;
                    idStrut = detailBean != null ? detailBean.getIdObject2() : null;
                    // Recupero i valori dell'oggetto
                    if (detailBean != null) {
                        ((it.eng.spagoLite.form.list.List<SingleValueField<?>>) getForm()
                                .getComponent(detailBean.getSourceList())).setTable(detailBean.getSourceTable());
                        DynamicSpagoLiteForm.getRicercaList(getForm()).setTable(detailBean.getSourceTable());
                        DynamicSpagoLiteForm.getRicercaList(getForm()).getTable()
                                .setCurrentRowIndex(detailBean.getCurrentRowIndex());
                        DynamicSpagoLiteForm.getRicercaList(getForm()).getTable().setPageSize(detailBean.getPageSize());
                    }
                } else {
                    idUnitaDoc = DynamicSpagoLiteForm.getRicercaList(getForm()).getTable().getCurrentRow()
                            .getBigDecimal(ID_UNITA_DOC_NAME); // Sto gestendo la lista di ricerca al primo
                    idStrut = DynamicSpagoLiteForm.getRicercaList(getForm()).getTable().getCurrentRow()
                            .getBigDecimal(ID_STRUT_NAME);
                }
                // Carico il dettaglio
                loadDettaglioConListe(idUnitaDoc, idStrut, DynamicSpagoLiteForm.getRicercaList(getForm()).getTable()
                        .getCurrentRow().getString(CODICE_INTERVENTO_NAME));
            }
        } catch (EMFError e) {
            logger.error("Errore nel ricaricamento della pagina " + publisherName, e);
            getMessageBox().addError("Errore nel ricaricamento della pagina " + publisherName);
            forwardToPublisher(getLastPublisher());
        } catch (SQLException ex) {
            Logger.getLogger(PABAction.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public String getControllerName() {
        return Application.Actions.PAB;
    }

    @SuppressWarnings("unchecked")
    public void loadDettaglioConListe(String... rigaElemento) throws EMFError, ParseException, SQLException {
        // Carico il dettaglio del progetto ricostruzione
        BaseRow row = null;
        if (rigaElemento != null) {
            row = (BaseRow) DynamicSpagoLiteForm.getRicercaList(getForm()).getTable()
                    .getRow(Integer.parseInt(rigaElemento[0]));

        } else {
            row = (BaseRow) DynamicSpagoLiteForm.getRicercaList(getForm()).getTable().getCurrentRow();
        }

        DynamicSpagoLiteForm form = ricercheLoader
                .getSpagoLiteForm((String) getSession().getAttribute(Constants.SESSIONE_NOME_RIC));
        BigDecimal idUnitaDoc = (BigDecimal) row.getObject(ID_UNITA_DOC_NAME);
        BigDecimal idStrut = (BigDecimal) row.getObject(ID_STRUT_NAME);
        BaseRow detailRow = ricercaBo.caricaDettaglioUd(idUnitaDoc, getRicercaSelezionata(), idStrut.longValue());
        form.getFormDettaglio().copyFromBean(detailRow);

        /*
         * Attenzione: ora devo dire al framework che ora mi trovo in PugForm nel quale setto anche tutti i componenti
         * di DynamicSpagoLiteForm, per mantenere il comportamento dinamico del dettaglio
         */
        PABForm pug = new PABForm();
        for (Component component : form.getComponentList("")) {
            pug.addComponent(component);
        }
        ((it.eng.spagoLite.form.list.List<SingleValueField<?>>) pug.getComponent(form.getRicercaList().getName()))
                .setTable(DynamicSpagoLiteForm.getRicercaList(getForm()).getTable());
        ((it.eng.spagoLite.form.list.List<SingleValueField<?>>) pug.getComponent(form.getRicercaList().getName()))
                .getTable().setCurrentRowIndex(Integer.parseInt(rigaElemento[0]));
        setForm(pug);

        // Al primo giro azzero lo stack
        NavigatorBeanManager.getNavigatorBeanManager(getSession()).resetNavigatorBeanStack();
        // Salvo in sessione il "tipo" di lista dalla quale "proviene" lo strumento urbanistico, sarà RicercaList
        String listName = DynamicSpagoLiteForm.getRicercaList(getForm()).getName();
        // Prendendo i dati dalla form, popolo un oggetto List generico del tipo gestito dal framework naturalmente
        it.eng.spagoLite.form.list.List formList = ((it.eng.spagoLite.form.list.List) getForm().getComponent(listName));
        // Ricavo la riga corrente, recuperata dall'oggetto Table della lista
        BaseRowInterface listRow = formList.getTable().getCurrentRow();

        // CARICARE LA LISTA DEI TIPI DOCUMENTO
        AroVLisDocTableBean listDocUd = ricercaBo.getAroVLisDocTableBean((BigDecimal) row.getObject(ID_UNITA_DOC_NAME));
        getForm().getDocumentiUDList().setTable(listDocUd);
        getForm().getDocumentiUDList().getTable().setPageSize(10);
        getForm().getDocumentiUDList().getTable().first();

        getForm().getPabButtonList().getScaricaProgetto().setDisableHourGlass(true);
        getForm().getPabButtonList().getScaricaProgetto().setEditMode();
        getForm().getPabButtonList().getScaricaProgetto().setDisableHourGlass(true);
        getForm().getDocumentiUDList().setViewMode();
        getForm().getDocumentiUDList().setStatus(Status.view);

        // Se nullo il relativo parametro, siamo per forza al primo livello di annidamento
        getForm().getLevel().getLevel_progetto_pab().setValue(BigDecimal.ONE.toPlainString());
        // ... quindi inserisco l'elemento allo stesso livello
        NavigatorBeanManager.getNavigatorBeanManager(getSession()).pushNavigatorBeanStack(
                listRow.getBigDecimal(ID_UNITA_DOC_NAME), listRow.getBigDecimal("id_strut"), formList.getName(),
                formList.getTable(), formList.getTable().getCurrentRowIndex(), formList.getTable().getPageSize(), 1);

        forwardToPublisher(Application.Publisher.DETTAGLIO_PAB);
    }

    public BaseRow loadDettaglioConListe(BigDecimal idUnitaDoc, BigDecimal idStrut, String codiceIntervento)
            throws SQLException, EMFError {
        BaseRow row = ricercaBo.caricaDettaglioUd(idUnitaDoc, getRicercaSelezionata(), idStrut.longValue());
        DynamicSpagoLiteForm.getFormDettaglio(getForm()).copyFromBean(row);
        //
        getForm().getPabButtonList().getScaricaProgetto().setDisableHourGlass(true);
        getForm().getPabButtonList().getScaricaProgetto().setEditMode();
        //
        AroVLisDocTableBean listDocUd = ricercaBo.getAroVLisDocTableBean(idUnitaDoc);
        int docUDPageSize = 10;
        if (getForm().getDocumentiUDList().getTable() != null) {
            docUDPageSize = getForm().getDocumentiUDList().getTable().getPageSize();
        }
        getForm().getDocumentiUDList().setTable(listDocUd);
        getForm().getDocumentiUDList().getTable().setPageSize(docUDPageSize);
        getForm().getDocumentiUDList().getTable().first();
        //
        return row;
    }

    public void downloadRecupero(String... rigaElemento) throws EMFError {
        forwardToPublisher(Application.Publisher.DETTAGLIO_PAB);
        AroVLisDocRowBean row;
        String urlParam = applicationProperties.getProperty(ConstDipParamApplic.NmParamApplic.URL_RECUP_UD.name());
        Integer timeoutParam = Integer
                .parseInt(applicationProperties.getProperty(ConstDipParamApplic.NmParamApplic.TIMEOUT_RECUP_UD.name()));
        String versioneParam = applicationProperties
                .getProperty(ConstDipParamApplic.NmParamApplic.VERSIONE_XML_RECUP_UD.name());
        String userIdParam = applicationProperties
                .getProperty(ConstDipParamApplic.NmParamApplic.USERID_RECUP_UD.name());
        String pwdParam = applicationProperties.getProperty(ConstDipParamApplic.NmParamApplic.PSW_RECUP_UD.name());
        if (rigaElemento != null) {
            row = (AroVLisDocRowBean) getForm().getDocumentiUDList().getTable()
                    .getRow(Integer.parseInt(rigaElemento[0]));
        } else {
            row = (AroVLisDocRowBean) getForm().getDocumentiUDList().getTable().getCurrentRow();
        }
        BigDecimal idStrut = DynamicSpagoLiteForm.getRicercaList(getForm()).getTable().getCurrentRow()
                .getBigDecimal(ID_STRUT_NAME);
        RecuperoWSBean strut = pievesestinaBO.getUdData(idStrut, row.getBigDecimal(ID_UNITA_DOC_NAME));
        strut.setTipoDocumento(row.getString("nm_tipo_doc"));
        String filename = "UD_" + strut.getRegistro() + "_" + strut.getAnno().toPlainString() + "_" + strut.getNumero()
                + "_" + strut.getTipoDocumento() + ".zip";
        callRecupero(TipoRichiesta.REC_DIP_UNI_DOC, urlParam, timeoutParam, strut, versioneParam, userIdParam, pwdParam,
                filename);
    }

    public void downloadComponente(String... rigaElemento) throws EMFError {
        forwardToPublisher(Application.Publisher.DETTAGLIO_TIPO_DOC);
        AroVLisCompDocRowBean row;

        String urlParam = applicationProperties.getProperty(ConstDipParamApplic.NmParamApplic.URL_RECUP_UD.name());
        Integer timeoutParam = Integer
                .parseInt(applicationProperties.getProperty(ConstDipParamApplic.NmParamApplic.TIMEOUT_RECUP_UD.name()));
        String versioneParam = applicationProperties
                .getProperty(ConstDipParamApplic.NmParamApplic.VERSIONE_XML_RECUP_UD.name());
        String userIdParam = applicationProperties
                .getProperty(ConstDipParamApplic.NmParamApplic.USERID_RECUP_UD.name());
        String pwdParam = applicationProperties.getProperty(ConstDipParamApplic.NmParamApplic.PSW_RECUP_UD.name());

        if (rigaElemento != null) {
            int startIndex = (getForm().getCompDocList().getTable().getCurrentPageIndex() - 1)
                    * getForm().getCompDocList().getTable().getPageSize();
            int endIndex = ((getForm().getCompDocList().getTable().getCurrentPageIndex() - 1)
                    * getForm().getCompDocList().getTable().getPageSize())
                    + getForm().getCompDocList().getTable().getPageSize();
            row = (AroVLisCompDocRowBean) getForm().getCompDocList().getTable()
                    .getRow((Integer.parseInt(rigaElemento[0]) + startIndex) % endIndex);
        } else {
            row = (AroVLisCompDocRowBean) getForm().getCompDocList().getTable().getCurrentRow();
        }
        RecuperoWSBean strut = pievesestinaBO.getUdData(row.getBigDecimal(ID_STRUT_NAME),
                row.getBigDecimal(ID_UNITA_DOC_NAME));
        strut.setIdDocumento(row.getString("cd_key_doc_vers"));
        strut.setOrdinePresentazioneComponente(row.getBigDecimal("ni_ord_comp_doc"));
        String filename = row.getDsUrnCompCalc().replace("urn:", "").replaceAll(":", "_") + ".zip";
        callRecupero(TipoRichiesta.REC_COMP, urlParam, timeoutParam, strut, versioneParam, userIdParam, pwdParam,
                filename);
    }

    private void callRecupero(TipoRichiesta tipoRichiesta, String urlParam, Integer timeoutParam, RecuperoWSBean strut,
            String versioneParam, String userIdParam, String pwdParam, String filename) throws EMFError {
        String xmlRequest = "";
        try {
            StringWriter writer = new StringWriter();
            Marshaller marshaller = jaxbSingleton.getContextRecupero().createMarshaller();

            Recupero recXml = new Recupero();
            recXml.setChiave(new ChiaveType());
            recXml.setVersatore(new VersatoreType());
            recXml.setVersione(versioneParam);

            recXml.getVersatore().setAmbiente(strut.getAmbiente());
            recXml.getVersatore().setEnte(strut.getEnte());
            recXml.getVersatore().setStruttura(strut.getStruttura());
            recXml.getVersatore().setUserID(userIdParam);
            recXml.getVersatore().setUtente(getUser().getUsername());

            recXml.getChiave().setAnno(strut.getAnno().toBigInteger());
            recXml.getChiave().setNumero(strut.getNumero());
            recXml.getChiave().setTipoRegistro(strut.getRegistro());
            recXml.getChiave().setIDDocumento(strut.getIdDocumento());
            recXml.getChiave().setOrdinePresentazioneComponente(strut.getOrdinePresentazioneComponente() != null
                    ? strut.getOrdinePresentazioneComponente().intValue() : null);
            recXml.getChiave().setTipoDocumento(strut.getTipoDocumento());
            // MEV#22921 Parametrizzazione servizi di recupero
            recXml.getChiave().setTipoNomeFile(TokenFileNameType.NOME_FILE_URN_VERSATO);

            marshaller.marshal(recXml, writer);
            xmlRequest = writer.toString();
        } catch (JAXBException ex) {
            logger.error("Eccezione nella creazione dell'xml di richiesta per il download", ex);
            getMessageBox().addError("Eccezione nella creazione dell'xml di richiesta per il download");
        }

        if (!getMessageBox().hasError()) {
            RichiestaWSInput input = new RichiestaWSInput(tipoRichiesta, urlParam, timeoutParam, true,
                    new BasicNameValuePair(RichiestaWSInput.CampiRichiesta.VERSIONE.name(), versioneParam),
                    new BasicNameValuePair(RichiestaWSInput.CampiRichiesta.LOGINNAME.name(), userIdParam),
                    new BasicNameValuePair(RichiestaWSInput.CampiRichiesta.PASSWORD.name(), pwdParam),
                    new BasicNameValuePair(RichiestaWSInput.CampiRichiesta.XMLSIP.name(), xmlRequest));

            EsitoConnessione esito = richiestaWSClient.callWs(input);
            if (!esito.isErroreConnessione() && esito.getCodiceEsito().equals(EsitoConnessione.Esito.OK.name())) {
                /*
                 * Definiamo l'output previsto che sarà un file in formato zip di cui si occuperà la servlet per fare il
                 * download
                 */

                getResponse().setContentType("application/zip");
                getResponse().setHeader("Content-Disposition", "attachment; filename=\"" + filename);

                try (OutputStream outUD = getServletOutputStream();
                        InputStream inputStream = (InputStream) esito.getResponse();) {
                    getResponse().setHeader("Content-Length", esito.getXmlResponse());
                    byte[] bytes = new byte[8000];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(bytes)) != -1) {
                        outUD.write(bytes, 0, bytesRead);
                    }
                    outUD.flush();
                } catch (IOException e) {
                    getMessageBox().addError("Eccezione nel recupero del documento");
                }
            } else {
                getMessageBox().addError("Errore dal servizio di recupero: "
                        + (esito.isErroreConnessione() ? esito.getDescrErrConnessione() : esito.getMessaggioErrore()));
            }
        }
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
        case Application.Publisher.DETTAGLIO_PAB:
            return getRicercaSelezionata();
        default:
            return null;
        }
    }

    private String getRicercaSelezionata() {
        return (String) getSession().getAttribute(Constants.SESSIONE_NOME_RIC);
    }

    @Override
    public void scaricaProgetto() throws Throwable {
        BaseRow row = (BaseRow) DynamicSpagoLiteForm.getRicercaList(getForm()).getTable().getCurrentRow();
        BigDecimal idUnitaDoc = row.getBigDecimal(ID_UNITA_DOC_NAME);
        BigDecimal idstrut = row.getBigDecimal(ID_STRUT_NAME);
        scaricaProgetto(idstrut, idUnitaDoc);
    }

    private void scaricaProgetto(BigDecimal idStrut, BigDecimal idUnitaDoc) throws Throwable {
        forwardToPublisher(Application.Publisher.DETTAGLIO_PAB);
        String urlParam = applicationProperties.getProperty(ConstDipParamApplic.NmParamApplic.URL_RECUP_UD.name());
        Integer timeoutParam = Integer
                .parseInt(applicationProperties.getProperty(ConstDipParamApplic.NmParamApplic.TIMEOUT_RECUP_UD.name()));
        String versioneParam = applicationProperties
                .getProperty(ConstDipParamApplic.NmParamApplic.VERSIONE_XML_RECUP_UD.name());
        String userIdParam = applicationProperties
                .getProperty(ConstDipParamApplic.NmParamApplic.USERID_RECUP_UD.name());
        String pwdParam = applicationProperties.getProperty(ConstDipParamApplic.NmParamApplic.PSW_RECUP_UD.name());
        RecuperoWSBean strut = pievesestinaBO.getUdData(idStrut, idUnitaDoc);
        String filename = "UD_" + strut.getRegistro() + "_" + strut.getAnno().toPlainString() + "_" + strut.getNumero()
                + ".zip";
        callRecupero(TipoRichiesta.REC_DIP_UNI_DOC, urlParam, timeoutParam, strut, versioneParam, userIdParam, pwdParam,
                filename);
    }
}
