/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package logingooglerealm;

import com.sun.appserv.security.AppservPasswordLoginModule;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.security.auth.login.LoginException;

public class GoogleLoginModule extends AppservPasswordLoginModule
{

    private static final Logger LOGGER = Logger.getGlobal();

    static {
        LOGGER.setLevel(Level.FINEST);
    }

    @Override
    protected void authenticateUser() throws LoginException
    {
        System.err.println("Cheguei no authenticateUser");
        GoogleRealm realm = (GoogleRealm) _currentRealm;
        System.err.println("SENHAAAA: " + _password);
        if (realm.authenticateUser(_username, _password)) {
            LOGGER.fine("* Grupos: *");
            List<String> groupsList = realm.getGroupList(_username);
            String[] groups = new String[groupsList.size()];
            int i = 0;
            for (String group : groupsList) {
                groups[i++] = group;
                LOGGER.log(Level.FINE, "{0}:{1}", new Object[]{i, groups[i - 1]});
            }
            commitUserAuthentication(groups);
            LOGGER.fine("*************");
        }
        else {
            throw new LoginException("Invalid login!");
        }

    }
}
