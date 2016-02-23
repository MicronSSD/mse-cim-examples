package com.micron.vmware.cim.client;

import com.micron.vmware.cim.client.enums.CIMMethod;
import com.micron.vmware.cim.client.enums.CIMObjectType;
import com.micron.vmware.cim.client.enums.CIMStatus;
import com.micron.vmware.cim.client.host.CIMHost;
import com.micron.vmware.cim.client.util.CIMInstanceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.cim.CIMArgument;
import javax.cim.CIMInstance;
import javax.cim.CIMObjectPath;
import javax.wbem.WBEMException;

public class APIMethod {
	private final static Logger logger = LoggerFactory.getLogger(com.micron.vmware.cim.client.APIMethod.class);

	private CIMMethod       cimMethod       = null;
	private CIMInstanceUtil cimInstanceUtil = null;

	public APIMethod(CIMMethod cimMethod) {
		this.cimMethod = cimMethod;
		cimInstanceUtil = new CIMInstanceUtil();
	}

	public CIMStatus invoke(CIMHost cimHost, CIMObjectType cimObjectType, String deviceId) {
		return invoke(cimHost, cimObjectType, deviceId, null);
	}

	public CIMStatus invoke(CIMHost cimHost, CIMObjectType cimObjectType, String deviceId,
			CIMArgument<?>[] cimArguments) {
		CIMStatus cimStatus = CIMStatus.CIM_STATUS_FAIL;

		if (cimHost == null || !cimHost.isValid() || deviceId == null) {
			return CIMStatus.CIM_STATUS_INVALID_ARGUMENT;
		}

		CIMInstance cimInstance = cimInstanceUtil
				.getInstanceByDeviceId(cimHost, cimObjectType, deviceId);

		if (cimInstance == null) {
			logger.error("Opening the host '" + cimHost + "' failed or the device ID '" + deviceId + "' is not found.");
			return cimStatus;
		}

		try {
			CIMObjectPath cimObjectPath = cimInstance.getObjectPath();
			cimInstanceUtil.getClient(cimHost, cimObjectType).invokeMethod(cimObjectPath, cimMethod.getValue(), cimArguments, null);
			cimStatus = CIMStatus.CIM_STATUS_SUCCESS;
		}
		catch (WBEMException e) {
			logger.error("Failed to invoke the CIM method '" + cimMethod.getValue() + "' on host '" + cimHost + "'", e);
		}

		return cimStatus;
	}
}
