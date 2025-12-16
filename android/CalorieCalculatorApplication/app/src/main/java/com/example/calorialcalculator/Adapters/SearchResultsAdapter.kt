package com.example.calorialcalculator.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.calorialcalculator.Backend.api.FoodItem

class SearchResultsAdapter(
    private val onClick: (FoodItem) -> Unit
) : RecyclerView.Adapter<SearchResultsAdapter.VH>() {

    private val items = mutableListOf<FoodItem>()

    fun submit(list: List<FoodItem>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context)
            .inflate(android.R.layout.simple_list_item_2, parent, false)
        return VH(v, onClick)
    }

    override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])
    override fun getItemCount(): Int = items.size

    class VH(itemView: View, val onClick: (FoodItem) -> Unit) : RecyclerView.ViewHolder(itemView) {
        private val t1 = itemView.findViewById<TextView>(android.R.id.text1)
        private val t2 = itemView.findViewById<TextView>(android.R.id.text2)

        fun bind(item: FoodItem) {
            t1.text = item.name
            t2.text = "Calories: ${item.calories} (per 100g)"
            itemView.setOnClickListener { onClick(item) }
        }
    }
}
