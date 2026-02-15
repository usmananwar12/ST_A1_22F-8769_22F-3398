package business;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bll.EditorBO;
import bll.IEditorBO;
import dal.IFacadeDAO;
import dto.Documents;

class TransliterateTest {

    private IEditorBO editorBO;
    private StubFacadeDAO stubDAO;

    @BeforeEach
    void setUp() {
        stubDAO = new StubFacadeDAO();
        editorBO = new EditorBO(stubDAO);
    }

    @Test
    void testTransliterate_ValidArabicText_ReturnsTransliteratedString() {
        String arabicText = "مرحبا"; // "Marhaba"
        String expectedRoman = "Marhaba";
        
        stubDAO.setTransliterationResult(expectedRoman);

        String result = editorBO.transliterate(1, arabicText);
        
        assertEquals(expectedRoman, result, "Should return transliterated text from DAO");
        assertTrue(stubDAO.wasTransliterateCalled(), "DAO method should be called");
        assertEquals(arabicText, stubDAO.getLastArabicInput(), "DAO should receive correct arabic text");
    }

    @Test
    void testTransliterate_EmptyText_ReturnsEmptyString() {
        String arabicText = "";
        stubDAO.setTransliterationResult("");
        
        String result = editorBO.transliterate(1, arabicText);
        
        assertEquals("", result, "Should return empty string for empty input");
    }

    @Test
    void testTransliterate_NullText_ReturnsStubDefault() {
        String result = editorBO.transliterate(1, null);
        
        assertEquals("", result, "Should handle null gracefully (based on stub behavior)");
    }

    private class StubFacadeDAO implements IFacadeDAO {
        private String transliterationResult = "";
        private boolean wasCalled = false;
        private String lastArabicInput = null;

        public void setTransliterationResult(String result) {
            this.transliterationResult = result;
        }

        public boolean wasTransliterateCalled() {
            return wasCalled;
        }

        public String getLastArabicInput() {
            return lastArabicInput;
        }

        @Override
        public String transliterateInDB(int pageId, String arabicText) {
            this.wasCalled = true;
            this.lastArabicInput = arabicText;
            return transliterationResult;
        }

        @Override public boolean createFileInDB(String nameOfFile, String content) { return false; }
        @Override public boolean updateFileInDB(int id, String fileName, int pageNumber, String content) { return false; }
        @Override public boolean deleteFileInDB(int id) { return false; }
        @Override public List<Documents> getFilesFromDB() { return new ArrayList<>(); }
        @Override public Map<String, String> lemmatizeWords(String text) { return null; }
        @Override public Map<String, List<String>> extractPOS(String text) { return null; }
        @Override public Map<String, String> extractRoots(String text) { return null; }
        @Override public double performTFIDF(List<String> unSelectedDocsContent, String selectedDocContent) { return 0; }
        @Override public Map<String, Double> performPMI(String content) { return null; }
        @Override public Map<String, Double> performPKL(String content) { return null; }
        @Override public Map<String, String> stemWords(String text) { return null; }
        @Override public Map<String, String> segmentWords(String text) { return null; }
    }
}

