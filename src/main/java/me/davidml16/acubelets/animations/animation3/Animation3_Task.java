package me.davidml16.acubelets.animations.animation3;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import me.davidml16.acubelets.Main;
import me.davidml16.acubelets.animations.Animation;
import me.davidml16.acubelets.enums.CubeletBoxState;
import me.davidml16.acubelets.objects.CubeletBox;
import me.davidml16.acubelets.objects.CubeletType;
import me.davidml16.acubelets.objects.Reward;
import me.davidml16.acubelets.utils.ColorUtil;
import me.davidml16.acubelets.utils.Particles;
import me.davidml16.acubelets.utils.Sounds;
import me.davidml16.acubelets.utils.UtilParticles;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.metadata.FixedMetadataValue;

import javax.rmi.CORBA.Util;
import java.util.List;
import java.util.Objects;

public class Animation3_Task implements Animation {

	private int id;

	private Main main;
	public Animation3_Task(Main main) {
		this.main = main;
	}

	private ArmorStand armorStand;
	private CubeletBox cubeletBox;
	private CubeletType cubeletType;
	private Animation3_Music music;
	private List<Color> colors;

	private ColorUtil.ColorSet<Integer, Integer, Integer> colorRarity;

	Location corner1, corner2, corner3, corner4;

	Location boxLocation;

	class Task implements Runnable {
		int time = 0;
		@Override
		public void run() {
			Location loc = null;
			if(armorStand != null) {
				loc = armorStand.getLocation().clone();
				if (time <= 50) loc.add(0, 0.02, 0);
				armorStand.teleport(loc);
				armorStand.setHeadPose(armorStand.getHeadPose().add(0, 0.26, 0));
			}

			time++;

			if(time == 60) {
				music.cancel();
				Sounds.playSound(cubeletBox.getLocation(), Sounds.MySound.FIREWORK_LAUNCH, 0.5f, 0);
			} else if(time > 60 && time < 80) {
				Objects.requireNonNull(loc).add(0, 0.45, 0);
				armorStand.teleport(loc);
				armorStand.setHeadPose(armorStand.getHeadPose().add(0, 0.26, 0));
				UtilParticles.display(Particles.FLAME, 0.10F, 0.25F, 0.15F, loc, 5);
			} else if(time > 80 && time < 90) {
				Objects.requireNonNull(loc).add(0, 0.35, 0);
				armorStand.teleport(loc);
				armorStand.setHeadPose(armorStand.getHeadPose().add(0, 0.26, 0));
				UtilParticles.display(Particles.FLAME, 0.10F, 0.25F, 0.15F, loc, 5);
			} else if(time > 90 && time < 100) {
				Objects.requireNonNull(loc).add(0, 0.25, 0);
				armorStand.teleport(loc);
				armorStand.setHeadPose(armorStand.getHeadPose().add(0, 0.26, 0));
				UtilParticles.display(Particles.FLAME, 0.10F, 0.25F, 0.15F, loc, 5);
			} else if(time > 100 && time < 110) {
				Objects.requireNonNull(loc).add(0, 0.15, 0);
				armorStand.teleport(loc);
				armorStand.setHeadPose(armorStand.getHeadPose().add(0, 0.26, 0));
				UtilParticles.display(Particles.FLAME, 0.10F, 0.25F, 0.15F, loc, 5);
			} else if(time > 110 && time < 120) {
				Objects.requireNonNull(loc).add(0, 0.05, 0);
				armorStand.teleport(loc);
				armorStand.setHeadPose(armorStand.getHeadPose().add(0, 0.26, 0));
				UtilParticles.display(Particles.FLAME, 0.10F, 0.25F, 0.15F, loc, 5);
			} else if(time > 120 && time < 130) {
				Objects.requireNonNull(loc).add(0, 0.01, 0);
				armorStand.setHeadPose(armorStand.getHeadPose().add(0, 0.26, 0));
				UtilParticles.display(Particles.FLAME, 0.10F, 0.25F, 0.15F, loc, 5);
			} else if(time == 130) {
				Sounds.playSound(cubeletBox.getLocation(), Sounds.MySound.IRONGOLEM_DEATH, 0.5f, 0);
			} else if(time > 130 && time < 150) {
				Objects.requireNonNull(loc).add(0, -0.81, 0);
				armorStand.teleport(loc);
				armorStand.setHeadPose(armorStand.getHeadPose().add(0, 0.26, 0));
				UtilParticles.display(Particles.SMOKE_LARGE, 0.15F, 0.15F, 0.15F, loc, 5);
			} else if(time == 150) {
				Reward reward = main.getCubeletRewardHandler().processReward(cubeletBox.getPlayerOpening(), cubeletType);
				cubeletBox.setLastReward(reward);
				colorRarity = ColorUtil.getRGBbyColor(ColorUtil.getColorByText(reward.getRarity().getName()));
				main.getHologramHandler().rewardHologram(cubeletBox, reward);
				main.getFireworkUtil().spawn(cubeletBox.getLocation().clone().add(0.5, 2, 0.5), FireworkEffect.Type.STAR, colors.get(0), colors.get(1));
				Sounds.playSound(cubeletBox.getLocation(), Sounds.MySound.EXPLODE, 0.5f, 0);
				cubeletBox.setState(CubeletBoxState.REWARD);
				armorStand.remove();
				armorStand = null;
			} else if (time > 150 && time < 250) {
				UtilParticles.drawParticleLine(corner1, corner2, Particles.REDSTONE, 10, colorRarity.getRed(), colorRarity.getGreen(), colorRarity.getBlue());
				UtilParticles.drawParticleLine(corner2, corner3, Particles.REDSTONE, 10, colorRarity.getRed(), colorRarity.getGreen(), colorRarity.getBlue());
				UtilParticles.drawParticleLine(corner3, corner4, Particles.REDSTONE, 10, colorRarity.getRed(), colorRarity.getGreen(), colorRarity.getBlue());
				UtilParticles.drawParticleLine(corner1, corner4, Particles.REDSTONE, 10, colorRarity.getRed(), colorRarity.getGreen(), colorRarity.getBlue());

				UtilParticles.display(Particles.FLAME, 1f, 0f, 1f, boxLocation, 2);
			} else if(time >= 250) {
				stop();
				for (Hologram hologram : cubeletBox.getHolograms().values()) {
					hologram.clearLines();
				}

				Bukkit.getServer().dispatchCommand(main.getServer().getConsoleSender(),
						cubeletBox.getLastReward().getCommand().replaceAll("%player%", cubeletBox.getPlayerOpening().getName()));
				main.getCubeletRewardHandler().sendLootMessage(cubeletBox.getPlayerOpening(), cubeletType, cubeletBox.getLastReward());

				cubeletBox.setState(CubeletBoxState.EMPTY);
				cubeletBox.setPlayerOpening(null);
				main.getHologramHandler().reloadHologram(cubeletBox);
			}

		}
	}
	
	public int getId() { return id; }

	public void start(CubeletBox box, CubeletType type) {
		armorStand = box.getLocation().getWorld().spawn(box.getLocation().clone().add(0.5, -0.35, 0.5), ArmorStand.class);
		armorStand.setVisible(false);
		armorStand.setGravity(false);
		armorStand.setHelmet(type.getIcon());
		armorStand.setSmall(true);
		armorStand.setRemoveWhenFarAway(false);
		armorStand.setCustomNameVisible(false);
		armorStand.setMetadata("ACUBELETS", new FixedMetadataValue(main, Boolean.TRUE));

		Location loc = armorStand.getLocation().clone();
		loc.setYaw(0);
		armorStand.teleport(loc);

		music = new Animation3_Music(box.getLocation());
		music.runTaskTimer(main, 5L, 1L);

		this.cubeletType = type;
		this.cubeletBox = box;
		this.cubeletBox.setState(CubeletBoxState.ANIMATION);
		this.colors = main.getFireworkUtil().getRandomColors();

		corner1 = cubeletBox.getLocation().clone().add(0.05, 0.55, 0.05);
		corner2 = cubeletBox.getLocation().clone().add(0.95, 0.55, 0.05);
		corner3 = cubeletBox.getLocation().clone().add(0.95, 0.55, 0.95);
		corner4 = cubeletBox.getLocation().clone().add(0.05, 0.55, 0.95);

		boxLocation = cubeletBox.getLocation().clone().add(0.5, 0, 0.5);

		id = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(main, new Task(), 0L, 1);

		main.getAnimationHandler().getTasks().add(this);
		main.getAnimationHandler().getArmorStands().add(armorStand);
	}
	
	public void stop() {
		music.cancel();

		main.getAnimationHandler().getTasks().remove(this);

		Bukkit.getServer().getScheduler().cancelTask(id);

		if(main.getAnimationHandler().getArmorStands().contains(armorStand)) {
			if(armorStand != null) armorStand.remove();
			main.getAnimationHandler().getArmorStands().remove(armorStand);
		}
	}
	
}