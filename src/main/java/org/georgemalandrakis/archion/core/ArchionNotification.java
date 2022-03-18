package org.georgemalandrakis.archion.core;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ArchionNotification {
	public enum NotificationType { info, warning, error, success, debug }

	@JsonProperty("Type")
	private NotificationType type;

	@JsonProperty("Message")
	private String message;

	@JsonProperty("Name")
	private String name;

	@JsonProperty("Index")
	private Integer index = -1;

	public ArchionNotification() {
	}

	public ArchionNotification(NotificationType type, String name, String message) {
		this.setType(type);
		this.setName(name);
		this.setMessage(message);
	}

	public ArchionNotification(NotificationType type, String name, String message, Integer index) {
		this.setType(type);
		this.setName(name);
		this.setMessage(message);
		this.setIndex(index);
	}

	public NotificationType getType() {
		return this.type;
	}
	public void setType(NotificationType type) {
		this.type = type;
	}

	public String getMessage() {
		return this.message;
	}
	public void setMessage(String message) {
		this.message = message;
	}

	public String getName() {
		return this.name;
	}
	public void setName(String name) {
		this.name = name;
	}

	public Integer getIndex() {
		return this.index;
	}
	public void setIndex(Integer index) {
		this.index = index;
	}
}
