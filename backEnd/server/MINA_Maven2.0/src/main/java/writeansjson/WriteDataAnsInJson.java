package writeansjson;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class WriteDataAnsInJson {
/**
 * 这部分代码还是直接复制了去年的，大概看了一下需要改一下getALL
 * */
    public static<T> String getAns(ArrayList<T> list, String type){
        Gson gs = new Gson();
        StringBuilder answer = new StringBuilder();
        if(list.size()==0){
            answer.append("{\""+type+"\":[]}");
        }
        else{
            answer.append("{\""+type+"\":[");
            for(T p : list){
                answer.append(gs.toJson(p, new TypeToken<T>(){}.getType())+",");
            }
            answer.deleteCharAt(answer.length()-1);
            answer.append("]}");
        }
        return answer.toString();
    }
//    public static<T> String getAll(ArrayList<T> paperList, ArrayList<CNNews> cnNewsList, ArrayList<News> newsList, ArrayList<Patent_new> patentList) {
//        String ans = getAns(paperList,"papers");
//        //开始添加patent结果
//        ans = ans.substring(0,ans.length()-1); //删去"}"
//        ans += ",";
//        String pa = getAns(patentList,"patents");
//        pa = pa.substring(1); //删去"{"
//        ans = ans + pa;
//        //patent结果添加完毕
//        //开始添加CN_News结果
//        ans = ans.substring(0,ans.length()-1); //删去"}"
//        ans += ",";
//        String cnnews = getAns(cnNewsList,"CN_News");
//        cnnews = cnnews.substring(1);
//        ans = ans + cnnews;
//        ans = ans.substring(0,ans.length()-1); //开始添加news
//        ans += ",";
//        String news = getAns(newsList,"news");
//        news = news.substring(1);
//        ans = ans + news;
//        return ans;
//    }
//
//
    /*
     * 用于拼接最后返回给前端的字符串
     * */
    public static String getFinalAnswer(String retrieveAnswer, String visualAnswer){
        String result = "";
        result += retrieveAnswer.substring(0,retrieveAnswer.length()-1)+",";
        result += visualAnswer.substring(1)+"}";
        return result;
    }

}
