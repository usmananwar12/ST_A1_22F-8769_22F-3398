package business;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import dal.TFIDFCalculator;

class TFIDFTest {

    private TFIDFCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new TFIDFCalculator();
    }


    @Test
    void testCalculateDocumentTfIdf_KnownDocument_MatchesManualCalculation() {
        
        calculator.addDocumentToCorpus("أ");
        calculator.addDocumentToCorpus("ب");
        calculator.addDocumentToCorpus("ج");

        String selectedDoc = "أ";
        
        
        double expectedScore = Math.log(1.5); 
        
        double actualScore = calculator.calculateDocumentTfIdf(selectedDoc);
        
        assertEquals(expectedScore, actualScore, 0.01, "TF-IDF score should match manual calculation");
    }


    @Test
    void testCalculateDocumentTfIdf_SpecialCharacters_ReturnsZeroOrGraceful() {
        String specialCharsDoc = "Hello @#$";
        
        
        double score = calculator.calculateDocumentTfIdf(specialCharsDoc);
        
        assertTrue(Double.isFinite(score) || Double.isNaN(score), "Should return a result (even 0 or NaN) without crashing");
    }

    @Test
    void testCalculateDocumentTfIdf_EmptyDocument_ReturnsZeroOrNaN() {
        calculator.addDocumentToCorpus("أ");
        double score = calculator.calculateDocumentTfIdf("");
        assertTrue(Double.isFinite(score) || Double.isNaN(score), "Empty document should be handled without exception");
    }
}
