package xyz.AlaDyn172.DEvent;

import java.awt.Color;
import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("deprecation")
public class Main
  extends JavaPlugin
  implements Listener
{
  FileConfiguration config;
  File cfile;
  public int items = 0;
  public boolean EventNow = false;
  public boolean sayDrop = false;
  public boolean dropItemsMsg = getConfig().getBoolean("dropItemMsg");
  public String foundItem = getConfig().getString("foundItem");
  public String noItemsLeftEvent = getConfig().getString("noItemsLeftEvent");
  public String moreItemsToFind = getConfig().getString("moreItemsToFind");
  public String itemDrop = getConfig().getString("itemDrop");
  public String teleportToEvent = getConfig().getString("teleportToEvent");
  public String noEventNow = getConfig().getString("noEventNow");
  public String alreadyOn = getConfig().getString("alreadyOn");
  public String alreadyOff = getConfig().getString("alreadyOff");
  public String eventTurnOn = getConfig().getString("eventTurnOn");
  public String eventTurnOff = getConfig().getString("eventTurnOff");
  public String droppedItems = getConfig().getString("droppedItems");
  public String placeDropped = getConfig().getString("placeDropped");
  
  public void sendColor(Player all, double x, double y, double z, Color red) {}
  
  ConsoleCommandSender console = getServer().getConsoleSender();
  
  public void onEnable()
  {
    Bukkit.getServer().getPluginManager().registerEvents(this, this);
    
    getConfig().options().copyDefaults(true);
    saveConfig();

    new Metrics(this);
    
    console.sendMessage(ChatColor.GREEN + "[Drop_Event] Plugin developed by Echo. You're using v2.5!");
    
    sayDrop = getConfig().getBoolean("dropItemMsg");
  }
  
  public void onDisable()
  {
    saveConfig();
  }

  
@EventHandler(priority=EventPriority.HIGHEST)
  public void onPlayerPickItemEvent(PlayerPickupItemEvent e)
  {
	  if(this.EventNow == true)
	  {
		  Player p = e.getPlayer();
		  this.foundItem = getConfig().getString("foundItem");
		  this.foundItem = this.foundItem.replaceAll("&", "§");
		  this.noItemsLeftEvent = this.noItemsLeftEvent.replaceAll("&", "§");
		  this.moreItemsToFind = this.moreItemsToFind.replaceAll("&", "§"); 
		  if(e.getItem().getCustomName().equalsIgnoreCase("DroppedItem") && this.EventNow == true)
		  {
			  int howManyCollected = 0;
			  howManyCollected = e.getItem().getItemStack().getAmount();
			  
			  String pName = p.getName();
			  
			  this.items -= howManyCollected;
			  if(this.items == 0)
			  {
				sendMSG("noItemsLeft", pName);
				this.EventNow = false;
			  }
			  else
			  {
				sendMSG("moreItemsToFind", pName);
			  }
		  }
	  }
  }

  public boolean sendMSG(String type, String args) {
	  
	  for(Player ps : Bukkit.getOnlinePlayers()) {
		  if(type == "noItemsLeft") {
			  ps.sendMessage(ChatColor.RED + args + " " + this.foundItem);
			  ps.sendMessage(this.noItemsLeftEvent);
		  } else if(type == "moreItemsToFind") {
			  ps.sendMessage(ChatColor.RED + args + ChatColor.GREEN + " " + this.foundItem);
			  ps.sendMessage(this.moreItemsToFind + " " + this.items);
		  } else if(type == "itemDropped") {
			  ps.sendMessage(this.itemDrop + " " + ChatColor.RED + args + " " + this.placeDropped);
		  } else if(type == "eventUse") {
			  ps.sendMessage(ChatColor.RED + "Admin " + args + " " + ChatColor.AQUA + "has started an event! " + ChatColor.GREEN + "(/gotoevent)");
		  }
	  }
	  
	  
	  return false;
  }
  
  @EventHandler(priority=EventPriority.HIGHEST)
  public void onPlayerDropItem(PlayerDropItemEvent e)
  {
    Player p = e.getPlayer();
    
    this.itemDrop = getConfig().getString("itemDrop");
    
    this.itemDrop = this.itemDrop.replaceAll("&", "§");
    if ((p.isOp()) && 
      (this.EventNow))
    {
     int howManyDropped = 0;
     howManyDropped = e.getItemDrop().getItemStack().getAmount();
    	
      String ItemDropped = e.getItemDrop().getName().replaceAll("item.", "").replaceAll("tile.", "").replaceAll(".default", "");
      
      this.itemDrop = this.itemDrop.replaceAll("-player-", p.getName());
      this.placeDropped = this.placeDropped.replaceAll("&", "§");
      
	  if(sayDrop == true)
	  {
		  sendMSG("itemDropped", ItemDropped.toUpperCase());
	  }
      e.getItemDrop().setCustomName("DroppedItem");
      this.items += howManyDropped;
    }
  }
  
  public boolean onCommand(CommandSender sender, Command cmdLabel, String cmd, String[] args)
  {
    Player p = (Player)sender;
    if ((cmd.equalsIgnoreCase("creategotoevent")) && 
      (p.hasPermission("devent.use"))) {
      if (this.EventNow)
      {
        double Px = p.getLocation().getX();
        double Py = p.getLocation().getY();
        double Pz = p.getLocation().getZ();
        float Ppitch = p.getLocation().getPitch();
        float Pyaw = p.getLocation().getYaw();
        
        getConfig().set("LocationEvent.x", Double.valueOf(Px));
        getConfig().set("LocationEvent.y", Double.valueOf(Py));
        getConfig().set("LocationEvent.z", Double.valueOf(Pz));
        getConfig().set("LocationEvent.pitch", Float.valueOf(Ppitch));
        getConfig().set("LocationEvent.yaw", Float.valueOf(Pyaw));
        saveConfig();
        reloadConfig();
        this.items = 0;
        
        p.sendMessage(ChatColor.GOLD + "You created the teleport for (/gotoevent)!");
      }
      else
      {
        p.sendMessage(ChatColor.RED + "The event is not live! To enable event type: [/eventon]");
      }
    }
    if (cmd.equalsIgnoreCase("gotoevent")) {
      if (this.EventNow)
      {
        double Px = getConfig().getDouble("LocationEvent.x");
        double Py = getConfig().getDouble("LocationEvent.y");
        double Pz = getConfig().getDouble("LocationEvent.z");
        @SuppressWarnings("unused")
		String Pworld = getConfig().getString("LocationEvent.world");
        float Ppitch = (float)getConfig().getDouble("LocationEvent.pitch");
        float Pyaw = (float)getConfig().getDouble("LocationEvent.yaw");
        
        World world = p.getWorld();
        
        Location l = new Location(world, Px, Py, Pz);
        l.setPitch(Ppitch);
        l.setYaw(Pyaw);
        
        p.teleport(l);
        
        this.teleportToEvent = this.teleportToEvent.replaceAll("&", "§");
        
        p.sendMessage(this.teleportToEvent);
      }
      else
      {
        this.noEventNow = this.noEventNow.replaceAll("&", "§");
        p.sendMessage(this.noEventNow);
      }
    }
    if ((cmd.equalsIgnoreCase("eventon")) && 
      (p.hasPermission("devent.use"))) {
      if (this.EventNow)
      {
        this.alreadyOn = this.alreadyOn.replaceAll("&", "§");
        p.sendMessage(this.alreadyOn);
      }
      else
      {
        this.EventNow = true;
        this.eventTurnOn = this.eventTurnOn.replaceAll("&", "§");
        p.sendMessage(this.eventTurnOn);
      }
    }
    if (cmd.equalsIgnoreCase("droppeditems"))
    {
      this.droppedItems = this.droppedItems.replaceAll("&", "§");
      p.sendMessage(this.droppedItems + " " + this.items);
    }
    if ((cmd.equalsIgnoreCase("eventoff")) && 
      (p.hasPermission("devent.use"))) {
      if (!this.EventNow)
      {
        this.alreadyOff = this.alreadyOff.replaceAll("&", "§");
        p.sendMessage(this.alreadyOff);
      }
      else
      {
        this.EventNow = false;
        this.eventTurnOff = this.eventTurnOff.replaceAll("&", "§");
        p.sendMessage(this.eventTurnOff);
        this.items = 0;
      }
    }
    if(cmd.equalsIgnoreCase("eventmsg"))
    {
    	if(p.hasPermission("devent.use"))
    	{
            sendMSG("eventUse", sender.getName());
    	}
    }
    if(cmd.equalsIgnoreCase("togdropmsg"))
    {
    	if(p.hasPermission("devent.use"))
    	{
            if(sayDrop == true)
            {
            	sayDrop = false;
            	getConfig().set("dropItemMsg", false);
            	saveConfig();
            	p.sendMessage(ChatColor.RED + "You set the drop item in event message to hidden.");
            }
            else if(sayDrop == false)
            {
            	sayDrop = true;
            	getConfig().set("dropItemMsg", true);
            	saveConfig();
            	p.sendMessage(ChatColor.RED + "You set the drop item in event message to display.");
            }
    	}
    }
    return false;
  }
}
