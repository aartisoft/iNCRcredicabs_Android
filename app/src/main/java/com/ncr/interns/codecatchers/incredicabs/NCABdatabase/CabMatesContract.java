package com.ncr.interns.codecatchers.incredicabs.NCABdatabase;

import android.provider.BaseColumns;

/**
 * Created by gs250365 on 3/18/2018.
 */

public class CabMatesContract implements BaseColumns {
    public static final String DB_TABLE = "CabMatesDetails";
    public static final String COLUMN_CABMATE_NAME = "CabMateName";
    public static final String COLUMN_CABMATE_CONTACT_NUMBER = "ContactNumber";
    public static final String COLUMN_CABMATE_ADDRESS = "CabMateAddress";
    public static final String COLUMN_CABMATE_QLID = "CabMateQlid";
    public static final String COLUMN_CABMATE_PICKUPTIME = "CabMatePickupTime";
    public static final String COLUMN_CABMATE_SHIFT_ID = "ShiftId";
    public static final String COLUMN_CABMATE_ROUTE_NUMBER = "RouteNumber";
    public static final String COLUMN_CABMATE_ROASTER_Id = "RoasterId";
    public static final String COLUMN_CABMATE_CAB_NUMBER = "CabNumber";

}
