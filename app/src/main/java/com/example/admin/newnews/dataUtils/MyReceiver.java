package com.example.admin.newnews.dataUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class MyReceiver extends BroadcastReceiver {
    public MyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        Log.d("tag","myReceiver");
        intent= new Intent(context,UpdateIntent.class);
        context.startService(intent);
        //context.startService(new Intent(context,UpdateIntent.class));
        //throw new UnsupportedOperationException("Not yet implemented");
    }
}
