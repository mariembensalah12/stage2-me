package stagev1.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.transaction.Transactional;
import stagev1.models.Forms;
import stagev1.repositories.FormRepository;
import stagev1.services.UserFormService;

@RestController
@RequestMapping("/form")
public class FormController {

    private final FormRepository formRepository;
    private final ObjectMapper objectMapper;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public FormController(FormRepository formRepository, ObjectMapper objectMapper, JdbcTemplate jdbcTemplate) {
        this.formRepository = formRepository;
        this.objectMapper = objectMapper;
        this.jdbcTemplate = jdbcTemplate;
    }
    @Autowired
    private UserFormService dynamicFormService;

    @GetMapping("/findall")
    public ResponseEntity<List<Forms>> findAllForms() throws JsonProcessingException {
      List<Forms> forms = formRepository.findAll();
        System.out.println("JSON response: " + objectMapper.writeValueAsString(forms));
        return ResponseEntity.ok(forms);
    }
    
    
    @PostMapping("/{id}/insertchampform")
    @Transactional
    public ResponseEntity<?> insertIntochampform(@PathVariable Integer id, @RequestBody Map<String, Object> newValue) {
        Optional<Forms> optionalForm = formRepository.findById(id);
        
        if (optionalForm.isPresent()) {
            Forms form = optionalForm.get();
            List<Map<String, Object>> champform = form.getChampform();
            
            if (champform == null) {
            	champform = new ArrayList<>();
            }
            
            // Générer un ID unique
            String uniqueId = UUID.randomUUID().toString();
            
            // Ajouter l'ID unique à la nouvelle valeur
            Map<String, Object> valueWithId = new HashMap<>(newValue);
            valueWithId.put("id", uniqueId);
            
            champform.add(valueWithId);
            form.setChampform(champform);
            
            formRepository.save(form);
            
            return ResponseEntity.ok().body("Valeur insérée avec succès dans champform avec l'ID: " + uniqueId);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @PutMapping("/{formId}/updatechampform/{valueId}")
    @Transactional
    public ResponseEntity<?> updateChampformValue(@PathVariable Integer formId, 
                                                  @PathVariable String valueId, 
                                                  @RequestBody Map<String, Object> updatedValue) {
        Optional<Forms> optionalForm = formRepository.findById(formId);

        if (optionalForm.isPresent()) {
            Forms form = optionalForm.get();
            List<Map<String, Object>> champform = form.getChampform();

            if (champform != null) {
                for (Map<String, Object> value : champform) {
                    if (value.get("id").equals(valueId)) {
                        // Conserver l'ID existant
                        updatedValue.put("id", valueId);
                        // Remplacer l'ancienne valeur par la nouvelle
                        champform.set(champform.indexOf(value), updatedValue);
                        
                        form.setChampform(champform);
                        formRepository.save(form);

                        return ResponseEntity.ok().body("Valeur mise à jour avec succès dans champform");
                    }
                }
                return ResponseEntity.notFound().build();
            } else {
                return ResponseEntity.badRequest().body("La liste champform est vide");
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @DeleteMapping("/{formId}/deletechampform/{valueId}")
    @Transactional
    public ResponseEntity<?> deleteChampformValue(@PathVariable Integer formId, 
                                                  @PathVariable String valueId) {
        Optional<Forms> optionalForm = formRepository.findById(formId);

        if (optionalForm.isPresent()) {
            Forms form = optionalForm.get();
            List<Map<String, Object>> champform = form.getChampform();

            if (champform != null) {
                boolean removed = champform.removeIf(value -> value.get("id").equals(valueId));
                
                if (removed) {
                    form.setChampform(champform);
                    formRepository.save(form);
                    return ResponseEntity.ok().body("Valeur supprimée avec succès de champform");
                } else {
                    return ResponseEntity.notFound().build();
                }
            } else {
                return ResponseEntity.badRequest().body("La liste champform est vide");
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/add")
    @Transactional
    public ResponseEntity<Forms> addForm(@RequestBody Map<String, String> request) {
        String nom = request.get("nom");
        if (nom == null || nom.isEmpty()) {
            return ResponseEntity.badRequest().body(null);
        }

        Forms form = new Forms();
        form.setNom(nom);
        form = formRepository.save(form);
        
        return ResponseEntity.ok(form);
    }
    
   
    
    
    
    @PutMapping("/update/{id}")
    @Transactional
    public ResponseEntity<Forms> updateForm(@PathVariable Integer id, @RequestBody Map<String, String> request) {
        Optional<Forms> optionalForm = formRepository.findById(id);
        
        if (!optionalForm.isPresent()) {
            return ResponseEntity.notFound().build();
        }
        
        Forms form = optionalForm.get();
        
        String nom = request.get("nom");
        if (nom != null && !nom.isEmpty()) {
            form.setNom(nom);
        }
        
        
        
        form = formRepository.save(form);
        
        return ResponseEntity.ok(form);
    }
    @DeleteMapping("delete/{id}")
   	public ResponseEntity<?> deleteTodo(@PathVariable Integer id){
   		
   		if(this.formRepository.findById(id).isPresent()) {
   			this.formRepository.deleteById(id);
   			return new ResponseEntity<>(HttpStatus.OK);
   			
   		}else {
   			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
   		}
       }
    @GetMapping("/{id}")
    public ResponseEntity<Forms> getFormById(@PathVariable Integer id) {
        Optional<Forms> optionalForm = formRepository.findById(id);

        if (optionalForm.isPresent()) {
            return ResponseEntity.ok(optionalForm.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    @PostMapping("/save")
    public ResponseEntity<?> saveFormData(@RequestParam String formName, @RequestBody Map<String, Object> formData) {
        try {
        	dynamicFormService.saveFormData(formName, formData);
            return ResponseEntity.ok().body(Collections.singletonMap("message", "Données sauvegardées avec succès"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Collections.singletonMap("error", "Erreur lors de la sauvegarde des données: " + e.getMessage()));
        }
    }

}
