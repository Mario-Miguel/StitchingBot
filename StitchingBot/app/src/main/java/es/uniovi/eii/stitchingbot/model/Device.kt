package es.uniovi.eii.stitchingbot.model

//Meter tambien el UUID del dispositivo
data class Device (var name: String, val mac: String, val status: Int, val type: Int)