package bounce;

import java.util.Random;

import org.newdawn.slick.state.StateBasedGame;

import jig.Entity;
import jig.Vector;

/**
 * The PowerUp class is an Entity that moves downward. When it
 * collides with the paddle its effect() method will be called.
 * 
 */
 class PowerUp extends Entity {

	protected Vector velocity;
	protected float speed;

	public PowerUp(final float x, final float y, final float vx, final float vy) {
		super(x, y);
		velocity = new Vector(vx, vy);
		speed = 0.2f;
	}
	
	public void setVelocity(final Vector v) {
		velocity = v;
	}
	
	public void setSpeed(float s) {
		speed = s;
	}

	public Vector getVelocity() {
		return velocity;
	}
	
	public boolean inRange(float ScreenWidth, float ScreenHeight) {
		//check if the power up is within screen bounds
		if(this.getCoarseGrainedMaxX()<0) {
			return false;
		}else if(this.getCoarseGrainedMinX()>ScreenWidth) {
			return false;
		}else if(this.getCoarseGrainedMaxY()<0) {
			return false;
		}else if(this.getCoarseGrainedMinY()>ScreenHeight) {
			return false;
		}else {
			return true;
		}
	}
	
	public void effect(StateBasedGame game) {
		//Will trigger when colliding with the paddle
	}

	public void update(final int delta) {
		translate(velocity.scale(delta*speed));
	}
	
	/**
	 * Spawns one of the PowerUp types chosen at random
	 */
	public static PowerUp spawnRandomPowerUp(float ScreenWidth) {
		Random rand = new Random();
		int PUNum = rand.nextInt(6);
		float PUPosX = 20+(float)rand.nextInt((int)ScreenWidth-20);
		float PUPosY = 20;
		
		if(PUNum==0) {
			return new AcceleratePowerUp(PUPosX,PUPosY);
		}else if(PUNum==1) {
			return new HealPowerUp(PUPosX,PUPosY);
		}else if(PUNum==2) {
			return new MorePowerUp(PUPosX,PUPosY);
		}else if(PUNum==3) {
			return new ProjectilePowerUp(PUPosX,PUPosY);
		}else if(PUNum==4) {
			return new SlowPowerUp(PUPosX,PUPosY);
		}else {
			return new XPowerUp(PUPosX,PUPosY);
		}
	}
}
