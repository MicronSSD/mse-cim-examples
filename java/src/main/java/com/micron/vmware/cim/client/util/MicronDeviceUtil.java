package com.micron.vmware.cim.client.util;

import com.micron.vmware.cim.client.enums.CIMObjectType;
import com.micron.vmware.cim.client.enums.DeviceType;
import com.micron.vmware.cim.client.host.CIMHost;
import com.micron.vmware.cim.client.model.MicronDevice;
import com.micron.vmware.cim.client.model.MicronSASDevice;
import com.micron.vmware.cim.client.model.MicronSATADevice;
import com.micron.vmware.cim.client.model.SMARTAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.cim.*;
import javax.wbem.CloseableIterator;
import java.util.ArrayList;
import java.util.List;

public class MicronDeviceUtil {

	private static final Logger _logger = LoggerFactory.getLogger(com.micron.vmware.cim.client.util.MicronDeviceUtil.class);

	private com.micron.vmware.cim.client.util.CIMInstanceUtil cimInstanceUtil;

	public MicronDeviceUtil() {
		this.cimInstanceUtil = new CIMInstanceUtil();
	}

	public List<MicronDevice> getDeviceList(CIMHost cimHost, CIMObjectType cimObjectType) {
		if (!cimHost.isValid()) {
			return null;
		}

		List<MicronDevice> drives = new ArrayList<MicronDevice>();

		CloseableIterator<CIMInstance> iterator = cimInstanceUtil
				.getAllInstances(cimHost, cimObjectType);

		if (iterator == null) {
			_logger.error("No instances found from host '{}'", cimHost.getUrl());
			return null;
		}

		try {
			if (iterator.hasNext() == false) {
				_logger.info("No drives found on host '{}'", cimHost.getUrl());
			} else {
				while (iterator.hasNext()) {
					CIMInstance cimInstance = iterator.next();

					if (_logger.isDebugEnabled()) {
						for (CIMProperty<?> prop : cimInstance.getProperties()) {
							_logger.debug("{}={}", new Object[] { prop.getName(), prop.getValue() });
						}
					}

					DeviceType deviceType = DeviceType
							.valueOf(cimInstance.getObjectPath().getObjectName());
					MicronDevice micronDevice = null;
					switch(deviceType) {
						case MICRON_SATADevice:
							micronDevice = new MicronSATADevice();
							setCommonDeviceProperties(micronDevice, cimInstance);
							setSATADeviceProperties(micronDevice, cimInstance);
							break;
						case MICRON_SASDevice:
							micronDevice = new MicronSASDevice();
							setCommonDeviceProperties(micronDevice, cimInstance);
							setSASDeviceProperties(micronDevice, cimInstance);
							break;
						default:
							_logger.error("Device Type mismatch.");
					}

					if (micronDevice != null) {
						drives.add(micronDevice);
					}
				}
			}
		} finally {
			iterator.close();
		}

		if (drives.size() <= 0) { return null; }

		return drives;
	}

	public MicronDevice getDeviceById(CIMHost cimHost, CIMObjectType cimObjectType, String deviceId) {
		if (!cimHost.isValid()) {
			return null;
		}

		CIMInstance cimInstance = cimInstanceUtil.getInstanceByDeviceId(cimHost, cimObjectType, deviceId);
		if (cimInstance == null) {
			_logger.error("No instance found from host '{}' for device id '{}'", cimHost.getUrl(), deviceId);
			return null;
		}

		if (_logger.isDebugEnabled()) {
			for (CIMProperty<?> prop : cimInstance.getProperties()) {
				_logger.debug("{}={}", new Object[] { prop.getName(), prop.getValue() });
			}
		}

		DeviceType deviceType = DeviceType.valueOf(cimInstance.getObjectPath().getObjectName());
		MicronDevice micronDevice = null;
		switch(deviceType) {
			case MICRON_SATADevice:
				micronDevice = new MicronSATADevice();
				setCommonDeviceProperties(micronDevice, cimInstance);
				setSATADeviceProperties(micronDevice, cimInstance);
				break;
			case MICRON_SASDevice:
				micronDevice = new MicronSASDevice();
				setCommonDeviceProperties(micronDevice, cimInstance);
				setSASDeviceProperties(micronDevice, cimInstance);
				break;
			default:
				_logger.error("Device Type mismatch.");
		}

		return micronDevice;
	}

	private void setCommonDeviceProperties(MicronDevice micronDevice, CIMInstance cimInstance) {
		micronDevice.setDeviceId(cimInstance.getPropertyValue("DeviceID").toString());
		micronDevice.setSerialNumber(cimInstance.getPropertyValue("DeviceSerialNumber").toString());
		micronDevice.setModelNumber(cimInstance.getPropertyValue("DeviceModelNumber").toString());
		micronDevice.setModelFamily(cimInstance.getPropertyValue("DeviceModelFamily").toString());
		micronDevice.setFirmwareVersion(cimInstance.getPropertyValue("DeviceFirmwareVersion").toString());
		micronDevice.setDeviceCapacity(Long.parseLong(cimInstance.getPropertyValue("DeviceCapacity").toString()));
		micronDevice.setNativeMaxAddress(Long.parseLong(cimInstance.getPropertyValue("NativeMaxAddress").toString()));
	}

	private void setSATADeviceProperties(MicronDevice micronDevice, CIMInstance cimInstance) {

		if (micronDevice instanceof MicronSATADevice) {
			MicronSATADevice sataDevice = (MicronSATADevice) micronDevice;
			sataDevice.setDeviceType(DeviceType.MICRON_SATADevice);
			sataDevice.setDevfsPath(cimInstance.getPropertyValue("DevfsPath").toString());
			sataDevice.setTcgStatus(Integer.parseInt(cimInstance.getPropertyValue("TCGStatus").toString()));
			sataDevice.setTcgssc(Integer.parseInt(cimInstance.getPropertyValue("TCGSSC").toString()));
			sataDevice.setSanitizeSupported(Boolean.parseBoolean(cimInstance.getPropertyValue("SanitizeSupported").toString()));
			sataDevice.setSataLinkSpeed(cimInstance.getPropertyValue("SATALinkSpeed").toString());

			//S.M.A.R.T Attributes
			UnsignedInteger8[] attrId = (UnsignedInteger8[])cimInstance.getPropertyValue("SMARTAttrID");
			String[] attrDesc = (String[])cimInstance.getPropertyValue("SMARTAttrDesc");
			UnsignedInteger32[] attrValue = (UnsignedInteger32[])cimInstance.getPropertyValue("SMARTAttrValue");
			UnsignedInteger64[] attrRawData = (UnsignedInteger64[])cimInstance.getPropertyValue("SMARTAttrRawData");
			UnsignedInteger32[] attrWorst = (UnsignedInteger32[])cimInstance.getPropertyValue("SMARTAttrWorst");
			UnsignedInteger32[] attrThreshold = (UnsignedInteger32[])cimInstance.getPropertyValue("SMARTAttrThreshold");
			String[] attrUnit = (String[])cimInstance.getPropertyValue("SMARTAttrUnits");
			for (int i = 0; i < attrId.length; i ++) {
				SMARTAttribute smartAttribute = new SMARTAttribute(
						Integer.parseInt(attrId[i].toString()),
						attrDesc[i],
						Integer.parseInt(attrValue[i].toString()),
						Long.parseLong(attrRawData[i].toString()),
						Integer.parseInt(attrWorst[i].toString()),
						Integer.parseInt(attrThreshold[i].toString()),
						attrUnit[i]);
				sataDevice.getSmartAttributes().add(smartAttribute);
			}
		}
	}

	private void setSASDeviceProperties(MicronDevice micronDevice, CIMInstance cimInstance) {

		if (micronDevice instanceof MicronSASDevice) {
			MicronSASDevice sasDevice = (MicronSASDevice) micronDevice;
			sasDevice.setDeviceType(DeviceType.MICRON_SASDevice);
			sasDevice.setSasLinkSpeed(Integer.parseInt(cimInstance
					.getPropertyValue("SASLinkSpeed").toString()));
			sasDevice.setCurrentTemperature(Integer.parseInt(cimInstance
					.getPropertyValue("CurrentTemperature").toString()));
			sasDevice.setPowerCycleCount(Integer.parseInt(cimInstance.getPropertyValue("PowerCycleCount").toString()));
			sasDevice.setLifetimeUsedPercentage(Integer.parseInt(cimInstance.getPropertyValue("LifetimeUsedPct").toString()));
			sasDevice.setPowerOnHours(Integer.parseInt(cimInstance.getPropertyValue("PowerOnHours").toString()));
		}
	}
}
