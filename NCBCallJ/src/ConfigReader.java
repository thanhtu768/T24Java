import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Properties;


import com.google.gson.Gson;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;

public class ConfigReader 
{
    public static final String CONFIG_FILE_PATH = "java/configuration/app.config"; 
    public String  sourceAppID;
    public String targetAppIDs;
    public String userID;
    public String userPassword;
    public static String FORMAT_CNF_URL = "endPoint.%s.url";
    public static String FORMAT_CNF_METHOD = "endPoint.%s.method";
    public static String FORMAT_CNF_FILE = "endPoint.%s.templateFile";
    public static void ParserJson() throws Exception
    {
        try (Reader reader = new BufferedReader(new FileReader(CONFIG_FILE_PATH))) 
        {
            Gson gson = new Gson();
            HashMap<String,String>  json = gson.fromJson(reader,HashMap.class);            
            json.values().getClass();            
            System.out.println(json.values());
            System.out.println(json.toString());
        } catch (Exception e) {
            throw e;
        }
    }

    public static String GetFlatFileConfig(String pName) throws Exception
    {        
        try
        {
            Properties  prop = new Properties();
            Reader reader = new BufferedReader(new FileReader(CONFIG_FILE_PATH));
            prop.load(reader);
            String result = prop.getProperty(pName);
            System.out.println(result);
            return result;
        } catch (Exception e) { 
            throw e;
        } 
    }

    public static String GetJsonConfigByMultiKey(String kName) throws Exception
    {
        try (JsonReader reader = new JsonReader(new FileReader(CONFIG_FILE_PATH))) 
        {            
            String kValue;
            JsonObject jObject = JsonParser.parseReader(reader).getAsJsonObject();
            kValue = CustomJsonHandle.GetJsonElementByMultiKey(jObject, kName).getAsString();
            return kValue;
        } catch (JsonIOException | JsonSyntaxException | IOException e) {
            throw e;
        }
    }
}
