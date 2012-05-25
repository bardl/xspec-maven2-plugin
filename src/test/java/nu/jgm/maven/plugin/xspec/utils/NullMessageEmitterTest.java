package nu.jgm.maven.plugin.xspec.utils;

import org.apache.commons.io.output.NullOutputStream;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Joakim Sundqvist
 */
public class NullMessageEmitterTest {

    @Test
    public void testOutputStreamIsSet() throws Exception {
        Assert.assertTrue(new NullMessageEmitter().getOutputStream().getClass().equals(NullOutputStream.class));
    }
}
