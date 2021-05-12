package es.uniovi.eii.stitchingbot.model

data class SewingMachine(val id: Int, val name: String, val imgUrl: String="", val hasPedal: Boolean){

    constructor(name: String,  imgUrl: String="",  hasPedal: Boolean ) : this(0, name, imgUrl, hasPedal)
}