package org.georgemalandrakis.archion;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.ImmutableMap;
import io.dropwizard.Configuration;
import io.dropwizard.jobs.JobConfiguration;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Map;

public class Config extends Configuration  implements JobConfiguration {
	public static final String BASE_SERVER = "server";
	public static final String BASE_NAME = "name";
	public static final String BASE_USERNAME = "username";
	public static final String BASE_PASSWORD = "password";

	public static final String JEDIS_SERVER = "server";

	public static final String LANGUAGE_BASE = "base";


	public static final String LOGSETTINGS_MAXFILESIZE = "logfilemaxsize";


	public static final String AMAZON_ACCESS_KEY = "accesskey";
	public static final String AMAZON_SECRET_KEY = "secretkey";
	public static final String LOCAL_FILE_LOCATION = "localfilelocation";


	@NotNull
	private Map<String, String> base = Collections.emptyMap();

	@NotNull
	private Map<String, String> jedis = Collections.emptyMap();

	@NotNull
	private Map<String, String> language = Collections.emptyMap();

	@NotNull
	private Map<String, String> logsettings = Collections.emptyMap();

	@NotNull
	private Map<String, Map<String, String>> logs = Collections.emptyMap();

	@NotNull
	private Map<String, String> filedb = Collections.emptyMap();

	@NotNull
	private Map<String, String> amazon = Collections.emptyMap();

	@NotNull
	private Map<String, String> localmachine = Collections.emptyMap();


	@JsonProperty
	public Map<String, String> getBase() {
		return this.base;
	}

	@JsonProperty
	public void setBase(Map<String, String> baseConfig) {
		ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
		for (Map.Entry<String, String> entry : baseConfig.entrySet()) {
			builder.put(entry.getKey(), entry.getValue());
		}
		this.base = builder.build();
	}

	@JsonProperty
	public Map<String, String> getJedis() {
		return this.jedis;
	}

	@JsonProperty
	public void setJedis(Map<String, String> jedisConfig) {
		ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
		for (Map.Entry<String, String> entry : jedisConfig.entrySet()) {
			builder.put(entry.getKey(), entry.getValue());
		}
		this.jedis = builder.build();
	}

	@JsonProperty
	public Map<String, String> getLanguage() {
		return this.language;
	}

	@JsonProperty
	public void setLanguage(Map<String, String> languageConfig) {
		ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
		for (Map.Entry<String, String> entry : languageConfig.entrySet()) {
			builder.put(entry.getKey(), entry.getValue());
		}
		this.language = builder.build();
	}

	@JsonProperty
	public Map<String, String> getLogsettings() {
		return this.logsettings;
	}

	@JsonProperty
	public void setLogsettings(Map<String, String> logsettingsConfig) {
		ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
		for (Map.Entry<String, String> entry : logsettingsConfig.entrySet()) {
			builder.put(entry.getKey(), entry.getValue());
		}
		this.logsettings = builder.build();
	}

	@JsonProperty
	public Map<String, Map<String, String>> getLogs() {
		return this.logs;
	}

	@JsonProperty
	public void setLogs(Map<String, Map<String, String>> logsConfig) {
		ImmutableMap.Builder<String, Map<String, String>> builder = ImmutableMap.builder();
		for (Map.Entry<String, Map<String, String>> entry : logsConfig.entrySet()) {
			builder.put(entry.getKey(), ImmutableMap.copyOf(entry.getValue()));
		}
		this.logs = builder.build();
	}


	@JsonProperty("filedb")
	public Map<String, String> getFiledatabase() {
		return this.filedb;
	}

	@JsonProperty("filedb")
	public void setFiledatabase(Map<String, String> filedatabaseConfig) {
		ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
		for (Map.Entry<String, String> entry : filedatabaseConfig.entrySet()) {
			builder.put(entry.getKey(), entry.getValue());
		}
		this.filedb = builder.build();
	}

	@JsonProperty("amazon")
	public Map<String, String> getAmazon() {
		return this.amazon;
	}

	@JsonProperty("amazon")
	public void setAmazon(Map<String, String> amazonConfig) {
		ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
		for (Map.Entry<String, String> entry : amazonConfig.entrySet()) {
			builder.put(entry.getKey(), entry.getValue());
		}
		this.amazon = builder.build();
	}

	@JsonProperty("localmachine")
	public void setLocalmachine(Map<String, String> amazonConfig) {
		ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
		for (Map.Entry<String, String> entry : amazonConfig.entrySet()) {
			builder.put(entry.getKey(), entry.getValue());
		}
		this.localmachine = builder.build();
	}


	@JsonProperty("localmachine")
	public Map<String, String> getLocalMachineFolder() {
		return this.localmachine;
	}

}