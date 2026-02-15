package dal;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

class HashingIntegrityTest {

    private class MockEditorDBDAO {
        private Map<Integer, String> fileHashes = new HashMap<>();
        private Map<Integer, String> fileContents = new HashMap<>();
        private int currentId = 1;

        public int createFile(String content) throws Exception {
            int id = currentId++;
            String hash = HashCalculator.calculateHash(content);
            fileHashes.put(id, hash);
            fileContents.put(id, content);
            return id;
        }

        public void updateFile(int id, String newContent) {
            if (fileHashes.containsKey(id)) {
                fileContents.put(id, newContent);
            }
        }

        public String getStoredHash(int id) {
            return fileHashes.get(id);
        }

        public String getStoredContent(int id) {
            return fileContents.get(id);
        }
    }


    @Test
    void testHashingIntegrity_ContentChanged_SessionHashChanges_OriginalHashRetained() throws Exception {
        MockEditorDBDAO mockDAO = new MockEditorDBDAO();
        
        // 1. Import Original File
        String originalContent = "Original Content";
        int fileId = mockDAO.createFile(originalContent);
        String originalHash = mockDAO.getStoredHash(fileId);
        
        assertNotNull(originalHash, "Hash should be generated on creation");

        // 2. Simulate Editing Session
        String modifiedContent = "Original Content Edited";
        String currentSessionHash = HashCalculator.calculateHash(modifiedContent);

        // 3. Update File in Persistence Layer
        mockDAO.updateFile(fileId, modifiedContent);
        
        // 4. Verify Integrity Logic
        
        // Assertion A: Current session hash should be DIFFERENT from original hash
        assertNotEquals(originalHash, currentSessionHash, "Editing file should change the current session hash");
        
        // Assertion B: Database metadata should RETAIN the original import hash
        String storedHashAfterUpdate = mockDAO.getStoredHash(fileId);
        assertEquals(originalHash, storedHashAfterUpdate, "Database should retain the original import hash after update");
        
        // Assertion C: Content is actually updated
        assertEquals(modifiedContent, mockDAO.getStoredContent(fileId));
    }
    
    @Test
    void testHashCalculator_SameContent_ReturnsSameHash() throws Exception {
        String content = "Test Content";
        String hash1 = HashCalculator.calculateHash(content);
        String hash2 = HashCalculator.calculateHash(content);
        
        assertEquals(hash1, hash2, "HashCalculator should be deterministic");
    }
    
    @Test
    void testHashCalculator_CommonMD5Check() throws Exception {
        // MD5("hello") = 5D41402ABC4B2A76B9719D911017C592
        String content = "hello";
        String expectedHash = "5D41402ABC4B2A76B9719D911017C592";
        String actualHash = HashCalculator.calculateHash(content);
        
        assertEquals(expectedHash, actualHash, "Should match standard MD5 output");
    }
}
