package helpers;

import lombok.val;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class EncryptionTest {

    @Test
    public void testEncryptionAndDecryption() {

        val plainText = "Some Plain Text";
        val secretKey = "ThisIsASecretKey";

        val encrypted = Encryption.encrypt(plainText, secretKey);
        val decrypted = Encryption.decrypt(encrypted, secretKey);

        System.out.println("[" + Encryption.decrypt("VtwMLkrJ5udJq4KybPrOYA==", secretKey) + "]"); // blank
        System.out.println("[" + Encryption.decrypt("/4eDxNEOhuAY6WkXKcEJcw==", secretKey) + "]"); // 12/12/2017
        System.out.println("[" + Encryption.decrypt("MXe/WK4pjSpfZBovyDU0SA==", secretKey) + "]"); // 08/12/2017

        assertEquals(plainText, decrypted);
        assertNotEquals(plainText, encrypted);
    }
}
