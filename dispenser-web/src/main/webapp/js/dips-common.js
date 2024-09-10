/*
 * E' il primo js caricato dalle varie jsp di ricerca standard di dips.
 * In queto js ci sono le funzionalità comuni a tutte le ricerche e la definizione del metodo standard dips-onLoad()
 * che richiama le due funzioni che servono per tutte le ricerche. 
 * La dips-onLoad() potrà essere ridefinita nel js della ricerca specifica per sovrascrivere il comportamento devinito in questo js.
 */
(function ($) {
    $.fn.infoStyle = function (newHtml) {
        this.html(function (i, oldHtml) {
            var StyledInfo = "<div class=\"ui-state-highlight ui-corner-all\" style=\"padding: 0 .7em;\">";
            StyledInfo += "<p><span class=\"ui-icon ui-icon-info\" style=\"float: left; margin-right: .3em;\">";
            StyledInfo += "</span>";
            StyledInfo += newHtml;
            StyledInfo += "</p></div>";
            return StyledInfo;
        });
    }
})(jQuery);

function getUltimaDataSyncro() {
    $.ajax("Ricerca.html?operation=ultimaDataSyncro").done(function (data) {
        $("#spagoLiteAppForm fieldset").first().append("<div id='ultimaDataSyncro' class='containerRight w50'></div>");
        if (typeof data.map[0].ultimaDataSyncro !== 'undefined') {
            $("#ultimaDataSyncro").infoStyle("L'ultima sincronizzazione con Sacer è avvenuta il " + data.map[0].ultimaDataSyncro);
        } else {
            $("#ultimaDataSyncro").infoStyle("L'ultima sincronizzazione con Sacer non è ancora avvenuta");
        }
    });
}

function getNumeroRecordsEstratti() {
    $.ajax("Ricerca.html?operation=numeroRecordsEstratti").done(function (data) {
        if (data.map[0].numeroRecordsEstratti >= data.map[0].maxRecordsPerQuery) {
            $("#spagoLiteAppForm fieldset").first().append("<div id='superamentoRecords' class='containerRight w50'></div>");
            $("#superamentoRecords").infoStyle("Attenzione: i risultati visualizzati nella tabella sono incompleti in quanto la ricerca ha restituito "+data.map[0].maxRecordsPerQuery+" o piu' risultati (numero massimo dei risultati visualizzabili). Per ottenere risultati completi si consiglia di limitare l'ambito della ricerca.");
        }
    });
}
/*
 * Funzione di onLoad() standard da ridefinire eventualmente nelle ricerche specifiche per aggiungere e/o cambiarne il comportamento
 */
function dips_onLoad() {
    // PAGINA DI RICERCA
    if ($('#anchorRicerca').length > 0) {
        getUltimaDataSyncro();
        getNumeroRecordsEstratti();
    }
}
