package org.team1.agent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

//import static org.team1.InsApp.increaseCounter;

public class JadeBootThread extends Thread {


    private final String jadeBoot_CLASS_NAME = "jade.Boot";
    private final String MAIN_METHOD_NAME = "main";
    //add the <agent-local-name>:<fully-qualified-agent-class> name here;
// you can add more than one by semicolon separated values.
    // private final String ACTOR_NAMES_args = "AppointmentAgent:org.team1.agent.AppointmentJadeAgent;EmailAgent:org.team1.agent.EmailAgent;SmsAgent:org.team1.agent.SmsAgent;CompletionAgent:org.team1.agent.CompletionAgent;PdfAgent:org.team1.agent.PdfAgent;ClinicAgent:org.team1.agent.ClinicAgent";
    private final String ACTOR_NAMES_args = "AppointmentAgent:org.team1.agent.AppointmentAgent;EmailAgent:org.team1.agent.EmailAgent;SmsAgent:org.team1.agent.SmsAgent;FeedbackAgent:org.team1.agent.FeedbackAgent;PdfAgent:org.team1.agent.PdfAgent;ClinicAgent:org.team1.agent.ClinicAgent";
    private final String GUI_args = "-gui";
    private final Class<?> secondClass;
    private final Method main;
    private final String[] params;

    public JadeBootThread() throws ClassNotFoundException, SecurityException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        secondClass = Class.forName(jadeBoot_CLASS_NAME);
        main = secondClass.getMethod(MAIN_METHOD_NAME, String[].class);
        params = new String[]{GUI_args, ACTOR_NAMES_args};
        System.out.println("Inside BootThread Constructor !");
        // increaseCounter();

    }

    @Override
    public void run() {
        try {


//            Profile p1 = new ProfileImpl(true);
//            p1.setParameter(ProfileImpl.PLATFORM_ID, "platform"+1); // ID range from 1
//
//            p1.setParameter(Profile.LOCAL_PORT, Integer.toString(1212)); // available
//
//            Runtime.instance().setCloseVM(true);
//            ContainerController mc = Runtime.instance().createMainContainer(p1);
            System.out.println("inside Boot thread !");
            //  if (InsApp.counter == 1) {
            main.invoke("", new Object[]{params});
            //   }


        } catch (IllegalArgumentException ex) {
            ex.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

}
