package org.team1;


import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.team1.agent.AppointmentJadeAgent;
import org.team1.agent.JadeBootThread;


@SpringBootApplication
public class InsApp {

    public static void main(String[] args ) throws StaleProxyException {

        SpringApplication.run(InsApp.class, args);
        try {
            new JadeBootThread().run();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }


}

