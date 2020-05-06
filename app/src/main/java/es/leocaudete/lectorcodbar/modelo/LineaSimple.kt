package es.leocaudete.lectorcodbar.modelo

import java.io.Serializable

class LineaSimple: Serializable {

    var codigo:String="" // Clave única que se compone de 0000 + partida 4dig + paquete 3dig
    var pies:Float=0f
}