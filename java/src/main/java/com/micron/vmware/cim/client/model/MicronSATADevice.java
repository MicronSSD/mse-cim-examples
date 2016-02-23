package com.micron.vmware.cim.client.model;

import java.util.ArrayList;
import java.util.List;

public class MicronSATADevice extends MicronDevice {

	private String devfsPath;
	private Integer     tcgStatus; //CIM client returns UnsignedInteger8
	private Integer     tcgssc; //CIM client returns UnsignedInteger8
	private Boolean              sanitizeSupported;
	private String               sataLinkSpeed;
	private List<SMARTAttribute> smartAttributes;

	public MicronSATADevice() {
		super();
		smartAttributes = new ArrayList<SMARTAttribute>();
	}

	public String getDevfsPath() {
		return devfsPath;
	}

	public void setDevfsPath(String devfsPath) {
		this.devfsPath = devfsPath;
	}

	public Integer getTcgStatus() {
		return tcgStatus;
	}

	public void setTcgStatus(Integer tcgStatus) {
		this.tcgStatus = tcgStatus;
	}

	public Integer getTcgssc() {
		return tcgssc;
	}

	public void setTcgssc(Integer tcgssc) {
		this.tcgssc = tcgssc;
	}

	public Boolean getSanitizeSupported() {
		return sanitizeSupported;
	}

	public void setSanitizeSupported(Boolean sanitizeSupported) {
		this.sanitizeSupported = sanitizeSupported;
	}

	public String getSataLinkSpeed() {
		return sataLinkSpeed;
	}

	public void setSataLinkSpeed(String sataLinkSpeed) {
		this.sataLinkSpeed = sataLinkSpeed;
	}

	public List<SMARTAttribute> getSmartAttributes() {
		return smartAttributes;
	}

	public void setSmartAttributes(List<SMARTAttribute> smartAttributes) {
		this.smartAttributes = smartAttributes;
	}
}
