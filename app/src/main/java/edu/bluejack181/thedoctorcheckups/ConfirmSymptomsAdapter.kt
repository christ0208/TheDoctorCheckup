package edu.bluejack181.thedoctorcheckups

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.confirmed_symptoms_list_item.view.*

class ConfirmSymptomsAdapter(val items: ArrayList<String>, val context: Context): RecyclerView.Adapter<ViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.confirmed_symptoms_list_item, p0, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(p0: ViewHolder, p1: Int) {
        p0.symptoms.text = items.get(p1)
    }
}

class ViewHolder(view: View):RecyclerView.ViewHolder(view){
    var symptoms = view.lbl_symptom
}