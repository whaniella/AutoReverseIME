package input;

import android.util.Log;

import com.g.autoreversegit.hook;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;



public class GBoard extends InputBase{
    public GBoard(){
        setPackName("com.google.android.inputmethod.latin");
    }


    @Override
    public ArrayList<String> getCandidateList(XC_MethodHook.MethodHookParam param) {
        ArrayList<String> result = new ArrayList<>();
        if (param.args[0] instanceof List || param.args[0] instanceof ArrayList){
            List<Object> ary = (List<Object>)param.args[0];
            Log.d("GboardPrediction", ary.toString());

            for (int i = 0; i< ary.size(); i++){
                try {
                    String str = getText(ary.get(i));
                    if (str != null){
                        result.add(str);
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            Log.d("GboardPrediction", result.toString());
        }

        return result;
    }

    @Override
    public boolean isPredictInterface(String num, String clsName, String methodName, String Sig) {
        boolean result = false;

        if(methodName.equals("appendTextCandidates") && Sig.contains("List")){
            result = true;
        }

        return  result;
    }

    private String getText(Object obj) throws IllegalAccessException {
        String str = null;

        final Class<?> Candidate = XposedHelpers.findClass("com.google.android.apps.inputmethod.libs.framework.core.Candidate", hook.mClassLoader);

        Field[] fields = Candidate.getDeclaredFields();
        for (Field field : fields)
        {
            if (field.getName().equals("a") && field.get(obj) instanceof CharSequence){
                str = String.valueOf(field.get(obj));
                break;
            }
        }

        return str;
    }

}
