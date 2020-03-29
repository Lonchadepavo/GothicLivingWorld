package com.loncha.gothiclivingworld;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.BlockVector2D;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.bukkit.BukkitUtil;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedPolygonalRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class Main extends JavaPlugin implements Listener{
	
	BlockVector maxPoint, minPoint;

	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
	}
	
	//Método para recorrer los bloques de una localización
	public void recorrerBloques(World w, String nombreRg, Material bloque, int maxBloques) {
		int maxClay = 10000;
		int nClay = 0;
		
		WorldGuardPlugin wg = getWorldGuard();
		RegionManager rmg = wg.getRegionManager(w);
		
		ProtectedPolygonalRegion region;
		
		Map<String, ProtectedRegion> regions = rmg.getRegions();
		Iterator it = regions.entrySet().iterator();
		
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry)it.next();
			System.out.println(pair.getKey());
			
			if (pair.getKey().equals(nombreRg)) {
				region = (ProtectedPolygonalRegion) pair.getValue();
				getPointsFromRegion(region);
			}
		}
		
		Location l1 = new Location(w, minPoint.getX(),minPoint.getY(),minPoint.getZ());
		Location l2 = new Location(w, maxPoint.getX(),maxPoint.getY(),maxPoint.getZ());
		
		int x = l1.getBlockX();
		int y = l1.getBlockY();
		int z = l1.getBlockZ();
		int maxX = l2.getBlockX();
		int maxZ = l2.getBlockZ();
		int maxY = l2.getBlockY();
		
		int nBloques = 0;
		for (int x1 = x; x1 <= maxX; x1++) {

			for (int y1 = y; y1 <= maxY; y1++) {
					
				for (int z1 = z; z1 <= maxZ; z1++) {
					Location l3 = new Location(w, x1, y1, z1);
					
					Block b = l3.getBlock();
					
					if (b.getType() == Material.AIR) {
						Location l4 = new Location(w, b.getX(), b.getY(), b.getZ());
						
						if (inRegion(l4,nombreRg)) {
							nBloques++;
							
							Location l5 = new Location(w, b.getX(), b.getY()-1, b.getZ());
							
							if (l5.getBlock().getType() == Material.DIRT || l5.getBlock().getType() == Material.GRASS) {
								b.setType(Material.CLAY);
							}
						}	
					}
				}
			}
		}
		
		System.out.println("Total clay: " + nBloques);
		
	}
	
	public void crearBloques(World w, String region, Material bloque, int maxBloques) {
		
	}
	
	public void getPointsFromRegion(ProtectedPolygonalRegion region) {
		List<BlockVector2D> points = region.getPoints();
		maxPoint = region.getMaximumPoint();
		minPoint = region.getMinimumPoint();

	}
	
	public WorldGuardPlugin getWorldGuard() {
	    Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
	 
	    if ((plugin == null) || (!(plugin instanceof WorldGuardPlugin))) {
	    return null;
	    }
	 
	    return (WorldGuardPlugin)plugin;
	}
	
	
	public static boolean inRegion(Location loc, String region) {

	    Vector v = BukkitUtil.toVector(loc);
	    RegionManager manager = WGBukkit.getRegionManager(loc.getWorld());
	    ProtectedRegion rg = manager.getRegion(region);
	    ApplicableRegionSet set = manager.getApplicableRegions(v);
	    
	    if (set.getRegions().contains(rg)) {
	    	return true;
	    	
	    } else {
	    	return false;
	    }

	}
	
}
