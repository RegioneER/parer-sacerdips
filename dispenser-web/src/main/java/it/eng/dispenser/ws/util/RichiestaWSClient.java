/*
 * Engineering Ingegneria Informatica S.p.A.
 *
 * Copyright (C) 2023 Regione Emilia-Romagna <p/> This program is free software: you can
 * redistribute it and/or modify it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the License, or (at your option)
 * any later version. <p/> This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Affero General Public License for more details. <p/> You should
 * have received a copy of the GNU Affero General Public License along with this program. If not,
 * see <https://www.gnu.org/licenses/>.
 */

package it.eng.dispenser.ws.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
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
        return callWs(input.getTipoRichiesta(), input.getUrlRichiesta(), input.getParams(),
                input.getTimeout(), input.isMultipart());
    }

    /*
     * Metodo che effettua la chiamata al servizio web. La chiama avviene tramite un client HTTP che
     * supporta HTTPS e bypassa la validazione del certificato SSL.
     *
     * Nota: la sessione viene chiusa dal chiamante che "consuma" l'oggetto o response ottenuta.
     */
    private EsitoConnessione callWs(TipoRichiesta tipoRichiesta, String url,
            List<NameValuePair> inputParams, int timeout, boolean multipart) {
        EsitoConnessione esitoConnessione = new EsitoConnessione();

        // create standard http client
        CloseableHttpClient httpclient = createHttpClient(timeout);
        try {
            //
            HttpPost httpPost = new HttpPost(url);
            if (multipart) {
                // Inizializza la request come multipart, nella modalità browser
                // compatible che
                // consente di inviare i dati come campi di una form web
                MultipartEntityBuilder builder = MultipartEntityBuilder.create()
                        .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                        .setCharset(StandardCharsets.UTF_8);
                for (NameValuePair param : inputParams) {
                    builder.addTextBody(param.getName(), param.getValue(),
                            ContentType.TEXT_PLAIN.withCharset(StandardCharsets.UTF_8));
                }
                httpPost.setEntity(builder.build());
            } else {
                httpPost.setEntity(new UrlEncodedFormEntity(inputParams));
            }

            log.debug("Esecuzione richiesta {}", httpPost.getRequestLine());
            HttpResponse response = httpclient.execute(httpPost);
            int statusCode = response.getStatusLine().getStatusCode();
            log.debug("Response status: {}", statusCode);

            if (statusCode != 200) {
                setError(esitoConnessione, "Il servizio restituisce errore " + statusCode);
            } else {
                processResponse(response, tipoRichiesta, esitoConnessione);
            }
        } catch (Exception ex) {
            final String msg = "Richiesta al servizio scaduta o fallita";
            log.error(msg, ex);
            setError(esitoConnessione, msg);
            // close client ONLY in case of error
            try {
                httpclient.close();
            } catch (IOException e) {
                log.error("Errore durante la chiusura del client HTTP", e);
            }
        }

        return esitoConnessione;
    }

    private CloseableHttpClient createHttpClient(int timeout) {
        RequestConfig config = RequestConfig.custom().setConnectTimeout(timeout)
                .setSocketTimeout(timeout).build();
        HttpClientBuilder builder = HttpClients.custom().setDefaultRequestConfig(config);

        try {
            // Create an SSLContext with TLS protocol
            SSLContext sslContext = SSLContext.getInstance("TLS");

            // Create a TrustManager that does not validate certificate chains
            X509TrustManager trustAllManager = new X509TrustManager() {
                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return new java.security.cert.X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(java.security.cert.X509Certificate[] certs,
                        String authType) {
                }

                @Override
                public void checkServerTrusted(java.security.cert.X509Certificate[] certs,
                        String authType) {
                }
            };

            // Initialize the SSLContext with the trust-all manager
            sslContext.init(null, new TrustManager[] {
                    trustAllManager }, new SecureRandom());

            // Create a SSLConnectionSocketFactory that uses the SSLContext and bypasses
            // hostname verification
            SSLConnectionSocketFactory ssf = new SSLConnectionSocketFactory(sslContext,
                    NoopHostnameVerifier.INSTANCE);

            // Set the SSLConnectionSocketFactory on the HttpClient builder
            builder.setSSLSocketFactory(ssf);
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            log.error("Error preparing HTTPS connection: {}", e.getMessage());
        }

        return builder.build();
    }

    private void setError(EsitoConnessione esito, String errorMessage) {
        esito.setErroreConnessione(true);
        esito.setDescrErrConnessione(errorMessage);
    }

    private void processResponse(HttpResponse response, TipoRichiesta tipoRichiesta,
            EsitoConnessione esitoConnessione) {
        try {
            HttpEntity resEntity = response.getEntity();
            if (resEntity != null) {
                String contentType = resEntity.getContentType().getValue();
                if (contentType.startsWith("application/zip")) {
                    esitoConnessione.setCodiceEsito(EsitoConnessione.Esito.OK.name());
                    esitoConnessione.setResponse(resEntity.getContent());
                    esitoConnessione.setXmlResponse(String.valueOf(resEntity.getContentLength()));
                } else if (contentType.startsWith("application/xml")) {
                    Unmarshaller um;
                    byte[] entityBA = EntityUtils.toByteArray(resEntity);
                    ByteArrayInputStream responseIS = new ByteArrayInputStream(entityBA);
                    switch (tipoRichiesta) {
                    case REC_DIP_UNI_DOC:
                    case REC_COMP:
                    case REC_PDF:
                    case REC_AIP:
                        um = jaxbSingleton.getContextStatoConservazione().createUnmarshaller();
                        StatoConservazione responseRecupero = XmlUtils.unmarshallResponse(um,
                                responseIS, StatoConservazione.class);
                        esitoConnessione.setCodiceEsito(responseRecupero.getEsitoGenerale()
                                .getCodiceEsito() == ECEsitoExtType.NEGATIVO
                                        ? EsitoConnessione.Esito.KO.name()
                                        : EsitoConnessione.Esito.OK.name());
                        esitoConnessione.setCodiceErrore(
                                responseRecupero.getEsitoGenerale().getCodiceErrore());
                        esitoConnessione.setMessaggioErrore(
                                responseRecupero.getEsitoGenerale().getMessaggioErrore());
                        esitoConnessione.setResponse(responseRecupero);
                        break;
                    default:
                        break;
                    }
                    esitoConnessione.setXmlResponse(new String(entityBA, StandardCharsets.UTF_8));
                }
                esitoConnessione.setErroreConnessione(false);
            }
        } catch (IOException | JAXBException ex) {
            log.error("Impossibile decodificare il messaggio di risposta", ex);
            esitoConnessione.setResponse(null);
            esitoConnessione.setXmlResponse(null);
            esitoConnessione.setErroreConnessione(false);
            esitoConnessione.setDescrErrConnessione(null);
            esitoConnessione.setCodiceEsito(EsitoConnessione.Esito.KO.name());
            esitoConnessione.setCodiceErrore(null);
            esitoConnessione
                    .setMessaggioErrore("Impossibile decodificare il messaggio di risposta");
        }
    }
}
