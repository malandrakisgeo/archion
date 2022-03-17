package org.georgemalandrakis.archion.auth;

import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;
import org.georgemalandrakis.archion.core.ArchionUser;
import org.georgemalandrakis.archion.dao.UserDAO;

import java.util.Optional;

public class BasicAuthenticator implements Authenticator<BasicCredentials, ArchionUser> {
    /*
        NOTE: This version of Archion includes no Authorization or Authentication! Not yet, at least.
    */

    private final UserDAO userDAO;

    public BasicAuthenticator(UserDAO userDAO) {
        this.userDAO = userDAO;

    }

    @Override
    public Optional<ArchionUser> authenticate(BasicCredentials basicCredentials) throws AuthenticationException {

        return Optional.empty();
    }

}
