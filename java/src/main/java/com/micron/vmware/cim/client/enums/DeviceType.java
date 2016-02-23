package com.micron.vmware.cim.client.enums;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum DeviceType {
	MICRON_SATADevice(0),
	MICRON_SASDevice(1);

	private int deviceTypeCode;
	private static final Map<Integer, com.micron.vmware.cim.client.enums.DeviceType> lookup = new HashMap<Integer, com.micron.vmware.cim.client.enums.DeviceType>();

	DeviceType(int deviceTypeCode) {
		this.deviceTypeCode = deviceTypeCode;
	}

	static {
		for (com.micron.vmware.cim.client.enums.DeviceType tcgType : EnumSet
				.allOf(com.micron.vmware.cim.client.enums.DeviceType.class)) {
			lookup.put(tcgType.getTcgTypeCode(), tcgType);
		}
	}

	public static com.micron.vmware.cim.client.enums.DeviceType getTCGType(int type) {
		return lookup.get(type);
	}

	public int getTcgTypeCode() {
		return deviceTypeCode;
	}
}
