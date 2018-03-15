package com.ncr.interns.codecatchers.incredicabs.NCABdatabase;

/**
 * Created by gs250365 on 3/15/2018.
 */

public class EmployeeCabMatesDetails {
    String CabMate_name,CabMate_address,CabMate_pickupTime,Cabmate_cotactNumber;

    public EmployeeCabMatesDetails(String cabMate_name, String cabMate_address,
                                   String cabMate_pickupTime, String cabmate_cotactNumber) {
        CabMate_name = cabMate_name;
        CabMate_address = cabMate_address;
        CabMate_pickupTime = cabMate_pickupTime;
        Cabmate_cotactNumber = cabmate_cotactNumber;
    }

    public String getCabMate_name() {
        return CabMate_name;
    }

    public String getCabMate_address() {
        return CabMate_address;
    }

    public String getCabMate_pickupTime() {
        return CabMate_pickupTime;
    }

    public String getCabmate_cotactNumber() {
        return Cabmate_cotactNumber;
    }
}
