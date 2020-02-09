package input;

import java.util.ArrayList;

import de.robv.android.xposed.XC_MethodHook;


public class GoKeyboard extends InputBase {
    public GoKeyboard(){
        setPackName("com.jb.emoji.gokeyboard");
    }

    @Override
    public ArrayList<String> getCandidateList(XC_MethodHook.MethodHookParam param) {
        return (ArrayList<String>) param.getResult();
    }

    @Override
    public boolean isPredictInterface(String num, String clsName, String methodName, String Sig) {
        boolean result = false;

        if(methodName.contains("Suggestion") && Sig.contains("List")){
            result = true;
        }

        return  result;
    }
}
