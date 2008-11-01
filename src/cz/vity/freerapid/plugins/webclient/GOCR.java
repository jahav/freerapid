package cz.vity.freerapid.plugins.webclient;

import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.PNMEncodeParam;
import cz.vity.freerapid.utilities.LogUtils;
import cz.vity.freerapid.utilities.Utils;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Scanner;
import java.util.logging.Logger;

/**
 * @author Ladislav Vitasek
 */
class GOCR {
    private final static Logger logger = Logger.getLogger(GOCR.class.getName());

    private final BufferedImage image;
    private final String commandLineOptions;
    private final static String PATH_WINDOWS = "tools/gocr/gocr046.exe";
    private final static String PATH_LINUX = "gocr";

    GOCR(BufferedImage image, String commandLineOptions) {

        this.image = image;
        this.commandLineOptions = commandLineOptions;
    }


    String recognize() throws IOException {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        final ImageEncoder encoder = ImageCodec.createImageEncoder("PNM", out, new PNMEncodeParam());

        encoder.encode(image);

        Scanner scanner = null;
        OutputStream processOut = null;
        final String command;
        if (Utils.isWindows()) {
            command = Utils.addFileSeparator(Utils.getAppPath()) + PATH_WINDOWS;
        } else {
            command = PATH_LINUX;
            try {
                final Process process = Runtime.getRuntime().exec(command);
                process.waitFor();
                if (process.exitValue() != 0)
                    return null;
            } catch (Exception e) {
                return null;
            }
        }

        try {
            final Process process = Runtime.getRuntime().exec(command + " " + commandLineOptions + " -f ASCII -");
            processOut = process.getOutputStream();
            processOut.write(out.toByteArray());
            processOut.flush();

            scanner = new Scanner(process.getInputStream());
            final String s = scanner.next();
            process.waitFor();
            if (process.exitValue() != 0)
                throw new IOException("Process exited abnormally");
            return s;
        } catch (Exception e) {
            LogUtils.processException(logger, e);
            throw new IOException(e);
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                LogUtils.processException(logger, e);
            }

            if (processOut != null) {
                try {
                    processOut.close();
                } catch (IOException e) {
                    LogUtils.processException(logger, e);
                }
            }

            if (scanner != null)
                try {
                    scanner.close();
                } catch (Exception e) {
                    LogUtils.processException(logger, e);
                }
        }
    }
}