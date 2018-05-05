package com.TheRPGAdventurer.ROTD.client.initialization;

import java.util.BitSet;

import org.lwjgl.input.Keyboard;

import com.TheRPGAdventurer.ROTD.server.network.DragonControlMessage;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

public class ROTDKeyBindings {
	
    public static final String KEY_CATEGORY = "key.categories.gameplay";
    public static  KeyBinding KEY_BREATH;
    
    public static void init() {
    	KEY_BREATH = new KeyBinding("key.dragon.breath", Keyboard.KEY_R, KEY_CATEGORY);
        ClientRegistry.registerKeyBinding(KEY_BREATH);
    	
    }
    
}
