package com.micron.vmware.cim.examples.device;

import com.micron.vmware.cim.client.enums.CIMObjectType;
import com.micron.vmware.cim.client.host.CIMHost;
import com.micron.vmware.cim.client.host.CIMHostUser;
import com.micron.vmware.cim.client.model.MicronDevice;
import com.micron.vmware.cim.client.util.MicronDeviceUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class GetAllDevices {

	private MicronDeviceUtil micronDeviceUtil;
	private CIMHost          cimHost;

	private static final String MICRON_DEVICE_URL      = "https://x.x.x.x:5989/micron/cimv2:MICRON_Device";
	private static final String USER                   = "root";
	private static final String PASS                   = "password";

	public GetAllDevices() {
		this.micronDeviceUtil = new MicronDeviceUtil();
	}

	@Test
	public void testGetDrivesList_ALL() {
		cimHost = new CIMHostUser(MICRON_DEVICE_URL, USER, PASS);
		List<MicronDevice> micronDevices = micronDeviceUtil
				.getDeviceList(cimHost, CIMObjectType.MICRON_DEVICE);

		Assert.assertNotNull(micronDevices);

		//assert a few common device properties
		Assert.assertTrue(
				micronDevices.get(0).getDeviceId() != null && !micronDevices.get(0).getDeviceId()
						.isEmpty());
	}
}
