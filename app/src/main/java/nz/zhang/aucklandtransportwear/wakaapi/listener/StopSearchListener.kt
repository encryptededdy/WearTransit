package nz.zhang.aucklandtransportwear.wakaapi.listener

import nz.zhang.aucklandtransportwear.wakaapi.Stop

interface StopSearchListener {
    fun update(services: List<Stop>?)
}