package io.github.zekerzhayard.stringtransformermod.asm.injectors;

import java.util.HashMap;

import io.github.zekerzhayard.stringtransformermod.asm.utils.FormatUtils;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;

public abstract class AbstractInjector {
    public static HashMap<EnumInjectorType, Class<? extends AbstractInjector>> injectors = new HashMap<>();

    protected String className;
    protected String methodNameDesc;
    protected String target;
    protected Integer ordinal;
    protected EnumInjectorType type;
    protected int count;

    public AbstractInjector(String className, String methodNameDesc, String target, Integer ordinal, EnumInjectorType type) {
        this.className = className;
        this.methodNameDesc = methodNameDesc;
        this.target = target;
        this.ordinal = ordinal;
        this.type = type;
    }

    public void inject(InsnList insnList) {
        for (AbstractInsnNode ain : insnList.toArray()) {
            if (this.matchTarget(ain)) {
                if (this.ordinal == null || this.ordinal.equals(count)) {
                    this.inject(insnList, ain);
                }
                this.count++;
            }
        }
    }

    protected abstract boolean matchTarget(AbstractInsnNode ain);

    protected void inject(InsnList insnList, AbstractInsnNode ain) {
        InsnList il = new InsnList();
        il.add(new LdcInsnNode(this.className));
        il.add(new LdcInsnNode(this.methodNameDesc));
        il.add(new LdcInsnNode(FormatUtils.getInjectFormat(this.type, this.target, this.ordinal)));
        il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "StringTransformerModHook", "translate", "(Ljava/lang/Object;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/Object;", false));
        insnList.insert(ain, il);
    }
}
