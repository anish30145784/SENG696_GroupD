package org.team1.utils;

import org.springframework.stereotype.Component;

import java.util.Iterator;
import java.util.Map;

@Component
public class CommonUtils {

    public static String replaceText(String text, Map<String, String> textToAndWithReplace) throws NullPointerException {
        text = (text != null ? text : "");
        Iterator<String> itr = textToAndWithReplace.keySet().iterator();
        while (itr.hasNext()) {
            String regex = itr.next();
            if ((textToAndWithReplace.get(regex) != null) && (textToAndWithReplace.get(regex).contains("$"))) {
                String changeValue = textToAndWithReplace.get(regex);
                regex = regex.replaceAll("\\\\", "");
                text = text.replace(regex, changeValue);
            } else {
                text = text.replaceAll(regex, textToAndWithReplace.get(regex));
            }
        }
        return text;
    }
}
