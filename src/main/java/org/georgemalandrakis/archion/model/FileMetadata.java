package org.georgemalandrakis.archion.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.sql.Timestamp;

@JsonIgnoreProperties(ignoreUnknown = true)
public class FileMetadata {

	@JsonProperty("Fileid")
	private String fileid;

	@JsonProperty("Originalfilename")
	private String originalfilename;

	@JsonProperty("Location")
	private String location;

	@JsonProperty("userid")
	private String userid;

	@JsonProperty("sizeinkbs")
	private String sizeinkbs;

	@JsonProperty("SHA1Hash")
	private String sha1Hash;

	@JsonProperty("Crc32Hex")
	private String crc32Hex;

	@JsonProperty("lastAccessed")
	private Timestamp lastAccessed;

	@JsonProperty("LastModified")
	private Timestamp lastmodified;

	@JsonProperty("LocalFileName")
	private String localfilename;

	@JsonProperty("Created")
	private Timestamp created;

	@JsonProperty("FileType")
	private String filetype;

	@JsonProperty("FileExtension")
	private String fileextension;

	private FileProcedurePhase phase;

	public FileMetadata() {
	}

	public String getFileid() {
		return fileid;
	}

	public void setFileid(String fileid) {
		this.fileid = fileid;
	}

	public String getOriginalfilename() {
		return originalfilename;
	}

	public void setOriginalfilename(String originalfilename) {
		this.originalfilename = originalfilename;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public void setUserid(String userid){
		this.userid = userid;
	}
	public String getUserid() {
		return userid;
	}

	public String getSizeinkbs() {
		return sizeinkbs;
	}

	public void setSizeinkbs(String sizeinkbs) {
		this.sizeinkbs = sizeinkbs;
	}


	public String getSha1Hash() {
		return sha1Hash;
	}

	public void setSha1Hash(String sha1Hash) {
		this.sha1Hash = sha1Hash;
	}

	public String getCrc32Hex() {
		return crc32Hex;
	}

	public void setCrc32Hex(String crc32Hex) {
		this.crc32Hex = crc32Hex;
	}

	public Timestamp getLastmodified() {
		return lastmodified;
	}

	public void setLastmodified(Timestamp lastmodified) {
		this.lastmodified = lastmodified;
	}

	public String getLocalfilename() {
		return localfilename;
	}

	public void setLocalfilename(String localfilename) {
		this.localfilename = localfilename;
	}

	public Timestamp getCreated() {
		return created;
	}

	public void setCreated(Timestamp created) {
		this.created = created;
	}

	public String getFiletype() {
		return filetype;
	}

	public void setFiletype(String filetype) {
		this.filetype = filetype;
	}

	public String getFileextension() {
		return fileextension;
	}

	public void setFileextension(String fileextension) {
		this.fileextension = fileextension;
	}

	public FileProcedurePhase getPhase() {
		return phase;
	}

	public void setPhase(FileProcedurePhase phase) {
		this.phase = phase;
	}

	public Timestamp getLastAccessed() {
		return lastAccessed;
	}

	public void setLastAccessed(Timestamp lastAccessed) {
		this.lastAccessed = lastAccessed;
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
}
