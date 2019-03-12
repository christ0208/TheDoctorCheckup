package edu.bluejack181.thedoctorcheckups

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.health_history_list_item.view.*

class HealthHistoryAdapter(val items: ArrayList<IllnessHistory>, val context: Context): RecyclerView.Adapter<HealthHistoryViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): HealthHistoryViewHolder {
        return HealthHistoryViewHolder(LayoutInflater.from(context).inflate(R.layout.health_history_list_item, p0, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(p0: HealthHistoryViewHolder, p1: Int) {
        val element = items.get(p1)
        p0.name.text = element.illness
        p0.dateTaken.text = element.dateTaken
    }

}

class HealthHistoryViewHolder(view: View): RecyclerView.ViewHolder(view){
    var dateTaken = view.lbl_date
    var name = view.lbl_illness
}