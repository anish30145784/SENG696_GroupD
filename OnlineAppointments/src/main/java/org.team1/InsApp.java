package org.team1;


import jade.wrapper.StaleProxyException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.team1.agent.JadeBootThread;


@SpringBootApplication
public class InsApp {

    public static void main(String[] args) throws StaleProxyException {
        try {
            new JadeBootThread().run();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        SpringApplication.run(InsApp.class, args);


    }


}

