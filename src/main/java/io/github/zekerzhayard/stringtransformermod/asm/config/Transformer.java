package io.github.zekerzhayard.stringtransformermod.asm.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import com.google.common.base.Strings;
import io.github.zekerzhayard.stringtransformermod.asm.injectors.EnumInjectorType;
import io.github.zekerzhayard.stringtransformermod.asm.utils.LangFileUtils;
import org.apache.commons.io.FileUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Transformer {
    private String id;
    private String defaultLanguage;
    private HashSet<Transformer.Class> classes = new HashSet<>();

    public Transformer(String id, String defaultLanguage, NodeList classes) throws IOException {
        if (Strings.isNullOrEmpty(id)) {
            throw new IllegalArgumentException("Profile ID must be specified!");
        }
        if (Strings.isNullOrEmpty(defaultLanguage)) {
            defaultLanguage = "en_US";
        }

        this.id = id;
        this.defaultLanguage = defaultLanguage;
        for (int i = 0; i < classes.getLength(); i++) {
            Node clazz = classes.item(i);
            if (clazz.getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element) clazz;
                this.classes.add(new Transformer.Class(e.getAttribute("name"), e.getElementsByTagName("class")));
            }
        }
    }

    public String getId() {
        return this.id;
    }

    public String getDefaultLanguage() {
        return this.defaultLanguage;
    }

    public HashSet<Transformer.Class> getClasses() {
        return this.classes;
    }

    public class Class {
        private String name;
        private HashSet<Transformer.Class.Method> methods = new HashSet<>();

        public Class(String name, NodeList methods) throws IOException {
            if (Strings.isNullOrEmpty(name)) {
                throw new IllegalArgumentException("Class name must be specified!");
            }

            this.name = name;
            for (int i = 0; i < methods.getLength(); i++) {
                Node method = methods.item(i);
                if (method.getNodeType() == Node.ELEMENT_NODE) {
                    Element e = (Element) method;
                    HashMap<EnumInjectorType, NodeList> injectors = new HashMap<>();
                    for (EnumInjectorType type : EnumInjectorType.values()) {
                        injectors.put(type, e.getElementsByTagName(type.name().toLowerCase()));
                    }
                    this.methods.add(new Transformer.Class.Method(e.getAttribute("name"), e.getAttribute("desc"), injectors));
                }
            }
        }

        public String getName() {
            return this.name;
        }

        public HashSet<Transformer.Class.Method> getMethods() {
            return this.methods;
        }

        public class Method {
            private String name;
            private String desc;
            private HashMap<EnumInjectorType, HashSet<Transformer.Class.Method.Injector>> injectors = new HashMap<>();

            public Method(String name, String desc, HashMap<EnumInjectorType, NodeList> injectors) throws IOException {
                if (Strings.isNullOrEmpty(name)) {
                    throw new IllegalArgumentException("Method name must be specified!");
                }
                if (Strings.isNullOrEmpty(desc)) {
                    throw new IllegalArgumentException("Method desc must be specified!");
                }

                this.name = name;
                this.desc = desc;
                for (Map.Entry<EnumInjectorType, NodeList> injectorEntry : injectors.entrySet()) {
                    HashSet<Transformer.Class.Method.Injector> set = new HashSet<>();
                    for (int i = 0; i < injectorEntry.getValue().getLength(); i++) {
                        Node injector = injectorEntry.getValue().item(i);
                        if (injector.getNodeType() == Node.ELEMENT_NODE) {
                            Element e = (Element) injector;
                            String ordinalStr = e.getAttribute("oridinal");
                            Integer ordinal = null;
                            if (!Strings.isNullOrEmpty(ordinalStr)) {
                                try {
                                    ordinal = Integer.valueOf(ordinalStr);
                                } catch (NumberFormatException ignored) {

                                }
                            }
                            set.add(new Transformer.Class.Method.Injector(e.getAttribute("target"), ordinal, e.getElementsByTagName(injectorEntry.getKey().name().toLowerCase())));
                        }
                    }
                    this.injectors.put(injectorEntry.getKey(), set);
                }
            }

            public String getName() {
                return this.name;
            }

            public String getDesc() {
                return this.desc;
            }

            public HashMap<EnumInjectorType, HashSet<Transformer.Class.Method.Injector>> getInjectors() {
                return this.injectors;
            }

            public class Injector {
                private String target;
                private Integer ordinal;
                private HashSet<Transformer.Class.Method.Injector.Key> keys = new HashSet<>();

                public Injector(String target, Integer ordinal, NodeList keys) throws IOException {
                    if (Strings.isNullOrEmpty(target)) {
                        throw new IllegalArgumentException("Method injector target must be specified!");
                    }
                    this.target = target;
                    this.ordinal = ordinal;
                    for (int i = 0; i < keys.getLength(); i++) {
                        Node key = keys.item(i);
                        if (key.getNodeType() == Node.ELEMENT_NODE) {
                            String string = ((Element) key).getAttribute("string");
                            if (Strings.isNullOrEmpty(string)) {
                                string = target;
                            }
                            String translateKey = key.getTextContent();
                            this.keys.add(new Transformer.Class.Method.Injector.Key(string, translateKey));
                        }
                    }
                }

                public String getTarget() {
                    return this.target;
                }

                public Integer getOrdinal() {
                    return this.ordinal;
                }

                public HashSet<Transformer.Class.Method.Injector.Key> getKeys() {
                    return this.keys;
                }

                public class Key {
                    private String string;
                    private String translateKey;

                    public Key(String string, String translateKey) throws IOException {
                        if (Strings.isNullOrEmpty(string)) {
                            throw new IllegalArgumentException("Method injector target string must be specified!");
                        }
                        if (Strings.isNullOrEmpty(translateKey)) {
                            throw new IllegalArgumentException("Method injector target translation key must be specified!");
                        }
                        this.string = string;
                        this.translateKey = translateKey;
                        LangFileUtils.writeTranslation(Transformer.this.getDefaultLanguage(), translateKey + "=" + string);
                    }

                    public String getString() {
                        return this.string;
                    }

                    public String getTranslateKey() {
                        return this.translateKey;
                    }
                }
            }
        }
    }
}
