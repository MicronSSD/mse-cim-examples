package com.micron.vmware.cim.examples.device;

import com.micron.vmware.cim.client.enums.CIMObjectType;
import com.micron.vmware.cim.client.host.CIMHost;
import com.micron.vmware.cim.client.host.CIMHostUser;
import com.micron.vmware.cim.client.model.MicronDevice;
import com.micron.vmware.cim.client.model.MicronSASDevice;
import com.micron.vmware.cim.client.util.MicronDeviceUtil;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by kvroman on 11/10/2015.
 */
public class GetSASDevices {

	private MicronDeviceUtil micronDeviceUtil;
	private CIMHost          cimHost;

	private static final String MICRON_SAS_DEVICE__URL = "https://x.x.x.x:5989/micron/cimv2:MICRON_SASDevice";
	private static final String USER                   = "root";
	private static final String PASS                   = "password";

	public GetSASDevices() {
		this.micronDeviceUtil = new MicronDeviceUtil();
	}

	@Test
	public void testGetDrivesListSAS() {
		cimHost = new CIMHostUser(MICRON_SAS_DEVICE__URL, USER, PASS);
		List<MicronDevice> micronDevices = micronDeviceUtil
				.getDeviceList(cimHost, CIMObjectType.MICRON_SAS_DEVICE);

		Assert.assertNotNull(micronDevices);
		for (MicronDevice micronDevice : micronDevices) {
			Assert.assertTrue(micronDevice instanceof MicronSASDevice);
		}
	}
}
