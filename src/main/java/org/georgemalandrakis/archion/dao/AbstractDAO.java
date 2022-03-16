package org.georgemalandrakis.archion.dao;

import org.georgemalandrakis.archion.core.ConnectionManager;
import org.georgemalandrakis.archion.core.ArchionRequest;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class AbstractDAO {
    protected final ConnectionManager connectionObject;

    public AbstractDAO(ConnectionManager connectionObject) {
        this.connectionObject = connectionObject;
    }


    public Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection("jdbc:postgresql://" + this.connectionObject.getBaseServer() + "/" + this.connectionObject.getBaseName(), this.connectionObject.getBaseUsername(), this.connectionObject.getBasePassword());
        connection.setAutoCommit(false);
        connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);


        return connection;
    }

    public Connection getBaseConnection() throws SQLException {
        if (this.connectionObject.getBaseConnection() == null) {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://" + this.connectionObject.getBaseServer() + "/" + this.connectionObject.getBaseName(), this.connectionObject.getBaseUsername(), this.connectionObject.getBasePassword());
            this.connectionObject.setBaseConnection(connection);
        }
        return this.connectionObject.getBaseConnection();
    }

    public Connection getConnection(ArchionRequest archionRequest) throws SQLException {
        if (archionRequest.getConnection() == null) {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://" + this.connectionObject.getBaseServer() + "/" + this.connectionObject.getBaseName(), this.connectionObject.getBaseUsername(), this.connectionObject.getBasePassword());
            connection.setAutoCommit(false);
            connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
            archionRequest.setConnection(connection);
        }
        return archionRequest.getConnection();
    }

}
