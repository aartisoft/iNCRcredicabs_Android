package com.ncr.interns.codecatchers.incredicabs.NCABdatabase;

/**
 * Created by gs250365 on 3/15/2018.
 */

public class EmployeeCabMatesDetails {
    String CabMate_name;
    String CabMate_address;
    String CabMate_pickupTime;
    String Cabmate_contactNumber;
    String CabMate_Qlid;

    public EmployeeCabMatesDetails(String cabMate_name, String cabMate_address,
                                   String cabMate_pickupTime, String cabmate_contactNumber,
                                   String cabMate_Qlid) {
        CabMate_name = cabMate_name;
        CabMate_address = cabMate_address;
        CabMate_pickupTime = cabMate_pickupTime;
        Cabmate_contactNumber = cabmate_contactNumber;
        CabMate_Qlid = cabMate_Qlid;
    }

    public String getCabMate_name() {
        return CabMate_name;
    }

    public String getCabMate_Qlid() {
        return CabMate_Qlid;
    }

    public String getCabMate_address() {
        return CabMate_address;
    }

    public String getCabMate_pickupTime() {
        return CabMate_pickupTime;
    }

    public String getCabmate_contactNumber() {
        return Cabmate_contactNumber;
    }
}
