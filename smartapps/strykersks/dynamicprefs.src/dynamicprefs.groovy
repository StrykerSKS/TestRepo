definition(
    name: "dynamicprefs",
    namespace: "StrykerSKS",
    author: "SmartThings",
    description: "passing params via href element to a dynamic page",
    category: "",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png")



preferences {
    page(name: "page1", title: "Select sensor and actuator types", nextPage: "page2", uninstall: true) {
        section {
            input("sensorType", "enum", options: [
                "contactSensor":"Open/Closed Sensor",
                "motionSensor":"Motion Sensor",
                "switch": "Switch",
                "moistureSensor": "Moisture Sensor"])

            input("actuatorType", "enum", options: [
                "switch": "Light or Switch",
                "lock": "Lock"]
            )
        }
    }

    page(name: "page2", title: "Select devices and action", install: true, uninstall: true)

}

def page2() {
    dynamicPage(name: "page2") {
        section {
            input(name: "sensor", type: "capability.$sensorType", title: "If the $sensorType device")
            input(name: "action", type: "enum", title: "is", options: attributeValues(sensorType))
        }
        section {
            input(name: "actuator", type: "capability.$actuatorType", title: "Set the $actuatorType")
            input(name: "action", type: "enum", title: "to", options: actions(actuatorType))
         }

    }
}

private attributeValues(attributeName) {
    switch(attributeName) {
        case "switch":
            return ["on","off"]
        case "contactSensor":
            return ["open","closed"]
        case "motionSensor":
            return ["active","inactive"]
        case "moistureSensor":
            return ["wet","dry"]
        default:
            return ["UNDEFINED"]
    }
}

private actions(attributeName) {
    switch(attributeName) {
        case "switch":
            return ["on","off"]
        case "lock":
            return ["lock","unlock"]
        default:
            return ["UNDEFINED"]
    }
}
