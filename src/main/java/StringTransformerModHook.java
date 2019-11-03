import java.util.List;

import io.github.zekerzhayard.stringtransformermod.asm.config.ConfigLoader;
import net.minecraft.client.resources.I18n;

public class StringTransformerModHook {
    public static List<String> addResourcePack(List<String> resourcePacks) {

        return resourcePacks;
    }

    public static String translate(Object object, String className, String methodNameDesc, String injector) {
        if (!(object instanceof String)) {
            throw new RuntimeException("Target is not a String! (" + object.getClass().getName() + ")");
        }
        return I18n.format(ConfigLoader.getTranslationMap().get(className).get(methodNameDesc).get(injector).get(object));
    }
}
