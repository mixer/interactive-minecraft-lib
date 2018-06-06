package com.mixer.interactive.minecraft.lib.util;

import com.mixer.interactive.GameClient;
import com.mixer.interactive.minecraft.lib.oauth.OAuthToken;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Base64;

/**
 * Provides support for reading/writing OAuth tokens to file.
 *
 * @author      Microsoft Corporation
 *
 * @since       1.0.0
 */
public class FileHelper {

    /**
     * Logger
     */
    private static final Logger LOG = LogManager.getLogger();

    /**
     * Writes the provided OAuth token to the provided file location.
     *
     * @param   token
     *          OAuthToken
     *
     * @return  <code>true</code> if the token was written to file, <code>false</code> otherwise.
     *
     * @since   1.0.0
     */
    public static boolean writeTokenToFile(String tokenFilePath, OAuthToken token) {
        if (tokenFilePath != null && !tokenFilePath.isEmpty() && token != null) {
            try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(tokenFilePath))) {
                outputStream.write(Base64.getEncoder().encode(GameClient.GSON.toJson(token).getBytes()));
                return true;
            }
            catch (IOException e) {
                LOG.error(e);
            }
        }

        return false;
    }

    /**
     * Reads a OAuth token from the provided file location.
     *
     * @return  An <code>OAuthToken</code> from the file at the provided location, <code>null</code> otherwise.
     *
     * @since   1.0.0
     */
    public static OAuthToken readTokenFile(String tokenFilePath) {
        if (tokenFilePath != null) {
            File tokenFile = new File(tokenFilePath);
            try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(tokenFile))) {
                byte[] result = new byte[(int) tokenFile.length()];
                inputStream.read(result);
                String resultString = new String(Base64.getDecoder().decode(result));
                return GameClient.GSON.fromJson(resultString, OAuthToken.class);
            }
            catch (IOException e) {
                LOG.error(e);
            }
        }
        return null;
    }
}
