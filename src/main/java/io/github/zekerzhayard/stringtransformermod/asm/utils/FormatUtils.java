package io.github.zekerzhayard.stringtransformermod.asm.utils;

import io.github.zekerzhayard.stringtransformermod.asm.injectors.EnumInjectorType;

public class FormatUtils {
    public static String getInjectFormat(EnumInjectorType type, String target, Integer ordinal) {
        return String.format("%s#%s#%s", type.name(), target, ordinal);
    }

    public static String getTargetFormat(String owner, String name, String desc) {
        return String.format("%s.%s:%s", owner, name, desc);
    }

    public static String getMethodFormat(String name, String desc) {
        return name + desc;
    }

    private FormatUtils() {

    }
}
