/**
 * Created by root on 10/28/16.
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
import org.mobicents.protocols.ss7.map.api.dialog.*;
import org.mobicents.protocols.ss7.map.api.errors.MAPErrorMessage;
import org.mobicents.protocols.ss7.map.api.primitives.*;
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
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberInformation.*;
import org.mobicents.protocols.ss7.map.api.service.mobility.subscriberManagement.*;
import org.mobicents.protocols.ss7.sccp.OriginationType;
import org.mobicents.protocols.ss7.sccp.RuleType;
import org.mobicents.protocols.ss7.sccp.SccpProvider;
import org.mobicents.protocols.ss7.sccp.SccpResource;
import org.mobicents.protocols.ss7.sccp.impl.SccpStackImpl;
import org.mobicents.protocols.ss7.sccp.parameter.GlobalTitle0100;
import org.mobicents.protocols.ss7.sccp.parameter.SccpAddress;
import org.mobicents.protocols.ss7.tcap.TCAPStackImpl;
import org.mobicents.protocols.ss7.tcap.api.TCAPStack;
import org.mobicents.protocols.ss7.tcap.asn.ApplicationContextName;
import org.mobicents.protocols.ss7.tcap.asn.comp.Problem;
import sun.misc.VM;

//import org.mobicents.protocols.ss7.map.api.dialog.MAPProviderError;


public class ProvideSubscriberInformationResp extends PSILowLevelServer {

    private static Logger logger = Logger.getLogger(ProvideSubscriberInformationResp.class);

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


    public ProvideSubscriberInformationResp() {
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


        //used GlobalTitleIndicator(GTI) class for createGlobalTitle is GlobalTitle0100(used for MAP)
        GlobalTitle0100 calling = this.sccpProvider.getParameterFactory().createGlobalTitle
                ("*", 0, org.mobicents.protocols.ss7.indicator.NumberingPlan.ISDN_TELEPHONY, null,
                        NatureOfAddress.INTERNATIONAL);
        GlobalTitle0100 called = this.sccpProvider.getParameterFactory().createGlobalTitle
                ("201179008244", 0, org.mobicents.protocols.ss7.indicator.NumberingPlan.ISDN_TELEPHONY, null,
                        NatureOfAddress.INTERNATIONAL);

       /* GlobalTitle0100 localVlr = this.sccpProvider.getParameterFactory().createGlobalTitle
                ("96599657765", 0, org.mobicents.protocols.ss7.indicator.NumberingPlan.ISDN_TELEPHONY, null,
                        NatureOfAddress.INTERNATIONAL);*/


        this.sccpStack.getRouter().addRoutingAddress
                (1, this.sccpProvider.getParameterFactory().createSccpAddress
                        (RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, called, SERVER_SPC, SSN_Server));

        this.sccpStack.getRouter().addRoutingAddress
                (2, this.sccpProvider.getParameterFactory().createSccpAddress(
                        RoutingIndicator.ROUTING_BASED_ON_DPC_AND_SSN, calling, 1, SSN_Client));
        /*this.sccpStack.getRouter().addRoutingAddress
                (3, this.sccpProvider.getParameterFactory().createSccpAddress
                        (RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, localVlr, SERVER_SPC, SSN_Server));*/



        SccpAddress patternLocal = this.sccpProvider.getParameterFactory().createSccpAddress(
                RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, calling, 1,SSN_Client );
        SccpAddress patternRemote = this.sccpProvider.getParameterFactory().createSccpAddress
                (RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, called, SERVER_SPC,SSN_Server);
        /*SccpAddress patternHLR = this.sccpProvider.getParameterFactory().createSccpAddress
                (RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, localVlr, SERVER_SPC,SSN_Server);*/


        String maskLocal = "K";
        String maskRemote = "R"; //change mask to match on any digits after (2010) i.e 2010*

        //translate local GT to its POC+SSN (local rule)GTT
        this.sccpStack.getRouter().addRule
                (1, RuleType.SOLITARY, null,OriginationType.LOCAL, patternLocal, maskLocal, 2, -1, null, 0);
        this.sccpStack.getRouter().addRule
                (2, RuleType.SOLITARY, null, OriginationType.REMOTE, patternRemote, maskRemote, 1, -1, null, 0);
        /*this.sccpStack.getRouter().addRule
                (3, RuleType.SOLITARY, null, OriginationType.REMOTE, patternHLR, maskRemote, 3, -1, null, 0);*/




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

        this.mapStack = new MAPStackImpl("MAP-VLR", this.tcapStack.getProvider());
        this.mapProvider = this.mapStack.getMAPProvider();

        this.mapProvider.addMAPDialogListener(this);
        this.mapProvider.getMAPServiceMobility().addMAPServiceListener(this);

        this.mapProvider.getMAPServiceMobility().acivate();


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
        System.out.println("***          Subscriber Information       ***");
        System.out.println("*********************************************");
        IpChannelType ipChannelType = IpChannelType.SCTP;

        if (args.length >= 1 && args[0].toLowerCase().equals("tcp")) {
            ipChannelType = IpChannelType.TCP;
        }

        final ProvideSubscriberInformationResp victim = new ProvideSubscriberInformationResp();

        logger.setLevel(org.apache.log4j.Level.DEBUG);

        try {
            victim.initializeStack(ipChannelType);

            // Lets pause for 20 seconds so stacks are initialized properly
            Thread.sleep(20000);





        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onProvideSubscriberInfoRequest(ProvideSubscriberInfoRequest provideSubscriberInfoRequest) {
        try{

            long invokeId = provideSubscriberInfoRequest.getInvokeId();

            //Creating Subscriber Information
            //cs domain info
            double Lat = 29.13;
            double Long = 31.56;
            double Uncertain = 10.123;

            //more accurate info from ps domain
            double Lat_ps = 28.113;
            double Long_ps = 32.568;
            double Uncertain_ps = 2.12;

            int mcc = 602;
            int mnc = 03;
            int LAC = 1234;
            int CI = 5678;

            GeographicalInformation geographicalInformation = this.mapProvider.getMAPParameterFactory()
                    .createGeographicalInformation(Lat,Long,Uncertain);

            CellGlobalIdOrServiceAreaIdFixedLength cellGlobalIdOrServiceAreaIdFixedLength =
                    this.mapProvider.getMAPParameterFactory().createCellGlobalIdOrServiceAreaIdFixedLength(mcc,mnc,LAC,CI);
            CellGlobalIdOrServiceAreaIdOrLAI cellGlobalIdOrServiceAreaIdOrLAI = this.mapProvider
                    .getMAPParameterFactory().createCellGlobalIdOrServiceAreaIdOrLAI(cellGlobalIdOrServiceAreaIdFixedLength);

            ISDNAddressString Vmsc = this.mapProvider.getMAPParameterFactory()
                    .createISDNAddressString(AddressNature.international_number,NumberingPlan.ISDN,"2015512458123");

            LocationInformation locationInformation = this.mapProvider.getMAPParameterFactory()
                    .createLocationInformation(30,geographicalInformation,Vmsc,null,cellGlobalIdOrServiceAreaIdOrLAI
                            ,null,null,null,null,false,false,null,null);

            IMEI imei = this.mapProvider.getMAPParameterFactory().createIMEI("35209900176148");

            SubscriberState subscriberState = this.mapProvider.getMAPParameterFactory()
                    .createSubscriberState(SubscriberStateChoice.assumedIdle,null);

            ISDNAddressString sgsn = this.mapProvider.getMAPParameterFactory()
                    .createISDNAddressString(AddressNature.international_number,NumberingPlan.ISDN,"20155555555");

            GeographicalInformation geographicalInformation_ps = this.mapProvider.getMAPParameterFactory()
                    .createGeographicalInformation(Lat_ps,Long_ps,Uncertain_ps);

            RAIdentity raIdentity = this.mapProvider.getMAPParameterFactory().createRAIdentity(new byte[] {(byte) 0x06,(byte)0x0020, (byte) 0x3, (byte) 0x40,
                    (byte)0x2d, (byte) 0x21});

            LocationInformationGPRS locationInformationGPRS = this.mapProvider.getMAPParameterFactory().
                    createLocationInformationGPRS(null,raIdentity,geographicalInformation_ps,sgsn,null,null,false,null,
                            true,30);

            SubscriberInfo subscriberInfo = this.mapProvider.getMAPParameterFactory()
                    .createSubscriberInfo(locationInformation,subscriberState,null,locationInformationGPRS,null,imei,null,
                            null,null);

            MAPDialogMobility mapDialogMobility = provideSubscriberInfoRequest.getMAPDialog();

            mapDialogMobility.setUserObject(invokeId);
            mapDialogMobility.addProvideSubscriberInfoResponse(invokeId,subscriberInfo,null);

            mapDialogMobility.send();

            logger.info("Subscriber Information Sent.....");

        } catch (MAPException e){
            e.printStackTrace();
        }


    }






    @Override
    public void onUpdateLocationRequest(UpdateLocationRequest updateLocationRequest) {


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

    /*@Override
    public void onAnyTimeSubscriptionInterrogationRequest(AnyTimeSubscriptionInterrogationRequest request) {

    }*/

    /*@Override
    public void onAnyTimeSubscriptionInterrogationResponse(AnyTimeSubscriptionInterrogationResponse response) {

    }*/


    @Override
    public void onProvideSubscriberInfoResponse(ProvideSubscriberInfoResponse provideSubscriberInfoResponse) {

    }

    @Override
    public void onInsertSubscriberDataRequest(InsertSubscriberDataRequest insertSubscriberDataRequest) {

    }

    @Override
    public void onInsertSubscriberDataResponse(InsertSubscriberDataResponse insertSubscriberDataResponse) {

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
}