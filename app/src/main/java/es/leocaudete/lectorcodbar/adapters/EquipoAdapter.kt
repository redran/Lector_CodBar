package es.leocaudete.lectorcodbar.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Toast
import es.leocaudete.lectorcodbar.R
import es.leocaudete.lectorcodbar.modelo.Equipo
import kotlinx.android.synthetic.main.equipo_gridview.view.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.*

class EquipoAdapter : BaseAdapter {
    var listaEquipos = ArrayList<Equipo>()
    var context: Context? = null
    var listaCodigos = ArrayList<String>()

    constructor(
        context: Context,
        listaEquipos: ArrayList<Equipo>,
        listaCodigos: ArrayList<String>
    ) : super() {
        this.context = context
        this.listaEquipos = listaEquipos
        this.listaCodigos = listaCodigos
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val equipo = this.listaEquipos[position]
        val inflator = LayoutInflater.from(context)
        var equipoView = inflator.inflate(R.layout.equipo_gridview, null)
        equipoView.image.setImageResource(equipo.imagen!!)

        equipoView.tvName.text = equipo.descripcion!!

        equipoView.image.setOnClickListener {

            enviarSocket(equipo.ipAddress, equipo.port)
        }

        return equipoView

    }

    override fun getItem(position: Int): Any {
        return listaEquipos[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return listaEquipos.size
    }

    // Recibe los datos del ordenador donde quiero enviar el fichero
    private fun enviarSocket(serverIP: String, port: Int) {


        doAsync {
            val serverAddr = InetAddress.getByName(serverIP)
            try {
                val mySocket = Socket(serverAddr, port)

                var socketOut = ObjectOutputStream(mySocket.getOutputStream())

                socketOut.writeObject(listaCodigos)
                socketOut.flush()

                var socketIn = ObjectInputStream(mySocket.getInputStream())
                var recibido = socketIn.readBoolean()

                // Esto se ejecuta dentro del hilo principal
                uiThread {
                    if (recibido) {
                        Toast.makeText(context, "Fichero enviado con exito.", Toast.LENGTH_LONG)
                            .show()
                        //longToast("Fichero enviado con exito.")
                    } else {
                        Toast.makeText(context, "Error al enviar el fichero.", Toast.LENGTH_LONG)
                            .show()
                        //longToast("Error al enviar el fichero.")
                    }
                }
            } catch (e: UnknownHostException) {
                // Esto se ejecuta dentro del hilo principal
                uiThread {
                    Toast.makeText(context, "Error de red.", Toast.LENGTH_LONG).show()
                    // longToast("Error de red.")
                }
            } catch (ioe: IOException) {
                uiThread {
                    Toast.makeText(context, "Error de red.", Toast.LENGTH_LONG).show()
                    // longToast("Error de red.")
                }
            } catch (ce: ConnectException) {
                uiThread {
                    Toast.makeText(context, "Error de red.", Toast.LENGTH_LONG).show()
                    // longToast("Error de red.")
                }
            } catch (se: SocketException) {
                uiThread {
                    Toast.makeText(context, "Error de red.", Toast.LENGTH_LONG).show()
                    // longToast("Error de red.")
                }
            }
        }
    }

}