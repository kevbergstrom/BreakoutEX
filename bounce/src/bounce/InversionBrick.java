package bounce;

import org.newdawn.slick.Color;
import org.newdawn.slick.state.StateBasedGame;

public class InversionBrick extends Brick{

	public InversionBrick(float x, float y, int bx, int by) {
		super(x, y, bx, by, new Color(0,0,255));
		
	}
	
	public void onHit(StateBasedGame game) {
		BounceGame bg = (BounceGame)game;
		bg.paddle.reverseControls();
	}

}
