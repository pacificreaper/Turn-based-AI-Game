package model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

import map.ClientMap;
import messagesbase.messagesfromserver.EPlayerGameState;

public class GameModel {
	private ClientMap map = new ClientMap();
	private EPlayerGameState playerState = EPlayerGameState.MustWait;
	private final PropertyChangeSupport changes = new PropertyChangeSupport(this);
	
	public void setMapForFirstTime(ClientMap map) {
		this.map = map;
	}
	
	public ClientMap getMap() {
		return map;
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		//enables to register new listeners
		changes.addPropertyChangeListener(listener);
	}
	
	public void setMap(ClientMap map) {
		ClientMap oldMap = new ClientMap(this.map.getFields());
		this.map = map;
		changes.firePropertyChange("map", oldMap, this.map);
	}
	
	public void setPlayerState(EPlayerGameState playerState) {
		EPlayerGameState oldPlayerState = this.playerState;
		this.playerState = playerState;
		changes.firePropertyChange("playerState", oldPlayerState, this.playerState);
	}
	
	public EPlayerGameState getPlayerState() {
		return this.playerState;
	}
	
	public boolean isMapDefined() {
		return !map.getFields().isEmpty();
	}
}
