package input;

import android.renderscript.ScriptGroup;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;


public class AiType extends InputBase {
    public AiType(){
        setPackName("com.aitype.android");
    }

    @Override
    public ArrayList<String> getCandidateList(XC_MethodHook.MethodHookParam param) {
        ArrayList<String> result = new ArrayList<>();
        if (param.getResult() instanceof List || param.getResult() instanceof ArrayList){
            List<?> ret = (List<?>) param.getResult();
            for (int i = 0; i< ret.size(); i++){
                result.add(ret.get(i).toString());
            }
        }

        for (int j = 0; j< param.args.length; j++){
            if (param.args[j] instanceof List || param.args[j] instanceof ArrayList){
                List<?> ret = (List<?>) param.args[j];
                for (int i = 0; i< ret.size(); i++){
                    result.add(ret.get(i).toString());
                }
                break;
            }
        }

        return result;
    }

    @Override
    public boolean isPredictInterface(String num, String clsName, String methodName, String Sig) {
        boolean result = false;
        if(Sig.contains("List")){
            result = true;
        }

        return result;
    }
}
