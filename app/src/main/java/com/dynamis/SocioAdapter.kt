package com.dynamis

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


data class SocioItem(
    val nombreCompleto: String,
    val dni: String,
    val fechaVencimiento: String
)


class SocioAdapter(
    private var items: List<SocioItem>
) : RecyclerView.Adapter<SocioAdapter.SocioViewHolder>() {

    class SocioViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombre)
        val tvDni: TextView = view.findViewById(R.id.tvDni)
        val tvFecha: TextView = view.findViewById(R.id.tvFecha)
        val ivFoto: ImageView = view.findViewById(R.id.ivFoto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SocioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_no_socio, parent, false)
        return SocioViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: SocioViewHolder, position: Int) {
        val item = items[position]

        holder.tvNombre.text = item.nombreCompleto
        holder.tvDni.text = "DNI: ${item.dni}"

        holder.tvFecha.text = "Venc: ${item.fechaVencimiento}"

        holder.ivFoto.setImageResource(R.drawable.dynamislogoinicio)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun actualizarLista(nuevaLista: List<SocioItem>) {
        items = nuevaLista
        notifyDataSetChanged()
    }
}