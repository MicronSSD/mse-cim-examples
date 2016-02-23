# Java Example Code

* Below are examples of how to get started with the Java code in this repository.
* There are working examples contained within the com.micron.vmware.cim.examples package.
* Most of the implementation is abstracted out to a few public methods to make it easy to communicate with Micron devices.
* The included build.xml will create a .jar file of the example code for easy use in other projects.
* This API was built on top of the SBLIM Client Java API: http://sourceforge.net/projects/sblim/
* Other dependencies are located in /lib.

### URL and Host setup

* MICRON_SATADevice and MICRON_SASDevice extend MICRON_Device.
* Pass the appropriate URL to the CIMHost object to query all devices or a specific device type.

```java
private final String MICRON_DEVICE_URL = "https://x.x.x.x:5989/micron/cimv2:MICRON_Device";  
private final String MICRON_SATA_DEVICE_URL = "https://x.x.x.x:5989/micron/cimv2:MICRON_SATADevice";  
private final String MICRON_SAS_DEVICE__URL = "https://x.x.x.x:5989/micron/cimv2:MICRON_SASDevice";  

//Create a new Host object with the appropriate URL and credentials  
CIMHost cimHost = new CIMHostUser(MICRON_SATA_DEVICE_URL, "user", "pass");  
```

* The above CIMHost object can be used to perform SATA-specific and generic operations.

### Retrieving devices from a host

* The ***MicronDeviceUtil*** class is responsible for retrieving device info from the Micron CIM provider.
 * It is converted into a more user-friendly format, an instance of MicronDevice or one of its subclasses.
* A MicronDevice object will contain information such as Device ID, Model Number, SMART data, and more.

**Retrieve All devices from a host**  

```java  
CIMHost cimHost = new CIMHostUser(MICRON_DEVICE_URL, "user", "pass");  
List<MicronDevice> micronDevices = micronDeviceUtil.getDeviceList(cimHost, CIMObjectType.MICRON_DEVICE);  
```

**Retrieve Only SATA devices**  

```java  
CIMHost cimHost = new CIMHostUser(MICRON_SATA_DEVICE_URL, "user", "pass");  
List<MicronDevice> sataDevices = micronDeviceUtil.getDeviceList(cimHost, CIMObjectType.MICRON_SATA_DEVICE);  
```

**Retrieve Only SAS devices**  

```java  
CIMHost cimHost = new CIMHostUser(MICRON_SAS_DEVICE_URL, "user", "pass");  
List<MicronDevice> sasDevices = micronDeviceUtil.getDeviceList(cimHost, CIMObjectType.MICRON_SAS_DEVICE);  
```

**Retrieve a device by DeviceId**  

```java
CIMHost cimHost = new CIMHostUser(MICRON_DEVICE_URL, USER, PASS);  
MicronDevice micronDevice = micronDeviceUtil.getDeviceById(cimHost, CIMObjectType.MICRON_SATA_DEVICE, "device id here");
```

### Calling CIM methods

* All CIM methods can be called using the ***APIMethod*** class, which takes the enum value of the method to be called.
* The device id, and a set of arguments if applicable, are required for the ***invoke*** method.

**Refresh SMART attributes**  

```java  
APIMethod apiMethod = new APIMethod(CIMMethod.UPDATE_DEVICE_DATA);  
List<MicronDevice> micronDevices = micronDeviceUtil.getDeviceList(cimHost, CIMObjectType.MICRON_SATA_DEVICE);  
for (MicronDevice micronDevice : micronDevices)  
{  
	CIMStatus cimStatus = apiMethod.invoke(cimHost, CIMObjectType.MICRON_SATA_DEVICE, micronDevice.getDeviceId());
}  
```

**Update Firmware**  

* SMART Data needs to be refreshed after a firmware update to properly show the current firmware version.
* The firmware files must be place under /scratch on the host.
* The only required argument is filepath.

```java  
APIMethod updateFirmware = new APIMethod(CIMMethod.UPDATE_FIRMWARE);  
CIMArgument<?>[] args = {  
    new CIMArgument<String>("filepath", CIMDataType.STRING_T, "/scratch/fwbin-m600"),  
};  
CIMStatus cimStatus = updateFirmware.invoke(cimHost, CIMObjectType.MICRON_SATA_DEVICE, micronDevice.getDeviceId(), args);  
```

**Secure Erase - SATA Only**  

```java  
APIMethod apiMethod = new APIMethod(CIMMethod.SATA_SECURE_ERASE);  
CIMStatus cimStatus = apiMethod.invoke(cimHost, CIMObjectType.MICRON_SATA_DEVICE, micronDevice.getDeviceId());
```

**Sanitize - SAS Only**  

```java  
APIMethod apiMethod = new APIMethod(CIMMethod.SAS_SANITIZE);  
CIMStatus cimStatus = apiMethod.invoke(cimHost, CIMObjectType.MICRON_SAS_DEVICE, micronDevice.getDeviceId());
```
