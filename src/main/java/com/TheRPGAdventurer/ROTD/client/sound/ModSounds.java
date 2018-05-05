package com.TheRPGAdventurer.ROTD.client.sound;

import com.TheRPGAdventurer.ROTD.RealmOfTheDragons;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

@ObjectHolder(RealmOfTheDragons.MODID)
public class ModSounds {
	
	@ObjectHolder("mob.dragon.step")
	public static final SoundEvent ENTITY_DRAGON_STEP = createSoundEvent("mob.dragon.step");

	@ObjectHolder("mob.dragon.breathe")
	public static final SoundEvent ENTITY_DRAGON_BREATHE = createSoundEvent("mob.dragon.breathe");

	@ObjectHolder("mob.dragon.death")
	public static final SoundEvent ENTITY_DRAGON_DEATH = createSoundEvent("mob.dragon.death");
	
	@ObjectHolder("mob.dragon.growl")
	public static final SoundEvent ENTITY_DRAGON_GROWL = createSoundEvent("mob.dragon.growl");
	
	@ObjectHolder("mob.dragon.nethergrowl")
	public static final SoundEvent ENTITY_NETHER_DRAGON_GROWL = createSoundEvent("mob.dragon.nethergrowl");
	
	@ObjectHolder("mob.dragon.skeletongrowl")
	public static final SoundEvent ENTITY_SKELETON_DRAGON_GROWL = createSoundEvent("mob.dragon.skeletongrowl");
	
	@ObjectHolder("mob.dragon.breathweapon.adultbreathefirestart")
	public static final SoundEvent ENTITY_DRAGON_BREATHSTART = createSoundEvent("mob.dragon.breathweapon.adultbreathefirestart");
	
	@ObjectHolder("mob.dragon.breathweapon.adultbreathefireloop")
	public static final SoundEvent ENTITY_DRAGON_BREATHLOOP = createSoundEvent("mob.dragon.breathweapon.adultbreathefireloop");
	
	@ObjectHolder("mob.dragon.breathweapon.adultbreathefirestop")
	public static final SoundEvent ENTITY_DRAGON_BREATHSTOP = createSoundEvent("mob.dragon.breathweapon.adultbreathefirestop");

	private static SoundEvent createSoundEvent(final String soundName) {
		final ResourceLocation soundID = new ResourceLocation(RealmOfTheDragons.MODID, soundName);
		return new SoundEvent(soundID).setRegistryName(soundID);
	}

	@Mod.EventBusSubscriber(modid = RealmOfTheDragons.MODID)
	public static class RegistrationHandler {
		@SubscribeEvent
		public static void registerSoundEvents(final RegistryEvent.Register<SoundEvent> event) {
			event.getRegistry().registerAll(
					ENTITY_DRAGON_STEP,
					ENTITY_DRAGON_BREATHE,
					ENTITY_DRAGON_DEATH,
					ENTITY_DRAGON_GROWL,
					ENTITY_NETHER_DRAGON_GROWL,
					ENTITY_SKELETON_DRAGON_GROWL,
					ENTITY_DRAGON_BREATHSTART,
					ENTITY_DRAGON_BREATHLOOP,
					ENTITY_DRAGON_BREATHSTOP
			);
		}
	}
	
	  public final String getJsonName() {return RealmOfTheDragons.MODID + ":" + jsonName;}

	  private ModSounds(String i_jsonName) {
	    jsonName = i_jsonName;
	  }
	  
	  private final String jsonName;
}
