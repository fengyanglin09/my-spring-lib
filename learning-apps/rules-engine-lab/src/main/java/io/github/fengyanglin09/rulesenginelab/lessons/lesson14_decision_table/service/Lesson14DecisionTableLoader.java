package io.github.fengyanglin09.rulesenginelab.lessons.lesson14_decision_table.service;

import io.github.fengyanglin09.rulesenginelab.lessons.lesson14_decision_table.model.Lesson14DecisionRow;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads the Lesson 14 CSV decision table from the classpath.
 *
 * <p>This loader is intentionally tiny and expects a simple training CSV with
 * no commas inside cell values. A production app should use a real CSV parser
 * or Drools' native decision-table support.</p>
 */
@Component
public class Lesson14DecisionTableLoader {

    private static final String TABLE_PATH =
            "io/github/fengyanglin09/rulesenginelab/lessons/lesson14_decision_table/table/discount-decision-table.csv";

    /**
     * Reads all configured decision rows.
     *
     * @return decision row facts ready to insert into the Rule Unit
     */
    public List<Lesson14DecisionRow> loadRows() {
        ClassPathResource resource = new ClassPathResource(TABLE_PATH);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                resource.getInputStream(), StandardCharsets.UTF_8))) {
            List<Lesson14DecisionRow> rows = new ArrayList<>();
            String line;
            boolean header = true;

            while ((line = reader.readLine()) != null) {
                if (line.isBlank()) {
                    continue;
                }
                if (header) {
                    header = false;
                    continue;
                }
                rows.add(toDecisionRow(line));
            }

            return rows;
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load Lesson 14 decision table", exception);
        }
    }

    private Lesson14DecisionRow toDecisionRow(String line) {
        String[] columns = line.split(",", -1);
        if (columns.length != 5) {
            throw new IllegalArgumentException("Decision table row must have 5 columns: " + line);
        }

        return new Lesson14DecisionRow(
                columns[0].trim(),
                columns[1].trim(),
                Integer.parseInt(columns[2].trim()),
                Integer.parseInt(columns[3].trim()),
                columns[4].trim()
        );
    }
}
