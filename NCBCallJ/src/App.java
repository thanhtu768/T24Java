public class App {
    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");
        char c = (char)65;
        System.out.println(c);
        CallAPIController flexCashController = new CallAPIController();
        String data = "getFlexTransInfoById^tult-FLEX-TEST9443C14";
        String data2 = "nopRutTienNHNN^ID.FLEX1141*USER1*USER2*VND12501000100011*VND*50000000*20231007*500000000224*CN Ha noi Thuc hien rut tien mat tu NHNN Thuc hien rut tien mat tu NHNN*Thuc hien rut tien mat tu NHNN Thuc hien rut tien mat tu NHNN Thuc hien rut tien mat tu NHNN*9001*VN0010001";
        String data3 = "getFlexTransInfoById^{\"getFlexTransInfoByIdReq\": {\"header\": {\"common\": {\"serviceVersion\": \"1\",\"messageId\": \"{{$guid}}\",\"transactionId\": \"{{$guid}}\",\"messageTimestamp\": \"2022-12-14T11:29:04.464+07:00\"},\"client\": {\"sourceAppID\": \"MB\",\"targetAppIDs\": \"T24\",\"userDetail\": {\"userID\": \"MB\",\"userPassword\": \"RUJBTksxMjM=\"}}},\"bodyReq\": {\"functionCode\": \"FLEXCASH-GETFLEXTRANSINFOBYID-SOAP-T24\",\"columnName\": \"ID\",\"criteriaValue\": \"FLEXID123\",\"operand\": \"EQ\"}}}";
        String data4 = "getFlexTransInfoById^{\"functionCode\": \"FLEXCASH-GETFLEXTRANSINFOBYID-SOAP-T24\",\"columnName\": \"ID\",\"criteriaValue\": \"FLEXID123\",\"operand\": \"EQ\"}";
        String data5 = "getFlexTransInfoById^{\"functionCode\": \"FLEXCASH-GETFLEXTRANSINFOBYID-SOAP-T24\",\"columnName\": \"ID\",\"criteriaValue\": \"tult-FLEX-TEST9443C14\",\"operand\": \"EQ\"}";
        //String res = flexCashController.CallESBRestAPIWithoutTemplate(data4);
        //String res = FlexCashController.CallRestAPIByFullJsonData(data3);
        String res = CallAPIController.CallESBRestAPI(data2);
        System.out.println("Ressponse: "+res);
    }
}
 