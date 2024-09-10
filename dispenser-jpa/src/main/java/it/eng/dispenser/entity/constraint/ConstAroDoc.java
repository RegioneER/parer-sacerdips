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

package it.eng.dispenser.entity.constraint;

public class ConstAroDoc {

    public enum TiAnnul {

        ANNULLAMENTO, SOSTITUZIONE
    }

    public enum TiConservazione {

        FISCALE, MIGRAZIONE, SOSTITUTIVA, VERSAMENTO_ANTICIPATO, VERSAMENTO_IN_ARCHIVIO
    }

    public enum TiCreazione {

        AGGIUNTA_DOCUMENTO, VERSAMENTO_UNITA_DOC
    }

    public enum TiDoc {

        ALLEGATO, ANNESSO, ANNOTAZIONE, PRINCIPALE
    }

    public enum TiEsitoVerifFirme {

        NEGATIVO, POSITIVO, WARNING
    }

    public enum TiStatoDoc {

        IN_ATTESA_MEMORIZZAZIONE, IN_ATTESA_SCHED, IN_VOLUME_APERTO, IN_VOLUME_CHIUSO, IN_VOLUME_DA_CHIUDERE,
        IN_VOLUME_IN_ERRORE, NON_SELEZ_SCHED
    }

    public enum TiStatoDocElencoVers {

        IN_ATTESA_MEMORIZZAZIONE, IN_ATTESA_SCHED, IN_ELENCO_APERTO, IN_ELENCO_CHIUSO, IN_ELENCO_DA_CHIUDERE,
        IN_ELENCO_FIRMATO, NON_SELEZ_SCHED
    }

}
