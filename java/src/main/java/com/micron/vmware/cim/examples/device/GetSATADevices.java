package com.micron.vmware.cim.examples.device;

import com.micron.vmware.cim.client.enums.CIMObjectType;
import com.micron.vmware.cim.client.host.CIMHost;
import com.micron.vmware.cim.client.host.CIMHostUser;
import com.micron.vmware.cim.client.model.MicronDevice;
import com.micron.vmware.cim.client.model.MicronSATADevice;
import com.micron.vmware.cim.client.util.MicronDeviceUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class GetSATADevices {

	private MicronDeviceUtil micronDeviceUtil;
	private CIMHost          cimHost;

	private static final String MICRON_SATA_DEVICE_URL = "https://x.x.x.x:5989/micron/cimv2:MICRON_SATADevice";
	private static final String USER                   = "root";
	private static final String PASS                   = "password";

	public GetSATADevices() {
		this.micronDeviceUtil = new MicronDeviceUtil();
	}

	@Test
	public void testGetDrivesList_SATA() {
		cimHost = new CIMHostUser(MICRON_SATA_DEVICE_URL, USER, PASS);
		List<MicronDevice> micronDevices = micronDeviceUtil
				.getDeviceList(cimHost, CIMObjectType.MICRON_SATA_DEVICE);

		Assert.assertNotNull(micronDevices);
		for (MicronDevice micronDevice : micronDevices) {
			Assert.assertTrue(micronDevice instanceof MicronSATADevice);
		}
	}
}
