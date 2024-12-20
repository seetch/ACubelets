package me.davidml16.acubelets.animations.animation.animation8;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.profiles.builder.XSkull;
import com.cryptomorin.xseries.profiles.objects.ProfileInputType;
import com.cryptomorin.xseries.profiles.objects.Profileable;
import io.github.bananapuncher714.nbteditor.NBTEditor;
import me.davidml16.acubelets.Main;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

public class Animation8_Tree extends BukkitRunnable {

    private Main main;
    private final Location location;
    private int step;

    private Animation8_PlaceSound placeSound;

    private final Set<ArmorStand> treeParts;

    public Animation8_Tree(Main main, Animation8_PlaceSound placeSound, Location location) {
        this.main = main;
        this.location = location.clone().add(0, -0.65, 0);
        this.placeSound = placeSound;
        this.step = 1;
        this.treeParts = new HashSet<>();
    }

    public void run() {

        if(step == 1)
            spawn(location, XMaterial.SPRUCE_LOG.parseItem());
        else if(step == 2)
            spawn(location.clone().add(0, 0.625, 0), XMaterial.SPRUCE_LOG.parseItem());
        else if(step == 3)
            spawn(location.clone().add(0, 1.25, 0), XMaterial.SPRUCE_LOG.parseItem());
        else if(step == 4) {
            spawn(location.clone().add(0.625, 1.25, 0), XMaterial.SPRUCE_LEAVES.parseItem());
            spawn(location.clone().add(-0.625, 1.25, 0), XMaterial.SPRUCE_LEAVES.parseItem());
            spawn(location.clone().add(0, 1.25, 0.625), XMaterial.SPRUCE_LEAVES.parseItem());
            spawn(location.clone().add(0, 1.25, -0.625), XMaterial.SPRUCE_LEAVES.parseItem());
        } else if(step == 5) {
            spawn(location.clone().add(0, 1.9, 0), XMaterial.SPRUCE_LEAVES.parseItem());
            spawn(location.clone().add(0.625, 0.625, 0.625), XMaterial.SPRUCE_LEAVES.parseItem());
            spawn(location.clone().add(-0.625, 0.625, -0.625), XMaterial.SPRUCE_LEAVES.parseItem());
            spawn(location.clone().add(-0.625, 0.625, 0.625), XMaterial.SPRUCE_LEAVES.parseItem());
            spawn(location.clone().add(0.625, 0.625, -0.625), XMaterial.SPRUCE_LEAVES.parseItem());
        } else if(step == 6) {
            spawn(location.clone().add(0.8, 0.625, 0), XMaterial.SPRUCE_LEAVES.parseItem());
            spawn(location.clone().add(-0.8, 0.625, 0), XMaterial.SPRUCE_LEAVES.parseItem());
            spawn(location.clone().add(0, 0.625, 0.8), XMaterial.SPRUCE_LEAVES.parseItem());
            spawn(location.clone().add(0, 0.625, -0.8), XMaterial.SPRUCE_LEAVES.parseItem());
        } else if(step == 7) {
            spawn(location.clone().add(0.7, 0, 0.7), XMaterial.SPRUCE_LEAVES.parseItem());
            spawn(location.clone().add(-0.7, 0, -0.7), XMaterial.SPRUCE_LEAVES.parseItem());
            spawn(location.clone().add(-0.7, 0, 0.7), XMaterial.SPRUCE_LEAVES.parseItem());
            spawn(location.clone().add(0.7, 0, -0.7), XMaterial.SPRUCE_LEAVES.parseItem());
        } else if(step == 8) {
            spawn(location.clone().add(1.1, 0, 0), XMaterial.SPRUCE_LEAVES.parseItem());
            spawn(location.clone().add(-1.1, 0, 0), XMaterial.SPRUCE_LEAVES.parseItem());
            spawn(location.clone().add(0, 0, 1.1), XMaterial.SPRUCE_LEAVES.parseItem());
            spawn(location.clone().add(0, 0, -1.1), XMaterial.SPRUCE_LEAVES.parseItem());
        } else if(step == 9) {
            spawn(location.clone().add(1, -0.78, 0), XSkull.createItem().profile(new Profileable.StringProfileable("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNmNlZjlhYTE0ZTg4NDc3M2VhYzEzNGE0ZWU4OTcyMDYzZjQ2NmRlNjc4MzYzY2Y3YjFhMjFhODViNyJ9fX0=", ProfileInputType.BASE64)).apply());
        } else if(step == 10) {
            spawn(location.clone().add(-1, -0.78, 0), XSkull.createItem().profile(new Profileable.StringProfileable("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWI2NzMwZGU3ZTViOTQxZWZjNmU4Y2JhZjU3NTVmOTQyMWEyMGRlODcxNzU5NjgyY2Q4ODhjYzRhODEyODIifX19", ProfileInputType.BASE64)).apply());
        } else if(step == 11) {
            spawn(location.clone().add(0, -0.78, 1), XSkull.createItem().profile(new Profileable.StringProfileable("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZDA4Y2U3ZGViYTU2YjcyNmE4MzJiNjExMTVjYTE2MzM2MTM1OWMzMDQzNGY3ZDVlM2MzZmFhNmZlNDA1MiJ9fX0=", ProfileInputType.BASE64)).apply());
        } else if(step == 12) {
            spawn(location.clone().add(0, -0.78, -1), XSkull.createItem().profile(new Profileable.StringProfileable("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYTNlNThlYTdmMzExM2NhZWNkMmIzYTZmMjdhZjUzYjljYzljZmVkN2IwNDNiYTMzNGI1MTY4ZjEzOTFkOSJ9fX0=", ProfileInputType.BASE64)).apply());
        } else if(step == 13) {

            try {
                placeSound.cancel();
            } catch(IllegalStateException | NullPointerException ignored) {}

            cancel();
        }

        step++;
    }

    public void restore() {
        for(ArmorStand armorStand : treeParts) if(armorStand != null) armorStand.remove();
    }

    private void spawn(Location spawnLoc, ItemStack itemStack) {
        ArmorStand armorStand = location.getWorld().spawn(spawnLoc, ArmorStand.class);

        NBTEditor.set( armorStand, ( byte ) 1, "Silent" );
        armorStand.setSilent(true);

        armorStand.setVisible(false);
        armorStand.setGravity(false);
        armorStand.setHelmet(itemStack);
        armorStand.setSmall(false);
        armorStand.setMarker(false);
        armorStand.setRemoveWhenFarAway(false);
        armorStand.setCustomNameVisible(false);
        armorStand.setMetadata("ACUBELETS", new FixedMetadataValue(main, Boolean.TRUE));

        armorStand.teleport(spawnLoc);

        treeParts.add(armorStand);
    }

}
