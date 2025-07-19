package me.greenmoon.addon.ffa;

import me.bedtwL.ffa.api.effect.PureArrowShootEffect;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

public class ArrowOreDropTrail extends PureArrowShootEffect {

    private final Material[] drops = {
            Material.DIAMOND,
            Material.GOLD_INGOT,
            Material.REDSTONE,
            Material.INK_SACK, // for Lapis Lazuli (damage = 4)
            Material.COAL,
            Material.EMERALD
    };

    private final short LAPIS_DAMAGE = 4;

    @Override
    public void arrowShootEffect(Location location, Player player) {
        Arrow arrow = null;

        for (Entity entity : player.getWorld().getEntities()) {
            if (entity instanceof Arrow) {
                Arrow a = (Arrow) entity;
                if (a.getShooter() instanceof Player && a.getShooter().equals(player)) {
                    if (a.getWorld().equals(location.getWorld()) && a.getLocation().distance(location) < 1.5) {
                        arrow = a;
                        break;
                    }
                }
            }
        }

        if (arrow == null) return;

        Arrow finalArrow = arrow;

        new BukkitRunnable() {
            @Override
            public void run() {
                if (finalArrow.isDead() || finalArrow.isOnGround() || finalArrow.getVelocity().length() < 0.1) {
                    this.cancel();
                    return;
                }

                Location dropLoc = finalArrow.getLocation().clone().add(0, 0.1, 0);
                Random random = new Random();
                Material randomDrop = drops[random.nextInt(drops.length)];
                ItemStack item;

                if (randomDrop == Material.INK_SACK) {
                    item = new ItemStack(randomDrop, 1, LAPIS_DAMAGE); // Lapis
                } else {
                    item = new ItemStack(randomDrop);
                }

                Item droppedItem = dropLoc.getWorld().dropItem(dropLoc, item);
                droppedItem.setPickupDelay(99999);
                droppedItem.setVelocity(new Vector(0, 0, 0));

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!droppedItem.isDead()) {
                            droppedItem.remove();
                        }
                    }
                }.runTaskLater(Bukkit.getPluginManager().getPlugin("PureFFA"), 30L); // 1.5 秒後刪除
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("PureFFA"), 0L, 20L); // 每秒檢查一次
    }

    @Override
    public String getName() {
        return "OreDropTrail";
    }

    @Override
    public String getItemNameKey() {
        return "ore-drop-trail";
    }

    @Override
    public ItemStack getItemBase() {
        return new ItemStack(Material.EMERALD_ORE);
    }
}
