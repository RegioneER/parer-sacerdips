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

package it.eng.dispenser.ws.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.eng.dispenser.component.JAXBSingleton;
import it.eng.dispenser.ws.dto.EsitoConnessione;
import it.eng.dispenser.ws.dto.RichiestaWSInput;
import it.eng.dispenser.ws.dto.RichiestaWSInput.TipoRichiesta;
import it.eng.dispenser.xml.utils.XmlUtils;
import it.eng.parer.ws.xml.versRespStato.ECEsitoExtType;
import it.eng.parer.ws.xml.versRespStato.StatoConservazione;

@Service
public class RichiestaWSClient {

    private static Logger log = LoggerFactory.getLogger(RichiestaWSClient.class);

    @Autowired
    private JAXBSingleton jaxbSingleton;

    public EsitoConnessione callWs(RichiestaWSInput input) {
        return callWs(input.getTipoRichiesta(), input.getUrlRichiesta(), input.getParams(), input.getTimeout(),
                input.isMultipart());
    }

    public EsitoConnessione callWs(TipoRichiesta tipoRichiesta, String url, List<NameValuePair> inputParams,
            int timeout, boolean multipart) {
        EsitoConnessione esitoConnessione = new EsitoConnessione();
        try {
            boolean useHttps = true;

            HttpParams httpParameters = new BasicHttpParams();
            HttpConnectionParams.setConnectionTimeout(httpParameters, timeout);
            HttpConnectionParams.setSoTimeout(httpParameters, timeout);
            // crea una nuova istanza di HttpClient, predisponendo la chiamata
            // del metodo POST
            HttpClient httpclient = new DefaultHttpClient(httpParameters);
            if (useHttps) {
                // se devo usare HTTPS...
                // creo un array di TrustManager per considerare tutti i
                // certificati server come validi.
                // questo andrebbe rimpiazzato con uno che validi il certificato
                // con un certstore...
                X509TrustManager tm = new X509TrustManager() {
                    @Override
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    @Override
                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                    }

                    @Override
                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                    }
                };

                try {
                    // Creo il contesto SSL utilizzando i trust manager creati
                    SSLContext ctx = SSLContext.getInstance("TLS");
                    ctx.init(null, new TrustManager[] { tm }, null);

                    // Creo la connessione https
                    SSLSocketFactory ssf = new SSLSocketFactory(ctx, SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                    ClientConnectionManager ccm = httpclient.getConnectionManager();
                    SchemeRegistry sr = ccm.getSchemeRegistry();
                    sr.register(new Scheme("https", 443, ssf));
                    httpclient = new DefaultHttpClient(ccm, httpclient.getParams());
                } catch (NoSuchAlgorithmException | KeyManagementException e) {
                    log.error("Errore interno nella preparazione della chiamata HTTPS " + e.getMessage());
                }
            }

            log.debug("Chiamata del servizio all'url " + url);
            HttpPost httpPost = new HttpPost(url);
            if (multipart) {
                // Inizializza la request come multipart, nella modalità browser
                // compatible che
                // consente di inviare i dati come campi di una form web
                MultipartEntity reqEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

                for (NameValuePair param : inputParams) {
                    reqEntity.addPart(param.getName(), new StringBody(param.getValue()));
                }

                httpPost.setEntity(reqEntity);
            } else {
                httpPost.setEntity(new UrlEncodedFormEntity(inputParams));
            }

            HttpResponse response = null;
            boolean timeoutException = false;
            int statusCode = 0;
            try {
                response = httpclient.execute(httpPost);
                statusCode = response.getStatusLine().getStatusCode();
            } catch (Exception ex) {
                timeoutException = true;
                log.debug("catch timeoutException " + ex);
            }
            if (statusCode == 404 || timeoutException) {
                esitoConnessione.setErroreConnessione(true);
                if (statusCode == 404) {
                    esitoConnessione.setDescrErrConnessione("Errore 404");
                } else {
                    esitoConnessione.setDescrErrConnessione("Errore timeout");
                }
            } else {
                // recupera la risposta
                if (response != null) {
                    HttpEntity resEntity = response.getEntity();
                    InputStream responseIS;
                    if (resEntity != null) {
                        if (resEntity.getContentType().getValue().startsWith("application/zip")) {
                            esitoConnessione.setCodiceEsito(EsitoConnessione.Esito.OK.name());
                            esitoConnessione.setResponse(resEntity.getContent());
                            // Aggiungo in un campo la lunghezza dell'oggetto,
                            // per aggiungerla al download
                            esitoConnessione.setXmlResponse(String.valueOf(resEntity.getContentLength()));
                        } else if (resEntity.getContentType().getValue().startsWith("application/xml")) {
                            Unmarshaller um;
                            byte[] entityBA = EntityUtils.toByteArray(resEntity);
                            responseIS = new ByteArrayInputStream(entityBA);
                            switch (tipoRichiesta) {
                            case REC_DIP_UNI_DOC:
                            case REC_COMP:
                            case REC_PDF:
                            case REC_AIP:
                                um = jaxbSingleton.getContextStatoConservazione().createUnmarshaller();
                                StatoConservazione responseRecupero = XmlUtils.unmarshallResponse(um, responseIS,
                                        StatoConservazione.class);
                                esitoConnessione.setCodiceEsito(
                                        responseRecupero.getEsitoGenerale().getCodiceEsito() == ECEsitoExtType.NEGATIVO
                                                ? EsitoConnessione.Esito.KO.name() : EsitoConnessione.Esito.OK.name());
                                esitoConnessione.setCodiceErrore(responseRecupero.getEsitoGenerale().getCodiceErrore());
                                esitoConnessione
                                        .setMessaggioErrore(responseRecupero.getEsitoGenerale().getMessaggioErrore());
                                esitoConnessione.setResponse(responseRecupero);
                                break;
                            default:
                                break;
                            }
                            esitoConnessione.setXmlResponse(new String(entityBA, StandardCharsets.UTF_8));
                        }
                        esitoConnessione.setErroreConnessione(false);
                    }
                }
            }
        } catch (IOException | JAXBException ex) {
            log.error("Impossibile decodificare il messaggio di risposta", ex);
            esitoConnessione.setResponse(null);
            esitoConnessione.setXmlResponse(null);
            esitoConnessione.setErroreConnessione(false);
            esitoConnessione.setDescrErrConnessione(null);
            esitoConnessione.setCodiceEsito(EsitoConnessione.Esito.KO.name());
            esitoConnessione.setCodiceErrore(null);
            esitoConnessione.setMessaggioErrore("Impossibile decodificare il messaggio di risposta");
        }
        return esitoConnessione;
    }
}
