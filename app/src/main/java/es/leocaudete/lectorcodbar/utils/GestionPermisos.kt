package es.leocaudete.lectorcodbar.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class GestionPermisos(val activity: Activity, val permiso:String, val code:Int) {

    fun checkPermissions():Boolean {
        // comprobamos si el permiso en cuestion está concedido
        if(ContextCompat.checkSelfPermission(activity,permiso)!=PackageManager.PERMISSION_GRANTED){
            // Si no está concedido entramos
            Log.d("DEBUG","No tienes persmiso para esta acción: " + permiso)

            // Si el usuario ya lo ha rechazado al menos una vez (TRUE),
            // se da una explicación
            if(ActivityCompat.shouldShowRequestPermissionRationale(activity,permiso)){
                Log.d("DEBUG","Se da una explicación")
                showAlert()
            }else{
                //No requiere explicación, se pregunta por el permiso
                Log.d("DEBUG","No se da una explicación.")
                ActivityCompat.requestPermissions(activity, arrayOf(permiso),code)
            }
        }else{
            Log.d("DEBUG","Permiso ($permiso) concedido!")
        }
        return ContextCompat.checkSelfPermission(activity,permiso)==PackageManager.PERMISSION_GRANTED
    }

    // Función encargada de mostrar un AlertDialog con informacion addicional.
    private fun showAlert(){
        val builder=AlertDialog.Builder(activity)
        builder.setTitle("Concesión de permisos")
        builder.setMessage("Puede resultar interesante indicar" + " porque se necesita conceder permisos.")
        builder.setPositiveButton(android.R.string.ok){_,_->
            Log.d("DEBUG", "Se acepta y se vuelve a pedir permiso")

            ActivityCompat.requestPermissions(activity, arrayOf(permiso),code)
        }

        builder.setNeutralButton(android.R.string.cancel, null)
        builder.show()
    }
}