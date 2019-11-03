package io.github.zekerzhayard.stringtransformermod.asm.config;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import io.github.zekerzhayard.stringtransformermod.asm.utils.LangFileUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class ConfigLoader {
    private final static Logger LOGGER = LogManager.getLogger();
    private static boolean isInitialized = false;
    private static HashMap<String, Transformer> transformers = new HashMap<>();
    private static ConcurrentHashMap<String, HashMap<String, HashMap<String, HashMap<String, String>>>> translationMap = new ConcurrentHashMap<>();

    public static void init(File configDir) throws IOException, ParserConfigurationException {
        if (isInitialized) {
            return;
        }
        isInitialized = true;

        if (!configDir.mkdirs() && !configDir.isDirectory()) {
            throw new IOException("\"{}\" already exists and cannot be created as a folder!");
        }

        HashMap<String, File> ids = new HashMap<>();
        for (File configXML : FileUtils.listFiles(configDir, new String[] {"xml"}, false)) {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document;
            try {
                document = builder.parse(configXML);
            } catch (SAXException e) {
                e.printStackTrace();
                continue;
            }

            Element element = document.getDocumentElement();
            String id = element.getAttribute("id");
            if (ids.containsKey(id)) {
                LOGGER.warn("Profile ID \"{}\" in \"{}\" already exists in \"{}\"!", id, configXML.getAbsolutePath(), ids.get(id).getAbsolutePath());
                continue;
            }

            try {
                transformers.put(configXML.getAbsolutePath(), new Transformer(id, element.getAttribute("defaultLanguage"), element.getElementsByTagName("class")));
                File configXMLDir = new File(StringUtils.substringBeforeLast(configXML.getAbsolutePath(), "."));
                if (configXMLDir.isDirectory()) {
                    for (File langFile : FileUtils.listFiles(configXMLDir, new String[] {"lang"}, false)) {
                        LangFileUtils.writeTranslation(StringUtils.substringBeforeLast(langFile.getName(), "."), FileUtils.readLines(langFile, StandardCharsets.UTF_8));
                    }
                }
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(e.getMessage() + " (" + configXML.getAbsolutePath() + ")");
            } catch (IOException e) {
                LOGGER.error(e.getMessage() + " (" + configXML.getAbsolutePath() + ")", e);
            }
            ids.put(id, configXML);
        }
    }

    public static HashMap<String, Transformer> getTransformers() {
        return transformers;
    }

    public static ConcurrentHashMap<String, HashMap<String, HashMap<String, HashMap<String, String>>>> getTranslationMap() {
        return translationMap;
    }

    public static HashMap<String, HashSet<Transformer.Class.Method>> getMethods() {
        HashMap<String, HashSet<Transformer.Class.Method>> map = new HashMap<>();
        for (Transformer transformer : transformers.values()) {
            for (Transformer.Class clazz : transformer.getClasses()) {
                HashSet<Transformer.Class.Method> set = map.get(clazz.getName());
                if (set == null) {
                    set = new HashSet<>();
                }
                set.addAll(clazz.getMethods());
                map.put(clazz.getName(), set);
            }
        }
        return map;
    }
}
