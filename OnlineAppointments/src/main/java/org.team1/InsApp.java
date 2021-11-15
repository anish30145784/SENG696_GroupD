package org.team1;


import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.team1.agent.JadeAgent;
import org.team1.agent.JadeBootThread;

import java.util.Properties;


@SpringBootApplication
public class InsApp {

    public static void main(String[] args ) throws StaleProxyException {
//        startMainContainer();
        try {
            new JadeBootThread().run();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        SpringApplication.run(InsApp.class, args);

    }

    private static void startMainContainer() throws StaleProxyException {
        Runtime runtime = jade.core.Runtime.instance();
        runtime.setCloseVM(true);

        Profile profile = new ProfileImpl("192.168.2.9", 12344, null);
        profile.setParameter(Profile.GUI, "true");

        AgentContainer mainContainer = runtime.createMainContainer(profile);

        AgentController agentOne = mainContainer
                .createNewAgent("AgentOne", JadeAgent.class.getName(), new Object[0]);
        agentOne.start();


    }

}

