import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LogControl 
{
    public final static String LOG_FILE_PATH = "java/logs/";
    public static final String FORMAT_DATE_TIME = "yyyy-MM-dd HH:mm:ss";
    public static final String FN_DATE_FORMAT = "yyyyMMdd";
    static DateFormat contentDateFormat = new SimpleDateFormat(FORMAT_DATE_TIME);
    static DateFormat fileNameDataFormat = new SimpleDateFormat(FN_DATE_FORMAT);
    static Date dNow = Calendar.getInstance().getTime();    
    static String dateContent = contentDateFormat.format(dNow);
    static String dateFilename = fileNameDataFormat.format(dNow);
    static String logFilePath = LOG_FILE_PATH+"log_"+dateFilename+".log" ;
    static String errorFilePath = LOG_FILE_PATH+"error_"+dateFilename+".log" ;
    private static Logger logger = Logger.getLogger(LogControl.class.getName());

    public static synchronized void WriteLog(String message)
    {
        try 
        {
            FileHandler fHandler;
            logger.setLevel(Level.ALL);
            logger.setUseParentHandlers(true);
            fHandler = new FileHandler(logFilePath, 0, 1, true);        
            SimpleFormatter formater = new SimpleFormatter();
            fHandler.setFormatter(formater);
            logger.addHandler(fHandler);
            message = "\n"+dateContent + ": " + message;
            logger.info(message);
            fHandler.close();
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    public static synchronized void WriteStackTrace(Exception ex)
    {
        try (PrintWriter pw = new PrintWriter(new FileOutputStream(new File(logFilePath), true))) 
        {            
            pw.write("\n"+dateContent+"  ");
            ex.printStackTrace(pw);
            pw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }        
    }
    
}
