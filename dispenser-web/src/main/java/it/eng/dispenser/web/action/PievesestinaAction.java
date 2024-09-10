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

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.http.message.BasicNameValuePair;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import it.eng.dispenser.bean.RecuperoWSBean;
import it.eng.dispenser.component.JAXBSingleton;
import it.eng.dispenser.entity.constraint.ConstDipParamApplic;
import it.eng.dispenser.slite.gen.Application;
import it.eng.dispenser.slite.gen.action.PievesestinaAbstractAction;
import it.eng.dispenser.slite.gen.form.PievesestinaForm;
import it.eng.dispenser.slite.gen.viewbean.AroVLisLinkUnitaDocTableBean;
import it.eng.dispenser.util.Constants;
import it.eng.dispenser.web.bo.PievesestinaBO;
import it.eng.dispenser.web.bo.RicercaBO;
import it.eng.dispenser.web.form.DynamicSpagoLiteForm;
import it.eng.dispenser.web.form.RicercheLoader;
import it.eng.dispenser.ws.dto.EsitoConnessione;
import it.eng.dispenser.ws.dto.RichiestaWSInput;
import it.eng.dispenser.ws.dto.RichiestaWSInput.TipoRichiesta;
import it.eng.dispenser.ws.util.RichiestaWSClient;
import it.eng.parer.dispenser.util.DataSourcePropertiesFactoryBean;
import it.eng.parer.ws.xml.versReqStato.ChiaveType;
import it.eng.parer.ws.xml.versReqStato.Recupero;
import it.eng.parer.ws.xml.versReqStato.VersatoreType;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.ExecutionHistory;
import it.eng.spagoLite.SessionManager;
import it.eng.spagoLite.db.base.BaseTableInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.table.BaseTable;
import it.eng.spagoLite.form.base.BaseForm;
import it.eng.spagoLite.form.fields.Field;
import it.eng.spagoLite.form.fields.Fields;
import it.eng.spagoLite.form.fields.SingleValueField;

public class PievesestinaAction extends PievesestinaAbstractAction {

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

    private org.slf4j.Logger log = LoggerFactory.getLogger(PievesestinaAction.class);

    @Override
    public void initOnClick() throws EMFError {
        // not implemented

    }

    @Override
    public void insertDettaglio() throws EMFError {
        // not implemented

    }

    @Override
    public void update(Fields<Field> fields) throws EMFError {
        // not implemented

    }

    @Override
    public void delete(Fields<Field> fields) throws EMFError {
        // not implemented

    }

    @Override
    public void loadDettaglio() throws EMFError {
        // not implemented

    }

    @Override
    public void undoDettaglio() throws EMFError {
        // not implemented

    }

    @Override
    public void saveDettaglio() throws EMFError {
        // not implemented

    }

    @Override
    public void dettaglioOnClick() throws EMFError {
        // not implemented

    }

    @Override
    public void elencoOnClick() throws EMFError {
        goBack();
    }

    @Override
    protected String getDefaultPublsherName() {
        return Application.Publisher.REFERTI_COLLEGATI;
    }

    @Override
    public void process() throws EMFError {
        // not implemented

    }

    @Override
    public void reloadAfterGoBack(String publisherName) {
        // not implemented

    }

    @Override
    public String getControllerName() {
        return Application.Actions.PIEVESESTINA;
    }

    public void refCollegati(String... rigaElemento) throws EMFError, ParseException {
        /* Recupero i dati che mi servono */
        String cdUd = null;
        String dataRefertoString = null;
        BaseRow row = new BaseRow();
        if (rigaElemento != null) {
            row = (BaseRow) DynamicSpagoLiteForm.getRicercaList(getForm()).getTable()
                    .getRow(Integer.parseInt(rigaElemento[0]));

        } else {
            row = (BaseRow) DynamicSpagoLiteForm.getRicercaList(getForm()).getTable().getCurrentRow();
        }

        /*
         * Attenzione: ora devo dire al framework che ora mi trovo in PievesestinaForm
         */
        PievesestinaForm pieve = new PievesestinaForm();
        setForm(pieve);
        cdUd = (String) row.getObject("numero");
        dataRefertoString = (String) row.getObject("data_reg");
        // CARICARE LA LISTA DEI REFERTI COLLEGATI
        AroVLisLinkUnitaDocTableBean linkUd = ricercaBo
                .getAroVLisLinkUnitaDocTableBean((BigDecimal) row.getObject("id_unita_doc"), cdUd, dataRefertoString);
        getForm().getRefertiCollegatiList().setTable(linkUd);
        getForm().getRefertiCollegatiList().getTable().first();
        getForm().getRefertiCollegatiList().getTable().setPageSize(10);

        forwardToPublisher(Application.Publisher.REFERTI_COLLEGATI);
    }

    public void refCollegati() throws EMFError, ParseException {
        refCollegati((String[]) null);
    }

    public void loadRefertoDisponibile() throws SQLException, EMFError {
        BigDecimal idUnitaDoc = ((AroVLisLinkUnitaDocTableBean) getForm().getRefertiCollegatiList().getTable())
                .getCurrentRow().getIdUnitaDocColleg();
        BigDecimal idStrut = ((AroVLisLinkUnitaDocTableBean) getForm().getRefertiCollegatiList().getTable())
                .getCurrentRow().getIdStrut();
        redirectToDettaglioPage(idUnitaDoc, idStrut);
    }

    public void redirectToDettaglioPage(BigDecimal idUnitaDoc, BigDecimal idStrut) throws EMFError, SQLException {
        DynamicSpagoLiteForm form = ricercheLoader
                .getSpagoLiteForm((String) getSession().getAttribute(Constants.SESSIONE_NOME_RIC));
        BaseRow row = ricercaBo.caricaDettaglioUd(idUnitaDoc,
                (String) getSession().getAttribute(Constants.SESSIONE_NOME_RIC), idStrut.longValue());
        BaseTable table = new BaseTable();
        table.add(row);
        table.setPageSize(1);
        setNavigationEvent(NE_DETTAGLIO_VIEW);
        redirectToPage(Application.Actions.RICERCA, form, form.getRicercaList().getName(), table, getNavigationEvent());
    }

    @SuppressWarnings("unchecked")
    private void redirectToPage(final String action, BaseForm form, String listToPopulate, BaseTableInterface<?> table,
            String event) throws EMFError {
        ((it.eng.spagoLite.form.list.List<SingleValueField<?>>) form.getComponent(listToPopulate)).setTable(table);
        redirectToAction(action, "?operation=listNavigationOnClick&navigationEvent=" + event + "&table="
                + listToPopulate + "&riga=" + table.getCurrentRowIndex(), form);
    }

    public void downloadRecupero(String... rigaElemento) throws EMFError {
        forwardToPublisher(Application.Publisher.RICERCA);
        BaseRow row;
        BaseForm tmp;
        BaseForm form = (BaseForm) getSession().getAttribute(Constants.SESSIONE_FORM_RIC);

        String urlParam = applicationProperties.getProperty(ConstDipParamApplic.NmParamApplic.URL_RECUP_UD.name());
        Integer timeoutParam = Integer
                .parseInt(applicationProperties.getProperty(ConstDipParamApplic.NmParamApplic.TIMEOUT_RECUP_UD.name()));
        String versioneParam = applicationProperties
                .getProperty(ConstDipParamApplic.NmParamApplic.VERSIONE_XML_RECUP_UD.name());
        String userIdParam = applicationProperties
                .getProperty(ConstDipParamApplic.NmParamApplic.USERID_RECUP_UD.name());
        String pwdParam = applicationProperties.getProperty(ConstDipParamApplic.NmParamApplic.PSW_RECUP_UD.name());

        tmp = getForm();
        if (tmp instanceof DynamicSpagoLiteForm) {
            getSession().setAttribute(Constants.SESSIONE_FORM_RIC, tmp);
            form = tmp;
        }
        if (rigaElemento != null) {
            row = (BaseRow) DynamicSpagoLiteForm.getRicercaList(form).getTable()
                    .getRow(Integer.parseInt(rigaElemento[0]));
        } else {
            row = (BaseRow) DynamicSpagoLiteForm.getRicercaList(form).getTable().getCurrentRow();
        }
        RecuperoWSBean strut = pievesestinaBO.getUdData(row.getBigDecimal("id_strut"),
                row.getBigDecimal("id_unita_doc"));
        String filename = "UD_" + strut.getRegistro() + "_" + strut.getAnno().toPlainString() + "_" + strut.getNumero()
                + ".zip";
        callRecupero(TipoRichiesta.REC_DIP_UNI_DOC, urlParam, timeoutParam, strut, versioneParam, userIdParam, pwdParam,
                filename);
    }

    public void downloadComponente(String... rigaElemento) throws EMFError {
        forwardToPublisher(Application.Publisher.RICERCA);
        BaseRow row;
        BaseForm tmp;
        BaseForm form = (BaseForm) getSession().getAttribute(Constants.SESSIONE_FORM_RIC);

        String urlParam = applicationProperties.getProperty(ConstDipParamApplic.NmParamApplic.URL_RECUP_UD.name());
        Integer timeoutParam = Integer
                .parseInt(applicationProperties.getProperty(ConstDipParamApplic.NmParamApplic.TIMEOUT_RECUP_UD.name()));
        String versioneParam = applicationProperties
                .getProperty(ConstDipParamApplic.NmParamApplic.VERSIONE_XML_RECUP_UD.name());
        String userIdParam = applicationProperties
                .getProperty(ConstDipParamApplic.NmParamApplic.USERID_RECUP_UD.name());
        String pwdParam = applicationProperties.getProperty(ConstDipParamApplic.NmParamApplic.PSW_RECUP_UD.name());

        tmp = getForm();
        if (tmp instanceof DynamicSpagoLiteForm) {
            getSession().setAttribute(Constants.SESSIONE_FORM_RIC, tmp);
            form = tmp;
        }
        if (rigaElemento != null) {
            row = (BaseRow) DynamicSpagoLiteForm.getRicercaList(form).getTable()
                    .getRow(Integer.parseInt(rigaElemento[0]));
        } else {
            row = (BaseRow) DynamicSpagoLiteForm.getRicercaList(form).getTable().getCurrentRow();
        }
        RecuperoWSBean strut = pievesestinaBO.getUdData(row.getBigDecimal("id_strut"),
                row.getBigDecimal("id_unita_doc"));
        strut.setIdDocumento(row.getString("cd_key_doc_vers_princ"));
        strut.setOrdinePresentazioneComponente(row.getBigDecimal("ni_ord_comp_pdf"));
        String filename = "COMP_" + strut.getRegistro() + "_" + strut.getAnno().toPlainString() + "_"
                + strut.getNumero() + ".zip";
        callRecupero(TipoRichiesta.REC_COMP, urlParam, timeoutParam, strut, versioneParam, userIdParam, pwdParam,
                filename);
    }

    public void downloadPdf(String... rigaElemento) throws EMFError {
        forwardToPublisher(Application.Publisher.RICERCA);
        BaseRow row;
        BaseForm tmp;
        BaseForm form = (BaseForm) getSession().getAttribute(Constants.SESSIONE_FORM_RIC);

        String urlParam = applicationProperties
                .getProperty(ConstDipParamApplic.NmParamApplic.URL_RECUP_COMP_TRASF.name());
        Integer timeoutParam = Integer
                .parseInt(applicationProperties.getProperty(ConstDipParamApplic.NmParamApplic.TIMEOUT_RECUP_UD.name()));
        String versioneParam = applicationProperties
                .getProperty(ConstDipParamApplic.NmParamApplic.VERSIONE_XML_RECUP_UD.name());
        String userIdParam = applicationProperties
                .getProperty(ConstDipParamApplic.NmParamApplic.USERID_RECUP_UD.name());
        String pwdParam = applicationProperties.getProperty(ConstDipParamApplic.NmParamApplic.PSW_RECUP_UD.name());

        tmp = getForm();
        if (tmp instanceof DynamicSpagoLiteForm) {
            getSession().setAttribute(Constants.SESSIONE_FORM_RIC, tmp);
            form = tmp;
        }
        if (rigaElemento != null) {
            row = (BaseRow) DynamicSpagoLiteForm.getRicercaList(form).getTable()
                    .getRow(Integer.parseInt(rigaElemento[0]));
        } else {
            row = (BaseRow) DynamicSpagoLiteForm.getRicercaList(form).getTable().getCurrentRow();
        }
        RecuperoWSBean strut = pievesestinaBO.getUdData(row.getBigDecimal("id_strut"),
                row.getBigDecimal("id_unita_doc"));
        strut.setIdDocumento(row.getString("cd_key_doc_vers_princ"));
        strut.setOrdinePresentazioneComponente(row.getBigDecimal("ni_ord_comp_xml"));
        String filename = "COMP_TRASF_" + strut.getRegistro() + "_" + strut.getAnno().toPlainString() + "_"
                + strut.getNumero() + ".zip";
        callRecupero(TipoRichiesta.REC_PDF, urlParam, timeoutParam, strut, versioneParam, userIdParam, pwdParam,
                filename);
    }

    public void downloadAip(String... rigaElemento) throws EMFError {
        forwardToPublisher(Application.Publisher.RICERCA);
        BaseRow row;
        BaseForm tmp;
        BaseForm form = (BaseForm) getSession().getAttribute(Constants.SESSIONE_FORM_RIC);

        String urlParam = applicationProperties.getProperty(ConstDipParamApplic.NmParamApplic.URL_RECUP_AIP.name());
        Integer timeoutParam = Integer
                .parseInt(applicationProperties.getProperty(ConstDipParamApplic.NmParamApplic.TIMEOUT_RECUP_UD.name()));
        String versioneParam = applicationProperties
                .getProperty(ConstDipParamApplic.NmParamApplic.VERSIONE_XML_RECUP_UD.name());
        String userIdParam = applicationProperties
                .getProperty(ConstDipParamApplic.NmParamApplic.USERID_RECUP_UD.name());
        String pwdParam = applicationProperties.getProperty(ConstDipParamApplic.NmParamApplic.PSW_RECUP_UD.name());

        tmp = getForm();
        if (tmp instanceof DynamicSpagoLiteForm) {
            getSession().setAttribute(Constants.SESSIONE_FORM_RIC, tmp);
            form = tmp;
        }
        if (rigaElemento != null) {
            row = (BaseRow) DynamicSpagoLiteForm.getRicercaList(form).getTable()
                    .getRow(Integer.parseInt(rigaElemento[0]));
        } else {
            row = (BaseRow) DynamicSpagoLiteForm.getRicercaList(form).getTable().getCurrentRow();
        }
        RecuperoWSBean strut = pievesestinaBO.getUdData(row.getBigDecimal("id_strut"),
                row.getBigDecimal("id_unita_doc"));
        String filename = "AIP_" + strut.getRegistro() + "_" + strut.getAnno().toPlainString() + "_" + strut.getNumero()
                + ".zip";
        callRecupero(TipoRichiesta.REC_AIP, urlParam, timeoutParam, strut, versioneParam, userIdParam, pwdParam,
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

            marshaller.marshal(recXml, writer);
            xmlRequest = writer.toString();
        } catch (JAXBException ex) {
            log.error("Eccezione nella creazione dell'xml di richiesta per il download", ex);
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
        ExecutionHistory history = SessionManager.removeLastExecutionHistory(getSession());
        SessionManager.setForm(getSession(), history.getForm());
        SessionManager.setCurrentAction(getSession(), history.getName());
        setLastPublisher(Application.Publisher.RICERCA);
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

    private String getRicercaSelezionata() {
        return (String) getSession().getAttribute(Constants.SESSIONE_NOME_RIC);
    }

}
