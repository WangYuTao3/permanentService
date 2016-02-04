package com.example.administrator.topactivity.utils.log;

public class LogConsts {
    private final static LogMode mode = LogMode.PUBLIC;
//    private final static String COMMAND = "/data/crashdumps";

    enum LogMode {
        PUBLIC, DEV, TEST
    }

    public static final String DIR_NAME = "ngdsLog";
    public static final String LOG_PART_ONE = "_part_one.log";
    public static final String LOG_PART_TWO = "_part_two.log";
    public static final long LOG_MAXSIZE = 1024 * 1024 * 5;

    public static final long FILE_LIMITSIZE = 1024 * 1024 * 25;
    public static final long ALL_LOG_MAXSIZE = 1024 * 1024 * 5;
}

