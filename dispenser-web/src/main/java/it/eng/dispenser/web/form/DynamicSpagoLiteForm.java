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

import it.eng.spagoLite.form.base.BaseForm;
import it.eng.spagoLite.form.fields.Field;
import it.eng.spagoLite.form.fields.Fields;
import it.eng.spagoLite.form.fields.SingleValueField;
import it.eng.spagoLite.form.fields.impl.Button;
import it.eng.spagoLite.form.fields.impl.CheckBox;
import it.eng.spagoLite.form.fields.impl.ComboBox;
import it.eng.spagoLite.form.fields.impl.Input;
import it.eng.spagoLite.form.list.List;
import it.eng.spagoLite.xmlbean.form.Field.Type;

public class DynamicSpagoLiteForm extends BaseForm {

    public static String DESCRIPTION = "Ricerca";

    public DynamicSpagoLiteForm() {
        super(DESCRIPTION);
        addComponent(new FormRicerca());
        addComponent(new FormDettaglio());
        addComponent(new RicercaList());
    }

    public static FormRicerca getFormRicerca(BaseForm form) {
        return (FormRicerca) form.getComponent(FormRicerca.NAME);
    }

    public static RicercaList getRicercaList(BaseForm form) {
        return (RicercaList) form.getComponent(RicercaList.NAME);
    }

    public static FormDettaglio getFormDettaglio(BaseForm form) {
        return (FormDettaglio) form.getComponent(FormDettaglio.NAME);
    }

    public FormRicerca getFormRicerca() {
        return (FormRicerca) getComponent(FormRicerca.NAME);
    }

    public RicercaList getRicercaList() {
        return (RicercaList) getComponent(RicercaList.NAME);
    }

    public FormDettaglio getFormDettaglio() {
        return (FormDettaglio) getComponent(FormDettaglio.NAME);
    }

    /**
     *
     * @author Quaranta_M Form di ricerca con un bottone "Ricerca" predefinito
     *
     */
    public static class FormRicerca extends Fields<Field> {

        public static String NAME = "FormRicerca";
        public static String DESCRIPTION = "Form di ricerca";
        public static final String ricerca = "Ricerca";
        public static final String pulisci = "Pulisci";

        public static final String RICERCA = NAME + "." + ricerca;
        public static final String PULISCI = NAME + "." + pulisci;

        public FormRicerca() {
            super(null, NAME, DESCRIPTION);
            addComponent(new Button(this, ricerca, "Ricerca", "", Type.STRING, null, false, false, false, false, false,
                    true));
            addComponent(new Button(this, pulisci, "Pulisci", "", Type.STRING, null, false, false, false, false, false,
                    true));
        }

        public ComboBox<String> getComboBox(String name) {
            return (ComboBox<String>) getComponent(name);
        }

        public Input<String> getString(String name) {
            return (Input<String>) getComponent(name);
        }

        public CheckBox<String> getCheckBox(String name) {
            return (CheckBox<String>) getComponent(name);
        }

        public Input<Timestamp> getDate(String name) {
            return (Input<Timestamp>) getComponent(name);
        }

        public Input<BigDecimal> getBigDecimal(String name) {
            return (Input<BigDecimal>) getComponent(name);
        }

        public Button<String> getRicerca() {
            return (Button<String>) getComponent(ricerca);
        }

        public Button<String> getPulisci() {
            return (Button<String>) getComponent(pulisci);
        }

    }

    public static class FormDettaglio extends Fields<Field> {

        public static String NAME = "FormDettaglio";
        public static String DESCRIPTION = "Form di dettaglio";

        // public static final String DETTAGLIO = NAME + "." + dettaglio;

        public FormDettaglio() {
            super(null, NAME, DESCRIPTION);

        }

        public ComboBox<String> getComboBox(String name) {
            return (ComboBox<String>) getComponent(name);
        }

        public Input<String> getString(String name) {
            return (Input<String>) getComponent(name);
        }

        public CheckBox<String> getCheckBox(String name) {
            return (CheckBox<String>) getComponent(name);
        }

        public Input<Timestamp> getDate(String name) {
            return (Input<Timestamp>) getComponent(name);
        }

        public Input<BigDecimal> getBigDecimal(String name) {
            return (Input<BigDecimal>) getComponent(name);
        }

        // case CHECK:
        // comp = new CheckBox<String>(form.getFormRicerca(), campo.getNmCampo(), campo.getDsCampo(), "",
        // Type.STRING, null, false, false, false, false);
        // break;
        // case COMBO:
        // comp = new ComboBox<String>(form.getFormRicerca(), campo.getNmCampo(), campo.getDsCampo(), "",
        // Type.STRING, null, false, false, false, false, 0);
        // populateCombo((ComboBox<String>) comp, campo.getDipValoreCombos());
        //
        // break;
        // case DATA:
        // comp = new Input<Timestamp>(form.getFormRicerca(), campo.getNmCampo(), campo.getDsCampo(), "",
        // Type.DATE, null, false, false, false, false, 0, null, null, null);
        // break;
        // case RADIO:
        // break;
        // case STRINGA:
        // comp = new Input<String>(form.getFormRicerca(), campo.getNmCampo(), campo.getDsCampo(), "",
        // Type.STRING, null, false, false, false, false, 0, null, null, null);
        // case NUMERO:
        // comp = new Input<Integer>(form.getFormRicerca(), campo.getNmCampo(), campo.getDsCampo(), "",
        // Type.INTEGER, null, false, false, false, false, 0, null, null, null);
        // break;

    }

    public static class RicercaList extends List<SingleValueField<?>> {

        public static String NAME = "RicercaList";
        public static String DESCRIPTION = "Lista di ricerca";

        public RicercaList() {
            super(null, NAME, DESCRIPTION, Status.view, DESCRIPTION, null, false, false, false, false, false, null);

        }

    }

}
