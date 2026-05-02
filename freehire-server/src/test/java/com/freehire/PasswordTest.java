package com.freehire;

import cn.hutool.crypto.digest.BCrypt;

public class PasswordTest {
    public static void main(String[] args) {
        String password = "admin123";
        String hash = BCrypt.hashpw(password);
        System.out.println("New Hash: " + hash);
        System.out.println("Verify new: " + BCrypt.checkpw(password, hash));
        
        String oldHash = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKXNR5FNqW4SJbYrWQNEqRFl8U7y";
        System.out.println("Verify old: " + BCrypt.checkpw(password, oldHash));
    }
}
