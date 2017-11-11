package nz.zhang.aucklandtransportwear.atapi.listener

import nz.zhang.aucklandtransportwear.atapi.Stop

/**
 * Created by Edward Zhang on 12/11/2017.
 */
interface StopsListListener {
    fun update(stops: List<Stop>?)
}