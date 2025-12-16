package com.example.calorialcalculator.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.calorialcalculator.Backend.api.RationCommands.SavedRationDto
import com.example.calorialcalculator.R

class SavedRationsAdapter(
    private val onClick: (SavedRationDto) -> Unit,
    private val onDelete: (SavedRationDto) -> Unit
) : RecyclerView.Adapter<SavedRationsAdapter.VH>() {

    private val items = mutableListOf<SavedRationDto>()

    fun submit(list: List<SavedRationDto>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_saved_ration, parent, false)
        return VH(v, onClick, onDelete)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])
    override fun getItemCount(): Int = items.size

    class VH(
        itemView: View,
        private val onClick: (SavedRationDto) -> Unit,
        private val onDelete: (SavedRationDto) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {

        private val tvName = itemView.findViewById<TextView>(R.id.tvRationName)
        private val tvKcal = itemView.findViewById<TextView>(R.id.tvRationKcal)
        private val btnDel = itemView.findViewById<ImageButton>(R.id.btnRationDelete)

        fun bind(item: SavedRationDto) {
            tvName.text = item.name
            tvKcal.text = "Kcal: ${item.totalCalories}"
            itemView.setOnClickListener { onClick(item) }
            btnDel.setOnClickListener { onDelete(item) }
        }
    }
}
