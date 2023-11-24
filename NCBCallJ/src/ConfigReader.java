import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
    public static String FORMAT_CNF_ESB = "endPoint.%s.esbHeader";
    public static String FORMAT_CNF_ESB_DEFAULT = "connection.esbHeader";
    public static String FORMAT_CNF_FILE_RES = "endPoint.%s.templateFileResponse";

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

    public static String GetJsonConfigByMultiKey(String kName)
    { 
        String kValue;
        JsonObject jObject = new JsonObject();
        try 
        {  
            jObject = GetAppConfig();
            kValue = CustomJsonHandle.GetJsonElementByMultiKey(jObject, kName).getAsString();
            return kValue;
        } catch (Exception e) {
            try 
            {
                kValue = CustomJsonHandle.GetJsonElementByMultiKey(jObject, kName).toString();
                return kValue;
            } catch (Exception ex) {
                ex.printStackTrace();
                LogControl.WriteStackTrace(ex);
            }
            return null;
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
            e.printStackTrace();
            throw e;
        }
    }

    public static JsonObject GetAppConfig() throws Exception{
        try (JsonReader jReader = new JsonReader(new FileReader(CONFIG_FILE_PATH))){
            JsonObject jObj = JsonParser.parseReader(jReader).getAsJsonObject();
            return jObj;
        } catch (Exception e) {
            throw e;
        }
    }
}
