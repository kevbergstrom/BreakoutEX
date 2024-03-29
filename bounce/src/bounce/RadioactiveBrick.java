package bounce;

import org.newdawn.slick.Color;
import org.newdawn.slick.state.StateBasedGame;

/**
 * The RadioactiveBrick class is a Brick that spawns 
 * a slime GravityProjectile when hit by the ball.
 * 
 */
public class RadioactiveBrick extends Brick{

	public RadioactiveBrick(float x, float y, int bx, int by) {
		super(x, y, bx, by, new Color(0,225,0));
		
	}
	@Override
	public void onHit(StateBasedGame game) {
		BounceGame bg = (BounceGame)game;
		Projectile proj = new GravityProjectile(this.getX(),this.getY());
		bg.projectiles.add(proj);
	}

}
