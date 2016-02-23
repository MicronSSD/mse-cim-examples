package com.micron.vmware.cim.client.util;

import com.micron.vmware.cim.client.enums.CIMObjectType;
import com.micron.vmware.cim.client.host.CIMHost;
import com.micron.vmware.cim.client.host.CIMHostSession;
import com.micron.vmware.cim.client.host.CIMHostUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.cim.CIMDataType;
import javax.cim.CIMInstance;
import javax.cim.CIMObjectPath;
import javax.cim.CIMProperty;
import javax.security.auth.Subject;
import javax.wbem.CloseableIterator;
import javax.wbem.WBEMException;
import javax.wbem.client.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

public class CIMInstanceUtil {

	private static final Logger _logger = LoggerFactory.getLogger(com.micron.vmware.cim.client.util.CIMInstanceUtil.class);

	private static final String CIM_NAMESPACE = "micron/cimv2";

	public CIMInstance getInstanceByDeviceId(CIMHost cimHost, CIMObjectType cimObjectType,
			String deviceId) {
		try {
			CIMProperty<?>[] properties = {
					new CIMProperty<String>("DeviceID", CIMDataType.STRING_T, deviceId, true, false, cimObjectType
							.getValue()) };

			WBEMClient wbemClient = getClient(cimHost, cimObjectType);

			CIMObjectPath cimObjectPath = getHostObjectPath(cimHost, properties, cimObjectType);

			CIMInstance cimInstance = wbemClient.getInstance(cimObjectPath, false, false, null);

			if (cimInstance == null) {
				_logger.error(
						"Specified device ID ' " + deviceId + "' not found on host '" + cimHost
								.getUrl() + "'.");
			}

			return cimInstance;
		} catch (WBEMException e) {
			_logger.error(
					"Failed to get drive instance for device ID '" + deviceId + "' on host '" + cimHost.toString() + "'", e);
		}

		return null;
	}

	public CloseableIterator<CIMInstance> getAllInstances(CIMHost cimHost, CIMObjectType cimObjectType) {
		try {
			WBEMClient client = getClient(cimHost, cimObjectType);
			CloseableIterator<CIMInstance> iterator = client.enumerateInstances(getHostObjectPath(cimHost, null, cimObjectType), false, false, false, null);
			return iterator;
		} catch (WBEMException e) {
			_logger.error("Failed to retrieve CIMInstance iterator!", e.getMessage());
		}

		return null;
	}

	public WBEMClient getClient(CIMHost cimHost, CIMObjectType cimObjectType) {
		return getClient(cimHost, cimObjectType, null);
	}

	public WBEMClient getClient(CIMHost cimHost, CIMObjectType cimObjectType, CIMProperty<?>[] properties) {
		Subject subject = createSubjectFromHost(cimHost);

		try {
			WBEMClient client = WBEMClientFactory.getClient(WBEMClientConstants.PROTOCOL_CIMXML);
			client.initialize(getHostObjectPath(cimHost, properties, cimObjectType), subject, Locale
					.getAvailableLocales());
			return client;
		} catch (WBEMException e) {
			_logger.error("Failed to initialize WBEMClient!", e.getMessage());
		}
		return null;
	}

	private CIMObjectPath getHostObjectPath(CIMHost cimHost, CIMProperty<?>[] properties, CIMObjectType cimObjectType) {
		try {
			URL UrlHost = new URL(cimHost.getUrl());
			return new CIMObjectPath(UrlHost.getProtocol(), UrlHost.getHost(),
					String.valueOf(UrlHost.getPort()), CIM_NAMESPACE, cimObjectType.getValue(), properties);
		} catch (MalformedURLException e) {
			_logger.error("Invalid URL '{}'", cimHost.getUrl(), e.getMessage());
		}
		return null;
	}

	private Subject createSubjectFromHost(CIMHost cimHost) {
		String user = "";
		String password = "";
		Subject subject = new Subject();

		if (cimHost instanceof CIMHostUser) {
			user = ((CIMHostUser)cimHost).getUsername();
			password = ((CIMHostUser)cimHost).getPassword();
		} else if (cimHost instanceof CIMHostSession) {
			user = ((CIMHostSession)cimHost).getSessionId();
			password = user;
		}

		subject.getPrincipals().add(new UserPrincipal(user));
		subject.getPrivateCredentials().add(new PasswordCredential(password.toCharArray()));

		return subject;
	}
}
