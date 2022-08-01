/**
 *  
 *	Fibaro FGS-212 Relay Device Type. 
 *  
 *	Author: Stian K. Christiansen (NIMROOZ)
 *	email: admin@authlife.com
 *	Date: 04.10.2016	
 * 
 * 	 
 *	Device Type should support all the features of the Fibaro FGS-212 relay,
 *  Added the ability to change config parameters through the device preferences,
 *	current energy consumption in WH and cumulative energy consumption in kWh.
 * -BEWARE- coding done in halfsleep, so dont expect to much!
 */
 
metadata {
definition (name: "Fibaro FGS-212 Relay", namespace: "NIMROOZ", author: "Stian K. Christiansen") {
capability "Switch"
capability "Polling"
capability "Configuration"
capability "Refresh"
capability "Power Meter"
capability "Actuator"
capability "Sensor"


attribute "switch1", "string"


command "on1"
command "off1"
command "reset"
command "changeSingleParamAfterSecure"
command "configureAfterSecure"

fingerprint deviceId: "0x1001", inClusters:"0x20, 0x25, 0x27, 0x60, 0x70, 0x72, 0x73, 0x7A, 0x85, 0x86, 0x8E"
}

simulator {
status "on": "command: 2003, payload: FF"
status "off": "command: 2003, payload: 00"

		for (int i = 0; i <= 10000; i += 1000) {
			status "power  ${i} W": new physicalgraph.zwave.Zwave().meterV3.meterReport(
				scaledMeterValue: i, precision: 3, meterType: 4, scale: 2, size: 4).incomingMessage()
		}
		for (int i = 0; i <= 100; i += 10) {
			status "energy  ${i} kWh": new physicalgraph.zwave.Zwave().meterV3.meterReport(
				scaledMeterValue: i, precision: 3, meterType: 0, scale: 0, size: 4).incomingMessage()
		}

        ["FF", "00", "09", "0A", "21", "42", "63"].each { val ->
			reply "2001$val,delay 100,2602": "command: 2603, payload: $val"
		}
	}


tiles(scale: 2) {

	standardTile("switch1", "device.switch1", width: 6, height: 4, canChangeIcon: true) {
		state "on", label: "ON", action: "off1", icon: "st.switches.switch.on", backgroundColor: "#79b821"
		state "off", label: "OFF", action: "on1", icon: "st.switches.switch.off", backgroundColor: "#ffffff"
    }
    
    standardTile("ConfigureAfterSecure", "device.configure", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
		state "configure", label:"", action:"ConfigureAfterSecure", icon:"st.secondary.configure"
    }
    standardTile("reset", "device.energy", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
		state "default", label:'reset kWh', action:"reset"
	}
    valueTile("energy", "device.energy", decoration: "flat", width: 2, height: 2) {
			state "default", label:'${currentValue} kWh'
	}
    valueTile("power", "device.power", decoration: "flat", width: 2, height: 2) {
			state "default", label:'${currentValue} W'
	}

    main(["switch1","power","energy"])
    details(["switch1","energy","power","reset","configure"])
}
	preferences {
        def paragraph = "GROUP 0 - Relay behavior - Basic functionalities"
    	input name: "param16", type: "number", range: "0..1", defaultValue: "1", required: true,
            title: "16. State of the device after a power failure. " +
                   "The Relay will return to the last state before power failure.\n" +
                   "Available settings:\n" +
                   "0 = the Relay does not save the state before a power failure, it returns to „off” position,\n" +
                   "1 = the Relay restores its state before power failure.\n" +
                   "Default value: 1."
                   
                   input name: "param1", type: "enum", defaultValue: "-1", required: true,
            options: ["0" : "0",
                      "1" : "1",
                      "2" : "2",
                      "-1" : "-1"],
            title: "1. ALL ON/ALL OFF function. " +
                   "Parameter allows for activation/deactivation of Z-Wave commands enabling/disabling all devices located in direct range of the main controller.\n" +
                   "Available settings:\n" +
                   "0 = All ON not active, All OFF not active,\n" +
                   "1 = All ON not active, All OFF active,\n" +
                   "2 = All ON active, All OFF not active,\n" +
                   "-1 = All ON active, All OFF active.\n" +
                   "Default value: -1."

        input name: "param31", type: "number", range: "0..3", defaultValue: "2", required: true,
             title: "31. Response to Water Flooding Alarm.\n" +
                    "Available settings:\n" +
                    "0 = No reaction,\n" +
                    "1 = Turn on the load,\n" +
                    "2 = Turn off the load,\n" +
                    "3 = Load blinking.\n" +
                    "Default value: 2."
                    
                    
        input name: "param30", type: "number", range: "0..3", defaultValue: "3", required: true,
             title: paragraph + "\n\n" +
                    "30. Response to General Purpose Alarm.\n" +
                    "Available settings:\n" +
                    "0 = No reaction,\n" +
                    "1 = Turn off the load,\n" +
                    "2 = Turn on the load,\n" +
                    "3 = Load blinking.\n" +
                    "Default value: 3."
                    
        input name: "param32", type: "number", range: "0..3", defaultValue: "3", required: true,
             title: "32. Response to Smoke, CO or CO2 Alarm.\n" +
                    "Available settings:\n" +
                    "0 = No reaction,\n" +
                    "1 = Turn off the load,\n" +
                    "2 = Turn on the load,\n" +
                    "3 = Load blinking.\n" +
                    "Default value: 3."

        input name: "param33", type: "number", range: "0..3", defaultValue: "1", required: true,
             title: "33. Response to Temperature Alarm.\n" +
                    "Available settings:\n" +
                    "0 = No reaction,\n" +
                    "1 = Turn off the load,\n" +
                    "2 = Turn on the load,\n" +
                    "3 = Load blinking.\n" +
                    "Default value: 1."

        input name: "param39", type: "number", range: "1..32767", defaultValue: "600", required: true,
             title: "39. Time of alarm state.\n" +
                    "Available settings: 1-65535 (1-65535 seconds).\n" +
                    "Default value: 600."
  }
}

def parse(String description) {
    def result = []
    def cmd = zwave.parse(description)
    if (cmd) {
        result += zwaveEvent(cmd)
        log.debug "Parsed ${cmd} to ${result.inspect()}"
    } else {
        log.debug "Non-parsed event: ${description}"
    }
    return result
}

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicReport cmd)
{
    def result
    if (cmd.value == 0) {
        result = createEvent(name: "switch", value: "off")
    } else {
        result = createEvent(name: "switch", value: "on")
    }
    return result
}

def zwaveEvent(physicalgraph.zwave.commands.basicv1.BasicSet cmd) {
	sendEvent(name: "switch", value: cmd.value ? "on" : "off", type: "digital")
    def result = []
    result << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:1, commandClass:37, command:2).format()
    result << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:2, commandClass:37, command:2).format()
    //result << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:3, commandClass:37, command:2).format()
    response(delayBetween(result, 1000)) // returns the result of reponse()
}

def zwaveEvent(physicalgraph.zwave.commands.switchbinaryv1.SwitchBinaryReport cmd)
{
    sendEvent(name: "switch", value: cmd.value ? "on" : "off", type: "digital")
    def result = []
    result << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:1, commandClass:37, command:2).format()
    result << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:2, commandClass:37, command:2).format()
    //result << zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:3, commandClass:37, command:2).format()
    response(delayBetween(result, 1000)) // returns the result of reponse()
}

def zwaveEvent(physicalgraph.zwave.commands.meterv3.MeterReport cmd) {
	log.trace(cmd)
	if (cmd.meterType == 1) {
		if (cmd.scale == 0) {
			return createEvent(name: "energy", value: cmd.scaledMeterValue, unit: "kWh")
		} else if (cmd.scale == 1) {
			return createEvent(name: "energy", value: cmd.scaledMeterValue, unit: "kVAh")
		} else if (cmd.scale == 2) {
			return createEvent(name: "power", value: Math.round(cmd.scaledMeterValue), unit: "W")
		} else {
			return createEvent(name: "electric", value: cmd.scaledMeterValue, unit: ["pulses", "V", "A", "R/Z", ""][cmd.scale - 3])
		}
	}
}

def zwaveEvent(physicalgraph.zwave.commands.multichannelv3.MultiChannelCapabilityReport cmd) 
{
    log.debug "multichannelv3.MultiChannelCapabilityReport $cmd"
    if (cmd.endPoint == 1 ) {
        def currstate = device.currentState("switch1").getValue()
        if (currstate == "on")
        	sendEvent(name: "switch1", value: "off", isStateChange: true, display: false)
        else if (currstate == "off")
        	sendEvent(name: "switch1", value: "on", isStateChange: true, display: false)
}
}

def zwaveEvent(physicalgraph.zwave.commands.multichannelv3.MultiChannelCmdEncap cmd) {
	def map = [ name: "switch$cmd.sourceEndPoint" ]

	if (cmd.commandClass == 37){
        if (cmd.parameter == [0]) {
            map.value = "off"
        }
        if (cmd.parameter == [255]) {
            map.value = "on"
        }
        createEvent(map)
    }
}

def zwaveEvent(physicalgraph.zwave.Command cmd) {
    // This will capture any commands not handled by other instances of zwaveEvent
    // and is recommended for development so you can see every command the device sends
    return createEvent(descriptionText: "${device.displayName}: ${cmd}")
}

def refresh() {
	log.trace("trace")
	secureSequence([
		zwave.meterV2.meterGet(scale: 0),
		zwave.meterV2.meterGet(scale: 2)
	])
}

def poll() {
	log.trace("poll")
	secureSequence([
		zwave.meterV2.meterGet(scale: 0),
		zwave.meterV2.meterGet(scale: 2)
	])
}

def reset() {
	log.trace("reset")
	return secureSequence([
    	zwave.switchMultilevelV1.switchMultilevelGet(),
		zwave.meterV2.meterReset(),
		zwave.meterV2.meterGet(scale: 0),
        zwave.meterV2.meterGet(scale: 2)
	])
}

def changeSingleParamAfterSecure(paramNum, paramSize, paramValue) {
	log.debug "changeSingleParamAfterSecure(paramNum: $paramNum, paramSize: $paramSize, paramValue: $paramValue)"
    def cmds = secureSequence([
    	zwave.configurationV1.configurationSet(parameterNumber: paramNum, size: paramSize, scaledConfigurationValue: paramValue)
        ])
    cmds
}

def configureAfterSecure() {
    log.debug "configureAfterSecure()"
        def cmds = secureSequence([
            zwave.configurationV1.configurationSet(parameterNumber: 1, size: 1, scaledConfigurationValue: param1.toInteger()),
            zwave.configurationV1.configurationSet(parameterNumber: 2, size: 1, scaledConfigurationValue: param2.toInteger()),
            zwave.configurationV1.configurationSet(parameterNumber: 3, size: 1, scaledConfigurationValue: param3.toInteger()),
            zwave.configurationV1.configurationSet(parameterNumber: 4, size: 2, scaledConfigurationValue: param4.toInteger()),
            zwave.configurationV1.configurationSet(parameterNumber: 5, size: 1, scaledConfigurationValue: param5.toInteger()),
            zwave.configurationV1.configurationSet(parameterNumber: 6, size: 2, scaledConfigurationValue: param6.toInteger()),
            zwave.configurationV1.configurationSet(parameterNumber: 7, size: 1, scaledConfigurationValue: param7.toInteger()),
            zwave.configurationV1.configurationSet(parameterNumber: 8, size: 2, scaledConfigurationValue: param8.toInteger()),
            zwave.configurationV1.configurationSet(parameterNumber: 9, size: 1, scaledConfigurationValue: param9.toInteger()),
            zwave.configurationV1.configurationSet(parameterNumber: 10, size: 2, scaledConfigurationValue: param10.toInteger()),
            zwave.configurationV1.configurationSet(parameterNumber: 11, size: 2, scaledConfigurationValue: param11.toInteger()),
            zwave.configurationV1.configurationSet(parameterNumber: 13, size: 1, scaledConfigurationValue: param13.toInteger()),
            zwave.configurationV1.configurationSet(parameterNumber: 15, size: 1, scaledConfigurationValue: param15.toInteger()),
            zwave.configurationV1.configurationSet(parameterNumber: 16, size: 2, scaledConfigurationValue: param16.toInteger()),
            zwave.configurationV1.configurationSet(parameterNumber: 19, size: 1, scaledConfigurationValue: param19.toInteger()),
            zwave.configurationV1.configurationSet(parameterNumber: 20, size: 1, scaledConfigurationValue: param20.toInteger()),
            zwave.configurationV1.configurationSet(parameterNumber: 21, size: 1, scaledConfigurationValue: param21.toInteger()),
            zwave.configurationV1.configurationSet(parameterNumber: 22, size: 1, scaledConfigurationValue: param22.toInteger()),
            zwave.configurationV1.configurationSet(parameterNumber: 23, size: 1, scaledConfigurationValue: param23.toInteger()),
            zwave.configurationV1.configurationSet(parameterNumber: 24, size: 1, scaledConfigurationValue: param24.toInteger()),
            zwave.configurationV1.configurationSet(parameterNumber: 25, size: 1, scaledConfigurationValue: param25.toInteger()),
            zwave.configurationV1.configurationSet(parameterNumber: 26, size: 1, scaledConfigurationValue: param26.toInteger()),
            zwave.configurationV1.configurationSet(parameterNumber: 27, size: 1, scaledConfigurationValue: param27.toInteger()),
            zwave.configurationV1.configurationSet(parameterNumber: 28, size: 1, scaledConfigurationValue: param28.toInteger()),
            zwave.configurationV1.configurationSet(parameterNumber: 29, size: 1, scaledConfigurationValue: param29.toInteger()),
            zwave.configurationV1.configurationSet(parameterNumber: 30, size: 1, scaledConfigurationValue: param30.toInteger()),
            zwave.configurationV1.configurationSet(parameterNumber: 32, size: 1, scaledConfigurationValue: param32.toInteger()),
            zwave.configurationV1.configurationSet(parameterNumber: 34, size: 1, scaledConfigurationValue: param34.toInteger()),
            zwave.configurationV1.configurationSet(parameterNumber: 35, size: 1, scaledConfigurationValue: param35.toInteger()),
            zwave.configurationV1.configurationSet(parameterNumber: 37, size: 1, scaledConfigurationValue: param37.toInteger()),
            zwave.configurationV1.configurationSet(parameterNumber: 39, size: 2, scaledConfigurationValue: param39.toInteger()),
            zwave.configurationV1.configurationSet(parameterNumber: 40, size: 1, scaledConfigurationValue: param40.toInteger()),
            zwave.configurationV1.configurationSet(parameterNumber: 41, size: 1, scaledConfigurationValue: param41.toInteger()),
            zwave.configurationV1.configurationSet(parameterNumber: 42, size: 1, scaledConfigurationValue: param42.toInteger()),
            zwave.configurationV1.configurationSet(parameterNumber: 43, size: 1, scaledConfigurationValue: param43.toInteger()),
            zwave.configurationV1.configurationSet(parameterNumber: 44, size: 2, scaledConfigurationValue: param44.toInteger()),
            zwave.configurationV1.configurationSet(parameterNumber: 45, size: 1, scaledConfigurationValue: param45.toInteger()),
            zwave.configurationV1.configurationSet(parameterNumber: 46, size: 1, scaledConfigurationValue: param46.toInteger()),
            zwave.configurationV1.configurationSet(parameterNumber: 47, size: 1, scaledConfigurationValue: param47.toInteger()),
            zwave.configurationV1.configurationSet(parameterNumber: 48, size: 1, scaledConfigurationValue: param48.toInteger()),
            zwave.configurationV1.configurationSet(parameterNumber: 49, size: 1, scaledConfigurationValue: param49.toInteger()),
            zwave.configurationV1.configurationSet(parameterNumber: 50, size: 1, scaledConfigurationValue: param50.toInteger()),
            zwave.configurationV1.configurationSet(parameterNumber: 52, size: 2, scaledConfigurationValue: param52.toInteger()),
            zwave.configurationV1.configurationSet(parameterNumber: 53, size: 2, scaledConfigurationValue: param53.toInteger()),
            zwave.configurationV1.configurationSet(parameterNumber: 54, size: 1, scaledConfigurationValue: param54.toInteger()),
            zwave.configurationV1.configurationSet(parameterNumber: 58, size: 1, scaledConfigurationValue: param58.toInteger()),
            zwave.configurationV1.configurationSet(parameterNumber: 59, size: 2, scaledConfigurationValue: param59.toInteger())
        ])

        // Register for Group 1
        if(paramAssociationGroup1) {
        	cmds << secure(zwave.associationV2.associationSet(groupingIdentifier:1, nodeId: [zwaveHubNodeId]))
        }
        else {
        	cmds << secure(zwave.associationV2.associationRemove(groupingIdentifier:1, nodeId: [zwaveHubNodeId]))
        }
        // Register for Group 2
        if(paramAssociationGroup2) {
        	cmds << secure(zwave.associationV2.associationSet(groupingIdentifier:2, nodeId: [zwaveHubNodeId]))
        }
        else {
        	cmds << secure(zwave.associationV2.associationRemove(groupingIdentifier:2, nodeId: [zwaveHubNodeId]))
        }
        // Register for Group 3
        if(paramAssociationGroup3) {
        	cmds << secure(zwave.associationV2.associationSet(groupingIdentifier:3, nodeId: [zwaveHubNodeId]))
        }
        else {
        	cmds << secure(zwave.associationV2.associationRemove(groupingIdentifier:3, nodeId: [zwaveHubNodeId]))
        }
        // Register for Group 4
        if(paramAssociationGroup4) {
        	cmds << secure(zwave.associationV2.associationSet(groupingIdentifier:4, nodeId: [zwaveHubNodeId]))
        }
        else {
        	cmds << secure(zwave.associationV2.associationRemove(groupingIdentifier:4, nodeId: [zwaveHubNodeId]))
        }
        // Register for Group 5
        if(paramAssociationGroups5) {
        	cmds << secure(zwave.associationV2.associationSet(groupingIdentifier:5, nodeId: [zwaveHubNodeId]))
        }
        else {
        	cmds << secure(zwave.associationV2.associationRemove(groupingIdentifier:5, nodeId: [zwaveHubNodeId]))
        }

	cmds
}

def configure() {
	// Wait until after the secure exchange for this
    log.debug "configure()"
}

def updated() {
	log.debug "updated()"
	response(["delay 2000"] + configureAfterSecure() + refresh())
}

private secure(physicalgraph.zwave.Command cmd) {
	log.trace(cmd)
	zwave.securityV1.securityMessageEncapsulation().encapsulate(cmd).format()
}

private secureSequence(commands, delay=200) {
	log.debug "$commands"
	delayBetween(commands.collect{ secure(it) }, delay)
}
def on1() {
    delayBetween([
        zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:1, commandClass:37, command:1, parameter:[255]).format(),
        zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:1, commandClass:37, command:2).format()
    ], 1000)
}

def off1() {
    delayBetween([
        zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:1, commandClass:37, command:1, parameter:[0]).format(),
        zwave.multiChannelV3.multiChannelCmdEncap(sourceEndPoint:1, destinationEndPoint:1, commandClass:37, command:2).format()
    ], 1000)
}