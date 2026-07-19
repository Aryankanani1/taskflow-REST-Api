package com.example.taskflow;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class PasswordEncoderTest {

    @Test
    void encodeTwiceAndMatch() {
        PasswordEncoder encoder = new BCryptPasswordEncoder();

        String hash1 = encoder.encode("hello");
        String hash2 = encoder.encode("hello");

        System.out.println("hash1 = " + hash1);
        System.out.println("hash2 = " + hash2);
        System.out.println("hash1.equals(hash2) ? " + hash1.equals(hash2));
        System.out.println("matches(hello, hash1) ? " + encoder.matches("hello", hash1));
        System.out.println("matches(hello, hash2) ? " + encoder.matches("hello", hash2));
        System.out.println("matches(wrong, hash1) ? " + encoder.matches("wrong", hash1));

        // Same input, DIFFERENT hashes (random salt each time)
        assertNotEquals(hash1, hash2);
        // ...yet matches() verifies both correctly
        assertTrue(encoder.matches("hello", hash1));
        assertTrue(encoder.matches("hello", hash2));
        // wrong password fails
        assertFalse(encoder.matches("wrong", hash1));
    }
}
