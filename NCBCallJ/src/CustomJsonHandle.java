import java.io.FileReader;
import java.io.IOException;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

public class CustomJsonHandle{

    public static JsonElement GetJsonElementByMultiKey(JsonObject jObject, String key)
    {
        try 
        {
            String[] lstKey = key.split("\\.");
            JsonObject jObjCopy = jObject.deepCopy();
            JsonElement jEle = null;
            for (String str : lstKey) {
                if(str == lstKey[lstKey.length -1]){
                        jEle = jObjCopy.get(str);
                }else{
                    jObjCopy = jObjCopy.getAsJsonObject(str);
                }
            }
            return jEle;
        } catch (Exception e) {
            throw e;
        }        
    } 

    public static String GetMultiLevelValueConfig(String kName, String filePath) throws Exception
    {
        FileReader fReader = new FileReader(filePath);
        try
        {            
            return GetMultiLevelValueConfig(kName, fReader);
        } catch (JsonIOException | JsonSyntaxException | IOException e) {
            throw e;
        }finally{
            fReader.close();
        }
    }

    public static String GetMultiLevelValueConfig(String kName, FileReader fReader) throws Exception
    {
        try (JsonReader jReader = new JsonReader(fReader)) 
        {            
            String kValue = GetMultiLevelValueConfig(kName, jReader);
            return kValue;
        } catch (JsonIOException | JsonSyntaxException | IOException e) {
            throw e;
        }
    }

    public static String GetMultiLevelValueConfig(String kName, JsonReader jReader) throws Exception
    {
        try
        {            
            String kValue;
            JsonObject jObject = JsonParser.parseReader(jReader).getAsJsonObject();
            kValue = CustomJsonHandle.GetJsonElementByMultiKey(jObject, kName).getAsString();
            return kValue;
        } catch (JsonIOException | JsonSyntaxException e) {
            throw e;
        }
    }
}