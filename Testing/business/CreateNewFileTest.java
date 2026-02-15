package business;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bll.EditorBO;
import bll.IEditorBO;
import dal.IFacadeDAO;
import dto.Documents;

class CreateNewFileTest {

    private IEditorBO editorBO;
    private StubFacadeDAO stubDAO;

    @BeforeEach
    void setUp() {
        stubDAO = new StubFacadeDAO();
        editorBO = new EditorBO(stubDAO);
    }

    
    @Test
    void testCreateFile_ValidFilenameAndContent_ReturnsTrue() {
        String filename = "testFile.txt";
        String content = "Hello World";
        boolean result = editorBO.createFile(filename, content);
        assertTrue(result, "Creating a file with valid inputs should return true");
        assertTrue(stubDAO.fileExists(filename), "File should be created in the stub DB");
    }

    @Test
    void testCreateFile_EmptyContent_ReturnsTrue() {
        // Assuming creating an empty file is allowed
        String filename = "emptyFile.txt";
        String content = "";
        boolean result = editorBO.createFile(filename, content);
        assertTrue(result, "Creating a file with empty content should return true");
    }


    @Test
    void testCreateFile_FilenameWithSpecialCharacters_ReturnsTrue() {
        String filename = "file_@#$.txt";
        String content = "Content";
        boolean result = editorBO.createFile(filename, content);
        assertTrue(result, "Creating a file with special characters in name should return true");
    }

    @Test
    void testCreateFile_LongFilename_ReturnsTrue() {
        String filename = "this_is_a_very_long_filename_to_test_boundary_limits_of_string_handling.txt";
        String content = "Content";
        boolean result = editorBO.createFile(filename, content);
        assertTrue(result, "Creating a file with a long name should return true");
    }


    @Test
    void testCreateFile_NullFilename_ReturnsFalse() {
        // The implementation catches exceptions and returns false
        boolean result = editorBO.createFile(null, "content");
        assertFalse(result, "Creating a file with null filename should return false (via exception handling)");
    }

    // New Test Case
    @Test
    void testCreateFile_ForceFailure_ReturnsFalse() {
        stubDAO.setForceFailure(true);
        boolean result = editorBO.createFile("fail.txt", "content");
        assertFalse(result, "Should return false when DAO is forced to fail");
    }

    @Test
    void testCreateFile_DatabaseConnectionFailure_ReturnsFalse() {
        stubDAO.setThrowException(true);
        boolean result = editorBO.createFile("db_fail.txt", "content");
        assertFalse(result, "Should return false when DB throws an exception");
    }

    @Test
    void testCreateFile_EmptyFilename_ReturnsFalse() {
        boolean result = editorBO.createFile("", "content");
        assertFalse(result, "Creating file with empty filename should return false");
    }



    private class StubFacadeDAO implements IFacadeDAO {
        private Map<String, String> db = new HashMap<>();
        private boolean throwException = false;
        private boolean forceFailure = false;

        public void setThrowException(boolean throwException) {
            this.throwException = throwException;
        }

        public void setForceFailure(boolean forceFailure) {
            this.forceFailure = forceFailure;
        }

        public boolean fileExists(String filename) {
            return db.containsKey(filename);
        }

        @Override
        public boolean createFileInDB(String nameOfFile, String content) {
            if (throwException) {
                throw new RuntimeException("DB Connection Failed");
            }
            if (forceFailure) {
                return false;
            }
            // Logic for negative cases
            if (nameOfFile == null) {
                throw new IllegalArgumentException("Filename cannot be null");
            }
            if (nameOfFile.trim().isEmpty()) {
                return false; 
            }
            
            db.put(nameOfFile, content);
            return true;
        }


        @Override public boolean updateFileInDB(int id, String fileName, int pageNumber, String content) { return false; }
        @Override public boolean deleteFileInDB(int id) { return false; }
        @Override public List<Documents> getFilesFromDB() { return new ArrayList<>(); }
        @Override public String transliterateInDB(int pageId, String arabicText) { return ""; }
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
