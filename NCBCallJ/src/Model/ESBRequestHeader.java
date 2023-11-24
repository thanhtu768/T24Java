package Model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class ESBRequestHeader {
    public CommonInfoESB common = new CommonInfoESB();
    public ClientESB client = new ClientESB();

    public String toJson(){
        try {
            Gson gson = new GsonBuilder().disableHtmlEscaping().create();
            String result = gson.toJson(this);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
