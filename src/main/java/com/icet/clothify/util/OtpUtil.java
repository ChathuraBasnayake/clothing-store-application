package com.icet.clothify.util; // Make sure this package is correct for your project

import java.security.SecureRandom;

/**
 * Utility class for generating One-Time Passwords (OTP).
 */
public class OtpUtil {

    private static final int OTP_LENGTH = 6;
    private static final SecureRandom random = new SecureRandom();

    /**
     * Generates a random 6-digit numeric OTP.
     *
     * @return A 6-digit OTP as a String.
     */
    public static String generateOtp() {
        StringBuilder otp = new StringBuilder(OTP_LENGTH);
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10)); // Appends a random digit (0-9)
        }
        return otp.toString();
    }
}
