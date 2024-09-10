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

package it.eng.dispenser.web.bo;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.eng.dispenser.entity.AroDoc;
import it.eng.dispenser.entity.DipCampoRicerca;
import it.eng.dispenser.entity.DipCombinazioneRicerca;
import it.eng.dispenser.entity.DipFiltroRicerca;
import it.eng.dispenser.entity.DipQueryWith;
import it.eng.dispenser.entity.DipValorePredefinito;
import it.eng.dispenser.entity.constraint.ConstDipCampoRicerca.TiDatoCampo;
import it.eng.dispenser.entity.constraint.ConstDipFiltroRicerca.TiDatoFiltro;
import it.eng.dispenser.entity.constraint.ConstDipFiltroRicerca.TiTipoFiltro;
import it.eng.dispenser.slite.gen.viewbean.AroVLisCompDocRowBean;
import it.eng.dispenser.slite.gen.viewbean.AroVLisCompDocTableBean;
import it.eng.dispenser.slite.gen.viewbean.AroVLisDocRowBean;
import it.eng.dispenser.slite.gen.viewbean.AroVLisDocTableBean;
import it.eng.dispenser.slite.gen.viewbean.AroVLisLinkUnitaDocRowBean;
import it.eng.dispenser.slite.gen.viewbean.AroVLisLinkUnitaDocTableBean;
import it.eng.dispenser.util.Transform;
import it.eng.dispenser.viewEntity.AroVLisCompDoc;
import it.eng.dispenser.viewEntity.AroVLisLinkUnitaDoc;
import it.eng.dispenser.web.form.DynamicSpagoLiteForm.FormRicerca;
import it.eng.dispenser.web.form.RicercheLoader;
import it.eng.dispenser.web.form.RisultatoBean;
import it.eng.dispenser.web.util.QueryBuilder;
import it.eng.spagoCore.error.EMFError;
import it.eng.spagoCore.util.JpaUtils;
import it.eng.spagoLite.db.base.row.BaseRow;
import it.eng.spagoLite.db.base.table.BaseTable;

@Service
@Transactional
public class RicercaBO {

    public static final int RICERCA_MAX_RESULTS = 1000;

    private static final String QRY_DATI_OUTPUT_1 = "" + "select * "
            + "from ( SELECT ARO_UNITA_DOC.ID_STRUT, ARO_USO_XSD_DATI_SPEC.ID_UNITA_DOC, DEC_ATTRIB_DATI_SPEC.NM_ATTRIB_DATI_SPEC as nm_attrib_dati_spec, ARO_VALORE_ATTRIB_DATI_SPEC.DL_VALORE as dl_valore ";
    private static final String QRY_DATI_OUTPUT_2_1 = " " + "from ";
    private static final String QRY_DATI_OUTPUT_2_2 = " " + " ARO_UNITA_DOC "
            + "join SACER.ARO_DOC ARO_DOC on (ARO_UNITA_DOC.ID_UNITA_DOC = ARO_DOC.ID_UNITA_DOC and ARO_DOC.TI_DOC ='PRINCIPALE' ) "
            + "join SACER.ARO_USO_XSD_DATI_SPEC ARO_USO_XSD_DATI_SPEC on (ARO_UNITA_DOC.ID_UNITA_DOC = ARO_USO_XSD_DATI_SPEC.ID_UNITA_DOC) "
            + "join SACER.ARO_VALORE_ATTRIB_DATI_SPEC ARO_VALORE_ATTRIB_DATI_SPEC on (ARO_USO_XSD_DATI_SPEC.ID_USO_XSD_DATI_SPEC = ARO_VALORE_ATTRIB_DATI_SPEC.ID_USO_XSD_DATI_SPEC) "
            + "join SACER.DEC_ATTRIB_DATI_SPEC DEC_ATTRIB_DATI_SPEC on (ARO_VALORE_ATTRIB_DATI_SPEC.ID_ATTRIB_DATI_SPEC = DEC_ATTRIB_DATI_SPEC.ID_ATTRIB_DATI_SPEC) ";
    private static final String QRY_DATI_OUTPUT_4 = " ) PIVOT ( MAX(dl_valore) FOR nm_attrib_dati_spec IN (";
    private static final String QRY_DATI_OUTPUT_5 = " ) ) ";

    // Porzione aggiunta per tradurre gli id_attrib_dati_Spec virtuali in reali
    private static final String QRY_META_UD_PORZIONE_PER_ATTRIB_DATI_SPEC = " IN (SELECT ds0.ID_ATTRIB_DATI_SPEC FROM SACER.DEC_ATTRIB_DATI_SPEC ds0 WHERE ds0.ID_ATTRIB_DATI_SPEC IN (SELECT ds1.ID_ATTRIB_DATI_SPEC FROM SACER.DEC_ATTRIB_DATI_SPEC ds1 WHERE ds1.id_STRUT IN (SELECT id_strut_reale FROM strut_referenziate_pug WHERE id_strut_fittizia = ?) AND ds1.TI_ENTITA_SACER = 'UNI_DOC' AND  ds1.TI_USO_ATTRIB = 'VERS' AND ds1.NM_ATTRIB_DATI_SPEC = (SELECT ds2.NM_ATTRIB_DATI_SPEC FROM SACER.DEC_ATTRIB_DATI_SPEC ds2 WHERE ds2.ID_ATTRIB_DATI_SPEC = ?)))";
    private static final String QRY_META_UD_WITH_STR_PARAM = "SELECT id_unita_doc FROM QRY_UD_BY_VL_META_STR WHERE dl_prefisso_valore = ? AND id_attrib_dati_spec "
            + QRY_META_UD_PORZIONE_PER_ATTRIB_DATI_SPEC;
    private static final String QRY_META_UD_WITH_STR_LIKE_PARAM = "SELECT id_unita_doc FROM QRY_UD_BY_VL_META_STR WHERE dl_prefisso_valore LIKE ? AND id_attrib_dati_spec "
            + QRY_META_UD_PORZIONE_PER_ATTRIB_DATI_SPEC;
    private static final String QRY_META_UD_WITH_NUM_PARAM = "SELECT id_unita_doc FROM QRY_UD_BY_VL_META_NUM WHERE dl_valore = ? AND id_attrib_dati_spec "
            + QRY_META_UD_PORZIONE_PER_ATTRIB_DATI_SPEC;
    private static final String QRY_META_UD_WITH_DATE_FROM_PARAM = "SELECT id_unita_doc FROM QRY_UD_BY_VL_META_DT WHERE dl_valore >= ? AND id_attrib_dati_spec "
            + QRY_META_UD_PORZIONE_PER_ATTRIB_DATI_SPEC;
    private static final String QRY_META_UD_WITH_DATE_TO_PARAM = "SELECT id_unita_doc FROM QRY_UD_BY_VL_META_DT WHERE dl_valore <= ? AND id_attrib_dati_spec "
            + QRY_META_UD_PORZIONE_PER_ATTRIB_DATI_SPEC;

    private static final String QRY_META_DOC_PORZIONE_PER_ATTRIB_DATI_SPEC = " IN (SELECT ds0.ID_ATTRIB_DATI_SPEC FROM SACER.DEC_ATTRIB_DATI_SPEC ds0 WHERE ds0.ID_ATTRIB_DATI_SPEC IN (SELECT ds1.ID_ATTRIB_DATI_SPEC FROM SACER.DEC_ATTRIB_DATI_SPEC ds1 WHERE ds1.id_STRUT IN (SELECT id_strut_reale FROM strut_referenziate_pug WHERE id_strut_fittizia = ?) AND ds1.TI_ENTITA_SACER = 'DOC' AND  ds1.TI_USO_ATTRIB = 'VERS' AND ds1.NM_ATTRIB_DATI_SPEC = (SELECT ds2.NM_ATTRIB_DATI_SPEC FROM SACER.DEC_ATTRIB_DATI_SPEC ds2 WHERE ds2.ID_ATTRIB_DATI_SPEC = ?)))";
    private static final String QRY_META_DOC_WITH_STR_PARAM = "SELECT id_unita_doc FROM QRY_DOC_BY_VL_META_STR WHERE dl_prefisso_valore = ? AND id_attrib_dati_spec "
            + QRY_META_DOC_PORZIONE_PER_ATTRIB_DATI_SPEC;
    private static final String QRY_META_DOC_WITH_STR_LIKE_PARAM = "SELECT id_unita_doc FROM QRY_DOC_BY_VL_META_STR WHERE dl_prefisso_valore LIKE ? AND id_attrib_dati_spec "
            + QRY_META_DOC_PORZIONE_PER_ATTRIB_DATI_SPEC;
    private static final String QRY_META_DOC_WITH_NUM_PARAM = "SELECT id_unita_doc FROM QRY_DOC_BY_VL_META_NUM WHERE dl_valore = ? AND id_attrib_dati_spec "
            + QRY_META_DOC_PORZIONE_PER_ATTRIB_DATI_SPEC;
    private static final String QRY_META_DOC_WITH_DATE_FROM_PARAM = "SELECT id_unita_doc FROM QRY_DOC_BY_VL_META_DT WHERE dl_valore >= ? AND id_attrib_dati_spec "
            + QRY_META_DOC_PORZIONE_PER_ATTRIB_DATI_SPEC;
    private static final String QRY_META_DOC_WITH_DATE_TO_PARAM = "SELECT id_unita_doc FROM QRY_DOC_BY_VL_META_DT WHERE dl_valore <= ? AND id_attrib_dati_spec "
            + QRY_META_DOC_PORZIONE_PER_ATTRIB_DATI_SPEC;

    private static final String QRY_DATO_PROF_WITH_STR_PARAM = "SELECT id_unita_doc FROM QRY_UD_BY_VL_DATO_PROFILO_STR WHERE dl_prefisso_valore = ? AND id_strut IN(SELECT id_strut_reale FROM STRUT_REFERENZIATE_PUG WHERE id_strut_fittizia = ?) and ti_dato_profilo = ? and id_tipo_unita_doc IN (SELECT TUD.id_tipo_unita_doc FROM SACER.DEC_TIPO_UNITA_DOC TUD WHERE TUD.NM_TIPO_UNITA_DOC = (SELECT TUD2.NM_TIPO_UNITA_DOC FROM SACER.DEC_TIPO_UNITA_DOC TUD2 WHERE TUD2.ID_TIPO_UNITA_DOC = ?))";
    private static final String QRY_DATO_PROF_WITH_STR_LIKE_PARAM = "SELECT id_unita_doc FROM QRY_UD_BY_VL_DATO_PROFILO_STR WHERE dl_prefisso_valore LIKE ? AND id_strut IN(SELECT id_strut_reale FROM STRUT_REFERENZIATE_PUG WHERE id_strut_fittizia = ?) and ti_dato_profilo = ? and id_tipo_unita_doc IN (SELECT TUD.id_tipo_unita_doc FROM SACER.DEC_TIPO_UNITA_DOC TUD WHERE TUD.NM_TIPO_UNITA_DOC = (SELECT TUD2.NM_TIPO_UNITA_DOC FROM SACER.DEC_TIPO_UNITA_DOC TUD2 WHERE TUD2.ID_TIPO_UNITA_DOC = ?))";
    private static final String QRY_DATO_PROF_WITH_NUM_PARAM = "SELECT id_unita_doc FROM QRY_UD_BY_VL_DATO_PROFILO_NUM WHERE dl_valore = ? AND id_strut IN(SELECT id_strut_reale FROM STRUT_REFERENZIATE_PUG WHERE id_strut_fittizia = ?) and ti_dato_profilo = ? and id_tipo_unita_doc IN (SELECT TUD.id_tipo_unita_doc FROM SACER.DEC_TIPO_UNITA_DOC TUD WHERE TUD.NM_TIPO_UNITA_DOC = (SELECT TUD2.NM_TIPO_UNITA_DOC FROM SACER.DEC_TIPO_UNITA_DOC TUD2 WHERE TUD2.ID_TIPO_UNITA_DOC = ?))";
    private static final String QRY_DATO_PROF_WITH_DATE_FROM_PARAM = "SELECT id_unita_doc FROM QRY_UD_BY_VL_DATO_PROFILO_DT WHERE dl_valore >= ? AND id_strut IN(SELECT id_strut_reale FROM STRUT_REFERENZIATE_PUG WHERE id_strut_fittizia = ?) and ti_dato_profilo = ? and id_tipo_unita_doc IN (SELECT TUD.id_tipo_unita_doc FROM SACER.DEC_TIPO_UNITA_DOC TUD WHERE TUD.NM_TIPO_UNITA_DOC = (SELECT TUD2.NM_TIPO_UNITA_DOC FROM SACER.DEC_TIPO_UNITA_DOC TUD2 WHERE TUD2.ID_TIPO_UNITA_DOC = ?))";
    private static final String QRY_DATO_PROF_WITH_DATE_TO_PARAM = "SELECT id_unita_doc FROM QRY_UD_BY_VL_DATO_PROFILO_DT WHERE dl_valore <= ? AND id_strut IN(SELECT id_strut_reale FROM STRUT_REFERENZIATE_PUG WHERE id_strut_fittizia = ?) and ti_dato_profilo = ? and id_tipo_unita_doc IN (SELECT TUD.id_tipo_unita_doc FROM SACER.DEC_TIPO_UNITA_DOC TUD WHERE TUD.NM_TIPO_UNITA_DOC = (SELECT TUD2.NM_TIPO_UNITA_DOC FROM SACER.DEC_TIPO_UNITA_DOC TUD2 WHERE TUD2.ID_TIPO_UNITA_DOC = ?))";

    // Clausola WITH...
    private static final String WITH_CLAUSE_TABLE_NAME = "TMP_ARO_UNITA_DOC_";
    private static final String WITH_CLAUSE_OUTPUT_1 = "WITH ";
    private static final String WITH_CLAUSE_OUTPUT_IN_OR_2 = ") OR ID_UNITA_DOC IN (";
    private static final String WITH_CLAUSE_OUTPUT_IN_OR_3 = " )) and ID_STRUT IN(SELECT id_strut_reale FROM strut_referenziate_pug WHERE id_strut_fittizia=%d)";

    @PersistenceContext
    private EntityManager em;
    @Autowired
    private RicercheLoader ricLoader;

    private Logger log = LoggerFactory.getLogger(RicercaBO.class);

    public BaseTable eseguiRicerca(FormRicerca form, String nmRicerca) throws EMFError, SQLException {
        Set<BigDecimal> uds = getListaUd(form, nmRicerca);
        BaseTable table = new BaseTable();
        if (!uds.isEmpty()) {
            RisultatoBean rb = ricLoader.getListaRisultati(nmRicerca);
            addResultRows(rb, uds, nmRicerca, table);
        }
        return table;
    }

    public BaseTable eseguiRicercaPerUds(String nmRicerca, Set<BigDecimal> uds) throws SQLException {
        BaseTable table = new BaseTable();
        if (!uds.isEmpty()) {
            RisultatoBean rb = ricLoader.getListaRisultati(nmRicerca);
            addResultRows(rb, uds, nmRicerca, table);
        }
        return table;
    }

    @SuppressWarnings("unchecked")
    public Set<BigDecimal> getListaUd(FormRicerca form, String nmRicerca) throws EMFError {
        Query q1 = em.createNamedQuery("DipCombinazioneRicerca.findAllByNameRic");
        int maxres = ricLoader.getMaxResultRicerca(nmRicerca);
        q1.setParameter("nmRicerca", nmRicerca);
        List<DipCombinazioneRicerca> combs = q1.getResultList();
        Set<BigDecimal> res = new HashSet<>();
        for (DipCombinazioneRicerca comb : combs) {
            QueryBuilder qb = new QueryBuilder();
            for (DipFiltroRicerca filtro : comb.getDipFiltroRicercas()) {
                generateQueryListaUd(filtro, qb, form);
            }
            /*
             * se non si fa il test sulla presenza della query potrebbe andare in null pointer exception
             */
            if (!qb.returnQuery().isEmpty()) {
                Query q2 = em.createNativeQuery(qb.returnQuery());
                q2.setMaxResults(maxres);
                qb.setQueryParams(q2);
                res.addAll(q2.getResultList());
            }
        }
        return res;
    }

    @SuppressWarnings("unchecked")
    public BaseRow caricaDettaglioUd(BigDecimal idUnitaDoc, String nmRicerca, Long idStrut) throws SQLException {
        RisultatoBean rb = ricLoader.getDettaglioRisultato(nmRicerca);
        StringBuilder qrSelect = new StringBuilder();
        StringBuilder qrWith = new StringBuilder();

        Query qStrut = em
                .createNativeQuery("SELECT ID_STRUT_FITTIZIA FROM strut_referenziate_pug WHERE ID_STRUT_REALE = ?");
        qStrut.setParameter(1, idStrut);
        List<BigDecimal> lis = qStrut.getResultList();
        long idStrutVirtuale = lis.get(0).longValueExact();

        int i = 0;
        int contatore = 1;

        // WITH
        qrWith.append(WITH_CLAUSE_OUTPUT_1);
        qrWith.append(WITH_CLAUSE_TABLE_NAME);
        qrWith.append(contatore);
        qrWith.append(" AS (SELECT /*+ MATERIALIZE */ * FROM SACER.ARO_UNITA_DOC WHERE (ID_UNITA_DOC IN (");
        qrWith.append(idUnitaDoc);
        qrWith.append(String.format(WITH_CLAUSE_OUTPUT_IN_OR_3, idStrutVirtuale));
        qrWith.append(")");
        // FINE WITH PRINCIPALE

        // WITH - CLAUSE PER DATI CALCOLATI
        Map<Long, DipQueryWith> mapWith = ricLoader.getClausoleWithPerDettaglio(nmRicerca);
        Set<Long> setWith = mapWith.keySet();
        Iterator<Long> itWith = setWith.iterator();
        while (itWith.hasNext()) {
            Long chiaveWith = itWith.next();
            DipQueryWith dipQueryWith = mapWith.get(chiaveWith);
            // INSERIRE LA CLAUSOLA WITH NELLO STATEMENT !!!!
            qrWith.append(", ");
            /*
             * prende il nome della query che diventa il nome della clausolaWITH con aggiunta del riferimento numerico
             * alla struttura.
             */
            qrWith.append(dipQueryWith.getNmQueryWith());
            qrWith.append("_");
            qrWith.append(contatore);
            qrWith.append(" AS ");
            /*
             * Estrae l'SQL della clausola WITH e sostituisce il riferimento alla tabella &TMP_ARO_UNITA_DOC con il nome
             * TMP_ARO_UNITA_DOC seguita dal progressivo della struttura.
             */
            qrWith.append(dipQueryWith.getDlQueryWith().replaceFirst("&TMP_ARO_UNITA_DOC",
                    WITH_CLAUSE_TABLE_NAME + contatore));
        }
        // Fine WITH - CLAUSE PER DATI CALCOLATI

        qrSelect.append(QRY_DATI_OUTPUT_1);
        List<String> dati = rb.getDatiProfiloByIdStrut(idStrutVirtuale);
        if (dati != null) {
            for (String datoProfilo : dati) {
                qrSelect.append(",");
                qrSelect.append(datoProfilo);
            }
        }
        List<String> datiWith = rb.getQueryCalcoloByIdStrut(idStrutVirtuale);
        if (datiWith != null) {
            for (String queryCalc : datiWith) {
                qrSelect.append(",");
                /*
                 * per la porzione di sql estratta sostituisce il campo con "&" con il riferimento all'apposita
                 * clausiola di WITH dichiarata in testa alla query
                 */
                qrSelect.append(sostituisciCampoChiave(queryCalc, contatore));

            }
        }
        qrSelect.append(QRY_DATI_OUTPUT_2_1);
        qrSelect.append(WITH_CLAUSE_TABLE_NAME);
        qrSelect.append(contatore);
        qrSelect.append(QRY_DATI_OUTPUT_2_2);
        qrSelect.append(QRY_DATI_OUTPUT_4);
        i = 0;
        dati = rb.getMetadatiByIdStrut(idStrutVirtuale);
        if (dati != null) {
            for (String metadato : dati) {
                if (i > 0) {
                    qrSelect.append(",");
                }
                qrSelect.append(metadato);
                i++;
            }
        }
        qrSelect.append(QRY_DATI_OUTPUT_5);

        String queryFinale = qrWith.toString() + " " + qrSelect.toString();
        log.debug(queryFinale);
        try (Connection cnn = JpaUtils.provideConnectionFrom(em); Statement st = cnn.createStatement();) {
            ResultSet res = st.executeQuery(queryFinale);
            BaseRow row = new BaseRow();
            while (res.next()) {
                row.loadFromResultSet(res);
            }
            return row;
        }
    }

    private void addResultRows(RisultatoBean rb, Set<BigDecimal> listaIdUd, String nomeRicerca, BaseTable table)
            throws SQLException {
        if (listaIdUd == null || listaIdUd.isEmpty()) {
            throw new IllegalArgumentException("Lista di ud nulla o vuota");
        }

        QueryBuilder qbSelect = new QueryBuilder();
        StringBuilder qbWith = new StringBuilder();

        // WITH
        int contatore = 0;
        // Definisce la prima clausola WITH
        qbWith.append(WITH_CLAUSE_OUTPUT_1);
        for (Long idStrut : rb.getListaStrutture()) {
            int i = 0;
            contatore++;

            // WITH PRINCIPALE
            if (contatore > 1) {
                qbWith.append(", ");
            }
            /*
             * Qui inizia a dichiarare la with principale che fa la select delle Unita' documentali per ID, dandole come
             * suffisso il numero struttura come _1, _2 ,_3 ecc.
             */
            qbWith.append(WITH_CLAUSE_TABLE_NAME);
            qbWith.append(contatore);
            qbWith.append(" AS (SELECT /*+ MATERIALIZE */ * FROM SACER.ARO_UNITA_DOC WHERE (ID_UNITA_DOC IN (");
            Iterator<BigDecimal> iterator = listaIdUd.iterator();
            while (iterator.hasNext()) {
                BigDecimal ud = iterator.next();
                if (i > 0) {
                    qbWith.append(",");
                }
                qbWith.append(ud);
                i++;
                if (i == RICERCA_MAX_RESULTS && iterator.hasNext()) {
                    qbWith.append(WITH_CLAUSE_OUTPUT_IN_OR_2);
                    i = 0;
                }
            }
            qbWith.append(String.format(WITH_CLAUSE_OUTPUT_IN_OR_3, idStrut));
            qbWith.append(")");
            // FINE WITH PRINCIPALE
            /*
             * Ha generato la with principale della struttura gestendo eventualmente la clausola OR per spezzare la IN()
             * per le limitazioni di Oracle
             */

            // WITH - CLAUSE PER DATI CALCOLATI
            Map<Long, DipQueryWith> mapWith = ricLoader.getClausoleWithPerLista(nomeRicerca);
            Set<Long> setWith = mapWith.keySet();
            Iterator<Long> itWith = setWith.iterator();
            while (itWith.hasNext()) {
                Long chiaveWith = itWith.next();
                DipQueryWith dipQueryWith = mapWith.get(chiaveWith);
                // INSERIRE LA CLAUSOLA WITH NELLO STATEMENT !!!!
                qbWith.append(", ");
                /*
                 * prende il nome della query che diventa il nome della clausolaWITH con aggiunta del riferimento
                 * numerico alla struttura.
                 */
                qbWith.append(dipQueryWith.getNmQueryWith());
                qbWith.append("_");
                qbWith.append(contatore);
                qbWith.append(" AS ");
                /*
                 * Estrae l'SQL della clausola WITH e sostituisce il riferimento alla tabella &TMP_ARO_UNITA_DOC con il
                 * nome TMP_ARO_UNITA_DOC seguita dal progressivo della struttura.
                 */
                qbWith.append(dipQueryWith.getDlQueryWith().replaceFirst("&TMP_ARO_UNITA_DOC",
                        WITH_CLAUSE_TABLE_NAME + contatore));
            }

            StringBuilder query = new StringBuilder();
            query.append(QRY_DATI_OUTPUT_1);
            List<String> dati = rb.getDatiProfiloByIdStrut(idStrut);
            if (dati != null) {
                for (String datoProfilo : dati) {
                    query.append(",");
                    query.append(datoProfilo);
                }
            }
            List<String> datiWith = rb.getQueryCalcoloByIdStrut(idStrut);
            if (datiWith != null) {
                for (String queryCalc : datiWith) {
                    query.append(",");
                    /*
                     * per la porzione di sql estratta sostituisce il campo con "&" con il riferimento all'apposita
                     * clausiola di WITH dichiarata in testa alla query
                     */
                    query.append(sostituisciCampoChiave(queryCalc, contatore));

                }
            }
            query.append(QRY_DATI_OUTPUT_2_1);
            query.append(WITH_CLAUSE_TABLE_NAME);
            query.append(contatore);
            query.append(QRY_DATI_OUTPUT_2_2);
            query.append(QRY_DATI_OUTPUT_4);
            i = 0;
            dati = rb.getMetadatiByIdStrut(idStrut);
            if (dati != null) {
                for (String metadato : dati) {
                    if (i > 0) {
                        query.append(",");
                    }
                    query.append(metadato);
                    i++;
                }
            }
            query.append(QRY_DATI_OUTPUT_5);
            qbSelect.addQueryForUnion(query.toString());
        }

        if (!rb.getOrderByList().isEmpty()) {
            StringBuilder orderByList = new StringBuilder();
            int i = 0;
            for (String orderbyclause : rb.getOrderByList()) {
                if (i > 0) {
                    orderByList.append(",");
                }
                orderByList.append(orderbyclause);
                i++;
            }
            qbSelect.addOrderByClause(orderByList.toString());
        }
        qbSelect.addLimitClauseOra12c(rb.getMaxResults());
        String queryFinale = qbWith.toString() + " " + qbSelect.returnQuery();
        log.debug(queryFinale);
        try (Connection cnn = JpaUtils.provideConnectionFrom(em); Statement st = cnn.createStatement();
                ResultSet resultSet = st.executeQuery(queryFinale);) {
            while (resultSet.next()) {
                BaseRow row = table.createRow();
                row.loadFromResultSet(resultSet);
                table.add(row);
            }
        }

    }

    private void generateQueryListaUd(DipFiltroRicerca filtro, QueryBuilder qb, FormRicerca form) throws EMFError {
        if (filtro.getFlStatico().equals("1")) {
            for (DipValorePredefinito predefVal : filtro.getDipValorePredefinitos()) {
                addFilterPredefinitoToQuery(filtro, predefVal.getDlValore(), qb);
            }
        } else {
            DipCampoRicerca campo = filtro.getDipCampoRicerca();
            String nomeCampo = campo.getNmCampo();
            Object val = null;
            switch (TiDatoCampo.valueOf(campo.getTiDatoCampo())) {
            case CHECK:
                val = form.getCheckBox(nomeCampo).parse();
                break;
            case COMBO:
                val = form.getComboBox(nomeCampo).parse();
                break;
            case COMBOAUTOCOMPILA:
                val = form.getComboBox(nomeCampo).parse();
                break;
            case DATA:
                val = form.getDate(nomeCampo).parse();
                break;
            case NUMERO:
                val = form.getBigDecimal(nomeCampo).parse();
                break;
            case STRINGA:
                val = form.getString(nomeCampo).parse();
                break;
            default:
                break;
            }
            if (val != null) {
                addFilterToQuery(filtro, val, qb);
            }
        }
    }

    /**
     * Il filtro predefinito ha sempre come valore un tipo stringa che sarà da parsare opportunamente
     *
     * @param filtro
     *            oggetto filtro di tipo {@link DipFiltroRicerca}
     * @param valoreFiltro
     *            valore
     * @param queryParziale
     *            query builder {@link QueryBuilder}
     */
    private void addFilterPredefinitoToQuery(DipFiltroRicerca filtro, String valoreFiltro, QueryBuilder queryParziale) {
        switch (TiDatoFiltro.valueOf(filtro.getTiDatoFiltro())) {
        case DATA_A:
            getQueryDataA(filtro, Timestamp.valueOf(valoreFiltro), queryParziale);
            break;
        case DATA_DA:
            getQueryDataDa(filtro, Timestamp.valueOf(valoreFiltro), queryParziale);
            break;
        case NUMERO:
            getQueryNumero(filtro, new BigDecimal(valoreFiltro), queryParziale);
            break;
        case STRINGA:
            getQueryStringa(filtro, valoreFiltro, queryParziale);
            break;

        }
    }

    /**
     * Il fitro genereico ha un objec da castare al tipo opportuno
     *
     * @param filtro
     *            oggetto di tipo filtro {@link DipFiltroRicerca}
     * @param valoreFiltro
     *            valore
     * @param queryParziale
     *            query builder {@link QueryBuilder}
     */
    private void addFilterToQuery(DipFiltroRicerca filtro, Object valoreFiltro, QueryBuilder queryParziale) {
        switch (TiDatoFiltro.valueOf(filtro.getTiDatoFiltro())) {
        case DATA_A:
            // Genera una DATA_A impostata alla fine esatta del giorno stesso
            getQueryDataA(filtro, getFinalTimestampFromRangeDate((java.sql.Timestamp) valoreFiltro), queryParziale);
            break;
        case DATA_DA:
            getQueryDataDa(filtro, (Timestamp) valoreFiltro, queryParziale);
            break;
        case NUMERO:
            getQueryNumero(filtro, new BigDecimal(valoreFiltro.toString()), queryParziale);
            break;
        case STRINGA:
            getQueryStringa(filtro, (String) valoreFiltro, queryParziale);
            break;

        }
    }

    private void getQueryStringa(DipFiltroRicerca filtro, String valoreFiltro, QueryBuilder queryParziale) {
        boolean isLikeQuery = valoreFiltro.endsWith("%");
        // taglio il valore ai primi N caratteri... nel caso di troncamento è
        // implicito il LIKE ..
        valoreFiltro = (valoreFiltro.length() > it.eng.dispenser.util.Constants.PREFIX_INDEX_LENGTH)
                ? valoreFiltro.substring(0, it.eng.dispenser.util.Constants.PREFIX_INDEX_LENGTH) : valoreFiltro;
        if (filtro.getTiTipoFiltro().equals(TiTipoFiltro.METADATO_DOC.name())) {
            if (isLikeQuery) {
                queryParziale.addQueryForIntersect(QRY_META_DOC_WITH_STR_LIKE_PARAM, valoreFiltro,
                        filtro.getDipCombinazioneRicerca().getIdStrut(), filtro.getIdAttribDatiSpec());
            } else {
                queryParziale.addQueryForIntersect(QRY_META_DOC_WITH_STR_PARAM, valoreFiltro,
                        filtro.getDipCombinazioneRicerca().getIdStrut(), filtro.getIdAttribDatiSpec());

            }
        } else if (filtro.getTiTipoFiltro().equals(TiTipoFiltro.METADATO_UD.name())) {
            if (isLikeQuery) {
                queryParziale.addQueryForIntersect(QRY_META_UD_WITH_STR_LIKE_PARAM, valoreFiltro,
                        filtro.getDipCombinazioneRicerca().getIdStrut(), filtro.getIdAttribDatiSpec());
            } else {
                queryParziale.addQueryForIntersect(QRY_META_UD_WITH_STR_PARAM, valoreFiltro,
                        filtro.getDipCombinazioneRicerca().getIdStrut(), filtro.getIdAttribDatiSpec());

            }
        } else {
            // Aggiunta la query anche per tipo unita doc che non c'era
            if (isLikeQuery) {
                queryParziale.addQueryForIntersect(QRY_DATO_PROF_WITH_STR_LIKE_PARAM, valoreFiltro,
                        // filtro.getDipCombinazioneRicerca().getIdStrut(), filtro.getTiTipoFiltro(),
                        filtro.getDipCombinazioneRicerca().getIdStrut(), filtro.getNmFiltroRicerca(),
                        filtro.getDipCombinazioneRicerca().getIdTipoUnitaDoc());
            } else {
                queryParziale.addQueryForIntersect(QRY_DATO_PROF_WITH_STR_PARAM, valoreFiltro,
                        // filtro.getDipCombinazioneRicerca().getIdStrut(), filtro.getTiTipoFiltro(),
                        filtro.getDipCombinazioneRicerca().getIdStrut(), filtro.getNmFiltroRicerca(),
                        filtro.getDipCombinazioneRicerca().getIdTipoUnitaDoc());
            }
        }
    }

    private void getQueryNumero(DipFiltroRicerca filtro, BigDecimal valoreFiltro, QueryBuilder queryParziale) {
        if (filtro.getTiTipoFiltro().equals(TiTipoFiltro.METADATO_DOC.name())) {
            queryParziale.addQueryForIntersect(QRY_META_DOC_WITH_NUM_PARAM, valoreFiltro,
                    filtro.getDipCombinazioneRicerca().getIdStrut(), filtro.getIdAttribDatiSpec());
        } else if (filtro.getTiTipoFiltro().equals(TiTipoFiltro.METADATO_UD.name())) {
            queryParziale.addQueryForIntersect(QRY_META_UD_WITH_NUM_PARAM, valoreFiltro,
                    filtro.getDipCombinazioneRicerca().getIdStrut(), filtro.getIdAttribDatiSpec());
        } else {
            // Inserito il parametro per la query per tipoUnitaDoc che mancava
            queryParziale.addQueryForIntersect(QRY_DATO_PROF_WITH_NUM_PARAM, valoreFiltro,
                    filtro.getDipCombinazioneRicerca().getIdStrut(), filtro.getNmFiltroRicerca(),
                    filtro.getDipCombinazioneRicerca().getIdTipoUnitaDoc());
        }
    }

    private void getQueryDataDa(DipFiltroRicerca filtro, Timestamp valoreFiltro, QueryBuilder queryParziale) {
        if (filtro.getTiTipoFiltro().equals(TiTipoFiltro.METADATO_DOC.name())) {
            queryParziale.addQueryForIntersect(QRY_META_DOC_WITH_DATE_FROM_PARAM, valoreFiltro,
                    filtro.getDipCombinazioneRicerca().getIdStrut(), filtro.getIdAttribDatiSpec());
        } else if (filtro.getTiTipoFiltro().equals(TiTipoFiltro.METADATO_UD.name())) {
            queryParziale.addQueryForIntersect(QRY_META_UD_WITH_DATE_FROM_PARAM, valoreFiltro,
                    filtro.getDipCombinazioneRicerca().getIdStrut(), filtro.getIdAttribDatiSpec());
        } else {
            // Inserita la query oer tipo unita doc che mancava
            queryParziale.addQueryForIntersect(QRY_DATO_PROF_WITH_DATE_FROM_PARAM, valoreFiltro,
                    // filtro.getDipCombinazioneRicerca().getIdStrut(), filtro.getTiTipoFiltro(),
                    filtro.getDipCombinazioneRicerca().getIdStrut(), filtro.getNmFiltroRicerca(),
                    filtro.getDipCombinazioneRicerca().getIdTipoUnitaDoc());
        }
    }

    private void getQueryDataA(DipFiltroRicerca filtro, Timestamp valoreFiltro, QueryBuilder queryParziale) {

        if (filtro.getTiTipoFiltro().equals(TiTipoFiltro.METADATO_DOC.name())) {
            queryParziale.addQueryForIntersect(QRY_META_DOC_WITH_DATE_TO_PARAM, valoreFiltro,
                    filtro.getDipCombinazioneRicerca().getIdStrut(), filtro.getIdAttribDatiSpec());
        } else if (filtro.getTiTipoFiltro().equals(TiTipoFiltro.METADATO_UD.name())) {
            queryParziale.addQueryForIntersect(QRY_META_UD_WITH_DATE_TO_PARAM, valoreFiltro,
                    filtro.getDipCombinazioneRicerca().getIdStrut(), filtro.getIdAttribDatiSpec());
        } else {
            // Inserita la query oer tipo unita doc che mancava
            queryParziale.addQueryForIntersect(QRY_DATO_PROF_WITH_DATE_TO_PARAM, valoreFiltro,
                    // filtro.getDipCombinazioneRicerca().getIdStrut(), filtro.getTiTipoFiltro(),
                    filtro.getDipCombinazioneRicerca().getIdStrut(), filtro.getNmFiltroRicerca(),
                    filtro.getDipCombinazioneRicerca().getIdTipoUnitaDoc());
        }
    }

    public Timestamp getUltimaDataSyncro(String nmRicerca) {
        String query = "SELECT MIN(dsa.dt_Ultima_Data_Elab) FROM Dip_Syncro_Aro dsa "
                + "WHERE dsa.id_Strut IN (SELECT DISTINCT(pug.id_Strut_reale) "
                + "                        FROM Dip_Ricerca ric "
                + "                        JOIN dip_Combinazione_Ricerca combs "
                + "                            ON (combs.id_ricerca=ric.id_ricerca) "
                + "                        JOIN STRUT_REFERENZIATE_PUG pug "
                + "                            ON (pug.id_strut_fittizia=combs.id_strut) "
                + "                        WHERE ric.nm_Ricerca = ?)";
        Query q = em.createNativeQuery(query);
        q.setParameter(1, nmRicerca);
        return (Timestamp) q.getSingleResult();

    }

    @SuppressWarnings("unchecked")
    public AroVLisLinkUnitaDocTableBean getAroVLisLinkUnitaDocTableBean(BigDecimal idUnitaDoc, String cdUd,
            String dataRefertoString) {
        String queryStr = "SELECT u FROM AroVLisLinkUnitaDoc u WHERE u.idUnitaDoc = :idUnitaDoc "
                + "ORDER BY u.dtRegUnitaDoc DESC ";
        Query query = em.createQuery(queryStr);
        query.setParameter("idUnitaDoc", idUnitaDoc);
        List<AroVLisLinkUnitaDoc> listaLink = query.getResultList();
        AroVLisLinkUnitaDocTableBean listaLinkTableBean = new AroVLisLinkUnitaDocTableBean();
        try {
            if (listaLink != null && !listaLink.isEmpty()) {
                for (AroVLisLinkUnitaDoc rigaLink : listaLink) {
                    AroVLisLinkUnitaDocRowBean row = (AroVLisLinkUnitaDocRowBean) Transform.entity2RowBean(rigaLink);
                    if (rigaLink.getFlRisolto().equals("1")) {
                        row.setString("referto_disponibile", "SI");
                    } else {
                        row.setString("referto_disponibile", "NO");
                    }
                    row.setString("cdUd", cdUd);
                    row.setString("dataRefertoString", dataRefertoString);
                    listaLinkTableBean.add(row);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return listaLinkTableBean;
    }

    @SuppressWarnings("unchecked")
    public AroVLisDocTableBean getAroVLisDocTableBean(BigDecimal idUnitaDoc) {
        Query q = em.createNativeQuery(
                "SELECT DISTINCT doc.id_unita_doc, doc.nm_tipo_doc, COUNT(*) cntDoc, LISTAGG(DISTINCT doc.nm_tipo_doc, ',' ON OVERFLOW TRUNCATE) WITHIN GROUP (ORDER BY doc.nm_tipo_doc) AS tipi_doc "
                        + "FROM SACER.ARO_V_LIS_DOC DOC WHERE ( doc.id_unita_doc = ? ) GROUP BY doc.id_unita_doc, doc.nm_tipo_doc ");
        q.setParameter(1, idUnitaDoc);

        List<Object[]> listaDoc = q.getResultList();
        AroVLisDocTableBean listaDocTableBean = new AroVLisDocTableBean();
        try {
            if (listaDoc != null && !listaDoc.isEmpty()) {
                for (Object[] rigaDoc : listaDoc) {
                    AroVLisDocRowBean row = new AroVLisDocRowBean();
                    row.setIdUnitaDoc((BigDecimal) rigaDoc[0]);
                    row.setNmTipoDoc((String) rigaDoc[1]);
                    row.setBigDecimal("cntDoc", (BigDecimal) rigaDoc[2]);
                    listaDocTableBean.add(row);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return listaDocTableBean;
    }

    @SuppressWarnings("unchecked")
    public AroVLisCompDocTableBean getAroVLisCompDocTableBean(BigDecimal idUnitaDoc, String nmTipoDoc) {
        String queryStr = "SELECT DISTINCT lis_comp_doc FROM AroVLisCompDoc lis_comp_doc, AroDoc doc, DecTipoDoc tipo_doc "
                + "WHERE lis_comp_doc.idDoc = doc.idDoc " + "AND doc.idTipoDoc = tipo_doc.idTipoDoc "
                + "AND doc.aroUnitaDoc.idUnitaDoc = :idUnitaDoc " + "AND tipo_doc.nmTipoDoc = :nmTipoDoc "
                + "ORDER BY lis_comp_doc.idCompDoc, lis_comp_doc.niOrdCompDoc ";

        Query query = em.createQuery(queryStr);
        query.setParameter("idUnitaDoc", idUnitaDoc.longValue());
        query.setParameter("nmTipoDoc", nmTipoDoc);
        List<AroVLisCompDoc> listaCompDoc = query.getResultList();
        AroVLisCompDocTableBean listaCompDocTableBean = new AroVLisCompDocTableBean();
        try {
            if (listaCompDoc != null && !listaCompDoc.isEmpty()) {
                for (AroVLisCompDoc rigaCompDoc : listaCompDoc) {
                    AroVLisCompDocRowBean row = (AroVLisCompDocRowBean) Transform.entity2RowBean(rigaCompDoc);
                    AroDoc aroDoc = em.find(AroDoc.class, row.getIdDoc().longValue());
                    row.setBigDecimal("id_strut",
                            BigDecimal.valueOf(aroDoc.getAroUnitaDoc().getOrgStrut().getIdStrut()));
                    row.setBigDecimal("id_unita_doc", BigDecimal.valueOf(aroDoc.getAroUnitaDoc().getIdUnitaDoc()));
                    row.setString("cd_key_doc_vers", aroDoc.getCdKeyDocVers());
                    listaCompDocTableBean.add(row);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return listaCompDocTableBean;
    }

    @SuppressWarnings("unchecked")
    public AroVLisLinkUnitaDocTableBean getAroVLisLinkUnitaDocTableBean(BigDecimal idUnitaDoc,
            String ricercaSelezionata) {
        String queryStr = "SELECT u FROM AroVLisLinkUnitaDoc u " + "WHERE u.idUnitaDoc = :idUnitaDoc "
                + "ORDER BY u.dtRegUnitaDoc DESC ";

        Query query = em.createQuery(queryStr);
        query.setParameter("idUnitaDoc", idUnitaDoc);
        List<AroVLisLinkUnitaDoc> listaLink = query.getResultList();
        AroVLisLinkUnitaDocTableBean listaLinkTableBean = new AroVLisLinkUnitaDocTableBean();
        try {
            if (listaLink != null && !listaLink.isEmpty()) {
                for (AroVLisLinkUnitaDoc rigaLink : listaLink) {
                    AroVLisLinkUnitaDocRowBean row = (AroVLisLinkUnitaDocRowBean) Transform.entity2RowBean(rigaLink);
                    BaseRow detailRow = caricaDettaglioUd(row.getIdUnitaDocColleg(), ricercaSelezionata,
                            row.getIdStrut().longValue());
                    row.setString("tipo_ente", detailRow.getString("tipo_ente"));
                    row.setString("unione_comuni", detailRow.getString("unione_comuni"));
                    row.setString("provincia_ente", detailRow.getString("provincia_ente"));
                    row.setString("tipo_strumento", detailRow.getString("tipo_strumento"));
                    row.setString("fase_elaborazione", detailRow.getString("fase_elaborazione"));
                    row.setTimestamp("dt_creazione", detailRow.getTimestamp("dt_creazione"));
                    row.setTimestamp("dt_registrazione", detailRow.getTimestamp("dt_registrazione"));
                    row.setString("oggetto", detailRow.getString("oggetto"));
                    listaLinkTableBean.add(row);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return listaLinkTableBean;
    }

    /**
     * Dato il nome ricerca (campo unique) restituisce la voce menu associata
     *
     * @param nmRicerca
     *            nome ricerca
     *
     * @return la voce menu
     */
    public String getNmEntryMenuRicerca(String nmRicerca) {
        String queryStr = "SELECT u.nmEntryMenu FROM DipRicerca u " + "WHERE u.nmRicerca = :nmRicerca ";
        Query query = em.createQuery(queryStr);
        query.setParameter("nmRicerca", nmRicerca);
        return (String) query.getSingleResult();
    }

    /**
     * Cerca tutte le parole chive che iniziano per & + parola e la sostituisce con il nome più il contatore
     *
     * @param str
     *            valore campo
     * @param contatore
     *            counter
     *
     * @return valore sostiuito opportunamente
     */
    private String sostituisciCampoChiave(String str, int contatore) {
        int indice = str.indexOf("&");

        if (indice > -1) {
            int indice2 = str.indexOf(" ", indice + 1);
            String name = str.substring(indice + 1, indice2);

            return sostituisciCampoChiave(str.replaceFirst("&" + name, name + "_" + contatore), contatore);
        } else {
            return str;
        }
    }

    /*
     * Torna una data finale impostata alle 23:59:59.999999999....
     */
    private Timestamp getFinalTimestampFromRangeDate(java.sql.Timestamp valoreData) {
        Date dataAppo = new java.util.Date(valoreData.getTime());
        dataAppo = DateUtils.setHours(dataAppo, 23);
        dataAppo = DateUtils.setMinutes(dataAppo, 59);
        dataAppo = DateUtils.setSeconds(dataAppo, 59);
        dataAppo = DateUtils.setMilliseconds(dataAppo, 999);
        Timestamp dataFinale = new Timestamp(dataAppo.getTime());
        dataFinale.setNanos(999999999);
        return dataFinale;
    }

}
