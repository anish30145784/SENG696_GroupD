package org.team1.utils;

public class Constrants {

    public static final String EMAIl_TEMPLATE_FEEDBACK = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
            "<style>\n" +
            "body {font-family: Arial, Helvetica, sans-serif;}\n" +
            "* {box-sizing: border-box;}\n" +
            "\n" +
            "input[type=text], select, textarea {\n" +
            "  width: 100%;\n" +
            "  padding: 12px;\n" +
            "  border: 1px solid #ccc;\n" +
            "  border-radius: 4px;\n" +
            "  box-sizing: border-box;\n" +
            "  margin-top: 6px;\n" +
            "  margin-bottom: 16px;\n" +
            "  resize: vertical;\n" +
            "}\n" +
            "\n" +
            "input[type=submit] {\n" +
            "  background-color: #04AA6D;\n" +
            "  color: white;\n" +
            "  padding: 12px 20px;\n" +
            "  border: none;\n" +
            "  border-radius: 4px;\n" +
            "  cursor: pointer;\n" +
            "}\n" +
            "\n" +
            "input[type=submit]:hover {\n" +
            "  background-color: #45a049;\n" +
            "}\n" +
            "\n" +
            ".container {\n" +
            "  border-radius: 5px;\n" +
            "  background-color: #f2f2f2;\n" +
            "  padding: 20px;\n" +
            "}\n" +
            "</style>\n" +
            "</head>\n" +
            "<body>\n" +
            "\n" +
            "<h3>feedback Form</h3>\n" +
            "\n" +
            "<div class=\"container\">\n" +
            "  <form action=\"http://localhost:8080/feedback\"  method=\"post\" >\n" +
            "    <label for=\"fname\">First Name</label>\n" +
            "    <input type=\"text\" id=\"fname\" name=\"firstName\" placeholder=\"Your name..\">\n" +
            "\n" +
            "    <label for=\"lname\">Last Name</label>\n" +
            "    <input type=\"text\" id=\"lname\" name=\"lastName\" placeholder=\"Your last name..\">\n" +
            "    \n" +
            "    <label for=\"lname\">email</label>\n" +
            "    <input type=\"text\" id=\"email\" name=\"email\" placeholder=\"Your email..\">\n" +
            "\n" +
            "    <label for=\"feedback\">feedback</label>\n" +
            "    <textarea id=\"feedback\" name=\"feedback\" placeholder=\"Write something..\" style=\"height:200px\"></textarea>\n" +
            "\n" +
            "    <input type=\"submit\" value=\"Submit\">\n" +
            "  </form>\n" +
            "</div>\n" +
            "\n" +
            "</body>\n" +
            "</html>\n";
}
