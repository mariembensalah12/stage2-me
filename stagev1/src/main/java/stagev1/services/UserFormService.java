package stagev1.services;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class UserFormService {
	@Autowired
    private JdbcTemplate jdbcTemplate;

    public void saveFormData(String formName, Map<String, Object> formData) {
        String tableName = "form_" + sanitizeTableName(formName);

        if (!tableExists(tableName)) {
            createTable(tableName, formData.keySet());
        } else {
            updateTableStructure(tableName, formData.keySet());
        }

        insertData(tableName, formData);
    }

    private String sanitizeTableName(String formName) {
        // Remplace les caractères non-alphanumériques par des underscores
        return formName.replaceAll("[^a-zA-Z0-9]", "_").toLowerCase();
    }
    
    private boolean tableExists(String tableName) {
        try {
            jdbcTemplate.queryForObject("SELECT 1 FROM " + tableName + " LIMIT 1", Integer.class);
            return true;
        } catch (DataAccessException e) {
            return false;
        }
    }
    private void createTable(String tableName, Set<String> columns) {
        StringBuilder sql = new StringBuilder("CREATE TABLE " + tableName + " (id SERIAL PRIMARY KEY");
        for (String column : columns) {
            sql.append(", ").append(column).append(" TEXT");
        }
        sql.append(")");
        jdbcTemplate.execute(sql.toString());
    }

    private void updateTableStructure(String tableName, Set<String> newColumns) {
        List<String> existingColumns = getExistingColumns(tableName);
        for (String column : newColumns) {
            if (!existingColumns.contains(column)) {
                jdbcTemplate.execute("ALTER TABLE " + tableName + " ADD COLUMN " + column + " TEXT");
            }
        }
    }

    private List<String> getExistingColumns(String tableName) {
        return jdbcTemplate.queryForList("SELECT column_name FROM information_schema.columns WHERE table_name = ?", String.class, tableName);
    }

    private void insertData(String tableName, Map<String, Object> formData) {
        String columns = String.join(", ", formData.keySet());
        String values = String.join(", ", Collections.nCopies(formData.size(), "?"));
        String sql = "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + values + ")";
        jdbcTemplate.update(sql, formData.values().toArray());
    }

}
