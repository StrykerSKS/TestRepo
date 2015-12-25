
metadata {
	definition (name: "Crash Thermo", namespace: "StrykerSKS", author: "StrykerSKS") {
		capability "Actuator"
		capability "Thermostat"
		
		  
	}

	simulator { }

    	tiles(scale: 2) {
        
        
		multiAttributeTile(name:"summary", type:"thermostat", width:6, height:4) {
			tileAttribute("device.temperature", key: "PRIMARY_CONTROL") {
				attributeState("default", label:'${currentValue}', unit:"F")
			}
            
            // TODO: Need a setTemperature action 
			tileAttribute("device.temperature", key: "VALUE_CONTROL") {
                attributeState("default", action: "setTemperature")
			}
            tileAttribute("device.humidity", key: "SECONDARY_CONTROL") {
				attributeState("default", label:'${currentValue}%', unit:"%")
			}  
            
			// TODO: Fix the thermostatOperatingState to use idle, heating, cooling and perhaps emergency heat?
			tileAttribute("device.thermostatOperatingState", key: "OPERATING_STATE") {
				attributeState("idle", backgroundColor:"#44b621")
				attributeState("heating", backgroundColor:"#ffa81e")
				attributeState("cooling", backgroundColor:"#269bd2")
                attributeState("auto", backgroundColor:"ff0000")
                attributeState("?", backgroundColor:"ff0000")
			}
            
			tileAttribute("device.thermostatMode", key: "THERMOSTAT_MODE") {
				attributeState("off", label:'${name}')
				attributeState("heat", label:'${name}')
				attributeState("cool", label:'${name}')
                attributeState("auto", label:'${name}')
			}
            tileAttribute("device.heatingSetpoint", key: "HEATING_SETPOINT") {
            	attributeState("default", label:'${currentValue}', unit:"F")
            }
			tileAttribute("device.coolingSetpoint", key: "COOLING_SETPOINT") {
				attributeState("default", label:'${currentValue}', unit:"F")
			}
            
        }
    }
        
