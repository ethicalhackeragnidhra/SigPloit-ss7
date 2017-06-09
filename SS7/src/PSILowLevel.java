
/**
 * Created by gh0 on 12/26/16.
 */

/**
 * Created by gh0 on 12/26/16.
 */



import org.mobicents.protocols.ss7.m3ua.impl.parameter.ParameterFactoryImpl;
import org.mobicents.protocols.ss7.map.api.MAPDialogListener;
import org.mobicents.protocols.ss7.map.api.service.callhandling.MAPServiceCallHandlingListener;

import java.util.*;

public abstract class PSILowLevel implements MAPDialogListener, MAPServiceCallHandlingListener {

    // MTP Details
    protected int CLIENT_SPC ;
    protected int SERVER_SPC ; //PC of adjacent STP
    protected int NETWORK_INDICATOR ;  //International
    protected int SERVICE_INDICATOR = 3 ; // SCCP
    protected int SSN_Server=7 ; //VLR SSN
    protected int SSN_Client=6 ; //HLR SSN

    // M3UA details
    protected String CLIENT_IP ;
    protected int CLIENT_PORT ;

    protected String SERVER_IP ;
    protected int SERVER_PORT ;

    protected String CLIENT_ASSOCIATION_NAME = "clientAsscoiation";



    //Target Details
    String IMSI ;
    String VLR;

    //Attacker Details
    String HLR;

    protected final ParameterFactoryImpl factory = new ParameterFactoryImpl();

    protected PSILowLevel() {

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

            System.out.print("\033[34m[*]\033[0mSet Remote VLR GT: ");
            VLR  = user_input.next();


            while (true) {
                System.out.print("\033[34m[*]\033[0mSet Target's IMSI: ");
                IMSI = user_input.next();
                if (IMSI.length() == 15 || IMSI.length() == 16) {
                    break;
                }else{
                    System.out.println("\033[31m[-]\033[0mWrong Format: IMSI must be 15 or 16 digits, please refer to the country's format");
                }
            }

            System.out.print("\033[34m[*]\033[0mSet your HLR GT: ");
            HLR = user_input.next();

            Thread.sleep(1000);

            System.out.println("\033[34m[*]\033[0mStack components are set...");
            System.out.println("\033[34m[*]\033[0mInitializing the Stack...");
            Thread.sleep(1000);


        } catch (Exception ex) {
            System.out.println("\033[31m[-]\033[0mError: " + ex.getMessage());

        }


    }
}


