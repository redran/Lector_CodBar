package es.leocaudete.lectorcodbar

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import es.leocaudete.lectorcodbar.modelo.Linea
import es.leocaudete.lectorcodbar.utils.ShowMessages
import java.io.EOFException
import java.io.File
import java.io.FileInputStream
import java.io.ObjectInputStream
import java.net.URI
import java.nio.file.Path
import java.nio.file.Paths

class MainActivity : AppCompatActivity() {

    private val OPEN_FILE_CODE=1
    private val DELETE_FILE_CODE=2

    private lateinit var storageLocalDir:String
    private var lineas=ArrayList<Linea>()
    var gestionMesajes= ShowMessages()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        storageLocalDir=getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).toString()

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
            R.id.lectoricon->{
                iniciaNuevaLectura()
                true
            }
            R.id.abrir->{
                abrir()
                true
            }
            R.id.abriricon->{
                abrir()
                true
            }
            R.id.eliminar->{
                eliminar()
                true
            }


            else -> super.onOptionsItemSelected(item)
        }

    }

    private fun iniciaNuevaLectura(){

        val intent=Intent(this, Lector::class.java).apply {
            putExtra("lineas", lineas)
        }
        startActivity(intent)
        finish()

    }

    private fun eliminar(){

        val intent=Intent(Intent.ACTION_OPEN_DOCUMENT).apply{
            addCategory(Intent.CATEGORY_OPENABLE)
            data=Uri.parse(storageLocalDir)
            type="*/*"
        }

        startActivityForResult(intent, DELETE_FILE_CODE )
    }

    private fun abrir(){

        val intent=Intent(Intent.ACTION_OPEN_DOCUMENT).apply{
            addCategory(Intent.CATEGORY_OPENABLE)
            data=Uri.parse(storageLocalDir)
            type="*/*"
        }

        startActivityForResult(intent, OPEN_FILE_CODE )




    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode==OPEN_FILE_CODE){
            if(resultCode== Activity.RESULT_OK){
                var fichero:File?=null
                data?.data?.also{uri ->
                    var ruta=(uri.lastPathSegment.toString()).split('/')

                    var nom_fich=ruta[ruta.size-1]

                    fichero = File("$storageLocalDir/$nom_fich" )
                }
                if(fichero!!.exists()){
                    // val fichero= File("$storageLocalDir/fichero.dat")
                    val ficheroSalida=FileInputStream(fichero)
                    val ficheroObjetos = ObjectInputStream(ficheroSalida)


                    var ln=ficheroObjetos.readObject() as? Linea

                    try{
                        while(ln!=null){
                            lineas.add(ln)
                            ln=ficheroObjetos.readObject() as? Linea
                        }
                    }catch(e:EOFException){
                        print("Final de Fichero")
                    }

                    ficheroObjetos.close()

                    if(lineas.size>0){
                        iniciaNuevaLectura()
                    }
                }


            }
            if(resultCode== Activity.RESULT_CANCELED){
                gestionMesajes.showAlert("Información", "No has seleccionado ningún fichero. ¿Quieres iniciar una nueva lectura?",this, {iniciaNuevaLectura()} )
            }

        }
        if(requestCode==DELETE_FILE_CODE){
            if(resultCode== Activity.RESULT_OK){
                var fichero:File?=null
                data?.data?.also{uri ->
                    var ruta=(uri.lastPathSegment.toString()).split('/')

                    var nom_fich=ruta[ruta.size-1]

                    fichero = File("$storageLocalDir/$nom_fich" )

                    if(fichero!!.exists()){
                        gestionMesajes.showAlert("Atención", "¿Estás seguro de que quieres eliminar el fichero: $nom_fich ",this, {File("$storageLocalDir/$nom_fich" ).delete()} )

                    }
                }

            }
            if(resultCode== Activity.RESULT_CANCELED){
                gestionMesajes.showAlert("Información", "No has seleccionado ningún fichero. ¿Quieres iniciar una nueva lectura?",this, {iniciaNuevaLectura()} )
            }

        }
    }
}
