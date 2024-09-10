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
 * PABValidator of search fields.
 *
 * @author Iacolucci_Ma
 *
 */
@Component("PABValidator")
public class PABValidator extends DefaultValidator {

    private static final Logger log = LoggerFactory.getLogger(PABValidator.class.getName());

    @SuppressWarnings("unchecked")
    @Override
    public boolean validate(Fields<Field> fields, MessageBox messageBox) {
        boolean valido = super.validate(fields, messageBox);
        for (Field field : fields) {
            Type.Enum typeField = field.getType();
            if (typeField.equals(Type.STRING)) {
                String value = null;
                SingleValueField<String> stringField = (SingleValueField<String>) field;

                if (field.getName().equalsIgnoreCase("ENTE_STRUTTURA")) {
                    try {
                        value = stringField.parse();
                        if (value == null) {
                            messageBox.addInfo("Almeno il filtro Ente/Struttura deve essere valorizzato");
                            valido = false;
                        }
                    } catch (EMFError e) {
                        log.error("String parse error", e);
                    }
                }
            }
        }
        return valido;
    }

}
