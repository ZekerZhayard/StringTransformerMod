package io.github.zekerzhayard.stringtransformermod.asm.transformers;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public class ResourcePackRepositoryTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (!transformedName.equals("net.minecraft.client.resources.ResourcePackRepository")) {
            return basicClass;
        }
        ClassNode cn = new ClassNode();
        new ClassReader(basicClass).accept(cn, 0);
        for (MethodNode mn : cn.methods) {
            if (!mn.name.equals("<init>")) {
                continue;
            }
            for (AbstractInsnNode ain : mn.instructions.toArray()) {
                if (ain.getOpcode() != Opcodes.INVOKEINTERFACE) {
                    continue;
                }
                MethodInsnNode min = (MethodInsnNode) ain;
                if (min.owner.equals("java/util/List") && min.name.equals("iterator") && min.desc.equals("()Ljava/util/Iterator;")) {
                    mn.instructions.insertBefore(min, new MethodInsnNode(Opcodes.INVOKESTATIC, "StringTransformerModHook", "addResourcePack", "(Ljava/util/List;)Ljava/util/List;", false));
                    break;
                }
            }
        }
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);
        return cw.toByteArray();
    }
}
