package org.team1;


import jade.wrapper.StaleProxyException;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.team1.agent.JadeBootThread;

import java.lang.reflect.InvocationTargetException;


@SpringBootApplication
public class InsApp {

    // public static int counter = 0;

    // public static void increaseCounter() {
    //    counter++;
    // }


    public static void main(String[] args) throws StaleProxyException {

        try {
            //System.out.println("Start Counter : " + counter++);
            System.out.println("Inside Main method of insApp ... ");
            new JadeBootThread().run();
            SpringApplication.run(InsApp.class, args);
        } catch (ClassNotFoundException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }


    }


}

