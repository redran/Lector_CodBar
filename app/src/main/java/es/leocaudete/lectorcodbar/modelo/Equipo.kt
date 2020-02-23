package es.leocaudete.lectorcodbar.modelo

import java.io.Serializable

class Equipo: Serializable {

    var ipAddress: String=""
    var port: Int=0
    var descripcion: String=""
    var imagen: Int=0

    constructor(ipAddress: String, port:Int, descripcion: String, imagen: Int){
        this.ipAddress=ipAddress
        this.port=port
        this.descripcion=descripcion
        this.imagen=imagen
    }
}