package org.georgemalandrakis.archion.dao;

import org.georgemalandrakis.archion.core.ConnectionManager;
import org.georgemalandrakis.archion.core.ArchionRequest;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class AbstractDAO {
    protected final ConnectionManager connectionObject;

    public AbstractDAO(ConnectionManager connectionObject) {
        this.connectionObject = connectionObject;
    }


    public Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:postgresql://" + this.connectionObject.getBaseServer() + "/" + this.connectionObject.getBaseName(),
                this.connectionObject.getBaseUsername(), this.connectionObject.getBasePassword());

        //Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/archion?user=postgres&password=postgres"); //works
        connection.setAutoCommit(true); //TODO: Perhaps unsafe
        connection.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);


        return connection;
    }


    public Connection getConnection(ArchionRequest archionRequest) throws SQLException {
        if (archionRequest.getConnection() == null) {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://" + this.connectionObject.getBaseServer() + "/" + this.connectionObject.getBaseName(), this.connectionObject.getBaseUsername(), this.connectionObject.getBasePassword());
            connection.setAutoCommit(true);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            archionRequest.setConnection(connection);
        }
        return archionRequest.getConnection();
    }

}
