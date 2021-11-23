package org.team1.utils;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.springframework.stereotype.Component;

@Component
public class SmsUtil {

    public static final String ACCOUNT_SID = "AC5f47f1500a21b11a7cb8c0b1b6ddc1c7";
    public static final String AUTH_TOKEN = "0307b475beb70c6159bd5b48b1eccfc3";


//    static {
//        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
//    }
    public static void main() throws Exception {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        Message message = Message.creator(
                        new com.twilio.type.PhoneNumber("+919205058292"),
                        "MGd5dc9fe0fc424fc51bd7844357eb6973",
                        "your appointment has been scheduled")
                .create();

        System.out.println(message.getSid());
    }
}
