package io.github.zekerzhayard.stringtransformermod.asm.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;

import org.apache.commons.io.FileUtils;

public class LangFileUtils {
    private static HashMap<String, File> langFiles = new HashMap<>();

    public static File getLangFile(String langCode) throws IOException {
        File langFile = langFiles.get(langCode);
        if (langFile != null) {
            return langFile;
        }
        File resourcePackLangDir = new File("./resourcepacks/StringTransformerMod/assets/minecraft/lang");
        if (!resourcePackLangDir.isDirectory() || !resourcePackLangDir.mkdirs()) {
            throw new IOException("Couldn't create resource pack directory!");
        }
        langFile = new File(resourcePackLangDir, langCode + ".lang");
        FileUtils.write(langFile, "");
        langFiles.put(langCode, langFile);
        return langFile;
    }

    public static void writeTranslation(String langCode, String data) throws IOException {
        FileUtils.write(getLangFile(langCode), data, StandardCharsets.UTF_8, true);
    }

    public static void writeTranslation(String langCode, Collection<String> data) throws IOException {
        FileUtils.writeLines(getLangFile(langCode), StandardCharsets.UTF_8.name(), data, true);
    }

    private LangFileUtils() {

    }
}
