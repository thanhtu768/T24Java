import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import Model.ClientESB;
import Model.CommonInfoESB;
import Model.ESBRequestHeader;
import Model.T24ParamModel;

public class CallAPIController {

    public static final int ESB_TIMEOUT = 1000;
    public static final String CHARSET_DEFAULT = "UTF-8";
    public static final String CONTENT_TYPE_DEFAUL = "application/json";
    public static final String FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    public static final String T24_DATE_FORMAT = "yyyyMMdd";
    public static final String FORMART_REQ_ROOT_NAME = "%sReq";
    public static final Date NOW_DATETIME = Calendar.getInstance().getTime();
    public static String FORMAT_TEMP_RES_STATUS = "%sres.responseStatus.status";
    public static String FORMAT_TEMP_RES_BODY = "%sRes.bodyRes";
    static String strUrl, method, request, esbCf;
    static URL url;
    public static T24ParamModel paramModel = new T24ParamModel();

    /**
     * 
     * @param data T24 sample:
     *             nopRutTienNHNN^param1*param2*param3*param4*...
     *             Example:nopRutTienNHNN^ID.FLEX1141*USER1*USER2*VND12501000100011*VND*50000000*20230909*500000000224*CN Ha noi Thuc hien rut tien mat tu NHNN Thuc hien rut tien mat tu NHNN*Thuc hien rut tien mat tu NHNN Thuc hien rut tien mat tu NHNN Thuc hien rut tien mat tu NHNN*9001*VN0010001 
     * @return response raw data / Json response
     */

    public static String CallESBRestAPI(String data) {
        try {
            SetInputCommonData(data);
            request = BuildRequestFromTempData(paramModel);
            request = GetESBRequestHeader(request, esbCf);
            return GetResponseString(request, false);
        } catch (Exception e) {
            e.printStackTrace();
            LogControl.WriteStackTrace(e);
            return e.toString();
        }
    }

     public static String CallESB_UseFullResponse(String data) {
        try {
            SetInputCommonData(data);
            request = BuildRequestFromTempData(paramModel);
            request = GetESBRequestHeader(request, esbCf);
            return GetResponseString(request, true);
        } catch (Exception e) {
            e.printStackTrace();
            LogControl.WriteStackTrace(e);
            return e.toString();
        }
    }

    /**
     * 
     * @param data T24 sample: 'getFlexTransInfoById^{"functionCode":"FLEXCASH-GETFLEXTRANSINFOBYID-SOAP-T24","columnName":"ID","criteriaValue": "tult-FLEX-TEST9443C14","operand": "EQ"}'
     * @return
     * @throws Exception
     */
    public static String CallESB_UseBodyRequest(String data) throws Exception {
        try {
            SetInputCommonData(data);
            ESBRequestHeader reqHeaderObj = GetESBRequestHeader(esbCf);
            String reqHeader = reqHeaderObj.toJson();
            request = BuildRequestFromStringData(reqHeader, paramModel.requestDataRaw);
            return GetResponseString(request, false);
        } catch (Exception e) {
            e.printStackTrace();
            LogControl.WriteStackTrace(e);
            return e.toString();
        }
    }

    /**
     * 
     * @param data T24 sample: 'getFlexTransInfoById^{"getFlexTransInfoByIdReq":{"header": {"common": {"serviceVersion": "1","messageId": "{{$guid}}","transactionId": "{{$guid}}","messageTimestamp":"2022-12-14T11:29:04.464+07:00"},"client": {"sourceAppID":"MB","targetAppIDs": "T24","userDetail": {"userID":"MB","userPassword": "RUJBTksxMjM="}}},"bodyReq":{"functionCode":"FLEXCASH-GETFLEXTRANSINFOBYID-SOAP-T24","columnName":"ID","criteriaValue": "tult-FLEX-TEST9443C14","operand": "EQ"}}}'
     * @return
     * @throws Exception
     */
    public static String CallESB_UseFullRequest(String data) throws Exception {
        try {
            SetInputCommonData(data);
            request = paramModel.requestDataRaw;
            return GetResponseString(request, false);
        } catch (Exception e) {
            e.printStackTrace();
            LogControl.WriteStackTrace(e);
            throw e;
        }
    }

    static void SetInputCommonData(String data) throws Exception {
        LogControl.WriteLog(data);
        paramModel = GetT24ParamModel(data);
        strUrl = ConfigReader
                .GetJsonConfigByMultiKey(String.format(ConfigReader.FORMAT_CNF_URL, paramModel.endPointName));
        method = ConfigReader
                .GetJsonConfigByMultiKey(String.format(ConfigReader.FORMAT_CNF_METHOD, paramModel.endPointName));
        url = new URL(strUrl);
        esbCf = ConfigReader
                .GetJsonConfigByMultiKey(String.format(ConfigReader.FORMAT_CNF_ESB, paramModel.endPointName));
    }

    public static T24ParamModel GetT24ParamModel(String data) {
        try {
            T24ParamModel t24DataModel = new T24ParamModel();
            char FM = (char) 254;
            char VM = (char) 253;
            // char SM = (char)252;
            String[] dataArray = data.split("[" + FM + "^]");
            t24DataModel.endPointName = dataArray[0];
            t24DataModel.requestDataRaw = dataArray[1];
            t24DataModel.requestDataArr = t24DataModel.requestDataRaw.split("[" + VM + "\\*]");
            return t24DataModel;
        } catch (Exception e) {
            String errorMess = "GetT24ParamModel: Data from T24 not match with T24ParamModel/" + data + "\n";
            System.out.println(errorMess);
            LogControl.WriteLog(errorMess);
            throw e;
        }
    }

    public static String BuildRequestFromTempData(T24ParamModel t24DataModel) throws Exception {
        String templateFilePath = ConfigReader
                .GetJsonConfigByMultiKey(String.format(ConfigReader.FORMAT_CNF_FILE, t24DataModel.endPointName));
        try (BufferedReader rd = new BufferedReader(new FileReader(templateFilePath))) {
            String request = "";
            String line;
            while ((line = rd.readLine()) != null)
                request = String.valueOf(request) + line;

            for (int i = 0; i < t24DataModel.requestDataArr.length; i++) {
                String tmpStr = "[" + i + "]";
                request = request.replace(tmpStr, t24DataModel.requestDataArr[i]);
            }
            rd.close();
            return request;
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static String BuildRequestFromStringData(String header, String body) throws Exception {
        try {
            Gson gson = new Gson();
            String rootName = String.format(FORMART_REQ_ROOT_NAME, paramModel.endPointName);
            Object objHeader = gson.fromJson(header, Object.class);
            Object objBody = gson.fromJson(body, Object.class);
            HashMap<String, Object> map = new HashMap<String, Object>();
            HashMap<String, Object> mapAll = new HashMap<String, Object>();
            map.put("header", objHeader);
            map.put("bodyReq", objBody);
            mapAll.put(rootName, map);
            String result = gson.toJson(mapAll);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static HttpURLConnection CreateConnection(URL url, String method) throws Exception {
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Content-Type", CONTENT_TYPE_DEFAUL);
            connection.setRequestProperty("Accept-Charset", CHARSET_DEFAULT);
            // connection.setRequestProperty("Authorization", "Basic "+"");
            connection.setConnectTimeout(ESB_TIMEOUT);
            connection.setRequestMethod(method);
            connection.setDoOutput(true);
            return connection;
        } catch (IOException e) {
            e.printStackTrace();
            LogControl.WriteStackTrace(e);
            throw e;
        }
    }

    public static String GetResponseString(String request, boolean isFullResponse) throws Exception {
        try {
            LogControl.WriteLog("Request: " + request);
            HttpURLConnection conn = (HttpURLConnection) CreateConnection(url, method);
            String jsonResponse = "";
            OutputStream os = conn.getOutputStream();
            os.write(request.getBytes());
            os.flush();
            os.close();
            if (conn.getResponseCode() != 200)
                return "Http error code : " + conn.getResponseCode() + " - " + conn.getResponseMessage();
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String output;
            while ((output = br.readLine()) != null)
                jsonResponse = String.valueOf(jsonResponse) + output;       
            br.close();
            if(isFullResponse)
                return jsonResponse;
            
            JsonObject jResObj = JsonParser.parseString(jsonResponse).getAsJsonObject();
            String result = GetResponseResultByConfig(jResObj);
            
            LogControl.WriteLog("Response: " + jsonResponse);
            LogControl.WriteLog("Response Send To T24: " + result);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    public static String GetResponseResultByConfig(JsonObject resBody){
        try {
            List<String> resParam = ConfigReader.GetResponseConfContent(paramModel.endPointName);
            List<String> T24FormatResponse = new ArrayList<String>();
            for (String cItem : resParam) {
                try {
                    String item = CustomJsonHandle.GetJsonElementByMultiKey(resBody, cItem).getAsString();
                    T24FormatResponse.add(item);
                } catch (Exception e) {
                    T24FormatResponse.add("");
                    e.printStackTrace();
                }
            }
            return String.join("*", T24FormatResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String GetESBResponseStatus(String param, T24ParamModel paramModel)
            throws Exception {
        try {
            String jsonResponse = GetResponseString(param, true);
            JsonObject jObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
            String keyStatus = String.format(FORMAT_TEMP_RES_STATUS, paramModel.endPointName);
            String value = CustomJsonHandle.GetJsonElementByMultiKey(jObject, keyStatus).getAsString();
            return value;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public String GetESBResponseStatus(String jsonResponse) throws Exception {
        try {
            JsonObject jObject = JsonParser.parseString(jsonResponse).getAsJsonObject();
            String keyStatus = String.format(FORMAT_TEMP_RES_STATUS, paramModel.endPointName);
            String value = CustomJsonHandle.GetJsonElementByMultiKey(jObject, keyStatus).getAsString();
            return value;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static CommonInfoESB GetCommonInfoESB() throws Exception {
        CommonInfoESB common = new CommonInfoESB();
        common.serviceVersion = ConfigReader.GetJsonConfigByMultiKey("connection.esb.serviceVersion");
        common.messageId = UUID.randomUUID().toString();
        common.transactionId = UUID.randomUUID().toString();
        DateFormat dateFormat = new SimpleDateFormat(FORMAT_DATE_TIME);
        common.messageTimestamp = dateFormat.format(NOW_DATETIME);
        return common;
    }

    public static ESBRequestHeader GetESBRequestHeader(String esbConfig) throws Exception {
        ESBRequestHeader header = new ESBRequestHeader();
        header.common = GetCommonInfoESB();
        String hdDataSource = "connection";
        if(esbConfig != null)
        {
            hdDataSource = "endPoint."+paramModel.endPointName;
        }
        header.client = GetClientInfo(hdDataSource);
        return header;
    }

    public static ClientESB GetDefaultClientInfo() throws Exception {
        try {
            ClientESB esb = new ClientESB();
            esb.sourceAppID = ConfigReader.GetJsonConfigByMultiKey("connection.esb.sourceAppID");
            esb.targetAppIDs = ConfigReader.GetJsonConfigByMultiKey("connection.esb.targetAppIDs");
            esb.userDetail.userID = ConfigReader.GetJsonConfigByMultiKey("connection.esb.userID");
            esb.userDetail.userPassword = ConfigReader.GetJsonConfigByMultiKey("connection.esb.userPassword");
            return esb;
        } catch (Exception e) {
            throw e;
        }
    }

    public static ClientESB GetClientInfo(String sourceName) throws Exception {
        try {
            ClientESB esb = new ClientESB();
            esb.sourceAppID = ConfigReader.GetJsonConfigByMultiKey(String.format("%s.esb.sourceAppID", sourceName));
            
            esb.targetAppIDs = ConfigReader.GetJsonConfigByMultiKey(String.format("%s.esb.targetAppIDs", sourceName));
            esb.userDetail.userID = ConfigReader.GetJsonConfigByMultiKey(String.format("%s.esb.userID", sourceName));
            esb.userDetail.userPassword = ConfigReader.GetJsonConfigByMultiKey(String.format("%s.esb.userPassword", sourceName));
            return esb;
        } catch (Exception e) {
            throw e;
        }
    }

    public static String GetESBRequestHeader(ESBRequestHeader headerObj) {
        try {
            Gson gson = new Gson();
            String result = gson.toJson(headerObj);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static String GetESBRequestHeader(String request, String esbConfig) throws Exception {
        ESBRequestHeader headerESB;
        try {
            headerESB = GetESBRequestHeader(esbConfig);
            request = request.replace("[H1]", headerESB.common.serviceVersion);
            request = request.replace("[H2]", headerESB.common.messageId);
            request = request.replace("[H3]", headerESB.common.transactionId);
            request = request.replace("[H4]", headerESB.common.messageTimestamp);

            request = request.replace("[H5]", headerESB.client.sourceAppID);
            request = request.replace("[H6]", headerESB.client.targetAppIDs);
            request = request.replace("[H7]", headerESB.client.userDetail.userID);
            request = request.replace("[H8]", headerESB.client.userDetail.userPassword);            
            return request;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

}
