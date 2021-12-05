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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.team1.models.Feedback;

import java.io.*;
import java.net.MalformedURLException;
import java.util.List;
import java.util.StringTokenizer;

public class PdfUtil {
    private static Logger logger = LoggerFactory.getLogger(PdfUtil.class);

    public static ByteArrayInputStream employeePDFReport(List<Feedback> employees) {
        //Document document = new Document(PageSize.A4);
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            System.out.println("Inside try block of PdfUtil !");
            Feedback feedback = employees.get(0);
            String fileName = (feedback.getClient().getFirstName() + "_" + feedback.getClient().getLastName()) + ".pdf";
            System.out.println("Report FileName : " + fileName);
            String dest = "D:\\PDFs\\" + fileName;
            PdfWriter writer = new PdfWriter(dest);
            PdfDocument pdfDoc = new PdfDocument(writer);
            pdfDoc.setDefaultPageSize(PageSize.A4);
            pdfDoc.addNewPage();
            Document document = new Document(pdfDoc);
            System.out.print("Inside PDF Util !");
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
            table.addCell(new Cell().add("Group D\nSENG 696\nE-Vet Clinic\nUniversity of Calgary")
                    .setTextAlignment(TextAlignment.RIGHT).setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setMarginTop(30f).setMarginBottom(30f).setMarginRight(10f)
                    .setBorder(Border.NO_BORDER));
            document.add(table);


            Paragraph name = new Paragraph("\n" + "Pet Owner's Name : " + feedback.getClient().getFirstName() + " " + feedback.getClient().getLastName());
            document.add(name);
            Paragraph breed = new Paragraph("Pet's Breed : " + feedback.getClient().getBreed());
            document.add(breed);
            Paragraph age = new Paragraph("Pet's Age(Years) : " + feedback.getClient().getAge());
            document.add(age);
            Paragraph patientID = new Paragraph("Pet's ID : " + feedback.getClient().getId());
            document.add(patientID);
            Paragraph emailID = new Paragraph("Pet Owner's EmailID : " + feedback.getClient().getEmail());
            document.add(emailID);
            Paragraph contact = new Paragraph("Pet Owner's Contact : " + feedback.getClient().getPhone());
            document.add(contact);
            Paragraph doctorName = new Paragraph("Doctor Name : " + feedback.getDoctor().getFirstName() + " " + feedback.getDoctor().getLastName());
            document.add(doctorName);

            System.out.print("Document creation in progress !!");

            // Map the response of Doctor against feedback field.
            // String feedback1 = "The patient is not able to breathe properly. It has a lung infection. Please administer Medicine : Synthroid.";
            StringTokenizer strtok = new StringTokenizer(feedback.getFeedback(), ".");
            String updatedFeedback = new String();
            while (strtok.hasMoreTokens()) {
                String temp = (Character.toString('\u2022') + " " + strtok.nextToken().trim() + "\n");
                updatedFeedback = updatedFeedback + (temp);
            }
            Paragraph response = new Paragraph("\n" + "Doctor's Response : " + "\n" + updatedFeedback);
            document.add(response);


            String imageStamp = "src/main/resources/static/images/NewStamp.jpg";
            ImageData authorizedStamp = ImageDataFactory.create(imageStamp);
            Image imgSign = new Image(authorizedStamp);
            imgSign.setFixedPosition(440, 100);
            document.add(imgSign);
            document.close();
            pdfDoc.close();
            System.out.println("PDF Created !");

            File f = new File(dest);
            byte[] buf = new byte[8192];
            FileInputStream fis = new FileInputStream(f);
            int c = 0;
            while ((c = fis.read(buf, 0, buf.length)) > 0) {
                out.write(buf, 0, c);
                out.flush();
            }

            out.close();
            System.out.println("stop");
            fis.close();


//            PdfWriter.getInstance(document, out);
//            document.open();
//
//            // Add Text to PDF file ->
//            Font font = FontFactory.getFont(FontFactory.COURIER, 14,
//                    BaseColor.BLACK);
//            Paragraph para = new Paragraph("FeedBack", font);
//            para.setAlignment(Element.ALIGN_CENTER);
//            document.add(para);
//            document.add(Chunk.NEWLINE);
//            PdfPTable table = new PdfPTable(5);
//
//            System.out.println("Inside PdfUtil !");
//            Image img = Image.getInstance("src/main/resources/static/images/efka-logotypo.png");
//
//            img.setAbsolutePosition(500f, 120f);
//
//            document.add(img);
//
//
//            Feedback feedback = employees.get(0);
//
//            Paragraph name = new Paragraph("\n" + "Patient Name : " + feedback.getClient().getFirstName());
//            document.add((Element) name);
//            Paragraph patientID = new Paragraph("PatientID : " + feedback.getClient().getId());
//            document.add((Element) patientID);
//            Paragraph emailID = new Paragraph("EmailID : " + feedback.getClient().getEmail());
//            document.add((Element) emailID);
//            Paragraph contact = new Paragraph("Contact : " + feedback.getClient().getPhone());
//            document.add((Element) contact);
//            Paragraph doctorName = new Paragraph("Doctor Name : " + feedback.getDoctor().getFirstName());
//            document.add((Element) doctorName);
//            Paragraph feedback1 = new Paragraph("Feedback : " + feedback.getFeedback());
//            document.add((Element) feedback1);
////                PdfPCell idCell = new PdfPCell(new Phrase(feedback.getId().
////                        toString()));
////                idCell.setPaddingLeft(4);
////                idCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
////                idCell.setHorizontalAlignment(Element.ALIGN_CENTER);
////                table.addCell(idCell);
////
////                PdfPCell firstNameCell = new PdfPCell(new Phrase
////                        (feedback.getDoctor().getFirstName()));
////                firstNameCell.setPaddingLeft(4);
////                firstNameCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
////                firstNameCell.setHorizontalAlignment(Element.ALIGN_LEFT);
////                table.addCell(firstNameCell);
////
////                PdfPCell emailCell = new PdfPCell(new Phrase
////                        (String.valueOf(feedback.getDoctor().getEmail())));
////                emailCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
////                emailCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
////                emailCell.setPaddingRight(4);
////                table.addCell(emailCell);
////
////                PdfPCell patientNameCell = new PdfPCell(new Phrase
////                        (feedback.getClient().getFirstName()));
////                patientNameCell.setPaddingLeft(4);
////                patientNameCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
////                patientNameCell.setHorizontalAlignment(Element.ALIGN_LEFT);
////                table.addCell(patientNameCell);
////
////                PdfPCell patientEmailCell = new PdfPCell(new Phrase
////                        (String.valueOf(feedback.getClient().getEmail())));
////                patientEmailCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
////                patientEmailCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
////                patientEmailCell.setPaddingRight(4);
////                table.addCell(patientEmailCell);
////
////
////                PdfPCell feedBackCell = new PdfPCell(new Phrase
////                        (String.valueOf(feedback.getFeedback())));
////                feedBackCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
////                feedBackCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
////                feedBackCell.setPaddingRight(4);
////                table.addCell(feedBackCell);
//
//            document.add(table);
//
//
//            document.close();
        } catch (FileNotFoundException | MalformedURLException e) {
            logger.error(e.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

}
