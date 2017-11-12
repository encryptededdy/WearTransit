package nz.zhang.aucklandtransportwear.atapi.listener

import nz.zhang.aucklandtransportwear.atapi.ServiceRT

interface RTBoardListener {
    fun update(services: List<ServiceRT>?)
}