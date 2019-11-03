package io.github.zekerzhayard.stringtransformermod.asm.injectors;

import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LdcInsnNode;

public class LdcInjector extends AbstractInjector {
    public LdcInjector(String className, String methodNameDesc, String target, Integer ordinal) {
        super(className, methodNameDesc, target, ordinal, EnumInjectorType.LDC);
    }

    @Override
    protected boolean matchTarget(AbstractInsnNode ain) {
        return ain instanceof LdcInsnNode && ((LdcInsnNode) ain).cst.equals(this.target);
    }
}
