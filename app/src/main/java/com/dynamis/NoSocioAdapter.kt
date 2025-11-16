package com.dynamis

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


data class NoSocioItem(
    val nombreCompleto: String,
    val dni: String,
    val fecha: String
)


class NoSociosAdapter(
    private var items: List<NoSocioItem>
) : RecyclerView.Adapter<NoSociosAdapter.NoSocioViewHolder>() {


    class NoSocioViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombre)
        val tvDni: TextView = view.findViewById(R.id.tvDni)
        val tvFecha: TextView = view.findViewById(R.id.tvFecha)
        val ivFoto: ImageView = view.findViewById(R.id.ivFoto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoSocioViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_no_socio, parent, false)
        return NoSocioViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: NoSocioViewHolder, position: Int) {
        val item = items[position]

        holder.tvNombre.text = item.nombreCompleto
        holder.tvDni.text = "DNI: ${item.dni}"
        holder.tvFecha.text = "Nac: ${item.fecha}"

        holder.ivFoto.setImageResource(R.drawable.dynamislogoinicio)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun actualizarLista(nuevaLista: List<NoSocioItem>) {
        items = nuevaLista
        notifyDataSetChanged()
    }
}