/**
*  Direct DSC
*
*   A basic test application for direction communication with the Alarm Server on the local LAN
*
*   Copyright 2014 Matt Martz
*   Copyright 2015 Sean Kendall Schneyer
*
*
*  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
*  in compliance with the License. You may obtain a copy of the License at:
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
*  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
*  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
*  for the specific language governing permissions and limitations under the License.
*
*/

preferences {
    input("hostpassword", "password", title: "Server Password:", description: "Note: this is sent in the clear (for now).  Don't use something stupid")
    input("hostaddress", "text", title: "IP Address for Envisalink:", description: "Ex: 10.0.0.12 or 192.168.0.4 (no http://)")
    input("hostport", "number", title: "Port of Envisalink (or Server)", description: "port")
}

metadata {
    // Automatically generated. Make future change here.
    definition (name: "Basic Direct", author: "Sean Schneyer") {
        capability "Alarm"
        command "arm"
        attribute "partition1", "string"
        attribute "alarmStatus", "string"
        attribute "alarmstate", "string"
    }

    // simulator metadata
    simulator {

    }

    // UI tile definitions
    tiles {
        standardTile("button","device.mainState", width: 2, height: 2, canChangeIcon: true) {
            state "default", label: 'Default', action: "arm", icon: "st.Home.home2", backgroundColor: "#79b821", nextState: "arming"
            state "arm", label: 'Armed', action: "disarm", icon: "st.Home.home3", backgroundColor: "#b82078", nextState: "disarming"
 
        }
        standardTile("nightbutton","device.button", width: 1, height: 1, canChangeIcon: true) {
            state "default", label: 'Night Arm', action: "nightarm", icon: "st.Weather.weather4", backgroundColor: "#2078b8", nextState: "default"            
        }
        main (["button"])
        details(["button"])
    }
}

// Parse incoming device messages to generate events
def parse(String description) {

}

def arm() {
    log.debug "Arming..."
    // contactEnvisalinkJson("arm")
    apiGet()
    
}



def refresh() {
    log.debug "Executing 'refresh' which is actually poll()"
    // poll()
}



private apiGet() {
    LOG("In apiGet...")
    LOG("apiGet(${path})")

    def headers = [
        HOST:       192.168.1.201:8112,
        Accept:     "*/*"
    ]

    LOG(headers)
    
    def httpRequest = [
        method:     'GET',
        path:       '/api?',
        headers:    headers
    ]
    LOG(httpRequest)
    return new physicalgraph.device.HubAction(httpRequest)
}


private String convertIPtoHex(ipAddress) { 
    String hex = ipAddress.tokenize( '.' ).collect {  String.format( '%02x', it.toInteger() ) }.join()
    log.debug "IP address entered is $ipAddress and the converted hex code is $hex"
    return hex

}

private String convertPortToHex(port) {
    String hexport = port.toString().format( '%04x', port.toInteger() )
    log.debug hexport
    return hexport
}

private Integer convertHexToInt(hex) {
    Integer.parseInt(hex,16)
}


private String convertHexToIP(hex) {
    log.debug("Convert hex to ip: $hex") 
    [convertHexToInt(hex[0..1]),convertHexToInt(hex[2..3]),convertHexToInt(hex[4..5]),convertHexToInt(hex[6..7])].join(".")
}

private getHostAddress() {
    def parts = device.deviceNetworkId.split(":")
    log.debug device.deviceNetworkId
    def ip = convertHexToIP(parts[0])
    def port = convertHexToInt(parts[1])
    return ip + ":" + port
}

private def LOG(message) {
    log.trace message
}

