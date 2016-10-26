package com.brackeen.javagamebook.tilegame.sprites;
import java.lang.reflect.Constructor;
import com.brackeen.javagamebook.graphics.*;

public class Bullet extends Sprite {
	public Bullet(Animation anim) {
		super(anim);
		state = 0;
		status = false;
		stateTime = 0;
		decayTime = 0;
		state = 0;
		// TODO Auto-generated constructor stub
	}
	private static final float bulletspeed = -0.95f;
	
	public boolean status;
	public int state;
    private long stateTime;
    private long decayTime;
    
    private static final int DIE_TIME = 100; //how long it takes for bullet to disappear on hit
    public static final int STATE_NORMAL = 0;
    public static final int STATE_DYING = 1;
    public static final int STATE_DEAD = 2;
    public static final int DECAY_TIME = 1000;	//how long bullet lasts when not hitting anything
    
    
	
    
    public void setState(int state) {
        if (this.state != state) {
            this.state = state;
            stateTime = 0;
            if (state == STATE_DYING) {
                setVelocityX(0);
                setVelocityY(0);
            }
        }
    }
    
//    public void wakeUp(boolean direc) {
//        if (getState() == STATE_NORMAL && getVelocityX() == 0) {
//        	if (direc == true){ //right
//        		setVelocityX(bulletspeed);
//        	}
//        	else {
//        		setVelocityX(-bulletspeed);
//        	}
////        }
//    }
    
	public int getState() {
		return this.state;
	}
//	public float getX() {
//		return this.xpos;
//        //current position of the player, where the bullet will come from
//    }
//	public void setX(float xpos){
//		this.xpos = xpos;
//	}
//	public void setY(float ypos){
//		this.ypos = ypos;
//	}	
	
    public void update(long elapsedTime) {
        // select the correct Animation
//        Animation newAnim = anim;
//        if (getVelocityX() < 0) {
//            newAnim = left;
//        }
//        else if (getVelocityX() > 0) {
//            newAnim = right;
//        }
//        if (state == STATE_DYING && newAnim == left) {
//            newAnim = deadLeft;
//        }
//        else if (state == STATE_DYING && newAnim == right) {
//            newAnim = deadRight;
//        }

        // update the Animation

    	anim.update(elapsedTime);

        // update to "dead" state
        stateTime += elapsedTime;
        
        if (state == STATE_DYING && stateTime >= DIE_TIME) {
            setState(STATE_DEAD);
        }
        else if (state == STATE_NORMAL && decayTime >= DECAY_TIME){
        	setState(STATE_DEAD);
        }
    }

}