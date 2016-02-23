package com.micron.vmware.cim.client.enums;

public enum CIMStatus {
	CIM_STATUS_SUCCESS(0),
	CIM_STATUS_INVALID_ARGUMENT(1),
	CIM_STATUS_FAIL(2);

	private int cimStatusCode;

	CIMStatus(int value) {
		this.cimStatusCode = cimStatusCode;
	}

	public int getCimStatusCode() {
		return cimStatusCode;
	}
}
