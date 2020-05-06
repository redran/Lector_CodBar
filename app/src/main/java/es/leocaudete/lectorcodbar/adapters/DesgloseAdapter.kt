package es.leocaudete.lectorcodbar.adapters


import android.app.AlertDialog
import android.content.Context
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import es.leocaudete.lectorcodbar.R
import es.leocaudete.lectorcodbar.modelo.LineaSimple

/**
 * @author Leonardo Caudete Palau
 */
class DesgloseAdapter : RecyclerView.Adapter<DesgloseAdapter.ViewHolder>() {

    // Desglose indices string -> Para futuras ampliaciones hay que ponerlos en preferencias
    private val INICIO_PAQUETE=11
    private val FIN_PAQUETE=14

    var lineas: MutableList<LineaSimple> = mutableListOf()
    lateinit var context: Context

    // Recibe todas las lineas y la posicion de la que queremos editar
    fun RecyclerAdapter(lineas: MutableList<LineaSimple>, context: Context) {
        this.lineas = lineas
        this.context = context
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lineas.get(position)
        holder.btEliminar.setOnClickListener {
            // eliminara todos los que tengan la misma partida
            eliminaRegistro(position)
        }
        holder.lineas=lineas
        holder.bind(item, context, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)

        return ViewHolder(
            layoutInflater.inflate(
                R.layout.desglose_recycled_view,
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
        builder.setMessage("Vas a eliminar el paquete: " + lineas[position].codigo.substring(INICIO_PAQUETE, FIN_PAQUETE)+ "¿Estás seguro?")
        builder.setPositiveButton(android.R.string.ok){_,_->
            lineas.removeAt(position)
            notifyItemRemoved(position)
        }
        builder.setNeutralButton(android.R.string.cancel, null)
        builder.show()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {



        val paquete = view.findViewById(R.id.tv_paquete) as TextView
        val pies = view.findViewById(R.id.et_pies) as TextView
        val btEliminar = view.findViewById(R.id.btEliminarPaquete) as ImageButton
        val btEditar = view.findViewById(R.id.btEditarPaquete) as ImageButton
        val btGuardar = view.findViewById(R.id.btGuardarPaquete) as ImageButton
        var lineas:MutableList<LineaSimple> = mutableListOf()



        fun bind(linea: LineaSimple, context: Context, position:Int) {
            val INICIO_PAQUETE=11
            val FIN_PAQUETE=14

            paquete.text = linea.codigo.substring(INICIO_PAQUETE, FIN_PAQUETE)
            pies.text = linea.pies.toString()

           btEditar.setOnClickListener {
               btEditar.visibility=View.GONE
               btGuardar.visibility=View.VISIBLE
               pies.isEnabled=true
           }

            btGuardar.setOnClickListener {
                btEditar.visibility=View.VISIBLE
                btGuardar.visibility=View.GONE
                linea.pies=pies.text.toString().toFloat()
                pies.isEnabled=false
            }

        }



    }
}

