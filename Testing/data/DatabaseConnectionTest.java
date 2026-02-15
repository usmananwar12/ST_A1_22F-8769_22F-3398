package data;

import static org.junit.jupiter.api.Assertions.*;

import dal.DatabaseConnection;


import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

import org.junit.jupiter.api.Test;

class DatabaseConnectionTest {

    @Test
    void testSingletonInstance_SameReference() {
        // 1. Get first instance
        DatabaseConnection instance1 = DatabaseConnection.getInstance();
        assertNotNull(instance1, "Instance should not be null");

        // 2. Get second instance
        DatabaseConnection instance2 = DatabaseConnection.getInstance();
        assertNotNull(instance2, "Instance should not be null");

        // 3. Verify they are the same object
        assertSame(instance1, instance2, "getInstance() should always return the same object reference");
    }

    @Test
    void testSingletonConstructor_IsPrivate() throws NoSuchMethodException {
        Constructor<DatabaseConnection> constructor = DatabaseConnection.class.getDeclaredConstructor();
        
        assertTrue(Modifier.isPrivate(constructor.getModifiers()), "Constructor should be private to enforce Singleton pattern");
    }
    
    @Test
    void testConnectionObject_IsAvailable() {
        DatabaseConnection instance = DatabaseConnection.getInstance();
        
        assertDoesNotThrow(() -> instance.getConnection());
    }
}
