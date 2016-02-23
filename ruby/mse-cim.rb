#!/usr/bin/env ruby

# Ruby wrapper around wbemcli
# tested/written with Ruby 2.2.3
# depends on wbemcli, available from most package management repositories
# http://sourceforge.net/projects/sblim/files/sblim-wbemcli/

require 'optparse'

DEVICE_TYPES = ["MICRON_Device", "MICRON_SATADevice", "MICRON_SASDevice"]
CIM_METHODS = ["UpdateDeviceData", "UpdateFirmware", "SecureErase", "SanitizeBlockErase"]

# Command Line Options
options = {}
optparse = OptionParser.new do |opts|

	opts.banner = "Usage: mse-cim.rb [options]"

	opts.on("-h", "--host HOST", "Hostname or ip:port") do |h|
		options[:host] = h
	end

	opts.on("-u", "--user USER", "Username") do |u|
		options[:user] = u
	end

	opts.on("-p", "--password PASSWORD", "Password") do |p|
		options[:password] = p
	end

	options[:enumerate] = false
	opts.on("-e", "--enumerate [DEVICE_TYPE]", DEVICE_TYPES, "Enumerate instances of the specified device type: #{DEVICE_TYPES}") do |type|
		options[:enumerate] = true
		options[:device_type] = type || nil
	end

	options[:getinstance] = false
	opts.on("-g", "--getinstance DEVICE_ID", "Get a specific instance of a drive by its ID. Must also specify device type with -t") do |d|
		options[:getinstance] = true
		options[:device_id] = d
	end

	opts.on("-m", "--method METHOD_NAME", CIM_METHODS, "CIM Method: #{CIM_METHODS}") do |m|
		options[:method] = m
	end

	opts.on("-d", "--deviceid DEVICE_ID", "Device ID") do |d|
		options[:device_id] = d
	end

	opts.on("-t", "--devicetype [DEVICE_TYPE]", DEVICE_TYPES, "Device Type: #{DEVICE_TYPES}") do |t|
		options[:device_type] = t
	end

	opts.on("-f", "--filepath FILEPATH", "Filepath to firmware") do |f|
		options[:filepath] = f 
	end

	opts.on("--help", "Show usage options") do
		puts opts
		exit
	end

	opts.on("--examples", "Show usage examples") do
		puts "Enumerate all devices"
		puts "	mse-cim.rb -h x.x.x.x -u root -p password -e"
		puts "Enumerate only SATA devices"
		puts "	mse-cim.rb -h x.x.x.x -u root -p password -e MICRON_SATADevice"
		puts "Enumerate only SAS devices"
		puts "	mse-cim.rb -h x.x.x.x -u root -p password -e MICRON_SASDevice"
		puts "Get a single instance by its ID"
		puts "  mse-cim.rb -h x.x.x.x -u root -p password -g <DEVICE_ID> -t <DEVICE_TYPE>"
		puts "Update/refresh SMART data on a SATA device:"
		puts "	mse-cim.rb -h x.x.x.x -u root -p password -m UpdateDeviceData -d <DEVICE_ID> -t <DEVICE_TYPE>"
		puts "Secure Erase a SATA device"
		puts "	mse-cim.rb -h x.x.x.x -u root -p password -m SecureErase -d <DEVICE_ID>"
		puts "Sanitize a SAS device"
		puts "	mse-cim.rb -h x.x.x.x -u root -p password -m SanitizeBlockErase -d <DEVICE_ID>"
		puts "Update the firmware of a device"
		puts "	mse-cim.rb -h x.x.x.x -u root -p password -m UpdateFirmware -d <DEVICE_ID> -t <DEVICE_TYPE> -f </path/to/firmware>"
		exit
	end

end

begin
	# Check for missing arguments
	optparse.parse!
	mandatory = [:host, :user, :password]
	missing = mandatory.select{ |param| options[param].nil? }
	unless missing.empty?
		puts "Missing required arguments: #{missing.join(', ')}"
		puts optparse
		exit
	end
rescue OptionParser::InvalidOption, OptionParser::MissingArgument, OptionParser::InvalidArgument
	puts $!.to_s                                                           
  	puts optparse                                                          
  	exit                                             
end

# Parse options and setup the wbemcli command to run
command = "wbemcli WBEMCLI_ARGS 'https://USER:PASS@HOST/micron/cimv2:DEVICE_TYPE.OPTIONS"
command["HOST"] = options[:host]
command["USER"] = options[:user]
command["PASS"] = options[:password]
if options[:enumerate]
	command["WBEMCLI_ARGS"] = "ei -nl -noverify"
	if options[:device_type]
		command["DEVICE_TYPE"] = options[:device_type]
	else
		#default to all devices if not otherwise specified
		command["DEVICE_TYPE"] = "MICRON_Device"
	end
	command[".OPTIONS"] = "'"
elsif options[:getinstance]
	if options[:device_id].nil?
		puts "Error: Device ID required to retrieve a specific device."
		puts optparse
		exit
	end
	if options[:device_type].nil?
		puts "Error: The device type is required when retrieving a specific device."
		puts optparse
		exit
	end

	command["WBEMCLI_ARGS"] = "gi -nl -noverify"
	command["DEVICE_TYPE"] = options[:device_type]
	command["OPTIONS"] = "DeviceID=\"" + options[:device_id] + "\"'"

elsif options[:method]
	if options[:device_id].nil?
		puts "Error: Device ID required when specifying a CIM method."
		puts optparse
		exit
	end

	command["WBEMCLI_ARGS"] = "cm -nl -noverify"

	case options[:method]
	when "UpdateDeviceData"
		if options[:device_type].nil?
			puts "Error: The device type is required when updating device data."
			puts optparse
			exit
		end
		command["DEVICE_TYPE"] = options[:device_type]
		command["OPTIONS"] = "DeviceID=\"#{options[:device_id]}\",CreationClassName=\"\",SystemName=\"\",SystemCreationClassName=\"\"\' UpdateDeviceData"
	when "UpdateFirmware"
		if options[:device_type].nil?
			puts "Error: The device type is required when updating the firmware."
			puts optparse
			exit
		end
		if options[:filepath].nil?
			puts "Error: Path to firmware file required"
			puts optparse
			exit
		end
		command["DEVICE_TYPE"] = options[:device_type]
		command["OPTIONS"] = "DeviceID=\"#{options[:device_id]}\",CreationClassName=\"\",SystemName=\"\",SystemCreationClassName=\"\"\' UpdateFirmware.filepath=#{options[:filepath]}"
	when "SecureErase"
		command["DEVICE_TYPE"] = "MICRON_SATADevice"
		command["OPTIONS"] = "DeviceID=\"#{options[:device_id]}\",CreationClassName=\"\",SystemName=\"\",SystemCreationClassName=\"\"\' SecureErase"
	when "SanitizeBlockErase"
		command["DEVICE_TYPE"] = "MICRON_SASDevice"
		command["OPTIONS"] = "DeviceID=\"#{options[:device_id]}\",CreationClassName=\"\",SystemName=\"\",SystemCreationClassName=\"\"\' SanitizeBlockErase"
	end
else
	puts "Invalid command. Must -e enumerate devices or -m specify a CIM method."
	puts optparse
	exit
end

puts command
begin
	result = `#{command}`
	puts result
	returnStatus = $?.to_i
	puts "return status = #{returnStatus}"
rescue
	puts result
	returnStatus = $?.to_i
	puts "return status = #{returnStatus}"
end
