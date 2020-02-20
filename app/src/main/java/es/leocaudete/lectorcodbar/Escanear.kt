package es.leocaudete.lectorcodbar

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView

class Escanear : AppCompatActivity(), ZXingScannerView.ResultHandler {

    private lateinit var escanerZXing: ZXingScannerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        escanerZXing=ZXingScannerView(this)
       // setContentView(R.layout.activity_escanear)
        setContentView(escanerZXing)
        escanerZXing.flash = escanerZXing.flash != true

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
        inflater.inflate(R.menu.escaner_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        return when(item?.itemId){
            R.id.flash -> {
                escanerZXing.flash = escanerZXing.flash != true
                true
            }

            R.id.cancelar->{
                val intentRegreso=Intent(this, Lector::class.java)
                setResult(Activity.RESULT_CANCELED,intentRegreso)
                finish()
                true

            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    override fun onResume() {
        super.onResume()
        // El manejador del resultado es esta misma clase, por eso implementamos ZXingScannerView.ReultHandler
        escanerZXing.setResultHandler(this)
        escanerZXing.startCamera()
    }


    override fun onPause() {
        super.onPause()
        escanerZXing.stopCamera()
    }

    // Estamos sobrescribiendo un método de la interfaz ZXingScannerView.ResultHandler
    override fun handleResult(resultado: Result?) {
        // Si quieres que se siga escaneando después de haber leído el código, descomenta lo siguiente:
        // Si la descomentas no recomiendo que llames a finish
        // escanerZXing.resumeCameraPreview(this);
        // Obener código/texto leído
        val codigoLeido= resultado?.getText()

        val intentRegreso=Intent(this, Lector::class.java).apply {
            putExtra("codigo",codigoLeido)
        }

        setResult(Activity.RESULT_OK,intentRegreso)

        finish()
    }
}
