package com.example.admin.newnews.dataUtils;

import android.content.Context;

/**
 * Created by Admin on 4/4/2016.
 */
public class DatabaseHelper {
    private static DatabaseHelper instance;

    public static synchronized DatabaseHelper getInstance(Context context) {
        // if (instance == null)
        // instance = new DatabaseHelper(context);

        return instance;
    }
//Other stuff...

}
