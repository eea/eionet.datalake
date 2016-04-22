package eionet.datalake.dao;

import eionet.datalake.model.UserRole;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;

/**
 * Create the initial user.
 */
public class InitialUser {

    /** * Service for user management.  */
    private UserManagementService userManagementService;

    public void setUserManagementService(UserManagementService userManagementService) {
        this.userManagementService = userManagementService;
    }

    /** Inital username. Injected from configuration. */
    private String initialUsername;

    public void setInitialUsername(String initialUsername) {
        this.initialUsername = initialUsername;
    }

    /** Inital user's password. Injected from configuration. */
    private String initialPassword;

    public void setInitialPassword(String initialPassword) {
        this.initialPassword = initialPassword;
    }

    private Log logger = LogFactory.getLog(InitialUser.class);

    /**
     * Adds new user to database when bean is constructed. In the XML configuration
     * for the bean add the attribute init-method="createUser".
     */
    public void createUser() {
        if (initialUsername == null || initialUsername.trim().equals("")) {
            logger.info("No initial user to create");
            return;
        }
        if (!userManagementService.userExists(initialUsername)) {
            ArrayList<SimpleGrantedAuthority> grantedAuthorities = new ArrayList<SimpleGrantedAuthority>(1);
            for (UserRole authority : UserRole.values()) {
                grantedAuthorities.add(new SimpleGrantedAuthority(authority.toString()));
            }
            User userDetails = new User(initialUsername, initialPassword, grantedAuthorities);
            userManagementService.createUser(userDetails);
            logger.info("Initial user " + initialUsername + " created");
        } else {
            logger.info("Initial user " + initialUsername + " exists already");
        }
    }

}
