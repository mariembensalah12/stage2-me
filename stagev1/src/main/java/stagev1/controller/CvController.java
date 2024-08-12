package stagev1.controller;

import java.io.IOException;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ch.qos.logback.classic.Logger;
import stagev1.models.CVData;
import stagev1.repositories.CvRepository;
import stagev1.services.AIService;
import stagev1.services.PDFService;

@RestController
@RequestMapping("/cv")
public class CvController {

    private static final Logger logger = (Logger) LoggerFactory.getLogger(CvController.class);

    @Autowired
    private CvRepository cvRepository;

    @Autowired
    private AIService aiService;

    @Autowired
    private PDFService pdfService;
  

    @GetMapping("/findall")
    public List<CVData> fetchAll() {
        return cvRepository.findAll();
    }

    @PostMapping("/generate")
    public ResponseEntity<?> generateCV(@RequestBody CVData cv) {
        logger.info("Début de la génération du CV");
        try {
            logger.info("Génération des propositions de CV");
            List<String> cvProposals = aiService.generateCVProposals(cv);
            
            logger.info("Génération des PDFs");
            List<String> pdfPaths = pdfService.generatePDFs(cv, cvProposals);
            
            logger.info("Génération terminée avec succès");
            return new ResponseEntity<>(pdfPaths, HttpStatus.CREATED);
        } catch (Exception e) {
            logger.error("Erreur lors de la génération du CV", e);
            return new ResponseEntity<>("Erreur lors de la génération du CV: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

       
    }
    

