package com.micron.vmware.cim.client.model;

import com.micron.vmware.cim.client.enums.DeviceType;

public class MicronDevice {

	private DeviceType deviceType;
	private String     deviceId;
	private String     serialNumber;
	private String     modelNumber;
	private String     modelFamily;
	private String     firmwareVersion;
	private Long       deviceCapacity; //CIM API returns UnsignedInteger64.
	private Long       nativeMaxAddress; //CIM API returns UnsignedInteger64.

	public DeviceType getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(DeviceType deviceType) {
		this.deviceType = deviceType;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getSerialNumber() {
		return serialNumber;
	}

	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	public String getModelNumber() {
		return modelNumber;
	}

	public void setModelNumber(String modelNumber) {
		this.modelNumber = modelNumber;
	}

	public String getModelFamily() {
		return modelFamily;
	}

	public void setModelFamily(String modelFamily) {
		this.modelFamily = modelFamily;
	}

	public String getFirmwareVersion() {
		return firmwareVersion;
	}

	public void setFirmwareVersion(String firmwareVersion) {
		this.firmwareVersion = firmwareVersion;
	}

	public Long getDeviceCapacity() {
		return deviceCapacity;
	}

	public void setDeviceCapacity(Long deviceCapacity) {
		this.deviceCapacity = deviceCapacity;
	}

	public Long getNativeMaxAddress() {
		return nativeMaxAddress;
	}

	public void setNativeMaxAddress(Long nativeMaxAddress) {
		this.nativeMaxAddress = nativeMaxAddress;
	}
}
