
/**
 * Created by gh0 on 12/26/16.
 */


import org.mobicents.protocols.ss7.m3ua.impl.parameter.ParameterFactoryImpl;
import org.mobicents.protocols.ss7.map.api.MAPDialogListener;
import org.mobicents.protocols.ss7.map.api.service.callhandling.MAPServiceCallHandlingListener;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public abstract class Stack implements MAPDialogListener, MAPServiceCallHandlingListener {

  //  private static final Logger logger = Logger.getLogger("map.test");


 //   protected static final String LOG_FILE_NAME = "maplog.log";
 //   protected static String logFileName = "maplog.txt";

    // MTP Details

    protected int CLIENT_SPC ;
    protected int SERVER_SPC ; //PC of adjacent STP
    protected int NETWORK_INDICATOR ;  //International
    protected int SERVICE_INDICATOR = 3 ; // SCCP
    protected int SSN_Server=6 ; //HLR SSN
    protected int SSN_Client = 8; //MSC SSN

    // M3UA details
    protected String CLIENT_IP ;
    protected int CLIENT_PORT ;

    protected String SERVER_IP ;
    protected int SERVER_PORT ;


    protected String SERVER_ASSOCIATION_NAME = "serverAsscoiation";
    protected String CLIENT_ASSOCIATION_NAME = "clientAsscoiation";

    protected String SERVER_NAME = "HLR01";

    //Target Details
    String MSISDN ;

    //Attacker Details
    String MSC;

    protected final ParameterFactoryImpl factory = new ParameterFactoryImpl();

    protected Stack() {

        init();
    }

    public void init() {
        try {

            Scanner user_input = new Scanner(System.in);


            System.out.print("\033[34m[*]\033[0mSet Client PC: ");
            CLIENT_SPC = user_input.nextInt();
            System.out.print("\033[34m[*]\033[0mSet Peer PC: ");
            SERVER_SPC = user_input.nextInt();


            System.out.print("\033[34m[*]\033[0mSet Client IP: ");
            CLIENT_IP = user_input.next();
            System.out.print("\033[34m[*]\033[0mSet Client Port: ");
            CLIENT_PORT = user_input.nextInt();
            System.out.print("\033[34m[*]\033[0mSet Peer IP: ");
            SERVER_IP = user_input.next();
            System.out.print("\033[34m[*]\033[0mSet Peer Port: ");
            SERVER_PORT = user_input.nextInt();


            System.out.print("\033[34m[*]\033[0mSet Network Indicator [0] International [1] National: ");
            NETWORK_INDICATOR = user_input.nextInt();

            System.out.print("\033[34m[*]\033[0mEnter Target's MSISDN: ");
            MSISDN  = user_input.next();


            System.out.print("\033[34m[*]\033[0mEnter your MSC GT : ");
            MSC = user_input.next();

            Thread.sleep(1000);

            System.out.println("\033[32m[*]\033[0mStack components are set...");
            System.out.println("\033[32m[*]\033[0mInitializing the Stack...");
            Thread.sleep(1000);


        } catch (Exception ex) {
            System.out.println("\033[31m[-]\033[0mError: "+ex);

        }


    }
}


