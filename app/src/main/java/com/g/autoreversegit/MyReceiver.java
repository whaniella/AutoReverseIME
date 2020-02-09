package com.g.autoreversegit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import junit.framework.Assert;

import java.util.ArrayList;



public class MyReceiver extends BroadcastReceiver {

    private  Handler mHandler;

    public MyReceiver(Handler handler){
        mHandler = handler;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("broadcastReceiverget", "getBroadcast");

        if (intent.getAction() == null || intent.getExtras() == null){
            return;
        }

        Log.d("xposed_broadcast", "mark:" + MainActivity.mMark);
            if (intent.getAction().equals(MainActivity.mMark)) {
                Bundle bundle = intent.getExtras();
                String strInfo = bundle.getString(MainActivity.mMark);
//            ArrayList<String> list = bundle.getStringArrayList(MainActivity.mMark);
//            if (list == null || list.size() == 0){
//                return;
//            }

            Message msg = new Message();
            msg.what = 0;
            Bundle msgBundle = new Bundle();
            msgBundle.putString(MainActivity.mMark, strInfo);

            msg.setData(msgBundle);
            mHandler.sendMessage(msg);
            Log.d("broadcastReceiverget", strInfo.toString());
        }else if(intent.getAction().equals(MainActivity.mPreMark)){
            Bundle bundle = intent.getExtras();
            ArrayList<String> list = bundle.getStringArrayList(MainActivity.mMark);
            if (list == null || list.size() == 0){
                return;
            }

            Message msg = new Message();
            msg.what = 1;
            Bundle msgBundle = new Bundle();
            msgBundle.putStringArrayList(MainActivity.mMark, list);

            msg.setData(msgBundle);
            mHandler.sendMessage(msg);
            Log.d("broadcastReceiverget", list.toString());
        }

    }
}
