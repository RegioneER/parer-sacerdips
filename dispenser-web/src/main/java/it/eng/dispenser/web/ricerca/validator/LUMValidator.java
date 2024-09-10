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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.eng.spagoCore.error.EMFError;
import it.eng.spagoLite.form.fields.Field;
import it.eng.spagoLite.form.fields.Fields;
import it.eng.spagoLite.form.fields.SingleValueField;
import it.eng.spagoLite.message.MessageBox;
import it.eng.spagoLite.xmlbean.form.Field.Type;
import org.springframework.stereotype.Component;

/**
 * Validator of search fields.
 *
 * @author Moretti_Lu
 *
 */
@Component("LUMValidator")
public class LUMValidator extends DefaultValidator {

    private static final Logger log = LoggerFactory.getLogger(LUMValidator.class.getName());

    @SuppressWarnings("unchecked")
    @Override
    public boolean validate(Fields<Field> fields, MessageBox messageBox) {

        for (Field field : fields) {
            Type.Enum typeField = field.getType();

            if (typeField.equals(Type.STRING)) {

                String value = null;
                SingleValueField<String> stringField = (SingleValueField<String>) field;

                try {
                    value = stringField.parse();

                    if (value != null) {
                        // value = value.toUpperCase();
                        /*
                         * Per il campo ID_PAZ_ANAGR deve essere fatto un parsing per considerare solo il codice
                         * inserito oppure il dominio più il codice di ricerca intervallati da qualsiasi carattere non
                         * alfanumerico o spazi. Se l'utente inserisce piu' di due codici il sistema considera solo i
                         * primi due. Siccome il campo in questione contiene un XML la ricerca viene effettuata con una
                         * stringa che inizia con una parte dell'XML + il primo valore inserito + il "%" + il secondo
                         * codice + "%" nel caso di inserimento di due valori. Se si inserisce un solo valore la ricerca
                         * viene fatta per "%" + codice + "%"
                         */
                        if (field.getName().equalsIgnoreCase("ID_PAZ_ANAGR")) {
                            value = value.trim();
                            value = value.replaceAll("[^A-Za-z0-9 ]", "");
                            value = value.toUpperCase();
                            value = value.replaceAll(" +\\s", " ");
                            String arr[] = value.split(" ");
                            if (arr.length == 0) {
                            } else if (arr.length == 1) {
                                value = "%" + arr[0] + "%";
                                stringField.setValue(value);
                            } else {
                                value = "<ID><Dominio>" + arr[0] + "%" + arr[1] + "%";
                                stringField.setValue(value);
                            }
                        } else {
                            stringField.setValue(value);
                        }
                    }

                } catch (EMFError e) {
                    log.error("String parse error", e);
                }
            }
        }

        return super.validate(fields, messageBox);
    }

}
