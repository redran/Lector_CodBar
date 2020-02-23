package es.leocaudete.lectorcodbar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import es.leocaudete.lectorcodbar.adapters.EquipoAdapter
import es.leocaudete.lectorcodbar.modelo.Equipo
import kotlinx.android.synthetic.main.activity_destinos.*

class Destinos : AppCompatActivity() {

    var adapter: EquipoAdapter?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_destinos)


        var listaCodigo=intent.getStringArrayListExtra("codigos")


        adapter=EquipoAdapter(this, creaLista(),listaCodigo)

        equiposGridView.adapter=adapter


    }

    // De momento lo rellenaremos de forma manual
    private fun creaLista(): ArrayList<Equipo> {
        val lista= ArrayList<Equipo>()
        lista.add(Equipo("192.168.1.19",2000, "Entrada", R.mipmap.recepcion))
        lista.add(Equipo("192.168.1.5",2000, "Mostrador Interior", R.mipmap.mostrador))
        lista.add(Equipo("192.168.1.9",2000, "Almacén", R.mipmap.almacen))
        lista.add(Equipo("192.168.1.16",2000, "Nave 2", R.mipmap.nave2))
        lista.add(Equipo("192.168.1.18",2000, "Nave 3", R.mipmap.nave3))
        return lista

    }


}
