package nz.zhang.aucklandtransportwear.wakaapi.listener

import nz.zhang.aucklandtransportwear.wakaapi.Trip

interface StopInfoListener {
    fun update(trips: List<Trip>?)
}