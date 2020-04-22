package me.davidml16.acubelets.handlers;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import me.davidml16.acubelets.Main;
import me.davidml16.acubelets.data.CubeletBox;
import me.davidml16.acubelets.data.CubeletType;
import me.davidml16.acubelets.data.Rarity;
import me.davidml16.acubelets.utils.ColorUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class CubeletRarityHandler {

    private Main main;

    public CubeletRarityHandler(Main main) {
        this.main = main;
    }

    public Rarity getRarityById(String type, String id) {
        return main.getCubeletTypesHandler().getTypeBydId(type).getRarities().get(id);
    }

    public boolean createRarity(String type, String id, String name, int chance) {
        CubeletType cubeletType = main.getCubeletTypesHandler().getTypeBydId(type);
        cubeletType.getRarities().put(id, new Rarity(id, name, chance));
        FileConfiguration config = main.getCubeletTypesHandler().getConfig(type);
        config.set("type.rarities", new ArrayList<>());
        for(Rarity rarity : cubeletType.getRarities().values()) {
            config.set("type.rarities." + rarity.getId() + ".name", rarity.getName());
            config.set("type.rarities." + rarity.getId() + ".chance", rarity.getChance());
        }
        main.getCubeletTypesHandler().saveConfig(type);
        return true;
    }

    public boolean removeRarity(String type, String id) {
        CubeletType cubeletType = main.getCubeletTypesHandler().getTypeBydId(type);
        if(cubeletType.getRarities().containsKey(id)) {
            FileConfiguration config = main.getCubeletTypesHandler().getConfig(type);
            cubeletType.getRarities().remove(id);
            config.set("type.rarities", new ArrayList<>());
            for(Rarity rarity : cubeletType.getRarities().values()) {
                config.set("type.rarities." + rarity.getId() + ".name", rarity.getName());
                config.set("type.rarities." + rarity.getId() + ".chance", rarity.getChance());
            }
            main.getCubeletTypesHandler().saveConfig(type);
            return true;
        }
        return false;
    }

    public void loadRarities() {
        Main.log.sendMessage(ColorUtil.translate(""));
        Main.log.sendMessage(ColorUtil.translate("  &eLoading rarities:"));

        for(CubeletType cubeletType : main.getCubeletTypesHandler().getTypes().values()) {
            FileConfiguration config = main.getCubeletTypesHandler().getConfig(cubeletType.getId());

            cubeletType.getRarities().clear();

            if(!config.contains("type.rarities")) {
                config.set("type.rarities", new ArrayList<>());
            }

            main.getCubeletTypesHandler().saveConfig(cubeletType.getId());

            if(config.contains("type.rarities")) {
                if(config.getConfigurationSection("type.rarities") != null) {
                    for (String id : config.getConfigurationSection("type.rarities").getKeys(false)) {
                        String name = config.getString("type.rarities." + id + ".name");
                        int chance = config.getInt("type.rarities." + id + ".chance");
                        cubeletType.getRarities().put(id, new Rarity(id, name, chance));
                    }
                }
            }

            Main.log.sendMessage(ColorUtil.translate("    &a'" + cubeletType.getId() + "&a' &7- " + (cubeletType.getRarities().size() > 0 ? "&a" : "&c") + cubeletType.getRarities().size() + " rarities"));
        }

        Main.log.sendMessage(" ");
    }

    public boolean rarityExist(String type, String id) {
        CubeletType cubeletType = main.getCubeletTypesHandler().getTypeBydId(type);
        return cubeletType.getRarities().containsKey(id);
    }

}
