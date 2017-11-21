package com.mixer.interactive.minecraft.lib.oauth;

import com.mixer.interactive.GameClient;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.common.ForgeHooks;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

/**
 * Helper methods for retrieving OAuthTokens using the Mixer shortcode authentication process.
 *
 * @author      Microsoft Corporation
 *
 * @since       1.0.0
 */
public class OAuthHelper {

    /**
     * Logger
     */
    private static final Logger LOG = LogManager.getLogger();

    /**
     * Mixer Interactive Minecraft OAuth Client Id
     */
    private static final String CLIENT_ID = "0f7b2674df1fdfbbfb3ad86f751a0057c3a6267888875f1f";

    /**
     * URL paths
     */
    private static final String BASE_PATH = "https://mixer.com/api/v1";
    private static final String SHORT_CODE_URL = BASE_PATH + "/oauth/shortcode";
    private static final String OAUTH_TOKEN_URL = BASE_PATH + "/oauth/token";
    private static final String HANDLE_CHECK_URL = SHORT_CODE_URL + "/check/";

    /**
     * Accept JSON Header
     */
    private static final BasicHeader ACCEPT_JSON = new BasicHeader("Accept", "application/json, */*");

    /**
     * Request formats
     */
    private static final String SHORT_CODE_REQUEST_FORMAT = "{ \"client_id\": \"%s\", \"scope\": \"interactive:robot:self\" }";
    private static final String OAUTH_TOKEN_REQUEST_FORMAT = "{ \"client_id\": \"%s\", \"code\": \"%s\", \"grant_type\": \"authorization_code\" }";
    private static final String REFRESH_TOKEN_REQUEST_FORMAT = "{ \"refresh_token\": \"%s\", \"grant_type\": \"refresh_token\", \"client_id\": \"%s\"}";

    /**
     * Private constructor to prevent instantiation
     *
     * @since   1.0.0
     */
    private OAuthHelper() {
        // NO-OP
    }

    /**
     * Retrieves an OAuthToken using the Mixer shortcode authentication process.
     *
     * @param   requester
     *          Requester of the OAuth token
     *
     * @return  A CompletableFuture that when complete will return an <code>OAuthToken</code> if one could be retrieved, null
     *          otherwise.
     *
     * @since   1.0.0
     */
    public static CompletableFuture<OAuthToken> getOAuthToken(ICommandSender requester) {
        return CompletableFuture.supplyAsync(() -> getShortCode())
                .thenCompose(shortCode -> CompletableFuture.supplyAsync(() -> getHandleCode(requester, shortCode)))
                .thenCompose(handleCode -> CompletableFuture.supplyAsync(() -> getOAuthToken(handleCode)))
                .exceptionally(throwable -> {
                    LOG.error(throwable);
                    return null;
                });
    }

    /**
     * Renews an OAuthToken.
     *
     * @param   token
     *          OAuthToken for renewal
     *
     * @return  A new OAuthToken
     *
     * @since   1.0.0
     */
    public static CompletableFuture<OAuthToken> renewOAuthToken(OAuthToken token) {
        return CompletableFuture.supplyAsync(() -> refreshOAuthToken(token))
                .exceptionally(throwable -> {
                    LOG.error(throwable);
                    return null;
                });
    }

    /**
     * Retrieves a <code>ShortCode</code>.
     *
     * @return  A <code>ShortCode</code>
     *
     * @since   1.0.0
     */
    private static ShortCode getShortCode() {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(SHORT_CODE_URL);
            post.addHeader(ACCEPT_JSON);
            post.setEntity(new StringEntity(String.format(SHORT_CODE_REQUEST_FORMAT, CLIENT_ID)));
            return GameClient.GSON.fromJson(EntityUtils.toString(httpClient.execute(post).getEntity()), ShortCode.class);
        }
        catch (IOException e) {
            LOG.error(e);
        }
        return null;
    }

    /**
     * Provided a shortcode, prompts the requester to complete the shortcode portion of the authentication
     * process and polls for a handle code indicating that the requester has completed their step.
     *
     * @param   requester
     *          Requester of the OAuthToken
     * @param   shortCode
     *          ShortCode
     *
     * @return  A <code>HandleCode</code>
     *
     * @since   1.0.0
     */
    private static HandleCode getHandleCode(ICommandSender requester, ShortCode shortCode) {
        if (shortCode != null) {
            ITextComponent textComponent = ForgeHooks.newChatWithLinks("Alright! Head over to https://mixer.com/go and enter code ");
            TextComponentString shortCodeComponent = new TextComponentString(shortCode.code);
            shortCodeComponent.getStyle().setColor(TextFormatting.DARK_AQUA);
            textComponent.appendSibling(shortCodeComponent);
            requester.addChatMessage(textComponent);


            int runCount = 0;
            while (runCount <= shortCode.expiresIn) {
                try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                    HttpResponse httpResponse = httpClient.execute(new HttpGet(HANDLE_CHECK_URL + shortCode.handle));
                    if (httpResponse.getStatusLine().getStatusCode() == 200) {
                        return GameClient.GSON.fromJson(EntityUtils.toString(httpResponse.getEntity()), HandleCode.class);
                    }
                    Thread.sleep(1000);
                    runCount++;
                }
                catch (IOException | InterruptedException e) {
                    LOG.error(e);
                }
            }
        }

        return null;
    }

    /**
     * Given the provided handle code, retrieves the OAuthToken from Mixer.
     *
     * @param   handleCode
     *          HandleCode
     *
     * @return  OAuthToken
     *
     * @since   1.0.0
     */
    private static OAuthToken getOAuthToken(HandleCode handleCode) {
        if (handleCode == null) {
            throw new RuntimeException("No handle code");
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(OAUTH_TOKEN_URL);
            post.addHeader(ACCEPT_JSON);
            post.setEntity(new StringEntity(String.format(OAUTH_TOKEN_REQUEST_FORMAT, CLIENT_ID, handleCode.code)));
            return GameClient.GSON.fromJson(EntityUtils.toString(httpClient.execute(post).getEntity()), OAuthToken.class);
        }
        catch (IOException e) {
            LOG.error(e);
        }

        return null;
    }

    /**
     * Refreshes an OAuthToken, returning a new OAuthToken.
     *
     * @param   token
     *          OAuthToken to be refreshed
     *
     * @return  A new OAuthToken
     *
     * @since   1.0.0
     */
    private static OAuthToken refreshOAuthToken(OAuthToken token) {
        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(OAUTH_TOKEN_URL);
            post.addHeader(ACCEPT_JSON);
            post.setEntity(new StringEntity(String.format(REFRESH_TOKEN_REQUEST_FORMAT, token.refreshToken, CLIENT_ID)));
            return GameClient.GSON.fromJson(EntityUtils.toString(httpClient.execute(post).getEntity()), OAuthToken.class);
        }
        catch (IOException e) {
            LOG.error(e);
        }

        return null;
    }
}
