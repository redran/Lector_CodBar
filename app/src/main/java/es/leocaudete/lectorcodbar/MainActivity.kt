package es.leocaudete.lectorcodbar

import android.content.Intent
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

class MainActivity : AppCompatActivity() {

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
    private fun abrir(){
        var fichero= File("$storageLocalDir/fichero.dat")
        var ficheroSalida=FileInputStream(fichero)
        var ficheroObjetos = ObjectInputStream(ficheroSalida)


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
        }else{
            gestionMesajes.showAlert("Información", "No hay ningún fichero guardado. ¿Quieres iniciar una nueva lectura?",this, {iniciaNuevaLectura()} )
        }


    }
}
