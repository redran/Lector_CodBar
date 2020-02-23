package es.leocaudete.lectorcodbar.adapters


import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import es.leocaudete.lectorcodbar.R
import es.leocaudete.lectorcodbar.modelo.Linea

/**
 * @author Leonardo Caudete Palau
 */
class RecyclerAdapter : RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {


    var lineas: MutableList<Linea> = mutableListOf()
    lateinit var context: Context

    fun RecyclerAdapter(lineas: MutableList<Linea>, context: Context) {
        this.lineas = lineas
        this.context = context
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lineas.get(position)
        holder.btEliminar.setOnClickListener {

            eliminaRegistro(position)
        }
        holder.bind(item, context, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return ViewHolder(
            layoutInflater.inflate(
                R.layout.lista_recycled_view,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return lineas.size
    }

    // Función encargada de mostrar un AlertDialog con informacion addicional.
    private fun eliminaRegistro(position:Int){
        val builder=AlertDialog.Builder(context)
        builder.setTitle("Atención")
        builder.setMessage("Vas a eliminar todas las lecturas de este código ¿Estás seguro?")
        builder.setPositiveButton(android.R.string.ok){_,_->
            lineas.removeAt(position)
            notifyItemRemoved(position)
        }
        builder.setNeutralButton(android.R.string.cancel, null)
        builder.show()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {


        val codigo = view.findViewById(R.id.tv_codigo) as TextView
        val cantidad = view.findViewById(R.id.tv_cantidad) as TextView
        val btRestar = view.findViewById(R.id.btRestar) as ImageButton
        val btEliminar = view.findViewById(R.id.btEliminar) as ImageButton


        fun bind(linea: Linea, context: Context, position:Int) {

            codigo.text = linea.codigo
            cantidad.text = linea.cantidad.toString()

            btRestar.setOnClickListener {
                if (linea.cantidad > 1) {
                    linea.cantidad -= 1
                    cantidad.text = linea.cantidad.toString()

                } else {
                    Toast.makeText(
                        context,
                        "Cantidad no puede ser menor que 1. Elimina la línea entera con el icono de la papelera",
                        Toast.LENGTH_LONG
                    ).show()

                }
            }


        }

    }
}

