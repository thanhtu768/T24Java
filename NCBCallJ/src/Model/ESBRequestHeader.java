package Model;

import com.google.gson.Gson;

public class ESBRequestHeader {
    public CommonInfoESB common = new CommonInfoESB();
    public ClientESB client = new ClientESB();

    public String toJson(){
        try {
            Gson gson = new Gson();
            String result = gson.toJson(this);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
