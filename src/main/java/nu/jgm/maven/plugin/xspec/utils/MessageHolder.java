package nu.jgm.maven.plugin.xspec.utils;

/**
 * @author Joakim Sundqvist
 */
public class MessageHolder {

    private static boolean ignoreErrors = false;


    public static void setIgnoreErrors(boolean ignoreErrors) {
        MessageHolder.ignoreErrors = ignoreErrors;
    }

    public static boolean isIgnoreErrors() {
        return ignoreErrors;
    }
}
