package com.g.autoreversegit;


import android.app.AndroidAppHelper;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.inputmethodservice.InputMethodService;
import android.os.Bundle;
import android.os.Debug;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.inputmethod.InputConnectionWrapper;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicBoolean;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import input.AiType;
import input.GBoard;
import input.GoKeyboard;
import input.InputBase;
import input.Kika;
import input.SwiftKey;
import input.Swype;
import input.Typany;



public class hook implements IXposedHookLoadPackage{
    public InputBase mInputBase = null;
    public static String mPackName = null;
    public static String mFilePath = null;
    public static String mPredictInterfaceFile = null;
    public static String mMark = null;
    //public static String mPreMark = null;
    public static ClassLoader mClassLoader = null;
    public static LoadPackageParam mlpparam = null;

    private AtomicBoolean mTrace = new AtomicBoolean(false);

    //private static HashMap<String, ArrayList<?>> mMap = new HashMap<>();

    public static class InputType{
        public static final int OTHER = 0;
        public static final int GBOARD = 1;
        public static final int KIKA = 2;
        public static final int SWIFTKEY = 3;
        public static final int TYPANY = 4;
        public static final int GOKB = 5;
        public static final int SWYPE = 6;
        public static final int AITYPE = 7;
    }

    public static final String[] aryInputPackName = {
            "null",
            "com.google.android.inputmethod.latin",
            "com.qisiemoji.inputmethod",
            "com.touchtype.swiftkey",
            "com.typany.ime",
            "com.jb.emoji.gokeyboard",
            "com.nuance.swype.trial",
            "com.aitype.android"
    };

    private InputBase initInputBase(String packName){
        InputBase ib = null;
        if (packName.equals(aryInputPackName[InputType.GBOARD])){
            ib = new GBoard();
        }else if(packName.equals(aryInputPackName[InputType.KIKA])){
            ib = new Kika();
        }else if(packName.equals(aryInputPackName[InputType.SWIFTKEY])){
            ib = new SwiftKey();
        }else if(packName.equals(aryInputPackName[InputType.TYPANY])){
            ib = new Typany();
        }else if(packName.equals(aryInputPackName[InputType.GOKB])){
            ib = new GoKeyboard();
        }else if(packName.equals(aryInputPackName[InputType.SWYPE])){
            ib = new Swype();
        }else if(packName.equals(aryInputPackName[InputType.AITYPE])){
            ib = new AiType();
        }

        return ib;
    }

    public String getVersion() {
        Log.d("hookversion", "in");
        String version = "";
        try {
            PackageManager manager = getContext().getPackageManager();
            PackageInfo info = manager.getPackageInfo(getContext().getPackageName(), 0);
            version = info.versionName;
            Log.d("hookversion", mPackName + ":" + version);

        } catch (Exception e) {
            e.printStackTrace();
            return version;
        }

        return  version;
    }

    @Override
    public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
//        Process process = Runtime.getRuntime().exec("su");
//        String cmd = "chmod 777" + strFileName;
//        DataOutputStream os = new DataOutputStream(process.getOutputStream());
//        os.writeBytes(cmd + "\n");
//        os.writeBytes("exit\n");
        //fu.CreateFile(strFileName);

//        if(mInputBase == null && mPackName == null){
//            Log.d("xposed_HLP", "init InputBase");
//            String defaultIM = Settings.Secure.getString(getContext().getContentResolver(),
//                    Settings.Secure.DEFAULT_INPUT_METHOD);
//            mPackName = defaultIM.substring(0, defaultIM.indexOf('/'));
//            Log.d("xposed_HLP", "mPackName:" + mPackName);
//            mInputBase = initInputBase(mPackName);
//            mFilePath = "/data/data/" + mPackName + "/ddmstrace.trace";
//            mMark = "mark" + mPackName;
//            //hookOnCreate(lpparam.classLoader);
//            //assert (mInputBase != null);
//        }

//        String appPermission = lpparam.appInfo.permission;
//
//        if (appPermission == null){return;}
//
//        if (!appPermission.equals("android.permission.BIND_INPUT_METHOD")){
//            Log.d("xposedTest", appPermission);
//            return;
//        }else{
//            mPackName = lpparam.packageName;
//        }
        int nCount;
        for (nCount = 1; nCount<aryInputPackName.length; nCount++ ){
            if (!lpparam.packageName.equals(aryInputPackName[nCount])){
                continue;
            }
            mPackName = aryInputPackName[nCount];
            break;
        }

        if (nCount == aryInputPackName.length){
            return;
        }

        mFilePath = "/data/data/" + mPackName + "/ddmstrace.trace";
        mPredictInterfaceFile = "/data/data/" + mPackName + "/predictinterface";
        mMark = "mark" + mPackName;
        //mPreMark = "pre" + mPackName;

        mInputBase = initInputBase(mPackName);

        Log.d("xposed_autoReverse", mPackName + "find.");

        mlpparam = lpparam;
        mClassLoader = lpparam.classLoader;

//        String version = getVersion();
//        if (createNewFile(mPredictInterfaceFile)){
//            ArrayList<String> list = readFile();
//            if (list.size() <= 0){//空文件
//                writeFile(version);
//            }else if(list.size() > 1 && version.equals(list.get(0))){//已存在逆向接口
//                return;
//            }
//        }

        boolean result = autoTrace(lpparam.classLoader);
        if (!result){
            Log.d("xposed_autoReverse", "failed.");
            return;
        }else{
            Log.d("xposed_autoReverse", "success.");
        }
    }

    //产生trace文件
    public boolean autoTrace(ClassLoader classLoader){

        final boolean[] result = {false};
        final Class<?> ICWrapper = XposedHelpers.findClass("com.android.internal.view.InputConnectionWrapper", classLoader);

//        XposedHelpers.findAndHookMethod(
//                "android.view.View" ,
//                classLoader,
//                "dispatchTouchEvent",
//                MotionEvent.class,
//                new XC_MethodHook() {
//                    @Override
//                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                        // this will be called before the clock was updated by the original method
//                        Log.d("xposed_trace", "onStartInputView");
//                        if(!mTrace.get()) {
//                            mTrace.set(true);
//                            Debug.startMethodTracing(mFilePath);
//                        }
//                    }
//
//                    @Override
//                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                        // this will be called after the clock was updated by the original method
//                    }
//                });

//        XposedHelpers.findAndHookMethod(
//                "android.inputmethodservice.InputMethodService" ,
//                classLoader,
//                "onStartInputView",
//                android.view.inputmethod.EditorInfo.class, boolean.class,
//                new XC_MethodHook() {
//                    @Override
//                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                        // this will be called before the clock was updated by the original method
//
//
//                    }
//
//                    @Override
//                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                        // this will be called after the clock was updated by the original method
//                    }
//                });

        XposedHelpers.findAndHookMethod(
                "com.android.internal.view.InputConnectionWrapper" ,
                classLoader,
                "setComposingText",
                CharSequence.class, int.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        // this will be called before the clock was updated by the original method
                        Log.d("xposed_trace", "onStartInputView");
                        if(!mTrace.get()) {
                            mTrace.set(true);
                            Log.d("xposed_trace", "startMethodTracing");
                            Debug.startMethodTracing(mFilePath);
                        }
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        // this will be called after the clock was updated by the original method
                    }
                });

        XposedHelpers.findAndHookMethod(
                "com.android.internal.view.InputConnectionWrapper" ,
                classLoader,
                "commitText",
                CharSequence.class, int.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        // this will be called before the clock was updated by the original method

                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        // this will be called after the clock was updated by the original method
                        Log.d("xposed_trace", "onFinishInputView");
                        if(mTrace.get()) {
                            mTrace.set(false);
                            Log.d("xposed_trace", "stopMethodTracing");
                            Debug.stopMethodTracing();
                        }
                        ArrayList<String> suggestList = analyseTrace(mFilePath);
                        if (suggestList != null && suggestList.size() != 0){
                            //connectAutoReverse(suggestList, mMark);
                            Log.d("xposed_trace", "trace Success");
                            result[0] = true;
                        }

                    }
                });

        return result[0];
    }

    //分析抓取的trace文件,返回可能为预测词接口的函数信息的list
    public ArrayList<String> analyseTrace(String filePath){
        String content = ""; //文件内容字符串

        ArrayList<String> suggestList = new ArrayList<String>();

        //打开文件
        File file = new File(filePath);
        if(file.exists() && file.length() == 0) {
            Log.d("xposedFile", " File empty.");
            return null;
        }

        //非目录的判断
        if (file.isDirectory())
        {
            Log.d("xposedFile", "The File doesn't not exist.");
            return null;
        }
        else
        {
            try {
                InputStream instream = new FileInputStream(file);
                if (instream != null)
                {
                    InputStreamReader inputreader = new InputStreamReader(instream);
                    BufferedReader buffreader = new BufferedReader(inputreader);
                    String line = null;
                    //分行读取
                    line = buffreader.readLine();
                    while(!line.equals("*methods") && line != null){
                        line = buffreader.readLine();
                    }

                    //Log.d("xposedFile", "into *methods");
                    line = buffreader.readLine();

                    while (line != null &&!line.equals("*end")) {
                        if(getSuggestFuncList(line)){
                            suggestList.add(line);
                            autoHookTest(mClassLoader, mlpparam, line);
                        }
                        line = buffreader.readLine();
                    }
                    Log.d("xposedFile","SuggestionFunc：" + suggestList.toString());

                    String hookMethodInfo = suggestList.get(suggestList.size() - 1);
                    Log.d("xposedFile","hookMethodInfo：" + hookMethodInfo);
                    //autoHookTest(mClassLoader, mlpparam, hookMethodInfo);
                    instream.close();

                }
            }
            catch (FileNotFoundException e)
            {
                Log.d("xposedFile", "The File doesn't not exist.");
            }
            catch (IOException e)
            {
                Log.d("xposedFile", e.getMessage());
            }
        }
        return suggestList;
    }

    //接口特征规则匹配
    public boolean getSuggestFuncList(String line){
        if (line == null || line.isEmpty() || mInputBase == null){
            Log.d("getSuggestFuncList", mInputBase.toString());
            return false;
        }

        String[] arrays = funcLineAnalyse(line);
        if (arrays == null){
            return false;
        }

        String strNum = arrays[0];
        String strClassName = arrays[1];
        String strMethodName = arrays[2];
        String strSig = arrays[3];

        return mInputBase.isPredictInterface(strNum, strClassName, strMethodName, strSig);
    }

    //分析行，拆分出Num，ClassName，MethodName，Sig
    private static String[] funcLineAnalyse(String line){
        if (line == null){
            return null;
        }

        char csplit = 0x09;
        String[] arrays = line.split(csplit + "+");

        return arrays;
    }

    //参数分析类
    private Object paramClass(String paramSig, ClassLoader classLoader){

        String strP = null;
        Object obj = null;

        if (paramSig.charAt(0) == '['){
            //类数组
            strP = paramSig.substring(2);//去掉[L
            strP.replace('/', '.');
            Object tmp = XposedHelpers.findClass(paramSig, classLoader);
            obj = Array.newInstance((Class<?>) tmp,1).getClass();
        }else{
            strP = paramSig.substring(1);//去掉L
            strP = strP.replace('/', '.');
            Log.d("paramClass", strP);
            obj = XposedHelpers.findClass(strP, classLoader);
        }

        return  obj;
    }

    //参数分析基本类型
    private Object paramBase(char sz){
        String strP = null;
        Object obj = null;

        switch (sz){
            case 'Z':
                obj = boolean.class;
                break;
            case 'B':
                obj = byte.class;
                break;
            case 'C':
                obj = char.class;
                break;
            case 'S':
                obj = short.class;
                break;
            case 'I':
                obj = int.class;
                break;
            case 'J':
                obj = long.class;
                break;
            case 'F':
                obj = float.class;
                break;
            case 'D':
                obj = double.class;
                break;
            case 'V':
            default:
                obj = null;
        }

        return  obj;
    }

    //分析签名获取函数返回值类型
    private String getSigRet(String sig){
        String ret = sig.substring(sig.indexOf(')') + 1, sig.length() - 1);
        String result = null;
        if (ret.contains("/") && ret.charAt(0) == 'L'){
            //返回值为类
            result = ret.substring(1);//去掉L
        }else{
            result = ret;
        }

        return result;
    }

    //参数分析，获取要hook的函数的参数Object数组
    private ArrayList<Object> SigAnalyse(String sig, ClassLoader classLoader){
        Log.d("SigAnalyse", sig);

        String ret = sig.substring(sig.indexOf(')') + 1, sig.length() - 1);//返回值
        String param = sig.substring(1, sig.indexOf(')'));//参数
        Log.d("SigAnalyse", "ret:" + ret + ", param:" + param);

        String[] aryParam = param.split(";");

        ArrayList<Object> listObj = new ArrayList<>();

        for (int i = 0; i< aryParam.length; i++){

            Log.d("SigAnalyse", aryParam[i]);
            //参数为类
            if (aryParam[i].contains("/") && aryParam[i].charAt(0) == 'L'){
                Object obj = paramClass(aryParam[i], classLoader);
                assert (obj != null);
                listObj.add(obj);
                continue;
            }

            //参数为基础类型
            for (int j =0; j < aryParam[i].length(); j++){
                char sz = aryParam[i].charAt(j);
                switch (sz) {
                    case 'L':{//class
                        Object obj = paramClass(aryParam[i].substring(j), classLoader);
                        j = aryParam[i].length();
                        assert (obj != null);
                        listObj.add(obj);
                        break;
                        }
                    case '[':{//数组
                        j++;
                        sz = aryParam[i].charAt(j);
                        Object tmp = paramBase(sz);
                        assert (tmp != null);
                        Object obj = Array.newInstance((Class<?>) tmp,1).getClass();
                        listObj.add(obj);
                        break;
                        }
                    default:
                        Object obj = paramBase(sz);
                        assert (obj != null);
                        listObj.add(obj);

                }
            }
        }

        Log.d("SigAnalyse", listObj.toString());
        return listObj;
    }

    //hook可能的接口并获取hook的返回值
    public void autoHookTest(ClassLoader classLoader, final LoadPackageParam lpparam, final String line){
        String[] arrays = funcLineAnalyse(line);
        assert (arrays != null);

        String strNum = arrays[0];
        String strClassName = arrays[1];
        String strMethodName = arrays[2];
        String strSig = arrays[3];

        Log.d("autoHookTest", "Num:" + strNum + ",ClsName:" +strClassName + ",Method:" + strMethodName + ",Sig:" + strSig);
        ArrayList<Object> listObj = SigAnalyse(strSig, classLoader);
        assert (listObj != null);

        XC_MethodHook xcMH = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                // this will be called before the clock was updated by the original method
                Log.d("xpose_Prediction","Before Hook");
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                // this will be called after the clock was updated by the original method
                Log.d("xposed_Prediction", param.args[0].toString());

                ArrayList<String> result = mInputBase.getCandidateList(param);
                if (result != null && result.size() >= 2) {
                    connectAutoReverse(line, result, mMark);
                    writePredictInterface(line, result);

                }
                Log.d("xpose_Predictions:", result.toString());

            }
        };

        listObj.add(xcMH);

        Object[] aryObj = new Object[listObj.size()];
        for (int i = 0; i< listObj.size(); i++){
            aryObj[i] = listObj.get(i);
        }

        XposedHelpers.findAndHookMethod(
                strClassName,
                classLoader,
                strMethodName,
                aryObj
        );
    }

    //文件操作
    public boolean createNewFile(String filePath){
        boolean result = false;
        File file = new File(filePath);
        if(!file.exists()) {
            System.out.println(filePath + filePath + "创建");
            try {
                if (file.createNewFile()) {
                    System.out.println(filePath + filePath + "成功！");
                    result = true;
                } else {
                    System.out.println(filePath + filePath + "失败！");
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println(filePath + filePath + "失败！" + e.getMessage());
            }

        }else{
            result = true;
        }
        return true;
    }

    public ArrayList<String> readFile() {
        ArrayList<String> list = new ArrayList<>();
        StringBuffer sb= new StringBuffer("");

        if (!createNewFile(mPredictInterfaceFile)){
            return list;
        }

        FileReader reader = null;
        try {
            reader = new FileReader(mPredictInterfaceFile);
            BufferedReader br = new BufferedReader(reader);
            String str = null;
            while((str = br.readLine()) != null) {
                list.add(str);
                System.out.println(str);
            }
            Log.d("readfile", list.toString());
            br.close();
            reader.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return list;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return list;
    }

    public void writeFile(String line){
        FileWriter writer = null;
        try {

            if(!createNewFile(mPredictInterfaceFile)){
                return;
            }
            Log.d("writeFile", line);
            writer = new FileWriter(mPredictInterfaceFile);
            BufferedWriter bw = new BufferedWriter(writer);
            bw.write(line);
            bw.newLine();

            bw.close();
            writer.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //将筛选出的最符合的接口写入文件
    public void writePredictInterface(String line, ArrayList<?> list){
        Log.d("writePredictInterface", line + " " + list.toString());
        if (list == null || list.size() < 3){
            return;
        }

        ArrayList<String> exitlist = readFile();

        Log.d("writePredictInterface",exitlist.toString());

        int i = 0;
        for(;i<exitlist.size(); i++){
            //该接口已经写入过
            if (exitlist.get(i).equals(line))
                return;
        }
        if (i == exitlist.size()){
            writeFile(line);
        }
    }

    //将预测的接口显示到autoReverse界面
    public void connectAutoReverse(String line, ArrayList<?> list, String mark) {
        //ArrayList<String> result = new ArrayList<>();
        String strInfo = null;
        if (line == null || list == null || list.size() == 0){
            Log.d("xposed_broadcast", "map empty");
        }

        strInfo = line + "\nCandidateList:" + list.toString();

        if (strInfo == null){return;}

        Bundle bundle = new Bundle();
        bundle.putString(mark, strInfo);
        Intent intent = new Intent(mark);
        intent.putExtras(bundle);

        Context context = getContext();
        if (context == null){
            Log.d("xposed_getContext", "error");
            return;
        }

        Log.d("xposed_broadcast", "send mark:" + mMark);
        getContext().sendBroadcast(intent);
    }

    private Context getContext() {
        return AndroidAppHelper.currentApplication();
    }
}
