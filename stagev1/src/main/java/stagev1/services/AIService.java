package stagev1.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import stagev1.models.CVData;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class AIService {
    private static final Logger logger = LoggerFactory.getLogger(AIService.class);

    @Value("${huggingface.api.url}")
    private String apiUrl;

    @Value("${huggingface.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String CV_TEMPLATE_FR = """
            Curriculum Vitae

            INFORMATIONS PERSONNELLES
            Nom: %s
            Prénom: %s
            Date de naissance: %s
            Email: %s
            Téléphone: %s

            DESCRIPTION
            %s

            OBJECTIF PROFESSIONNEL
            %s

            EXPÉRIENCE PROFESSIONNELLE
            %s

            FORMATION
            %s

            %s
            """;

    private static final String CV_TEMPLATE_EN = """
            Curriculum Vitae

            PERSONAL INFORMATION
            Last Name: %s
            First Name: %s
            Date of Birth: %s
            Email: %s
            Phone: %s

            SUMMARY
            %s

            PROFESSIONAL OBJECTIVE
            %s

            WORK EXPERIENCE
            %s

            EDUCATION
            %s

            %s
            """;

    private static final String DEFAULT_VALUE_FR = "Non spécifié";
    private static final String DEFAULT_VALUE_EN = "Not specified";

    public List<String> generateCVProposals(CVData cvData) {
        validateCVData(cvData);
        List<String> proposals = new ArrayList<>();
        
        // Generate CV in French
        String frObjective = generateObjective(cvData);
        String frSkills = generateSkills(cvData, "fr");
        String frCV = String.format(CV_TEMPLATE_FR,
            Optional.ofNullable(cvData.getNom()).orElse(DEFAULT_VALUE_FR),
            Optional.ofNullable(cvData.getPrénom()).orElse(DEFAULT_VALUE_FR),
            Optional.ofNullable(cvData.getDateNais()).orElse(DEFAULT_VALUE_FR),
            Optional.ofNullable(cvData.getEmail()).orElse(DEFAULT_VALUE_FR),
            Optional.ofNullable(cvData.getTel()).orElse(DEFAULT_VALUE_FR),
            Optional.ofNullable(cvData.getDescription()).orElse(DEFAULT_VALUE_FR),
            frObjective,
            Optional.ofNullable(cvData.getExperience()).orElse(DEFAULT_VALUE_FR),
            Optional.ofNullable(cvData.getEducation()).orElse(DEFAULT_VALUE_FR),
            frSkills
        );
        proposals.add(frCV);
        
        // Generate CV in English
        String enObjective = translateToEnglish(frObjective);
        String enSkills = generateSkills(cvData, "en");
        String enCV = String.format(CV_TEMPLATE_EN,
            Optional.ofNullable(cvData.getNom()).orElse(DEFAULT_VALUE_EN),
            Optional.ofNullable(cvData.getPrénom()).orElse(DEFAULT_VALUE_EN),
            Optional.ofNullable(cvData.getDateNais()).orElse(DEFAULT_VALUE_EN),
            Optional.ofNullable(cvData.getEmail()).orElse(DEFAULT_VALUE_EN),
            Optional.ofNullable(cvData.getTel()).orElse(DEFAULT_VALUE_EN),
            translateToEnglish(Optional.ofNullable(cvData.getDescription()).orElse(DEFAULT_VALUE_EN)),
            enObjective,
            translateToEnglish(Optional.ofNullable(cvData.getExperience()).orElse(DEFAULT_VALUE_EN)),
            translateToEnglish(Optional.ofNullable(cvData.getEducation()).orElse(DEFAULT_VALUE_EN)),
            enSkills
        );
        proposals.add(enCV);
        
        return proposals;
    }

    private void validateCVData(CVData cvData) {
        if (cvData == null) {
            throw new IllegalArgumentException("CVData cannot be null");
        }
        // Add more specific validations if needed
    }
    public String generateObjective(CVData cvData) {
        String prompt = String.format("Rédigez un objectif professionnel de 10 à 15 mots maximum pour un CV . Utilisez ces informations :\nNom: %s\nPrénom: %s\nDescription: %s\n\nObjectif professionnel (10-15 mots) :",
            cvData.getNom(), cvData.getPrénom(), cvData.getDescription());
        
        String result = callHuggingFaceAPI(prompt);
        logger.info("Raw API response for French objective: {}", result);
        String extractedObjective = extractObjective(result);
        logger.info("Extracted French objective: {}", extractedObjective);
        return extractedObjective;
    }

    public String translateToEnglish(String frenchObjective) {
        String prompt = String.format("Translate the following French professional objective to English:\n\n%s\n\nEnglish translation:", frenchObjective);
        
        String result = callHuggingFaceAPI(prompt);
        logger.info("Raw API response for English translation: {}", result);
        String translatedObjective = extractTranslation(result);
        logger.info("Translated English objective: {}", translatedObjective);
        return translatedObjective;
    }

    private String extractObjective(String apiResponse) {
        String[] lines = apiResponse.split("\n");
        String objectiveLine = Arrays.stream(lines)
            .filter(line -> line.toLowerCase().contains("objectif professionnel"))
            .findFirst()
            .orElse("");

        String[] parts = objectiveLine.split(":");
        if (parts.length > 1) {
            String objective = parts[1].trim();
            return truncateText(objective, 15);
        }
        return "Objectif professionnel non généré correctement";
    }

    private String extractTranslation(String apiResponse) {
        // Diviser la réponse API en lignes
        String[] lines = apiResponse.split("\n");
        boolean translationFound = false;

        for (String line : lines) {
        	 if (line.toLowerCase().contains("a:")) {
                 break; // Arrêter l'ajout de lignes si le marqueur est trouvé
             }
        	 
            // Détection du début de la traduction
            if (line.toLowerCase().contains("english translation")) {
                translationFound = true;
                continue; // Passer à la ligne suivante après avoir trouvé le marqueur
            }

            // Extraction de la première ligne après le marqueur
            if (translationFound) {
                return line.trim(); // Retourner la première ligne non vide
            }
        }

        // Retourner une chaîne vide si rien n'est trouvé
        return "";
    }




    private String generateSkills(CVData cvData, String lang) {
        String skillsString = (cvData.getSkills() != null) ? cvData.getSkills() : "";
        String[] skills = skillsString.split(",");

        Set<String> defaultSoftSkills = new HashSet<>(Arrays.asList(
            "travail en équipe", "communication", "résolution de problèmes",
            "teamwork", "communication", "problem-solving"
        ));

        List<String> technicalSkills = new ArrayList<>();
        Set<String> softSkills = new LinkedHashSet<>();

        for (String skill : skills) {
            String trimmedSkill = skill.trim();
            String lowerCaseSkill = trimmedSkill.toLowerCase();

            if (defaultSoftSkills.contains(lowerCaseSkill)) {
                // Traduire uniquement les compétences transversales
                String translatedSkill = lang.equals("en") ? translateToEnglish(lowerCaseSkill) : lowerCaseSkill;
                softSkills.add(capitalizeFirstLetter(translatedSkill));
            } else if (!trimmedSkill.isEmpty()) {
                // Ne pas traduire les compétences techniques et ignorer les compétences vides
                technicalSkills.add(trimmedSkill);
            }
        }

        // Ajouter les compétences transversales manquantes
        if (lang.equals("en")) {
            softSkills.addAll(Arrays.asList("Teamwork", "Communication", "Problem-solving"));
        } else {
            softSkills.addAll(Arrays.asList("Travail en équipe", "Communication", "Résolution de problèmes"));
        }

        StringBuilder result = new StringBuilder();

        if (!technicalSkills.isEmpty()) {
            result.append(lang.equals("fr") ? "Compétences techniques :\n" : "Technical Skills:\n");
            for (int i = 0; i < technicalSkills.size(); i++) {
                result.append(i + 1).append(". ").append(technicalSkills.get(i)).append("\n");
            }
        }

        if (!softSkills.isEmpty()) {
            result.append(lang.equals("fr") ? "\nCompétences transversales :\n" : "\nSoft Skills:\n");
            List<String> sortedSoftSkills = new ArrayList<>(softSkills);
            Collections.sort(sortedSoftSkills);
            for (int i = 0; i < sortedSoftSkills.size(); i++) {
                result.append(i + 1).append(". ").append(sortedSoftSkills.get(i)).append("\n");
            }
        }

        return result.toString();
    }

   
    
   
    private String capitalizeFirstLetter(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    private String cleanAPIResponse(String response) {
        response = response.replaceAll("(?i)Rédigez.*?:\\s*", "")
                           .replaceAll("(?i)Listez.*?:\\s*", "")
                           .replaceAll("(?i)Développez.*?:\\s*", "")
                           .replaceAll("(?i)Basé sur ces compétences :\\s*", "")
                           .replaceAll("(?i)Compétences transversales :\\s*", "")
                           .replaceAll("(?i)Ecrivez.*", "")
                           .replaceAll("(?i)propose.*", "")
                           .replaceAll("(?i)Découvrez.*", "")
                           .replaceAll("(?i)Translate the following.*?English translation:", "")
                           .replaceAll("(?i)My first thought.*", "")
                           .replaceAll("(?i)I'd translate.*", "")
                           .replaceAll("(?m)^•\\s*", "")
                           .replaceAll("(?m)\\n{2,}", "\n")
                           .trim();
        
        return response.replaceAll("(?m)^[•]?[ ]*", "")
                       .replaceAll("(?m)\\n{2,}", "\n")
                       .trim();
    }

    private String callHuggingFaceAPI(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + apiKey);
        Map<String, Object> body = Map.of(
            "inputs", prompt,
            "parameters", Map.of(
                "max_new_tokens", 100,
                "temperature", 0.7,
                "top_p", 0.95
            )
        );
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        try {
            logger.info("Sending request to Hugging Face API: {}", prompt);
            ResponseEntity<List> response = restTemplate.postForEntity(apiUrl, request, List.class);
            List<Map<String, Object>> result = response.getBody();
            if (result != null && !result.isEmpty()) {
                String generatedText = (String) result.get(0).get("generated_text");
                logger.info("Received response from Hugging Face API: {}", generatedText);
                return cleanAPIResponse(generatedText);
            }
        } catch (Exception e) {
            logger.error("Error calling Hugging Face API", e);
        }
        logger.warn("Failed to generate content, returning default");
        return "Contenu non généré";
    }



    private String truncateText(String text, int maxWords) {
        String[] words = text.split("\\s+");
        if (words.length > maxWords) {
            text = String.join(" ", Arrays.copyOfRange(words, 0, maxWords));
        }
        return text;
    }
}
