package io.github.zekerzhayard.stringtransformermod.asm.injectors;

import java.util.HashMap;

public enum EnumInjectorType {
    LDC(LdcInjector.class),
    FIELD(FieldInjector.class),
    INVOKE(InvokeInjector.class),
    LOAD(LoadInjector.class);

    private EnumInjectorType(Class<? extends AbstractInjector> clazz) {
        AbstractInjector.injectors.put(this, clazz);
    }
}
