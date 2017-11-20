package com.mixer.interactive.minecraft.lib.oauth;

import com.google.gson.annotations.SerializedName;

/**
 * Short code for use as part of the shortcode OAuth process.
 *
 * @author      Microsoft Corporation
 *
 * @since       1.0.0
 */
public class ShortCode {

    @SerializedName("code")
    public String code;

    @SerializedName("expires_in")
    public int expiresIn;

    public String handle;
}
