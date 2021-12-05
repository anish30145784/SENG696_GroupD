package org.team1.utils;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.stereotype.Component;

@Component
public class SmsUtil {

    public static final String ACCOUNT_SID = "ACd7d2f411ca83a29bbc446adca095a516";
    public static final String AUTH_TOKEN = "5d0ec4f295834c6acc2b66743432c389";
    public static final String TRIAL_NUMBER = "+16672205255";

    public static void main(String body, Long to) throws Exception {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        String toNumber = "+1" + to.toString();
        Message message = Message.creator(
                new PhoneNumber(toNumber), new PhoneNumber(TRIAL_NUMBER), body).create();

        System.out.println(message.getSid());
    }
}
