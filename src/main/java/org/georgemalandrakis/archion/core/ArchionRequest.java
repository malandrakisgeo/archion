package org.georgemalandrakis.archion.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.georgemalandrakis.archion.model.FileMetadata;

import java.io.IOException;
import java.sql.Connection;
import java.util.Date;
import java.util.Locale;
import java.util.UUID;

public class ArchionRequest {

	@JsonProperty("Session")
	private String session;

	@JsonProperty("Expires")
	private Date expires;

	@JsonProperty("User")
	private ArchionUser archionUser;

	@JsonProperty("FileMetadata")
	FileMetadata initialMetadata;

	@JsonIgnore
	private Locale baseLanguage;

	@JsonIgnore
	private Connection connection;

	@JsonIgnore
	private ArchionResponse archionResponse;


	public ArchionRequest() {
		this.archionResponse = new ArchionResponse();
	}

	//Used when parsing from JSON
	public ArchionRequest( String jsonRequest) { //DON'T DELETE!
		this.archionResponse = new ArchionResponse();
		this.selfFromJSON(jsonRequest); //TODO: Check it
	}

	public ArchionRequest(Integer expirationIntervall) {
		this.session = UUID.randomUUID().toString();

		Long expirationDate = System.currentTimeMillis() + (new Long(expirationIntervall) * 1000L);
		this.expires = new Date(expirationDate);

		this.archionResponse = new ArchionResponse();
	}

	@JsonIgnore
	public String toJSON() {
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = "";
		try {
			jsonString = mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
		}

		return jsonString;
	}

	public String getSession() {
		return this.session;
	}

	public void setSession(String session) {
		this.session = session;
	}

	public Date getExpires() {
		return this.expires;
	}
	public void setExpires(Date expires) {
		this.expires = expires;
	}

	public ArchionUser getUserObject() {
		return this.archionUser;
	}
	public void setUserObject(ArchionUser archionUser) {
		this.archionUser = archionUser;
	}

	public Locale getBaseLanguage() {
		return this.baseLanguage;
	}
	public void setBaseLanguage(Locale baseLanguage) {
		this.baseLanguage = baseLanguage;
	}

	public Connection getConnection() {
		return this.connection;
	}
	public void setConnection(Connection connection) {
		this.connection = connection;
	}


	public ArchionResponse getResponseObject() {
		return this.archionResponse;
	}
	public void setResponseObject(ArchionResponse archionResponse) {
		this.archionResponse = archionResponse;
	}

	public FileMetadata getInitialMetadata() {
		return initialMetadata;
	}


	private void selfFromJSON(String json){
		ArchionRequest request = fromJSON(json);
		this.setConnection(request.getConnection());
		this.setUserObject(request.getUserObject());
		this.setExpires(request.getExpires());
		this.setSession(request.getSession());
	}

	public static ArchionRequest fromJSON(String json) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			 return mapper.readValue(json, ArchionRequest.class);
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (JsonParseException e) {
			System.out.println(e.getMessage());
			String ee = e.getMessage();
			e.printStackTrace();
		} catch (IOException e) {
		}
	return null;

	}
}