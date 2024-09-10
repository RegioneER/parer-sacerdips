function dips_onLoad() {

    // PAGINA DI RICERCA
    if ($('#anchorRicerca').length > 0) {
        loadDettaglioStrumentoUrbanistico();
        getUltimaDataSyncro();
        getNumeroRecordsEstratti();
    }

    // PAGINA DI DETTAGLIO
    if ($('#anchorDettaglio').length > 0) {
        scriviTitoloPaginaDettaglio();
        creaColonneDownload();
    }

    //PAGINA DI DETTAGLIO DEL TIPO DOCUMENTO
    if ($('#anchorTipoDoc').length > 0) {            
        scriviTitoloPaginaDettaglioTipoDoc();
        creaLinkDownloadComponente();
    }

}

function getUltimaDataSyncro() {
    $.ajax("Ricerca.html?operation=ultimaDataSyncro").done(function (data) {
        $("#spagoLiteAppForm fieldset").first().append("<div id='ultimaDataSyncro' class='containerRight w50'></div>");
        $("#ultimaDataSyncro").infoStyle("L'ultima sincronizzazione con Sacer è avvenuta il " + data.map[0].ultimaDataSyncro);
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

function scriviTitoloPaginaDettaglio() {
    var registro = $('#REGISTRO_hidden').attr("value");
    var anno = $('#ANNO_hidden').attr("value");
    var numero = $('#NUMERO_hidden').attr("value");
    var tipoEnte = $('#ENTE_CONVENZIONATO_hidden').attr("value");
    $('.contentTitle h2').html("Dettaglio dello Strumento Urbanistico " + registro + "/" + anno + "/" + numero + " - " + tipoEnte);
}

function scriviTitoloPaginaDettaglioTipoDoc() {
    var tipoDoc = $('#Nm_tipo_doc_hidden').attr("value");
    var registro = $('#REGISTRO_hidden').attr("value");
    var anno = $('#ANNO_hidden').attr("value");
    var numero = $('#NUMERO_hidden').attr("value");
    var tipoEnte = $('#ENTE_CONVENZIONATO_hidden').attr("value");
    $('.contentTitle h2').html("Dettaglio del tipo documento " + tipoDoc + " dello Strumento Urbanistico " + registro + "/" + anno + "/" + numero + " - " + tipoEnte);
}

function loadDettaglioStrumentoUrbanistico() {
    var idx = $('#RicercaList tr th:empty()').index() + 1;
    $('#RicercaList tr td:nth-child(' + idx + ')').contents('a').attr('href', function () {
        var str = $(this).parents('tr').find('td a').attr('href').split("&");
        str = str.filter(function (value) {
            return value.indexOf("riga") >= 0;
        });
        var el = "Ricerca.html?operation__customLogic__loadDettaglioConListe__" + str[0].split("=")[1];
        return el;
    });
}

function creaColonneDownload() {
    creaLinkDownloadRecuperoDaListaTipoDoc();
    creaLinkDownloadRecuperoDaListaColleg();
}

function creaLinkDownloadRecuperoDaListaTipoDoc() {
    var downloadIdx = $('#DocumentiUDList tr th:contains("Download tipo documento")').index() + 1;
    $('#DocumentiUDList tr td:nth-child(' + downloadIdx + ')').wrapInner(function () {
        var str = $(this).parents('tr').find('td a').attr('href').split("&");
        str = str.filter(function (value) {
            return value.indexOf("riga") >= 0;
        });
        var el = $("<a></a>").attr("href", "PUG.html?operation__downloadRecupero__" + str[0].split("=")[1]).addClass("DownloadFileSessione");
        return el;
    });
}

function creaLinkDownloadRecuperoDaListaColleg() {
    var downloadIdx = $('#StrumentiCollegatiList tr th:contains("Download strumento urbanistico")').index() + 1;
    $('#StrumentiCollegatiList tr td:nth-child(' + downloadIdx + ')').wrapInner(function () {
        var str = $(this).parents('tr').find('td a').attr('href').split("&");
        str = str.filter(function (value) {
            return value.indexOf("riga") >= 0;
        });
        var el = $("<a></a>").attr("href", "PUG.html?operation__downloadStrumento__" + str[0].split("=")[1]).addClass("DownloadFileSessione");
        return el;
    });
}

function creaLinkDownloadComponente() {   
    var downloadIdx = $('#CompDocList tr th:contains("Download componente")').index() + 1;
    $('#CompDocList tr td:nth-child(' + downloadIdx + ')').wrapInner(function () {
        var idx = $(this).parents('tr').index();
        var el = $("<a></a>").attr("href", "PUG.html?operation__downloadComponente__" + idx).addClass("DownloadFileSessione");
        return el;
    });
}
