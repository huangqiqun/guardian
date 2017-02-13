package com.leadbank.guardian.util;

public interface RandomStringGenerator {

    int getMinLength();

    int getMaxLength();

    String getNewString();
    
    byte[] getNewStringAsBytes();
}
