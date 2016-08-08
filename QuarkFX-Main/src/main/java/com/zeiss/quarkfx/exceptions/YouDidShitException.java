package com.zeiss.quarkfx.exceptions;

/**
 * This exception is used when a future programmer does great shit with our code.
 * e.g. when you do something that is clearly forbidden by the documentation but you still do it
 */
public class YouDidShitException extends IllegalStateException {

    /**
     * One does not simply throw an epic exception
     */
    public YouDidShitException() {
        super();
    }

    /**
     * One does not simply throw an epic exception
     * @param message without declaiming the reason
     */
    public YouDidShitException(String message) {
        super(message);
    }

    /**
     * One does not simply throw an epic exception
     * @param message without declaiming the reason
     * @param cause for a brainless failure
     */
    public YouDidShitException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * One does not simply throw an epic exception
     * @param cause for a brainless failure
     */
    public YouDidShitException(Throwable cause) {
        super(cause);
    }
}
