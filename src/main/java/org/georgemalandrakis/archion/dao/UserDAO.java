package org.georgemalandrakis.archion.dao;

import org.georgemalandrakis.archion.core.ConnectionManager;

public class UserDAO extends AbstractDAO {
    public UserDAO(ConnectionManager connectionObject) {
        super(connectionObject);
    }

    /*
        NOTE: This version of Archion does not (yet) use Authentication, so everything regarding Users has been removed.
     */


}
