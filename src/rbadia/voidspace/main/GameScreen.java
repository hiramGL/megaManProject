package rbadia.voidspace.main;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.List;
import java.util.Random;

import javax.swing.JLabel;

import rbadia.voidspace.graphics.GraphicsManager;
import rbadia.voidspace.model.Asteroid;
import rbadia.voidspace.model.BigBullet;
import rbadia.voidspace.model.Boss;
import rbadia.voidspace.model.Bullet;
import rbadia.voidspace.model.BulletBoss;
import rbadia.voidspace.model.Floor;
import rbadia.voidspace.model.MegaMan;
import rbadia.voidspace.model.Platform;
import rbadia.voidspace.sounds.SoundManager;

/**
 * Main game screen. Handles all game graphics updates and some of the game logic.
 */
public class GameScreen extends BaseScreen{
	private static final long serialVersionUID = 1L;

	private BufferedImage backBuffer;
	private Graphics2D g2d;
	
	private static final int NEW_SHIP_DELAY = 500;
	private static final int NEW_ASTEROID_DELAY = 500;
	//	private static final int NEW_ASTEROID_2_DELAY = 500;
	//	private static final int NEW_BIG_ASTEROID_DELAY = 500;

	//	private long lastShipTime;
	private long lastAsteroidTime;
	//	private long lastAsteroid2Time;
	//	private long lastBigAsteroidTime;

	private Rectangle asteroidExplosion;
	//	private Rectangle bigAsteroidExplosion;
	//	private Rectangle shipExplosion;
	//	private Rectangle bossExplosion;

	private JLabel shipsValueLabel;
	private JLabel destroyedValueLabel;
	private JLabel levelValueLabel;

	private Random rand;
	private int randNum1;
	private int randNum2;

	private Font originalFont;
	private Font bigFont;
	private Font biggestFont;
	
	private Boss bosslevel3;
	
	private boolean nextLvl = false;
	public boolean isNextLvl() {
		return nextLvl;
	}

	public void setNextLvl(boolean nextLvl) {
		this.nextLvl = nextLvl;
		
	}

	private GameStatus status;
	private SoundManager soundMan;
	private GraphicsManager graphicsMan;
	private GameLogic gameLogic;
	private InputHandler input;
	//private Platform[] platforms;

	private int boom=0;
	public int level=1;
	public int maxLevel = 3;
	//private int damage=0;
	//	private int scroll=0;
	//	private int bossHealth=0;
	//	private int delay=0;


	/**
	 * This method initializes 
	 * 
	 */
	public GameScreen() {
		super();
		// initialize random number generator
		rand = new Random();

		initialize();

		// init graphics manager
		graphicsMan = new GraphicsManager();

		// init back buffer image
		backBuffer = new BufferedImage(500, 400, BufferedImage.TYPE_INT_RGB);
		g2d = backBuffer.createGraphics();
		
	}

	/**
	 * Initialization method (for VE compatibility).
	 */
	protected void initialize() {
		// set panel properties
		this.setSize(new Dimension(500, 400));
		this.setPreferredSize(new Dimension(500, 400));
		this.setBackground(Color.BLACK);
		
	}

	/**
	 * Update the game screen.
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		// draw current backbuffer to the actual game screen
		g.drawImage(backBuffer, 0, 0, this);
	}

	/**
	 * Update the game screen's backbuffer image.
	 */
	public void updateScreen(){
		MegaMan megaMan = gameLogic.getMegaMan();
		Floor[] floor = gameLogic.getFloor();
		Platform[] numPlatforms = gameLogic.getNumPlatforms();
		List<Bullet> bullets = gameLogic.getBullets();
		Asteroid asteroid = gameLogic.getAsteroid();
		Asteroid asteroid1 = gameLogic.getAsteroid();
		Asteroid asteroid2 = gameLogic.getAsteroid();
		Boss boss3 = gameLogic.getBoss();
		List<BigBullet> bigBullets = gameLogic.getBigBullets();
		//		Asteroid asteroid2 = gameLogic.getAsteroid2();
		//		BigAsteroid bigAsteroid = gameLogic.getBigAsteroid();
				List<BulletBoss> bulletsBoss = gameLogic.getBulletBoss();
		//		List<BulletBoss2> bulletsBoss2 = gameLogic.getBulletBoss2();		
		//		Boss boss = gameLogic.getBoss();
		//		Boss boss2 = gameLogic.getBoss2();


		// set orignal font - for later use
		if(this.originalFont == null){
			this.originalFont = g2d.getFont();
			this.bigFont = originalFont;
		}

		// erase screen
		g2d.setPaint(Color.BLACK);
		g2d.fillRect(0, 0, getSize().width, getSize().height);
		
		
		
		
		
		//draw Background Image of level 3
		if(boom > 10){
		g2d.drawImage(graphicsMan.backgroundImg, 0, 0, this);
		//Draw Boss in top of the screen
	
		}
		
		//always checking for request to pass level(N)
		if(nextLvl && boom < 5){
			restructure();
			this.YouPassedLevel_1();
			this.setNextLvl(false);
		}
		if(nextLvl && (boom > 5 && boom < 10)){
			restructure2();
			this.YouPassedLevel_2();
			this.setNextLvl(false);
		}
		
		// draw 50 random stars
		if(boom < 10)
		drawStars(50);
		
		// if the game is starting, draw "Get Ready" message
		if(status.isGameStarting()){
			drawGetReady();
			return;
		}

		// if the game is over, draw the "Game Over" message
		if(status.isGameOver()){
			// draw the message
			drawGameOver();

			long currentTime = System.currentTimeMillis();
			// draw the explosions until their time passes
			if((currentTime - lastAsteroidTime) < NEW_ASTEROID_DELAY){
				graphicsMan.drawAsteroidExplosion(asteroidExplosion, g2d, this);
			}
			//			if((currentTime - lastShipTime) < NEW_SHIP_DELAY){
			//				graphicsMan.drawShipExplosion(shipExplosion, g2d, this);
			//			}
			return;
		}

		//if the game is won, draw the "You Win!!!" message
		
		if(status.isGameWon() ){
			// draw the message
			if(getBoom() == 5){
				restructure();
			YouPassedLevel_1();
			
			}
			else if(getBoom() == 10){
				restructure2();
				YouPassedLevel_2();
			}
			long currentTime = System.currentTimeMillis();
			// draw the explosions until their time passes
			if((currentTime - lastAsteroidTime) < NEW_ASTEROID_DELAY){
				graphicsMan.drawAsteroidExplosion(asteroidExplosion, g2d, this);
			}
			return;
		}
		
		

		// the game has not started yet
		if(!status.isGameStarted()){
			// draw game title screen
			initialMessage();
			return;
		}

		//draw Floor
		
		for(int i=0; i<9; i++){
			graphicsMan.drawFloor(floor[i], g2d, this, i);	
		}

		
		//		if(level==1){
		//draw Platform LV. 1
		for(int i=0; i<8; i++){
			graphicsMan.drawPlatform(numPlatforms[i], g2d, this, i);
			//			}
		}
		//		//draw Platform LV. 2
		//		else if(level==2){
		//			for(int i=0; i<8; i++){
		//			
		//				graphicsMan.drawPlatform2(numPlatforms[i], g2d, this, i);
		//			}	
		//		}

		//draw MegaMan
		if(!status.isNewMegaMan()){
			if((Gravity() == true) || ((Gravity() == true) && (Fire() == true || Fire2() == true))){
				graphicsMan.drawMegaFallR(megaMan, g2d, this);
			}
		}

		if((Fire() == true || Fire2()== true) && (Gravity()==false)){
			graphicsMan.drawMegaFireR(megaMan, g2d, this);
		}

		if((Gravity()==false) && (Fire()==false) && (Fire2()==false)){
			graphicsMan.drawMegaMan(megaMan, g2d, this);
		}

		// draw first asteroid
		if(!status.isNewAsteroid() && getBoom() <= 5){
			// draw the asteroid until it reaches the bottom of the screen

			//LEVEL 1
			if((asteroid.getX() + asteroid.getAsteroidWidth() >  0) && (getBoom() <= 5 || getBoom() == 15)){
				asteroid.translate(-asteroid.getSpeed(), 0);
				graphicsMan.drawAsteroid(asteroid, g2d, this);	
				
			}
		
			
			else if (getBoom() <= 15){
				asteroid.setLocation(this.getWidth() - asteroid.getAsteroidWidth(),
						rand.nextInt(this.getHeight() - asteroid.getAsteroidHeight() - 32));
			}	
			
		}

		else if(!status.isNewAsteroid() && getBoom() <= 10){
			// draw the asteroid until it reaches the bottom of the screen
			//LEVEL 2
			
			if((asteroid.getX() + asteroid.getAsteroidWidth() >  0)){
				asteroid.translate(-asteroid.getSpeed(), asteroid.getSpeed()/2);
				graphicsMan.drawAsteroid(asteroid, g2d, this);	
				
			}
			else if (getBoom() <= 15){
				asteroid.setLocation(this.getWidth() - asteroid.getAsteroidWidth(),
						rand.nextInt(this.getHeight() - asteroid.getAsteroidHeight() - 32));
				
			}
			
		}
		//LEVEL 3!!!!!!
		//Making a Boss in top of the screen
		else if(!status.isNewBoss() && getBoom() > 10){
			if(boss3.getX() + boss3.getBossWidth() >= 0){
				if(boss3.crash()){
					boss3.setSpeed(boss3.getSpeed3()*-1);
					
				}
				boss3.translate(   boss3.getSpeed3(), 0);	
				graphicsMan.drawBoss(boss3, g2d, this);
				gameLogic.fireBulletBoss();
				
				}
				
			}
		
		
			
		else if(!status.isNewAsteroid() && getBoom() > 10){
			        
			
			//Making Asteroid speed random
			randNum1 = rand.nextInt(3);
			randNum2 = rand.nextInt(5);
			
			while(randNum2 == 0){
				randNum2 = rand.nextInt(3);
			}
			if((asteroid.getX() + asteroid.getAsteroidWidth() >  0)){
				
				asteroid.translate(-asteroid.getSpeed() * randNum1, asteroid.getSpeed()/randNum2);
				graphicsMan.drawAsteroid(asteroid, g2d, this);

				
				
			}
			else if (getBoom() > 10){
				asteroid.setLocation(this.getWidth() - asteroid.getAsteroidWidth(),
						rand.nextInt(this.getHeight() - asteroid.getAsteroidHeight() - 32));
				
				
			}
			}
		
		
		else{
			long currentTime = System.currentTimeMillis();
			if((currentTime - lastAsteroidTime) > NEW_ASTEROID_DELAY){
				// draw a new asteroid
				lastAsteroidTime = currentTime;
				status.setNewAsteroid(false);
				asteroid.setLocation(this.getWidth() - asteroid.getAsteroidWidth(),
						rand.nextInt(this.getHeight() - asteroid.getAsteroidHeight() - 32));
			}

			else{
				// draw explosion
				graphicsMan.drawAsteroidExplosion(asteroidExplosion, g2d, this);
			}
		}

		// draw bullets   
		for(int i=0; i<bullets.size(); i++){
			Bullet bullet = bullets.get(i);
			graphicsMan.drawBullet(bullet, g2d, this);

			boolean remove =   gameLogic.moveBullet(bullet);
			if(remove){
				bullets.remove(i);
				i--;
			}
		}

		// draw big bullets
		for(int i=0; i<bigBullets.size(); i++){
			BigBullet bigBullet = bigBullets.get(i);
			graphicsMan.drawBigBullet(bigBullet, g2d, this);

			boolean remove = gameLogic.moveBigBullet(bigBullet);
			if(remove){
				bigBullets.remove(i);
				i--;
			}
		}
		
		//draw boss bullet
		for(int i=0; i<bigBullets.size(); i++){
			BigBullet bossBullet = bigBullets.get(i);
			graphicsMan.drawBigBullet(bossBullet, g2d, this);

			boolean remove = gameLogic.moveBigBullet(bossBullet);
			if(remove){
				bigBullets.remove(i);
				i--;
			}
		}
		
		// check bullet-asteroid collisions
		for(int i=0; i<bullets.size(); i++){
			Bullet bullet = bullets.get(i);
			if(asteroid.intersects(bullet)){
				// increase asteroids destroyed count
				status.setAsteroidsDestroyed(status.getAsteroidsDestroyed() + 100);

				removeAsteroid(asteroid);


				setBoom(getBoom() + 1);
				damage=0;
				// remove bullet
				bullets.remove(i);
				break;
			}
		}

		// check big bullet-asteroid collisions
		for(int i=0; i<bigBullets.size(); i++){
			BigBullet bigBullet = bigBullets.get(i);
			if(asteroid.intersects(bigBullet)){
				// increase asteroids destroyed count
				status.setAsteroidsDestroyed(status.getAsteroidsDestroyed() + 100);

				removeAsteroid(asteroid);

			
				setBoom(getBoom() + 1);
				
				damage=0;
			}
		}

		//MM-Asteroid collision
		if(asteroid.intersects(megaMan)){
			status.setShipsLeft(status.getShipsLeft() - 1);
			removeAsteroid(asteroid);
		}

		//Asteroid-Floor collision
		for(int i=0; i<9; i++){
			if(asteroid.intersects(floor[i])){
				removeAsteroid(asteroid);

			}
		}
		


		
		
		status.getAsteroidsDestroyed();
		status.getShipsLeft();
		status.getLevel();
		
		
		// update asteroids destroyed label  
		destroyedValueLabel.setText(Long.toString(status.getAsteroidsDestroyed()));

		// update ships left label
		shipsValueLabel.setText(Integer.toString(status.getShipsLeft()));

		//update level label
		levelValueLabel.setText(Long.toString(status.getLevel()));
	}

	/**
	 * Draws the "Game Over" message.
	 */
	protected void drawGameOver() {
		String gameOverStr = "GAME OVER";

		Font currentFont = biggestFont == null? bigFont : biggestFont;
		float fontSize = currentFont.getSize2D();
		bigFont = currentFont.deriveFont(fontSize + 1).deriveFont(Font.BOLD);
		FontMetrics fm = g2d.getFontMetrics(bigFont);
		int strWidth = fm.stringWidth(gameOverStr);
		if(strWidth > this.getWidth() - 10){
			biggestFont = currentFont;
			bigFont = biggestFont;
			fm = g2d.getFontMetrics(bigFont);
			strWidth = fm.stringWidth(gameOverStr);
		}
		int ascent = fm.getAscent();
		int strX = (this.getWidth() - strWidth)/2;
		int strY = (this.getHeight() + ascent)/2;
		g2d.setFont(bigFont);
		g2d.setPaint(Color.WHITE);
		g2d.drawString(gameOverStr, strX, strY);

		boomReset();
		healthReset();
		delayReset();
	}

	public void YouPassedLevel_1() {
		String youWinStr = "You Pass";

		Font currentFont = biggestFont == null? bigFont : biggestFont;
		float fontSize = currentFont.getSize2D();
		bigFont = currentFont.deriveFont(fontSize + 1).deriveFont(Font.BOLD);
		FontMetrics fm = g2d.getFontMetrics(bigFont);
		int strWidth = fm.stringWidth(youWinStr);
		if(strWidth > this.getWidth() - 10){
			biggestFont = currentFont;
			bigFont = biggestFont;
			fm = g2d.getFontMetrics(bigFont);
			strWidth = fm.stringWidth(youWinStr);
		}
		int ascent = fm.getAscent();
		int strX = (this.getWidth() - strWidth)/2;
		int strY = (this.getHeight() + ascent)/2;
		g2d.setFont(bigFont);
		g2d.setPaint(Color.YELLOW);
		g2d.drawString(youWinStr, strX, strY);

		g2d.setFont(originalFont);
		fm = g2d.getFontMetrics();
		String newGameStr = "Next level starting soon";
		strWidth = fm.stringWidth(newGameStr);
		strX = (this.getWidth() - strWidth)/2;
		strY = (this.getHeight() + fm.getAscent())/2 + ascent + 16;
		g2d.setPaint(Color.YELLOW);
		g2d.drawString(newGameStr, strX, strY);

		setBoom(6);	//Change value in order for the next level to start

		//		boomReset();
		//		healthReset();
		//		delayReset();
	}
	protected void YouPassedLevel_2() {
		String youWinStr = "You Pass";

		Font currentFont = biggestFont == null? bigFont : biggestFont;
		float fontSize = currentFont.getSize2D();
		bigFont = currentFont.deriveFont(fontSize + 1).deriveFont(Font.BOLD);
		FontMetrics fm = g2d.getFontMetrics(bigFont);
		int strWidth = fm.stringWidth(youWinStr);
		if(strWidth > this.getWidth() - 10){
			biggestFont = currentFont;
			bigFont = biggestFont;
			fm = g2d.getFontMetrics(bigFont);
			strWidth = fm.stringWidth(youWinStr);
		}
		int ascent = fm.getAscent();
		int strX = (this.getWidth() - strWidth)/2;
		int strY = (this.getHeight() + ascent)/2;
		g2d.setFont(bigFont);
		g2d.setPaint(Color.YELLOW);
		g2d.drawString(youWinStr, strX, strY);

		g2d.setFont(originalFont);
		fm = g2d.getFontMetrics();
		String newGameStr = "Next level starting soon";
		strWidth = fm.stringWidth(newGameStr);
		strX = (this.getWidth() - strWidth)/2;
		strY = (this.getHeight() + fm.getAscent())/2 + ascent + 16;
		g2d.setPaint(Color.YELLOW);
		g2d.drawString(newGameStr, strX, strY);

		setBoom(11);	//Change value in order for the next level to start

		//		boomReset();
		//		healthReset();
		//		delayReset();
	}

	/**
	 * Draws the initial "Get Ready!" message.
	 */
	protected void drawGetReady() {
		String readyStr = "Get Ready"; 
		g2d.setFont(originalFont.deriveFont(originalFont.getSize2D() + 1));
		FontMetrics fm = g2d.getFontMetrics();
		int ascent = fm.getAscent();
		int strWidth = fm.stringWidth(readyStr);
		int strX = (this.getWidth() - strWidth)/2;
		int strY = (this.getHeight() + ascent)/2;
		g2d.setPaint(Color.WHITE);
		g2d.drawString(readyStr, strX, strY);
	}

	/**
	 * Draws the specified number of stars randomly on the game screen.
	 * @param numberOfStars the number of stars to draw
	 */
	protected void drawStars(int numberOfStars) {
		g2d.setColor(Color.WHITE);
		for(int i=0; i<numberOfStars; i++){
			int x = (int)(Math.random() * this.getWidth());
			int y = (int)(Math.random() * this.getHeight());
			g2d.drawLine(x, y, x, y);
		}
	}

	/**
	 * Display initial game title screen.
	 */
	protected void initialMessage() {
		String gameTitleStr = "Definitely Not MegaMan";

		Font currentFont = biggestFont == null? bigFont : biggestFont;
		float fontSize = currentFont.getSize2D();
		bigFont = currentFont.deriveFont(fontSize + 1).deriveFont(Font.BOLD).deriveFont(Font.ITALIC);
		FontMetrics fm = g2d.getFontMetrics(bigFont);
		int strWidth = fm.stringWidth(gameTitleStr);
		if(strWidth > this.getWidth() - 10){
			bigFont = currentFont;
			biggestFont = currentFont;
			fm = g2d.getFontMetrics(currentFont);
			strWidth = fm.stringWidth(gameTitleStr);
		}
		g2d.setFont(bigFont);
		int ascent = fm.getAscent();
		int strX = (this.getWidth() - strWidth)/2;
		int strY = (this.getHeight() + ascent)/2 - ascent;
		g2d.setPaint(Color.YELLOW);
		g2d.drawString(gameTitleStr, strX, strY);

		g2d.setFont(originalFont);
		fm = g2d.getFontMetrics();
		String newGameStr = "Press <Space> to Start a New Game.";
		strWidth = fm.stringWidth(newGameStr);
		strX = (this.getWidth() - strWidth)/2;
		strY = (this.getHeight() + fm.getAscent())/2 + ascent + 16;
		g2d.setPaint(Color.WHITE);
		g2d.drawString(newGameStr, strX, strY);

		fm = g2d.getFontMetrics();
		String itemGameStr = "Press <I> for Item Menu.";
		strWidth = fm.stringWidth(itemGameStr);
		strX = (this.getWidth() - strWidth)/2;
		strY = strY + 16;
		g2d.drawString(itemGameStr, strX, strY);

		fm = g2d.getFontMetrics();
		String shopGameStr = "Press <S> for Shop Menu.";
		strWidth = fm.stringWidth(shopGameStr);
		strX = (this.getWidth() - strWidth)/2;
		strY = strY + 16;
		g2d.drawString(shopGameStr, strX, strY);

		fm = g2d.getFontMetrics();
		String exitGameStr = "Press <Esc> to Exit the Game.";
		strWidth = fm.stringWidth(exitGameStr);
		strX = (this.getWidth() - strWidth)/2;
		strY = strY + 16;
		g2d.drawString(exitGameStr, strX, strY);
	}

	/**
	 * Prepare screen for game over.
	 */
	public void doGameOver(){
		shipsValueLabel.setForeground(new Color(128, 0, 0));
	}

	/**
	 * Prepare screen for a new game.
	 */
	public void doNewGame(){		
		lastAsteroidTime = -NEW_ASTEROID_DELAY;
		//lastBigAsteroidTime = -NEW_BIG_ASTEROID_DELAY;
		lastShipTime = -NEW_SHIP_DELAY;

		bigFont = originalFont;
		biggestFont = null;

		// set labels' text
		shipsValueLabel.setForeground(Color.BLACK);
		shipsValueLabel.setText(Integer.toString(status.getShipsLeft()));
		destroyedValueLabel.setText(Long.toString(status.getAsteroidsDestroyed()));
		levelValueLabel.setText(Long.toString(status.getLevel()));
	}

	/**
	 * Sets the game graphics manager.
	 * @param graphicsMan the graphics manager
	 */
	public void setGraphicsMan(GraphicsManager graphicsMan) {
		this.graphicsMan = graphicsMan;
	}

	/**
	 * Sets the game logic handler
	 * @param gameLogic the game logic handler
	 */
	public void setGameLogic(GameLogic gameLogic) {
		this.gameLogic = gameLogic;
		this.status = gameLogic.getStatus();
		this.soundMan = gameLogic.getSoundMan();
	}
	
	//If "N" is pressed it will request for a new level
	

	/**
	 * Sets the label that displays the value for asteroids destroyed.
	 * @param destroyedValueLabel the label to set
	 */
	public void setDestroyedValueLabel(JLabel destroyedValueLabel) {
		this.destroyedValueLabel = destroyedValueLabel;
	}

	/**
	 * Sets the label that displays the value for ship (lives) left
	 * @param shipsValueLabel the label to set
	 */
	public void setShipsValueLabel(JLabel shipsValueLabel) {
		this.shipsValueLabel = shipsValueLabel;
	}

	public void setLevelValueLabel(JLabel levelValueLabel){
		this.levelValueLabel = levelValueLabel;
	}
	
	

	public int getBoom(){
		return boom;
	}
	public int boomReset(){
		setBoom(0);
		return getBoom();
	}
	public long healthReset(){
		setBoom(0);
		return getBoom();
	}
	public long delayReset(){
		setBoom(0);
		return getBoom();
	}

	protected boolean Gravity(){
		MegaMan megaMan = gameLogic.getMegaMan();
		Floor[] floor = gameLogic.getFloor();

		for(int i=0; i<9; i++){
			if((megaMan.getY() + megaMan.getMegaManHeight() -17 < this.getHeight() - floor[i].getFloorHeight()/2) 
					&& Fall() == true){

				megaMan.translate(0 , 2);
				return true;

			}
		}
		return false;
	}
	//Bullet fire pose
	protected boolean Fire(){
		MegaMan megaMan = gameLogic.getMegaMan();
		List<Bullet> bullets = gameLogic.getBullets();
		for(int i=0; i<bullets.size(); i++){
			Bullet bullet = bullets.get(i);
			if((bullet.getX() > megaMan.getX() + megaMan.getMegaManWidth()) && 
					(bullet.getX() <= megaMan.getX() + megaMan.getMegaManWidth() + 60)){
				return true;
			}
		}
		return false;
	}

	//BigBullet fire pose
	protected boolean Fire2(){
		MegaMan megaMan = gameLogic.getMegaMan();
		List<BigBullet> bigBullets = gameLogic.getBigBullets();
		for(int i=0; i<bigBullets.size(); i++){
			BigBullet bigBullet = bigBullets.get(i);
			if((bigBullet.getX() > megaMan.getX() + megaMan.getMegaManWidth()) && 
					(bigBullet.getX() <= megaMan.getX() + megaMan.getMegaManWidth() + 60)){
				return true;
			}
		}
		return false;
	}

	//Platform Gravity
	public boolean Fall(){
		MegaMan megaMan = gameLogic.getMegaMan(); 
		Platform[] platform = gameLogic.getNumPlatforms();
		for(int i=0; i<8; i++){
			if((((platform[i].getX() < megaMan.getX()) && (megaMan.getX()< platform[i].getX() + platform[i].getPlatformWidth()))
					|| ((platform[i].getX() < megaMan.getX() + megaMan.getMegaManWidth()) 
							&& (megaMan.getX() + megaMan.getMegaManWidth()< platform[i].getX() + platform[i].getPlatformWidth())))
					&& megaMan.getY() + megaMan.getMegaManHeight() == platform[i].getY()
					){
				return false;
			}
		}
		return true;
	}

	public void restructure(){
		Platform[] platform = gameLogic.getNumPlatforms();
		for(int i=0; i<8; i++){
			if(i<4)	platform[i].setLocation(50+ i*50, getHeight()/2 + 140 - i*40);
			if(i==4) platform[i].setLocation(50 +i*50, getHeight()/2 + 140 - 3*40);
			if(i>4){	
				int n=4;
				platform[i].setLocation(50 + i*50, getHeight()/2 + 20 + (i-n)*40 );
				n=n+2;
			}
		}
		status.setLevel(status.getLevel() + 1);
	}
	
	public void restructure2(){
		Platform[] platform = gameLogic.getNumPlatforms();
		platform[0].setLocation(50+ 1*50, getHeight()/2 + 140 - 0*40);
		platform[1].setLocation(100+ 2*50 -5 ,getHeight()/2 + 140 - 1*40);
		platform[2].setLocation(50+ 1*50 ,getHeight()/2 + 140 - 2*40);
		platform[3].setLocation(100+  2*50 -5 ,getHeight()/2 + 140 - 3*40);
		platform[4].setLocation(100+  4*50  ,getHeight()/2 + 140 - 3*40);
		platform[5].setLocation(100+  6*50 -5 ,getHeight()/2 + 140 - 2*40);
		platform[6].setLocation(100+ 4*50  ,getHeight()/2 + 140 - 1*40);
		platform[7].setLocation(100+ 6*50 - 5  ,getHeight()/2 + 140 - 0*40);
		
		status.setLevel(status.getLevel() + 1);
	}

	public void removeAsteroid(Asteroid asteroid){
		// "remove" asteroid
		asteroidExplosion = new Rectangle(
				asteroid.x,
				asteroid.y,
				asteroid.width,
				asteroid.height);
		asteroid.setLocation(-asteroid.width, -asteroid.height);
		status.setNewAsteroid(true);
		lastAsteroidTime = System.currentTimeMillis();

		// play asteroid explosion sound
		soundMan.playAsteroidExplosionSound();
	}

	public void setBoom(int boom) {
		this.boom = boom;
	}
}
