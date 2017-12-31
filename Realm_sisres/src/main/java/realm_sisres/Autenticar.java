package realm_sisres;

import com.sun.appserv.security.AppservPasswordLoginModule;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.login.LoginException;

public class Autenticar extends AppservPasswordLoginModule
{
    private static final Logger LOGGER = Logger.getGlobal();
    
    static {
        LOGGER.setLevel(Level.FINEST);
    }
    
    @Override
    protected void authenticateUser() throws LoginException
    {
        System.err.println("Cheguei no authenticateUser");
        Realm_sisres realm = (Realm_sisres) _currentRealm;
        if (realm.authenticateUser(_username, _password))
        {
            LOGGER.fine("* Grupos: *");
            List<String> groupsList = realm.getGroupList(_username);
            String[] groups = new String[groupsList.size()];
            int i = 0;
            for (String group : groupsList)
            {
                groups[i++] = group;
                LOGGER.log(Level.FINE, "{0}:{1}", new Object[]{i, groups[i-1]});
            }            
            commitUserAuthentication(groups);
            LOGGER.fine("*************");
        } else
        {
            throw new LoginException("Invalid login!");
        }

    }

}
