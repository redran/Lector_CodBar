package es.leocaudete.lectorcodbar.modelo

import java.io.Serializable

class Linea: Serializable {

    var codigo:String=""
    var cantidad:Float=0f
    var partida:String=""
    var pies:Float=0f
    var desglose = ArrayList<LineaSimple>()


}