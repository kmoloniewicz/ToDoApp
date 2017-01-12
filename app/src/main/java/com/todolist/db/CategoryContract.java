package com.todolist.db;

import android.provider.BaseColumns;

public class CategoryContract {
    public static final String DB_NAME = "com.todolist.db";
    public static final int DB_VERSION = 12;
    public static final String CATEGORY_NAME_PARAM = "CATEGORY_NAME";

    public class CategoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "Categories";
        public static final String TITLE = "Title";
    }
}
