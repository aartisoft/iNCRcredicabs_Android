package com.ncr.interns.codecatchers.incredicabs.NCABdatabase;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by gs250365 on 3/12/2018.
 */

public class NcabSQLiteHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "NCABDatabase";
    private static final int DB_VERSION = 3;
    Context ctx;
    SQLiteDatabase sqLiteDatabase;
    private static final String TAG = "NcabSQLiteHelper";
    private final String CREATE_EMPLOYEE_TABLE_QUERY = "CREATE TABLE "+EmployeeContract.DB_TABLE+
            " ("+EmployeeContract._ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
            EmployeeContract.COLUMN_EMP_QLID+" TEXT NOT NULL,"+
            EmployeeContract.COLUMN_FIRST_NAME+" TEXT NOT NULL,"+
            EmployeeContract.COLUMN_MIDDLE_NAME+" TEXT,"+
            EmployeeContract.COLUMN_LAST_NAME+" TEXT,"+
            EmployeeContract.COLUMN_CONTACT_NUMBER+" TEXT NOT NULL,"+
            EmployeeContract.COLUMN_EMERGENCY_CONTACT_NUMBER+" TEXT NOT NULL,"+
            EmployeeContract.COLUMN_HOME_ADDRESS+" TEXT NOT NULL,"+
            EmployeeContract.COLUMN_OFFICE_ADDRESS+" TEXT NOT NULL,"+
            EmployeeContract.COLUMN_LEVEL_1_MANAGER+" TEXT NOT NULL,"+
            EmployeeContract.COLUMN_LEVEL_1_MANAGER_NAME+" TEXT NOT NULL,"+
            EmployeeContract.COLUMN_LEVEL_2_MANAGER_NAME+" TEXT NOT NULL,"+
            EmployeeContract.COLUMN_LEVEL_2_MANAGER+" TEXT NOT NULL,"+
            EmployeeContract.COLUMN_EMP_ROLE+" INTEGER NOT NULL,"+
            EmployeeContract.COLUMN_EMP_REFERESHED_TOKEN+" TEXT);";
    // TODO: 3/18/2018 If getting refreshed token make it not null

    private final String CREATE_CABMATE_TABLE_QUERY = "CREATE TABLE "+CabMatesContract.DB_TABLE+" ("
            +CabMatesContract._ID+" INTEGER PRIMARY KEY AUTOINCREMENT,"+
            CabMatesContract.COLUMN_CABMATE_QLID+" TEXT NOT NULL,"+
            CabMatesContract.COLUMN_CABMATE_NAME+" TEXT NOT NULL,"+
            CabMatesContract.COLUMN_CABMATE_CONTACT_NUMBER+" TEXT NOT NULL,"+
            CabMatesContract.COLUMN_CABMATE_ADDRESS+" TEXT NOT NULL);";


    public NcabSQLiteHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        this.ctx = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create Employee Table in SQlite Database
        db.execSQL(CREATE_EMPLOYEE_TABLE_QUERY);
        db.execSQL(CREATE_CABMATE_TABLE_QUERY);
        Log.i(TAG, "onCreate: Table Created ");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+EmployeeContract.DB_TABLE);
    }
}
