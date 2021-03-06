package ssell.TentThis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;
import org.bukkit.util.config.ConfigurationNode;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class TentThis 
	extends JavaPlugin
{
	private static final Logger log = Logger.getLogger( "Minecraft" );
	
	public static PermissionHandler Permissions;
	
	public boolean permission = false;
	public boolean noCommandDefault = false;
	
	public final TTBlockListener blockListener = new TTBlockListener( this );
	public final TTPlayerListener playerListener = new TTPlayerListener( this );
	public final TTSchemaLoader schemaLoader = new TTSchemaLoader( this );
	public final TTManager manager = new TTManager( this );
	public final TTReverseSchema reverseSchema = new TTReverseSchema( this );
	
	static String mainDirectory = "plugins/TentThis";
	File file = new File(mainDirectory + File.separator + "config.yml");
	File playr = new File(mainDirectory + File.separator + "players.yml");
	
	
	
	//--------------------------------------------------------------------------------------
	
	public Configuration load(File file){

        try {
            Configuration config = new Configuration(file);
            config.load();
            return config;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
	
	public void writeConfig(String root, String x){ //just so you know, you may want to write a boolean, integer or double to the file as well, therefore u wouldnt write it to the file as "String" you would change it to something else
    	Configuration config = load(file);
        config.setProperty(root, x);
        config.save();
    }

	public void writePlayr(String root, String x){ //just so you know, you may want to write a boolean, integer or double to the file as well, therefore u wouldnt write it to the file as "String" you would change it to something else
    	Configuration config = load(playr);
        config.setProperty(root, x);
        config.save();
    }	
	
	public void writeTents(String root, ArrayList<String> tick ){ //just so you know, you may want to write a boolean, integer or double to the file as well, therefore u wouldnt write it to the file as "String" you would change it to something else
    	Configuration config = load(playr);
        config.setProperty(root, tick);
        config.save();
    }
	
    public String readConfig(String root){
    	Configuration config = load(file);
        return config.getString(root);
    }
    
    public String readPlayr(String root){
    	Configuration config = load(playr);
        return config.getString(root);
    }
    


	
	public void onEnable( )
	{
		PluginManager pluginMgr = getServer( ).getPluginManager( );
		
		pluginMgr.registerEvent( Event.Type.BLOCK_DAMAGE, blockListener, 
                Event.Priority.Low, this );
		pluginMgr.registerEvent( Event.Type.BLOCK_BREAK, blockListener, 
				Event.Priority.Low, this );
		pluginMgr.registerEvent( Event.Type.PLAYER_INTERACT, playerListener, 
				Event.Priority.Low, this );
		pluginMgr.registerEvent( Event.Type.PLAYER_JOIN, playerListener, 
				Event.Priority.Low, this );
		pluginMgr.registerEvent( Event.Type.PLAYER_QUIT, playerListener, 
				Event.Priority.Low, this );
		
		setupPermissions( );
		
		getDefaults( );
		setupSchemas( );
		
		
		
		log.info( "TentThis: v2.5 Unofficial by BigBenM is enabled!" );		
	}
	
	public void onDisable( )
	{
		manager.saveAll( );
	}
	
	/**
	 * Permissions List:<br><br>
	 * 
	 * TentThis.commands.setOwnSchema<br>
	 * TentThis.commands.setAllSchema<br>
	 * TentThis.commands.setLimit<br>
	 * TentThis.general.destroyAnyTent<br>
	 * TentThis.commands.reverseSchema<br>
	 */
	private void setupPermissions( ) 
	{
	  	Plugin test = this.getServer( ).getPluginManager( ).getPlugin( "Permissions" );

	  	if( TentThis.Permissions == null ) 
	 	{
	     	if( test != null ) 
	     	{
	        	permission = true;
	        	TentThis.Permissions = ( ( Permissions )test ).getHandler( );
	      	} 
	    	else
	    	{
	        	log.info("Permission system not detected, defaulting to OP");
	     	}
	 	}
	}
	
	/**
	 * List of commands:<br><br>
	 *  /ttTent<br>
	 *  /ttSchema [Schema] [Player]<br>
	 *  /ttReload<br>
	 *  /ttLimit [Limit] [Player]<br>
	 */
	public boolean onCommand( CommandSender sender, Command command, String label, String[] args )
	{
		String[] split = args;
		String commandName = command.getName().toLowerCase();
	        
		if ( sender instanceof Player ) 
		{
			Player player = ( Player )sender;
			
			if( commandName.equals( "tttent" ) )
			{				
				tentCommand( player );
				
				return true;
			}
			else if( commandName.equals( "ttschema" ) )
			{
				if( split.length == 2 )
				{
					schemaCommandAll( player, split[ 0 ], split[ 1 ] );
				}
				else if( split.length == 1 )
				{
					schemaCommandSelf( player, split[ 0 ] );
				}
				else
				{
					player.sendMessage( ChatColor.DARK_RED + "Improper use of command! /ttSchema <schemaName> <optional:player>" );
				}
				
				return true;
			}
			else if( commandName.equals( "ttlimit" ) )
			{
				if( split.length == 2 )
				{
					limitCommand( player, split[ 0 ], split[ 1 ] );
				}
				else
				{
					player.sendMessage( ChatColor.DARK_RED + "Improper use of command! /ttLimit <limit> <playerName>" );
				}
				
				return true;
			}
			else if( commandName.equals( "ttnocommand" ) )
			{
				noCommand( player );
				
				return true;
			}
			else if( commandName.equals( "ttreload" ) )
			{
				reloadCommand( player );
				
				return true;
			}
			else if( commandName.equals( "ttinfo" ) )
			{
				infoCommand( player );
				
				return true;
			}
			else if( commandName.equals( "ttreverseschema" ) )
			{
				if( split.length == 3 )
				{
					reverseCommand( player, split[ 0 ], split[ 1 ], split[ 2 ] );
				}
				else
				{
					player.sendMessage( ChatColor.DARK_RED + "Invalid Command! /ttReverseSchema <SchemaName> <cornerToIgnore> <destructionBlock>" );
				}
				
				return true;
			}
		}
		
		return false;
	}
	
	//--------------------------------------------------------------------------------------
	// Command Methods
	
	/**
	 * Called when player uses /ttTent command.
	 */
	public void tentCommand( Player player )
	{
		if( blockListener.listenList.contains( player.getName( ) ) )
		{
			//Already on. Don't wait anymore
			blockListener.listenList.remove( player.getName( ) );
			
			player.sendMessage( ChatColor.GOLD + "No longer waiting to build tent." );
		}
		else
		{
			blockListener.listenList.add( player.getName( ) );
			
			//Remove player from the playerListener list (NoCommand)
			if( playerListener.listenList.contains( player.getName( ) ) )
			{
				playerListener.listenList.remove( player.getName( ) );
				player.sendMessage( ChatColor.GOLD + "NoCommand disabled!" );
			}
			
			player.sendMessage( ChatColor.GOLD + "Waiting to build tent." );
		}
	}
	
	/**
	 * Called when a player attempts to set their own schema.
	 * 
	 * @param player
	 * @param schema
	 */
	public void schemaCommandSelf( Player player, String schema )
	{
		//Using Permissions?
		if( permission )
		{
			//Player has permission?
			if( !TentThis.Permissions.has( player, "TentThis.commands.setOwnSchema" ) )
			{
				player.sendMessage( ChatColor.DARK_RED + "You don't have permission to perform this action!" );
				
				return;
			}
		}
		
		//Did the schema setting work?
		if( manager.setSchema( schema, player.getName( ) ) )
		{
			player.sendMessage( ChatColor.GOLD + "'" + schema + "' set as schema!" );
		}	
		else
		{
			player.sendMessage( ChatColor.DARK_RED + "Failed to set '" + schema + "' as schema!" );
		}
	}
	
	/**
	 * Called when a player attempts to set the schema of another.<br><br>
	 * If '-all' is provided for who, then it is set to all players.
	 * 
	 * @param player
	 * @param schema
	 * @param who
	 */
	public void schemaCommandAll( Player player, String schema, String who )
	{
		//Using Permissions?
		if( permission )
		{
			//Player has permission?
			if( !TentThis.Permissions.has( player, "TentThis.commands.setAllSchema" ) )
			{
				player.sendMessage( ChatColor.DARK_RED + "You don't have permission to perform this action!" );
				
				return;
			}
		}
		
		//Did the schema setting work?
		if( manager.setSchema( schema, who ) )
		{
			player.sendMessage( ChatColor.GOLD + "'" + schema + "' set as schema for '" + who +"'!" );
		}
		else
		{
			player.sendMessage( ChatColor.DARK_RED + "Failed to set '" + schema + "' as schema!" );
		}
	}
	
	/**
	 * Sets the tent limit for the specified player.<br><br>
	 * If '-all' is provided as the player, then it is set for everyone.
	 * 
	 * @param player
	 * @param name
	 */
	public void limitCommand( Player player, String name, String limitStr )
	{
		log.info( "ttLimit: " + name + " " + limitStr );
		//Using Permissions?
		if( permission )
		{
			if( !TentThis.Permissions.has( player, "TentThis.commands.setLimit" ) )
			{
				player.sendMessage( ChatColor.DARK_RED + "You don't have permission to perform this action!" );
				
				return;
			}
		}	
		
		int limit;
			
		//Make sure the user provided an integer
		try
		{
			limit = Integer.parseInt( limitStr );
		}
		catch( NumberFormatException nfe )
		{
			player.sendMessage( ChatColor.DARK_RED + "Integer value provided is not an integer!" );
				
			return;
		}
			
		manager.setLimit( limit, name );
			
		player.sendMessage( ChatColor.GOLD + "Limit set to " + limit + " for '" + name + "'!" );
	}
	
	/**
	 * Handles /ttNoCommand.<br><br>
	 * If enabled, player no longer needs to call /ttTent and needs to right-click the block.
	 * 
	 * @param player
	 */
	public void noCommand( Player player )
	{
		if( playerListener.listenList.contains( player.getName( ) ) )
		{
			//Already on the list. Remove them.
			playerListener.listenList.remove( player.getName( ) );
			
			player.sendMessage( ChatColor.GOLD + "No longer using NoCommand! Must manually use /ttTent and left-click blocks." );
		}
		else
		{
			playerListener.listenList.add( player.getName( ) );
			
			player.sendMessage( ChatColor.GOLD + "Now using NoCommand! Must right-click blocks in this mode." );
		}
		
		if( blockListener.listenList.contains( player.getName( ) ) )
		{
			blockListener.listenList.remove( player.getName( ) );
		}
	}
	
	/**
	 * Updates the creation block.
	 * 
	 * @param player
	 */
	public void reloadCommand( Player player )
	{
		if( permission )
		{
			if( !TentThis.Permissions.has( player, "TentThis.commands.reload" ) )
			{
				player.sendMessage( ChatColor.DARK_RED + "You don't have permission to perform this action!" );
				
				return;
			}
		}
		
		if( getDefaults( ) )
		{
			player.sendMessage( ChatColor.GOLD + "TentThis reload successful!" );
			log.info("TentThis reloaded by " +  player.getDisplayName());
		}
		else
		{
			player.sendMessage( ChatColor.DARK_RED + "TentThis reload failed!" );
		}
	}
	
	/**
	 * Lists the following to the player:<br><br>
	 * CreationBlock<br>
	 * # of Tents / Tent Limit<br>
	 * List of schemas. Current is green.
	 * 
	 * @param player
	 */
	public void infoCommand( Player player )
	{
		//Example: CreationBlock: Sponge [19]
		player.sendMessage( ChatColor.GOLD + "CreationBlock: " + 
				Material.getMaterial( blockListener.creationBlock ) + 
				" [" + blockListener.creationBlock + "]" );
		
		TTPlayer ttPlayer = manager.getPlayer( player.getName( ) );
		
		//Example: TentLimit: 3/16
		if( ( ttPlayer.tentList.size( ) < ttPlayer.limit ) ||
			( ttPlayer.limit < 0 ) )
		{
			player.sendMessage( ChatColor.GOLD + "TentLimit: " + ChatColor.GREEN +
					ttPlayer.tentList.size( ) + "/" + ttPlayer.limit );
		}
		else
		{
			player.sendMessage( ChatColor.GOLD + "TentLimit: " + ChatColor.RED +
					ttPlayer.tentList.size( ) + "/" + ttPlayer.limit );
		}
		
		player.sendMessage( ChatColor.GOLD + "List of Schemas:" );
		
		for( int i = 0; i < manager.tentList.size( ); i++ )
		{
			if( ttPlayer.currentTent.equals( manager.tentList.get( i ) ) )
			{
				player.sendMessage( ChatColor.GREEN + manager.tentList.get( i ).schemaName );
			}
			else
			{
				player.sendMessage( ChatColor.RED + manager.tentList.get( i ).schemaName );
			}
		}
		
	}
	
	/**
	 * 
	 * @param player Player who sent the command
	 * @param schemaName Name of the schema
	 * @param cornerToIgnore Corner(s) to ignore when saving. 1 = first, 2 = last, 3 = both.
	 * @param destructionBlock Block that when broken, will destroy the tent.
	 */
	public void reverseCommand( Player player, String schemaName, String cornerToIgnore, String destructionBlock )
	{
		if( permission )
		{
			if( !TentThis.Permissions.has( player, "TentThis.commands.reverseSchema" ) )
			{
				player.sendMessage( ChatColor.DARK_RED + "You don't have permission to perform this action!" );
				
				return;
			}
		}
		
		if( schemaName != null )
		{
			if( manager.exists( schemaName ) )
			{
				player.sendMessage( ChatColor.DARK_RED + "A schema with that name already exists!" );
				
				return;
			}
			
			player.sendMessage( ChatColor.GREEN + "TentThis Reverse Schema: Please select the corners." );
		
			reverseSchema.waitForPlayer( player.getName( ), schemaName, 
					Integer.parseInt( cornerToIgnore ), Integer.parseInt( destructionBlock ) );
		}
	}
	
	//--------------------------------------------------------------------------------------
	
	public void buildTent( String name, Block block )
	{
		TTPlayer player = manager.getPlayer( name );
		
		if( player != null )
		{
			if( ( player.tentList.size( ) < player.limit ) || ( player.limit < 0 ) ) 
			{
				//Player can build more
				schemaLoader.renderTent( getServer( ).getPlayer( name ), block, manager.getPlayer( name ).currentTent );
				this.writeTents("test", manager.tentList);
			}
			else
			{
				getServer( ).getPlayer( name ).sendMessage( ChatColor.DARK_RED + "You are at your limit! Destroy an existing tent." );
			}
		}
	}
	
	public boolean getDefaults( )
	{
	
		new File(mainDirectory).mkdir();


        if(!file.exists()){
            try {
                file.createNewFile();
                this.writeConfig("config.CreationBlock","19");
                this.writeConfig("config.TentLimit", "-1");
                this.writeConfig("config.NoCommand", "false");
                this.writeConfig("config.KeepBlock", "false");
                this.writeConfig("config.DefaultSchema", "Single");
                this.writeConfig("Schemas.Name", "Default");
                this.writeConfig("Schemas.DeconstructionBlock", "35");
                this.writeConfig("Schemas.DimensionX", "5");
                this.writeConfig("Schemas.DimensionY", "6");
                this.writeConfig("Schemas.Layers", "4");
                this.writeConfig("Schemas.WoolColor", "green");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } 
        
        if(!playr.exists()){
            try {
                playr.createNewFile();
                this.writePlayr("JohnDoe", "");
                this.writePlayr("JohnDoe.DefaultSchema", "Single");
                this.writePlayr("JohnDoe.GetBlockBack", "false");
                this.writePlayr("JohnDoe.TentLimit", "-1");
                this.writePlayr("JohnDoe.TentAmount", "0");
                this.writePlayr("JohnDoe.Tents.Locations", "");
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        } 
        
        //Get Creation Block from config.yml & Display info in log
		String readcblock = this.readConfig("config.CreationBlock");
		int cblock = Integer.parseInt(readcblock.trim());
		log.info( "TentThis: Creation Block set to " + cblock + " (" + Material.getMaterial(cblock) + ")" );
		blockListener.creationBlock = cblock;
		playerListener.creationBlock = cblock;
	
		//Get Tent Limit from config.yml & Display info in log
		String readtentlimit = this.readConfig("config.TentLimit");
		int tlimit = Integer.parseInt(readtentlimit.trim());
		log.info( "TentThis: Default Tent Limit set to " + tlimit );
		manager.globalLimit = tlimit;
		
		//Get NoCommand from config.yml & Display info in log
		String readnocommand = this.readConfig("config.NoCommand");
		Boolean nocommand = Boolean.parseBoolean(readnocommand);
		log.info( "TentThis: NoCommand set to " + nocommand );
		noCommandDefault = nocommand;
		
		
		
		return true;
	}
	
	public void setupSchemas( )
	{		
		//Gather all of the schemas
		
		String name2 = this.readConfig("config.DefaultSchema");
		
		manager.createTent(name2);
		//Set default as the first tent in the list
		manager.defaultSchema = manager.tentList.get( 0 ).schemaName;
	}

	public void writeTents(String root, List<TTTent> tentList) {
		// TODO Auto-generated method stub
		
	}
	}

