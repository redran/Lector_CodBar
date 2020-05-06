package es.leocaudete.lectorcodbar.modelo

import java.io.Serializable

class Linea: Serializable {

    var partida:String="" // parte del codigo que se corresponde con la partida
    var desglose = ArrayList<LineaSimple>()


}