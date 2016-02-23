# Python Example Code

Below are examples of how to get started with the Python code in this repository. There is one Python script: MCIM.py.
MCIM contains the main module and CIM interaction and some utility functions
to parse the command line, output information to a CSV or JSON format etc. Error handling is minimal to allow
readability and user can choose to use portions of this code and add additional error handling as per their need.

These are compatible with Python 2.7 and depend on the pywbem-0.80_dev-py2.7.egg/pywbem-0.7, M2Crypto (for windows 64-bit:
M2CryptoWin64-0.21.1_3-py2.7.egg-info). If using python 2.6 you will also need to install argparse module.

The MCIM contains two main methods: one to

* enumerate all Micron Devices or a specific Micron device (can be output to a csv or JSON format)

and the other is a generic wrapper around the pywbem InvokeMethod and is used to perform the following functions:

* Sanitize a SAS Drive

* Secure Erase a SATA Drive

* Update Firmware for a Drive. Note: The firmware files must be place under /scratch on the ESXi host. SMART Data
needs to be refreshed after a firmware update to properly show the current firmware version.

* Refresh SMART Attributes for a Drive



# Usage
*python MCIM.py -h*

usage: MCIM.py [-h] -l URL -u USER -p PASSWORD -n NAMESPACE [-e] [-s] [-t]
                [-j OUTPUTJSON] [-v OUTPUTCSV] [-z] [-c] [-r] [-w] [-f FWFILEPATH]
               [-d DEVICEID] [-y DEVICETYPE]

optional arguments:
  -h, --help            show this help message and exit

  -l URL, --url URL     CIM URL of the format:
                        https://host:port

  -u USER, --user USER  User Name

  -p PASSWORD, --password PASSWORD
                        Password

  -n NAMESPACE, --namespace NAMESPACE
                        CIM Namespace e.g. 'micron/cimv2'

  -e, --enum            Enumerate Micron Devices

  -s, --enumSAS         Enumerate SAS Micron Devices

  -t, --enumSATA        Enumerate SATA Micron Devices

  -y, --getInstance     Get specific instance of a Micron Device. Please
                        provide device ID and device Type as a command line arg

  -j OUTPUTJSON, --outputJSON OUTPUTJSON
                       Enumeration of Devices can be output to a JSON file if
                       user wishes to. Provide full path including file name
                       e.g. C:\Users\xyz\EnumAllMicron.json

  -v OUTPUTCSV, --outputCSV OUTPUTCSV
                        Enumeration of Devices can be output to a CSV file if
                        user wishes to. Provide full path including file name
                        e.g. C:\Users\xyz\EnumAllMicron.csv

  -z, --sanitize        Sanitize Micron SAS drive. Please provide device ID as
                        a command line arg

  -c, --secureErase     Secure Erase Micron SATA drive. Please provide device
                        ID as a command line arg

  -r, --refreshSMART    Refresh SMART Data for a Micron drive. Please
                        provide device ID and device Type as a command line arg

  -w, --updateFW        Update FW Version for Micron drive. Please
                        provide device ID, device Type and full path with filename
                        for FW binary as a command line arg

  -f FWFILEPATH, --fwFilePath FWFILEPATH
                        Provide full path with filename for FW binary as a
                        command line arg for update firmware operation. The
                        file must already exist on the ESXi host.

  -d DEVICEID, --deviceID DEVICEID
                        Device ID of the drive you wish to perform the
                        operation on


## Enumerate All Devices and output to a CSV File (-v)
python MCIM.py -e -l https://host:port -u root -p password -n micron/cimv2
-v "C:\\Users\\XYZ\\enumerationAll.csv"


## Enumerate SATA Devices and output to a JSON File (-j)
python MCIM.py -t -l https://host:port -u root -p password -n micron/cimv2
-j "C:\\Users\\XYZ\\enumerationSATA.json"


## Enumerate SAS Devices and output to a CSV File (-v)
python MCIM.py -t -l https://host:port -u root -p password -n micron/cimv2
-v "C:\\Users\\XYZ\\enumerationSAS.csv"

## Get instance of a specific SAS Device and output to a CSV File (-v)
python MCIM.py -g -d "naa.5000c50030155387" -y "MICRON_SASDevice" -l https://host:port -u root -p
password -n micron/cimv2  -j "C:\Users\krao\enumerateSpecific.json"

## Sanitize a SAS Drive
python MCIM.py -z -d "naa.5000c5003014576b" -l https://host:port -u root
-p password -n micron/cimv2


## Secure Erase a SATA Drive
python MCIM.py -z -d "naa.5000c5003014576b" -l https://host:port -u root
-p password -n micron/cimv2


## Update Firmware for a SAS/SATA Drive. SAS example shown below. For SATA, change device type to MICRON_SATADevice.
python MCIM.py -w -f "//scratch//bfriedma//fwbin-m600" -d "naa.5000c50030155387" -y "MICRON_SASDevice" -l
https://host:port -u root -p password -n micron/cimv2


## Refresh SMART Data for a SAS/SATA Drive (Recommended after a FW update)
## SAS example shown below. For SATA, change device type to MICRON_SASDevice.
python MCIM.py -r  -d "naa.500a07510f7fc945" -y "MICRON_SATADevice" -l https://host:port -u root
-p password -n micron/cimv2


