package com.g.autoreversegit;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;


import com.stericson.RootShell.exceptions.RootDeniedException;
import com.stericson.RootShell.execution.Command;
import com.stericson.RootTools.RootTools;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeoutException;

public class MainActivity extends AppCompatActivity {

    private List<InterfaceName> interfaceList = new ArrayList<InterfaceName>();

    private BroadcastReceiver mBroadcastReceiver;

    public static String mPackName = null;
    public static String mMark = null;
    public static String mPreMark = null;
    public static String mPredictInterfaceFile = null;

    public Handler handlerUpdateUI  = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String strInfo  = bundle.getString(MainActivity.mMark);

            addInterface(strInfo);
        }

    };

    @Override
    protected void onRestart(){
        super.onRestart();

        String defaultIM = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.DEFAULT_INPUT_METHOD);

        String packname = defaultIM.substring(0, defaultIM.indexOf('/'));

        Log.d("autoreverse", "oldIM:" + mPackName +",newIM:" + packname);
        if (!mPackName.equals(packname)){
            Log.d("autoreverse", "DefaultInputMethodChanged");
            mPackName = packname;
            mMark = "mark" + mPackName;
            mPreMark = "pre" + mPackName;
            mPredictInterfaceFile = "/data/data/" + mPackName + "/predictinterface";

            interfaceList.clear();

            unregisterReceiver(mBroadcastReceiver);

            mBroadcastReceiver = new MyReceiver(handlerUpdateUI);
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(mMark);
            registerReceiver(mBroadcastReceiver, intentFilter);
        }

    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String defaultIM = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.DEFAULT_INPUT_METHOD);
        Log.d("MainActivity", "defaultIM:" + defaultIM);

        mPackName = defaultIM.substring(0, defaultIM.indexOf('/'));
        mMark = "mark" + mPackName;
        mPreMark = "pre" + mPackName;
        mPredictInterfaceFile = "/data/data/" + mPackName + "/predictinterface";

        Log.d("MainActivity", "packName:" + mPackName +", mark:" + mMark);

        mBroadcastReceiver = new MyReceiver(handlerUpdateUI);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(mMark);
        registerReceiver(mBroadcastReceiver, intentFilter);

        final InterfaceNameAdapter adapter = new InterfaceNameAdapter(MainActivity.this, R.layout.listview, interfaceList);

        final ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        Button bt = (Button) findViewById(R.id.bt);
        bt.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                adapter.notifyDataSetChanged();

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                InterfaceName in = interfaceList.get(position);
            }
        });

        final EditText editText = findViewById(R.id.et);

        //showSoftInputFromWindow(this, editText);
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
                           public void run() {
                               InputMethodManager inputManager =
                                       (InputMethodManager)editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                               inputManager.showSoftInput(editText, 0);
                           }
                       }, 998);

        timer.schedule(new TimerTask() {
                            @Override
                            public void run() {
                                if (isKeyboardShown(listView.getRootView())){
                                    Log.d("MainActivity", "keyboard show");
                                }else{
                                    Log.d("MainActivity", "keyboard not show");
                                }
                            }
                        },2000);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Point p = getCenter(listView.getRootView());
                Log.d("MainActivity", "X:" + p.x + " Y:" + p.y);

                CMDUtil.processCommand("input tap " + p.x + " " + p.y);

            }
        },3000);

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Point p = getCandidate(listView.getRootView());
                Log.d("MainActivity", "X:" + p.x + " Y:" + p.y);

                CMDUtil.processCommand("input tap " + p.x + " " + p.y);

            }
        },5000);

    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }


    private void addInterface(String data){
        if (data == null){
            return;
        }

        interfaceList.add(new InterfaceName(data));
    }

    private boolean isKeyboardShown(View rootView) {
        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        String str = "VisiableBtm:" + r.bottom + ", btm:" + rootView.getBottom();

        Log.d("MainActivity", str);

        return rootView.getBottom() > r.bottom;

    }

    private Point getCenter(View rootView){
        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);

        Point p = new Point();
        p.set(rootView.getRight()/2, (rootView.getBottom() - r.bottom)/2 + r.bottom);

        return p;
    }


    private Point getCandidate(View rootView){
        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);

        Point p = new Point();
        p.set(rootView.getRight()/2, r.bottom + 40);

        return p;
    }

}
