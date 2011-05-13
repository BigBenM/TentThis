package ssell.TentThis;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.inventory.ItemStack;

public class TTBlockListener 
	extends BlockListener
{
	private final TentThis plugin;
	
	public List< String > listenList = new ArrayList< String >( );
	
	public int creationBlock = 19;
	
	//--------------------------------------------------------------------------------------
	
	public TTBlockListener( TentThis instance )
	{
		plugin = instance;
	}
	
	@Override
	public void onBlockDamage( BlockDamageEvent event ) 
	{
		super.onBlockDamage( event );
		
		if( listenList.contains( event.getPlayer( ).getName( ) ) )
		{
			if( event.getBlock( ).getTypeId( ) == creationBlock )
			{
				plugin.buildTent( event.getPlayer( ).getName( ), event.getBlock( ) );
			}
			
			listenList.remove( event.getPlayer( ).getName( ) );
		}
	}
	
	@Override
	public void onBlockBreak( BlockBreakEvent event )
	{
		super.onBlockBreak( event );
		
		if( plugin.manager.isDestructionBlock( event.getBlock( ).getTypeId( ) ) )
		{
			
			//Get the player that owns the tent
			TTPlayer owningPlayer = plugin.manager.whoOwnsThis( event.getBlock( ) );
			
			if( owningPlayer != null )
			{				
				//Do not allow unauthorized players to break the tent.
				if( !event.getPlayer( ).getName( ).equalsIgnoreCase( owningPlayer.name ) )
				{
					if( plugin.permission )
					{
						if( !TentThis.Permissions.has( event.getPlayer( ), "TentThis.general.destroyAnyTent" ) )
						{
							event.getPlayer( ).sendMessage( ChatColor.DARK_RED + "This is not your tent!" );
							event.setCancelled( true );
							return;
						}
					}
					else
					{
						event.getPlayer( ).sendMessage( ChatColor.DARK_RED + "This is not your tent!" );
						event.setCancelled( true );
						return;
					}
					
				}
				
				//Get the actual tent
				List< Block > tent = null;
                                int tentListIndex = -1;
				
				for( int i = 0; i < owningPlayer.tentList.size( ); i++ )
				{	
					if( owningPlayer.tentList.get( i ).second.contains( event.getBlock( ) ) )
					{
						if( plugin.manager.destructionBlockBelongToSchema( 
							owningPlayer.tentList.get( i ).first, event.getBlock( ).getTypeId( ) ) )
						{
							tent = owningPlayer.tentList.get( i ).second;
                                                        tentListIndex = i;
							break;
						}
					}
					
				}
				
				if( tent != null )
				{
					event.getPlayer( ).getInventory( ).addItem( new ItemStack( creationBlock, 1 ) );
					plugin.schemaLoader.destroyTent( tent, event.getPlayer( ), owningPlayer, tentListIndex );
				}
				else
				{
					event.setCancelled( true );
				}
			}
		}
		else
		{
			//Does the block belong to a tent?
			TTPlayer player = plugin.manager.whoOwnsThis( event.getBlock( ) );
			
			if( player != null )
			{				
				event.setCancelled( true );
			}
		}
		
		Block block = null;
	}
}
