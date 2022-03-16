package org.georgemalandrakis.archion.core;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;

public class ArchionResponse {
	@JsonProperty("Data")
	private Object data;

	@JsonProperty("Info")
	private Object info;

	@JsonProperty("Notification")
	private List<ArchionNotification> notification;

	public ArchionResponse() {
		this.notification = new ArrayList<>();
	}

	public Object getData() {
		return this.data;
	}
	public void setData(Object data) {
		this.data = data;
	}

	public Object getInfo() {
		return this.info;
	}
	public void setInfo(Object info) {
		this.info = info;
	}

	public List<ArchionNotification> getNotification() {
		return this.notification;
	}
	public void setNotification(List<ArchionNotification> notification) {
		this.notification = notification;
	}
	public Boolean hasNotification() {
		return (this.notification.size() > 0 ? true : false);
	}

	public Boolean hasType(ArchionNotification.NotificationType type) {
		for (ArchionNotification archionNotification : this.getNotification()) {
			if (archionNotification.getType() == type) {
				return true;
			}
		}
		return false;
	}

	public void addError(String name, String message) {
		ArchionNotification archionNotification = new ArchionNotification(ArchionNotification.NotificationType.error, name, message);
		this.getNotification().add(archionNotification);
	}

	public void addError(String name, String message, Integer index) {
		ArchionNotification archionNotification = new ArchionNotification(ArchionNotification.NotificationType.error, name, message, index);
		this.getNotification().add(archionNotification);
	}

	public void addDebug(String name, String message) {
		ArchionNotification archionNotification = new ArchionNotification(ArchionNotification.NotificationType.debug, name, message);
		this.getNotification().add(archionNotification);
	}

	public void addDebug(String name, String message, Integer index) {
		ArchionNotification archionNotification = new ArchionNotification(ArchionNotification.NotificationType.debug, name, message, index);
		this.getNotification().add(archionNotification);
	}

	public void addSuccess(String name, String message) {
		ArchionNotification archionNotification = new ArchionNotification(ArchionNotification.NotificationType.success, name, message);
		this.getNotification().add(archionNotification);
	}

	public void addSuccess(String name, String message, Integer index) {
		ArchionNotification archionNotification = new ArchionNotification(ArchionNotification.NotificationType.success, name, message, index);
		this.getNotification().add(archionNotification);
	}

	public void addWarning(String name, String message) {
		ArchionNotification archionNotification = new ArchionNotification(ArchionNotification.NotificationType.warning, name, message);
		this.getNotification().add(archionNotification);
	}

	public void addWarning(String name, String message, Integer index) {
		ArchionNotification archionNotification = new ArchionNotification(ArchionNotification.NotificationType.warning, name, message, index);
		this.getNotification().add(archionNotification);
	}

	public void addInformation(String name, String message) {
		ArchionNotification archionNotification = new ArchionNotification(ArchionNotification.NotificationType.info, name, message);
		this.getNotification().add(archionNotification);
	}

	public void addInformation(String name, String message, Integer index) {
		ArchionNotification archionNotification = new ArchionNotification(ArchionNotification.NotificationType.info, name, message, index);
		this.getNotification().add(archionNotification);
	}

	public void addSystem(String name, String message) {
		ArchionNotification archionNotification = new ArchionNotification(ArchionNotification.NotificationType.system, name, message);
		this.getNotification().add(archionNotification);
	}

	public void addSystem(String name, String message, Integer index) {
		ArchionNotification archionNotification = new ArchionNotification(ArchionNotification.NotificationType.system, name, message, index);
		this.getNotification().add(archionNotification);
	}

	public void addBlocking(String name, String message) {
		ArchionNotification archionNotification = new ArchionNotification(ArchionNotification.NotificationType.blocking, name, message);
		this.getNotification().add(archionNotification);
	}

	public void addBlocking(String name, String message, Integer index) {
		ArchionNotification archionNotification = new ArchionNotification(ArchionNotification.NotificationType.blocking, name, message, index);
		this.getNotification().add(archionNotification);
	}

	public void massManagement(String name, String message){
		int errorCounter = 0;
		for (ArchionNotification archionNotification : this.notification) {
			if(archionNotification.getType() == ArchionNotification.NotificationType.error || archionNotification.getType() == ArchionNotification.NotificationType.blocking){
				errorCounter++;
			}
		}
		//No errors, everything went well
		if(errorCounter == 0){
			this.notification.clear();
			addSuccess(name, message);
		}
	}
}
