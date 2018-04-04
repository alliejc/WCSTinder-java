package com.alliejc.wcstinder.trackmyswing

data class Dancer (
        var firstName: String,
        var lastName: String,
        var wSCID: Int,
        var currentPoints: Int,
        var division: String,
        var role: String,
        var qualifiesForNextDivision: Boolean,
        var divisionRoleQualifies: String
)