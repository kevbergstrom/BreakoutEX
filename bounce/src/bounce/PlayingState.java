package bounce;

import java.util.Iterator;
import jig.Vector;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;


/**
 * This state is active when the Game is being played. In this state, sound is
 * turned on, the ball moves freely. The players health and score are decided
 * in this state. Destroying all of the bricks will move to the ResultsScreenState. 
 * . Losing all of your health will move to the GameOverState.
 *  The user can also control the paddle using the WAS & D keys.
 * 
 * Transitions From StartUpState
 * 
 * Transitions To GameOverState or ResultsScreenState
 */
class PlayingState extends BasicGameState {
	
	private float timeTaken;
	private int powerUpsGot;
	private int damageTaken;
	
	@Override
	public void init(GameContainer container, StateBasedGame game)
			throws SlickException {
	}

	@Override
	public void enter(GameContainer container, StateBasedGame game) {
		container.setSoundOn(true);
		//set up variables for score calculating
		timeTaken = 0f;
		powerUpsGot = 0;
		damageTaken = 0;
	}
	@Override
	public void render(GameContainer container, StateBasedGame game,
			Graphics g) throws SlickException {
		BounceGame bg = (BounceGame)game;
		
		if(bg.background!=null) {
			g.drawImage(bg.background, 0, 0);
		}
		
		for (BallTrail t : bg.trails)
			t.render(g);
		
		bg.ball.render(g);
		bg.paddle.render(g);

		for (Brick br : bg.bricks)
			br.render(g);
		for (Bang b : bg.explosions)
			b.render(g);
		for (Projectile p : bg.projectiles)
			p.render(g);
		for (PowerUp pu : bg.powerups)
			pu.render(g);
		
		g.drawString("Level: " + bg.currentLevel, 10, 30);
			
		if(bg.invincibility) {
			g.drawString("Invinciblility: On", 10, 50);
		}
		
		if(bg.paddle.getProjShield()) {
			bg.paddle.renderProjShield(g);
		}
		
	}
	
	@Override
	public void update(GameContainer container, StateBasedGame game,
			int delta) throws SlickException {
		
		if(delta>100) {
			//ignore large deltas
			return;
		}
		
		Input input = container.getInput();
		BounceGame bg = (BounceGame)game;
		timeTaken += delta;
		
		//give the paddle velocity only if the player is pressing a key
		bg.paddle.setVelocity(new Vector(0, 0));

		if (input.isKeyDown(Input.KEY_W)) {
			bg.paddle.setVelocity(bg.paddle.getVelocity().add(new Vector(0f, -1.0f)));
		}
		if (input.isKeyDown(Input.KEY_S)) {
			bg.paddle.setVelocity(bg.paddle.getVelocity().add(new Vector(0f, +1.0f)));
		}
		if (input.isKeyDown(Input.KEY_A)) {
			bg.paddle.setVelocity(bg.paddle.getVelocity().add(new Vector(-1.0f, 0)));
		}
		if (input.isKeyDown(Input.KEY_D)) {
			bg.paddle.setVelocity(bg.paddle.getVelocity().add(new Vector(+1.0f, 0f)));
		}
		//Invincibility cheat
		if (input.isKeyDown(Input.KEY_INSERT))
			bg.invincibility = true;
		//level warp cheats
		if(input.isKeyDown(Input.KEY_F1)) {
			bg.currentLevel = 0;
			bg.enterState(BounceGame.STARTUPSTATE);	
		}else if(input.isKeyDown(Input.KEY_F2)) {
			bg.currentLevel = 1;
			bg.enterState(BounceGame.STARTUPSTATE);	
		}else if(input.isKeyDown(Input.KEY_F3)) {
			bg.currentLevel = 2;
			bg.enterState(BounceGame.STARTUPSTATE);	
		}else if(input.isKeyDown(Input.KEY_F4)) {
			bg.currentLevel = 3;
			bg.enterState(BounceGame.STARTUPSTATE);	
		}else if(input.isKeyDown(Input.KEY_F5)) {
			bg.currentLevel = 4;
			bg.enterState(BounceGame.STARTUPSTATE);	
		}else if(input.isKeyDown(Input.KEY_F6)) {
			bg.currentLevel = 5;
			bg.enterState(BounceGame.STARTUPSTATE);	
		}else if(input.isKeyDown(Input.KEY_F7)) {
			bg.currentLevel = 6;
			bg.enterState(BounceGame.STARTUPSTATE);	
		}else if(input.isKeyDown(Input.KEY_F12)) {
			//instantly win level
			bg.bricks.clear();
		}
		
		boolean bounced = false;
		
		//ball and paddle collision
		if(bg.ball.collides(bg.paddle) != null) {
			//check the side of the collision
			int sideOfCol = bg.ball.sideOfCollision(bg.paddle);
			if(sideOfCol == 0) {
				bg.ball.setPosition(new Vector(bg.ball.getX(),bg.paddle.getCoarseGrainedMinY()-
						bg.ball.getCoarseGrainedHeight()/2));
				//only collide if the ball is moving towards the paddle
				if(bg.ball.getVelocity().getY()>0) {
					bg.ball.bounce(0);
					bounced = true;
				}
			}else if(sideOfCol == 1) {
				bg.ball.setPosition(new Vector(bg.ball.getX(),bg.paddle.getCoarseGrainedMaxY()+
						bg.ball.getCoarseGrainedHeight()/2));
				if(bg.ball.getVelocity().getY()<0) {
					bg.ball.bounce(0);
					bounced = true;
				}
			}else if(sideOfCol == 2) {
				bg.ball.setPosition(new Vector(bg.paddle.getCoarseGrainedMinX()-
						bg.ball.getCoarseGrainedWidth()/2,bg.ball.getY()));
				if(bg.ball.getVelocity().getX()>0) {
					bg.ball.bounce(90);
					bounced = true;
				}
			}else if(sideOfCol == 3) {
				bg.ball.setPosition(new Vector(bg.paddle.getCoarseGrainedMaxX()+
						bg.ball.getCoarseGrainedWidth()/2,bg.ball.getY()));
				if(bg.ball.getVelocity().getX()<0) {
					bg.ball.bounce(90);
					bounced = true;
				}
			}
		}
		
		//ball and bricks collision
		
		// check bricks for collision and remove dead ones
		float nextBounce = -1;
		for (Iterator<Brick> i = bg.bricks.iterator(); i.hasNext();) {
			Brick nextBrick = i.next();
			if(nextBrick.isActive()) {
				if(bg.ball.collides(nextBrick) != null) {
					int sideOfCol = bg.ball.sideOfCollision(nextBrick);
					nextBrick.onHit(bg);
					if(sideOfCol == 0) {
						if(bg.ball.getVelocity().getY()>0) {
							nextBrick.damageBrick(bg.ball.getDamage(), game);
							nextBounce = 0;
						}
					}else if(sideOfCol == 1) {
						if(bg.ball.getVelocity().getY()<0) {
							nextBrick.damageBrick(bg.ball.getDamage(), game);
							nextBounce = 0;
						}
					}else if(sideOfCol == 2) {
						if(bg.ball.getVelocity().getX()>0) {
							nextBrick.damageBrick(bg.ball.getDamage(), game);
							nextBounce = 90;
						}
					}else if(sideOfCol == 3) {
						if(bg.ball.getVelocity().getX()<0) {
							nextBrick.damageBrick(bg.ball.getDamage(), game);
							nextBounce = 90;
						}
					}
				}
			}
			if (!nextBrick.isActive()) {
				nextBrick.onDeath(bg);
				i.remove();
			}
		}
		//only bounce off of bricks once each frame
		if(nextBounce>-1) {
			bg.ball.bounce(nextBounce);
			bounced = true;
		}
		
		//check if the ball is out of bounds
		//only bounce the ball out if it is moving towards the bound and colliding with it
		if ((bg.ball.getVelocity().getX()>0 && bg.ball.getCoarseGrainedMaxX() > bg.ScreenWidth)
				|| (bg.ball.getVelocity().getX()<0 && bg.ball.getCoarseGrainedMinX() < 0)) {
			bg.ball.bounce(90);
			bounced = true;
		} else if (bg.ball.getVelocity().getY()<0 && bg.ball.getCoarseGrainedMinY() < 0) {
			bg.ball.bounce(0);
			bounced = true;
		}else if(bg.ball.getCoarseGrainedMaxY() > bg.ScreenHeight) {
			//the ball hit the bottom of the screen
			bg.ball.reset();
			bg.health = bg.health -1;
			damageTaken += 1;
			if(bg.health<0) {
				bg.health = 0;
			}
			bg.paddle.setHealth(bg.health);
			
		}
		//add an explosion if the ball bounced
		if (bounced) {
			bg.explosions.add(new Bang(bg.ball.getX(), bg.ball.getY()));
		}
		
		//update the projectiles
		for (Iterator<Projectile> i = bg.projectiles.iterator(); i.hasNext();) {
			Projectile nextProj = i.next();
			nextProj.update(delta);
			if (!nextProj.inRange(bg.ScreenWidth, bg.ScreenHeight)) {
				i.remove();
			}else if(bg.paddle.collides(nextProj) != null) {
				
				if(!bg.paddle.getProjShield() && !bg.paddle.getiFrame()) {
					//if the player isn't invulnerable
					bg.health -= nextProj.getDamage();
					damageTaken += nextProj.getDamage();
					bg.paddle.turnOniFrame();
					if(bg.health<0) {
						bg.health = 0;
					}
					bg.paddle.setHealth(bg.health);
				}
				i.remove();
			}
		}
		
		//PowerUp spawning
		 bg.powerUpTimer =  bg.powerUpTimer - delta;
		 if(bg.powerUpTimer<0) {
			 bg.powerUpTimer = bg.powerUpDelay;
			 //generate a random PowerUp
			 PowerUp newPU = PowerUp.spawnRandomPowerUp(bg.ScreenWidth-20);
			 if(newPU!=null) {
				 bg.powerups.add(newPU);
			 }
		 }
		
		//update the PowerUps
		for (Iterator<PowerUp> i = bg.powerups.iterator(); i.hasNext();) {
			PowerUp nextPU = i.next();
			nextPU.update(delta);
			if (!nextPU.inRange(bg.ScreenWidth, bg.ScreenHeight)) {
				i.remove();
			}else if(bg.paddle.collides(nextPU) != null) {
				powerUpsGot++;
				nextPU.effect(game);
				i.remove();
			}
		}
		
		// check if there are any finished explosions, if so remove them
		for (Iterator<Bang> i = bg.explosions.iterator(); i.hasNext();) {
			if (!i.next().isActive()) {
				i.remove();
			}
		}
		
		//BallTrail spawning
		 bg.trailTimer =  bg.trailTimer - delta;
		 if(bg.trailTimer<0) {
			 bg.trailTimer = bg.trailDelay;
			 //generate a new trail
			 BallTrail newT = new BallTrail(bg.ball.getX(),bg.ball.getY());
			 if(newT!=null) {
				 bg.trails.add(newT);
			 }
		 }
		 
		//update the BallTrails
		for (Iterator<BallTrail> i = bg.trails.iterator(); i.hasNext();) {
			BallTrail nextT = i.next();
			nextT.update(delta);
			if(!nextT.isActive()) {
				i.remove();
			}
		}
		
		bg.ball.update(delta);
		bg.paddle.update(delta);

		//check if the player has lost
		if (bg.health<=0 && !bg.invincibility) {
			//player lost
			game.enterState(BounceGame.GAMEOVERSTATE);
		}else if(bg.bricks.size()==0) {
			//player won
			((ResultsScreenState)game.getState(BounceGame.RESULTSSCREENSTATE)).setUserScore(timeTaken,powerUpsGot,damageTaken);
			game.enterState(BounceGame.RESULTSSCREENSTATE);
		}
	}

	@Override
	public int getID() {
		return BounceGame.PLAYINGSTATE;
	}
	
}