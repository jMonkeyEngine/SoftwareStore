package com.jayfella.website.config.external;

public class SecurityConfig {

    // 80,000 as of march 2019
    private int pbkdfIterations = 80000;

    // A good rule of thumb is to use a salt that is the same size as the output of the hash function.
    private int passwordHashLength = 512; // in bits
    private int saltHashLength = 512;

    SecurityConfig() {
    }

    public int getPbkdfIterations() {
        return pbkdfIterations;
    }

    public void setPbkdfIterations(int pbkdfIterations) {
        this.pbkdfIterations = pbkdfIterations;
    }

    public int getPasswordHashLength() {
        return passwordHashLength;
    }

    public void setPasswordHashLength(int passwordHashLength) {
        this.passwordHashLength = passwordHashLength;
    }

    public int getSaltHashLength() {
        return saltHashLength;
    }

    public void setSaltHashLength(int saltHashLength) {
        this.saltHashLength = saltHashLength;
    }
}
