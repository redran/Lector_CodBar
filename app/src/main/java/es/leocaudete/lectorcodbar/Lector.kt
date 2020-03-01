package es.leocaudete.lectorcodbar

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import es.leocaudete.lectorcodbar.adapters.RecyclerAdapter
import es.leocaudete.lectorcodbar.modelo.Linea
import es.leocaudete.lectorcodbar.modelo.LineaSimple
import es.leocaudete.lectorcodbar.utils.GestionPermisos
import es.leocaudete.lectorcodbar.utils.ShowMessages
import kotlinx.android.synthetic.main.activity_lector.*
import java.io.File
import java.io.FileOutputStream
import java.io.ObjectOutputStream


class Lector : AppCompatActivity() {

    private val MY_PERMISSIONS_REQUEST_CODE = 234
    private val CODIGO_ESCANEAR = 1
    private val CODIGO_GUARDAR = 2

    // Desglose indices string
    private val INICIO_PARTIDA=0
    private val FIN_PARTIDA=9
    private val INICIO_PAQUETE=9
    private val FIN_PAQUETE=13
    private val INICIO_PIES=14
    private val FIN_PIES=17

    private lateinit var storageLocalDir: String

    private val myAdapter: RecyclerAdapter =
        RecyclerAdapter()
    private lateinit var gestionPermisos: GestionPermisos
    private var lineasComletas = ArrayList<Linea>()

    private var candidad_pies: Float = 0f

    private lateinit var gestorMensajes: ShowMessages


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lector)
        storageLocalDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString()
        gestorMensajes = ShowMessages()

        lineasComletas = intent.getSerializableExtra("lineas") as ArrayList<Linea>
        setUpRecyclerView()

    }

    // Opción de volver a tras a través del botón del móvil
    override fun onBackPressed() {
        gestorMensajes.showAlert(
            "Atención",
            "Estás a punto de salir y se perderán las lecturas realizadas. ¿Estás seguro?",
            this,
            { cancelar() })

    }

    /*
 Infla el menú
  */

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.lector_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.cancelar -> {
                gestorMensajes.showAlert(
                    "Atención",
                    "Estás a punto de salir y se perderán las lecturas realizadas. ¿Estás seguro?",
                    this,
                    { cancelar() })
                true
            }

            R.id.limpiar -> {
                gestorMensajes.showAlert(
                    "Atención",
                    "Se van ha borrar todos los datos leidos. ¿Estás seguro?",
                    this,
                    { limpiar() })
                true

            }
            R.id.save -> {
                guardar()
                true
            }
            R.id.save_icon -> {
                guardar()
                true
            }
            R.id.send -> {
                enviar()
                true
            }
            R.id.send_icon -> {
                enviar()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }


    private fun enviar() {
        if (lineasComletas.size > 0) {
            val intent = Intent(this, Destinos::class.java).apply {
                putExtra("codigos", creaListaTxt())
            }
            startActivity(intent)
        } else {
            Toast.makeText(
                this,
                "Antes de enviar al servidor agrega una lectura",
                Toast.LENGTH_LONG
            ).show()
        }


    }

    private fun limpiar() {

        lineasComletas.clear()
        setUpRecyclerView()
    }

    private fun cancelar() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun guardar() {

        if (lineasComletas.size > 0) {
            val intent = Intent(this, GuardarLectura::class.java)
            startActivityForResult(intent, CODIGO_GUARDAR)
        } else {
            Toast.makeText(this, "No se puede guardar un fichero sin líneas", Toast.LENGTH_LONG)
                .show()
        }
    }


    // Va a crear un ArrayList que contiene todas las líneas leidas
    private fun creaListaTxt(): ArrayList<String> {
        var lista = ArrayList<String>()

        for (linea in lineasComletas) {
           for(sublinea in linea.desglose){
               lista.add(sublinea.codigo)
           }
        }
        return lista
    }

    private fun setUpRecyclerView() {


        listadoLineas.setHasFixedSize(true)
        listadoLineas.layoutManager = LinearLayoutManager(this)
        myAdapter.RecyclerAdapter(lineasComletas, this)
        listadoLineas.adapter = myAdapter

    }

    // Accion que lleva a cabo el botón al ser pulsado
    fun escanea(view: View) {
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("DEBUG", "El permiso ya está concedido")
            val i = Intent(this, Escanear::class.java)
            startActivityForResult(i, CODIGO_ESCANEAR)
        } else {
            gestionPermisos = GestionPermisos(
                this,
                android.Manifest.permission.CAMERA,
                MY_PERMISSIONS_REQUEST_CODE
            )
            gestionPermisos.checkPermissions()
        }
    }



    // Procesa el codigo leido y rellena los Arrays de Objetos
    private fun preProcesoLinea(codigo: String) {


        var partida = codigo.substring(INICIO_PARTIDA, FIN_PARTIDA)
        var paquete = codigo.substring(INICIO_PAQUETE, FIN_PAQUETE)
        var codigo_unico="00000$partida$paquete"
        var pies=0

        // Comprobamos que el paquete no se haya leido ya
        var encontrado = false
        for (linea in lineasComletas) {

            for(sublineas in linea.desglose){
                if (sublineas.codigo.equals(codigo_unico)) {
                    gestorMensajes.showAlertOneButton(
                        "Atención",
                        "El paquete ya se ha leido.",
                        this
                    )
                    encontrado = true
                }
            }

        }


        // Los codigos son claves únicas asi que nunca se repiten
        if (!encontrado || lineasComletas.isEmpty()) {

            // comprobamos si la partida existe y sino la agregegamos
            var indexEncontrado = -1
            for (i in lineasComletas.indices) {
              if(lineasComletas[i].partida.equals(partida)){
                  indexEncontrado=i
              }
            }

            // Si no se encuentra se Agrega uno nuevo
            var indexActual:Int
            if(indexEncontrado==-1){
                val nuevaLinea = Linea()
                nuevaLinea.partida = partida
                lineasComletas.add(nuevaLinea)
                indexActual=lineasComletas.size-1
            }
            // Si se encuentra nos posicionamos en el para añadir lo leido en su subarray
            else{
                indexActual=indexEncontrado
            }

            //Ahora añadimos la linea con el codigo bueno y los pies en el array LineaSimple
            var lineaSimple=LineaSimple()
            lineaSimple.codigo=codigo_unico

            // Aqui tenemos la diferencia
            // Si es una etiqueta antigua llamamos al modal para que ingrese los pies
            // Si es nueva cogemos los pies del desglose del string leido
            if (codigo.length == 13) {
                lineasComletas[indexActual].desglose.add(lineaSimple)
                mostrarModal(indexActual, lineasComletas[indexActual].desglose.size-1)
            } else {
                lineaSimple.pies = codigo.substring(INICIO_PIES, FIN_PIES).toFloat()
                lineasComletas[indexActual].desglose.add(lineaSimple)
            }





        }
    }

    private fun mostrarModal(posicion: Int, posicionSubArray:Int) {
        val builder = AlertDialog.Builder(this)
        val inflater = layoutInflater
        builder.setTitle("Gestor de cantidad")
        val dialogLayout = inflater.inflate(R.layout.alert_dialog_with_eddittext, null)
        val editText = dialogLayout.findViewById<EditText>(R.id.etPies)
        builder.setView(dialogLayout)
        builder.setPositiveButton("ACEPTAR") { dialogInterface, i ->
            aceptaModal(
                posicion,
                posicionSubArray,
                editText.text.toString().toFloat()

            )
        }
        builder.show()

    }

    private fun aceptaModal(posicion: Int, posicionSubArray:Int,cantidad: Float) {
        lineasComletas[posicion].desglose[posicionSubArray].pies = cantidad // Actualizamos la línea del ArrayCompleto
        setUpRecyclerView()
    }

    // Al volver de la camara, mostramos el codigo leido
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CODIGO_ESCANEAR) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    val codigo = data?.getStringExtra("codigo")
                    preProcesoLinea(codigo)

                }
            }
        }
        if (requestCode == CODIGO_GUARDAR) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    val nombreFichero = data?.getStringExtra("nombre")

                    var fichero = File("$storageLocalDir/$nombreFichero")
                    if (!fichero.exists()) {
                        var ficheroSalida = FileOutputStream(fichero)
                        var ficheroObjetos = ObjectOutputStream(ficheroSalida)
                        for (ln in lineasComletas) {
                            ficheroObjetos.writeObject(ln)
                        }
                        ficheroObjetos.close()
                        cancelar()
                    } else {
                        Toast.makeText(
                            this,
                            "Ya existe un fichero con ese nombre",
                            Toast.LENGTH_LONG
                        ).show()
                    }

                }
            }
        }

    }


}
