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

import it.eng.dispenser.slite.gen.Application;
import it.eng.dispenser.slite.gen.action.GestioneJobAbstractAction;
import it.eng.dispenser.slite.gen.form.GestioneJobForm;
import it.eng.dispenser.slite.gen.form.GestioneJobForm.GestioneJobRicercaFiltri;
import it.eng.dispenser.slite.gen.form.GestioneJobForm.FiltriJobSchedulati;
import it.eng.dispenser.slite.gen.viewbean.LogVVisLastSchedRowBean;
import it.eng.dispenser.util.Constants;
import it.eng.dispenser.util.ejb.GestioneJobEjb;
import it.eng.dispenser.web.bo.JobBO;
import it.eng.dispenser.web.util.ComboGetter;
import it.eng.dispenser.web.validator.GestioneJobValidator;
import it.eng.parer.jboss.timer.service.JbossTimerEjb;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.db.base.BaseTableInterface;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.table.BaseTable;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;
import it.eng.spagoLite.form.base.BaseForm;
import it.eng.spagoLite.form.fields.Field;
import it.eng.spagoLite.form.fields.Fields;
import it.eng.spagoLite.form.fields.SingleValueField;
import it.eng.spagoLite.form.fields.impl.Button;
import it.eng.spagoLite.form.fields.impl.CheckBox;
import it.eng.spagoLite.form.fields.impl.Input;
import it.eng.spagoLite.message.Message;
import it.eng.spagoLite.message.Message.MessageLevel;
import it.eng.spagoLite.message.MessageBox.ViewMode;
import it.eng.spagoLite.security.Secure;
import java.math.BigDecimal;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import javax.ejb.EJB;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Action per la gestione dei job
 *
 * @author Moretti_Lu
 *
 */
public class GestioneJobAction extends GestioneJobAbstractAction {

    private static final String BACK_TO_RICERCA_JOB = "backToRicercaJob";
    private static final String DESCRIZIONE_JOB = "Sincronizzazione con Sacer";
    private static final String ATTRIBUTE_VISUALIZZA_RIPRISTINA_FOTO = "visualizzaRipristinaFoto";
    private static final String ATTRIBUTE_FOTO_SALVATA = "fotoSalvata";
    private static final String STATO_JOB = "stato_job";
    private static final String DS_JOB = "ds_job";
    private static final String COMBO_KEY_NM_JOB = "nm_job";
    private static final String COMBO_KEY_TI_STATO_JOB = "ti_stato_job";
    private static final String ATTRIBUTE_FROM_LINK = "fromLink";
    private static final String INFO_NESSUN_JOB_SELEZIONATO = "Nessun job selezionato";
    private static final String ATTRIBUTE_FROM_LISTA_PRINC = "fromListaPrinc";
    private static final String JOB_SELEZIONATI = "job_selezionati";
    private Logger log = LoggerFactory.getLogger(GestioneJobAction.class.getName());

    @EJB(mappedName = "java:app/JbossTimerWrapper-ejb/JbossTimerEjb")
    private JbossTimerEjb jbossTimerEjb;

    @Autowired
    private JobBO jobBo;

    private static final String UNSOPPORTED_OPERATION_EXCEPTION = "Not supported yet.";
    public static final String FROM_RICERCA_JOB = "fromRicercaJob";
    public static final String ECCEZIONE_MSG = "Eccezione";

    @Override
    public void ricercaJobSchedulati() throws EMFError {
        getForm().getFiltriJobSchedulati().getRicercaJobSchedulati().setDisableHourGlass(true);
        FiltriJobSchedulati filtri = getForm().getFiltriJobSchedulati();

        // Esegue la post dei filtri compilati
        if (getSession().getAttribute(FROM_RICERCA_JOB) != null) {
            getSession().removeAttribute(FROM_RICERCA_JOB);
            getSession().setAttribute(BACK_TO_RICERCA_JOB, true);
        } else {
            filtri.post(getRequest());
        }

        // Valida i filtri per verificare quelli obbligatori
        if (filtri.validate(getMessageBox())) {
            // Valida in maniera piÃƒÂ¹ specifica i dati
            Date datada = filtri.getDt_reg_log_job_da().parse();
            Date dataa = filtri.getDt_reg_log_job_a().parse();
            BigDecimal oreda = filtri.getOre_dt_reg_log_job_da().parse();
            BigDecimal orea = filtri.getOre_dt_reg_log_job_a().parse();
            BigDecimal minutida = filtri.getMinuti_dt_reg_log_job_da().parse();
            BigDecimal minutia = filtri.getMinuti_dt_reg_log_job_a().parse();
            String descrizioneDataDa = filtri.getDt_reg_log_job_da().getHtmlDescription();
            String descrizioneDataA = filtri.getDt_reg_log_job_a().getHtmlDescription();

            // Valida i campi di ricerca
            GestioneJobValidator validator = new GestioneJobValidator(getMessageBox());
            Date[] dateValidate = validator.validaDate(datada, oreda, minutida, dataa, orea, minutia, descrizioneDataDa,
                    descrizioneDataA);

            if (!getMessageBox().hasError()) {
                // Setta la lista dei job in base ai filtri di ricerca
                getForm().getJobSchedulatiList().setTable(jobBo.getMonVLisSchedJobViewBean(filtri, dateValidate));

                getForm().getJobSchedulatiList().getTable().setPageSize(10);
                // Workaround in modo che la lista punti al primo record, non all'ultimo
                getForm().getJobSchedulatiList().getTable().first();

                // Setto i campi di "StatoOggetto Job"
                String nomeJob = filtri.getNm_job().parse();
                Date proxAttivazione = jbossTimerEjb.getDataProssimaAttivazione(nomeJob);

                LogVVisLastSchedRowBean rb = jobBo.getLogVVisLastSched(nomeJob);
                String formattata = "";
                DateFormat formato = new SimpleDateFormat(Constants.DATE_FORMAT_JOB);
                if (proxAttivazione != null) {
                    formattata = formato.format(proxAttivazione);
                }
                getForm().getStatoJob().getDt_prossima_attivazione().setValue(formattata);
                if (rb.getFlJobAttivo() != null) {
                    if (rb.getFlJobAttivo().equals("1")) {
                        getForm().getStatoJob().getAttivo().setChecked(true);
                        formattata = formato.format(rb.getDtEventoIni());
                        getForm().getStatoJob().getDt_reg_log_job_ini().setValue(formattata);
                    } else {
                        getForm().getStatoJob().getAttivo().setChecked(false);
                        getForm().getStatoJob().getDt_reg_log_job_ini().setValue(null);
                    }
                } else {
                    getForm().getStatoJob().getAttivo().setChecked(false);
                    getForm().getStatoJob().getDt_reg_log_job_ini().setValue(null);
                }
            }
        }
        forwardToPublisher(Application.Publisher.SCHEDULAZIONI_JOB_LIST);
    }

    @Override
    public void pulisciJobSchedulati() throws EMFError {
        try {
            this.schedulazioniJob();
        } catch (Exception ex) {
            log.error(ECCEZIONE_MSG, ex);
        }
    }

    @Override
    public void startReplicaUtenti() throws EMFError {
        throw new UnsupportedOperationException(UNSOPPORTED_OPERATION_EXCEPTION); // Generated from
        // nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void stopReplicaUtenti() throws EMFError {
        throw new UnsupportedOperationException(UNSOPPORTED_OPERATION_EXCEPTION); // Generated from
        // nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Secure(action = "Menu.AmministrazioneSistema.SchedulazioniJob")
    public void schedulazioniJob() {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.AmministrazioneSistema.SchedulazioniJob");
        getForm().getFiltriJobSchedulati().reset();
        getSession().removeAttribute(FROM_RICERCA_JOB);
        getSession().removeAttribute(BACK_TO_RICERCA_JOB);
        getForm().getJobSchedulatiList().setTable(null);
        getForm().getStatoJob().reset();
        populateFiltriJob();
        getForm().getFiltriJobSchedulati().setEditMode();
        forwardToPublisher(Application.Publisher.SCHEDULAZIONI_JOB_LIST);
    }

    private void populateFiltriJob() {
        getForm().getFiltriJobSchedulati().getNm_job().setDecodeMap(
                ComboGetter.getMappaSortedGenericEnum(COMBO_KEY_NM_JOB, Constants.NomiJob.getComboSchedulazioniJob()));

        // Inserisco il valore di default nel campo data
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat(Constants.DATE_FORMAT);
        getForm().getFiltriJobSchedulati().getDt_reg_log_job_da().setValue(df.format(cal.getTime()));
    }

    public void apriVisualizzaSchedulazioniJob() {
        Integer riga = Integer.parseInt(getRequest().getParameter("riga"));
        String nmJob = ((BaseTable) getForm().getGestioneJobRicercaList().getTable()).getRow(riga)
                .getString(COMBO_KEY_NM_JOB);
        redirectToSchedulazioniJob(nmJob);
    }

    private void redirectToSchedulazioniJob(String nmJob) {
        GestioneJobForm form = prepareRedirectToSchedulazioniJob(nmJob);
        redirectToPage(Application.Actions.GESTIONE_JOB, form, form.getJobSchedulatiList().getName(),
                getForm().getGestioneJobRicercaList().getTable());
    }

    private GestioneJobForm prepareRedirectToSchedulazioniJob(String nmJob) {
        GestioneJobForm form = new GestioneJobForm();
        /* Preparo la pagina di destinazione */
        form.getFiltriJobSchedulati().setEditMode();
        DecodeMap dec = ComboGetter.getMappaSortedGenericEnum(COMBO_KEY_NM_JOB, Constants.NomiJob.values());
        form.getFiltriJobSchedulati().getNm_job().setDecodeMap(dec);
        /* Setto il valore del Job da cercare in Visualizzazione Job Schedulati */
        form.getFiltriJobSchedulati().getNm_job().setValue(nmJob);
        // Preparo la data di Schedulazione Da una settimana prima rispetto la data corrente
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, -7);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        DateFormat f = new SimpleDateFormat("dd/MM/yyyy");
        form.getFiltriJobSchedulati().getDt_reg_log_job_da().setValue(f.format(c.getTime()));
        getSession().setAttribute(FROM_RICERCA_JOB, true);
        return form;
    }

    @SuppressWarnings("unchecked")
    private void redirectToPage(final String action, BaseForm form, String listToPopulate,
            BaseTableInterface<?> table) {
        ((it.eng.spagoLite.form.list.List<SingleValueField<?>>) form.getComponent(listToPopulate)).setTable(table);
        redirectToAction(action, "?operation=ricercaJobSchedulatiDaGestioneJob", form);
    }

    public void ricercaJobSchedulatiDaGestioneJob() throws EMFError {
        ricercaJobSchedulati();
    }

    private enum OPERAZIONE {
        START("lancio il timer"), ESECUZIONE_SINGOLA("esecuzione singola"), STOP("stop");

        protected String desc;

        OPERAZIONE(String desc) {
            this.desc = desc;
        }

        public String description() {
            return desc;
        }
    }

    @EJB(mappedName = "java:app/Dispenser-ejb/GestioneJobEjb")
    private GestioneJobEjb gestioneJobEjb;

    // <editor-fold defaultstate="collapsed" desc="NUOVA GESTIONE JOB">
    @Secure(action = "Menu.AmministrazioneSistema.GestioneJobRicerca")
    public void gestioneJobRicercaPage() throws EMFError {
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.AmministrazioneSistema.GestioneJobRicerca");
        getForm().getGestioneJobRicercaList().setTable(null);
        resetFiltriGestioneJobRicercaPage();
        popolaInformazioniJob();
        tabRicercaJobTabOnClick();
        getForm().getGestioneJobInfo().getSalvaFotoGestioneJob().setEditMode();
        getForm().getGestioneJobInfo().getDisabilitaAllJobs().setEditMode();
        getForm().getGestioneJobInfo2().getRipristinaFotoGestioneJob().setEditMode();
        getForm().getGestioneJobInfo().getRicaricaGestioneJob().setEditMode();
        getSession().removeAttribute(ATTRIBUTE_VISUALIZZA_RIPRISTINA_FOTO);
        abilitaDisabilitaBottoniJob(!gestioneJobEjb.isDecJobFotoEmpty() && gestioneJobEjb.areAllJobsDisattivati(),
                getSession().getAttribute(ATTRIBUTE_FOTO_SALVATA) != null);
        forwardToPublisher(Application.Publisher.GESTIONE_JOB_RICERCA);
    }

    public void resetFiltriGestioneJobRicercaPage() {
        getForm().getGestioneJobRicercaFiltri().setEditMode();
        getForm().getGestioneJobRicercaFiltri().reset();
        getForm().getGestioneJobRicercaList().setTable(null);
        BaseTable ambitoTableBean = gestioneJobEjb.getAmbitoJob();
        getForm().getGestioneJobRicercaFiltri().getNm_ambito()
                .setDecodeMap(DecodeMap.Factory.newInstance(ambitoTableBean, "nm_ambito", "nm_ambito"));
        getForm().getGestioneJobRicercaFiltri().getTi_stato_job().setDecodeMap(getMappaTiStatoJob());
    }

    public static DecodeMap getMappaTiStatoJob() {
        BaseTable bt = new BaseTable();
        BaseRow br = new BaseRow();
        BaseRow br1 = new BaseRow();
        BaseRow br2 = new BaseRow();
        DecodeMap mappaStatoAgg = new DecodeMap();
        br.setString(COMBO_KEY_TI_STATO_JOB, Constants.StatoTimer.ATTIVO.name());
        bt.add(br);
        br1.setString(COMBO_KEY_TI_STATO_JOB, Constants.StatoTimer.DISATTIVO.name());
        bt.add(br1);
        br2.setString(COMBO_KEY_TI_STATO_JOB, Constants.StatoTimer.IN_ESECUZIONE.name());
        bt.add(br2);
        mappaStatoAgg.populatedMap(bt, COMBO_KEY_TI_STATO_JOB, COMBO_KEY_TI_STATO_JOB);
        return mappaStatoAgg;
    }

    public String[] calcolaInformazioniJob() {
        BaseRow infoJobRowBean = gestioneJobEjb.getInfoJobRowBean();
        int niTotJobPresenti = infoJobRowBean.getBigDecimal("ni_tot_job_presenti") != null
                ? infoJobRowBean.getBigDecimal("ni_tot_job_presenti").intValue() : 0;
        int niTotJobAttivi = infoJobRowBean.getBigDecimal("ni_tot_job_attivi") != null
                ? infoJobRowBean.getBigDecimal("ni_tot_job_attivi").intValue() : 0;
        int niTotJobDisattivi = infoJobRowBean.getBigDecimal("ni_tot_job_disattivi") != null
                ? infoJobRowBean.getBigDecimal("ni_tot_job_disattivi").intValue() : 0;

        String[] info = new String[3];
        info[0] = "" + niTotJobPresenti;
        info[1] = "" + niTotJobAttivi;
        info[2] = "" + niTotJobDisattivi;
        return info;
    }

    public void popolaInformazioniJob() {
        String[] info = calcolaInformazioniJob();
        getForm().getGestioneJobRicercaInfo().getNi_tot_job_presenti().setValue(info[0]);
        getForm().getGestioneJobRicercaInfo().getNi_tot_job_attivi().setValue(info[1]);
        getForm().getGestioneJobRicercaInfo().getNi_tot_job_disattivi().setValue(info[2]);
    }

    public void popolaInfoDecJobAmministrazioneJobTab() {
        String[] info = calcolaInformazioniJob();
        getForm().getGestioneJobInfo().getNi_tot_job_presenti().setValue(info[0]);
        getForm().getGestioneJobInfo().getNi_tot_job_attivi().setValue(info[1]);
        getForm().getGestioneJobInfo().getNi_tot_job_disattivi().setValue(info[2]);
    }

    @Override
    public void tabRicercaJobTabOnClick() throws EMFError {
        getForm().getGestioneJobTabs().setCurrentTab(getForm().getGestioneJobTabs().getRicercaJobTab());
        ricercaGestioneJob();
        forwardToPublisher(Application.Publisher.GESTIONE_JOB_RICERCA);
    }

    @Override
    public void tabAmmJobTabOnClick() throws EMFError {
        getForm().getGestioneJobTabs().setCurrentTab(getForm().getGestioneJobTabs().getAmmJobTab());
        abilitaDisabilitaBottoniJob(!gestioneJobEjb.isDecJobFotoEmpty() && gestioneJobEjb.areAllJobsDisattivati(),
                getSession().getAttribute(ATTRIBUTE_FOTO_SALVATA) != null);

        decoraDatiTabAmmJobTab();

        forwardToPublisher(Application.Publisher.GESTIONE_JOB_RICERCA);
    }

    public void decoraDatiTabAmmJobTab() {
        popolaInfoDecJobAmministrazioneJobTab();
        popolaInfoDecJobFotoAmministrazioneJobTab();

        BaseTable jobTB = gestioneJobEjb.getDecJobTableBeanPerAmm();
        getForm().getGestioneJobListPerAmm().setTable(jobTB);
        getForm().getGestioneJobListPerAmm().getTable().setPageSize(100);
        getForm().getGestioneJobListPerAmm().getTable().first();

        BaseTable jobFotoTB = gestioneJobEjb.getDecJobFotoTableBeanPerAmm();
        getForm().getGestioneJobFotoListPerAmm().setTable(jobFotoTB);
        getForm().getGestioneJobFotoListPerAmm().getTable().setPageSize(100);
        getForm().getGestioneJobFotoListPerAmm().getTable().first();

    }

    public void popolaInfoDecJobFotoAmministrazioneJobTab() {
        BaseRow infoJobFotoRowBean = gestioneJobEjb.getInfoJobFotoRowBean();
        int niTotJobPresenti2 = infoJobFotoRowBean.getBigDecimal("ni_tot_job_presenti2") != null
                ? infoJobFotoRowBean.getBigDecimal("ni_tot_job_presenti2").intValue() : 0;
        int niTotJobAttivi2 = infoJobFotoRowBean.getBigDecimal("ni_tot_job_attivi2") != null
                ? infoJobFotoRowBean.getBigDecimal("ni_tot_job_attivi2").intValue() : 0;
        int niTotJobDisattivi2 = infoJobFotoRowBean.getBigDecimal("ni_tot_job_disattivi2") != null
                ? infoJobFotoRowBean.getBigDecimal("ni_tot_job_disattivi2").intValue() : 0;
        int niTotJobNuovi2 = infoJobFotoRowBean.getBigDecimal("ni_tot_job_nuovi2") != null
                ? infoJobFotoRowBean.getBigDecimal("ni_tot_job_nuovi2").intValue() : 0;
        int niTotJobSoloFoto = infoJobFotoRowBean.getBigDecimal("ni_tot_job_solo_foto") != null
                ? infoJobFotoRowBean.getBigDecimal("ni_tot_job_solo_foto").intValue() : 0;

        Date dataLastFoto = infoJobFotoRowBean.getTimestamp("last_job_foto");
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

        getForm().getGestioneJobInfo2().getNi_tot_job_presenti2().setValue("" + niTotJobPresenti2);
        getForm().getGestioneJobInfo2().getNi_tot_job_attivi2().setValue("" + niTotJobAttivi2);
        getForm().getGestioneJobInfo2().getNi_tot_job_disattivi2().setValue("" + niTotJobDisattivi2);

        getForm().getGestioneJobInfo2().getNi_tot_job_nuovi2().setValue("" + niTotJobNuovi2);
        getForm().getGestioneJobInfo2().getNi_tot_job_solo_foto().setValue("" + niTotJobSoloFoto);

        getForm().getInfoJob2Section().setLegend("Foto dei job alla data " + df.format(dataLastFoto));

    }

    @Override
    public void totJobOperation() throws EMFError {
        ricercaGestioneJob();
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public void totJobAttiviOperation() throws EMFError {
        String[] attivi = new String[1];
        attivi[0] = Constants.StatoTimer.ATTIVO.name();
        getRequest().setAttribute(ATTRIBUTE_FROM_LINK, true);
        getForm().getGestioneJobRicercaFiltri().getTi_stato_job().setValues(attivi);
        ricercaGestioneJob();
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public void totJobDisattiviOperation() throws EMFError {
        String[] disattivi = new String[1];
        disattivi[0] = Constants.StatoTimer.DISATTIVO.name();
        getRequest().setAttribute(ATTRIBUTE_FROM_LINK, true);
        getForm().getGestioneJobRicercaFiltri().getTi_stato_job().setValues(disattivi);
        ricercaGestioneJob();
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public void ricercaGestioneJob() throws EMFError {
        getForm().getGestioneJobRicercaFiltri().getRicercaGestioneJob().setDisableHourGlass(true);
        GestioneJobRicercaFiltri filtri = getForm().getGestioneJobRicercaFiltri();
        if (getRequest().getAttribute(ATTRIBUTE_FROM_LINK) == null
                && (getRequest().getAttribute(ATTRIBUTE_FROM_LISTA_PRINC) == null)) {
            filtri.post(getRequest());
        }
        popolaInformazioniJob();
        if (filtri.validate(getMessageBox())) {
            BaseTable jobTB = gestioneJobEjb.getDecJobTableBean(filtri);
            getForm().getGestioneJobRicercaList().setTable(jobTB);
            getForm().getGestioneJobRicercaList().getTable().setPageSize(100);
            getForm().getGestioneJobRicercaList().getTable().first();
        }
        forwardToPublisher(Application.Publisher.GESTIONE_JOB_RICERCA);
    }

    @Override
    public void startMassivoGestioneJob() throws EMFError {
        // Recupero i record selezionati
        getForm().getGestioneJobRicercaList().post(getRequest());
        BaseTable tabella = (BaseTable) getForm().getGestioneJobRicercaList().getTable();

        if (tabella != null) {
            ArrayList<Object[]> listaSelezionati = new ArrayList<>();
            ArrayList<Object[]> listaNonSelezionati = new ArrayList<>();
            ArrayList<Object[]> listaNoTimer = new ArrayList<>();
            boolean almenoUnoSel = false;
            for (int i = 0; i < tabella.size(); i++) {
                BaseRow riga = tabella.getRow(i);
                if (riga.getString(JOB_SELEZIONATI).equals("1")) {
                    almenoUnoSel = true;
                    Object[] jobDaValutare = new Object[3];
                    jobDaValutare[0] = i;
                    jobDaValutare[1] = riga.getString(COMBO_KEY_NM_JOB);
                    jobDaValutare[2] = riga.getString(DS_JOB);
                    if (riga.getString(STATO_JOB).equals(Constants.StatoTimer.DISATTIVO.name())) {
                        if (riga.getString("ti_sched_job").equals("NO_TIMER")) {
                            listaNoTimer.add(jobDaValutare);
                        } else {
                            listaSelezionati.add(jobDaValutare);
                        }
                    } else {
                        listaNonSelezionati.add(jobDaValutare);
                    }
                }
            }

            if (almenoUnoSel) {// listaSelezionati

                StringBuilder message = new StringBuilder();
                StringBuilder jobSchedulatiString = new StringBuilder();
                for (Object[] obj : listaSelezionati) {
                    startGestioneJobOperation((String) obj[1], (String) obj[2]);
                    jobSchedulatiString.append(obj[1]).append("<br>");
                }
                if (jobSchedulatiString.length() > 1) {
                    message.append("Sono stati schedulati i seguenti job: <br><br>").append(jobSchedulatiString)
                            .append("<br>");
                }

                StringBuilder jobNonSchedulatiString = new StringBuilder();
                for (Object[] obj : listaNonSelezionati) {
                    jobNonSchedulatiString.append((String) obj[1]).append("<br>");
                }
                if (jobNonSchedulatiString.length() > 1) {
                    message.append("<br>Non sono stati schedulati i seguenti job: <br><br>")
                            .append(jobNonSchedulatiString)
                            .append("<br> in quanto in stato già ATTIVO o IN_ELABORAZIONE<br>");
                }

                StringBuilder jobNoTimerString = new StringBuilder();
                for (Object[] obj : listaNoTimer) {
                    jobNoTimerString.append((String) obj[1]).append("<br>");
                }
                if (jobNoTimerString.length() > 1) {
                    message.append("<br>Non sono stati schedulati i seguenti job: <br><br>").append(jobNoTimerString)
                            .append("<br> in quanto di tipo NO_TIMER. Per essi è possibile lanciare solo l'ESECUZIONE SINGOLA<br>");
                }

                message.append("<br>L'operazione richiesta diventerà effettiva entro il prossimo minuto.");
                getMessageBox().clear();
                getMessageBox().setViewMode(ViewMode.alert);
                getMessageBox().addInfo(message.toString());
            } else {
                getMessageBox().addInfo(INFO_NESSUN_JOB_SELEZIONATO);
            }
        } else {
            getMessageBox().addInfo(INFO_NESSUN_JOB_SELEZIONATO);
        }
        popolaInformazioniJob();
        ricercaGestioneJob();
    }

    public void startGestioneJobOperation() throws EMFError {
        // Recupero la riga sulla quale ho cliccato Start
        Integer riga = Integer.parseInt(getRequest().getParameter("riga"));
        // Eseguo lo start del job interessato
        String nmJob = getForm().getGestioneJobRicercaList().getTable().getRow(riga).getString(COMBO_KEY_NM_JOB);
        String dsJob = getForm().getGestioneJobRicercaList().getTable().getRow(riga).getString(DS_JOB);
        startGestioneJobOperation(nmJob, dsJob);
        getRequest().setAttribute(ATTRIBUTE_FROM_LISTA_PRINC, true);
        ricercaGestioneJob();
    }

    public void startGestioneJobOperation(String nmJob, String dsJob) {
        // Eseguo lo start del job interessato
        setJobVBeforeOperation(nmJob);
        eseguiNuovo(nmJob, dsJob, null, OPERAZIONE.START);
    }

    public void setJobVBeforeOperation(String nmJob) {
        Timestamp dataAttivazioneJob = getActivationDateJob(nmJob);
        StatoJob statoJob = new StatoJob(nmJob, null, null, null, null, null, null, null, dataAttivazioneJob);
        gestisciStatoJobNuovo(statoJob);
    }

    private boolean gestisciStatoJobNuovo(StatoJob statoJob) {
        // se non è ancora passato un minuto da quando è stato premuto un pulsante non posso fare nulla
        return jbossTimerEjb.isEsecuzioneInCorso(statoJob.getNomeJob());

    }

    private void eseguiNuovo(String nomeJob, String descrizioneJob, String nomeApplicazione, OPERAZIONE operazione) {
        // Messaggio sul logger di sistema
        StringBuilder info = new StringBuilder(descrizioneJob);
        info.append(": ").append(operazione.description()).append(" [").append(nomeJob);
        if (nomeApplicazione != null) {
            info.append("_").append(nomeApplicazione);
        }
        info.append("]");
        log.info(info.toString());

        String message = "Errore durante la schedulazione del job";

        switch (operazione) {
        case START:
            jbossTimerEjb.start(nomeJob, null);
            message = descrizioneJob
                    + ": job correttamente schedulato. L'operazione richiesta verrà schedulata correttamente entro il prossimo minuto.";
            break;
        case ESECUZIONE_SINGOLA:
            jbossTimerEjb.esecuzioneSingola(nomeJob, null);
            message = descrizioneJob
                    + ": job correttamente schedulato per esecuzione singola. L'operazione richiesta verrà effettuata entro il prossimo minuto.";
            break;
        case STOP:
            jbossTimerEjb.stop(nomeJob);
            message = descrizioneJob
                    + ": schedulazione job annullata. L'operazione richiesta diventerà effettiva entro il prossimo minuto.";
            break;
        }

        // Segnalo l'avvenuta operazione sul job
        getMessageBox().addMessage(new Message(MessageLevel.INF, message));
        getMessageBox().setViewMode(ViewMode.plain);
    }

    @Override
    public void stopMassivoGestioneJob() throws EMFError {
        // Recupero i record selezionati
        getForm().getGestioneJobRicercaList().post(getRequest());
        BaseTable tabella = (BaseTable) getForm().getGestioneJobRicercaList().getTable();

        if (tabella != null) {
            ArrayList<Object[]> listaSelezionati = new ArrayList<>();
            ArrayList<Object[]> listaNonSelezionati = new ArrayList<>();
            ArrayList<Object[]> listaNoTimer = new ArrayList<>();
            boolean almenoUnoSel = false;
            for (int i = 0; i < tabella.size(); i++) {
                BaseRow riga = tabella.getRow(i);
                if (riga.getString(JOB_SELEZIONATI).equals("1")) {
                    almenoUnoSel = true;
                    Object[] jobDaValutare = new Object[3];
                    jobDaValutare[0] = i;
                    jobDaValutare[1] = riga.getString(COMBO_KEY_NM_JOB);
                    jobDaValutare[2] = riga.getString(DS_JOB);
                    if (riga.getString(STATO_JOB).equals(Constants.StatoTimer.ATTIVO.name())) {
                        if (riga.getString("ti_sched_job").equals("NO_TIMER")) {
                            listaNoTimer.add(jobDaValutare);
                        } else {
                            listaSelezionati.add(jobDaValutare);
                        }
                    } else {
                        listaNonSelezionati.add(jobDaValutare);
                    }
                }
            }

            if (almenoUnoSel) {
                StringBuilder jobSchedulatiString = new StringBuilder();

                StringBuilder message = new StringBuilder();
                for (Object[] obj : listaSelezionati) {
                    stopGestioneJobOperation((String) obj[1], (String) obj[2]);
                    jobSchedulatiString.append((String) obj[1]).append("<br>");
                }
                if (jobSchedulatiString.length() > 1) {
                    message.append("Sono stati stoppati i seguenti job: <br><br>").append(jobSchedulatiString)
                            .append("<br>");
                }

                StringBuilder jobNonSchedulatiString = new StringBuilder();
                for (Object[] obj : listaNonSelezionati) {
                    jobNonSchedulatiString.append((String) obj[1]).append("<br>");
                }
                if (jobNonSchedulatiString.length() > 1) {
                    message.append("<br>Non sono stati stoppati i seguenti job: <br><br>")
                            .append(jobNonSchedulatiString)
                            .append("<br> in quanto in stato già DISATTIVO o IN_ESECUZIONE<br>");
                }

                StringBuilder jobNoTimerString = new StringBuilder();
                for (Object[] obj : listaNoTimer) {
                    jobNoTimerString.append((String) obj[1]).append("<br>");
                }
                if (jobNoTimerString.length() > 1) {
                    message.append("<br>Non sono stati stoppati i seguenti job: <br><br>").append(jobNoTimerString)
                            .append("<br> in quanto di tipo NO_TIMER<br>");
                }
                message.append("<br>L'operazione richiesta diventerà effettiva entro il prossimo minuto.");
                getMessageBox().clear();
                getMessageBox().setViewMode(ViewMode.alert);
                getMessageBox().addInfo(message.toString());
            } else {
                getMessageBox().addInfo(INFO_NESSUN_JOB_SELEZIONATO);
            }
        } else {
            getMessageBox().addInfo(INFO_NESSUN_JOB_SELEZIONATO);
        }
        popolaInformazioniJob();
        ricercaGestioneJob();
    }

    public void stopGestioneJobOperation() throws EMFError {
        // Recupero la riga sulla quale ho cliccato Start
        Integer riga = Integer.parseInt(getRequest().getParameter("riga"));
        // Eseguo lo start del job interessato
        String nmJob = getForm().getGestioneJobRicercaList().getTable().getRow(riga).getString(COMBO_KEY_NM_JOB);
        String dsJob = getForm().getGestioneJobRicercaList().getTable().getRow(riga).getString(DS_JOB);
        stopGestioneJobOperation(nmJob, dsJob);
        getRequest().setAttribute(ATTRIBUTE_FROM_LISTA_PRINC, true);
        ricercaGestioneJob();
    }

    public void stopGestioneJobOperation(String nmJob, String dsJob) {
        // Eseguo lo start del job interessato
        setJobVBeforeOperation(nmJob);
        eseguiNuovo(nmJob, dsJob, null, OPERAZIONE.STOP);
    }

    @Override
    public void esecuzioneSingolaMassivaGestioneJob() throws EMFError {
        // Recupero i record selezionati
        getForm().getGestioneJobRicercaList().post(getRequest());
        BaseTable tabella = (BaseTable) getForm().getGestioneJobRicercaList().getTable();

        if (tabella != null) {
            ArrayList<Object[]> listaSelezionati = new ArrayList<>();
            ArrayList<Object[]> listaNonSelezionati = new ArrayList<>();
            boolean almenoUnoSel = false;
            for (int i = 0; i < tabella.size(); i++) {
                BaseRow riga = tabella.getRow(i);
                if (riga.getString(JOB_SELEZIONATI).equals("1")) {
                    almenoUnoSel = true;
                    Object[] jobDaValutare = new Object[3];
                    jobDaValutare[0] = i;
                    jobDaValutare[1] = riga.getString(COMBO_KEY_NM_JOB);
                    jobDaValutare[2] = riga.getString(DS_JOB);
                    if (riga.getString(STATO_JOB).equals(Constants.StatoTimer.DISATTIVO.name())) {
                        listaSelezionati.add(jobDaValutare);
                    } else {
                        listaNonSelezionati.add(jobDaValutare);
                    }
                }
            }

            if (almenoUnoSel) {
                StringBuilder jobSchedulatiString = new StringBuilder();

                StringBuilder message = new StringBuilder();
                for (Object[] obj : listaSelezionati) {
                    esecuzioneSingolaGestioneJobOperation((String) obj[1], (String) obj[2]);
                    jobSchedulatiString.append((String) obj[1]).append("<br>");
                }

                if (jobSchedulatiString.length() > 1) {
                    message.append("Sono stati attivati in esecuzione singola i seguenti job: <br><br>")
                            .append(jobSchedulatiString).append("<br>");
                }

                StringBuilder jobNonSchedulatiString = new StringBuilder();
                for (Object[] obj : listaNonSelezionati) {
                    jobNonSchedulatiString.append((String) obj[1]).append("<br>");
                }
                if (jobNonSchedulatiString.length() > 1) {
                    message.append("<br>Non sono stati attivati in esecuzione singola i seguenti job: <br><br>")
                            .append(jobNonSchedulatiString)
                            .append("<br> in quanto in stato già ATTIVO o IN_ESECUZIONE<br>");
                }

                getMessageBox().clear();
                getMessageBox().setViewMode(ViewMode.alert);
                getMessageBox()
                        .addInfo(message + "L'operazione richiesta diventerà effettiva entro il prossimo minuto.");
            } else {
                getMessageBox().addInfo(INFO_NESSUN_JOB_SELEZIONATO);
            }
        } else {
            getMessageBox().addInfo(INFO_NESSUN_JOB_SELEZIONATO);
        }
        popolaInformazioniJob();
        ricercaGestioneJob();
    }

    public void esecuzioneSingolaGestioneJobOperation() throws EMFError {
        // Recupero la riga sulla quale ho cliccato Start
        Integer riga = Integer.parseInt(getRequest().getParameter("riga"));
        // Eseguo lo start del job interessato
        String nmJob = getForm().getGestioneJobRicercaList().getTable().getRow(riga).getString(COMBO_KEY_NM_JOB);
        String dsJob = getForm().getGestioneJobRicercaList().getTable().getRow(riga).getString(DS_JOB);
        esecuzioneSingolaGestioneJobOperation(nmJob, dsJob);
        getRequest().setAttribute(ATTRIBUTE_FROM_LISTA_PRINC, true);
        ricercaGestioneJob();
    }

    public void esecuzioneSingolaGestioneJobOperation(String nmJob, String dsJob) {
        // Eseguo lo start del job interessato
        setJobVBeforeOperation(nmJob);
        eseguiNuovo(nmJob, dsJob, null, OPERAZIONE.ESECUZIONE_SINGOLA);
    }

    @Override
    public void salvaFotoGestioneJob() throws EMFError {
        // Eseguo il salvataggio foto, solo se ho almeno 1 JOB attivo
        BaseTable tabella = (BaseTable) getForm().getGestioneJobListPerAmm().getTable();
        boolean trovatoAttivo = false;
        for (BaseRow riga : tabella) {
            if (riga.getString(STATO_JOB).equals(Constants.StatoTimer.ATTIVO.name())) {
                trovatoAttivo = true;
                break;
            }
        }
        if (trovatoAttivo) {
            gestioneJobEjb.salvaFoto();
            getSession().setAttribute(ATTRIBUTE_FOTO_SALVATA, true);
            getMessageBox().addInfo("Foto JOB salvata con successo!");
        } else {
            getMessageBox().addInfo("Nessun JOB attivo trovato: non Ã¨ stata salvata la foto!");
        }
        tabAmmJobTabOnClick();

        abilitaDisabilitaBottoniJob(!gestioneJobEjb.isDecJobFotoEmpty() && gestioneJobEjb.areAllJobsDisattivati(),
                getSession().getAttribute(ATTRIBUTE_FOTO_SALVATA) != null);
    }

    @Override
    public void disabilitaAllJobs() throws EMFError {
        gestioneJobEjb.disabilitaAllJobs();
        tabAmmJobTabOnClick();
        getMessageBox().addInfo("Tutti i job disattivati con successo!");
        forwardToPublisher(getLastPublisher());
    }

    @Override
    public void ricaricaGestioneJob() throws EMFError {
        tabAmmJobTabOnClick();
    }

    @Override
    public void ripristinaFotoGestioneJob() throws EMFError {
        gestioneJobEjb.ripristinaFotoGestioneJob();
        tabAmmJobTabOnClick();
        getMessageBox().addInfo(
                "Ripristino foto effettuato con successo! Attendere il minuto successivo per l'allineamento dei JOB eventualmente rischedulati");
        forwardToPublisher(getLastPublisher());
    }

    public void abilitaDisabilitaBottoniJob(boolean abilitaRipristinaFoto, boolean abilitaDisabilitaAllJobs) {
        if (abilitaRipristinaFoto) {
            getForm().getGestioneJobInfo2().getRipristinaFotoGestioneJob().setReadonly(false);
            getSession().setAttribute(ATTRIBUTE_VISUALIZZA_RIPRISTINA_FOTO, true);
        } else {
            getForm().getGestioneJobInfo2().getRipristinaFotoGestioneJob().setReadonly(true);
            getSession().removeAttribute(ATTRIBUTE_VISUALIZZA_RIPRISTINA_FOTO);
        }
        getForm().getGestioneJobInfo().getDisabilitaAllJobs().setReadonly(!abilitaDisabilitaAllJobs);
    }

    /**
     * Returns the activation date of job otherwise <code>null</code>
     *
     * @param jobName
     *            the job name
     *
     * @return timestamp con data attivazione del job
     */
    private Timestamp getActivationDateJob(String jobName) {
        Timestamp res = null;
        LogVVisLastSchedRowBean rb = jobBo.getLogVVisLastSched(jobName);

        if (rb.getFlJobAttivo() != null && rb.getFlJobAttivo().equals("1")) {
            res = rb.getDtEventoIni();
        }

        return res;
    }

    @Override
    public String getControllerName() {
        return Application.Actions.GESTIONE_JOB;
    }

    @Override
    protected String getDefaultPublsherName() {
        return Application.Publisher.GESTIONE_JOB;
    }

    @Secure(action = "Menu.AmministrazioneSistema.GestioneJob")
    @Override
    public void initOnClick() throws EMFError {
        // Sets the correct menu tab
        getUser().getMenu().reset();
        getUser().getMenu().select("Menu.AmministrazioneSistema.GestioneJob");

        // <editor-fold defaultstate="collapsed" desc="UI Gestione job per Sincronizzazione con Sacer">
        Timestamp dataAttivazioneJob = getActivationDateJob(Constants.JobEnum.SACER_SYNCRO.name());
        StatoJob allineaMetadati = new StatoJob(Constants.JobEnum.SACER_SYNCRO.name(),
                getForm().getAllineaMetadatiJob().getFl_data_accurata(),
                getForm().getAllineaMetadatiJob().getStartAllineaMetadati(),
                getForm().getAllineaMetadatiJob().getStartOnceAllineaMetadati(),
                getForm().getAllineaMetadatiJob().getStopAllineaMetadati(),
                getForm().getAllineaMetadatiJob().getDtNextActivation(), getForm().getAllineaMetadatiJob().getAttivo(),
                getForm().getAllineaMetadatiJob().getDtStartJob(), dataAttivazioneJob);

        gestisciStatoJob(allineaMetadati);
        // </editor-fold>

        forwardToPublisher(Application.Publisher.GESTIONE_JOB);
    }

    /**
     * Cuore della classe: qui Ã¨ definita la logica STANDARD degli stati dei job a livello di <b>interfaccia web<b>.
     * Per i job che devono implementare una logica non standard non Ã¨ consigliabile utilizzare questo metodo. Si Ã¨
     * cercato di mantenere una simmetria tra esposizione/inibizione dei controlli grafici.
     *
     * @param statoJob
     *            Rappresentazione dello stato <b>a livello di interfaccia grafica</b> del job.
     *
     */
    private void gestisciStatoJob(StatoJob statoJob) {
        // se non Ã¨ ancora passato un minuto da quando Ã¨ stato premuto un pulsante non posso fare nulla
        boolean operazioneInCorso = jbossTimerEjb.isEsecuzioneInCorso(statoJob.getNomeJob());

        statoJob.getFlagDataAccurata().setViewMode();
        statoJob.getFlagDataAccurata().setValue("L'operazione richiesta verrà effettuata entro il prossimo minuto.");
        statoJob.getStart().setHidden(operazioneInCorso);
        statoJob.getEsecuzioneSingola().setHidden(operazioneInCorso);
        statoJob.getStop().setHidden(operazioneInCorso);
        statoJob.getDataProssimaAttivazione().setHidden(operazioneInCorso);

        statoJob.getFlagDataAccurata().setHidden(!operazioneInCorso);
        if (operazioneInCorso) {
            return;
        }

        // Posso operare sulla pagina
        Date nextActivation = jbossTimerEjb.getDataProssimaAttivazione(statoJob.getNomeJob());
        boolean dataAccurata = jbossTimerEjb.isDataProssimaAttivazioneAccurata(statoJob.getNomeJob());
        DateFormat formato = new SimpleDateFormat(Constants.DATE_FORMAT_JOB);

        /*
         * Se il job Ã¨ già schedulato o in esecuzione singola nascondo il pulsante Start/esecuzione singola, mostro
         * Stop e visualizzo la prossima attivazione. Viceversa se Ã¨ fermo mostro Start e nascondo Stop
         */
        if (nextActivation != null) {
            statoJob.getStart().setViewMode();
            statoJob.getEsecuzioneSingola().setViewMode();
            statoJob.getStop().setEditMode();
            statoJob.getDataProssimaAttivazione().setValue(formato.format(nextActivation));
        } else {
            statoJob.getStart().setEditMode();
            statoJob.getEsecuzioneSingola().setEditMode();
            statoJob.getStop().setViewMode();
            statoJob.getDataProssimaAttivazione().setValue(null);
        }

        boolean flagHidden = nextActivation == null || dataAccurata;
        // se la data c'Ã¨ ma non Ã¨ accurata non visualizzare la "data prossima attivazione"
        statoJob.getDataProssimaAttivazione().setHidden(!flagHidden);

        if (statoJob.getDataAttivazione() != null) {
            statoJob.getCheckAttivo().setChecked(true);
            statoJob.getDataRegistrazioneJob()
                    .setValue(formato.format(new Date(statoJob.getDataAttivazione().getTime())));
        } else {
            statoJob.getCheckAttivo().setChecked(false);
            statoJob.getDataRegistrazioneJob().setValue(null);
        }
    }

    // <editor-fold defaultstate="collapsed" desc="UI Classe che mappa lo stato dei job">
    /**
     * Astrazione dei componenti della pagina utilizzati per i "box" dei job.
     *
     * @author Snidero_L
     */
    private static final class StatoJob {

        private final String nomeJob;
        private final Input<String> flagDataAccurata;
        private final Button<String> start;
        private final Button<String> esecuzioneSingola;
        private final Button<String> stop;
        private final Input<Timestamp> dataProssimaAttivazione;
        private final CheckBox<String> checkAttivo;
        private final Input<Timestamp> dataRegistrazioneJob;
        private final Timestamp dataAttivazione;

        // Mi serve per evitare una null pointer Exception
        private static final Button<String> NULL_BUTTON = new Button<>(null, "EMPTY_BUTTON", "Pulsante vuoto", null,
                null, null, false, true, true, false);

        public StatoJob(String nomeJob, Input<String> flagDataAccurata, Button<String> start,
                Button<String> esecuzioneSingola, Button<String> stop, Input<Timestamp> dataProssimaAttivazione,
                CheckBox<String> checkAttivo, Input<Timestamp> dataRegistrazioneJob, Timestamp dataAttivazione) {
            this.nomeJob = nomeJob;
            this.flagDataAccurata = flagDataAccurata;
            this.start = start;
            this.esecuzioneSingola = esecuzioneSingola;
            this.stop = stop;
            this.dataProssimaAttivazione = dataProssimaAttivazione;
            this.checkAttivo = checkAttivo;
            this.dataRegistrazioneJob = dataRegistrazioneJob;
            this.dataAttivazione = dataAttivazione;
        }

        public String getNomeJob() {
            return nomeJob;
        }

        public Input<String> getFlagDataAccurata() {
            return flagDataAccurata;
        }

        public Button<String> getStart() {
            if (start == null) {
                return NULL_BUTTON;
            }
            return start;
        }

        public Button<String> getEsecuzioneSingola() {
            return esecuzioneSingola;
        }

        public Button<String> getStop() {
            if (stop == null) {
                return NULL_BUTTON;
            }
            return stop;
        }

        public Input<Timestamp> getDataProssimaAttivazione() {
            return dataProssimaAttivazione;
        }

        public CheckBox<String> getCheckAttivo() {
            return checkAttivo;
        }

        public Input<Timestamp> getDataRegistrazioneJob() {
            return dataRegistrazioneJob;
        }

        public Timestamp getDataAttivazione() {
            return dataAttivazione;
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Funzioni non implementate">
    @Override
    public void process() throws EMFError {
        throw new UnsupportedOperationException(UNSOPPORTED_OPERATION_EXCEPTION);
    }

    @Override
    public void loadDettaglio() throws EMFError {
        throw new UnsupportedOperationException(UNSOPPORTED_OPERATION_EXCEPTION);
    }

    @Override
    public void undoDettaglio() throws EMFError {
        throw new UnsupportedOperationException(UNSOPPORTED_OPERATION_EXCEPTION);
    }

    @Override
    public void saveDettaglio() throws EMFError {
        throw new UnsupportedOperationException(UNSOPPORTED_OPERATION_EXCEPTION);
    }

    @Override
    public void dettaglioOnClick() throws EMFError {
        throw new UnsupportedOperationException(UNSOPPORTED_OPERATION_EXCEPTION);
    }

    @Override
    public void elencoOnClick() throws EMFError {
        if (getSession().getAttribute(FROM_RICERCA_JOB) != null) {
            getSession().removeAttribute(FROM_RICERCA_JOB);
        }
        goBack();
    }

    @Override
    public void insertDettaglio() throws EMFError {
        throw new UnsupportedOperationException(UNSOPPORTED_OPERATION_EXCEPTION);
    }

    @Override
    public void update(Fields<Field> fields) throws EMFError {
        throw new UnsupportedOperationException(UNSOPPORTED_OPERATION_EXCEPTION);
    }

    @Override
    public void delete(Fields<Field> fields) throws EMFError {
        throw new UnsupportedOperationException(UNSOPPORTED_OPERATION_EXCEPTION);
    }

    @Override
    public void reloadAfterGoBack(String publisherName) {
        getSession().removeAttribute(BACK_TO_RICERCA_JOB);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Methods to manage SacerSyncroJob schedulation">
    /**
     * Starts SacerSyncroJob schedulation.
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void startAllineaMetadati() throws EMFError {
        esegui(Constants.JobEnum.SACER_SYNCRO.name(), DESCRIZIONE_JOB, null, OPERAZIONE.START);
    }

    /**
     * Starts a single execution of SacerSyncroJob.
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void startOnceAllineaMetadati() throws EMFError {
        esegui(Constants.JobEnum.SACER_SYNCRO.name(), DESCRIZIONE_JOB, null, OPERAZIONE.ESECUZIONE_SINGOLA);
    }

    /**
     * Stops SacerSyncroJob schedulation.
     *
     * @throws EMFError
     *             errore generico
     */
    @Override
    public void stopAllineaMetadati() throws EMFError {
        esegui(Constants.JobEnum.SACER_SYNCRO.name(), DESCRIZIONE_JOB, null, OPERAZIONE.STOP);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Esecuzione di un job STANDARD">
    /**
     * Esegui una delle seguenti operazioni:
     * <ul>
     * <li>{@link OPERAZIONE#START}</li>
     * <li>{@link OPERAZIONE#ESECUZIONE_SINGOLA}</li>
     * <li>{@link OPERAZIONE#STOP}</li>
     * </ul>
     *
     * @param nomeJob
     *            nome del job
     * @param descrizioneJob
     *            descrizione (che comparirà sul LOG) del job
     * @param nomeApplicazione
     *            nome dell'applicazione. <b>Obbligatorio per i job che elaborano i LOG "PREMIS"</b>
     * @param operazione
     *            una delle tre operazioni dell'enum
     *
     * @throws EMFError
     *             Errore di esecuzione
     */
    private void esegui(String nomeJob, String descrizioneJob, String nomeApplicazione, OPERAZIONE operazione)
            throws EMFError {
        // Messaggio sul LOG di sistema
        StringBuilder info = new StringBuilder(descrizioneJob);
        info.append(": ").append(operazione.description()).append(" [").append(nomeJob);
        if (nomeApplicazione != null) {
            info.append("_").append(nomeApplicazione);
        }
        info.append("]");
        log.info(info.toString());

        String message = "Errore durante la schedulazione del job";

        switch (operazione) {
        case START:
            jbossTimerEjb.start(nomeJob, null);
            message = descrizioneJob + ": job correttamente schedulato";
            break;
        case ESECUZIONE_SINGOLA:
            jbossTimerEjb.esecuzioneSingola(nomeJob, null);
            message = descrizioneJob + ": job correttamente schedulato per esecuzione singola";
            break;
        case STOP:
            jbossTimerEjb.stop(nomeJob);
            message = descrizioneJob + ": schedulazione job annullata";
            break;
        }

        // Segnalo l'avvenuta operazione sul job
        getMessageBox().addMessage(new Message(MessageLevel.INF, message));
        getMessageBox().setViewMode(ViewMode.plain);
        // Risetto la pagina rilanciando l'initOnClick
        initOnClick();
    }
    // </editor-fold>
}
