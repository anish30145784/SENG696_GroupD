package org.team1.utils;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import org.team1.models.Feedback;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.StringTokenizer;

public class PDF {


    public static ByteArrayInputStream employeePDFReport
            (List<Feedback> feedbacks) throws FileNotFoundException, MalformedURLException {

        Feedback feedback = Collections.max(feedbacks, Comparator.comparing(Feedback::getCreatedDate));
        String dest = "PDFs/sample.pdf";
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.setDefaultPageSize(PageSize.A4);
        pdfDoc.addNewPage();
        Document document = new Document(pdfDoc);

        float col = 280f;
        float col_width[] = {col, col, col};

        Table table = new Table(col_width);
        table.setBackgroundColor(new DeviceRgb(63, 169, 219));
        table.setFontColor(Color.WHITE);
        String imageFile = "src/main/resources/static/images/efka-logotypo.png";
        ImageData data = ImageDataFactory.create(imageFile);
        Image img = new Image(data);
        table.addCell(img.setAutoScale(true).setWidth(100f).setHeight(120f));
        table.addCell(new Cell().add("DIAGNOSIS REPORT").setBold().setTextAlignment(TextAlignment.CENTER)
                .setVerticalAlignment(VerticalAlignment.MIDDLE).setMarginTop(30f).setMarginBottom(30f)
                .setBorder(Border.NO_BORDER).setFontSize(35f));
        table.addCell(new Cell().add("Group D\nSENL 696\nE-Vet Clinic\nUniversity of Calbary")
                .setTextAlignment(TextAlignment.RIGHT).setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setMarginTop(30f).setMarginBottom(30f).setMarginRight(10f)
                .setBorder(Border.NO_BORDER));
        document.add(table);


        Paragraph name = new Paragraph("\n" + "Patient Name : " + feedback.getClient().getFirstName());
        document.add(name);
//        Paragraph breed = new Paragraph("Breed : " + "Poodle");
//        document.add(breed);
//        Paragraph age = new Paragraph("Age(Years) : " + "9");
//        document.add(age);
        Paragraph patientID = new Paragraph("PatientID : " + feedback.getClient().getId());
        document.add(patientID);
        Paragraph emailID = new Paragraph("EmailID : " + feedback.getClient().getEmail());
        document.add(emailID);
        Paragraph contact = new Paragraph("Contact : " + feedback.getClient().getPhone());
        document.add(contact);
        Paragraph doctorName = new Paragraph("Doctor Name : " + feedback.getDoctor().getFirstName());
        document.add(doctorName);


        StringTokenizer strtok = new StringTokenizer(feedback.getFeedback(), ".");
        String updatedFeedback = new String();
        while (strtok.hasMoreTokens()) {
            String temp = (Character.toString('\u2022') + " " + strtok.nextToken().trim() + "\n");
            updatedFeedback = updatedFeedback + (temp);
        }
        Paragraph response = new Paragraph("\n" + "Doctor's Response : " + "\n" + updatedFeedback);
        document.add(response);


        String imageStamp = "static/images/authoried.jpg";
        ImageData authorizedStamp = ImageDataFactory.create(imageStamp);
        Image imgSign = new Image(authorizedStamp);
        imgSign.setFixedPosition(440, 100);
        document.add(imgSign);
        ;

        document.close();
        pdfDoc.close();
        System.out.println("PDF Created");
        return null;
    }

}
