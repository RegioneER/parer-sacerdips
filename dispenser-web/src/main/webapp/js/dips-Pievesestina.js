function dips_onLoad() {

    /* Quando l'utente clicca su ricerca si memorizza cio' che l'utente aveva
     * inserito per riprenderlo quando si ritorna dalla post verso il server.
     * questo perché il valore del campo viene modificato a livello server e si
     * vedrebbe a video il valore processato.
     */
    $("input[name='operation__ricerca']").click(function (event) {
        var contenutoOriginale = $("#ID_PAZ_ANAGR").length && $("#ID_PAZ_ANAGR").val().trim();
        setCookie(contenutoOriginale);
    });

    $("input[name='operation__pulisci']").click(function (event) {
        // Pulisce il cookie in fase di pulizia richiesta dall'utente
        deleteCookie();
    });

    // PAGINA DI RICERCA
    if ($('#anchorRicerca').length > 0) {
        creaColonneDownload();
        coloraDiGialloAnnullati();
        creaLinkRefertiCollegatiRic();
        getUltimaDataSyncro();
        getNumeroRecordsEstratti();

        /*  Al caricamento della pagina vede se esiste un cookie con il valore che
         aveva digitato originariamente l'utente */
        var contenuto = $("#ID_PAZ_ANAGR").length && $("#ID_PAZ_ANAGR").val().trim();
        /* Se si clicca nel menu di pievesestina il campo è vuoto quindi il cookie non va ripristinato,
           anzi cancellato del tutto */
        if (contenuto != null && contenuto != "") {
            var str = getCookie();
            if (str != "") {
                $("#ID_PAZ_ANAGR").val(str);
            }
        } else {
            deleteCookie();
        }

    }

    // PAGINA DI DETTAGLIO
    if ($('#anchorDettaglio').length > 0) {
        scriviTitoloPaginaDettaglio();
        calcolaStrutturaPrescrCalc();
        cleanMediciRefertanti();
        cleanIDPazAnagrAziendale();
        cleanTipoPrestazione();
        creaLinkRefertiCollegatiDett();
        nascondiCampiNonValorizzati();
    }

//         //PAGINA DI REFERTI COLLEGATI
//	if ($('#anchorRefColl').length > 0) {            
//            creaLinkRefertiCollegatiList();
//        }

}

var cookieName = "dispenser-pievesestina-id-paziente";

// Settaggio del cookie per pievesestina
function setCookie(cvalue) {
    document.cookie = cookieName + "=" + cvalue + ";";
}

// Lettura del cookie per pievesestina
function getCookie() {
    var name = cookieName + "=";
    var ca = document.cookie.split(';');
    for (var i = 0; i < ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0) == ' ')
            c = c.substring(1);
        if (c.indexOf(name) == 0)
            return c.substring(name.length, c.length);
    }
    return "";
}

// Cancellazione del cookie
function deleteCookie() {
    document.cookie = cookieName + "=; expires=Thu, 01 Jan 1970 00:00:00 UTC";
}

function scriviTitoloPaginaDettaglio() {
    var idReferto = $('#NUMERO_hidden').attr("value");
    var dataReferto = $('#DATA_REG_hidden').attr("value");
    $('.contentTitle h2').html("Dettaglio sul referto ID " + idReferto + " del " + dataReferto);

}

function creaLinkRefertiCollegatiRic() {
    // ottengo i td collocati nella colonna "Referti collegati" e
    // inserisco un link se hanno il valore SI
    var idx = $('#RicercaList tr th:contains("Referti collegati")').index() + 1;
    $('#RicercaList tr td:nth-child(' + idx + '):contains("SI")').contents().wrap(function () {

        var str = $(this).parents('tr').find('td a').attr('href').split("&");
        str = str.filter(function (value) {
            return value.indexOf("riga") >= 0;
        });

        var el = $("<a></a>").attr("href", "Ricerca.html?operation__customLogic__refCollegati__" + str[0].split("=")[1]);
        return el;
    });

}

//function creaLinkRefertiCollegatiList(){
//	// ottengo i td collocati nella colonna "Referto disponibile" e
//	// inserisco un link se hanno il valore SI
//	var idx = $('#RefertiCollegatiList tr th:contains("Referto disponibile")').index() + 1;
//	$('#RefertiCollegatiList tr td:nth-child(' + idx + '):contains("SI")').contents().wrap(function() {
//		
//		
//		
//		var el = $("<a></a>").attr("href", "Pievesestina.html?operation__loadRefCollegato");
//		return el;
//	});	
//	
//}


function creaLinkRefertiCollegatiDett() {
    // ottengo i td collocati nella colonna "Referti collegati" e
    // inserisco un link se hanno il valore SI
    var numRefColl = $('#NUM_DOC_COLLEGATI_hidden').attr("value");
    if (numRefColl !== '0') {
        $('#NUM_DOC_COLLEGATI').contents().wrap(function () {
            var el = $("<a></a>").attr("href", "Ricerca.html?operation__customLogic__refCollegati")
            return el;
        });

    }

}

function coloraDiGialloAnnullati() {
    // setto il colore giallo sulle righe che riportano la validita
    // annullato
    var elems = $('#RicercaList tr td[title]').filter(function () {
        return $(this).attr('title').toLowerCase().indexOf('annullato') > -1;
    });

    elems.siblings().css('background-color', 'gold');
    elems.css('background-color', 'gold');
}

function calcolaStrutturaPrescrCalc() {
    var calc = "";
	var strut = $('#STRUT_PRESCRITTRICE_DEST_hidden').attr("value");
	var id = $('#ID_STRUT_PRESCRITTRICE_DEST_hidden').attr("value");
    var medico = $('#NM_STRUT_PRESCRITTRICE_DEST_hidden').attr("value");

    if(strut != null && strut != "")
    	calc += strut;

    if(id != null && id != "")
    	calc += " - ID " + id;

    if(medico != null && medico != "" && medico != "Non Specificato")
    	calc += " (medico " + medico + ")";

    $('#STRUT_PRESCRITTRICE_CALC').html(calc);
}

function nascondiCampiNonValorizzati() {
    if ($('#DATA_ANNUL_hidden').attr("value") == "") {
        $('label[for="DATA_ANNUL"]').addClass('displayNone');
        $('#DATA_ANNUL').addClass('displayNone');
    }
    if ($('#COD_NOSOGRAFICO_hidden').attr("value") == "") {
        $('label[for="COD_NOSOGRAFICO"]').addClass('displayNone');
        $('#COD_NOSOGRAFICO').addClass('displayNone');
    }

    if ($('#MSG_ERRORE_hidden').attr("value") == "") {
        $('label[for="MSG_ERRORE"]').addClass('displayNone');
        $('#MSG_ERRORE').addClass('displayNone');
    }

}

function cleanMediciRefertanti() {
    var innerHtml = $('#AUTORE').html();
    
    innerHtml = innerHtml.replace(/&lt;\/Nominativo&gt;&lt;Nominativo&gt;/g, ', ');
    innerHtml = innerHtml.replace(/&lt;Nominativo&gt;/g, '');
    innerHtml = innerHtml.replace(/&lt;\/Nominativo&gt;/g, '');
    $('#AUTORE').html(innerHtml);
}

function cleanIDPazAnagrAziendale() {
    var innerHtml = $('#ID_PAZ_ANAGR_AZ').html();
    innerHtml = innerHtml.replace(/&lt;\/Valore&gt;&lt;\/ID&gt;&lt;ID&gt;&lt;Dominio&gt;/g, ', ');
    innerHtml = innerHtml.replace(/&lt;ID&gt;&lt;Dominio&gt;/g, '');
    innerHtml = innerHtml.replace(/&lt;\/Dominio&gt;&lt;Valore&gt;/g, ': ');
    innerHtml = innerHtml.replace(/&lt;\/Valore&gt;&lt;\/ID&gt;/g, '');
    $('#ID_PAZ_ANAGR_AZ').html(innerHtml);
}

function cleanTipoPrestazione() {
    var innerHtml = $('#TIPO_PRESTAZIONE').html();

    innerHtml = innerHtml.replace(/&lt;\/Prestazione&gt;&lt;Prestazione&gt;/g, '<br />');
    innerHtml = innerHtml.replace(/&lt;Prestazione&gt;/g, '');
    innerHtml = innerHtml.replace(/&lt;\/Prestazione&gt;/g, '');
    $('#TIPO_PRESTAZIONE').html(innerHtml);
}

function creaColonneDownload() {
    $('#RicercaList tr th').last().after('<th>Download referto archiviato</th><th>Download PDF originario</th><th>Download PDF Sacer</th><th>Download AIP per esibizione</th>');
    var elems = $('#RicercaList tr td').parents('tr');
    elems.each(function (index) {
        $(this).find("td:last-child").after($("<td></td><td></td><td></td><td></td>"));
    });
    creaLinkDownloadRecupero();
    creaLinkDownloadComponente();
    creaLinkDownloadPdf();
    creaDownloadAipPerEsibizione();
}

function creaLinkDownloadRecupero() {
    var downloadIdx = $('#RicercaList tr th:contains("Download referto archiviato")').index() + 1;
    $('#RicercaList tr td:nth-child(' + downloadIdx + ')').wrapInner(function () {

        var str = $(this).parents('tr').find('td a').attr('href').split("&");
        str = str.filter(function (value) {
            return value.indexOf("riga") >= 0;
        });

        var el = $("<a></a>").attr("href", "Ricerca.html?operation__customLogic__downloadRecupero__" + str[0].split("=")[1]).addClass("DownloadFileSessione");
        return el;
    });
}

function creaLinkDownloadComponente() {
    var idx = $('#RicercaList tr th:contains("Con PDF")').index() + 1;
    var downloadIdx = $('#RicercaList tr th:contains("Download PDF originario")').index() + 1;

    $('#RicercaList tr td:nth-child(' + idx + '):contains("SI")').siblings(':nth-child(' + downloadIdx + ')').wrapInner(function () {
        var str = $(this).parents('tr').find('td a').attr('href').split("&");
        str = str.filter(function (value) {
            return value.indexOf("riga") >= 0;
        });
        var el = $("<a></a>").attr("href", "Ricerca.html?operation__customLogic__downloadComponente__" + str[0].split("=")[1]).addClass("DownloadFileSessione");
        return el;
    });
}

function creaLinkDownloadPdf() {
    var downloadIdx = $('#RicercaList tr th:contains("Download PDF Sacer")').index() + 1;
    $('#RicercaList tr td:nth-child(' + downloadIdx + ')').wrapInner(function () {

        var str = $(this).parents('tr').find('td a').attr('href').split("&");
        str = str.filter(function (value) {
            return value.indexOf("riga") >= 0;
        });

        var el = $("<a></a>").attr("href", "Ricerca.html?operation__customLogic__downloadPdf__" + str[0].split("=")[1]).addClass("DownloadFileSessione");
        return el;
    });
}

function creaDownloadAipPerEsibizione() {
    var idx = $('#RicercaList tr th:contains("formato UniSyncro")').index() + 1;
    var downloadIdx = $('#RicercaList tr th:contains("Download AIP per esibizione")').index() + 1;
    $('#RicercaList tr td:nth-child(' + idx + ') input:hidden[value=1]').parent().siblings(':nth-child(' + downloadIdx + ')').wrapInner(function () {
//    $('#RicercaList tr td:nth-child(' + idx + '):contains("1")').siblings(':nth-child(' + downloadIdx + ')').wrapInner(function () {

        var str = $(this).parents('tr').find('td a').attr('href').split("&");
        str = str.filter(function (value) {
            return value.indexOf("riga") >= 0;
        });

        var el = $("<a></a>").attr("href", "Ricerca.html?operation__customLogic__downloadAip__" + str[0].split("=")[1]).addClass("DownloadFileSessione");
        return el;
    });

/*    
    var downloadIdx = $('#RicercaList tr th:contains("Download AIP per esibizione")').index() + 1;
    $('#RicercaList tr td:nth-child(' + downloadIdx + ')').wrapInner(function () {

        var str = $(this).parents('tr').find('td a').attr('href').split("&");
        str = str.filter(function (value) {
            return value.indexOf("riga") >= 0;
        });

        var el = $("<a></a>").attr("href", "Ricerca.html?operation__customLogic__downloadAip__" + str[0].split("=")[1]).addClass("DownloadFileSessione");
        return el;
    });
*/
    
}
