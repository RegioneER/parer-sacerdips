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

package it.eng.dispenser.spring;

import it.eng.dispenser.web.action.GestioneJobAction;
import it.eng.dispenser.web.action.HomeAction;
import it.eng.dispenser.web.action.LUMAction;
import it.eng.dispenser.web.action.NoteRilascioAction;
import it.eng.dispenser.web.action.PABAction;
import it.eng.dispenser.web.action.PUGAction;
import it.eng.dispenser.web.action.PievesestinaAction;
import it.eng.dispenser.web.action.RicercaAction;
import it.eng.dispenser.web.action.SISMAAction;
import it.eng.dispenser.web.action.SceltaOrganizzazioneAction;
import it.eng.dispenser.web.security.SacerdipsAuthenticator;
import it.eng.dispenser.web.util.ApplicationBasePropertiesSeviceImpl;
import it.eng.paginator.ejb.PaginatorImpl;
import it.eng.parer.dispenser.util.CustomPlaceholder;
import it.eng.parer.dispenser.util.DataSourcePropertiesFactoryBean;
import it.eng.spagoLite.actions.RedirectAction;
import it.eng.spagoLite.actions.security.LoginAction;
import it.eng.spagoLite.actions.security.LogoutAction;
import java.util.HashMap;
import java.util.Map;
import javax.naming.NamingException;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.Ordered;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.jdbc.datasource.lookup.JndiDataSourceLookup;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.orm.jpa.support.PersistenceAnnotationBeanPostProcessor;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.jta.JtaTransactionManager;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.JstlView;

/**
 *
 * @author Marco Iacolucci
 */
@EnableWebMvc
@EnableTransactionManagement
@ComponentScan(basePackages = { "it.eng.dispenser.web", "it.eng.dispenser.component", "it.eng.dispenser.ws",
        "it.eng.dispenser.spring", "it.eng.dispenser.web.action", "it.eng.dispenser.slite.gen.action",
        "it.eng.spagoCore", "it.eng.spagoLite" })
@Configuration
public class WebMvcConfiguration implements WebMvcConfigurer {

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        /*
         * qui si dichiarano le risorse statiche
         */
        registry.addResourceHandler("/css/**", "/images/**", "/img/**", "/js/**", "/webjars/**")
                .addResourceLocations("/css/", "/images/", "/img/", "/js/", "/webjars/").setCachePeriod(0);
        registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
    }

    @Bean
    public InternalResourceViewResolver resolver() {
        InternalResourceViewResolver resolver = new InternalResourceViewResolver();
        resolver.setViewClass(JstlView.class);
        resolver.setPrefix("/jsp/");
        resolver.setSuffix(".jsp");
        resolver.setExposedContextBeanNames("ricercheLoader");
        return resolver;
    }

    @Bean(name = "paginator")
    PaginatorImpl paginatorImpl() {
        return new PaginatorImpl();
    }

    /*
     * Template da inserire nelle applicazioni che usano SpagoLite e che utilizzano l' Help On line. Deve implementare
     * l'interfaccia IApplicationBasePropertiesSevice
     *
     */
    @Bean
    ApplicationBasePropertiesSeviceImpl applicationBasePropertiesSeviceImpl() {
        return new ApplicationBasePropertiesSeviceImpl();
    }

    /*
     * MEV#27457 - Rendere indipendente SIAM da PING Template per chiamate rest con impostazione del timeout
     */
    @Bean
    RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory c = new SimpleClientHttpRequestFactory();
        c.setReadTimeout(15000);
        c.setConnectTimeout(15000);
        return new RestTemplate(c);
    }

    /*
     * Classe che va a caricare le autorizzazioni da IAM
     */
    @Bean(name = "authenticator")
    SacerdipsAuthenticator sacerdipsAuthenticator() {
        return new SacerdipsAuthenticator();
    }

    /*
     * Serve per parametrizzare l'applicazione specifica per esempio per caricare le variabili di sistema che hanno come
     * suffisso ad esempio "saceriam".
     */
    @Bean
    String nomeApplicazione() {
        return "sacerdips";
    }

    @Bean
    public EntityManagerFactory entityManagerFactory() throws NamingException {
        JndiObjectFactoryBean jndiFactoryBean = new JndiObjectFactoryBean();
        jndiFactoryBean.setJndiName("java:comp/env/jpa/DispenserJPA");
        jndiFactoryBean.setProxyInterface(EntityManagerFactory.class);
        jndiFactoryBean.afterPropertiesSet();
        return (EntityManagerFactory) jndiFactoryBean.getObject();
    }

    @Bean(name = "transactionManager")
    public JtaTransactionManager transactionManager() {
        JtaTransactionManager jta = new JtaTransactionManager();
        return jta;
    }

    @Bean(name = "em")
    public PersistenceAnnotationBeanPostProcessor persistenceAnnotationBeanPostProcessor() {
        PersistenceAnnotationBeanPostProcessor pe = new PersistenceAnnotationBeanPostProcessor();
        pe.setResourceRef(true);
        Map<String, String> persistenceUnits = new HashMap<>();
        persistenceUnits.put("DispenserJPA", "jpa/DispenserJPA");
        pe.setPersistenceUnits(persistenceUnits);
        return pe;
    }

    @Bean
    public DataSource dataSource() throws NamingException {
        JndiDataSourceLookup j = new JndiDataSourceLookup();
        return j.getDataSource("jboss/datasources/DispenserDs");
    }

    @Bean
    public DataSourcePropertiesFactoryBean applicationProperties(DataSource dataSource) {
        DataSourcePropertiesFactoryBean d = new DataSourcePropertiesFactoryBean();
        d.setDataSource(dataSource);
        return d;
    }

    @Bean
    public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer(DataSourcePropertiesFactoryBean ds)
            throws Exception {
        return new CustomPlaceholder(ds.getObject());
    }

    /*
     * CONFIGURAZIONE DEI BEAN DELLE ACTION che prima erano nell'xml di springweb Configurazione delle action ereditate
     * dal framework
     */
    @Bean(value = "/View.html")
    RedirectAction redirectAction() {
        return new RedirectAction();
    }

    @Bean(value = "/Login.html")
    LoginAction loginAction() {
        return new LoginAction();
    }

    @Bean(value = "/Logout.html")
    LogoutAction logoutAction() {
        return new LogoutAction();
    }

    /* Configurazione delle action specifiche del modulo web */
    @Bean(value = "/Home.html")
    HomeAction homeAction() {
        return new HomeAction();
    }

    @Bean(value = "/SceltaOrganizzazione.html")
    SceltaOrganizzazioneAction sceltaOrganizzazioneAction() {
        return new SceltaOrganizzazioneAction();
    }

    @Bean(value = "/NoteRilascio.html")
    NoteRilascioAction noteRilascioAction() {
        return new NoteRilascioAction();
    }

    @Bean(value = "/Ricerca.html")
    RicercaAction ricercaAction() {
        return new RicercaAction();
    }

    @Bean(value = "/GestioneJob.html")
    GestioneJobAction gestioneJobAction() {
        return new GestioneJobAction();
    }

    @Bean(value = "/LUM.html")
    LUMAction lUMAction() {
        return new LUMAction();
    }

    @Bean(value = "/PAB.html")
    PABAction pABAction() {
        return new PABAction();
    }

    @Bean(value = "/PUG.html")
    PUGAction pUGAction() {
        return new PUGAction();
    }

    @Bean(value = "/SISMA.html")
    SISMAAction sISMAAction() {
        return new SISMAAction();
    }

    @Bean(value = "/Pievesestina.html")
    PievesestinaAction pievesestinaAction() {
        return new PievesestinaAction();
    }

}
