package io.github.zekerzhayard.stringtransformermod.asm.injectors;

import io.github.zekerzhayard.stringtransformermod.asm.utils.FormatUtils;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

public class InvokeInjector extends AbstractInjector {
    public InvokeInjector(String className, String methodNameDesc, String target, Integer ordinal) {
        super(className, methodNameDesc, target, ordinal, EnumInjectorType.INVOKE);
    }

    @Override
    protected boolean matchTarget(AbstractInsnNode ain) {
        if (ain instanceof MethodInsnNode) {
            MethodInsnNode min = (MethodInsnNode) ain;
            String mappedClassName = FMLDeobfuscatingRemapper.INSTANCE.mapType(min.owner);
            String mappedMethodName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(min.owner, min.name, min.desc);
            String mappedMethodDesc = FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(min.desc);
            return this.target.equals(FormatUtils.getTargetFormat(mappedClassName, mappedMethodName, mappedMethodDesc));
        }
        return false;
    }
}
