package com.brackeen.javagamebook.tilegame;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;

import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.sampled.AudioFormat;

import com.brackeen.javagamebook.graphics.*;
import com.brackeen.javagamebook.sound.*;
import com.brackeen.javagamebook.input.*;
import com.brackeen.javagamebook.test.GameCore;
import com.brackeen.javagamebook.tilegame.sprites.*;
import com.brackeen.javagamebook.tilegame.bulletQClass;

/**
    GameManager manages all parts of the game.
 */
public class GameManager extends GameCore {

	public static void main(String[] args) {
		new GameManager().run();
	}

	// uncompressed, 44100Hz, 16-bit, mono, signed, little-endian
	private static final AudioFormat PLAYBACK_FORMAT =
			new AudioFormat(44100, 16, 1, true, false);

	private static final int DRUM_TRACK = 1;

	public static final float GRAVITY = 0.002f;

	private Point pointCache = new Point();
	private TileMap map;
	private MidiPlayer midiPlayer;
	private SoundManager soundManager;
	private ResourceManager resourceManager;
	private Sound prizeSound;
	private Sound boopSound;
	private Sound hitSound;
	private Sound deathSound;
	private Sound shroomTrippinSound;
	private Sound shootSound;
	private Sound hitSelfSound;
	private InputManager inputManager;
	private TileMapRenderer renderer;
    public static final int HEALTHTIME = 1000; //timer to count to one second for the health
    
    
	private GameAction moveLeft;
	private GameAction moveRight;
	private GameAction jump;
	private GameAction shoot;
	private GameAction exit;
	private long shottimer;
	private long stilltimer;
	private long prevtile;
	private long currtile;

	public void init() {
		super.init();

		// set up input manager
		initInput();

		// start resource manager
		resourceManager = new ResourceManager(
				screen.getFullScreenWindow().getGraphicsConfiguration());

		// load resources
		renderer = new TileMapRenderer();
		renderer.setBackground(
				resourceManager.loadImage("background.png"));

		// load first map
		map = resourceManager.loadNextMap();

		// load sounds
		soundManager = new SoundManager(PLAYBACK_FORMAT);
		prizeSound = soundManager.getSound("sounds/prize.wav");
		boopSound = soundManager.getSound("sounds/boop2.wav");
		// hitSound = soundManager.getSound("sounds/hitEnemy.wav");
		//        hitSelfSound = soundManager.getSound("sounds/hitSelf.wav");
		//        deathSound = soundManager.getSound("sounds/death.wav");
		shroomTrippinSound = soundManager.getSound("sounds/shroom.wav");
		//        shootSound = soundManager.getSound("sounds/prize.wav");

		// start music
		midiPlayer = new MidiPlayer();
		Sequence sequence =
				midiPlayer.getSequence("sounds/music.midi");
		midiPlayer.play(sequence, true);
		toggleDrumPlayback();
		stilltimer = -2000;
	}


	/**
        Closes any resources used by the GameManager.
	 */
	public void stop() {
		super.stop();
		midiPlayer.close();
		soundManager.close();
	}


	private void initInput() {
		moveLeft = new GameAction("moveLeft");
		moveRight = new GameAction("moveRight");
		jump = new GameAction("jump",
				GameAction.DETECT_INITAL_PRESS_ONLY);
		exit = new GameAction("exit",
				GameAction.DETECT_INITAL_PRESS_ONLY);

		shoot = new GameAction("shoot");

		inputManager = new InputManager(
				screen.getFullScreenWindow());
		inputManager.setCursor(InputManager.INVISIBLE_CURSOR);

		inputManager.mapToKey(moveLeft, KeyEvent.VK_LEFT);
		inputManager.mapToKey(moveRight, KeyEvent.VK_RIGHT);
		inputManager.mapToKey(jump, KeyEvent.VK_SPACE);
		inputManager.mapToKey(exit, KeyEvent.VK_ESCAPE);
		inputManager.mapToKey(shoot, KeyEvent.VK_F);	 	//map shoot to s

	}


	private void checkInput(long elapsedTime) {

		if (exit.isPressed()) {
			stop();
		}

		Player player = (Player)map.getPlayer();
		if (player.isAlive()) {
			float velocityX = 0;
			if (moveLeft.isPressed()) {
				velocityX-=player.getMaxSpeed();
				player.lastFacing = false;
			}
			if (moveRight.isPressed()) {
				velocityX+=player.getMaxSpeed();
				player.lastFacing = true;
			}
			if (jump.isPressed()) {
				player.jump(false);
			}
			if (shoot.isPressed()){
				player.setFirePressed(true);
				if (player.getGunMode() != Player.RELOAD){
					if (player.lastFacing && shottimer == 0 || ((player.getAutoTimer() == 0) && (player.getGunMode() == Player.AUTO))){
						resourceManager.addAttackBull(map, player.getX() + 60, player.getY() + 25, player.lastFacing, true);
					}
					else if (shottimer == 0 || ((player.getAutoTimer() == 0) && (player.getGunMode() == Player.AUTO))) {
						resourceManager.addAttackBull(map, player.getX(), player.getY() + 25, player.lastFacing, true);
					}
					shottimer+=elapsedTime;
				}
			}
			else {//if shoot not pressed
				player.setFirePressed(false);
				shottimer = 0;
			}
			player.setVelocityX(velocityX);
		}

	}

	public void draw(Graphics2D g) {
		renderer.draw(g, map,
				screen.getWidth(), screen.getHeight());
	}


	/**
        Gets the current map.
	 */
	public TileMap getMap() {
		return map;
	}


	/**
        Turns on/off drum playback in the midi music (track 1).
	 */
	public void toggleDrumPlayback() {
		Sequencer sequencer = midiPlayer.getSequencer();
		if (sequencer != null) {
			sequencer.setTrackMute(DRUM_TRACK,
					!sequencer.getTrackMute(DRUM_TRACK));
		}
	}


	/**
        Gets the tile that a Sprites collides with. Only the
        Sprite's X or Y should be changed, not both. Returns null
        if no collision is detected.
	 */
	public Point getTileCollision(Sprite sprite,
			float newX, float newY)
	{
		float fromX = Math.min(sprite.getX(), newX);
		float fromY = Math.min(sprite.getY(), newY);
		float toX = Math.max(sprite.getX(), newX);
		float toY = Math.max(sprite.getY(), newY);

		// get the tile locations
		int fromTileX = TileMapRenderer.pixelsToTiles(fromX);
		int fromTileY = TileMapRenderer.pixelsToTiles(fromY);
		int toTileX = TileMapRenderer.pixelsToTiles(
				toX + sprite.getWidth() - 1);
		int toTileY = TileMapRenderer.pixelsToTiles(
				toY + sprite.getHeight() - 1);

		// check each tile for a collision
		for (int x=fromTileX; x<=toTileX; x++) {
			for (int y=fromTileY; y<=toTileY; y++) {
				if (x < 0 || x >= map.getWidth() ||
						map.getTile(x, y) != null)
				{
					// collision found, return the tile
					pointCache.setLocation(x, y);
					return pointCache;
				}
			}
		}

		// no collision found
		return null;
	}


	/**
        Checks if two Sprites collide with one another. Returns
        false if the two Sprites are the same. Returns false if
        one of the Sprites is a Creature that is not alive.
	 */
	public boolean isCollision(Sprite s1, Sprite s2) {
		// if the Sprites are the same, return false
		if (s1 == s2) {
			return false;
		}

		// if one of the Sprites is a dead Creature, return false
		if (s1 instanceof Creature && !((Creature)s1).isAlive()) {
			return false;
		}
		if (s2 instanceof Creature && !((Creature)s2).isAlive()) {
			return false;
		}

		// get the pixel location of the Sprites
		int s1x = Math.round(s1.getX());
		int s1y = Math.round(s1.getY());
		int s2x = Math.round(s2.getX());
		int s2y = Math.round(s2.getY());

		// check if the two sprites' boundaries intersect
		return (s1x < s2x + s2.getWidth() &&
				s2x < s1x + s1.getWidth() &&
				s1y < s2y + s2.getHeight() &&
				s2y < s1y + s1.getHeight());
	}


	/**
        Gets the Sprite that collides with the specified Sprite,
        or null if no Sprite collides with the specified Sprite.
	 */
	public Sprite getSpriteCollision(Sprite sprite) {

		// run through the list of Sprites
		Iterator i = map.getSprites();
		while (i.hasNext()) {
			Sprite otherSprite = (Sprite)i.next();
			if (isCollision(sprite, otherSprite)) {
				// collision found, return the Sprite
				return otherSprite;
			}
		}

		// no collision found
		return null;
	}


	/**
        Updates Animation, position, and velocity of all Sprites
        in the current map.
	 */
	public void update(long elapsedTime) {
		Player player = (Player)map.getPlayer();
		Queue bulletQ = new LinkedList();

		// player is dead! start map over
		if (player.getState() == Creature.STATE_DEAD) {
			map = resourceManager.reloadMap();
			stilltimer = 0;
			prevtile = TileMapRenderer.pixelsToTiles(player.getX());
			currtile = prevtile;
			return;
		}
		if (player.getVelocityX() == 0 && player.getOnGround()){
			stilltimer += elapsedTime;
		}
		else{
			stilltimer = 0;
		}
		if (stilltimer >= HEALTHTIME){
			stilltimer = 0;
			player.adjustHealth(5);
		}
		if (prevtile != currtile){
			player.adjustHealth(1);
			prevtile = currtile;
		}
		else{
			currtile = TileMapRenderer.pixelsToTiles(player.getX());
		}
		// get keyboard/mouse input
		checkInput(elapsedTime);

		// update player
		updateCreature(player, elapsedTime);

		player.update(elapsedTime/*, player.getX()*/);
		player.updateGun(elapsedTime);


		// update other sprites
		Iterator i = map.getSprites();
		while (i.hasNext()) {
			Sprite sprite = (Sprite)i.next();
			if (sprite instanceof Creature) {
				Creature creature = (Creature)sprite;
				checkCreatureCollision(creature);
				if (creature.getState() == Creature.STATE_DEAD) {
					i.remove();
					player.adjustHealth(10);
				}
				else {
					updateCreature(creature, elapsedTime);
					if(creature.creaturefire){
						creature.getPlayerLoc(player);
						creature.updateCreatureGun(elapsedTime);
						if (creature.autotimer2 == 0){
							if (creature.getX() >= player.getX() && creature.getState() == Creature.STATE_NORMAL){
								bulletQ.add(new bulletQClass(creature.getX() + 60, creature.getY() + 25,false));
							}
							else if (creature.getState() == Creature.STATE_NORMAL){
								bulletQ.add(new bulletQClass(creature.getX(), creature.getY() + 25 ,true));
							}
						}
						
					}
				}
			}
			else if (sprite instanceof Bullet){//destroying used bullets, updating bullet positions
				Bullet bullet = (Bullet)sprite;
				if (bullet.getState() == Bullet.STATE_DEAD){
					i.remove();
				}
				else {
					updateBullet((Player) player, bullet, elapsedTime);
				}
			}
				sprite.update(elapsedTime);
		}
		Iterator j = bulletQ.iterator();
		while (j.hasNext()){
			bulletQClass bcls = (bulletQClass) j.next();
			resourceManager.addAttackBull(map, bcls.x, bcls.y, bcls.direc, false);
			j.remove();
		}
	}


	/**
        Updates the creature, applying gravity for creatures that
        aren't flying, and checks collisions.
	 */
	private void updateBullet(Player player, Bullet bullet, long elapsedTime){ 	
		float dx = bullet.getVelocityX();
		float oldX = bullet.getX();
		float newX = oldX + dx * elapsedTime;
		Point tile = getTileCollision(bullet, newX, bullet.getY());
		if (tile == null) {
			bullet.setX(newX);
		}
		else {
			// line up with the tile boundary
			if (dx > 0) {
				bullet.setX(TileMapRenderer.tilesToPixels(tile.x) - bullet.getWidth());
			}
			else if (dx < 0) {
				bullet.setX(TileMapRenderer.tilesToPixels(tile.x + 1));
			}	
			bullet.collide();	
		}
	}

	private void updateCreature(Creature creature,
			long elapsedTime)
	{

		// apply gravity
		if (!creature.isFlying()) {
			creature.setVelocityY(creature.getVelocityY() +
					GRAVITY * elapsedTime);
		}

		// change x
		float dx = creature.getVelocityX();
		float oldX = creature.getX();
		float newX = oldX + dx * elapsedTime;
		Point tile = getTileCollision(creature, newX, creature.getY());
		if (tile == null) {
			creature.setX(newX);
		}
		else {
			// line up with the tile boundary
			if (dx > 0) {
				creature.setX(
						TileMapRenderer.tilesToPixels(tile.x) -
						creature.getWidth());
			}
			else if (dx < 0) {
				creature.setX(
						TileMapRenderer.tilesToPixels(tile.x + 1));
			}
			creature.collideHorizontal();
		}
		if (creature instanceof Player) {
			checkPlayerCollision((Player)creature, false, elapsedTime);
		}


		// change y
		float dy = creature.getVelocityY();
		float oldY = creature.getY();
		float newY = oldY + dy * elapsedTime;
		tile = getTileCollision(creature, creature.getX(), newY);
		if (tile == null) {
			creature.setY(newY);
		}
		else {
			// line up with the tile boundary
			if (dy > 0) {
				creature.setY(
						TileMapRenderer.tilesToPixels(tile.y) -
						creature.getHeight());
			}
			else if (dy < 0) {
				creature.setY(
						TileMapRenderer.tilesToPixels(tile.y + 1));
			}
			creature.collideVertical();
		}
		if (creature instanceof Player) {
			boolean canKill = (oldY < creature.getY());
			checkPlayerCollision((Player)creature, canKill, elapsedTime);
		}

	}


	public void checkCreatureCollision(Creature creature)
	{
		if (!creature.isAlive()) {
			return;
		}

		// check for player collision with other sprites
		Sprite collisionSprite = getSpriteCollision(creature);

		if (collisionSprite instanceof Bullet){
			Bullet bullet = (Bullet)collisionSprite;
			//soundManager.play(hitEnemySound);
			if (bullet.friendly){
				soundManager.play(boopSound);
				bullet.setState(Bullet.STATE_DYING);
				creature.setState(Creature.STATE_DYING);
			}
		}
	}
	/**
        Checks for Player collision with other Sprites. If
        canKill is true, collisions with Creatures will kill
        them.
	 */
	public void checkPlayerCollision(Player player,
			boolean canKill, long elapsedTime)
	{
		if (!player.isAlive()) {
			return;
		}

		// check for player collision with other sprites
		Sprite collisionSprite = getSpriteCollision(player);
		if (collisionSprite instanceof PowerUp) {
			acquirePowerUp((PowerUp)collisionSprite, (Player) player);
		}
		else if (collisionSprite instanceof Creature) {
			Creature badguy = (Creature)collisionSprite;
			if (canKill) {
				// kill the badguy and make player bounce
				soundManager.play(boopSound);
				badguy.setState(Creature.STATE_DYING);
				player.setY(badguy.getY() - player.getHeight());
				player.jump(true);
			}
			else {
				// player dies!
				if (player.getInv()){
					badguy.setState(Creature.STATE_DYING);
				}
				else{
					player.setState(Creature.STATE_DYING);
				}
			}
		}
		else if (collisionSprite instanceof Bullet){
			Bullet bullet = (Bullet)collisionSprite;
			//soundManager.play(hitSelfSound);
			if (!bullet.friendly && (bullet.getState() == Bullet.STATE_NORMAL)){
				bullet.setState(Bullet.STATE_DYING);

				player.adjustHealth(-5);
				if(player.getHealth() <= 0){
					player.setHealth(0);
					player.setState(Creature.STATE_DYING);
				}
			}
		}
	}


	/**
        Gives the player the specified power up and removes it
        from the map.
	 */
	public void acquirePowerUp(PowerUp powerUp, Player player) {
		// remove it from the map
		map.removeSprite(powerUp);

		if (powerUp instanceof PowerUp.Star) {
			// do something here, like give the player points
			soundManager.play(prizeSound);
			player.setInv(true);
		}
		else if (powerUp instanceof PowerUp.Music) {
			// change the music
			soundManager.play(prizeSound);
			toggleDrumPlayback();
		}
		else if (powerUp instanceof PowerUp.Goal) {
			// advance to next map
			soundManager.play(prizeSound,
					new EchoFilter(2000, .7f), false);
			map = resourceManager.loadNextMap();
		}
		else if (powerUp instanceof PowerUp.Shroom) {
		    soundManager.play(shroomTrippinSound);
			player.adjustHealth(+5);
			
		}
		else if (powerUp instanceof PowerUp.Gas){
			player.setGunMode(Sprite.RELOAD);
		}
		else if (powerUp instanceof PowerUp.Explosion){
			player.adjustHealth(-10);
		}
	}

}
