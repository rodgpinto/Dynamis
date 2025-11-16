package com.dynamis

import android.annotation.SuppressLint
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


data class VencimientoItem(
    val nombreCompleto: String,
    val dni: String,
    val fechaVencimiento: Long
)


class VencimientoAdapter(
    private var items: List<VencimientoItem>
) : RecyclerView.Adapter<VencimientoAdapter.VencimientoViewHolder>() {

    class VencimientoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNombre: TextView = view.findViewById(R.id.tvNombre)
        val tvDni: TextView = view.findViewById(R.id.tvDni)
        val tvFecha: TextView = view.findViewById(R.id.tvFecha)
        val ivFoto: ImageView = view.findViewById(R.id.ivFoto)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VencimientoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_no_socio, parent, false)
        return VencimientoViewHolder(view)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: VencimientoViewHolder, position: Int) {
        val item = items[position]

        holder.tvNombre.text = item.nombreCompleto
        holder.tvDni.text = "DNI: ${item.dni}"

        holder.tvFecha.text = "Venció: ${formatearFecha(item.fechaVencimiento)}"

        holder.tvFecha.setTextColor(Color.RED)

        holder.ivFoto.setImageResource(R.drawable.dynamislogoinicio)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun actualizarLista(nuevaLista: List<VencimientoItem>) {
        items = nuevaLista
        notifyDataSetChanged()
    }

    private fun formatearFecha(timestamp: Long): String {
        return try {
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val date = Date(timestamp)
            sdf.format(date)
        } catch (e: Exception) { "Error" }
    }
}