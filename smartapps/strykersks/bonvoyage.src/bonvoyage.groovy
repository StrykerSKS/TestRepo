/**
 *  Bon Voyage
 *
 *  Author: SmartThings
 *  Date: 2013-03-07
 *
 *  Monitors a set of presence detectors and triggers a mode change when everyone has left.
 */

preferences {
    section("When all of these people leave home") {
        input "people", "capability.presenceSensor", multiple: true
    }
    section("Change to this mode") {
        input "newMode", "mode", title: "Mode?"
    }
    section("False alarm threshold (defaults to 10 min)") {
        input "falseAlarmThreshold", "decimal", title: "Number of minutes", required: false
    }
    section( "Notifications" ) {
        input "sendPushMessage", "enum", title: "Send a push notification?",
            options: ["Yes", "No"], required: false
        input "phone", "phone", title: "Send a Text Message?", required: false
    }
}


definition(
    name: "bonvoyage",
    namespace: "StrykerSKS",
    author: "Testing Only",
    description: "Connect your Ecobee thermostat to SmartThings.",
    category: "My Apps",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Partner/ecobee.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Partner/ecobee@2x.png"
)

def installed() {
    log.debug "Installed with settings: ${settings}"
    log.debug "Current mode = ${location.mode}, people = ${people.collect{it.label + ': ' + it.currentPresence}}"
    subscribe(people, "presence", presence)
}

def updated() {
    log.debug "Updated with settings: ${settings}"
    log.debug "Current mode = ${location.mode}, people = ${people.collect{it.label + ': ' + it.currentPresence}}"
    unsubscribe()
    subscribe(people, "presence", presence)
}

def presence(evt) {
    log.debug "evt.name: $evt.value"

    // The presence capability can either by "present" or "not present".
    // If the user is not present, we want to check if everyone is away
    if (evt.value == "not present") {
        // Check that the desire mode isn't already the same as the current mode.
        if (location.mode != newMode) {
            log.debug "checking if everyone is away"
            // If everyone is away, start the sequence
            if (everyoneIsAway()) {
                log.debug "starting sequence"
                runIn(findFalseAlarmThreshold() * 60, "takeAction", [overwrite: false])
            }
        }
        else {
            log.debug "mode is the same, not evaluating"
        }
    }
    else {
        log.debug "present; doing nothing"
    }
}

// returns true if all configured sensors are not present,
// false otherwise.
private everyoneIsAway() {
    def result = true
    // iterate over our people variable that we defined
    // in the preferences method
    for (person in people) {
        if (person.currentPresence == "present") {
            // someone is present, so set our our result
            // variable to false and terminate the loop.
            result = false
            break
        }
    }
    log.debug "everyoneIsAway: $result"
    return result
}

// gets the false alarm threshold, in minutes. Defaults to
// 10 minutes if the preference is not defined.
private findFalseAlarmThreshold() {
    // In Groovy, the return statement is implied, and not required.
    // We check to see if the variable we set in the preferences
    // is defined and non-empty, and if it is, return it.  Otherwise,
    // return our default value of 10
    (falseAlarmThreshold != null && falseAlarmThreshold != "") ? falseAlarmThreshold : 10
}

def takeAction() {
    if (everyoneIsAway()) {
        def threshold = 1000 * 60 * findFalseAlarmThreshold() - 1000
        def awayLongEnough = people.findAll { person ->
            def presenceState = person.currentState("presence")
            def elapsed = now() - presenceState.rawDateCreated.time
            elapsed >= threshold
        }
        log.debug "Found ${awayLongEnough.size()} out of ${people.size()} person(s) who were away long enough"
        if (awayLongEnough.size() == people.size()) {
            //def message = "${app.label} changed your mode to '${newMode}' because everyone left home"
            def message = "SmartThings changed your mode to '${newMode}' because everyone left home"
            log.info message
            send(message)
            setLocationMode(newMode)
        } else {
            log.debug "not everyone has been away long enough; doing nothing"
        }
    } else {
        log.debug "not everyone is away; doing nothing"
    }
}

private send(msg) {
    if ( sendPushMessage != "No" ) {
        log.debug( "sending push message" )
        sendPush( msg )
    }

    if ( phone ) {
        log.debug( "sending text message" )
        sendSms( phone, msg )
    }

    log.debug msg
}
