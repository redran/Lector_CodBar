package es.leocaudete.lectorcodbar

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.WindowManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import es.leocaudete.lectorcodbar.modelo.Linea
import es.leocaudete.lectorcodbar.utils.ShowMessages
import java.io.EOFException
import java.io.File
import java.io.FileInputStream
import java.io.ObjectInputStream

class MainActivity : AppCompatActivity() {

    private lateinit var storageLocalDir: String
    private var lineas = ArrayList<Linea>()
    var gestionMesajes = ShowMessages()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        storageLocalDir = getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString()
        val home_dir = File(storageLocalDir)
        if (!home_dir.exists()) {
            home_dir.mkdirs()
        }
    }

    // Anulamos la opción de volver a tras a través del botón del móvil
    override fun onBackPressed() {
        //
    }

    /*
   Infla el menú
    */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    /*
  Acciones sobre los elementos del menú al hacer click
   */
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when (item?.itemId) {
            R.id.lector -> {
                iniciaNuevaLectura()
                true
            }
            R.id.lectoricon -> {
                iniciaNuevaLectura()
                true
            }
            R.id.abrir -> {
                abrir()
                true
            }
            R.id.abriricon -> {
                abrir()
                true
            }
            R.id.eliminar -> {
                eliminar()
                true
            }
            R.id.salir -> {
                System.exit(0)
                true
            }


            else -> super.onOptionsItemSelected(item)
        }

    }

    private fun iniciaNuevaLectura() {

        val intent = Intent(this, Lector::class.java).apply {
            putExtra("lineas", lineas)
        }
        startActivity(intent)
        finish()

    }

    private fun eliminar() {

        myAlertDialogSinglePersistentList(1)
    }

    private fun abrir() {
        myAlertDialogSinglePersistentList(2)


    }



    /**
     * Abre un Fichero pasado por parametro
     *
     */
    fun abre(nombre: String) {
        var fichero = File("$storageLocalDir/$nombre")

        if (fichero!!.exists()) {

            val ficheroSalida = FileInputStream(fichero)
            val ficheroObjetos = ObjectInputStream(ficheroSalida)


            var ln = ficheroObjetos.readObject() as? Linea

            try {
                while (ln != null) {
                    lineas.add(ln)
                    ln = ficheroObjetos.readObject() as? Linea
                }
            } catch (e: EOFException) {
                print("Final de Fichero")
            }

            ficheroObjetos.close()

            if (lineas.size > 0) {
                iniciaNuevaLectura()
            }
        }
    }

    /**
     * Elimina un fichero pasado por parametro
     */
    fun elimina(nombre: String) {
        var fichero = File("$storageLocalDir/$nombre")

        if (fichero!!.exists()) {
            gestionMesajes.showAlert(
                "Atención",
                "¿Estás seguro de que quieres eliminar el fichero: $nombre ",
                this,
                { File("$storageLocalDir/$nombre").delete() })

        }
    }

    /**
     * Opcion 1: Delete File
     * Opcion 2: Load File
     */
    private fun myAlertDialogSinglePersistentList(opcion: Int) {
        // Primero obtenemos los nosmbres de los ficheros
        var dir = File(storageLocalDir)
        var hijos = dir.list()

        if (hijos.size > 0) {
            val builder = AlertDialog.Builder(this)

            builder.apply {
                if (opcion == 2) {
                    setTitle("Selecciona un fichero para abrir")
                } else {
                    setTitle("Selecciona un fichero para eliminar")
                }


                setSingleChoiceItems(hijos, 0) { _, which ->
                    Log.d(
                        "DEBUG",
                        hijos[which]
                    )
                }
                setPositiveButton(android.R.string.yes) { dialog, _ ->
                    val selectedPosition =
                        (dialog as AlertDialog).listView.checkedItemPosition
                    if (opcion == 1) {
                        elimina(hijos[selectedPosition])
                    } else {
                        abre(hijos[selectedPosition])
                    }
                }
                setNegativeButton(android.R.string.no) { _, _ ->
                    Toast.makeText(
                        context,
                        "Se ha cancelado la operación",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            builder.show().window.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
        } else {
            // Sino tiene nombres mostramos un texto informando
            Toast.makeText(this, "No hay ficheros guardados", Toast.LENGTH_LONG).show()
        }


    }

}
