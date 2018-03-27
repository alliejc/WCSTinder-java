
package com.alliejc.wcstinder.trackmyswing;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Dancer {

    @SerializedName("FirstName")
    @Expose
    private String firstName;
    @SerializedName("LastName")
    @Expose
    private String lastName;
    @SerializedName("WSCID")
    @Expose
    private Integer wSCID;
    @SerializedName("CurrentPoints")
    @Expose
    private Integer currentPoints;
    @SerializedName("Division")
    @Expose
    private String division;
    @SerializedName("Role")
    @Expose
    private String role;
    @SerializedName("QualifiesForNextDivision")
    @Expose
    private Boolean qualifiesForNextDivision;
    @SerializedName("DivisionRoleQualifies")
    @Expose
    private String divisionRoleQualifies;

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Integer getWSCID() {
        return wSCID;
    }

    public void setWSCID(Integer wSCID) {
        this.wSCID = wSCID;
    }

    public Integer getCurrentPoints() {
        return currentPoints;
    }

    public void setCurrentPoints(Integer currentPoints) {
        this.currentPoints = currentPoints;
    }

    public String getDivision() {
        return division;
    }

    public void setDivision(String division) {
        this.division = division;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getQualifiesForNextDivision() {
        return qualifiesForNextDivision;
    }

    public void setQualifiesForNextDivision(Boolean qualifiesForNextDivision) {
        this.qualifiesForNextDivision = qualifiesForNextDivision;
    }

    public String getDivisionRoleQualifies() {
        return divisionRoleQualifies;
    }

    public void setDivisionRoleQualifies(String divisionRoleQualifies) {
        this.divisionRoleQualifies = divisionRoleQualifies;
    }

}
