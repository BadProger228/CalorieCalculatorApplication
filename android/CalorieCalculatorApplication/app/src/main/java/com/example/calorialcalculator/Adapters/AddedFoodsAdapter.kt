package com.example.calorialcalculator.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.calorialcalculator.AddedFoodUi
import com.example.calorialcalculator.R

class AddedFoodsAdapter(
    private val items: List<AddedFoodUi>,
    private val onRemove: (Int) -> Unit
) : RecyclerView.Adapter<AddedFoodsAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_added_food, parent, false)
        return VH(v, onRemove)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position], position)
    override fun getItemCount(): Int = items.size

    class VH(itemView: View, val onRemove: (Int) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val tvName = itemView.findViewById<TextView>(R.id.tvRowName)
        private val tvInfo = itemView.findViewById<TextView>(R.id.tvRowInfo)
        private val btnDel = itemView.findViewById<ImageButton>(R.id.btnRowDelete)

        fun bind(item: AddedFoodUi, pos: Int) {
            tvName.text = "${item.name} (${item.grams}g)"
            tvInfo.text = "Kcal ${item.calories} | P ${item.protein} | F ${item.fat} | C ${item.carbs}"
            btnDel.setOnClickListener { onRemove(pos) }
        }
    }
}
