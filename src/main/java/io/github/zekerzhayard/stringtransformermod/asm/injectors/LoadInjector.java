package io.github.zekerzhayard.stringtransformermod.asm.injectors;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class LoadInjector extends AbstractInjector {
    public LoadInjector(String className, String methodNameDesc, String target, Integer ordinal) {
        super(className, methodNameDesc, target, ordinal, EnumInjectorType.LOAD);
    }

    @Override
    protected boolean matchTarget(AbstractInsnNode ain) {
        return ain.getOpcode() == Opcodes.ALOAD && this.target.equals(String.valueOf(((VarInsnNode) ain).var));
    }
}
