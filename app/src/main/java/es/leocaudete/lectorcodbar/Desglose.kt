package es.leocaudete.lectorcodbar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import es.leocaudete.lectorcodbar.adapters.DesgloseAdapter
import es.leocaudete.lectorcodbar.modelo.Linea
import es.leocaudete.lectorcodbar.modelo.LineaSimple
import es.leocaudete.lectorcodbar.utils.ShowMessages
import kotlinx.android.synthetic.main.activity_desglose.*

class Desglose : AppCompatActivity() {

    private var lineasOriginal= ArrayList<Linea>()
    private var posicion=-1
    private lateinit var gestorMensajes: ShowMessages

    private val myAdapter: DesgloseAdapter =
        DesgloseAdapter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_desglose)
        gestorMensajes = ShowMessages()
        // Recibimos la lista
        lineasOriginal = intent.getSerializableExtra("lineas") as ArrayList<Linea> // Todas las lecturas
        posicion=intent.getIntExtra("posicion", -1)
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {


        desgloselineas.setHasFixedSize(true)
        desgloselineas.layoutManager = LinearLayoutManager(this)
        myAdapter.RecyclerAdapter(lineasOriginal[posicion].desglose,  this)
        desgloselineas.adapter = myAdapter

    }
    // Opción de volver a tras a través del botón del móvil
    override fun onBackPressed() {
       cancelar()

    }
    fun guardar(view: View) {
        volver(lineasOriginal)
    }

    fun cancelarbt(view: View) {
        cancelar()
    }
    fun cancelar(){

        gestorMensajes.showAlert(
            "Atención",
            "Estás a punto de salir y se perderán las lecturas realizadas. ¿Estás seguro?",
            this,
            {super.onBackPressed() })
    }
    fun volver(lista:MutableList<Linea>){
        var ArrayLineas=ArrayList(lista)
        var intent= Intent(this, Lector::class.java).apply {
            putExtra("lineas", ArrayLineas )
        }
        startActivity(intent)
    }
}
