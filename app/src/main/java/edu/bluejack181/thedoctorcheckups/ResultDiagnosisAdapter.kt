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
        p0.name.text = "Illness Name: " + items.get(p1).name
        p0.accuracy.text = "Accuracy: " + items.get(p1).accuracy.toString() + "%"
        p0.medical_condition.text = "Description: \n" + items.get(p1).medical_condition
        p0.treatmentDesc.text = "Solution: \n" + items.get(p1).treatment
    }
}

class DiagnosisViewHolder(view: View): RecyclerView.ViewHolder(view){
    var name = view.lbl_name
    var accuracy = view.lbl_accuracy
    var medical_condition = view.lbl_medical_condition
    var treatmentDesc = view.lbl_treatment
}