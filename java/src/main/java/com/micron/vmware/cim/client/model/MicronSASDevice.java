package com.micron.vmware.cim.client.model;

public class MicronSASDevice extends MicronDevice {

	private Integer           sasLinkSpeed;
	private Integer           currentTemperature;
	private Integer           powerCycleCount; //CIM API returns UnsignedInteger32
	private Integer           lifetimeUsedPercentage;
	private Integer powerOnHours; //CIM API returns UnsignedInteger32

	public Integer getSasLinkSpeed() {
		return sasLinkSpeed;
	}

	public void setSasLinkSpeed(Integer sasLinkSpeed) {
		this.sasLinkSpeed = sasLinkSpeed;
	}

	public Integer getCurrentTemperature() {
		return currentTemperature;
	}

	public void setCurrentTemperature(Integer currentTemperature) {
		this.currentTemperature = currentTemperature;
	}

	public Integer getPowerCycleCount() {
		return powerCycleCount;
	}

	public void setPowerCycleCount(Integer powerCycleCount) {
		this.powerCycleCount = powerCycleCount;
	}

	public Integer getLifetimeUsedPercentage() {
		return lifetimeUsedPercentage;
	}

	public void setLifetimeUsedPercentage(Integer lifetimeUsedPercentage) {
		this.lifetimeUsedPercentage = lifetimeUsedPercentage;
	}

	public Integer getPowerOnHours() {
		return powerOnHours;
	}

	public void setPowerOnHours(Integer powerOnHours) {
		this.powerOnHours = powerOnHours;
	}
}
