package com.ddi.assessment.news.api.security;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class HashUtils {
    public static String hash(String raw) { return BCrypt.hashpw(raw, BCrypt.gensalt()); }
    public static boolean matches(String raw, String hashed) { return BCrypt.checkpw(raw, hashed); }
}
