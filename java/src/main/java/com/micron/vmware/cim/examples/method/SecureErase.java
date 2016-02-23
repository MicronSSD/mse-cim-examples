package com.micron.vmware.cim.examples.method;

import com.micron.vmware.cim.client.APIMethod;
import com.micron.vmware.cim.client.enums.CIMMethod;
import com.micron.vmware.cim.client.enums.CIMObjectType;
import com.micron.vmware.cim.client.enums.CIMStatus;
import com.micron.vmware.cim.client.host.CIMHost;
import com.micron.vmware.cim.client.host.CIMHostUser;
import com.micron.vmware.cim.client.model.MicronDevice;
import com.micron.vmware.cim.client.util.MicronDeviceUtil;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

public class SecureErase {

	private MicronDeviceUtil micronDeviceUtil;
	private CIMHost          cimHost;

	private final String MICRON_SATA_DEVICE_URL = "https://x.x.x.x:5989/micron/cimv2:MICRON_SATADevice";
	private final String USER                   = "root";
	private final String PASS                   = "password";

	public SecureErase() {
		this.micronDeviceUtil = new MicronDeviceUtil();
	}

	@Ignore @Test public void testSecureErase() {
		//Get list of SATA devices (Only SATA devices support Secure Erase)
		cimHost = new CIMHostUser(MICRON_SATA_DEVICE_URL, USER, PASS);
		List<MicronDevice> micronDevices = micronDeviceUtil
				.getDeviceList(cimHost, CIMObjectType.MICRON_SATA_DEVICE);
		Assert.assertFalse(micronDevices.isEmpty());

		APIMethod apiMethod = new APIMethod(CIMMethod.SATA_SECURE_ERASE);

		CIMStatus cimStatus = apiMethod
				.invoke(cimHost, CIMObjectType.MICRON_SATA_DEVICE, micronDevices.get(0)
						.getDeviceId());

		Assert.assertEquals("Unexpected API status.", CIMStatus.CIM_STATUS_SUCCESS, cimStatus);
	}
}
