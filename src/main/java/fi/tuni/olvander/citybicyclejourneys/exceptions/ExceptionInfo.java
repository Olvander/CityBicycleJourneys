package fi.tuni.olvander.citybicyclejourneys.exceptions;

/**
 * A class for Exception information to be shown in a Response Entity.
 *
 * @author  Olli Pertovaara
 * @version 2023.12.13
 * @since   1.21
 */
public class ExceptionInfo {

    /**
     * A String having the exception information.
     */
    private String exceptionInfo;

    /**
     * A constructor that saves the exception information message.
     *
     * @param exceptionInfo The information about the exception
     */
    public ExceptionInfo(String exceptionInfo) {
        this.exceptionInfo = exceptionInfo;
    }

    /**
     * A getter method for the exception information.
     *
     * @return The String containing the exception information
     */
    public String getExceptionInfo() {
        return exceptionInfo;
    }

    /**
     * A setter method for the exception information.
     *
     * @param exceptionInfo The exception information in String format
     */
    public void setExceptionInfo(String exceptionInfo) {
        this.exceptionInfo = exceptionInfo;
    }
}
