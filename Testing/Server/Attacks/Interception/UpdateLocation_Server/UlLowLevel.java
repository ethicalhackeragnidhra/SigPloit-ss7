
/**
 * Created by gh0 on 9/8/16.
 */
/**
 *
 */

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.SimpleLayout;
import org.mobicents.protocols.ss7.map.api.MAPDialog;
import org.mobicents.protocols.ss7.map.api.MAPDialogListener;
import org.mobicents.protocols.ss7.m3ua.impl.parameter.ParameterFactoryImpl;
import org.mobicents.protocols.ss7.map.api.primitives.AddressString;
import org.mobicents.protocols.ss7.map.api.service.mobility.MAPDialogMobility;
import org.mobicents.protocols.ss7.map.api.service.mobility.MAPServiceMobilityListener;
import org.mobicents.protocols.ss7.map.api.service.sms.MAPDialogSms;
import org.mobicents.protocols.ss7.map.api.service.sms.MAPServiceSmsListener;


public abstract class UlLowLevel implements MAPDialogListener, MAPServiceMobilityListener,MAPServiceSmsListener{

    private static final Logger logger = Logger.getLogger("map.test");

    protected static final String LOG_FILE_NAME = "log.file.name";
    protected static String logFileName = "maplog.txt";

    // MTP Details
    protected final int CLIENT_SPC = 1;
    protected final int SERVER_SPC = 2; //PC of adjacent STP
    protected final int LOCAL_PC = 3; // local node PC to be included in the sccp local routing
    protected final int NETWORK_INDICATOR = 0;  //International
    protected final int SERVICE_INDICATOR = 3; // SCCP
    protected final int SSN_Client = 7; //VLR SSN
    protected final int SSN_Server = 6; //HLR SSN
    protected final int SSN_MSC = 8; //MSC SSN

    // M3UA details
    protected final String CLIENT_IP = "192.168.56.101";
    protected final int CLIENT_PORT = 2905;


    protected final String SERVER_IP = "192.168.56.102";
    protected final int SERVER_PORT = 2906;


    protected final String SERVER_ASSOCIATION_NAME = "serverAsscoiation";


    protected final String SERVER_NAME = "HLR01";



    protected final ParameterFactoryImpl factory = new ParameterFactoryImpl();

    protected UlLowLevel() {
        init();
    }

    public void init() {
        try {
            Properties tckProperties = new Properties();

            InputStream inStreamLog4j = UlLowLevel.class.getResourceAsStream("log4j.properties");

            System.out.println("Input Stream = " + inStreamLog4j);

            Properties propertiesLog4j = new Properties();
            try {
                propertiesLog4j.load(inStreamLog4j);
                PropertyConfigurator.configure(propertiesLog4j);
            } catch (IOException e) {
                e.printStackTrace();
                BasicConfigurator.configure();
            }

            logger.debug("log4j configured");

            String lf = System.getProperties().getProperty(LOG_FILE_NAME);
            if (lf != null) {
                logFileName = lf;
            }

            // If already created a print writer then just use it.
            try {
                logger.addAppender(new FileAppender(new SimpleLayout(), logFileName));
            } catch (FileNotFoundException fnfe) {

            }
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException(ex);
        }

    }

    public abstract void onDialogRequestEricsson(MAPDialog mapDialog, AddressString addressString, AddressString addressString1, AddressString addressString2, AddressString addressString3);
}
