package com.brackeen.javagamebook.tilegame.sprites;

import com.brackeen.javagamebook.graphics.Animation;

/**
    The Player.
*/
public class Player extends Creature {

    private static final float JUMP_SPEED = -.95f;
    private static final int MANUAL = 0;
    private static final int AUTO = 1;
    private static final int FIRE_PERIOD = 500;

    private boolean onGround;
    private int health;
    private int autostate;
    public boolean lastFacing;
    private long autoTimer;

    public Player(Animation left, Animation right,
        Animation deadLeft, Animation deadRight)
    {
        super(left, right, deadLeft, deadRight);
        onGround = true;
    }


    public void collideHorizontal() {
        setVelocityX(0);
    }
    
    public int getHealth(){
    	return this.health;
    }
    
    public void setHealth(int newHealth){
    	this.health = newHealth;
    }
    public void adjustHealth(int dmg) {
    	this.health += dmg;
    }

    
    
    public void shoot(boolean sPressed){
    	if(sPressed = true){
    		
    	}
    	else {
    		autoTimer = 0;
    	}
    }
    
    
    
    public void collideVertical() {
        // check if collided with ground
        if (getVelocityY() > 0) {
            onGround = true;
        }
        setVelocityY(0);
    }


    public void setY(float y) {
        // check if falling
        if (Math.round(y) > Math.round(getY())) {
            onGround = false;
        }
        super.setY(y);
    }


    public void wakeUp() {
        // do nothing
    }


    /**
        Makes the player jump if the player is on the ground or
        if forceJump is true.
    */
    public void jump(boolean forceJump) {
        if (onGround || forceJump) {
            onGround = false;
            setVelocityY(JUMP_SPEED);
        }
    }


    public float getMaxSpeed() {
        return 0.5f;
    }

}
