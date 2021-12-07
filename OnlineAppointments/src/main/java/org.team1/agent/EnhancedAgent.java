package org.team1.agent;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.SearchConstraints;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

import java.util.HashSet;
import java.util.Set;

public class EnhancedAgent extends Agent {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    protected Set<AID> searchForService(String serviceName) {
        System.out.println("Inside Search !");
        Set<AID> foundAgents = new HashSet<>();
        DFAgentDescription dfd = new DFAgentDescription();
        ServiceDescription sd = new ServiceDescription();
        sd.setType(serviceName.toLowerCase());
        dfd.addServices(sd);

        SearchConstraints sc = new SearchConstraints();
        sc.setMaxResults(Long.valueOf(-1));

        try {
            DFAgentDescription[] results = DFService.search(this, dfd, sc);
            for (DFAgentDescription result : results) {
                foundAgents.add(result.getName());
            }
            return foundAgents;
        } catch (FIPAException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    protected void takeDown() {
        try {
            DFService.deregister(this);
        } catch (Exception ex) {
        }
    }

    protected void register(String serviceName) {
        System.out.println("Inside Registration : " + serviceName);
        DFAgentDescription dfd = new DFAgentDescription();
        AID aid = getAID();
        dfd.setName(aid);
        ServiceDescription sd = new ServiceDescription();
        sd.setName(getLocalName());
        sd.setType(serviceName.toLowerCase());
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        } catch (FIPAException ex) {
            ex.printStackTrace();
        }
        System.out.println("Agents registered so far --> \n");
        showAllAgents();
    }

    public void showAllAgents() {
        DFAgentDescription dfd1 = new DFAgentDescription();
        DFAgentDescription[] result;
        try {
            result = DFService.search(this, dfd1);
            if (result.length == 6) {
                System.out.println("Search returns: " + result.length + " elements");
                for (int i = 0; i < result.length; i++) {
                    System.out.println(" " + result[i].getName() + " --> " + result[i].getName().getLocalName());
                }


            }

        } catch (FIPAException e) {
            e.printStackTrace();
        }
    }

}
