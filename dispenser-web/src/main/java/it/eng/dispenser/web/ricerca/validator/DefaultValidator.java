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

package it.eng.dispenser.web.ricerca.validator;

import java.util.Date;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.form.fields.Field;
import it.eng.spagoLite.form.fields.Fields;
import it.eng.spagoLite.form.fields.MultiValueField;
import it.eng.spagoLite.form.fields.SingleValueField;
import it.eng.spagoLite.message.Message;
import it.eng.spagoLite.message.MessageBox;
import it.eng.spagoLite.xmlbean.form.Field.Type;
import org.springframework.stereotype.Component;

/**
 * Validator of search fields.
 *
 * @author Moretti_Lu
 *
 */
@Component("DefaultValidator")
public class DefaultValidator extends IValidator {

    /**
     * The chars number that a string must have before the char '%'.
     */
    protected static final int CHAR_BEFORE = 3;
    /**
     * Suffix added to the name of date field used as interval start.
     */
    protected static final String SUFFIX_DATA_START = "_DA";
    /**
     * Suffix added to the name of date field used as interval end.
     */
    protected static final String SUFFIX_DATA_END = "_A";

    private static final Logger log = LoggerFactory.getLogger(DefaultValidator.class.getName());

    @SuppressWarnings("unchecked")
    @Override
    public boolean validate(Fields<Field> fields, MessageBox messageBox) {
        boolean one = false; // Sets true if at least one field has value
        HashMap<String, IntervalDate> intervals = new HashMap<String, IntervalDate>(); // Used to store the intervals
                                                                                       // extremes

        for (Field field : fields) {
            Type.Enum typeField = field.getType();

            if (typeField.equals(Type.STRING)) {

                String value = null;
                SingleValueField<String> stringField = (SingleValueField<String>) field;

                try {
                    value = stringField.parse();

                } catch (EMFError e) {
                    log.error("String parse error", e);
                }

                if (value != null && !value.isEmpty()) {

                    if (!one) {
                        one = true;
                    }

                    // Checks if the String field ends with the char '%'
                    if (value.endsWith("%")) {
                        // Checks if the chars before '%' are at least CHAR_BEFORE
                        if (value.length() <= CHAR_BEFORE) {
                            messageBox.addMessage(new Message(Message.MessageLevel.ERR,
                                    "Nel campo \"" + field.getDescription()
                                            + "\" prima del carattere '%' ci devono essere " + CHAR_BEFORE
                                            + " caratteri"));
                            return false;
                        }
                    }
                    /*
                     * tutti i campi stringa li converte in uppercase per effettuare la ricerca su dati del db che sono
                     * già stati convertiti in uppercase. In tal modo le ricerche funzionano anche se l'utente inserisce
                     * un dato in minuscolo.
                     */
                    value = value.toUpperCase();
                    stringField.setValue(value);

                }
                // Used to test prompt
                log.debug("String field \"" + field.getName() + "\" (\"" + field.getDescription() + "\"): " + value);

            } else if (typeField.equals(Type.DATE)) {
                Date value = null;
                SingleValueField<Date> dateField = (SingleValueField<Date>) field;

                try {
                    value = dateField.parse();
                } catch (EMFError e) {
                    log.error("Date parse error", e);
                }

                if (value != null) {
                    if (!one) {
                        one = true;
                    }

                    String name = dateField.getName();
                    if (name.endsWith(SUFFIX_DATA_START)) {
                        name = name.subSequence(0, name.length() - SUFFIX_DATA_START.length()).toString();

                        if (intervals.containsKey(name)) {
                            IntervalDate date_after = intervals.get(name);

                            if (date_after.isInit()) {
                                log.warn("There are two \"" + name + SUFFIX_DATA_START + "\"");
                            }

                            if (value.after(date_after.getDate())) {
                                messageBox.addMessage(
                                        new Message(Message.MessageLevel.ERR, "Il campo \"" + field.getDescription()
                                                + "\" e' successivo a \"" + date_after.getDescription() + "\""));
                                return false;
                            } else {
                                intervals.remove(name);
                            }
                        } else {
                            SingleValueField<Date> dataA = (SingleValueField<Date>) fields
                                    .getComponent(name + SUFFIX_DATA_END);
                            String str = dataA.getValue();
                            if (str.trim().equals("")) {
                                dataA.setValue(dateField.getValue());
                            }
                            IntervalDate interval = new IntervalDate(true, dateField.getDescription(), value);
                            intervals.put(name, interval);
                        }
                    } else if (dateField.getName().endsWith(SUFFIX_DATA_END)) {
                        name = name.subSequence(0, name.length() - SUFFIX_DATA_END.length()).toString();

                        if (intervals.containsKey(name)) {
                            IntervalDate date_before = intervals.get(name);

                            if (!date_before.isInit()) {
                                log.warn("There are two \"" + name + SUFFIX_DATA_END + "\"");
                            }

                            if (value.before(date_before.getDate())) {
                                messageBox.addMessage(
                                        new Message(Message.MessageLevel.ERR, "Il campo \"" + field.getDescription()
                                                + "\" e' precedente a \"" + date_before.getDescription() + "\""));
                                return false;
                            } else {
                                intervals.remove(name);
                            }
                        } else {
                            SingleValueField<Date> dataDa = (SingleValueField<Date>) fields
                                    .getComponent(name + SUFFIX_DATA_START);
                            String str = dataDa.getValue();
                            if (str.trim().equals("")) {
                                dataDa.setValue(dateField.getValue());
                            } else {
                                IntervalDate interval = new IntervalDate(false, dateField.getDescription(), value);
                                intervals.put(name, interval);
                            }
                        }
                    }
                }

                // Used to test prompt
                log.debug("Date field \"" + field.getName() + "\" (\"" + field.getDescription() + "\"): " + value);
            } else if (!one) {
                if (field instanceof SingleValueField) {
                    SingleValueField<?> singleField = (SingleValueField<?>) field;

                    try {
                        if (singleField.parse() != null) {
                            one = true;
                        }
                    } catch (EMFError e) {
                        log.error("SingleValueField parse error", e);
                    }

                    // Used to test prompt
                    log.debug(singleField.getType().toString() + " field \"" + singleField.getName() + "\" (\""
                            + singleField.getDescription() + "\"): " + singleField.getValue());
                } else if (field instanceof MultiValueField) {
                    // TODO to test
                    MultiValueField<?> multiField = (MultiValueField<?>) field;

                    try {
                        if (multiField.parse() != null) {
                            one = true;
                        }
                    } catch (EMFError e) {
                        log.error("MultiValueField parse error", e);
                    }

                    // Used to test prompt
                    log.debug(multiField.getType().toString() + " field \"" + multiField.getName() + "\" (\""
                            + multiField.getDescription() + "\"): " + multiField.getValues());
                }
            }
        }

        // If no fields are set throws an error message.
        if (!one) {
            messageBox.addMessage(new Message(Message.MessageLevel.ERR, "Inserire almeno un campo"));
            return false;
        } else if (!intervals.isEmpty()) {
            // If only one of date interval is set throws an error message.
            for (IntervalDate i : intervals.values()) {
                messageBox.addMessage(new Message(Message.MessageLevel.ERR,
                        "Hai inserito solo il campo \"" + i.getDescription() + "\" dell'intervallo temporale"));
            }
            return false;
        }

        return true;
    }

    protected class IntervalDate {

        /**
         * Sets true if is the interval start
         */
        private boolean init;
        /**
         * Used in the error text.
         */
        private String description;
        private Date date;

        public IntervalDate(boolean init, String description, Date date) {
            this.init = init;
            this.description = description;
            this.date = date;
        }

        public boolean isInit() {
            return this.init;
        }

        public String getDescription() {
            return this.description;
        }

        public Date getDate() {
            return this.date;
        }
    }
}
