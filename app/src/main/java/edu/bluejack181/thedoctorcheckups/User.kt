package edu.bluejack181.thedoctorcheckups

data class User(
    var name: String = "",
    var email: String = "",
    var gender: String = "",
    var address: String = "",
    var day: Int = 0,
    var month: Int = 0,
    var year: Int = 0
)