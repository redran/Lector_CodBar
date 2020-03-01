package es.leocaudete.lectorcodbar.modelo

import java.io.Serializable

class LineaSimple: Serializable {

    var codigo:String="" // Clave única que se compone de 0000 + partida + paquete
    var pies:Float=0f
}