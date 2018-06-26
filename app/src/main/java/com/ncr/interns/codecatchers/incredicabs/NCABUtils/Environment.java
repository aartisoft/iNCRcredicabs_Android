package com.ncr.interns.codecatchers.incredicabs.NCABUtils;

public class Environment {

    private static final String URL_BASE_AWS_PROD = "http://ec2-18-219-151-75.us-east-2.compute.amazonaws.com:8080";

    public static final String URL_LOGIN               = URL_BASE_AWS_PROD + "/NCAB/EmployeeService/login-android";
    public static final String URL_UNSCHEDULED_REQUEST = URL_BASE_AWS_PROD + "/NCAB/RequestService/sendRequest";
    public static final String URL_FEEDBACK            = URL_BASE_AWS_PROD + "/NCAB/RosterService/complaint";
    public static final String URL_GET_SHIFT_DETAILS   = URL_BASE_AWS_PROD + "/NCAB/RosterService/getCabShift";
    public static final String URL_CHECK_IN            = URL_BASE_AWS_PROD + "/NCAB/AndroidService/checkin";
    public static final String URL_CHECK_OUT           = URL_BASE_AWS_PROD + "/NCAB/AndroidService/checkout";
    public static final String URL_SAVE_TOKEN          = URL_BASE_AWS_PROD + "/NCAB/EmployeeService/set-push-token-android";
    public static final String URL_GET_TOKEN           = URL_BASE_AWS_PROD + "NCAB//EmployeeService/get-push-token-android";
    public static final String URL_REQUEST_APPROVE     = URL_BASE_AWS_PROD + "/NCAB/AndroidService/approval";
    public static final String URL_REQUEST_REJECT      = URL_BASE_AWS_PROD + "/NCAB/AndroidService/approval";
    public static final String URL_GET_MY_REQUEST      = URL_BASE_AWS_PROD + "/NCAB/AndroidService/NotificationsByMe";
    public static final String URL_REQUEST_FOR_ME      = URL_BASE_AWS_PROD + "/NCAB/AndroidService/NotificationsForMe";
    public static final String URL_SOS                 = URL_BASE_AWS_PROD + "/NCAB/EmployeeService/sos-trigger-android";
}
