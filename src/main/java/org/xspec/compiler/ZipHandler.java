package org.xspec.compiler;

import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class ZipHandler {


    /**
     * @param prefix
     * @param suffix
     * @return
     * @throws IOException
     */
    public static File createTempZipFile(final String prefix, final String suffix) throws IOException {
        final File tempFile = File.createTempFile(prefix, suffix);
        final OutputStream tempOutputStream = new FileOutputStream(tempFile);
        final InputStream inputStream = ZipHandler.class.getClassLoader().getResourceAsStream(prefix + "." + suffix);
        try {
            IOUtils.copy(inputStream, tempOutputStream);
        } finally {
            IOUtils.closeQuietly(inputStream);
            IOUtils.closeQuietly(tempOutputStream);
        }
        return tempFile;
    }

    /**
     * @param tempDir
     * @throws java.io.IOException
     */
    public static void unzipFile(File tempDir, File zipFile) throws IOException {
        ZipFile zf = new ZipFile(zipFile);
        Enumeration e = zf.entries();
        while (e.hasMoreElements()) {
            ZipEntry ze = (ZipEntry) e.nextElement();
            final String fileName = tempDir.getAbsolutePath() + File.separator + ze.getName().replace('/', File.separatorChar).replace('\\', File.separatorChar);
            final File file = new File(fileName);

            if(ze.isDirectory()) {
              file.mkdir();
              continue;
            }


            FileOutputStream fout = null;

            try {
                fout = new FileOutputStream(file);

                IOUtils.copy(zf.getInputStream(ze), fout);
            } catch (IOException i) {

            } finally {
                IOUtils.closeQuietly(fout);
            }

        }
    }

    /**
     * @param files
     */
    public static void createFolders(File... files) {
        for (File f : files) {
            if (!f.exists()) {
                f.mkdirs();
            }
        }

    }
}
