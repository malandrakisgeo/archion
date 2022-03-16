package org.georgemalandrakis.archion.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import java.security.Principal;

public class ArchionUser implements Principal {
	@JsonProperty("Id")
	@NotNull
	private String id;

	@JsonProperty("Email")
	@NotNull
	private String email;

	@JsonProperty("Name")
	@NotNull
	private String name;

	@JsonIgnore
	private String password;

	@JsonIgnore
	private String salt;


	public ArchionUser() {
	}

	public String getId() {
		return this.id;
	}
	public void setId(String id) {
		this.id = id;
	}

	public String getEmail() {
		return this.email;
	}
	public void setEmail(String email) {
		this.email = email;
	}

	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return this.password;
	}
	public void setPassword(String password) {
		this.password = password;
	}

	public String getSalt() {
		return this.salt;
	}
	public void setSalt(String salt) {
		this.salt = salt;
	}



}
