package it.eng.dispenser.spring;

import it.eng.spagoLite.spring.CustomSaml2AuthenticationSuccessHandler;
import it.eng.dispenser.grantedEntity.UsrUser;
import it.eng.dispenser.util.ejb.LoginLogHelper;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.EJB;
import org.springframework.stereotype.Component;

/**
 *
 * @author Marco Iacolucci
 */
@Component
public class SacerdipsSaml2AuthenticationSuccessHandler extends CustomSaml2AuthenticationSuccessHandler {

    @EJB(mappedName = "java:app/Dispenser-ejb/LoginLogHelper")
    private LoginLogHelper loginLogHelper;

    @Override
    protected List<UtenteDb> findUtentiPerCodiceFiscale(String codiceFiscale) {
        ArrayList<UtenteDb> al = new ArrayList<>();
        List<UsrUser> l = loginLogHelper.findByCodiceFiscale(codiceFiscale);
        for (UsrUser usrUser : l) {
            UtenteDb u = new UtenteDb();
            u.setId(usrUser.getIdUserIam());
            u.setCodiceFiscale(usrUser.getCdFisc());
            u.setDataScadenzaPassword(u.getDataScadenzaPassword());
            u.setUsername(usrUser.getNmUserid());
            al.add(u);
        }
        return al;
    }

    @Override
    protected UtenteDb getUtentePerUsername(String username) {
        UsrUser usrUser = loginLogHelper.findUser(username);
        UtenteDb u = new UtenteDb();
        u.setId(usrUser.getIdUserIam());
        u.setCodiceFiscale(usrUser.getCdFisc());
        u.setDataScadenzaPassword(u.getDataScadenzaPassword());
        u.setUsername(usrUser.getNmUserid());
        return u;
    }

    @Override
    protected List<UtenteDb> findUtentiPerUsernameCaseInsensitive(String username) {
        List<UtenteDb> al = new ArrayList<>();
        List<UsrUser> l = loginLogHelper.findUtentiPerUsernameCaseInsensitive(username);
        for (UsrUser usrUser : l) {
            UtenteDb u = new UtenteDb();
            u.setId(usrUser.getIdUserIam());
            u.setCodiceFiscale(usrUser.getCdFisc());
            u.setDataScadenzaPassword(u.getDataScadenzaPassword());
            u.setUsername(usrUser.getNmUserid());
            al.add(u);
        }
        return al;
    }

}
