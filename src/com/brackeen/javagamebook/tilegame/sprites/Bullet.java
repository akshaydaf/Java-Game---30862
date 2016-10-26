package com.brackeen.javagamebook.tilegame.sprites;
import java.lang.reflect.Constructor;
import com.brackeen.javagamebook.graphics.*;
public class Bullet extends Sprite {
	public Bullet(Animation anim) {
		super(anim);
		// TODO Auto-generated constructor stub
	}
	private static final float bulletspeed = -0.95f;
	private float ypos;
	private float xpos;
	private float dx;
	public float getX() {
		return 0;
        //current position of the player, where the bullet will come from
    }
	public void setX(float xpos){
		this.xpos = xpos;
	}
	public void setY(float ypos){
		this.ypos = ypos;
	}	
	
}