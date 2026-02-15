package business;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import bll.EditorBO;
import bll.IEditorBO;
import dal.IFacadeDAO;
import dto.Documents;

class ImportTest {

    private IEditorBO editorBO;
    private StubFacadeDAO stubDAO;
    private File tempFile;

    @BeforeEach
    void setUp() throws IOException {
        stubDAO = new StubFacadeDAO();
        editorBO = new EditorBO(stubDAO);
        
        tempFile = File.createTempFile("testImport", ".txt");
    }

    @AfterEach
    void tearDown() {
        if (tempFile != null && tempFile.exists()) {
            tempFile.delete();
        }
    }

    @Test
    void testImportTextFile_ValidTxtFile_ReturnsTrue() throws IOException {
        
        String content = "Hello World\nThis is a test file.";
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write(content);
        }

        // Test import logic
        boolean result = editorBO.importTextFiles(tempFile, "imported_doc.txt");
        
        assertTrue(result, "Importing a valid .txt file should return true");
        assertTrue(stubDAO.fileExists("imported_doc.txt"), "File should be created in DB");
        assertEquals(content + "\n", stubDAO.getFileContent("imported_doc.txt"), "Content should match (lines appended with newline)");
    }

    @Test
    void testImportTextFile_ValidMd5File_ReturnsTrue() throws IOException {
        
        File md5File = File.createTempFile("testImport", ".md5");
        try (FileWriter writer = new FileWriter(md5File)) {
            writer.write("MD5 Content");
        }
        
        boolean result = editorBO.importTextFiles(md5File, "imported.md5");
        
        assertTrue(result, "Importing a valid .md5 file should return true");
        assertTrue(stubDAO.fileExists("imported.md5"), "File should be created in DB");
        
        md5File.delete();
    }
   

    @Test
    void testImportTextFile_InvalidExtension_ReturnsFalse() throws IOException {
        // create file with unsupported extension
        File invalidFile = File.createTempFile("testImport", ".pdf");
        
        boolean result = editorBO.importTextFiles(invalidFile, "doc.pdf");
        
        assertFalse(result, "Importing unsupported file extension should return false");
        assertFalse(stubDAO.fileExists("doc.pdf"), "File should NOT be created in DB");
        
        invalidFile.delete();
    }
    
    @Test
    void testImportTextFile_NonExistentFile_ReturnsFalse() {
        File missingFile = new File("non_existent_file.txt");
        boolean result = editorBO.importTextFiles(missingFile, "missing.txt");
        
        assertFalse(result, "Importing missing file should handle exception and return false");
    }
    
    @Test
    void testImportTextFile_EmptyFile_ReturnsTrue() throws IOException {
         // Create empty file
         
         boolean result = editorBO.importTextFiles(tempFile, "empty.txt");
         assertTrue(result, "Importing empty file should be allowed (creates empty document)");
         assertTrue(stubDAO.fileExists("empty.txt"));
    }

    private class StubFacadeDAO implements IFacadeDAO {
        private Map<String, String> db = new HashMap<>();

        public boolean fileExists(String filename) {
            return db.containsKey(filename);
        }
        
        public String getFileContent(String filename) {
            return db.get(filename);
        }

        @Override
        public boolean createFileInDB(String nameOfFile, String content) {
            // Simulate DB Creation
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
