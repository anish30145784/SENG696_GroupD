package org.team1;


import jade.wrapper.StaleProxyException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.team1.agent.JadeBootThread;

import java.lang.reflect.InvocationTargetException;


@SpringBootApplication
public class InsApp {

    public static void main(String[] args) throws StaleProxyException {
        try {
            //System.out.println("Start Counter : " + counter++);
            new JadeBootThread().run();
            SpringApplication.run(InsApp.class, args);
        } catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }


    }


}

