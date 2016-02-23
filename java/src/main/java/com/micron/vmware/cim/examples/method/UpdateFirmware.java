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

import javax.cim.CIMArgument;
import javax.cim.CIMDataType;
import java.util.List;

public class UpdateFirmware {

	private MicronDeviceUtil micronDeviceUtil;
	private CIMHost          cimHost;

	private final String MICRON_SATA_DEVICE_URL = "https://x.x.x.x:5989/micron/cimv2:MICRON_SATADevice";
	private final String USER                   = "root";
	private final String PASS                   = "password";

	public UpdateFirmware() {
		this.micronDeviceUtil = new MicronDeviceUtil();
	}

	@Ignore @Test public void testFirmwareUpdate() {
		APIMethod updateFirmware = new APIMethod(CIMMethod.UPDATE_FIRMWARE);
		APIMethod updateDeviceData = new APIMethod(CIMMethod.UPDATE_DEVICE_DATA);
		CIMStatus cimStatus;
		MicronDevice micronDevice;

		//Find SATA devices (could also be SAS)
		cimHost = new CIMHostUser(MICRON_SATA_DEVICE_URL, USER, PASS);
		List<MicronDevice> micronDevices = micronDeviceUtil
				.getDeviceList(cimHost, CIMObjectType.MICRON_SATA_DEVICE);
		Assert.assertFalse(micronDevices.isEmpty());
		micronDevice = micronDevices.get(0);

		//Upgrade to MU03
		CIMArgument<?>[] args = {
				new CIMArgument<String>("filepath", CIMDataType.STRING_T, "/path-to-firmware")
		};
		cimStatus = updateFirmware.invoke(cimHost, CIMObjectType.MICRON_SATA_DEVICE, micronDevice.getDeviceId(), args);
		//Assert CIM Status
		Assert.assertEquals(CIMStatus.CIM_STATUS_SUCCESS, cimStatus);
		//Assert firmware version
		cimStatus = updateDeviceData.invoke(cimHost, CIMObjectType.MICRON_SATA_DEVICE, micronDevice.getDeviceId());
		Assert.assertEquals(CIMStatus.CIM_STATUS_SUCCESS, cimStatus);
		micronDevice = micronDeviceUtil.getDeviceById(cimHost, CIMObjectType.MICRON_SATA_DEVICE, micronDevices.get(0).getDeviceId());
		Assert.assertEquals("Firmware update to MU03 failed", "MU03", micronDevice.getFirmwareVersion());
	}
}
