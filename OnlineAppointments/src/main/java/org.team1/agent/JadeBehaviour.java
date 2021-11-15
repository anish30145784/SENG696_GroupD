package org.team1.agent;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;

public class JadeBehaviour extends CyclicBehaviour {


    private enum State {
        EMAIl, SMS, INVITE
    }

    private final Agent agent;
    private final String otherAgentName;
    private State state;

    public JadeBehaviour(Agent agent, String otherAgentName) {
        this.agent = agent;
        this.otherAgentName = otherAgentName;
        this.state = State.EMAIl;
    }
    @Override
    public void action() {
        System.out.println("njcnljqnlsqnlcnql");
        done();
    }


}
