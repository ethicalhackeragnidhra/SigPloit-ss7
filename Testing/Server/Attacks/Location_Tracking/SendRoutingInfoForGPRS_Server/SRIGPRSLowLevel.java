/**
 * Created by gh0 on 6/23/17.
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
import org.mobicents.protocols.ss7.map.api.service.pdpContextActivation.MAPServicePdpContextActivation;




public abstract class SRIGPRSLowLevel implements MAPDialogListener, MAPServicePdpContextActivation {

    private static final Logger logger = Logger.getLogger("map.test");

    protected static final String LOG_FILE_NAME = "log.file.name";
    protected static String logFileName = "maplog.txt";

    // MTP Details
    protected final int CLIENT_SPC = 1;
    protected final int SERVER_SPC = 2; //PC of adjacent STP
    protected final int NETWORK_INDICATOR = 0;  //International
    protected final int SERVICE_INDICATOR = 3; // SCCP
    protected final int SSN_Client = 150; //GGSN SSN
    protected final int SSN_Server = 6; //HLR SSN

    // M3UA details
    // protected final String CLIENT_IP = "172.31.96.40";
    protected final String CLIENT_IP = "192.168.56.102";
    protected final int CLIENT_PORT = 2905;

    // protected final String SERVER_IP = "172.31.96.41";
    protected final String SERVER_IP = "192.168.56.101";
    protected final int SERVER_PORT = 2906;

    protected final int ROUTING_CONTEXT = 100;

    protected final String SERVER_ASSOCIATION_NAME = "serverAsscoiation";
    protected final String CLIENT_ASSOCIATION_NAME = "clientAsscoiation";

    protected final String SERVER_NAME = "HLR02";



    protected final ParameterFactoryImpl factory = new ParameterFactoryImpl();

    protected SRIGPRSLowLevel() {
        init();
    }

    public void init() {
        try {


            InputStream inStreamLog4j = SRIGPRSLowLevel.class.getResourceAsStream("log4j.properties");

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
