/*
** 2016 March 18
**
** The author disclaims copyright to this source code. In place of
** a legal notice, here is a blessing:
**    May you do good and not evil.
**    May you find forgiveness for yourself and forgive others.
**    May you share freely, never taking more than you give.
 */
package com.TheRPGAdventurer.ROTD;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.DummyConfigElement.DummyCategoryElement;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

public class RealmOfTheDragonsConfigGui extends GuiConfig {
    
    private static final Configuration CONFIG = RealmOfTheDragons.instance.getConfig().getConfig();
    
    public RealmOfTheDragonsConfigGui(GuiScreen parentScreen) {
        super(
            parentScreen,
            Arrays.asList(
                new ConfigElement(CONFIG.getCategory(RealmOfTheDragonsConfig.CATEGORY_MAIN)),
                new ConfigElement(CONFIG.getCategory(RealmOfTheDragonsConfig.CATEGORY_WORLDGEN))
            ), RealmOfTheDragons.MODID, false, false,
               RealmOfTheDragons.NAME
        );
    }
}
