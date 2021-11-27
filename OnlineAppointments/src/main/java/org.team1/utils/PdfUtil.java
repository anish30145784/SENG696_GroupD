package org.team1.utils;


import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.team1.models.Feedback;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.stream.Stream;

public class PdfUtil {
    private static Logger logger = LoggerFactory.getLogger(PdfUtil.class);

    public static ByteArrayInputStream employeePDFReport
            (List<Feedback> employees) {
        Document document = new Document(PageSize.A4.rotate());
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {

            PdfWriter.getInstance(document, out);
            document.open();

            // Add Text to PDF file ->
            Font font = FontFactory.getFont(FontFactory.COURIER, 14,
                    BaseColor.BLACK);
            Paragraph para = new Paragraph("FeedBack", font);
            para.setAlignment(Element.ALIGN_CENTER);
            document.add(para);
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(5);
            // Add PDF Table Header ->
            Stream.of("ID", "First Name", "Email", "Patient Name", "Patient Name", "FeedBack").forEach(headerTitle ->
            {
                PdfPCell header = new PdfPCell();
                Font headFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
                header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                header.setHorizontalAlignment(Element.ALIGN_LEFT);
                header.setBorderWidth(1);
                header.setPhrase(new Phrase(headerTitle, headFont));
                table.addCell(header);
            });

            for (Feedback feedback : employees) {
                PdfPCell idCell = new PdfPCell(new Phrase(feedback.getId().
                        toString()));
                idCell.setPaddingLeft(4);
                idCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                idCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(idCell);

                PdfPCell firstNameCell = new PdfPCell(new Phrase
                        (feedback.getDoctor().getFirstName()));
                firstNameCell.setPaddingLeft(4);
                firstNameCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                firstNameCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(firstNameCell);

                PdfPCell emailCell = new PdfPCell(new Phrase
                        (String.valueOf(feedback.getDoctor().getEmail())));
                emailCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                emailCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                emailCell.setPaddingRight(4);
                table.addCell(emailCell);

                PdfPCell patientNameCell = new PdfPCell(new Phrase
                        (feedback.getClient().getFirstName()));
                patientNameCell.setPaddingLeft(4);
                patientNameCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                patientNameCell.setHorizontalAlignment(Element.ALIGN_LEFT);
                table.addCell(patientNameCell);

                PdfPCell patientEmailCell = new PdfPCell(new Phrase
                        (String.valueOf(feedback.getClient().getEmail())));
                patientEmailCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                patientEmailCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                patientEmailCell.setPaddingRight(4);
                table.addCell(patientEmailCell);


                PdfPCell feedBackCell = new PdfPCell(new Phrase
                        (String.valueOf(feedback.getFeedback())));
                feedBackCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
                feedBackCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
                feedBackCell.setPaddingRight(4);
                table.addCell(feedBackCell);
            }
            document.add(table);


            document.close();
        } catch (DocumentException e) {
            logger.error(e.toString());
        }

        return new ByteArrayInputStream(out.toByteArray());
    }

}
