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

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import it.eng.dispenser.entity.DecAttribDatiSpec;
import it.eng.dispenser.entity.DipAttribRisultato;
import it.eng.dispenser.entity.DipCampoRicerca;
import it.eng.dispenser.entity.DipDefAttributo;
import it.eng.dispenser.entity.DipElementoDettaglio;
import it.eng.dispenser.entity.DipElementoLista;
import it.eng.dispenser.entity.DipGruppoCampi;
import it.eng.dispenser.entity.DipGruppoDettaglio;
import it.eng.dispenser.entity.DipQueryWith;
import it.eng.dispenser.entity.DipRicerca;
import it.eng.dispenser.entity.DipServer;
import it.eng.dispenser.entity.DipUsoWith;
import it.eng.dispenser.entity.DipValoreCombo;
import it.eng.dispenser.entity.constraint.ConstDipAttribRisultato.TiDatoAttributo;
import it.eng.dispenser.entity.constraint.ConstDipCampoRicerca.TiDatoCampo;
import it.eng.dispenser.entity.constraint.ConstDipDefAttributo.TiTipoColonna;
import it.eng.dispenser.entity.constraint.ConstDipServer;
import it.eng.dispenser.slite.gen.tablebean.DipValoreComboTableBean;
import it.eng.dispenser.slite.gen.tablebean.DipValoreComboTableDescriptor;
import it.eng.dispenser.util.Transform;
import it.eng.spagoLite.db.oracle.decode.DecodeMap;
import it.eng.spagoLite.form.fields.Section;
import it.eng.spagoLite.form.fields.SingleValueField;
import it.eng.spagoLite.form.fields.impl.CheckBox;
import it.eng.spagoLite.form.fields.impl.ComboBox;
import it.eng.spagoLite.form.fields.impl.Input;
import it.eng.spagoLite.xmlbean.form.Field.Type;

/**
 *
 * @author Quaranta_M Questa classe si occupa del caricamento all'avvio dell'applicazione e on demand delle form per le
 *         ricerche configurate su DB. L'accesso per tutti i metodi pubblici è sincronizzato perché c'è un meccanismo di
 *         polling che ogni tot secondi va a vedere se sul db il server (se stesso) è da aggiornare in termini di cache
 *         in quando una ricerca può essere stata modificata sul DB oppure dalle (future) funzioni di amministrazione.
 *
 */
@SuppressWarnings("unchecked")
@Component
public class RicercheLoader {

    private final Logger logger = LoggerFactory.getLogger(RicercheLoader.class);

    /*
     * Questi tre oggetti servono per gestire il modo efficiente il locking di tutto l'oggetto facendo in modo che in
     * fare si lettura dei dati non venga loccato tutto l'oggetto ma soltanto in fase di scrittura.
     */
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock();
    private final Lock writeLock = readWriteLock.writeLock();

    @Autowired
    private PlatformTransactionManager transactionManager;

    @PersistenceContext
    private EntityManager entityManager;

    @Value(value = "${SERVER_NAME_SYSTEM_PROPERTY}")
    private String serverInstanceNameSystemProperty;

    // Contiene l'instance name del server
    private String serverInstanceName;

    // MAPPA CON <NOME_RICERCA, BEAN_FORM>
    private final Map<String, DipRicerca> ricerche = new HashMap<>();
    // MAPPA CON <NOME_RICERCA, RISULTATO_BEAN>
    private final Map<String, RisultatoBean> listaRisultati = new HashMap<>();
    // MAPPA CON <NOME_RICERCA, RISULTATO_BEAN>
    private final Map<String, RisultatoBean> dettaglioRisultato = new HashMap<>();
    // MAPPA CON <NOME_RICERCA, CLAUSOLA WITH> , preserva l'ordine di inserimento (ordinato quindi)
    private final Map<String, Map<Long, DipQueryWith>> listaTemporaneaClausoleWith = new HashMap<>();
    // MAPPA CON <NOME_RICERCA, CLAUSOLA WITH> , preserva l'ordine di inserimento (ordinato quindi)
    private final Map<String, Map<Long, DipQueryWith>> listaClausoleWithPerLista = new HashMap<>();
    // MAPPA CON <NOME_RICERCA, CLAUSOLA WITH> , preserva l'ordine di inserimento (ordinato quindi)
    private final Map<String, Map<Long, DipQueryWith>> listaClausoleWithPerDettaglio = new HashMap<>();

    @PostConstruct
    private void init() {
        serverInstanceName = System.getProperty(serverInstanceNameSystemProperty, "");
        if (serverInstanceName.equals("")) {
            logger.warn("Non e' stato possibile ottenere il serverInstanceName dalla System property [{}] ",
                    serverInstanceNameSystemProperty);
        }
        // Inizializzazione allo startup della cache delle ricerche
        loadForms();
        // Inizializzasione allo startup del serverName sulla tabella delle istanze server
        DipServer server = retrieveServer();

        if (server == null) {
            TransactionTemplate tpl = new TransactionTemplate(transactionManager);
            tpl.execute(new TransactionCallbackWithoutResult() {

                @Override
                protected void doInTransactionWithoutResult(TransactionStatus ts) {
                    DipServer server = new DipServer();
                    server.setCdServer(serverInstanceName);
                    server.setFlDaAllineare(ConstDipServer.flgDaAllineare.N.toString());
                    entityManager.persist(server);
                }
            });
            logger.debug("INIT applicazione effettuata.");
        }

    }

    // LOCK WRITE
    private void loadForms() {
        writeLock.lock();
        try {
            ricerche.clear();
            listaRisultati.clear();
            dettaglioRisultato.clear();
            listaClausoleWithPerLista.clear();
            listaClausoleWithPerDettaglio.clear();
            listaTemporaneaClausoleWith.clear();
            logger.info("Caricamento ricerche...");
            List<DipRicerca> res = retrieveRicerche();
            logger.info(String.format("Ricerche caricate: [%d]", (res == null ? 0 : res.size())));
            for (DipRicerca ricerca : res) {
                // DA usare per test in locale su vpn lenta:if (ricerca.getNmRicerca().equalsIgnoreCase("LUM")) {
                ricerche.put(ricerca.getNmRicerca(), ricerca);
                listaTemporaneaClausoleWith.put(ricerca.getNmRicerca(), new LinkedHashMap<Long, DipQueryWith>());
                listaClausoleWithPerLista.put(ricerca.getNmRicerca(), new LinkedHashMap<Long, DipQueryWith>());
                listaClausoleWithPerDettaglio.put(ricerca.getNmRicerca(), new LinkedHashMap<Long, DipQueryWith>());
                loadClausoleWithTemporanee(ricerca);
                loadAttributiLista(ricerca);
                loadAttributiDettaglio(ricerca);
                // La lista temporanea una volta inizializzato tutto non serve piu'
                listaTemporaneaClausoleWith.clear();
                // } /// ------------------
            }
        } finally {
            writeLock.unlock();
        }
    }

    @Scheduled(initialDelay = 60000, fixedDelayString = "${CACHE_DELAY_POLLING}")
    void polling() {
        logger.debug("Polling sulla cache, verifico se necessario ricaricare tutte le entita' in memoria...");
        /*
         * Il tutto deve essere demarcato in un contesto transazionale in quanto questo metodo non è chiamato
         * dall'esterno quindi non passa attraverso il proxy transazionale.
         */
        TransactionTemplate tpl = new TransactionTemplate(transactionManager);
        tpl.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus ts) {
                DipServer server = retrieveServer();
                if (server != null) {
                    if (server.getFlDaAllineare().equals(ConstDipServer.flgDaAllineare.S.name())) {
                        logger.info("Polling: la cache del server [{}] e' da aggiornare, ricarico tutti i dati dal db.",
                                serverInstanceName);
                        loadForms();
                        server.setFlDaAllineare(ConstDipServer.flgDaAllineare.N.name());
                        entityManager.merge(server);
                    } else {
                        logger.debug("Polling: il server [{}] e' gia' allineato.", serverInstanceName);
                    }
                } else {
                    /*
                     * per ulteriore robustezza se il record per qualche motivo non e' presente sul DB lo inserisco con
                     * flag a 'N'
                     */
                    server = new DipServer();
                    server.setCdServer(serverInstanceName);
                    server.setFlDaAllineare(ConstDipServer.flgDaAllineare.N.toString());
                    entityManager.persist(server);
                }
            }
        });
        logger.debug("Fine polling verifica caricamento entita' in memoria.");
    }

    // LOCK READ
    public DipRicerca getRicerca(String nomeRicerca) {
        readLock.lock();
        DipRicerca ric = null;
        try {
            ric = ricerche.get(nomeRicerca);
        } finally {
            readLock.unlock();
        }
        return ric;
    }

    public int getMaxResultRicerca(String nomeRicerca) {
        readLock.lock();
        int ric = -1;
        try {
            ric = ricerche.get(nomeRicerca) != null ? ricerche.get(nomeRicerca).getNiResLimit().intValue() : -1;
        } finally {
            readLock.unlock();
        }
        return ric;
    }

    // LOCK READ
    public RisultatoBean getListaRisultati(String nomeRicerca) {
        readLock.lock();
        RisultatoBean bean = null;
        try {
            bean = listaRisultati.get(nomeRicerca);
        } finally {
            readLock.unlock();
        }
        return bean;
    }

    // LOCK READ
    public Map<Long, DipQueryWith> getClausoleWithPerDettaglio(String nomeRicerca) {
        readLock.lock();
        Map<Long, DipQueryWith> ret = null;
        try {
            ret = this.listaClausoleWithPerDettaglio.get(nomeRicerca);
        } finally {
            readLock.unlock();
        }
        return ret;
    }

    // LOCK READ
    public Map<Long, DipQueryWith> getClausoleWithPerLista(String nomeRicerca) {
        readLock.lock();
        Map<Long, DipQueryWith> ret = null;
        try {
            ret = this.listaClausoleWithPerLista.get(nomeRicerca);
        } finally {
            readLock.unlock();
        }
        return ret;
    }

    // LOCK READ
    public RisultatoBean getDettaglioRisultato(String nomeRicerca) {
        readLock.lock();
        RisultatoBean bean = null;
        try {
            bean = dettaglioRisultato.get(nomeRicerca);
        } finally {
            readLock.unlock();
        }
        return bean;
    }

    // LOCK READ
    /*
     * il lock è abbastanza inutile in questo contesto, ma lo metto per scupolo
     */
    public Map<String, DipRicerca> getRicerche() {
        readLock.lock();
        Map<String, DipRicerca> mappa = null;
        try {
            mappa = ricerche;
        } finally {
            readLock.unlock();
        }
        return mappa;
    }

    // LOCK READ
    @Transactional
    public DynamicSpagoLiteForm getSpagoLiteForm(String nomeRicerca) {
        readLock.lock();
        DynamicSpagoLiteForm form = null;
        try {
            if (nomeRicerca == null) {
                throw new IllegalArgumentException();
            }
            form = new DynamicSpagoLiteForm();
            DipRicerca ricerca = ricerche.get(nomeRicerca);
            creaFormRicerca(ricerca, form);
            creaListaRisultati(ricerca, form);
            creaFormDettaglio(ricerca, form);
        } finally {
            readLock.unlock();
        }

        return form;
    }

    // serve la transazione per i campi lazy, viene impostata dal padre
    private void creaListaRisultati(DipRicerca ricerca, DynamicSpagoLiteForm form) {
        Query q = entityManager.createNamedQuery("DipElementoLista.findByNMRicerca");
        q.setParameter("nmRicerca", ricerca.getNmRicerca());
        List<DipElementoLista> elementiLista = q.getResultList();

        for (DipElementoLista elLista : elementiLista) {
            DipAttribRisultato attributoRisultato = elLista.getDipAttribRisultato();
            SingleValueField<?> comp = null;
            boolean isHidden = elLista.getFlVisibile().equals("0");
            switch (TiDatoAttributo.valueOf(attributoRisultato.getTiDatoAttributo())) {
            case CHECK:
                comp = new CheckBox<String>(form.getRicercaList(), attributoRisultato.getNmAttributo(),
                        elLista.getDsElementoLista(), "", Type.STRING, null, false, isHidden, false, false,
                        StringUtils.EMPTY);
                break;
            case COMBO:
                comp = new ComboBox<String>(form.getRicercaList(), attributoRisultato.getNmAttributo(),
                        elLista.getDsElementoLista(), "", Type.STRING, null, false, isHidden, false, false, 0, true,
                        false/*
                              * Nota: la combo attualmente non viene popolata, non si prevede l'utilizzo, al momento,
                              * del componente select2 (disattivato per default)
                              */);
                break;
            case DATA:
                comp = new Input<Timestamp>(form.getRicercaList(), attributoRisultato.getNmAttributo(),
                        elLista.getDsElementoLista(), "", Type.DATE, null, false, false, isHidden, false, false, 0,
                        null, null, null, StringUtils.EMPTY);
                break;
            case RADIO:
                break;
            case STRINGA:
                comp = new Input<String>(form.getRicercaList(), attributoRisultato.getNmAttributo(),
                        elLista.getDsElementoLista(), "", Type.STRING, null, false, false, isHidden, false, false, 0,
                        null, null, null, StringUtils.EMPTY);
                break;
            case NUMERO:
                comp = new Input<BigDecimal>(form.getRicercaList(), attributoRisultato.getNmAttributo(),
                        elLista.getDsElementoLista(), "", Type.INTEGER, null, false, false, isHidden, false, false, 0,
                        null, null, null, StringUtils.EMPTY);
                break;
            default:
                break;
            }
            form.getRicercaList().addComponent(comp);
        }

    }

    // serve la transazione per i campi lazy, viene impostata dal padre
    private void creaFormRicerca(DipRicerca ricerca, DynamicSpagoLiteForm form) {
        for (DipGruppoCampi gruppoCampi : ricerca.getDipGruppoCampiOrdinati()) {
            Section section = new Section(null, "FILTR_" + gruppoCampi.getNmGruppo(), gruppoCampi.getDsGruppo(),
                    gruppoCampi.getDsGruppo(), false, false, true, true, true);
            form.addComponent(section);
            for (DipCampoRicerca campo : gruppoCampi.getDipCampoRicercaOrdinati()) {
                SingleValueField<?> comp = null;
                switch (TiDatoCampo.valueOf(campo.getTiDatoCampo())) {
                case CHECK:
                    comp = new CheckBox<String>(form.getFormRicerca(), campo.getNmCampo(), campo.getDsCampo(), "",
                            Type.STRING, null, false, false, false, false, StringUtils.EMPTY);
                    break;
                case COMBO:
                    comp = new ComboBox<String>(form.getFormRicerca(), campo.getNmCampo(), campo.getDsCampo(), "",
                            Type.STRING, null, false, false, false, false, 0, true, false);
                    populateCombo((ComboBox<String>) comp, getDipValoreCombosLazy(campo));
                    break;
                case COMBOAUTOCOMPILA:
                    comp = new ComboBox<String>(form.getFormRicerca(), campo.getNmCampo(), campo.getDsCampo(), "",
                            Type.STRING, null, false, false, false, false, 0, true, true);
                    populateCombo((ComboBox<String>) comp, getDipValoreCombosLazy(campo));
                    break;
                case DATA:
                    comp = new Input<Timestamp>(form.getFormRicerca(), campo.getNmCampo(), campo.getDsCampo(), "",
                            Type.DATE, null, false, false, false, false, false, 0, null, null, null, StringUtils.EMPTY);
                    break;
                case RADIO:
                    break;
                case STRINGA:
                    comp = new Input<String>(form.getFormRicerca(), campo.getNmCampo(), campo.getDsCampo(), "",
                            Type.STRING, null, false, false, false, false, false, 0, null, null, null,
                            StringUtils.EMPTY);
                    break;
                case NUMERO:
                    comp = new Input<BigDecimal>(form.getFormRicerca(), campo.getNmCampo(), campo.getDsCampo(), "",
                            Type.INTEGER, null, false, false, false, false, false, 0, null, null, null,
                            StringUtils.EMPTY);
                    break;
                default:
                    break;
                }
                form.getFormRicerca().addComponent(comp);
            }
        }
        form.getFormRicerca().setEditMode();
    }

    private List<DipValoreCombo> getDipValoreCombosLazy(DipCampoRicerca campo) {
        Query q = entityManager.createQuery(
                "SELECT u FROM DipValoreCombo u WHERE u.dipCampoRicerca = :dipCampoRicerca ORDER BY nmValoreCombo ASC",
                DipValoreCombo.class);
        q.setParameter("dipCampoRicerca", campo);
        return q.getResultList();
    }

    private void creaFormDettaglio(DipRicerca ricerca, DynamicSpagoLiteForm form) {
        for (DipGruppoDettaglio gruppo : ricerca.getDipGruppoDettaglios()) {
            Section section = new Section(null, "DETT_" + gruppo.getNmGruppoDettaglio(), gruppo.getDsGruppoDettaglio(),
                    gruppo.getDsGruppoDettaglio(), false, false, true, true, true);
            form.addComponent(section);
            for (DipElementoDettaglio elemento : gruppo.getDipElementoDettaglioOrdinati()) {
                SingleValueField<?> comp = null;
                boolean isHidden = elemento.getFlVisibile().equals("0");
                DipAttribRisultato attributo = elemento.getDipAttribRisultato();
                switch (TiDatoAttributo.valueOf(attributo.getTiDatoAttributo())) {
                case DATA:
                    comp = new Input<Timestamp>(form.getFormDettaglio(), attributo.getNmAttributo(),
                            elemento.getDsElementoDettaglio(), "", Type.DATE, null, false, false, isHidden, false,
                            false, 0, null, null, null, StringUtils.EMPTY);
                    break;

                case STRINGA:
                    comp = new Input<String>(form.getFormDettaglio(), attributo.getNmAttributo(),
                            elemento.getDsElementoDettaglio(), "", Type.STRING, null, false, false, isHidden, false,
                            false, 0, null, null, null, StringUtils.EMPTY);
                    break;
                case NUMERO:
                    comp = new Input<BigDecimal>(form.getFormDettaglio(), attributo.getNmAttributo(),
                            elemento.getDsElementoDettaglio(), "", Type.INTEGER, null, false, false, isHidden, false,
                            false, 0, null, null, null, StringUtils.EMPTY);
                    break;
                default:
                    break;
                }
                form.getFormDettaglio().addComponent(comp);
            }
        }
    }

    private void loadAttributiDettaglio(DipRicerca ricerca) {
        RisultatoBean rb = new RisultatoBean();
        Query q = entityManager.createNamedQuery("DipAttribRisultato.findAllDettaglio");
        q.setParameter("nmRicerca", ricerca.getNmRicerca());
        logger.info(String.format("Caricamento AttributiDettaglio per la ricerca [%s]", ricerca.getNmRicerca()));
        List<DipAttribRisultato> listaDipAttribRisultatos = q.getResultList();
        logger.info(String.format("Trovati %d AttributiDettaglio per la ricerca [%s]",
                (listaDipAttribRisultatos == null ? 0 : listaDipAttribRisultatos.size()), ricerca.getNmRicerca()));
        for (DipAttribRisultato attributoRisultato : listaDipAttribRisultatos) {
            logger.debug(String.format("Gestione DipAttribRisultato [%s] per la ricerca [%s]",
                    attributoRisultato.getNmAttributo(), ricerca.getNmRicerca()));
            for (DipDefAttributo col : attributoRisultato.getDipDefAttributos()) {
                logger.debug(String.format("Gestione DipDefAttributo [%s] idAttribDatiSpec [%d] tiDefAttrib [%s]",
                        col.getNmDatoProfilo(),
                        (col.getIdAttribDatiSpec() == null ? 0 : col.getIdAttribDatiSpec().longValueExact()),
                        col.getTiDefAttrib()));
                long idStrut = col.getIdStrut().longValue();
                rb.addIdStruttura(idStrut);
                switch (TiTipoColonna.valueOf(col.getTiDefAttrib())) {
                case DATO_PROFILO:
                    rb.putDatoProfilo(idStrut, col.getNmDatoProfilo() + " as " + attributoRisultato.getNmAttributo());
                    break;
                case METADATO_DOC:
                case METADATO_UD:
                    DecAttribDatiSpec sp = entityManager.find(DecAttribDatiSpec.class,
                            col.getIdAttribDatiSpec().longValueExact());
                    rb.putMetadatoUnico(idStrut,
                            String.format("'%s' as %s", sp.getNmAttribDatiSpec(), attributoRisultato.getNmAttributo()));
                    break;
                case DATO_CALCOLATO:
                    List<DipQueryWith> duw = getQueryWith(col);
                    if (duw != null && duw.size() > 0) {
                        for (Iterator<DipQueryWith> iterator = duw.iterator(); iterator.hasNext();) {
                            DipQueryWith dq = iterator.next();
                            DipQueryWith dqTemp = listaTemporaneaClausoleWith.get(ricerca.getNmRicerca())
                                    .get(dq.getIdQueryWith());
                            listaClausoleWithPerDettaglio.get(ricerca.getNmRicerca()).put(dqTemp.getIdQueryWith(),
                                    dqTemp);
                        }
                    }
                    rb.putQueryCalcolo(idStrut, col.getCdQueryCalc() + " as " + attributoRisultato.getNmAttributo());
                    break;
                default:
                    break;
                }
            }
        }
        logger.info(String.format("Fine caricamento per la ricerca [%s]", ricerca.getNmRicerca()));
        riordinaLista(ricerca.getNmRicerca(), listaClausoleWithPerDettaglio);
        dettaglioRisultato.put(ricerca.getNmRicerca(), rb);

    }

    private void loadAttributiLista(DipRicerca ricerca) {
        RisultatoBean rb = new RisultatoBean();
        rb.setMaxResults(ricerca.getNiResLimit().intValue());
        logger.info(String.format("Caricamento attributi lista per la ricerca [%s]", ricerca.getNmRicerca()));
        Query q = entityManager.createNamedQuery("DipAttribRisultato.findAllLista");
        q.setParameter("nmRicerca", ricerca.getNmRicerca());
        List<DipAttribRisultato> listaDipAttribRisultatos = q.getResultList();
        logger.info(String.format("Trovati %d attributi lista per la ricerca [%s]",
                (listaDipAttribRisultatos == null ? 0 : listaDipAttribRisultatos.size()), ricerca.getNmRicerca()));
        // Si crea una lista ordinata per posizione di ORDER BY da leggere successivamente
        SortedMap<BigDecimal, StringBuilder> listaOrderBy = new TreeMap<>();
        for (DipAttribRisultato attributoRisultato : listaDipAttribRisultatos) {
            logger.debug(String.format("Gestione DipAttribRisultato [%s] per la ricerca [%s]",
                    attributoRisultato.getNmAttributo(), ricerca.getNmRicerca()));
            if (attributoRisultato.getFlOrderby().equals("1")) {
                StringBuilder strOrderBy = new StringBuilder(attributoRisultato.getNmAttributo());
                strOrderBy.append(" ");
                if (attributoRisultato.getTiOrderby() != null
                        && attributoRisultato.getTiOrderby().equalsIgnoreCase("DESC")) {
                    strOrderBy.append("DESC");
                } else {
                    strOrderBy.append("ASC");
                }
                // Inserisce nella lista ordinata la singola clausola di ORDER BY
                listaOrderBy.put(attributoRisultato.getNiOrderby(), strOrderBy);
            }
            for (DipDefAttributo col : attributoRisultato.getDipDefAttributos()) {
                long idStrut = col.getIdStrut().longValue();
                rb.addIdStruttura(idStrut);
                switch (TiTipoColonna.valueOf(col.getTiDefAttrib())) {
                case DATO_PROFILO:
                    rb.putDatoProfilo(idStrut, col.getNmDatoProfilo() + " as " + attributoRisultato.getNmAttributo());
                    break;
                case METADATO_DOC:
                case METADATO_UD:
                    DecAttribDatiSpec sp = entityManager.find(DecAttribDatiSpec.class,
                            col.getIdAttribDatiSpec().longValueExact());
                    rb.putMetadatoUnico(idStrut,
                            String.format("'%s' as %s", sp.getNmAttribDatiSpec(), attributoRisultato.getNmAttributo()));
                    break;
                case DATO_CALCOLATO:
                    List<DipQueryWith> duw = getQueryWith(col);
                    if (duw != null && duw.size() > 0) {
                        for (Iterator<DipQueryWith> iterator = duw.iterator(); iterator.hasNext();) {
                            DipQueryWith dq = iterator.next();
                            DipQueryWith dqTemp = listaTemporaneaClausoleWith.get(ricerca.getNmRicerca())
                                    .get(dq.getIdQueryWith());
                            listaClausoleWithPerLista.get(ricerca.getNmRicerca()).put(dqTemp.getIdQueryWith(), dqTemp);
                        }
                    }
                    rb.putQueryCalcolo(idStrut, col.getCdQueryCalc() + " as " + attributoRisultato.getNmAttributo());
                    break;
                }
            }
        }
        logger.info(String.format("Fine caricamento attributi lista per la ricerca [%s]", ricerca.getNmRicerca()));
        riordinaLista(ricerca.getNmRicerca(), listaClausoleWithPerLista);
        /*
         * Se sono state trovate delle colonne ORDER BY si estraggono dalla lista ordinata per posizione e si
         * inseriscono in RisultatoBean nelle clausole ORDER BY.
         */
        if (listaOrderBy.size() > 0) {
            Set<?> set = listaOrderBy.keySet();
            Iterator<?> it = set.iterator();
            while (it.hasNext()) {
                Object next = listaOrderBy.get(it.next());
                rb.addOrderbyClause(next.toString());
            }
        }

        listaRisultati.put(ricerca.getNmRicerca(), rb);

    }

    /*
     * Carica tutte le clausole WITH della ricerca passata come parametro preservandone l'ordinamento. Le WITH
     * all'interno dello statement di ricerca (lista o dettaglio) devono seguire un preciso ordinamento!
     */
    private void loadClausoleWithTemporanee(DipRicerca ricerca) {
        logger.info(String.format("Caricamento ClausoleWithTemporanee per la ricerca [%s]", ricerca.getNmRicerca()));
        Query q = entityManager.createNamedQuery("DipQueryWith.findByDipRicercaOrdered");
        q.setParameter("dipRicerca", ricerca);
        List<DipQueryWith> listaQueryWith = q.getResultList();
        logger.info(String.format("Trovate %d ClausoleWithTemporanee per la ricerca [%s]",
                (listaQueryWith == null ? 0 : listaQueryWith.size()), ricerca.getNmRicerca()));
        if (listaQueryWith != null) {
            Map<Long, DipQueryWith> mappa = new LinkedHashMap<>();
            for (DipQueryWith with : listaQueryWith) {
                mappa.put(with.getIdQueryWith(), with);
            }
            listaTemporaneaClausoleWith.put(ricerca.getNmRicerca(), mappa);
        }
    }

    private void populateCombo(ComboBox<String> comp, List<DipValoreCombo> dipValoreCombos) {

        try {
            DipValoreComboTableBean tb = (DipValoreComboTableBean) Transform.entities2TableBean(dipValoreCombos);
            DecodeMap dm = DecodeMap.Factory.newInstance(tb, DipValoreComboTableDescriptor.COL_CD_VALORE_COMBO,
                    DipValoreComboTableDescriptor.COL_NM_VALORE_COMBO);
            comp.setDecodeMap(dm);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }

    }

    private List<DipRicerca> retrieveRicerche() {
        Query q = entityManager.createNamedQuery("DipRicerca.findAll");
        return q.getResultList();
    }

    /*
     * Torna il record associato all'attuale istanza server
     */
    private DipServer retrieveServer() {
        DipServer server = null;
        Query q = entityManager.createNamedQuery("DipServer.findByServerName");
        q.setParameter("cdServer", serverInstanceName);
        try {
            server = (DipServer) q.getSingleResult();
        } catch (NoResultException ex) {
            logger.info("Nessun record trovato per il server [{}]", serverInstanceName);
        }
        return server;
    }

    /*
     * Torna le clausole WITH a cui è associato l'oggetto DefAttributo
     */
    private List<DipQueryWith> getQueryWith(DipDefAttributo defAtt) {
        List<DipUsoWith> lista = null;
        ArrayList<DipQueryWith> al = null;
        Query q = entityManager.createNamedQuery("DipUsoWith.findByDipDefAttributo");
        q.setParameter("DipDefAttributo", defAtt);
        try {
            lista = q.getResultList();
            if (lista != null && lista.size() > 0) {
                al = new ArrayList<>();
                for (Iterator<DipUsoWith> iterator = lista.iterator(); iterator.hasNext();) {
                    DipUsoWith uso = iterator.next();
                    DipQueryWith qw = uso.getIdQueryWith();
                    al.add(qw);
                }
            }

        } catch (NoResultException ex) {
            logger.info("Nessun record trovato per l'attributo [{}]", defAtt.getIdDefAttributo());
        }
        return al;
    }

    /*
     * Funzione di riordinamento della lista (dettaglio o elenco) passata come argomento per preservare l'ordinamento
     * delle clausole WITH presenti in listaTemporaneaClausoleWith
     */
    private void riordinaLista(String nomeRicerca, Map<String, Map<Long, DipQueryWith>> listaPar) {
        Map<String, Map<Long, DipQueryWith>> listaClausoleWithNuova = new HashMap<>();
        Map<Long, DipQueryWith> nuovaMappa = new LinkedHashMap<Long, DipQueryWith>();
        listaClausoleWithNuova.put(nomeRicerca, nuovaMappa);

        Map<Long, DipQueryWith> listaClausoleTemporanee = listaTemporaneaClausoleWith.get(nomeRicerca);
        Map<Long, DipQueryWith> listaClausoleWith = listaPar.get(nomeRicerca);
        Set<Long> setClausoleTemporanee = listaClausoleTemporanee.keySet();
        Iterator<Long> it = setClausoleTemporanee.iterator();
        while (it.hasNext()) {
            Long ogg = it.next();
            if (listaClausoleWith.containsKey(ogg)) {
                nuovaMappa.put(ogg, listaClausoleTemporanee.get(ogg));
            }
        }
        listaPar.put(nomeRicerca, nuovaMappa);
    }

}
