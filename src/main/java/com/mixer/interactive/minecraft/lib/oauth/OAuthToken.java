package com.mixer.interactive.minecraft.lib.oauth;

import com.google.gson.annotations.SerializedName;

/**
 * OAuth token.
 *
 * @author      Microsoft Corporation
 *
 * @since       1.0.0
 */
public class OAuthToken {

    @SerializedName("access_token")
    public String accessToken;

    @SerializedName("expires_in")
    public int expiresIn;

    @SerializedName("refresh_token")
    public String refreshToken;

    @SerializedName("token_type")
    public String tokenType;
}
