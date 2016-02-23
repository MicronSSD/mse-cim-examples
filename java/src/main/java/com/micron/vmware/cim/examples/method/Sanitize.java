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

public class Sanitize {

	private MicronDeviceUtil micronDeviceUtil;
	private CIMHost          cimHost;

	private final String MICRON_SAS_DEVICE__URL = "https://x.x.x.x:5989/micron/cimv2:MICRON_SASDevice";
	private final String USER                   = "root";
	private final String PASS                   = "password";

	public Sanitize() {
		this.micronDeviceUtil = new MicronDeviceUtil();
	}

	@Ignore @Test public void testSanitize() {
		//Get list of SAS devices (Only SAS devices support Sanitize)
		cimHost = new CIMHostUser(MICRON_SAS_DEVICE__URL, USER, PASS);
		List<MicronDevice> micronDevices = micronDeviceUtil
				.getDeviceList(cimHost, CIMObjectType.MICRON_SAS_DEVICE);
		Assert.assertFalse(micronDevices.isEmpty());

		APIMethod apiMethod = new APIMethod(CIMMethod.SAS_SANITIZE);

		CIMStatus cimStatus = apiMethod
				.invoke(cimHost, CIMObjectType.MICRON_SAS_DEVICE, micronDevices.get(0)
						.getDeviceId());

		Assert.assertEquals("Unexpected API status.", CIMStatus.CIM_STATUS_SUCCESS, cimStatus);
	}
}
