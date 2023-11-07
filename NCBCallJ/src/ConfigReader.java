import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    public static String FORMAT_CNF_FILE_RES = "endPoint.%s.templateFileResponse";
     
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

        try 
        {  
                      
            String kValue;
            JsonObject jObject = GetAppConfig();
            kValue = CustomJsonHandle.GetJsonElementByMultiKey(jObject, kName).getAsString();
            return kValue;
        } catch (JsonIOException | JsonSyntaxException | IOException e) {
            throw e;
        }
    }

    public static List<String> GetResponseConfContent(String iName) throws Exception{
        String confKey = String.format(FORMAT_CNF_FILE_RES, iName);
        String filePath = CustomJsonHandle.GetMultiLevelValueConfig(confKey, CONFIG_FILE_PATH);
        File file = new File(filePath);        
        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isReader = new InputStreamReader(fis, "UTF-8");
            BufferedReader reader = new BufferedReader(isReader);
            String line;
            List<String> lstResult = new ArrayList<String>();
            while((line = reader.readLine()) != null)
                lstResult.add(line);
            fis.close();
            isReader.close();
            reader.close();
            return lstResult;
        } catch (FileNotFoundException | UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            throw e;
        }
    }

    public static JsonObject GetAppConfig() throws Exception{
        try (JsonReader jReader = new JsonReader(new FileReader(CONFIG_FILE_PATH))){
            JsonObject jObj = JsonParser.parseReader(jReader).getAsJsonObject();
            return jObj;
        } catch (Exception e) {
            // TODO: handle exception
            throw e;
        }
    }
}
