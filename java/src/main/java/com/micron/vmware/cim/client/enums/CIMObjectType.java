package com.micron.vmware.cim.client.enums;

public enum CIMObjectType {
	MICRON_DEVICE("MICRON_Device"),
	MICRON_SATA_DEVICE("MICRON_SATADevice"),
	MICRON_SAS_DEVICE("MICRON_SASDevice");

	private final String value;

	private CIMObjectType(final String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}
}
