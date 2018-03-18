package com.ncr.interns.codecatchers.incredicabs.NCABdatabase;

/**
 * Created by gs250365 on 3/12/2018.
 */

public class EmployeeData {

    //Model class for employee Data

    private String EmployeeQlID;
    private String EmployeeFirstName;
    private String EmployeeMiddleName;
    private String EmployeeLastName;
    private String Level1ManagerQlid;
    private String Level2ManagerQlid;
    private String Level1ManagerName;
    private String Level2ManagerName;
    private String HomeAddress;
    private String OfficeAddress;
    private String contactNumber;
    private String refreshedToken;
    private String emergencyContactNumber;
    private int role;

    public String getLevel1ManagerName() {
        return Level1ManagerName;
    }

    public String getLevel2ManagerName() {
        return Level2ManagerName;
    }

    public EmployeeData(String employeeQlID, String employeeFirstName,
                        String employeeMiddleName, String employeeLastName,
                        String level1ManagerQlid, String leve21ManagerQlid, String level2ManagerName,
                        String level1ManagerName,
                        String homeAddress, String officeAddress, String contactNumber,
                        String refreshedToken, String emergencyContactumber, int role) {
        EmployeeQlID = employeeQlID;
        EmployeeFirstName = employeeFirstName;
        EmployeeMiddleName = employeeMiddleName;
        EmployeeLastName = employeeLastName;

        Level1ManagerQlid = level1ManagerQlid;
        Level2ManagerQlid = leve21ManagerQlid;
        HomeAddress = homeAddress;
        Level1ManagerName = level1ManagerName;
        Level2ManagerName = level2ManagerName;
        OfficeAddress = officeAddress;
        this.contactNumber = contactNumber;
        this.refreshedToken = refreshedToken;
        this.emergencyContactNumber = emergencyContactumber;
        this.role = role;
    }

    public String getEmployeeQlID() {
        return EmployeeQlID;
    }

    public void setEmployeeQlID(String employeeQlID) {
        EmployeeQlID = employeeQlID;
    }

    public String getEmployeeFirstName() {
        return EmployeeFirstName;
    }

    public void setEmployeeFirstName(String employeeFirstName) {
        EmployeeFirstName = employeeFirstName;
    }

    public String getEmployeeMiddleName() {
        return EmployeeMiddleName;
    }

    public void setEmployeeMiddleName(String employeeMiddleName) {
        EmployeeMiddleName = employeeMiddleName;
    }

    public String getEmployeeLastName() {
        return EmployeeLastName;
    }

    public void setEmployeeLastName(String employeeLastName) {
        EmployeeLastName = employeeLastName;
    }

    public String getLevel1ManagerQlid() {
        return Level1ManagerQlid;
    }

    public void setLevel1ManagerQlid(String level1ManagerQlid) {
        Level1ManagerQlid = level1ManagerQlid;
    }

    public String getLevel2ManagerQlid() {
        return Level2ManagerQlid;
    }

    public void setLevel2ManagerQlid(String level2ManagerQlid) {
        Level2ManagerQlid = level2ManagerQlid;
    }

    public String getHomeAddress() {
        return HomeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        HomeAddress = homeAddress;
    }

    public String getOfficeAddress() {
        return OfficeAddress;
    }

    public void setOfficeAddress(String officeAddress) {
        OfficeAddress = officeAddress;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getRefreshedToken() {
        return refreshedToken;
    }

    public void setRefreshedToken(String refreshedToken) {
        this.refreshedToken = refreshedToken;
    }

    public String getEmergencyContactNumber() {
        return emergencyContactNumber;
    }

    public void setEmergencyContactNumber(String emergencyContactumber) {
        this.emergencyContactNumber = emergencyContactumber;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }
}
