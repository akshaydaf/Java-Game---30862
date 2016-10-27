package com.brackeen.javagamebook.graphics;

import java.awt.Image;

public class Sprite {

    protected Animation anim;
    // position (pixels)
    private float x;
    private float y;
    // velocity (pixels per millisecond)
    private float dx;
    private float dy;
    private int health;
    private int ammo;
    
    private int fireMode;
    
    public static final int MAX_HEALTH = 40;
    public static final int MAX_BULLETS = 10;
    
    public static final int HOLDTIME = 1000;
    public static final int RELOADTIME = 1000;
    public static final int AUTOTIME = 500;
    public static final int SINGLETIME = 250;
    
    private boolean fireEN;
    
    public static final int MAN = 0;
    public static final int HOLD_WAIT = 1;
    public static final int AUTO = 2;
    public static final int RELOAD = 3;
    private long holdTimer;	//how long the button has been held
    private long reloadTimer;
    private long autoTimer;
    private long singleTimer;
    private boolean firePressed;
    private boolean firstShot;
    public boolean friendly;
    
    
    
    
    
    //PLAYER SHIT
    
    public long getHoldTimer(){
    	return this.holdTimer;
    }
    public long getReloadTimer(){
    	return this.reloadTimer;
    }
    public long getAutoTimer(){
    	return this.autoTimer;
    }
    public long getSingleTimer(){
    	return this.singleTimer;
    }
    public void setFirePressed(boolean pressed){
    	this.firePressed = pressed;
    }
    public boolean getFirePressed(){
    	return (this.firePressed);
    }
    public void setFireEN(boolean en){
    	this.firePressed = en;
    }
    public boolean getFireen(){
    	return (this.fireEN);
    }
    
    public int getHealth(){
    	return this.health;
    }  
    public void setHealth(int newHealth){
    	this.health = newHealth;
    }
    public void adjustHealth(int dmg) {
    	this.health += dmg;
    	if (this.health > 40) {
    		this.health = 40;
    	}
    }
    public int getGunMode(){
    	return this.fireMode;
    }
    public int getAmmo(){
    	return this.ammo;
    }
    
    
    
    /**
        Creates a new Sprite object with the specified Animation.
    */
    public Sprite(Animation anim) {
        this.anim = anim;
        health = 20;
        fireEN = true;
        fireMode = HOLD_WAIT;
        holdTimer = 0;	//how long the button has been held
        reloadTimer = 0;
        autoTimer = 0;
        singleTimer = 0;
        firePressed = false;
        firstShot = true;
        friendly = true;
        ammo = MAX_BULLETS;
    }

    /**
        Updates this Sprite's Animation and its position based
        on the velocity.
    */
    public void update(long elapsedTime) {         
        x += dx * elapsedTime;
        y += dy * elapsedTime;
        anim.update(elapsedTime);
        System.out.println(elapsedTime);
    }
    
    public void updateGun(long elapsedTime) {//ALSO UPDATE STATE MACHINE SHIT YO
        if (this.fireMode == HOLD_WAIT){
        	if (this.firstShot){
        		this.firstShot = false;
        		this.fireEN = true;
        	}
        	if (this.holdTimer > HOLDTIME){
        		this.fireMode = AUTO;
        		this.holdTimer = 0;
        		this.firstShot = true;
        	}
        	else if (this.firePressed == false){
        		this.holdTimer = 0;
        		this.firstShot = true;
        		this.fireEN = true;
        	}
        	else if (this.firePressed == true){
        		this.holdTimer += elapsedTime;
        	}
        }
        else if (this.fireMode == AUTO){
        	if (this.firePressed == false){
        		this.holdTimer = 0;
        		this.fireMode = HOLD_WAIT;
        		this.autoTimer = 0;
        	}
        	else if (this.firePressed == true){
        		this.autoTimer += elapsedTime;
        		if (this.autoTimer >= AUTOTIME){
        			this.autoTimer = 0;
        			this.ammo -= 1;
        			this.fireEN = true;
        		}
        		if(this.ammo <= 0){
        			this.fireMode = RELOAD;
        			this.ammo = 0;
        		}
        	}
        }
        else if (this.fireMode == RELOAD){
        	
        	this.reloadTimer += elapsedTime;
        	if (this.reloadTimer >= RELOADTIME){
        		this.reloadTimer = 0;
        		this.fireEN = true;
        		this.ammo = MAX_BULLETS;
        		this.fireMode = AUTO;
        		if (this.firePressed){
        			fireMode = AUTO;
        		} else {
        			this.fireMode = HOLD_WAIT;
        			this.reloadTimer = 0;
        			this.autoTimer = 0;
        			this.holdTimer = 0;
        		}
        	
        		
        	}
        }
    }

    /**
        Gets this Sprite's current x position.
    */
    public float getX() {
        return x;
    }

    /**
        Gets this Sprite's current y position.
    */
    public float getY() {
        return y;
    }

    /**
        Sets this Sprite's current x position.
    */
    public void setX(float x) {
        this.x = x;
    }

    /**
        Sets this Sprite's current y position.
    */
    public void setY(float y) {
        this.y = y;
    }

    /**
        Gets this Sprite's width, based on the size of the
        current image.
    */
    public int getWidth() {
        return anim.getImage().getWidth(null);
    }

    /**
        Gets this Sprite's height, based on the size of the
        current image.
    */
    public int getHeight() {
        return anim.getImage().getHeight(null);
    }

    /**
        Gets the horizontal velocity of this Sprite in pixels
        per millisecond.
    */
    public float getVelocityX() {
        return dx;
    }

    /**
        Gets the vertical velocity of this Sprite in pixels
        per millisecond.
    */
    public float getVelocityY() {
        return dy;
    }

    /**
        Sets the horizontal velocity of this Sprite in pixels
        per millisecond.
    */
    public void setVelocityX(float dx) {
        this.dx = dx;
    }

    /**
        Sets the vertical velocity of this Sprite in pixels
        per millisecond.
    */
    public void setVelocityY(float dy) {
        this.dy = dy;
    }

    /**
        Gets this Sprite's current image.
    */
    public Image getImage() {
        return anim.getImage();
    }

    /**
        Clones this Sprite. Does not clone position or velocity
        info.
    */
    public Object clone() {
        return new Sprite(anim);
    }
}
