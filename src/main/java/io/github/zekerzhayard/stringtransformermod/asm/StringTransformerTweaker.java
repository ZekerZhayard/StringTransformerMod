package io.github.zekerzhayard.stringtransformermod.asm;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import io.github.zekerzhayard.stringtransformermod.asm.config.ConfigLoader;
import io.github.zekerzhayard.stringtransformermod.asm.transformers.TranslationTransformer;
import net.minecraft.launchwrapper.ITweaker;
import net.minecraft.launchwrapper.LaunchClassLoader;

public class StringTransformerTweaker implements ITweaker {
    @Override
    public void acceptOptions(List<String> args, File gameDir, File assetsDir, String profile) {
        if (gameDir == null) {
            gameDir = new File(".");
        }
        try {
            ConfigLoader.init(new File(gameDir, "config"));
        } catch (IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void injectIntoClassLoader(LaunchClassLoader classLoader) {
        classLoader.registerTransformer(TranslationTransformer.class.getName());
    }

    @Override
    public String getLaunchTarget() {
        return null;
    }

    @Override
    public String[] getLaunchArguments() {
        return new String[0];
    }
}
