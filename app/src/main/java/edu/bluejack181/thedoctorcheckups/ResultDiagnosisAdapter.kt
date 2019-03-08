package edu.bluejack181.thedoctorcheckups

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.confirmed_symptoms_list_item.view.*
import kotlinx.android.synthetic.main.result_diagnosis_list_item.view.*

class ResultDiagnosisAdapter(val items: ArrayList<Diagnosis>, val context: Context): RecyclerView.Adapter<DiagnosisViewHolder>() {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): DiagnosisViewHolder{
        return DiagnosisViewHolder(LayoutInflater.from(context).inflate(R.layout.result_diagnosis_list_item, p0, false))
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(p0: DiagnosisViewHolder, p1: Int) {
        p0.id.text = items.get(p1).id.toString()
        p0.name.text = items.get(p1).name
        p0.accuracy.text = items.get(p1).accuracy.toString()
        p0.prof_name.text = items.get(p1).prof_name
        p0.medical_condition.text = items.get(p1).medical_condition
        p0.treatmentDesc.text = items.get(p1).treatment
    }
}

class DiagnosisViewHolder(view: View): RecyclerView.ViewHolder(view){
    var id = view.lbl_id
    var name = view.lbl_name
    var accuracy = view.lbl_accuracy
    var prof_name = view.lbl_prof_name
    var medical_condition = view.lbl_medical_condition
    var treatmentDesc = view.lbl_treatment
}