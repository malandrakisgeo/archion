package org.georgemalandrakis.archion.auth;

import io.dropwizard.auth.Authorizer;
import org.georgemalandrakis.archion.core.ArchionUser;

import javax.annotation.Nullable;
import javax.ws.rs.container.ContainerRequestContext;

public class BasicAuthorizer implements Authorizer<ArchionUser> {
/*
        NOTE: This version of Archion includes no Authorization or Authentication! Not yet, at least.
 */
    @Override
    public boolean authorize(ArchionUser archionUser, String s) {
        return true;
    }

    @Override
    public boolean authorize(ArchionUser principal, String role, @Nullable ContainerRequestContext requestContext) {
        return Authorizer.super.authorize(principal, role, requestContext);
    }

}
