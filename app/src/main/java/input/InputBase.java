package input;

import java.util.ArrayList;

import de.robv.android.xposed.XC_MethodHook;



public abstract class InputBase {
    public static String mPackName;

    public void setPackName(String packName){
        mPackName = packName;
    }

    public String getPackName(){
        return  mPackName;
    }

    public abstract ArrayList<String> getCandidateList(XC_MethodHook.MethodHookParam param);

    public abstract boolean isPredictInterface(String num, String clsName, String methodName, String Sig);

}
