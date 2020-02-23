package es.leocaudete.lectorcodbar

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_guardar_lectura.*

class GuardarLectura : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_guardar_lectura)
    }

    fun volver(view: View) {
        onBackPressed()
    }
    fun guardar(view: View) {
        if(etNombreFichero.text.isBlank() || etNombreFichero.text.isEmpty()){
            Toast.makeText(this, "Tienes que escribir un nombre", Toast.LENGTH_LONG).show()
        }else{


            var intent=Intent(this, Lector::class.java).apply {
                putExtra("nombre", etNombreFichero.text.toString() + ".dat")
            }
            setResult(Activity.RESULT_OK,intent)
            finish()
        }

    }
}
