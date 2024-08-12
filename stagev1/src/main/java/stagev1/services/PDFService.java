package stagev1.services;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.layout.borders.Border;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stagev1.models.CVData;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PDFService {
    private static final Logger logger = LoggerFactory.getLogger(PDFService.class);
    private static final String PDF_DIRECTORY = "C:\\Users\\Mariem\\Desktop\\stagev1\\front\\dynamic-form\\src\\assets/";
    private final AIService aiService;
    @Autowired
    public PDFService(AIService aiService) {
        this.aiService = aiService;
    }

    private static final Map<String, Map<String, String>> TRANSLATIONS = Map.of(
        "fr", Map.of(
            "title", "Curriculum Vitae",
            "personalInfo", "INFORMATIONS PERSONNELLES",
            "description", "DESCRIPTION",
            "objective", "OBJECTIF PROFESSIONNEL",
            "experience", "EXPÉRIENCE PROFESSIONNELLE",
            "education", "FORMATION",
            "skills", "COMPÉTENCES",
            "birth", "Né(e) le",
            "technicalSkills", "Compétences techniques",
            "softSkills", "Compétences transversales"
        ),
        "en", Map.of(
            "title", "Curriculum Vitae",
            "personalInfo", "PERSONAL INFORMATION",
            "description", "SUMMARY",
            "objective", "PROFESSIONAL OBJECTIVE",
            "experience", "WORK EXPERIENCE",
            "education", "EDUCATION",
            "skills", "SKILLS",
            "birth", "Date of Birth",
            "technicalSkills", "Technical Skills",
            "softSkills", "Soft Skills"
        )
    );

    public List<String> generatePDFs(CVData cvData, List<String> cvProposals) throws IOException {
        List<String> pdfPaths = new ArrayList<>();
        File pdfDir = new File(PDF_DIRECTORY);
        if (!pdfDir.exists() && !pdfDir.mkdirs()) {
            throw new IOException("Failed to create directories: " + PDF_DIRECTORY);
        }

        String[] languages = {"fr", "en"};
        
        String frenchObjective = aiService.generateObjective(cvData);
        String englishObjective = aiService.translateToEnglish(frenchObjective);
        logger.info("French Objective: {}", frenchObjective);
        logger.info("Translated English Objective: {}", englishObjective);


        for (int i = 0; i < cvProposals.size(); i++) {
            String lang = languages[i];
            String fileName = "cvproposal_" + lang + ".pdf";
            String filePath = PDF_DIRECTORY + fileName;
            
            try (PdfWriter writer = new PdfWriter(filePath);
                 PdfDocument pdf = new PdfDocument(writer);
                 Document document = new Document(pdf, PageSize.A4)) {

                PdfFont titleFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
                PdfFont normalFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
                PdfFont boldFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);

                Color titleColor = new DeviceRgb(0, 51, 102);
                Color textColor = new DeviceRgb(51, 51, 51);
                Color separatorColor = new DeviceRgb(200, 200, 200);

                document.setMargins(20, 20, 20, 20);

                addTitle(document, TRANSLATIONS.get(lang).get("title"), titleFont, titleColor);

                float[] columnWidths = {1, 2};
                Table table = new Table(UnitValue.createPercentArray(columnWidths)).useAllAvailableWidth();

                Cell leftCell = new Cell().setBorder(Border.NO_BORDER).setPadding(5);
                addPersonalInfo(leftCell, cvData, normalFont, textColor, lang);
                table.addCell(leftCell);

                Cell rightCell = new Cell().setBorder(Border.NO_BORDER).setPadding(5);
                addSection(rightCell, TRANSLATIONS.get(lang).get("description"), titleFont, titleColor);
                rightCell.add(new Paragraph(lang.equals("fr") ? cvData.getDescription() : translateToEnglish(cvData.getDescription()))
                    .setFont(normalFont).setFontColor(textColor).setMarginBottom(5));
                addSection(rightCell, TRANSLATIONS.get(lang).get("objective"), titleFont, titleColor);
                
                String objective = lang.equals("fr") ? frenchObjective : englishObjective;

                logger.info("Objective for {} CV: {}", lang, objective);

                if (objective != null && !objective.isEmpty()) {
                    rightCell.add(new Paragraph(objective)
                        .setFont(normalFont)
                        .setFontColor(textColor)
                        .setMarginBottom(5));
                } else {
                    rightCell.add(new Paragraph(lang.equals("fr") ? "Objectif non spécifié" : "Objective not specified")
                        .setFont(normalFont)
                        .setFontColor(textColor)
                        .setMarginBottom(5));
                }
                table.addCell(rightCell);

                document.add(table);
                document.add(new LineSeparator(new SolidLine()).setMarginTop(5).setMarginBottom(5));

                addSection(document, TRANSLATIONS.get(lang).get("experience"), titleFont, titleColor);
                document.add(new Paragraph(lang.equals("fr") ? cvData.getExperience() : translateToEnglish(cvData.getExperience()))
                    .setFont(normalFont).setFontColor(textColor).setMarginBottom(5));

                document.add(new LineSeparator(new SolidLine()).setMarginTop(5).setMarginBottom(5));

                addSection(document, TRANSLATIONS.get(lang).get("education"), titleFont, titleColor);
                document.add(new Paragraph(lang.equals("fr") ? cvData.getEducation() : translateToEnglish(cvData.getEducation()))
                    .setFont(normalFont).setFontColor(textColor).setMarginBottom(5));

                document.add(new LineSeparator(new SolidLine()).setMarginTop(5).setMarginBottom(5));

                addSection(document, TRANSLATIONS.get(lang).get("skills"), titleFont, titleColor);
                addSkills(document, extractSkills(cvProposals.get(i)), boldFont, textColor, lang);
            }

            pdfPaths.add("/" + fileName);
        }

        return pdfPaths;
    }


   
    private void addTitle(Document document, String title, PdfFont font, Color color) {
        document.add(new Paragraph(title)
            .setFont(font)
            .setFontSize(20)
            .setFontColor(color)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(10));
    }

    private void addSection(Object container, String title, PdfFont font, Color color) {
        Paragraph sectionTitle = new Paragraph(title)
            .setFont(font)
            .setFontSize(14)
            .setFontColor(color)
            .setMarginTop(10)
            .setMarginBottom(5);

        if (container instanceof Document) {
            ((Document) container).add(sectionTitle);
        } else if (container instanceof Cell) {
            ((Cell) container).add(sectionTitle);
        } else {
            throw new IllegalArgumentException("Unsupported container type: " + container.getClass().getName());
        }
    }

    private void addPersonalInfo(Object container, CVData cvData, PdfFont font, Color color, String lang) {
        Paragraph nameParagraph = new Paragraph(cvData.getNom() + " " + cvData.getPrénom())
            .setFont(font)
            .setFontSize(16)
            .setFontColor(color)
            .setBold()
            .setMarginBottom(5);

        Paragraph emailParagraph = new Paragraph(cvData.getEmail())
            .setFont(font)
            .setFontColor(color)
            .setMarginBottom(5);

        Paragraph telParagraph = new Paragraph(cvData.getTel())
            .setFont(font)
            .setFontColor(color)
            .setMarginBottom(5);

        if (container instanceof Cell) {
            Cell cell = (Cell) container;
            cell.add(nameParagraph);
            cell.add(emailParagraph);
            cell.add(telParagraph);
            if (cvData.getDateNais() != null) {
                cell.add(new Paragraph(TRANSLATIONS.get(lang).get("birth") + " " + cvData.getDateNais())
                    .setFont(font)
                    .setFontColor(color)
                    .setMarginBottom(5));
            }
        } else if (container instanceof Document) {
            Document doc = (Document) container;
            doc.add(nameParagraph);
            doc.add(emailParagraph);
            doc.add(telParagraph);
            if (cvData.getDateNais() != null) {
                doc.add(new Paragraph(TRANSLATIONS.get(lang).get("birth") + " " + cvData.getDateNais())
                    .setFont(font)
                    .setFontColor(color)
                    .setMarginBottom(5));
            }
        } else {
            throw new IllegalArgumentException("Unsupported container type: " + container.getClass().getName());
        }
    }

    private void addSkills(Document document, String skills, PdfFont font, Color color, String lang) {
        String[] skillSections = skills.split("\n\n");
        for (String section : skillSections) {
            String[] lines = section.split("\n");
            if (lines.length > 0) {
                String sectionTitle = lines[0].trim();
                if (sectionTitle.equalsIgnoreCase("Compétences techniques") || sectionTitle.equalsIgnoreCase("Technical Skills")) {
                    sectionTitle = TRANSLATIONS.get(lang).get("technicalSkills");
                } else if (sectionTitle.equalsIgnoreCase("Compétences transversales") || sectionTitle.equalsIgnoreCase("Soft Skills")) {
                    sectionTitle = TRANSLATIONS.get(lang).get("softSkills");
                }
                
                Paragraph sectionTitleParagraph = new Paragraph(sectionTitle)
                    .setFont(font)
                    .setFontSize(12)
                    .setFontColor(color)
                    .setMarginTop(5)
                    .setMarginBottom(5);
                document.add(sectionTitleParagraph);
                
                for (int i = 1; i < lines.length; i++) {
                    Paragraph skillParagraph = new Paragraph(lines[i].trim())
                        .setFont(font)
                        .setFontColor(color)
                        .setMarginLeft(10)
                        .setMarginBottom(5);
                    document.add(skillParagraph);
                }
            }
        }
    }

   

    private String extractSkills(String cvProposal) {
        String[] lines = cvProposal.split("\n");
        boolean inSkillsSection = false;
        StringBuilder skills = new StringBuilder();
        for (String line : lines) {
            if (line.toUpperCase().contains("COMPÉTENCES") || line.toUpperCase().contains("SKILLS")) {
                inSkillsSection = true;
                skills.append(line).append("\n");
                continue;
            }
            if (inSkillsSection) {
                skills.append(line).append("\n");
            }
        }
        return skills.toString().trim();
    }

    private String translateToEnglish(String text) {
        return aiService.translateToEnglish(text);
    }
}