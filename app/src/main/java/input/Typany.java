package input;

import android.util.Log;

import com.g.autoreversegit.hook;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by Administrator on 2017/12/22.
 */

public class Typany extends InputBase {
    public Typany(){
        setPackName("com.typany.ime");
    }

    @Override
    public ArrayList<String> getCandidateList(XC_MethodHook.MethodHookParam param) {
        ArrayList<String> result = new ArrayList<>();

        if (param.args[0] instanceof List || param.args[0] instanceof ArrayList){
            ArrayList<?> ary = (ArrayList<?>) param.args[0];

            for(int i = 0; i<ary.size(); i++){
                String str = getPrediction(ary.get(i));
                Log.d("getCandidateList", str);
                result.add(str);
            }
        }

        return result;
    }

    @Override
    public boolean isPredictInterface(String num, String clsName, String methodName, String Sig) {
        boolean result = false;
        if((clsName.contains("Candidate") || clsName.contains("candidate")) &&Sig.contains("List")){
            result = true;
        }

        return result;
    }

    public String getPrediction(Object candidate) {
        String result = null;

        if (candidate == null || hook.mlpparam == null) return result;

        //候选词类
        final Class<?> Candidate = XposedHelpers.findClass("com.typany.engine.shared.Candidate", hook.mlpparam.classLoader);

        try {
            Method methodGetWord = Candidate.getMethod("getWord");
            methodGetWord.setAccessible(true);
            result = (String)methodGetWord.invoke(candidate);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        return  result;
    }
}
