#python_version  :2.7.6
#dependency modules:
# pywbem-0.80_dev-py2.7.egg
# M2Crypt0 (for windows 64-bit: M2CryptoWin64-0.21.1_3-py2.7.egg-info)
# argparse (if using python 2.6)
import traceback
import argparse
import csv
import json
import pywbem
from pywbem import WBEMConnection
from pywbem import CIMInstanceName
import re

MICRON_DEVICE = "MICRON_Device"
MICRON_SATA_DEVICE="MICRON_SATADevice"
MICRON_SAS_DEVICE="MICRON_SASDevice"

CIM_METHOD_SANITIZE="SanitizeBlockErase"
CIM_METHOD_SECURE_ERASE="SecureErase"
CIM_METHOD_UPDATE_DATA="UpdateDeviceData"
CIM_METHOD_UPDATE_FW="UpdateFirmware"

def getArguments() :
    parser = argparse.ArgumentParser()
    parser.add_argument('-l', '--url', required= True,
                        help="CIM URL of the format: https://<host>:<port>")
    parser.add_argument('-u', '--user', required= True, help="User Name")
    parser.add_argument('-p', '--password', required= True, help="Password")
    parser.add_argument('-n', '--namespace', required= True, help="CIM Namespace e.g. 'micron/cimv2'")


    parser.add_argument('-e', '--enum', action='store_true', help="Enumerate Micron Devices")
    parser.add_argument('-s', '--enumSAS', action='store_true', help="Enumerate SAS Micron Devices")
    parser.add_argument('-t', '--enumSATA', action='store_true', help="Enumerate SATA Micron Devices")
    parser.add_argument('-g', '--getInstance', action='store_true', help="Get Specific Micron Device using the "
                                      "Path parameter. Please provide device ID and device type as a command line arg")

    parser.add_argument('-j', '--outputJSON', help="Enumeration of Devices can be output to a JSON file if user "
                            "wishes to. Provide full path including file name e.g. C:\\Users\\xyz\\EnumAllMicron.json")
    parser.add_argument('-v', '--outputCSV', help="Enumeration of Devices can be output to a CSV file if user "
                            "wishes to. Provide full path including file name e.g. C:\\Users\\xyz\\EnumAllMicron.csv")
    parser.add_argument('-z', '--sanitize', action='store_true', help="Sanitize Micron SAS drive. Please "
                                                 "provide device ID as a command line arg")
    parser.add_argument('-c', '--secureErase', action='store_true', help="Secure Erase Micron SATA drive. Please "
                                                    "provide device ID as a command line arg")
    parser.add_argument('-r', '--refreshSMART', action='store_true', help="Refresh SMART Data for a Micron SATA/SAS "
                                            "drive. Please provide device ID and device type as a command line arg")
    parser.add_argument('-w', '--updateFW', action='store_true', help="Update FW Version for Micron SATA/SAS drive. "
                                                 "Please provide device ID, device type and full path with filename"
                                                                      " for FW binary as a command line arg")

    parser.add_argument('-f', '--fwFilePath', help="Provide full path with filename for FW binary as a "
                    "command line arg for update firmware operation. The file must already exist on the ESXi host.")
    parser.add_argument('-d', '--deviceID', help="Device ID of the drive you wish to perform the operation on")
    parser.add_argument('-y', '--deviceType', help="Device Type of the drive you wish to perform the operation on: "
                                                   "MICRON_SATADevice or MICRON_SASDevice")

    parsedArgs = parser.parse_args()
    return parsedArgs


def checkArgsForErrors(parsedArgs):
    errorFlag = False
    if ((parsedArgs.sanitize and not parsedArgs.deviceID) or
        (parsedArgs.secureErase and not parsedArgs.deviceID) or
        (parsedArgs.refreshSMART and not parsedArgs.deviceID) or
        (parsedArgs.updateFW and not parsedArgs.deviceID) or
            (parsedArgs.getInstance and not parsedArgs.deviceID)):
        print("Error: Need to provide device ID for this operation using -d or --deviceId command line argument")
        errorFlag = True

    if((parsedArgs.updateFW and not parsedArgs.deviceID)):
        print("Error: Need to provide full path with filename for FW binary as a command line arg for update "
              "firmware operation. The file must already exist on the ESXi host.")
        errorFlag = True

    if ((parsedArgs.refreshSMART and not parsedArgs.deviceType) or
        (parsedArgs.updateFW and not parsedArgs.deviceType) or
        (parsedArgs.getInstance and not parsedArgs.deviceType)):
        print("Error: Need to provide device type for this operation using -y or --deviceType command line argument")
        errorFlag = True
    return errorFlag

#Generic Functions to write to a file
def outputToCSVFile(fileNameAndPath, micronDevices):
    outputFileHandle = None
    try:
        outputFileHandle = open(fileNameAndPath, 'wb')
        device_array = []
        headers = []
        for deviceID in micronDevices.keys():
            deviceProperties = {}
            for k, v in micronDevices[deviceID].properties.iteritems():
                deviceProperties[k] = v.value
            device_array.append(deviceProperties)
            headers.extend(deviceProperties.keys())

        orderedListHeaders = set(headers)
        csvwriter = csv.DictWriter(outputFileHandle, dialect='excel', delimiter=',', extrasaction='ignore',
                                   fieldnames=orderedListHeaders)
        csvwriter.writerow(dict((fn,fn) for fn in orderedListHeaders))
        for row in device_array:
            csvwriter.writerow(row)

        print "Enumeration output successfully saved in ", fileNameAndPath

    except Exception:
        print "An exception occurred when trying to write to a CSV file. Exception: "  + \
                        traceback.format_exc() + "\n"
    finally:
        if outputFileHandle is not None :
            outputFileHandle.close()


def outputToJSONFile(fileNameAndPath, micronDevices):
    outputFileHandle = None
    try:
        outputFileHandle = open(fileNameAndPath, 'wb')
        device_array = []

        for deviceID in micronDevices.keys():
            deviceProperties = {}
            for k, v in micronDevices[deviceID].properties.iteritems():
                deviceProperties[k] = v.value
            device_array.append(deviceProperties)

        json.dump(device_array, outputFileHandle)

        print "Enumeration output successfully saved in ", fileNameAndPath
    except Exception:
        print "An exception occurred when trying to write to a JSON file. Exception: "  + \
                        traceback.format_exc() + "\n"
    finally:
        if outputFileHandle is not None :
            outputFileHandle.close()

def getInstance(url, user, password, namespace, deviceId, deviceType, printEnumToScreen=False):
    micronDevices = {}

    keybindings = {'DeviceID': deviceId}

    # Create a connection to the CIM
    wbemConnection = WBEMConnection(url, (user, password), namespace)

    if wbemConnection != None:
        try:
            cimInstanceName = CIMInstanceName(deviceType, keybindings, url, namespace)
            # Enumerate specific Micron SAS or SATA drive
            cimInstance = wbemConnection.GetInstance(cimInstanceName)
            index=0
            if cimInstance==None:
                print "No Micron device found during getInstance"
            else:
                deviceProperties = {}
                for k, v in cimInstance.properties.iteritems():
                    deviceProperties[k] = v.value

                if printEnumToScreen == True:
                    print "Properties for Device #", index+1, ":"
                    print "Device ID: " , deviceProperties["DeviceID"]
                    print json.dumps(deviceProperties)

                micronDevices[deviceProperties["DeviceID"]] = cimInstance

                index += 1
                if printEnumToScreen == True:
                    print "===================================================="
        except:
             print "An exception occurred when attempting to get specific Micron device. Please " \
                                                              "check that  all parameters e.g. user, namespace, " \
                                                              "device ID, device type etc are correct. " \
                                                              "Exception: " \
                                                               + traceback.format_exc() + "\n"
    else:
        print "Could not connect to the CIM. Please check that the command line " \
              "arguments for connection are accurate"

    return (wbemConnection, micronDevices)


def enumerateMicronDevices(url, user, password, namespace, deviceType, printEnumToScreen=False):
    micronDevices = {}
    # Create a connection to the CIM
    wbemConnection = WBEMConnection(url, (user, password), namespace)

    if wbemConnection != None:
        try:
            # Enumerate all Micron SAS and SATA drives
            cimInstances = wbemConnection.EnumerateInstances(deviceType, namespace)
            index=0
            if len(cimInstances) == 0:
                print "No Micron devices found during enumeration"
            for cimInstance in cimInstances:
                deviceProperties = {}
                for k, v in cimInstance.properties.iteritems():
                    deviceProperties[k] = v.value

                if printEnumToScreen == True:
                    print "Properties for Device #", index+1, ":"
                    print "Device ID: " , deviceProperties["DeviceID"]
                    print json.dumps(deviceProperties)

                micronDevices[deviceProperties["DeviceID"]] = cimInstance

                index += 1
                if printEnumToScreen == True:
                    print "===================================================="
        except:
             print "An exception occurred when attempting to enumerate Micron devices. Please " \
                                                         "Please check that all parameters e.g user, password, namespace" \
                                                                    ", device type etc are correct. " \
                                                                    "Exception: " \
                                                                    + traceback.format_exc() + "\n"
    else:
        print "Could not connect to the CIM. Please check that the command line " \
              "arguments for connection are accurate"

    return (wbemConnection, micronDevices)

# Generic wrapper around CIM InvokeMethod
def micronCIMInvokeMethod(url, user, password, namespace, deviceId, deviceType, methodName, params=None):
    (wbemConnection, micronDevices) = getInstance(url, user, password, namespace, deviceId, deviceType)
    returnValue = None
    outParams = None
    errorFlag = False

    if len(micronDevices) != 0 :
        if micronDevices[deviceId] :
            try:
                if(params is None):
                    (returnValue, outParams) = wbemConnection.InvokeMethod(methodName,
                                                                  micronDevices[deviceId].path)
                else:
                    if (hasattr(pywbem, '__version__') and re.search('0.8.0', pywbem.__version__)) :
                        (returnValue, outParams) = wbemConnection.InvokeMethod(methodName,
                                                                  micronDevices[deviceId].path, params)
                    else:
                        (returnValue, outParams) = wbemConnection.InvokeMethod(methodName,
                                                                  micronDevices[deviceId].path, **params)
            except:
                print "An exception occurred when attempting to invoke an operation on a  specific Micron device. " \
                                                        "Please check that all parameters e.g user, namespace, device ID" \
                                                        ", device type etc are correct. " \
                                                        "Exception: " \
                                                        + traceback.format_exc() + "\n"
        else:
            print "\nNo Micron drive with Device ID: ", deviceId, " was found. Please check that the " \
                                                                       "Device ID is correct"
            errorFlag = True

    else:
        print "\nNo Micron drives found for performing this operation."
        errorFlag = True

    return (returnValue, outParams, errorFlag)



if __name__=="__main__":
    args = getArguments()
    errorFlag = checkArgsForErrors(args)
    if(errorFlag == True): # command line argument errors are shown to the user in checkArgsForErrors
        exit(0)

    if(args.enum):
        print "Enumerating all Micron Devices:"
        (wbemConnection, micronDevices) = enumerateMicronDevices(args.url, args.user, args.password, args.namespace,
                                                                 MICRON_DEVICE, True)
        if(args.outputCSV):
            outputToCSVFile(args.outputCSV, micronDevices)
        elif(args.outputJSON):
            outputToJSONFile(args.outputJSON, micronDevices)
    elif(args.enumSAS):
        print "Enumerating Micron SAS Devices:"
        (wbemConnection, micronDevices) = enumerateMicronDevices(args.url, args.user, args.password, args.namespace,
                                                                 MICRON_SAS_DEVICE, True)
        if(args.outputCSV):
            outputToCSVFile(args.outputCSV, micronDevices)
        elif(args.outputJSON):
            outputToJSONFile(args.outputJSON, micronDevices)
    elif(args.enumSATA):
        print "Enumerating Micron SATA Devices:"
        (wbemConnection, micronDevices) = enumerateMicronDevices(args.url, args.user, args.password, args.namespace,
                                                                 MICRON_SATA_DEVICE, True)
        if(args.outputCSV):
            outputToCSVFile(args.outputCSV, micronDevices)
        elif(args.outputJSON):
            outputToJSONFile(args.outputJSON, micronDevices)
    elif(args.sanitize):
        print "Sanitizing SAS Drive with Device ID: ", args.deviceID
        (returnValue, outParams, errorFlag) = micronCIMInvokeMethod(args.url, args.user, args.password, args.namespace,
                                                         args.deviceID, MICRON_SAS_DEVICE,
                                            CIM_METHOD_SANITIZE)
        if returnValue == None and errorFlag == False:
            print "Successfully sanitized device with Device ID ", \
                    args.deviceID

    elif(args.updateFW):
        print "Updating Firmware for Drive with Device ID: ", args.deviceID
        if (hasattr(pywbem, '__version__') and re.search('0.8.0', pywbem.__version__)) :
            (returnValue, outParams, errorFlag) = micronCIMInvokeMethod(args.url, args.user, args.password,
                                                                        args.namespace, args.deviceID, args.deviceType,
                                            CIM_METHOD_UPDATE_FW, [("filePath",args.fwFilePath)])
        else:
            (returnValue, outParams, errorFlag) = micronCIMInvokeMethod(args.url, args.user, args.password,
                                                                        args.namespace, args.deviceID, args.deviceType,
                                            CIM_METHOD_UPDATE_FW, {"filePath":args.fwFilePath})
        if returnValue == None and errorFlag == False:
            print "Successfully updated firmware for device with Device ID ", \
                    args.deviceID, ". It is recommended that you refresh SMART Data after a firmware update operation"

    elif(args.refreshSMART):
        print "Refreshing SMART Data for Drive with Device ID: ", args.deviceID
        (returnValue, outParams, errorFlag) = micronCIMInvokeMethod(args.url, args.user, args.password, args.namespace,
                                                                    args.deviceID, args.deviceType,
                                            CIM_METHOD_UPDATE_DATA)
        if returnValue == None and errorFlag == False:
            print "Successfully refreshed SMART Data for device with Device ID ", \
                    args.deviceID

    elif(args.secureErase):
        print "Secure Erasing SATA Drive with Device ID: ", args.deviceID
        (returnValue, outParams, errorFlag) = micronCIMInvokeMethod(args.url, args.user, args.password, args.namespace,
                                                                    args.deviceID, MICRON_SATA_DEVICE,
                                            CIM_METHOD_SECURE_ERASE)
        if returnValue == None and errorFlag == False:
            print "Successfully Secure Erased SATA Drive with Device ID ", \
                    args.deviceID

    elif(args.getInstance):
        print "Enumerating specific Micron Device with Device ID: ", args.deviceID
        (wbemConnection, micronDevices) = getInstance(args.url, args.user, args.password, args.namespace,
                                                                 args.deviceID, args.deviceType, True)
        if(args.outputCSV):
            outputToCSVFile(args.outputCSV, micronDevices)
        elif(args.outputJSON):
            outputToJSONFile(args.outputJSON, micronDevices)
