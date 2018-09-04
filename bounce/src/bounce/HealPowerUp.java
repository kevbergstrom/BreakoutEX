package bounce;

import org.newdawn.slick.Image;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import jig.ResourceManager;

public class HealPowerUp extends PowerUp{

	private int healAmount;
	
	public HealPowerUp(float x, float y) {
		super(x, y, 0, 1f);
		Image newImage = ResourceManager.getImage(BounceGame.HEAL_POWERUPIMG_RSC).getScaledCopy(40, 40);
		newImage.setFilter(Image.FILTER_NEAREST);
		addImageWithBoundingBox(newImage);
		healAmount = 1;
	}
	
	public void effect(StateBasedGame game) {
		BounceGame bg = (BounceGame)game;
		bg.health = bg.health + healAmount;
		if(bg.health>bg.maxHealth) {
			bg.health = bg.maxHealth;
		}
		bg.paddle.setHealth(bg.health);
	}

}
