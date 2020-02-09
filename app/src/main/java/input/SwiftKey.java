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


public class SwiftKey extends InputBase {

    public SwiftKey(){
        setPackName("com.touchtype.swiftkey");
    }

    @Override
    public ArrayList<String> getCandidateList(XC_MethodHook.MethodHookParam param) {
        Log.d("getCandidateList", "SwiftKey");

        ArrayList<String> result = new ArrayList<>();

        if (param.args[0] instanceof List || param.args[0] instanceof ArrayList){
            ArrayList<?> ary = (ArrayList<?>) param.getResult();

            for(int i = 0; i<ary.size(); i++){
                String str = GetPrediction(ary.get(i));
                Log.d("getCandidateList", str);
                result.add(str);
            }
        }


        return result;
    }

    @Override
    public boolean isPredictInterface(String num, String clsName, String methodName, String Sig) {
        boolean result = false;
        if(clsName.contains("candidates")&& (methodName.contains("Predict") || methodName.contains("Candidate") || methodName.contains("candidates")) &&Sig.contains("List")){
            result = true;
        }

        return result;
    }

    public String GetPrediction(Object candidate){
        String result = null;

        assert (candidate != null);
        //assert (mClassLoader != null);

        //候选词基类
        final Class<?> Candidate = XposedHelpers.findClass("com.touchtype_fluency.service.candidates.Candidate", hook.mClassLoader);

        //预测词
        final Class<?> FluencyCandidate = XposedHelpers.findClass("com.touchtype_fluency.service.candidates.FluencyCandidate",hook.mClassLoader);

        //补全词??
        final Class<?> VerbatimCandidate = XposedHelpers.findClass("com.touchtype_fluency.service.candidates.VerbatimCandidate",hook.mClassLoader);

        try {
            Method getUserFacingText = Candidate.getMethod("getUserFacingText");
            getUserFacingText.setAccessible(true);
            result = (String)getUserFacingText.invoke(candidate);
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
