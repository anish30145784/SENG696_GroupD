package org.team1.agent;

import jade.core.AID;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import org.team1.client.BaseRequest;
import org.team1.client.BaseResponse;
import org.team1.client.EService;
import org.team1.client.EServiceImplService;
import org.team1.models.Appointment;
import org.team1.models.MeetingData;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

public class VideoLinkAgent extends EnhancedAgent {
    public Set<AID> smsOremailA = new HashSet<>();

    public void setup() {

        register("video");
        System.out.println("Inside Video Link Agent !");

        addBehaviour(new TickerBehaviour(this, 4000) {

            protected void onTick() {
                try {


                    ACLMessage msg = blockingReceive();
                    Appointment appointment = (Appointment) msg.getContentObject();

                    URL url = new URL("http://" + InetAddress.getLocalHost().getHostAddress() + ":7780/ws/eservice?wsdl");
                    EServiceImplService eServiceImplService = new EServiceImplService(url);
                    EService eService = eServiceImplService.getEServiceImplPort();
                    BaseRequest baseRequest = new BaseRequest();
                    baseRequest.setUsername(appointment.getClient().getFirstName());
                    baseRequest.setSurname(appointment.getClient().getLastName());
                    BaseResponse baseResponse = eService.getZoomLink(baseRequest);
                    System.out.println("Meeting Id : " + baseResponse.getZoomLinkList().get(0).getMeetingId());
                    System.out.println("Meeting Url : " + baseResponse.getZoomLinkList().get(0).getUrl());
                    System.out.println("Meeting Password : " + baseResponse.getZoomLinkList().get(0).getPassword());

                    MeetingData meetingData = new MeetingData();
                    meetingData.setMeetingId(baseResponse.getZoomLinkList().get(0).getMeetingId());
                    meetingData.setPassword(baseResponse.getZoomLinkList().get(0).getPassword());
                    meetingData.setUrl(baseResponse.getZoomLinkList().get(0).getUrl());

                    System.out.println("Sending Meeting details to " + msg.getConversationId() + " Agent !");
                    smsOremailA = searchForService(msg.getConversationId());
                    for (AID agentEmail : smsOremailA) {
                        ACLMessage aclsmsOrEmailMsg = new ACLMessage(ACLMessage.INFORM);
                        //aclUpdEmailMsg.addReceiver(new AID("EmailAgent", AID.ISLOCALNAME));
                        aclsmsOrEmailMsg.setContentObject((Serializable) meetingData);
                        aclsmsOrEmailMsg.setConversationId("VideoLink");
                        aclsmsOrEmailMsg.addReceiver(agentEmail);
                        send(aclsmsOrEmailMsg);
                    }

                } catch (UnreadableException | IOException | NoClassDefFoundError e) {
                    e.printStackTrace();
                }
            }
        });
    }
}



