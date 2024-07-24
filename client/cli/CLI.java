package cli;
import java.beans.PropertyChangeListener;
import java.util.*;

import map.ClientMap;
import map.PlayerHalfMapNode;
import map.Position;
import map.TerrainType;
import messagesbase.messagesfromserver.EFortState;
import messagesbase.messagesfromserver.EPlayerGameState;
import messagesbase.messagesfromserver.EPlayerPositionState;
import model.GameModel;

public class CLI {
	MapIconColors color = new MapIconColors();
	
	public CLI(GameModel model) {
		model.addPropertyChangeListener(modelChangedListener);
	}
	
	  final PropertyChangeListener modelChangedListener = event -> {
		  if ("map".equals(event.getPropertyName())) {
              displayMap((ClientMap) event.getNewValue());
          }
		  
		  if ("playerState".equals(event.getPropertyName())) {
              if ((EPlayerGameState)event.getNewValue() == EPlayerGameState.Lost) {
            	  reportLoss();
              } else if (event.getNewValue() == EPlayerGameState.Won) {
            	  reportWin();
              }
          }
	    };
	
	public void displayMap(ClientMap map) {	
		Map<Position, PlayerHalfMapNode> fields = map.getFields();
		for (int j = 0; j <= map.getHeight(); j++) {
			final int y = j;
			for (int i = 0; i <= map.getWidth(); i++) {
				final int x = i;
				PlayerHalfMapNode node = fields.get(new Position(x, y));
				// no nulls !! change!
				if (node.getFortState() == EFortState.MyFortPresent){
					printFort(node);
				} else {
					TerrainType terrain = node.getTerrain();
					switch(terrain) {
					case Grass: 
						if (node.isDiscovered()) {
							printDiscoveredGrass(node, map);
						} else 
							printUndiscoveredGrass(node);
						break;
					case Mountain: 
						if (node.isDiscovered()) {
							printDiscoveredMountain(node);
						}
						else 
							printUndiscoveredMountain(node);
						break;
					case Water: 
						printWater(node);
						break;
					default:
						break;
					}
				}
			}
			System.out.println();
	     }
		System.out.println();
	}
	
	private void printFort(PlayerHalfMapNode node) {
		System.out.print(color.white + "Ӆ" + color.reset);
		if (node.getPlayerPositionState() == EPlayerPositionState.MyPlayerPosition)
			System.out.print(color.blue + "⚉" + color.reset);
		else if (node.getPlayerPositionState() == EPlayerPositionState.EnemyPlayerPosition)
			System.out.print(color.red + "⚉" + color.reset);
		else if (node.getPlayerPositionState() == EPlayerPositionState.BothPlayerPosition)
			System.out.print(color.white + "⚔" + color.reset);

		else 
			System.out.print(color.white + "Ӆ" + color.reset);
		System.out.print(color.white + "Ӆ " + color.reset);
	}
	
	private void printDiscoveredGrass(PlayerHalfMapNode node, ClientMap map) {
		System.out.print(color.green + "Ш" + color.reset);
		if (node.getPlayerPositionState() == EPlayerPositionState.MyPlayerPosition)
			System.out.print(color.blue + "⚉" + color.reset);
		else if (node.getPlayerPositionState() == EPlayerPositionState.EnemyPlayerPosition)
			System.out.print(color.red + "⚉" + color.reset);
		else if (node.getPlayerPositionState() == EPlayerPositionState.BothPlayerPosition)
			System.out.print(color.white + "⚔" + color.reset);
		else if (map.getTreasurePosition() != null && map.getTreasurePosition().equals(node)) {
			System.out.print(color.yellow + "Ѱ" + color.reset);
		}

		else 
			System.out.print(color.green + "Ш" + color.reset);
		System.out.print(color.green + "Ш " + color.reset);
	}
	
	private void printUndiscoveredGrass(PlayerHalfMapNode node) {
		System.out.print("Ш");
		if (node.getPlayerPositionState() == EPlayerPositionState.MyPlayerPosition)
			System.out.print(color.blue + "⚉" + color.reset);
		else if (node.getPlayerPositionState() == EPlayerPositionState.EnemyPlayerPosition)
			System.out.print(color.red + "⚉" + color.reset);
		else if (node.getPlayerPositionState() == EPlayerPositionState.BothPlayerPosition)
			System.out.print(color.white + "⚔" + color.reset);

		else 
			System.out.print("Ш");
		System.out.print("Ш ");
	}
	
	private void printDiscoveredMountain(PlayerHalfMapNode node) {
		System.out.print(color.yellow + "Ѧ" + color.reset);
		if (node.getPlayerPositionState() == EPlayerPositionState.MyPlayerPosition)
			System.out.print(color.blue + "⚉" + color.reset);
		else if (node.getPlayerPositionState() == EPlayerPositionState.EnemyPlayerPosition)
			System.out.print(color.red + "⚉" + color.reset);
		else if (node.getPlayerPositionState() == EPlayerPositionState.BothPlayerPosition)
			System.out.print(color.white + "⚔" + color.reset);

		else 
			System.out.print(color.yellow + "Ѧ" + color.reset);
		System.out.print(color.yellow + "Ѧ " + color.reset);
	}
	
	private void printUndiscoveredMountain(PlayerHalfMapNode node) {
		System.out.print("Ѧ");
		if (node.getPlayerPositionState() == EPlayerPositionState.MyPlayerPosition)
			System.out.print(color.blue + "⚉" + color.reset);
		else if (node.getPlayerPositionState() == EPlayerPositionState.EnemyPlayerPosition)
			System.out.print(color.red + "⚉" + color.reset);
		else if (node.getPlayerPositionState() == EPlayerPositionState.BothPlayerPosition)
			System.out.print(color.white + "⚔" + color.reset);

		else 
			System.out.print("Ѧ");
		System.out.print("Ѧ ");
	}
	
	private void printWater(PlayerHalfMapNode node) {
		System.out.print(color.cyan + "ʬ" + color.reset);
		if (node.getPlayerPositionState() == EPlayerPositionState.MyPlayerPosition)
			System.out.print(color.blue + "⚉" + color.reset);
		else if (node.getPlayerPositionState() == EPlayerPositionState.EnemyPlayerPosition)
			System.out.print(color.red + "⚉" + color.reset);
		else if (node.getPlayerPositionState() == EPlayerPositionState.BothPlayerPosition)
			System.out.print(color.white + "⚔" + color.reset);

		else 
			System.out.print(color.cyan + "ʬ" + color.reset);
		System.out.print(color.cyan + "ʬ " + color.reset);
	}
	
	private void reportLoss() {
		System.out.println("LOST!");
	}
	
	private void reportWin() {
		System.out.println("WON!");
	}
}