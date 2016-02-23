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
import org.junit.Test;

import java.util.List;

public class UpdateDeviceData {

	private MicronDeviceUtil micronDeviceUtil;
	private CIMHost          cimHost;

	private final String MICRON_SATA_DEVICE_URL = "https://x.x.x.x:5989/micron/cimv2:MICRON_SATADevice";
	private final String MICRON_SAS_DEVICE__URL = "https://x.x.x.x:5989/micron/cimv2:MICRON_SASDevice";
	private final String USER                   = "root";
	private final String PASS                   = "password";

	public UpdateDeviceData() {
		this.micronDeviceUtil = new MicronDeviceUtil();
	}

	@Test
	public void testUpdateDeviceData_SATA() {
		cimHost = new CIMHostUser(MICRON_SATA_DEVICE_URL, USER, PASS);

		APIMethod updateDeviceData = new APIMethod(CIMMethod.UPDATE_DEVICE_DATA);

		List<MicronDevice> micronDevices = micronDeviceUtil
				.getDeviceList(cimHost, CIMObjectType.MICRON_SATA_DEVICE);

		CIMStatus cimStatus = updateDeviceData
				.invoke(cimHost, CIMObjectType.MICRON_SATA_DEVICE, micronDevices.get(0)
						.getDeviceId());

		Assert.assertEquals("Unexpected API status.", CIMStatus.CIM_STATUS_SUCCESS, cimStatus);
	}

	@Test
	public void testUpdateDeviceData_SAS() {
		cimHost = new CIMHostUser(MICRON_SAS_DEVICE__URL, USER, PASS);

		APIMethod updateDeviceData = new APIMethod(CIMMethod.UPDATE_DEVICE_DATA);

		List<MicronDevice> micronDevices = micronDeviceUtil.getDeviceList(cimHost, CIMObjectType.MICRON_SAS_DEVICE);

		CIMStatus cimStatus = updateDeviceData.invoke(cimHost, CIMObjectType.MICRON_SAS_DEVICE, micronDevices.get(0).getDeviceId());

		Assert.assertEquals("Unexpected API status.", CIMStatus.CIM_STATUS_SUCCESS, cimStatus);
	}
}
