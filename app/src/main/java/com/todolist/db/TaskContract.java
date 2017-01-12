package com.todolist.db;

import android.provider.BaseColumns;

public class TaskContract {
    public static final String DB_NAME = "com.todolist.db";
    public static final int DB_VERSION = 12;

    public class TaskEntry implements BaseColumns {
        public static final String TABLE_NAME = "Tasks";
        public static final String TITLE = "Title";
        public static final String STAGE = "Stage";
        public static final String CATEGORY_FOREIGN_KEY = "fk_task";
    }
}
