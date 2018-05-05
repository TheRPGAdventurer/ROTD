package com.TheRPGAdventurer.ROTD;

import static com.TheRPGAdventurer.ROTD.RealmOfTheDragonsLootTables.RegistrationHandler.create;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;

public class RealmOfTheDragonsLootTables {
	
	public static final ResourceLocation ENTITIES_DRAGON_AMETHYST = create("amethyst");
	public static final ResourceLocation ENTITIES_DRAGON_GARNET = create("garnet");
	public static final ResourceLocation ENTITIES_DRAGON_JADE = create("jade");
	public static final ResourceLocation ENTITIES_DRAGON_RUBY = create("ruby");
	public static final ResourceLocation ENTITIES_DRAGON_SAPPHIRE = create("sapphire");
	public static final ResourceLocation ENTITIES_DRAGON_END = create("ender");
	public static final ResourceLocation ENTITIES_DRAGON_NETHER = create("nether");
	public static final ResourceLocation ENTITIES_DRAGON_SKELETON = create("skeleton");
	
	/**
	 * Register this mod's {@link LootTable}s.
	 */
	public static void registerLootTables() {
		RegistrationHandler.LOOT_TABLES.forEach(LootTableList::register);
	}

	public static class RegistrationHandler {
		
		/**
		 * Stores the IDs of this mod's {@link LootTable}s.
		 */
		private static final Set<ResourceLocation> LOOT_TABLES = new HashSet<>();

		/**
		 * Create a {@link LootTable} ID.
		 *
		 * @param id The ID of the LootTable without the testmod3: prefix
		 * @return The ID of the LootTable
		 */
		protected static ResourceLocation create(String id) {
			final ResourceLocation lootTable = new ResourceLocation(RealmOfTheDragons.MODID, id);
			RegistrationHandler.LOOT_TABLES.add(lootTable);
			return lootTable;
		}
	}
}	