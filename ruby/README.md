# Ruby WBEMCLI Wrapper

This is a simple ruby wrapper around the wbemcli command line tool.  
It was written/tested with Ruby 2.2.3.  
Depends on the wbemcli command line tool, available from most package management repositories.  
http://sourceforge.net/projects/sblim/files/sblim-wbemcli/  

## Examples

DEVICE_TYPE can be one of three values: MICRON_Device, MICRON_SATADevice, MICRON_SASDevice  
Use mse-cim.rb --help for more info  

### Enumerate all devices
```
mse-cim.rb -h x.x.x.x -u root -p password -e
```

### Enumerate only SATA devices
```
mse-cim.rb -h x.x.x.x -u root -p password -e MICRON_SATADevice
```

### Enumerate only SAS devices
```
mse-cim.rb -h x.x.x.x -u root -p password -e MICRON_SASDevice
```

### Get a single device instance by its ID
```
mse-cim.rb -h x.x.x.x -u root -p password -g <DEVICE_ID> -t <DEVICE_TYPE>
```

### Update/refresh SMART data on a device
```
mse-cim.rb -h x.x.x.x -u root -p password -m UpdateDeviceData -d <DEVICE_ID> -t <DEVICE_TYPE>
```

### Secure Erase a SATA device
```
mse-cim.rb -h x.x.x.x -u root -p password -m SecureErase -d <DEVICE_ID>
```

### Sanitize a SAS device
```
mse-cim.rb -h x.x.x.x -u root -p password -m SanitizeBlockErase -d <DEVICE_ID>
```

### Update the firmware of a device
```
mse-cim.rb -h x.x.x.x -u root -p password -m UpdateFirmware -d <DEVICE_ID> -t <DEVICE_TYPE> -f </path/to/firmware>
```