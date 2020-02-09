package input;

import com.g.autoreversegit.hook;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;



public class Kika extends InputBase{
    //设置输入法包名
    public Kika(){
        setPackName("com.qisiemoji.inputmethod");
    }

    /*
    作用：获取Hook到的预测词
    参数：xpose hook到对应函数的信息
    返回值：预测词List
     */
    @Override
    public ArrayList<String> getCandidateList(XC_MethodHook.MethodHookParam param) {
        if (param.getResult() instanceof List || param.getResult() instanceof ArrayList)
            return (ArrayList<String>) param.getResult();
        return null;
    }

    /*
    作用：判断该接口是否可能是预测词接口
    参数：接口信息，ID，ClassName, MethodName, 签名
    返回值：是预测词接口返回true，否则返回false
     */
    @Override
    public boolean isPredictInterface(String num, String clsName, String methodName, String Sig) {
        boolean result = false;
        if(methodName.contains("Suggestion") && Sig.contains("List")){
            result = true;
        }
        return  result;
    }
}
