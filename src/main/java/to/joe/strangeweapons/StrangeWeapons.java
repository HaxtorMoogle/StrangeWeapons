package to.joe.strangeweapons;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class StrangeWeapons extends JavaPlugin implements Listener {

    private HashMap<Integer, String> weaponText = new HashMap<Integer, String>();

    @Override
    public void onEnable() {
        getConfig().options().copyDefaults(true);
        saveConfig();

        getCommand("strange").setExecutor(new StrangeCommand(this));
        getServer().getPluginManager().registerEvents(this, this);

        for (String test : getConfig().getStringList("levels")) {
            String[] split = test.split(",");
            weaponText.put(Integer.parseInt(split[0]), split[1]);
        }
    }

    String getWeaponName(int kills) {
        while (!weaponText.containsKey(kills)) {
            kills--;
            if (kills < 0)
                return "Sub-par";
        }
        return weaponText.get(kills);
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player && event.getEntity() instanceof Player && ((Player) event.getEntity()).getHealth() - event.getDamage() <= 0) {
            Player p = (Player) event.getDamager();
            NameableItem item = new NameableItem((CraftItemStack) p.getItemInHand());
            if (!p.getItemInHand().equals(Material.AIR) && item.isStrange()) {
                int kills = item.getKills();
                kills++;
                item.setKills(kills);
                String oldName = item.getName();
                item.setName(ChatColor.GOLD + getWeaponName(kills) + p.getItemInHand().getType().toString().toLowerCase().replaceAll("_", " "));
                item.setLore(new String[] { ChatColor.WHITE + "Kills: " + kills });
                if (!oldName.equals(item.getName())) {
                    getServer().broadcastMessage(ChatColor.GOLD + p.getName() + "'s " + oldName + " has reached a new rank: " + getWeaponName(kills));
                }
            }
        }
    }
}