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

package it.eng.dispenser.bo;

import it.eng.dispenser.bean.DatoProfilo;
import it.eng.dispenser.bean.MetadatoDoc;
import it.eng.dispenser.bean.MetadatoUD;
import it.eng.dispenser.entity.AroUnitaDoc;
import it.eng.dispenser.entity.DipSyncroAro;
import it.eng.dispenser.entity.DipTipoDatoProfilo;
import it.eng.dispenser.entity.QryDocByVlMetaDt;
import it.eng.dispenser.entity.QryDocByVlMetaDtId;
import it.eng.dispenser.entity.QryDocByVlMetaNum;
import it.eng.dispenser.entity.QryDocByVlMetaNumId;
import it.eng.dispenser.entity.QryDocByVlMetaStr;
import it.eng.dispenser.entity.QryDocByVlMetaStrId;
import it.eng.dispenser.entity.QryUdByVlDatoProfiloDt;
import it.eng.dispenser.entity.QryUdByVlDatoProfiloDtId;
import it.eng.dispenser.entity.QryUdByVlDatoProfiloNum;
import it.eng.dispenser.entity.QryUdByVlDatoProfiloNumId;
import it.eng.dispenser.entity.QryUdByVlDatoProfiloStr;
import it.eng.dispenser.entity.QryUdByVlDatoProfiloStrId;
import it.eng.dispenser.entity.QryUdByVlMetaDt;
import it.eng.dispenser.entity.QryUdByVlMetaDtId;
import it.eng.dispenser.entity.QryUdByVlMetaNum;
import it.eng.dispenser.entity.QryUdByVlMetaNumId;
import it.eng.dispenser.entity.QryUdByVlMetaStr;
import it.eng.dispenser.entity.QryUdByVlMetaStrId;
import it.eng.dispenser.entity.constraint.ConstDipLogJob;
import it.eng.dispenser.entity.constraint.ConstDipTipoDatoProfilo;
import it.eng.dispenser.grantedEntity.DecTipoUnitaDoc;
import it.eng.dispenser.helper.DispenserHelper;
import it.eng.dispenser.job.helper.JobHelper;
import it.eng.dispenser.util.Constants;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.xml.bind.DatatypeConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 *
 * @author Moretti_Lu
 *
 */
@SuppressWarnings("unchecked")
@Stateless(mappedName = "SacerSyncroJob")
@LocalBean
public class SacerSyncroJob {

    private static final Logger logger = LoggerFactory.getLogger(SacerSyncroJob.class.getName());

    private static final String STRUCTURE_ID = "structureID";
    private static final String NUMERICO = "NUMERICO";
    private static final String ALFANUMERICO = "ALFANUMERICO";
    private static final String DATE_FROM = "dateFrom";
    private static final String DATE_TO = "dateTo";
    private static final String DELETE_FROM = "DELETE FROM ";
    private static final String EXCETPION_CANCELLA_TROPPI_DATI = "Cancello piu di un dato!";

    @PersistenceContext
    private EntityManager em;

    @EJB(mappedName = "java:app/Dispenser-ejb/JobHelper")
    private JobHelper jobHelper;

    @EJB(mappedName = "java:app/Dispenser-ejb/DispenserHelper")
    private DispenserHelper dispenserHelper;

    @Resource
    private SessionContext context;

    public void sincronizzazioneConSacer() throws Exception {
        logger.info("Sincronizzazione con Sacer - Inizio esecuzione job");

        // Obtains the current date
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date now = cal.getTime();

        logger.info("Sincronizzazione con Sacer - Recupero strutture");
        List<BigDecimal> structureIDList = getStructures();

        for (BigDecimal structureID : structureIDList) {
            Date dateTo = null;
            Date dateLastElaboration = null;
            // Obtains the date of the last elaboration
            dateLastElaboration = getDateLastElaboration(structureID);
            logger.info("Sincronizzazione con Sacer - IDStruct: {}  data ultima elaborazione: {}.", structureID,
                    dateLastElaboration);
            // Recupero un nuovo riferimento al EJB - cosi passo dal container
            SacerSyncroJob newSacerSyncroJob = context.getBusinessObject(SacerSyncroJob.class);
            while (dateLastElaboration.before(now)) {
                cal.setTime(dateLastElaboration);
                cal.add(Calendar.HOUR, +1);
                dateTo = cal.getTime();
                // Execute the transaction
                newSacerSyncroJob.execute(structureID, dateLastElaboration, dateTo);
                dateLastElaboration = dateTo;
            }
            logger.info("Sincronizzazione con Sacer - IDStruct: {} terminata.", structureID);
        }
        jobHelper.writeAtomicLogJob(Constants.JobEnum.SACER_SYNCRO.name(),
                ConstDipLogJob.tiEvento.FINE_ESECUZIONE.name());
        logger.info("Sincronizzazione con Sacer - Fine esecuzione job");
    }

    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    public void execute(BigDecimal structureID, Date dateFrom, Date dateTo) throws Exception {
        List<MetadatoUD> metadatiUd;
        List<MetadatoDoc> metadatiDoc;
        List<DatoProfilo> datiProfilo;
        List<DipTipoDatoProfilo> tipiDatoProfiloUd;
        logger.info("Processo struttura {} per il range di date {} - {}", structureID, dateFrom, dateTo);
        tipiDatoProfiloUd = getTipiDatoProfilo(dateFrom, ConstDipTipoDatoProfilo.tiOrigineDatoProfilo.UNI_DOC.name());

        metadatiUd = getMetadatiUDInserted(structureID, dateFrom, dateTo);
        insertMetadatiUD(metadatiUd);

        metadatiDoc = getMetadatiDocInserted(structureID, dateFrom, dateTo);
        insertMetadatiDoc(metadatiDoc);

        long numUdEscluse = 0;
        Object[] ogg = getDatiProfiloInserted(structureID, dateFrom, dateTo, tipiDatoProfiloUd, numUdEscluse);
        datiProfilo = (List<DatoProfilo>) ogg[0];
        numUdEscluse = (Long) ogg[1];
        insertDatiProfilo(datiProfilo);
        /* Gestire la parte del documento principale */

        metadatiUd = getMetadatiUDAnnul(structureID, dateFrom, dateTo);
        deleteMetadatiUD(metadatiUd);

        metadatiDoc = getMetadatiDocAnnul(structureID, dateFrom, dateTo);
        deleteMetadatiDoc(metadatiDoc);

        datiProfilo = getDatiProfiloAnnul(structureID, dateFrom, dateTo, tipiDatoProfiloUd);
        deleteDatiProfilo(datiProfilo);

        updateLastElaboration(structureID, dateFrom, dateTo, numUdEscluse);

    }

    /**
     * Restituisce la lista di strutture su cui eseguire la sincronizzazione.
     *
     * @return
     */
    private List<BigDecimal> getStructures() {
        Query q = em.createQuery("SELECT d.idStrut FROM DipSyncroAro d");
        return q.getResultList();
    }

    /**
     * Restituisce la data dell'ultima sincronizzazione.
     *
     * @param idStructure
     *
     * @return
     *
     * @throws Exception
     */
    private Date getDateLastElaboration(BigDecimal structureID) throws Exception {
        String query = "SELECT d.dtUltimaDataElab FROM DipSyncroAro d WHERE d.idStrut = :structureID";

        Query q = em.createQuery(query);
        q.setParameter(STRUCTURE_ID, structureID);

        List<Date> list = q.getResultList();

        if (list.isEmpty()) {
            logger.warn("Entry non presente nella DIP_SYNCRO_ARO per idStructure = {}", structureID);
            throw new Exception("Errore nella configurazione della DIP_SYNCRO_ARO");
        } else if (list.size() == 1) {
            return list.get(0);
        } else {
            // Impossibile ID_STRUCT UF
            logger.warn("Ci sono due entry nella DIP_SYNCRO_ARO per idStructure = {}", structureID);
            throw new Exception("Ci sono due entry nella DIP_SYNCRO_ARO per idStructure = " + structureID);
        }
    }

    // METODI PER METADATI UNITA DOCUMENTARIE
    /**
     * Restituisce i <code>MetadatoUD</code> delle UnitÃƒÂ  Documentarie inserite da <code>dateFrom</code> (inclusa) a
     * <code>dateTo</code> (esclusa) per la <code>structureID</code>.
     *
     * @param structID
     * @param dateFrom
     * @param dateTo
     *
     * @return
     */
    private List<MetadatoUD> getMetadatiUDInserted(BigDecimal structID, Date dateFrom, Date dateTo) {
        // MAC#31007 - Correzione logica di caricamento dati dei versamenti per le dimensioni di prod
        String query = "SELECT NEW  it.eng.dispenser.bean.MetadatoUD(ads.idAttribDatiSpec, ud.idUnitaDoc, vad.dlValore, ads.tiAttribDatiSpec) "
                + "FROM AroUnitaDoc ud, VrsSessioneVers ses " + "JOIN ud.aroUsoXsdDatiSpecs da "
                + "JOIN da.aroValoreAttribDatiSpecs vad " + "JOIN vad.decAttribDatiSpec ads "
                + "WHERE ses.idStrut = :structureID " + "AND ses.dtChiusura >= :dateFrom "
                + "AND ses.dtChiusura < :dateTo " + "AND da.tiEntitaSacer = 'UNI_DOC' "
                + "AND ads.tiAttribDatiSpec IS NOT NULL " + "AND vad.dlValore IS NOT NULL "
                + "AND ud.orgStrut.idStrut=ses.idStrut " + "AND ud.idUnitaDoc=ses.idUnitaDoc "
                // MAC#31066 - correzione estrazione dati UD in caso di aggiunta documenti
                + "AND ses.tiSessioneVers='VERSAMENTO' " + "AND ses.tiStatoSessioneVers='CHIUSA_OK' ";

        ;
        Query q = em.createQuery(query);
        q.setParameter(STRUCTURE_ID, structID.longValue());
        q.setParameter(DATE_FROM, dateFrom);
        q.setParameter(DATE_TO, dateTo);
        return q.getResultList();
    }

    /**
     * Inserisce le entry dei <code>MetadatoUD</code> dalle tabelle
     *
     * @param metadati
     *
     * @return
     */
    private void insertMetadatiUD(List<MetadatoUD> metadati) {
        BigDecimal idUd = BigDecimal.ZERO;
        for (MetadatoUD metadato : metadati) {
            // MEV#30872 - Arricchire il livello di log INFO dei caricamenti effettuati dal JOB_SYNCRO
            if (idUd.compareTo(metadato.getUdID()) != 0) {
                idUd = metadato.getUdID();
                logger.info("Inserisco metadati UD per ud: {}", idUd);
            }
            switch (metadato.getType()) {
            case "DATA":
                QryUdByVlMetaDtId idDt = new QryUdByVlMetaDtId();
                idDt.setIdAttribDatiSpec(metadato.getAttrID());
                idDt.setIdUnitaDoc(metadato.getUdID());
                QryUdByVlMetaDt entryDt = new QryUdByVlMetaDt();
                entryDt.setQryUdByVlMetaDtId(idDt);
                Calendar cal = DatatypeConverter.parseDate(metadato.getValue());
                Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
                entryDt.setDlValore(timestamp);
                em.persist(entryDt);
                break;

            case NUMERICO:
                QryUdByVlMetaNumId idNum = new QryUdByVlMetaNumId();
                idNum.setIdAttribDatiSpec(metadato.getAttrID());
                idNum.setIdUnitaDoc(metadato.getUdID());
                QryUdByVlMetaNum entryNum = new QryUdByVlMetaNum();
                entryNum.setQryUdByVlMetaNumId(idNum);
                entryNum.setDlValore(new BigDecimal(metadato.getValue()));
                em.persist(entryNum);
                break;

            case ALFANUMERICO:
                QryUdByVlMetaStrId id = new QryUdByVlMetaStrId();
                id.setIdAttribDatiSpec(metadato.getAttrID());
                id.setIdUnitaDoc(metadato.getUdID());
                QryUdByVlMetaStr entryStr = new QryUdByVlMetaStr();
                entryStr.setQryUdByVlMetaStrId(id);
                String value = metadato.getValue();
                if (value.length() > Constants.PREFIX_INDEX_LENGTH) {
                    String prefisso = value.substring(0, Constants.PREFIX_INDEX_LENGTH - 1);
                    entryStr.setDlPrefissoValore(prefisso.toUpperCase());
                } else {
                    entryStr.setDlPrefissoValore(value.toUpperCase());
                }
                entryStr.setDlValore(value);
                em.persist(entryStr);
                break;
            default:
                break;
            }
        }
    }

    /**
     * Restituisce i <code>MetadatoUD</code> delle Unita Documentarie cancellate da <code>dateFrom</code> (inclusa) a
     * <code>dateTo</code> (esclusa) per la <code>structureID</code>.
     *
     * @param structID
     * @param dateFrom
     * @param dateTo
     *
     * @return
     */
    private List<MetadatoUD> getMetadatiUDAnnul(BigDecimal structID, Date dateFrom, Date dateTo) {
        String query = "select new  it.eng.dispenser.bean.MetadatoUD(ads.idAttribDatiSpec, ud.idUnitaDoc, vad.dlValore, ads.tiAttribDatiSpec) "
                + "from AroRichAnnulVer rich " + "join rich.aroStatoRichAnnulVersCor  stato "
                + "join rich.aroItemRichAnnulVers item " + "join item.aroUnitaDoc ud "
                + "join ud.aroUsoXsdDatiSpecs da " + "join da.aroValoreAttribDatiSpecs vad "
                + "join vad.decAttribDatiSpec ads " + "where rich.orgStrut.idStrut = :structureID "
                + "and stato.tiStatoRichAnnulVers='EVASA' " + "and stato.dtRegStatoRichAnnulVers >= :dateFrom "
                + "and stato.dtRegStatoRichAnnulVers < :dateTo " + "and da.tiEntitaSacer = 'UNI_DOC' "
                + "and ads.tiAttribDatiSpec IS NOT NULL " + "and vad.dlValore IS NOT NULL "
                + "and item.tiItemRichAnnulVers = 'UNI_DOC' " + "and item.tiStatoItem='ANNULLATO' ";
        Query q = em.createQuery(query);
        q.setParameter(STRUCTURE_ID, structID.longValue());
        q.setParameter(DATE_FROM, dateFrom);
        q.setParameter(DATE_TO, dateTo);
        return q.getResultList();
    }

    /**
     * Cancella le entry dei <code>MetadatoUD</code> dalle tabelle
     *
     * @param metadati
     *
     * @return
     */
    private int deleteMetadatiUD(List<MetadatoUD> metadati) throws Exception {
        int result = 0;

        for (MetadatoUD metadato : metadati) {
            int del = 0;

            StringBuilder query = new StringBuilder();
            query.append(DELETE_FROM);

            switch (metadato.getType()) {
            case "DATA":
                query.append("QryUdByVlMetaDt");
                break;

            case NUMERICO:
                query.append("QryUdByVlMetaNum");
                break;

            case ALFANUMERICO:
                query.append("QryUdByVlMetaStr");
                break;
            default:
                break;
            }

            query.append(" d WHERE (d.id.idUnitaDoc = :udID) AND (d.id.idAttribDatiSpec = :attribID)");
            Query q = em.createQuery(query.toString());
            q.setParameter("udID", metadato.getUdID());
            q.setParameter("attribID", metadato.getAttrID());
            del = q.executeUpdate();

            if (del == 1) {
                result += del;
            }
            if (del > 1) {
                throw new Exception(EXCETPION_CANCELLA_TROPPI_DATI);
            }
        }

        return result;
    }

    // METODI PER METADATI DOCUMENTI
    /**
     * Restituisce i <code>MetadatoDoc</code> delle UnitÃƒÂ  Documentarie inserite da <code>dateFrom</code> (inclusa) a
     * <code>dateTo</code> (esclusa) per la <code>structureID</code>.
     *
     * @param structID
     * @param dateFrom
     * @param dateTo
     *
     * @return
     */
    private List<MetadatoDoc> getMetadatiDocInserted(BigDecimal structID, Date dateFrom, Date dateTo) {
        // MAC#31007 - Correzione logica di caricamento dati dei versamenti per le dimensioni di prod
        String query = "SELECT NEW  it.eng.dispenser.bean.MetadatoDoc(ads.idAttribDatiSpec, doc.idDoc, ud.idUnitaDoc, vad.dlValore, ads.tiAttribDatiSpec) "
                + "FROM AroUnitaDoc ud, VrsSessioneVers ses " + "JOIN ud.aroUsoXsdDatiSpecs da "
                + "JOIN da.aroValoreAttribDatiSpecs vad " + "JOIN vad.decAttribDatiSpec ads " + "JOIN da.aroDoc doc "
                + "WHERE (ses.idStrut = :structureID) " + "AND (ses.dtChiusura >= :dateFrom "
                + "AND ses.dtChiusura < :dateTo) " + "AND (da.tiEntitaSacer = 'DOC') "
                + "AND (ads.tiAttribDatiSpec IS NOT NULL) " + "AND (vad.dlValore IS NOT NULL)"
                + "AND ud.orgStrut.idStrut=ses.idStrut " + "AND ud.idUnitaDoc=ses.idUnitaDoc "
                + "AND ses.tiSessioneVers='VERSAMENTO' " + "AND ses.tiStatoSessioneVers='CHIUSA_OK' ";

        Query q = em.createQuery(query);
        q.setParameter(STRUCTURE_ID, structID.longValue());
        q.setParameter(DATE_FROM, dateFrom);
        q.setParameter(DATE_TO, dateTo);
        return q.getResultList();
    }

    /**
     * Inserts the <code>MetadatoDoc</code> into tables.
     *
     * @param metadati
     *
     * @return
     */
    private void insertMetadatiDoc(List<MetadatoDoc> metadati) {
        BigDecimal idUd = BigDecimal.ZERO;
        for (MetadatoDoc metadato : metadati) {
            // MEV#30872 - Arricchire il livello di log INFO dei caricamenti effettuati dal JOB_SYNCRO
            if (idUd.compareTo(metadato.getUdID()) != 0) {
                idUd = metadato.getUdID();
                logger.info("Inserisco metadati DOC per ud: {}", idUd);
            }
            switch (metadato.getType()) {
            case "DATA":
                QryDocByVlMetaDtId idDt = new QryDocByVlMetaDtId();
                idDt.setIdAttribDatiSpec(metadato.getAttrID());
                idDt.setIdDoc(metadato.getDocID());
                idDt.setIdUnitaDoc(metadato.getUdID());
                QryDocByVlMetaDt entryDt = new QryDocByVlMetaDt();
                entryDt.setQryDocByVlMetaDtId(idDt);
                Calendar cal = DatatypeConverter.parseDate(metadato.getValue());
                Timestamp timestamp = new Timestamp(cal.getTimeInMillis());
                entryDt.setDlValore(timestamp);
                em.persist(entryDt);
                break;

            case NUMERICO:
                QryDocByVlMetaNumId compositeId = new QryDocByVlMetaNumId();
                compositeId.setIdAttribDatiSpec(metadato.getAttrID());
                compositeId.setIdDoc(metadato.getDocID());
                compositeId.setIdUnitaDoc(metadato.getUdID());
                QryDocByVlMetaNum entryNum = new QryDocByVlMetaNum();
                entryNum.setQryDocByVlMetaNumId(compositeId);
                entryNum.setDlValore(new BigDecimal(metadato.getValue()));
                em.persist(entryNum);
                break;

            case ALFANUMERICO:
                QryDocByVlMetaStrId idStr = new QryDocByVlMetaStrId();
                idStr.setIdAttribDatiSpec(metadato.getAttrID());
                idStr.setIdDoc(metadato.getDocID());
                idStr.setIdUnitaDoc(metadato.getUdID());
                QryDocByVlMetaStr entryStr = new QryDocByVlMetaStr();
                entryStr.setQryDocByVlMetaStrId(idStr);
                String value = metadato.getValue();
                if (value.length() > Constants.PREFIX_INDEX_LENGTH) {
                    String prefisso = value.substring(0, Constants.PREFIX_INDEX_LENGTH - 1);
                    entryStr.setDlPrefissoValore(prefisso.toUpperCase());
                } else {
                    entryStr.setDlPrefissoValore(value.toUpperCase());
                }
                entryStr.setDlValore(value);
                em.persist(entryStr);
                break;
            default:
                break;
            }
        }
    }

    /**
     * Restituisce i <code>MetadatoDoc</code> delle Unita Documentarie cancellate da <code>dateFrom</code> (inclusa) a
     * <code>dateTo</code> (esclusa) per la <code>structureID</code>.
     *
     * @param structID
     * @param dateFrom
     * @param dateTo
     *
     * @return
     */
    private List<MetadatoDoc> getMetadatiDocAnnul(BigDecimal structID, Date dateFrom, Date dateTo) {

        String query = "select NEW it.eng.dispenser.bean.MetadatoDoc(ads.idAttribDatiSpec, doc.idDoc, ud.idUnitaDoc, vad.dlValore, ads.tiAttribDatiSpec) "
                + "from AroRichAnnulVer rich " + "join rich.aroStatoRichAnnulVersCor  stato "
                + "join rich.aroItemRichAnnulVers item " + "join item.aroUnitaDoc ud "
                + "join ud.aroUsoXsdDatiSpecs da " + "join da.aroValoreAttribDatiSpecs vad "
                + "join vad.decAttribDatiSpec ads " + "join da.aroDoc doc "
                + "where rich.orgStrut.idStrut = :structureID " + "and stato.tiStatoRichAnnulVers = 'EVASA' "
                + "and stato.dtRegStatoRichAnnulVers >= :dateFrom and stato.dtRegStatoRichAnnulVers< :dateTo "
                + "and da.tiEntitaSacer = 'DOC' " + "and ads.tiAttribDatiSpec IS NOT NULL "
                + "and vad.dlValore IS NOT NULL " + "and item.tiItemRichAnnulVers = 'UNI_DOC' "
                + "and item.tiStatoItem ='ANNULLATO' ";
        Query q = em.createQuery(query);
        q.setParameter(STRUCTURE_ID, structID.longValue());
        q.setParameter(DATE_FROM, dateFrom);
        q.setParameter(DATE_TO, dateTo);
        return q.getResultList();
    }

    /**
     * Cancella le entry dei <code>MetadatoDoc</code> dalle tabelle
     *
     * @param metadati
     *
     * @return
     */
    private int deleteMetadatiDoc(List<MetadatoDoc> metadati) throws Exception {
        int result = 0;

        for (MetadatoDoc metadato : metadati) {
            int del = 0;

            StringBuilder query = new StringBuilder();
            query.append(DELETE_FROM);

            switch (metadato.getType()) {
            case "DATA":
                query.append("QryDocByVlMetaDt");
                break;

            case NUMERICO:
                query.append("QryDocByVlMetaNum");
                break;

            case ALFANUMERICO:
                query.append("QryDocByVlMetaStr");
                break;
            default:
                break;
            }

            query.append(
                    " d WHERE (d.id.idUnitaDoc = :udID) AND (d.id.idAttribDatiSpec = :attribID) AND (d.id.idDoc = :docID)");
            Query q = em.createQuery(query.toString());
            q.setParameter("udID", metadato.getUdID());
            q.setParameter("attribID", metadato.getAttrID());
            q.setParameter("docID", metadato.getDocID());
            del = q.executeUpdate();
            if (del == 1) {
                result += del;
            }
            if (del > 1) {
                throw new Exception(EXCETPION_CANCELLA_TROPPI_DATI);
            }
        }
        return result;
    }

    /*
     * Torna l'elenco dei dati di profilo di tipo documento principale o Unita doc e con data istituz. maggiore uguale a
     * quella passata.
     */
    private List<DipTipoDatoProfilo> getTipiDatoProfilo(Date dateFrom, String origineDato) {
        Query q = em.createNamedQuery("DipTipoDatoProfilo.findByDtIstituzEqualOrSmallerAndOrigine");
        q.setParameter("dtIstituz", dateFrom);
        q.setParameter("origine", origineDato);
        return q.getResultList();
    }

    // METODI PER DATI PROFILO
    /**
     * Restituisce i<code>DatoProfilo</code> delle UnitÃ Â  Documentarie inseritele informazioni di profilazione delle
     * Unita Documentarie inserite da <code>dateFrom</code> (inclusa) a <code>dateTo</code> (esclusa) per la
     * <code>structureID</code>. Il contatore deve essere tornato al chiamante perchÃ© viene incrementato se alcune ud
     * vengono escluse dal conteggio dei dati di profilo nel caso di Strumenti urbanistici
     *
     * @param structID
     * @param dateFrom
     * @param dateTo
     * @param tipiDatoProfilo
     * @param numUdEscluse
     *
     * @return
     */
    private Object[] getDatiProfiloInserted(BigDecimal structID, Date dateFrom, Date dateTo,
            List<DipTipoDatoProfilo> tipiDatoProfilo, long numUdEscluse) {

        /* Deve determinare se si sta lavorando su una struttura che fa capo ad una struttura fittizia (caso PUG) */
        boolean isStrumentoUrbanistico = false;
        BigDecimal idStruturaFittizia = dispenserHelper.getStrutFittiziaByStrutReale(structID);
        if (idStruturaFittizia != null) {
            isStrumentoUrbanistico = true;
        }
        // MAC#31007 - Correzione logica di caricamento dati dei versamenti per le dimensioni di prod
        String query = "SELECT ud FROM AroUnitaDoc ud, VrsSessioneVers ses " + "WHERE (ses.idStrut = :structureID) "
                + "AND (ses.dtChiusura >= :dateFrom " + "AND ses.dtChiusura < :dateTo) "
                + "AND ud.orgStrut.idStrut=ses.idStrut " + "AND ud.idUnitaDoc=ses.idUnitaDoc "
                // MAC#31066 - correzione estrazione dati UD in caso di aggiunta documenti
                + "AND ses.tiSessioneVers='VERSAMENTO' " + "AND ses.tiStatoSessioneVers='CHIUSA_OK' ";
        Query q = em.createQuery(query);
        q.setParameter(STRUCTURE_ID, structID.longValue());
        q.setParameter(DATE_FROM, dateFrom);
        q.setParameter(DATE_TO, dateTo);
        List<AroUnitaDoc> list = q.getResultList();
        List<DatoProfilo> res = new ArrayList<>();
        Method metodo = null;
        Map<String, Method> mappaMetodi = new HashMap<>();
        for (AroUnitaDoc uniDoc : list) {
            /*
             * Nel caso in cui si sta lavorando per una struttura che fa campo ad una fittizia (caso PUG) deve
             * verificare se la UD reale estratta esiste anche nella struttura fittizia. Se non esiste la esclude dal
             * calcolo
             */
            DecTipoUnitaDoc decTipoUnitaDocFittizio = null;
            DecTipoUnitaDoc decTipoUnitaDocReale = null;
            if (isStrumentoUrbanistico) {
                decTipoUnitaDocReale = em.find(DecTipoUnitaDoc.class, uniDoc.getIdTipoUnitaDoc().longValueExact());
                decTipoUnitaDocFittizio = dispenserHelper.getDecTipoUnitaDocByStrutAndNome(idStruturaFittizia,
                        decTipoUnitaDocReale.getNmTipoUnitaDoc());
                if (decTipoUnitaDocFittizio == null) {
                    numUdEscluse++;
                    continue;
                }
            }
            // fine modifica

            // Carica l'array dei metodi reflection per invocazione dinamica
            if (mappaMetodi.size() == 0) {
                String nomeMetodo = null;
                for (DipTipoDatoProfilo tipo : tipiDatoProfilo) {
                    nomeMetodo = getNomeMetodoCamelCase(tipo.getNmColonnaDb());
                    try {
                        /*
                         * Nel caso di tipo conversione ENTE_STRUTTURA o TIPO_UD il metodo dinamico non viene utilizzato
                         * altrimenti si generava il Warning di mancato agganciodel metodo. MAC#27609 - DIPS - indagine
                         * sul mancato aggiornamento di alcune ricerche
                         */
                        if (isStrumentoUrbanistico && tipo.getTipoConversione() != null
                                && (tipo.getTipoConversione().equals(DipTipoDatoProfilo.TipoConversione.ENTE_STRUTTURA)
                                        || tipo.getTipoConversione()
                                                .equals(DipTipoDatoProfilo.TipoConversione.TIPO_UD))) {
                            metodo = null; // non viene utilizzato per l'invocazione dinamica!
                            logger.debug(
                                    "Trovato il metodo {} per il dato di profilo configurato ma non necessario agganciarlo.",
                                    nomeMetodo);
                        } else {
                            metodo = uniDoc.getClass().getMethod(nomeMetodo);
                            logger.debug("Agganciato il metodo {} per il dato di profilo configurato.", nomeMetodo);
                        }
                        mappaMetodi.put(tipo.getNmColonnaDb(), metodo);
                    } catch (NoSuchMethodException | SecurityException ex) {
                        logger.warn("Errore nell'aggancio del metodo {} della classe AroUnitaDoc", nomeMetodo, ex);
                    }
                }
            }
            Object valore = null;
            DatoProfilo datoProfilo = null;
            for (DipTipoDatoProfilo tipo : tipiDatoProfilo) {
                try {
                    // MODIFICA PER PUG!
                    /*
                     * Se tipo conversione (DEL TIPO DATO PROFILO) Ã¨ uguale a "TIPO_UD" si prende il nome del tipo UD
                     * reale e l'id struttura reale, si cerca a quale struttura fittizia appartiene e poi con l'id della
                     * struttura fittizia ed il nome tipo ud estratto si cerca il tipo ud associato alla struttura
                     * fittizia. L'ID del tipo UD fittizio trovato viene messo nel "valore" del DatoProfilo
                     */
                    if (isStrumentoUrbanistico && tipo.getTipoConversione() != null
                            && tipo.getTipoConversione().equals(DipTipoDatoProfilo.TipoConversione.TIPO_UD)) {
                        // MAC#19891 - Problema al job null pointer exception. Possono esserci UD non presenti nella
                        // struttura fittizia, da bypassare
                        datoProfilo = new DatoProfilo(structID, tipo.getNmTipoDatoProfilo(), uniDoc.getIdUnitaDoc(),
                                new BigDecimal(decTipoUnitaDocFittizio.getIdTipoUnitaDoc()), tipo.getTiDatoProfilo(),
                                uniDoc.getIdTipoUnitaDoc());
                        res.add(datoProfilo);
                    } else if (isStrumentoUrbanistico && tipo.getTipoConversione() != null
                            && tipo.getTipoConversione().equals(DipTipoDatoProfilo.TipoConversione.ENTE_STRUTTURA)) {
                        /* Modifica per la EVO#26718 Gara PAB */
                        datoProfilo = new DatoProfilo(structID, tipo.getNmTipoDatoProfilo(), uniDoc.getIdUnitaDoc(),
                                new BigDecimal(uniDoc.getOrgStrut().getIdStrut()), tipo.getTiDatoProfilo(),
                                uniDoc.getIdTipoUnitaDoc());
                        res.add(datoProfilo);
                    } else { // Testare la nullitÃ Â  del dato
                        metodo = mappaMetodi.get(tipo.getNmColonnaDb());
                        valore = metodo.invoke(uniDoc);
                        if (valore != null) {
                            datoProfilo = new DatoProfilo(structID, tipo.getNmTipoDatoProfilo(), uniDoc.getIdUnitaDoc(),
                                    valore, tipo.getTiDatoProfilo(), uniDoc.getIdTipoUnitaDoc());
                            res.add(datoProfilo);
                        }
                    }
                    // FINE MODIFICA PER PUG

                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    logger.warn("Errore nell'invocazione del metodo {} della classe AroUnitaDoc", metodo.getName(), ex);
                }
            }
        }
        Object[] ogg = new Object[2];
        ogg[0] = res;
        ogg[1] = numUdEscluse;
        return ogg;
    }

    /**
     * Inserts the <code>DatoProfilo</code> into tables.
     *
     * @param metadati
     *
     * @return
     */
    private void insertDatiProfilo(List<DatoProfilo> dati) {
        BigDecimal idUd = BigDecimal.ZERO;
        for (DatoProfilo dato : dati) {
            // MEV#30872 - Arricchire il livello di log INFO dei caricamenti effettuati dal JOB_SYNCRO
            if (idUd.compareTo(dato.getUdID()) != 0) {
                idUd = dato.getUdID();
                logger.info("Inserisco metadati PROFILO per ud: {}", idUd);
            }
            switch (dato.getType()) {
            case "DATA":
                QryUdByVlDatoProfiloDtId idData = new QryUdByVlDatoProfiloDtId();
                idData.setIdStrut(dato.getStructID());
                idData.setTiDatoProfilo(dato.getTipoDatoProfilo());
                idData.setIdUnitaDoc(dato.getUdID());
                QryUdByVlDatoProfiloDt entryDate = new QryUdByVlDatoProfiloDt();
                entryDate.setQryUdByVlDatoProfiloDtId(idData);
                entryDate.setDlValore(new Timestamp(((Date) dato.getValue()).getTime()));
                entryDate.setIdTipoUnitaDoc(dato.getIdTipoUnitaDoc());
                em.persist(entryDate);
                break;

            case NUMERICO:
                QryUdByVlDatoProfiloNumId idNum = new QryUdByVlDatoProfiloNumId();
                idNum.setIdStrut(dato.getStructID());
                idNum.setTiDatoProfilo(dato.getTipoDatoProfilo());
                idNum.setIdUnitaDoc(dato.getUdID());
                QryUdByVlDatoProfiloNum entryNum = new QryUdByVlDatoProfiloNum();
                entryNum.setQryUdByVlDatoProfiloNumId(idNum);
                entryNum.setDlValore((BigDecimal) dato.getValue());
                entryNum.setIdTipoUnitaDoc(dato.getIdTipoUnitaDoc());
                em.persist(entryNum);
                break;

            case ALFANUMERICO:
                QryUdByVlDatoProfiloStrId idStr = new QryUdByVlDatoProfiloStrId();
                idStr.setIdStrut(dato.getStructID());
                idStr.setTiDatoProfilo(dato.getTipoDatoProfilo());
                idStr.setIdUnitaDoc(dato.getUdID());
                QryUdByVlDatoProfiloStr entryStr = new QryUdByVlDatoProfiloStr();
                entryStr.setQryUdByVlDatoProfiloStrId(idStr);
                entryStr.setIdTipoUnitaDoc(dato.getIdTipoUnitaDoc());

                String value = (String) dato.getValue();
                if (value != null) {
                    if (value.length() > Constants.PREFIX_INDEX_LENGTH) {
                        String prefisso = value.substring(0, Constants.PREFIX_INDEX_LENGTH - 1);
                        entryStr.setDlPrefissoValore(prefisso.toUpperCase());
                    } else {
                        entryStr.setDlPrefissoValore(value.toUpperCase());
                    }
                    entryStr.setDlValore(value);
                }
                em.persist(entryStr);
                break;
            default:
                break;
            }
        }
    }

    /**
     * Restituisce i <code>MetadatoDoc</code> delle Unita Documentarie cancellate da <code>dateFrom</code> (inclusa) a
     * <code>dateTo</code> (esclusa) per la <code>structureID</code>.
     *
     * @param structID
     * @param dateFrom
     * @param dateTo
     *
     * @return
     */
    private List<DatoProfilo> getDatiProfiloAnnul(BigDecimal structID, Date dateFrom, Date dateTo,
            List<DipTipoDatoProfilo> tipiDatoProfilo) {
        String query = "select ud " + "from AroRichAnnulVer rich " + "join rich.aroStatoRichAnnulVersCor stato "
                + "join rich.aroItemRichAnnulVers item " + "join item.aroUnitaDoc ud "
                + "where rich.orgStrut.idStrut =:structureID " + "and stato.dtRegStatoRichAnnulVers >= :dateFrom "
                + "and stato.dtRegStatoRichAnnulVers < :dateTo " + "and item.tiItemRichAnnulVers = 'UNI_DOC' "
                + "and item.tiStatoItem = 'ANNULLATO' ";
        Query q = em.createQuery(query);
        q.setParameter(STRUCTURE_ID, structID.longValue());
        q.setParameter(DATE_FROM, dateFrom);
        q.setParameter(DATE_TO, dateTo);
        List<AroUnitaDoc> list = q.getResultList();
        List<DatoProfilo> res = new ArrayList<>();
        Method metodo = null;
        Map<String, Method> mappaMetodi = new HashMap<>();
        for (AroUnitaDoc uniDoc : list) {
            // Carica l'array dei metodi reflection per invocazione dinamica
            if (mappaMetodi.size() == 0) {
                String nomeMetodo = null;
                for (DipTipoDatoProfilo tipo : tipiDatoProfilo) {
                    nomeMetodo = getNomeMetodoCamelCase(tipo.getNmColonnaDb());
                    try {
                        if (tipo.getTipoConversione() != null && tipo.getTipoConversione()
                                .equals(DipTipoDatoProfilo.TipoConversione.ENTE_STRUTTURA)) {
                            metodo = null;
                        } else {
                            metodo = uniDoc.getClass().getMethod(nomeMetodo);
                        }
                        mappaMetodi.put(tipo.getNmColonnaDb(), metodo);
                    } catch (NoSuchMethodException | SecurityException ex) {
                        logger.warn("Errore nell'aggancio del metodo {} della classe AroUnitaDoc", nomeMetodo, ex);
                    }
                }
            }
            Object valore = null;
            DatoProfilo datoProfilo = null;
            for (DipTipoDatoProfilo tipo : tipiDatoProfilo) {
                try {
                    metodo = mappaMetodi.get(tipo.getNmColonnaDb());
                    /* Modifica per la EVO#26718 Gara PAB */
                    if (tipo.getTipoConversione() != null
                            && tipo.getTipoConversione().equals(DipTipoDatoProfilo.TipoConversione.ENTE_STRUTTURA)) {
                        datoProfilo = new DatoProfilo(structID, tipo.getNmTipoDatoProfilo(), uniDoc.getIdUnitaDoc(),
                                new BigDecimal(uniDoc.getOrgStrut().getIdStrut()), tipo.getTiDatoProfilo(),
                                uniDoc.getIdTipoUnitaDoc());
                    } else {
                        valore = metodo.invoke(uniDoc);
                        datoProfilo = new DatoProfilo(structID, tipo.getNmTipoDatoProfilo(), uniDoc.getIdUnitaDoc(),
                                valore, tipo.getTiDatoProfilo(), uniDoc.getIdTipoUnitaDoc());
                    }
                    res.add(datoProfilo);
                } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    logger.warn("Errore nell'invocazione del metodo {} della classe AroUnitaDoc", metodo.getName(), ex);
                }
            }
        }
        return res;
    }

    /**
     * Cancella le entry dei <code>DatoProfilo</code> dalle tabelle
     *
     * @param metadati
     *
     * @return
     */
    private int deleteDatiProfilo(List<DatoProfilo> metadati) throws Exception {
        int result = 0;

        for (DatoProfilo metadato : metadati) {
            int del = 0;

            StringBuilder query = new StringBuilder();
            query.append(DELETE_FROM);

            switch (metadato.getType()) {
            case "DATA":
                query.append("QryUdByVlDatoProfiloDt");
                break;

            case NUMERICO:
                query.append("QryUdByVlDatoProfiloNum");
                break;

            case ALFANUMERICO:
                query.append("QryUdByVlDatoProfiloStr");
                break;
            default:
                break;
            }

            query.append(
                    " d WHERE (d.id.idStrut = :structID) AND (d.id.tiDatoProfilo = :tipoDato) AND (d.id.idUnitaDoc = :udID) AND (d.idTipoUnitaDoc = :idTipoUnitaDoc)");
            Query q = em.createQuery(query.toString());
            q.setParameter("structID", metadato.getStructID());
            q.setParameter("tipoDato", metadato.getTipoDatoProfilo());
            q.setParameter("udID", metadato.getUdID());
            q.setParameter("idTipoUnitaDoc", metadato.getIdTipoUnitaDoc());
            del = q.executeUpdate();
            if (del == 1) {
                result += del;
            }
            if (del > 1) {
                throw new Exception(EXCETPION_CANCELLA_TROPPI_DATI);
            }
        }

        return result;
    }

    /**
     * Updates the entry in <code>DIP_SYNCRO_ARO</code>.
     *
     * @param structureID
     * @param date
     * @param udInserted
     *            the number of UD inserted in the transaction
     * @param udDeleted
     *            the number of UD annul in the transaction
     *
     * @throws Exception
     */
    private void updateLastElaboration(BigDecimal structureID, Date dateFrom, Date dateTo, long numUdEscluse)
            throws Exception {
        long udInserted;
        long udDeleted;
        long docInserted;
        long docDeleted;

        Query q = em.createQuery("SELECT d " + "FROM DipSyncroAro d " + "WHERE d.idStrut = :structure");
        q.setParameter("structure", structureID);
        List<DipSyncroAro> list = q.getResultList();

        if (list.size() != 1) {
            logger.error("Error in DIP_SYNCRO_ARO for idStructure = {}", structureID);
            throw new Exception("Error in DIP_SYNCRO_ARO for idStructure = " + structureID);
        }

        DipSyncroAro element = list.get(0);
        element.setDtUltimaDataElab(dateTo);

        udInserted = getNumUDInserted(structureID, dateFrom, dateTo);
        BigDecimal udSinc = BigDecimal.valueOf(element.getNiUdSincro().doubleValue() + udInserted - numUdEscluse);
        element.setNiUdSincro(udSinc);

        udDeleted = getNumUDDeleted(structureID, dateFrom, dateTo);
        BigDecimal udAnn = BigDecimal.valueOf(element.getNiUdAnnul().doubleValue() + udDeleted);
        element.setNiUdAnnul(udAnn);

        docInserted = getNumDocInserted(structureID, dateFrom, dateTo);
        BigDecimal docSinc = BigDecimal.valueOf(element.getNiDocSincro().doubleValue() + docInserted);
        element.setNiDocSincro(docSinc);

        docDeleted = getNumDocDeleted(structureID, dateFrom, dateTo);
        BigDecimal docAnn = BigDecimal.valueOf(element.getNiDocAnnul().doubleValue() + docDeleted);
        element.setNiDocAnnul(docAnn);
        em.persist(element);
    }

    /**
     * Returns the value of the UD inserted in the table.
     *
     * @param structID
     * @param dateFrom
     * @param dateTo
     *
     * @return
     */
    private long getNumUDInserted(BigDecimal structID, Date dateFrom, Date dateTo) {
        // MAC#31007 - Correzione logica di caricamento dati dei versamenti per le dimensioni di prod
        String query = "SELECT COUNT(DISTINCT ud.idUnitaDoc) " + "FROM AroUnitaDoc ud, VrsSessioneVers ses "
                + "WHERE ses.idStrut = :structureID " + "AND (ses.dtChiusura >= :dateFrom "
                + "AND ses.dtChiusura < :dateTo) " + "AND ud.orgStrut.idStrut=ses.idStrut "
                + "AND ses.idUnitaDoc=ud.idUnitaDoc "
                // MAC#31066 - correzione estrazione dati UD in caso di aggiunta documenti
                + "AND ses.tiSessioneVers='VERSAMENTO' " + "AND ses.tiStatoSessioneVers='CHIUSA_OK' ";
        Query q = em.createQuery(query);
        q.setParameter(STRUCTURE_ID, structID.longValue());
        q.setParameter(DATE_FROM, dateFrom);
        q.setParameter(DATE_TO, dateTo);
        List<Long> list = q.getResultList();
        return list.get(0);
    }

    /**
     * Returns the value of the UD deleted in the table.
     *
     * @param structID
     * @param dateFrom
     * @param dateTo
     *
     * @return
     */
    private long getNumUDDeleted(BigDecimal structID, Date dateFrom, Date dateTo) {

        String query = "select count(distinct ud.idUnitaDoc) " + "from AroRichAnnulVer rich "
                + "join rich.aroStatoRichAnnulVersCor  stato " + "join rich.aroItemRichAnnulVers item "
                + "join item.aroUnitaDoc ud " + "where rich.orgStrut.idStrut = :structureID "
                + "and stato.tiStatoRichAnnulVers = 'EVASA' " + "and stato.dtRegStatoRichAnnulVers >= :dateFrom "
                + "and stato.dtRegStatoRichAnnulVers < :dateTo " + "and item.tiItemRichAnnulVers = 'UNI_DOC' "
                + "and item.tiStatoItem = 'ANNULLATO' ";

        Query q = em.createQuery(query);
        q.setParameter(STRUCTURE_ID, structID.longValue());
        q.setParameter(DATE_FROM, dateFrom);
        q.setParameter(DATE_TO, dateTo);
        List<Long> list = q.getResultList();
        return list.get(0);
    }

    /**
     * Returns the value of the Doc inserted in the table.
     *
     * @param structID
     * @param dateFrom
     * @param dateTo
     *
     * @return
     */
    private long getNumDocInserted(BigDecimal structID, Date dateFrom, Date dateTo) {
        // MAC#31007 - Correzione logica di caricamento dati dei versamenti per le dimensioni di prod
        String query = "SELECT COUNT(DISTINCT doc.idDoc) " + "FROM AroUnitaDoc ud, VrsSessioneVers ses "
                + "JOIN ud.aroUsoXsdDatiSpecs da " + "JOIN da.aroValoreAttribDatiSpecs vad "
                + "JOIN vad.decAttribDatiSpec ads " + "JOIN da.aroDoc doc " + "WHERE (ses.idStrut = :structureID) "
                + "AND (ses.dtChiusura >= :dateFrom " + "AND ses.dtChiusura < :dateTo) "
                + "AND (da.tiEntitaSacer = 'DOC') " + "AND (ads.tiAttribDatiSpec IS NOT NULL) "
                + "AND (vad.dlValore IS NOT NULL) " + "AND ud.orgStrut.idStrut=ses.idStrut "
                + "AND ses.idUnitaDoc=ud.idUnitaDoc " + "AND ses.tiSessioneVers='VERSAMENTO' "
                + "AND ses.tiStatoSessioneVers='CHIUSA_OK' ";

        Query q = em.createQuery(query);
        q.setParameter(STRUCTURE_ID, structID.longValue());
        q.setParameter(DATE_FROM, dateFrom);
        q.setParameter(DATE_TO, dateTo);
        List<Long> list = q.getResultList();

        return list.get(0);
    }

    /**
     * Returns the value of the Doc deleted in the table.
     *
     * @param structID
     * @param dateFrom
     * @param dateTo
     *
     * @return
     */
    private long getNumDocDeleted(BigDecimal structID, Date dateFrom, Date dateTo) {

        String query = "select count(distinct doc.idDoc) " + "from AroRichAnnulVer rich "
                + "join rich.aroStatoRichAnnulVersCor  stato " + "join rich.aroItemRichAnnulVers item "
                + "join item.aroUnitaDoc ud " + "join ud.aroUsoXsdDatiSpecs da "
                + "join da.aroValoreAttribDatiSpecs vad " + "join vad.decAttribDatiSpec ads " + "join da.aroDoc doc "
                + "where rich.orgStrut.idStrut = :structureID " + "and stato.tiStatoRichAnnulVers = 'EVASA' "
                + "and stato.dtRegStatoRichAnnulVers >= :dateFrom and stato.dtRegStatoRichAnnulVers < :dateTo "
                + "and item.tiItemRichAnnulVers = 'UNI_DOC' " + "and item.tiStatoItem = 'ANNULLATO' ";

        Query q = em.createQuery(query);
        q.setParameter(STRUCTURE_ID, structID.longValue());
        q.setParameter(DATE_FROM, dateFrom);
        q.setParameter(DATE_TO, dateTo);
        List<Long> list = q.getResultList();
        return list.get(0);
    }

    /*
     * Dato un nome di colonna in formato ORACLE (database es.: CAMPO_PROVA) torna getCampoProva come nome di metodo.
     */
    private String getNomeMetodoCamelCase(String nomeColonna) {
        StringBuilder buffer = new StringBuilder();
        String[] parole = nomeColonna.split("_");
        if (parole != null) {
            buffer.append("get");
            for (String parola : parole) {
                // Trasforma in camelCase
                buffer.append(Character.toUpperCase(parola.charAt(0)));
                buffer.append(parola.toLowerCase().substring(1));
            }
        }
        return buffer.toString();
    }
}
