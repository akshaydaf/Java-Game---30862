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
		state = STATE_NORMAL;
		 
		// TODO Auto-generated constructor stub
	}
	private static final float bulletspeed = -0.95f;
	
	public boolean status;
	
	
	public int state;
    private long stateTime;
    private long decayTime;
    public long bulletTimer;
    
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
    public Object clone() {
        // use reflection to create the correct subclass
        Constructor constructor = getClass().getConstructors()[0];
        try {
            return constructor.newInstance(new Object[] {
                (Animation)anim.clone(),
            });
        }
        catch (Exception ex) {
            // should never happen
            ex.printStackTrace();
            return null;
        }
    }
    
	public int getState() {
		return this.state;
	}
	
	public void collide(){
		setState(Bullet.STATE_DYING);
		setVelocityX(0);
		setVelocityY(0);
	}
	
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
        bulletTimer += elapsedTime;
        
        if (state == STATE_DYING && stateTime >= DIE_TIME) {
            setState(STATE_DEAD);
        }
        else if (state == STATE_NORMAL && decayTime >= DECAY_TIME){
        	setState(STATE_DEAD);
        }
        if (state == STATE_NORMAL && bulletTimer >= 1000){
        	setState(STATE_DEAD);
        }
    }

}