package io.github.zekerzhayard.stringtransformermod.asm.transformers;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import io.github.zekerzhayard.stringtransformermod.asm.config.ConfigLoader;
import io.github.zekerzhayard.stringtransformermod.asm.config.Transformer;
import io.github.zekerzhayard.stringtransformermod.asm.injectors.AbstractInjector;
import io.github.zekerzhayard.stringtransformermod.asm.injectors.EnumInjectorType;
import io.github.zekerzhayard.stringtransformermod.asm.utils.FormatUtils;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class TranslationTransformer implements IClassTransformer {
    private HashMap<String, HashSet<Transformer.Class.Method>> methods = ConfigLoader.getMethods();

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        HashSet<Transformer.Class.Method> methodSet = this.methods.get(transformedName);
        if (methodSet == null) {
            return basicClass;
        }
        ClassNode cn = new ClassNode();
        new ClassReader(basicClass).accept(cn, 0);
        for (MethodNode mn : cn.methods) {
            String mappedMethodName = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(cn.name, mn.name, mn.desc);
            String mappedMethodDesc = FMLDeobfuscatingRemapper.INSTANCE.mapMethodDesc(mn.desc);
            HashMap<String, HashMap<String, HashMap<String, String>>> methods = new HashMap<>();
            for (Transformer.Class.Method method : methodSet) {
                if (!method.getName().equals(mappedMethodName) || !method.getDesc().equals(mappedMethodDesc)) {
                    continue;
                }
                HashMap<String, HashMap<String, String>> types = new HashMap<>();
                for (Map.Entry<EnumInjectorType, HashSet<Transformer.Class.Method.Injector>> entry : method.getInjectors().entrySet()) {
                    try {
                        for (Transformer.Class.Method.Injector injector : entry.getValue()) {
                            HashMap<String, String> translationKeys = new HashMap<>();
                            for (Transformer.Class.Method.Injector.Key key : injector.getKeys()) {
                                translationKeys.put(key.getString(), key.getTranslateKey());
                            }
                            types.put(FormatUtils.getInjectFormat(entry.getKey(), injector.getTarget(), injector.getOrdinal()), translationKeys);
                            AbstractInjector.injectors.get(entry.getKey())
                                .getConstructor(String.class, String.class, String.class, Integer.class)
                                .newInstance(transformedName, FormatUtils.getMethodFormat(mappedMethodName, mappedMethodDesc), injector.getTarget(), injector.getOrdinal())
                                .inject(mn.instructions);
                        }
                    } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
                methods.put(mappedMethodName + mappedMethodDesc, types);
            }
            ConfigLoader.getTranslationMap().put(transformedName, methods);
        }
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        cn.accept(cw);
        return cw.toByteArray();
    }
}
