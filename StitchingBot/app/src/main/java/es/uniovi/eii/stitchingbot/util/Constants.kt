package es.uniovi.eii.stitchingbot.util

import java.util.*

object Constants {
    const val LOGO = "logo"
    const val SEWING_MACHINE = "machine"
    const val SUMMARY = "summary"
    const val TAG_BLUETOOTH = "BluetoothStitching"
    const val TAG_TRANSLATE = "TranslateOrders"
    const val ASK_FOR_ACTIONS = "M"
    const val START_EXECUTION = "B"
    const val CONFIGURE_PULLEY = "C"
    const val PAUSE_EXECUTION = "P"
    const val RESUME_EXECUTION = "R"
    const val STOP_EXECUTION = "T"
    const val UP = "W"
    const val DOWN = "S"
    const val LEFT = "A"
    const val RIGHT = "D"
    const val START_AUTOHOME="H"
    const val CREATION_MODE = "creation"
    const val TAG_SEWINGMACHINE = "SewingMachine"
    val MY_UUID: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
    val CONNECTING_STATUS = 1
    val REQUEST_ENABLE_BT: Int = 3
    val REQUEST_PERMISSION_BLUETOOTH: Int = 5
}
