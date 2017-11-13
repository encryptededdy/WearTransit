package nz.zhang.aucklandtransportwear.wakaapi.listener

import nz.zhang.aucklandtransportwear.wakaapi.WakaTrip

interface StopInfoListener {
    fun update(services: List<WakaTrip>?)
}