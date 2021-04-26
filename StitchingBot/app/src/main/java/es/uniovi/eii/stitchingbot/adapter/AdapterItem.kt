package es.uniovi.eii.stitchingbot.adapter

data class AdapterItem<out T>(val value: T?, val viewType: Int)