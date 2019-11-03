package io.github.zekerzhayard.stringtransformermod;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;

public class StringTransformerMod extends DummyModContainer {
    public StringTransformerMod() {
        super(new ModMetadata());
        ModMetadata md = this.getMetadata();
        md.modId = "stringtransformermod";
        md.name = "StringTransformerMod";
        md.version = "@VERSION@";
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        bus.register(this);
        return true;
    }

    @Subscribe
    public void onConstruct(FMLConstructionEvent event) {

    }
}
