package com.mixer.interactive.minecraft.lib;

import net.minecraftforge.fml.common.Mod;

/**
 * Mixer Interactive Minecraft Library.
 *
 * @author      Microsoft Corporation
 *
 * @since       1.0.0
 */
@Mod(modid = MixerInteractiveLib.MOD_ID,
        name = "Mixer Interactive Lib",
        version = "@MOD_VERSION@",
        dependencies = "required-after:forge@[13.19.0,];")
public class MixerInteractiveLib {

    /**
     * Unique mod identifier
     */
    public static final String MOD_ID = "mixer-interactive-lib";

    /**
     * Mod instance
     */
    @Mod.Instance(MOD_ID)
    public static MixerInteractiveLib instance;
}
