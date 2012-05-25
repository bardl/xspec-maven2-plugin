package nu.jgm.maven.plugin.xspec.utils;

import net.sf.saxon.event.MessageEmitter;
import net.sf.saxon.trans.XPathException;
import org.apache.commons.io.output.NullOutputStream;

/**
 * MessageEmitter that redirects all messages to /dev/null
 */
public class NullMessageEmitter extends MessageEmitter{
    public NullMessageEmitter() throws XPathException {
        super();
        setOutputStream(NullOutputStream.NULL_OUTPUT_STREAM);
    }
}
