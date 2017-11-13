package nz.zhang.aucklandtransportwear.wakaapi.listener

import nz.zhang.aucklandtransportwear.wakaapi.WakaService

interface StopInfoListener {
    fun update(services: List<WakaService>?)
}