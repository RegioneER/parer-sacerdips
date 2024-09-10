---
title: "Configurazione Sacer Dips"
---
# Configurazione Jboss EAP 7.3

## Datasource

### Web EAP Management

`Configuration > Subsystem > Datasources & Drivers > Datasources`

Cliccare su

`(+)`

**Choose Template** => Oracle 

| Attribute | |
| --- | --- | 
| **Name** | DispenserPool |
| **JNDI Name** | java:/jboss/datasources/DispenserDs |

| JDBC Driver | |
| --- | --- |
**Driver Name** | ojdbc8
**Driver Module Name** | com.oracle
**Driver Class Name** | oracle.jdbc.driver.OracleDriver

| Connection | |
| --- | --- |
**Connection URL** Esempio: jdbc:oracle:thin:@hostname:1521/SERVICE.ente.regione.emr.it
**User Name** SACER_RIC
**Password** xxxxxx

Mettere a true il parametro *spy*, si può fare solo tramite CLI 

```bash
/subsystem=datasources/xa-data-source=DispenserPool:write-attribute(name=spy,value=true)
```

### JBoss CLI

```bash
data-source add --name=DispenserPool --jndi-name=java:jboss/datasources/DispenserDs --connection-url=jdbc:oracle:thin:@hostname:1521/SERVICE.ente.regione.emr.it --user-name=SACER_RIC --password=xxx --driver-name=ojdbc8 --max-pool-size=64 --jta=true --spy=true --exception-sorter-class-name=org.jboss.jca.adapters.jdbc.extensions.oracle.OracleExceptionSorter --stale-connection-checker-class-name=org.jboss.jca.adapters.jdbc.extensions.oracle.OracleStaleConnectionChecker --valid-connection-checker-class-name=org.jboss.jca.adapters.jdbc.extensions.oracle.OracleValidConnectionChecker --statistics-enabled=true --use-ccm=true --use-fast-fail=true --validate-on-match=true --flush-strategy=FailingConnectionOnly --background-validation=false --min-pool-size=8 --enabled=true
```

## Key Store 

È necessario mettere il keystore in formato JKS in una cartella accessibile all'IDP e poi configurare la system properties sacerdips-jks-path con il path al file.  
Una soluzione è di usare la cartella ${jboss.server.config.dir}/certs in modo da averli versionati.


## System properties

### Web EAP Management

`Configuration > System properties`

Impostare le seguenti properties

Chiave | Valore di esempio | Descrizione
--- | --- | ---
fed-metadata-url | https://parer-svil.ente.regione.emr.it/saceriam/federationMetadata | Indirizzo dal quale scaricare il file federation metadata
idp-identity-id | https://parer-svil.ente.regione.emr.it/idp/shibboleth | Identificativo univoco dell'IDP all'interno della Federazione
sacerdips-key-manager-pass | *password_jks_sacerdips* | Chiave del Java Key Store utilizzato per ottenere la chiave privata del circolo di fiducia dell'IDP
sacerdips-timeout-metadata | 10000 | Timeout in secondi per la ricezione dei metadati dall'IDP
sacerdips-temp-file | /var/tmp/tmp-sacerdipsfederation.xml | Percorso assoluto del file xml che rappresenta l'applicazione all'interno del circolo di fiducia
sacerdips-sp-identity-id | https://parer-svil.ente.regione.emr.it/sacerdips  | Identità dell'applicazione all'interno del circolo di fiducia
sacerdips-refresh-checkinterval | 600000 | Intervallo di tempo in secondi utilizzato per ricontattare l'IDP per eventuali variazioni sulla configurazione del circolo di fiducia
sacerdips-store-key-name | sacerdips | Alias del certificato dell'applicazione all'interno del Java Key Store
sacerdips-jks-path | /opt/jboss-eap/certs/sacerdips.jks | Percorso assoluto del Java Key Store dell'applicazione

### JBoss CLI

```bash 
/system-property=fed-metadata-url:add(value="https://parer-svil.ente.regione.emr.it/saceriam/federationMetadata")
/system-property=idp-identity-id:add(value="https://parer-svil.ente.regione.emr.it/idp/shibboleth")
/system-property=sacerdips-key-manager-pass:add(value="password_jks_sacerdips")
/system-property=sacerdips-timeout-metadata:add(value="10000")
/system-property=sacerdips-temp-file:add(value="/var/tmp/tmp-sacerdips-federation.xml")
/system-property=sacerdips-sp-identity-id:add(value="https://parer-svil.ente.regione.emr.it/sacerdips")
/system-property=sacerdips-refresh-check-interval:add(value="600000")
/system-property=sacerdips-store-key-name:add(value="sacerdips")
/system-property=sacerdips-jks-path:add(value="/opt/jboss-eap/certs/sacerdips.jks")
```

## Logging profile

### Hibernate custom handler
Assicurarsi di aver installato il modulo ApplicationLogCustomHandler (Vedi documentazione di configurazione di Jboss EAP 7.3).

Configurare un custom handler nel subsystem **jboss:domain:logging:1.5**.

```xml
<subsystem xmlns="urn:jboss:domain:logging:1.5">
    <!-- ... --> 
    <custom-handler name="sacerdips_jdbc_handler" class="it.eng.tools.jboss.module.logger.ApplicationLogCustomHandler" module="it.eng.tools.jboss.module.logger">
        <level name="INFO"/>
        <formatter>
            <named-formatter name="PATTERN"/>
        </formatter>
        <properties>
            <property name="fileName" value="sacerdips_jdbc.log"/>
            <property name="deployment" value="sacerdips"/>
        </properties>
    </custom-handler>
    <!-- ... -->
</subsystem>
```
I comandi CLI 

```bash 
/subsystem=logging/custom-handler=sacerdips_jdbc_handler:add(class=it.eng.tools.jboss.module.logger.ApplicationLogCustomHandler,module=it.eng.tools.jboss.module.logger,level=INFO)

/subsystem=logging/custom-handler=sacerdips_jdbc_handler:write-attribute(name=named-formatter,value=PATTERN)

/subsystem=logging/custom-handler=sacerdips_jdbc_handler:write-attribute(name=properties,value={fileName=>"sacerdips_jdbc.log", deployment=>"sacerdips"})
```

Associare l'handler ai logger **jboss.jdbc.spy** e **org.hibernate**, sempre nel subsystem **jboss:domain:logging:1.5**. 


```xml
<subsystem xmlns="urn:jboss:domain:logging:1.5">
    <!-- ... -->
    <logger category="jboss.jdbc.spy" use-parent-handlers="false">
        <level name="DEBUG"/>
        <filter-spec value="match(&quot;Statement|prepareStatement&quot;)"/>
        <handlers>
            <handler name="sacerdips_jdbc_handler"/>
        </handlers>
    </logger>
    <logger category="org.hibernate" use-parent-handlers="false">
        <level name="WARNING"/>
        <handlers>
            <handler name="sacerdips_jdbc_handler"/>
        </handlers>
    </logger>
    <!-- ... -->
</subsystem>
```

I comandi CLI

```bash
/subsystem=logging/logger=org.hibernate:add-handler(name=sacerdips_jdbc_handler)

/subsystem=logging/logger=jboss.jdbc.spy:add-handler(name=sacerdips_jdbc_handler)
```
### Profilo SACERDIPS

```xml
<logging-profiles>
    <!-- ... -->
    <logging-profile name="SACERDIPS">
        <periodic-rotating-file-handler name="sacerdips_handler">
            <level name="DEBUG"/>
            <formatter>
                <pattern-formatter pattern="%d{yyyy-MM-dd HH:mm:ss,SSS} %-5p [%c] (%t) %s%E%n"/>
            </formatter>
            <file relative-to="jboss.server.log.dir" path="sacerdips.log"/>
            <suffix value=".yyyy-MM-dd"/>
            <append value="true"/>
        </periodic-rotating-file-handler>
        <logger category="org.springframework" use-parent-handlers="true">
            <level name="INFO"/>
        </logger>
        <logger category="httpclient.wire.content" use-parent-handlers="true">
            <level name="INFO"/>
        </logger>
        <logger category="org.apache.cxf" use-parent-handlers="true">
            <level name="INFO"/>
        </logger>
        <logger category="javax.activation" use-parent-handlers="true">
            <level name="INFO"/>
        </logger>
        <logger category="org.opensaml" use-parent-handlers="true">
            <level name="INFO"/>
        </logger>
        <logger category="org.hibernate" use-parent-handlers="true">
            <level name="ERROR"/>
        </logger>
        <logger category="jboss.jdbc.spy" use-parent-handlers="true">
            <level name="ERROR"/>
        </logger>
        <root-logger>
            <level name="INFO"/>
            <handlers>
                <handler name="sacerdips_handler"/>
            </handlers>
        </root-logger>
    </logging-profile>
    <!-- ... -->
</logging-profiles>
```

#### JBoss CLI 

```bash
/subsystem=logging/logging-profile=SACERDIPS:add()
/subsystem=logging/logging-profile=SACERDIPS/periodic-rotating-file-handler=sacerdips_handler:add(level=DEBUG,formatter="%d{yyyy-MM-dd HH:mm:ss,SSS} %-5p [%c] (%t) %s%E%n",file={path="sacerdips.log",relative-to="jboss.server.log.dir"},suffix=".yyyy-MM-dd",append=true)
/subsystem=logging/logging-profile=SACERDIPS/root-logger=ROOT:add(level=INFO,handlers=[sacerdips_handler])
/subsystem=logging/logging-profile=SACERDIPS/logger=org.springframework:add(level=INFO,use-parent-handlers=true)
/subsystem=logging/logging-profile=SACERDIPS/logger=httpclient.wire.content:add(level=INFO,use-parent-handlers=true)
/subsystem=logging/logging-profile=SACERDIPS/logger=org.apache.cxf:add(level=INFO,use-parent-handlers=true)
/subsystem=logging/logging-profile=SACERDIPS/logger=javax.activation:add(level=INFO,use-parent-handlers=true)
/subsystem=logging/logging-profile=SACERDIPS/logger=org.opensaml:add(level=INFO,use-parent-handlers=true)
/subsystem=logging/logging-profile=SACERDIPS/logger=org.hibernate:add(level=ERROR,use-parent-handlers=true)
/subsystem=logging/logging-profile=SACERDIPS/logger=jboss.jdbc.spy:add(level=ERROR,use-parent-handlers=true)
```
