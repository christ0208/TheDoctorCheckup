package edu.bluejack181.thedoctorcheckups

class Diagnosis(id: Int, name: String, accuracy: Int) {
    var id = id
    var name = name
    var accuracy = accuracy
    var prof_name: String = ""
    var medical_condition: String = ""
    var treatment: String = ""

    fun setInfo(prof_name: String, medical_condition: String, treatment: String){
        this.prof_name = prof_name
        this.medical_condition = medical_condition
        this.treatment = treatment
    }
}