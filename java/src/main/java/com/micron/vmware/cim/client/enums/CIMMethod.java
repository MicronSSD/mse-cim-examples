package com.micron.vmware.cim.client.enums;

public enum CIMMethod {
	UPDATE_DEVICE_DATA("UpdateDeviceData"),
	UPDATE_FIRMWARE("UpdateFirmware"),
	SATA_SECURE_ERASE("SecureErase"),
	SAS_SANITIZE("SanitizeBlockErase");

	private final String value;

	private CIMMethod(final String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
