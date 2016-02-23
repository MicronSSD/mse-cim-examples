# Powershell Examples

Below are examples of how to use the CIM cmdlets (included with Powershell 3.0 and above) with the Micron SSD CIM provider.

# ESXi Configuration Changes

More recent versions of ESXi use SFCB (http://sblim.sourceforge.net/wiki/index.php/Sfcb) as its CIMOM (CIM Object Manager).  
SFCB itself only supports communication through the WBEM (CIM-XML over HTTP) protocol, however the Powershell CIM cmdlets use the WS-Management(SOAP) protocol.
VMware bundles ESXi with openwsman, which is configured to act as a proxy between the CIMOM and a WS-Man client.
If you want to use any WS-Man implementations to communicate with a custom CIM provider/namespace, you need to make a configuration change on the ESXi host.

1. Open /etc/openwsman/openwsman.conf.templ
2. Add an entry for Micron in 'vendor_namespaces'
    * MICRON=http://schemas.micron.com/wbem/wscim/1/cim-schema/2  
    * Note: WS-Man requires a ResourceURI for CIM management unlike WBEM. This value was made up arbitrarily. It can be whatever you want as long as it's unique and matches the URI in your powershell script.
3. Restart the wsman server.
    * /etc/init.d/wsman restart

The changes you've made to openwsman.conf.temp will persist through any service or power restarts, and automatically copied to the actual config file used, openwsman.conf.

# Examples

Import the CIM cmdlets and configure the session with the ESXi host.

```
import-module CimCmdlets
$Ipaddress = "x.x.x.x"
$HostUsername = Get-Credential root
$CIOpt = New-CimSessionOption -SkipCACheck -SkipCNCheck -SkipRevocationCheck -Encoding Utf8 -UseSsl
$Session = New-CimSession -Authentication Basic -Credential $HostUsername -ComputerName $Ipaddress   -port 443 -SessionOption $CIOpt
```

Retrieve a list of all drives, or just SATA or SAS drives.

```
$AllDevices = @(Get-CimInstance -CimSession $Session -Namespace "micron/cimv2" -ResourceUri "http://schemas.micron.com/wbem/wscim/1/cim-schema/2/MICRON_Device")
$SATADevices = @(Get-CimInstance -CimSession $Session -Namespace "micron/cimv2" -ResourceUri "http://schemas.micron.com/wbem/wscim/1/cim-schema/2/MICRON_SATADevice")
$SASDevices = @(Get-CimInstance -CimSession $Session -Namespace "micron/cimv2" -ResourceUri "http://schemas.micron.com/wbem/wscim/1/cim-schema/2/MICRON_SASDevice")
```

Refresh SMART data of a drive - will need to run Get-CimInstance again to see changes.

```
Invoke-CimMethod -CimSession $Session -InputObject $SATADevices[0] -MethodName "UpdateDeviceData"
```

Upgrade the firmware of a drive

```
$Arguments = @{"filepath" = "/scratch/fwbin-m600"}
Invoke-CimMethod -CimSession $Session -InputObject $SATADevices[0] -MethodName "UpdateFirmware" -Arguments $Arguments
```

Secure Erase a SATA drive

```
Invoke-CimMethod -CimSession $Session -InputObject $SATADevices[0] -MethodName "UpdateDeviceData"
```

Sanitize a SAS drive

```
Invoke-CimMethod -CimSession $Session -InputObject $SASDevices[0] -MethodName "SanitizeBlockErase"  
```
