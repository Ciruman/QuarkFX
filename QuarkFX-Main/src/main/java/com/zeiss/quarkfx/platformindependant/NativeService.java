package com.zeiss.quarkfx.platformindependant;

/**
 * Interface defining platform specific functionality
 */
public interface NativeService {

    /**
     * receive an intent - called only internally
     * @param args information regarding the intent
     * @return an Intent object with transferred data
     */
    IntentP getIntent(String[] args);

    /**
     * sends an intent for transferring data to other applications
     * @param intent the intent containing the data and receiver information
     * @throws ClassNotFoundException
     */
    void sendIntent(IntentP intent) throws ClassNotFoundException;
}
