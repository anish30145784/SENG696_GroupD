package org.team1.utils;

public class Constrants {

    public static final String EMAIl_TEMPLATE_FEEDBACK = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "<head>\n" +
            "  <script src=\"https://code.jquery.com/jquery-3.3.1.min.js\"\n" +
            "    integrity=\"sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8=\" crossorigin=\"anonymous\"></script>\n" +
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
            "    <label for=\"fname\">doctor Name</label>\n" +
            "    <input type=\"text\" id=\"fname\" name=\"firstName\" value=\"{doctor.name}\" disabled>\n" +
            "    \n" +
            "    <label for=\"lname\">doctor email</label>\n" +
            "    <input type=\"text\" id=\"email\" name=\"email\" value=\"{doctor.email}\" disabled>\n" +
            "\n" +
            "    <label for=\"fname\">patient Name</label>\n" +
            "    <input type=\"text\" id=\"pname\" name=\"pName\" value=\"{patient.name}\" disabled>\n" +
            "    \n" +
            "    <label for=\"lname\">patient email</label>\n" +
            "    <input type=\"text\" id=\"pemail\" name=\"pEmail\" value=\"{patient.email}\" disabled>\n" +
            "\n" +
            "    <label for=\"feedback\">feedback</label>\n" +
            "    <textarea id=\"feedback\" name=\"feedback\" placeholder=\"Write something..\" style=\"height:200px\"></textarea>\n" +
            "\n" +
            "    <input type=\"button\" onclick=\"func()\" value=\"Submit\">\n" +
            "  </form>\n" +
            "</div>\n" +
            "\n" +
            "</body>\n" +
            "<script>\n" +
            "  function func(){\n" +
            "    var doctorName = document.getElementById(\"fname\").defaultValue;\n" +
            "    var doctorEmail = document.getElementById(\"email\").defaultValue;\n" +
            "    var patientName = document.getElementById(\"pname\").defaultValue;\n" +
            "    var patientEmail = document.getElementById(\"pemail\").defaultValue;\n" +
            "    var feedback = document.getElementById(\"feedback\").value;\n" +
            "    console.log(doctorName,doctorEmail,patientName,patientEmail,feedback);\n" +
            "\n" +
            "     let updatedata = {\n" +
            "      \"firstName\": doctorName,\n" +
            "      \"email\": doctorEmail,\n" +
            "      \"patientName\": patientName,\n" +
            "      \"patientEmail\" : patientEmail,\n" +
            "      \"feedback\": feedback\n" +
            "    };\n" +
            "\n" +
            "    // var xhr = new XMLHttpRequest();\n" +
            "    // xhr.open(\"POST\", \"http://localhost:8080/feedback\", true);\n" +
            "    // xhr.setRequestHeader('Content-Type', 'application/json');\n" +
            "    // xhr.send(JSON.stringify(updatedata));\n" +
            "\n" +
            "    $.ajax({\n" +
            "      url: \"http://localhost:8080/feedback\",\n" +
            "      type: 'POST',\n" +
            "      data: JSON.stringify(updatedata),\n" +
            "      dataType: 'json',\n" +
            "      crossDomain: true,\n" +
            "      contentType: 'application/json',\n" +
            "      success: function (data) {\n" +
            "        $(\"#updateModal\").modal();\n" +
            "      },\n" +
            "      statusCode: {\n" +
            "        500: function () {\n" +
            "          alert(\"Invalid data!\");\n" +
            "        }\n" +
            "      }\n" +
            "    });\n" +
            "\n" +
            "\n" +
            "  }\n" +
            "</script>\n" +
            "</html>";

    public static final String DOCTOR_NAME = "\\{doctor.name\\}";
    public static final String DOCTOR_EMAIl = "\\{doctor.email\\}";
    public static final String PATIENT_NAME = "\\{patient.name\\}";
    public static final String PATIENT_EMAIL = "\\{patient.email\\}";

    public static final String NAME = "\\{Name\\}";
}
