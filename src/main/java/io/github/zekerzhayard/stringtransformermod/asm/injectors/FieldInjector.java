package io.github.zekerzhayard.stringtransformermod.asm.injectors;

import io.github.zekerzhayard.stringtransformermod.asm.utils.FormatUtils;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;

public class FieldInjector extends AbstractInjector {
    public FieldInjector(String className, String methodNameDesc, String target, Integer ordinal) {
        super(className, methodNameDesc, target, ordinal, EnumInjectorType.FIELD);
    }

    @Override
    protected boolean matchTarget(AbstractInsnNode ain) {
        if (ain instanceof FieldInsnNode) {
            FieldInsnNode fin = (FieldInsnNode) ain;
            String mappedClassName = FMLDeobfuscatingRemapper.INSTANCE.mapType(fin.owner);
            String mappedFieldName = FMLDeobfuscatingRemapper.INSTANCE.mapFieldName(fin.owner, fin.name, fin.desc);
            String mappedFieldDesc = FMLDeobfuscatingRemapper.INSTANCE.mapDesc(fin.desc);
            return this.target.equals(FormatUtils.getTargetFormat(mappedClassName, mappedFieldName, mappedFieldDesc));
        }
        return false;
    }
}
