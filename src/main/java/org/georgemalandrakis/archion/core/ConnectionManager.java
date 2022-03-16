package org.georgemalandrakis.archion.core;

import redis.clients.jedis.JedisPool;

import java.sql.Connection;
import java.util.Locale;

public class ConnectionManager {
	private JedisPool jedisPool;
	private Connection baseConnection;
	private Locale baseLanguage;

	private String baseName = "";
	private String baseServer = "";
	private String baseUsername = "";
	private String basePassword = "";

	private String databaseServer = "";
	private String databaseUsername = "";
	private String databasePassword = "";

	private String amazonSecretkey = "";
	private String amazonAccesskey = "";

	private String localMachineFolder = "";

	public ConnectionManager() {

	}

	public JedisPool getJedisPool() {
		return this.jedisPool;
	}

	public void setJedisPool(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}

	public Connection getBaseConnection() {
		return this.baseConnection;
	}

	public void setBaseConnection(Connection baseConnection) {
		this.baseConnection = baseConnection;
	}

	public String getBaseServer() {
		return this.baseServer;
	}

	public void setBaseServer(String baseServer) {
		this.baseServer = baseServer;
	}

	public String getBaseName() {
		return this.baseName;
	}

	public void setBaseName(String baseName) {
		this.baseName = baseName;
	}

	public String getBaseUsername() {
		return this.baseUsername;
	}

	public void setBaseUsername(String baseUsername) {
		this.baseUsername = baseUsername;
	}

	public String getBasePassword() {
		return this.basePassword;
	}

	public void setBasePassword(String basePassword) {
		this.basePassword = basePassword;
	}

	public Locale getBaseLanguage() {
		return this.baseLanguage;
	}

	public void setBaseLanguage(Locale baseLanguage) {
		this.baseLanguage = baseLanguage;
	}

	public String getAmazonSecretkey() {
		return amazonSecretkey;
	}

	public void setAmazonSecretkey(String amazonSecretkey) {
		this.amazonSecretkey = amazonSecretkey;
	}

	public String getAmazonAccesskey() {
		return amazonAccesskey;
	}

	public void setAmazonAccesskey(String amazonAccesskey) {
		this.amazonAccesskey = amazonAccesskey;
	}

	public String getLocalMachineFolder() {
		return localMachineFolder;
	}

	public void setLocalMachineFolder(String localMachineFolder) {
		this.localMachineFolder = localMachineFolder;
	}
}
