package com.alliejc.wcstinder.trackmyswing

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class Dancer (
        @SerializedName("FirstName")
        @Expose var firstName: String,

        @SerializedName("LastName")
        @Expose var lastName: String,

        @SerializedName("WSCID")
        @Expose var wSCID: Int,

        @SerializedName("CurrentPoints")
        @Expose var currentPoints: Int,

        @SerializedName("Division")
        @Expose var division: String,

        @SerializedName("Role")
        @Expose var role: String,

        @SerializedName("QualifiesForNextDivision")
        @Expose var qualifiesForNextDivision: Boolean,

        @SerializedName("DivisionRoleQualifies")
        @Expose var divisionRoleQualifies: String,

        @SerializedName("Relevance")
        @Expose var relevance: Int
)