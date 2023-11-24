package Model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class CommonInfoESB {
    public String serviceVersion = "1";
    public String messageId = UUID.randomUUID().toString();
    public String transactionId = UUID.randomUUID().toString();
    public String messageTimestamp = GetNowTimestamp();

    private String GetNowTimestamp(){
        Date now = Calendar.getInstance().getTime();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.format(now);
    }
}
