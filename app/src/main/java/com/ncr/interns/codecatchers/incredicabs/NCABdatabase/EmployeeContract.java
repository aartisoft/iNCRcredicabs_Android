package com.ncr.interns.codecatchers.incredicabs.NCABdatabase;

import android.provider.BaseColumns;

/**
 * Created by gs250365 on 3/12/2018.
 */

public class EmployeeContract implements BaseColumns {
    public  static final String DB_TABLE = "EmployeeData";
    public static final String COLUMN_EMP_QLID = "EmployeeQlid";
    public static final String COLUMN_FIRST_NAME ="EmployeeFirstName";
    public static final String COLUMN_MIDDLE_NAME = "EmployeeMiddleName";
    public static final String COLUMN_LAST_NAME = "EmployeeLastName";
    public static final String COLUMN_LEVEL_1_MANAGER = "Level1ManagerQlid";
    public static final String COLUMN_LEVEL_2_MANAGER = "Leve21ManagerQlid";
    public static final String COLUMN_HOME_ADDRESS = "HomeAddress";
    public static final String COLUMN_OFFICE_ADDRESS = "OfficeAddress";
    public static final String COLUMN_CONTACT_NUMBER = "contactNumber";
    public static final String COLUMN_EMERGENCY_CONTACT_NUMBER = "emergencyContactNumber";
    public static final String COLUMN_EMP_ROLE = "role";
    public static final String COLUMN_EMP_REFERESHED_TOKEN ="refreshedToken";


}
