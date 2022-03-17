package Block_Game;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.*;
import javax.swing.*;

public class Game {
	
	
	
	static class MyFrame extends JFrame{
		
		//constant
		static int BALL_WIDTH =20;
		static int BALL_HEIGHT =20;
		static int BLOCK_ROWS =5;
		static int BLOCK_COLUMNS =10;
		static int BLOCK_WIDTH =40;
		static int BLOCK_HEIGHT =10;
		static int BLOCK_GAP =3;
		static int BAR_WIDTH =80;
		static int BAR_HEIGHT =20;
		static int CANVAS_WIDTH =400 + (BLOCK_GAP + BLOCK_COLUMNS) - BLOCK_GAP;
		static int CANVAS_HEIGHT =600;
		
		//variable
		static MyPanel myPanel = null;
		static int score =0;
		static Timer timer = null;
		static Block[][] blocks = new Block[BLOCK_ROWS][BLOCK_COLUMNS]; 
		static Bar bar = new Bar();
		static Ball ball = new Ball();
		static int barXTarget = bar.x; //target value - interpaltion
		static int dir = 0; //공  방향 0: Up-rigth 1 : Down-rigth 2 : Up-left 3 : down=letf 
		static int ballSpeed = 5; //공 속도
		static boolean isGameFinish = false;
		
		static class Ball{
			int x = CANVAS_WIDTH/2 - BALL_WIDTH/2;
			int y = CANVAS_HEIGHT/2 - BALL_HEIGHT/2;
			int width = BALL_WIDTH;
			int heigth = BALL_HEIGHT;
			
			Point getCenter() {
				return new Point(x +(BALL_WIDTH/2),y+(BALL_HEIGHT/2));
			}
			Point getBottomCenter() {
				return new Point(x +(BALL_WIDTH/2),y);
			}
			Point getTopCenter() {
				return new Point(x,y +(BALL_HEIGHT/2));
			}
			Point getRightCenter() {
				return new Point(x +(BALL_WIDTH),y+(BALL_HEIGHT/2));
			}
		}
		static class Bar{
			int x  = CANVAS_WIDTH/2 - BAR_WIDTH/2;
			int y = CANVAS_HEIGHT -100;
			int width = BAR_WIDTH;
			int heigth = BAR_HEIGHT;
	}
		static class Block{
			int x;
			int y;
			int width = BLOCK_WIDTH;
			int heigth = BLOCK_HEIGHT;
			int color =0; //0;whithe 1:yellow 2:bloue 3:mazanta 4:red
			boolean isHidden = false; //after collision, block will be hidden.
		}
		
		
		
		static class MyPanel extends JPanel{ //VANAS for Draw!
			public MyPanel() {
				this.setSize(CANVAS_WIDTH,CANVAS_HEIGHT);
				this.setBackground(Color.WHITE);  ///////////BLACK
			}
			@Override
			public void paint(Graphics g) {
				super.paint(g);
				Graphics2D g2d = (Graphics2D)g;
				
				drawUI( g2d );
			}
			private void drawUI(Graphics2D g2d) {
				//draw Blocks
				for(int i=0; i<BLOCK_ROWS; i++) {
					for(int j=0; j<BLOCK_COLUMNS; j++) {
						if(blocks[i][j].isHidden) {
							continue;
						}
						if(blocks[i][j].color == 0){
							g2d.setColor(Color.GRAY); /////////
						}
						else if(blocks[i][j].color == 1){
							g2d.setColor(Color.YELLOW);//
						}
						else if(blocks[i][j].color == 2){
							g2d.setColor(Color.LIGHT_GRAY);
						}
						else if(blocks[i][j].color == 3){
							g2d.setColor(Color.CYAN);
						}
						else if(blocks[i][j].color == 4){
							g2d.setColor(Color.PINK);
						}
						g2d.fillRect(blocks[i][j].x, blocks[i][j].y, 
								blocks[i][j].width, blocks[i][j].heigth);
					}
				//draw score
					g2d.setColor(Color.DARK_GRAY); /////////////글자색
					g2d.setFont(new Font("TimesRaman", Font.BOLD,20));
					g2d.drawString("score :"+score, CANVAS_WIDTH/2 -30,20);
					if(isGameFinish) {
						g2d.setColor(Color.BLUE);
						g2d.drawString("score : "+ score + "Game Finish" , CANVAS_WIDTH/2 -55,50);
					}
					//draw Ball
					g2d.setColor(Color.DARK_GRAY);//공색 ////////////////////
					g2d.fillOval(ball.x,ball.y,BALL_WIDTH, BALL_HEIGHT);
					
					//draw bar
					g2d.setColor(Color.DARK_GRAY);
					g2d.fillRect(bar.x,bar.y,bar.width,bar.heigth);
					
				}
				
			}
		}
		
		public MyFrame(String title){
			super(title);
			this.setVisible(true);
			this.setSize(CANVAS_WIDTH, CANVAS_HEIGHT);
			this.setLocation(400,300);
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			initData();
		
			myPanel = new MyPanel();
			this.add("Center", myPanel);
			
			setKeyListener();
			startTimer();
			
		}
		public void initData() {
			for(int i=0; i<BLOCK_ROWS; i++) {
				for(int j=0; j<BLOCK_COLUMNS; j++) {
					blocks[i][j] = new Block();
					blocks[i][j].x = BLOCK_WIDTH*j + BLOCK_GAP*j;
					blocks[i][j].y = 100+ BLOCK_HEIGHT*i + BLOCK_GAP*i;
					blocks[i][j].heigth = BLOCK_HEIGHT;
					blocks[i][j].color =4-i;
					blocks[i][j].isHidden = false;
				}
			}
		}
		public void setKeyListener() {
			this.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if(e.getKeyCode()== KeyEvent.VK_LEFT) {
						System.out.println("Pressed Left Key");
						barXTarget -= 20;
						if(bar.x < barXTarget) { //repate key pressed...
							barXTarget = bar.x;
						}
					}
					else if(e.getKeyCode()== KeyEvent.VK_RIGHT) {
						System.out.println("Pressen Right Key");
						barXTarget += 20;
						if(bar.x > barXTarget) { //repate key pressed...
							barXTarget = bar.x;
						}
					}
				}
			});
		}
		public void startTimer() {
			timer = new Timer(20,new ActionListener(){
				@Override
				public void actionPerformed(ActionEvent e) { //Timer event
					movement();
					checkCollision();
					checkColisionBlock();
					myPanel.repaint();
					
					
					isGameFinish();
					
					
					
				}
			});
			timer.start();  //start timer!
		}
		public void isGameFinish() {
			//Game Success!
			int count =0;
			for(int i=0; i<BLOCK_ROWS; i++) {
				for(int j=0; j<BLOCK_COLUMNS; j++) {
					Block block = blocks[i][j];
					if(block.isHidden)
						count++;
				}
			}
			if(count == BLOCK_ROWS*BLOCK_COLUMNS) {
				//Game Finished!
				//timer.stop();
				isGameFinish = true;
			}
		}
		public void movement() {
			if(bar.x < barXTarget) {
				bar.x += 5;
			}else if(bar.x > barXTarget) {
				bar.x -= 5;
			}
			if(dir == 0) { //up-right 위로
				ball.x += ballSpeed;
				ball.y -= ballSpeed;
			}else if(dir ==1) { //down - right
				ball.x += ballSpeed;
				ball.y += ballSpeed;
			}else if(dir ==2) { //up- left
				ball.x -= ballSpeed;
				ball.y -= ballSpeed;
			}else if(dir ==3) { //down- lift
				ball.x -= ballSpeed;
				ball.y += ballSpeed;
			}
		}
		public boolean dupRect(Rectangle rect1, Rectangle rect2) {
			return rect1.intersects(rect2);
		}
		public void checkCollision() {   //벽
			if(dir == 0) { 
				//wall
				if(ball.y<0) {
					dir =1;
				}
				if(ball.x >CANVAS_WIDTH - BALL_WIDTH) {
					dir =2;
				}
				//bar
				//none
				
			}else if(dir ==1) { 
				//wall
					if(ball.y > CANVAS_HEIGHT - BALL_HEIGHT - BALL_HEIGHT) {
						dir =0;
						
						//reset
						dir =0;
						ball.x = CANVAS_WIDTH/2 - BALL_WIDTH/2;
						ball.y = CANVAS_HEIGHT/2 - BALL_HEIGHT/2;
						score =0;
					}
					if(ball.x >CANVAS_WIDTH - BALL_WIDTH) {
						dir =3;
					}
				//bar
					if(ball.getBottomCenter().y >= bar.y) {
						if(dupRect(new Rectangle(ball.x, ball.y , ball.width, ball.heigth),
									new Rectangle(bar.x, bar.y, bar.width, bar.heigth))) {
							dir = 0;
						}
					}
			}else if(dir ==2) { 
				//wall
				if(ball.y< 0) {
					dir =3;
				}
				if(ball.x <0) {
					dir =0;
				}
			}else if(dir ==3) { 
				//wall
				if(ball.y> CANVAS_HEIGHT - BALL_HEIGHT-BALL_HEIGHT) {
					dir =2;
					
					//reset
					dir =0;
					ball.x = CANVAS_WIDTH/2 - BALL_WIDTH/2;
					ball.y = CANVAS_HEIGHT/2 - BALL_HEIGHT/2;
					score =0;
				}
				if(ball.x <0) {
					dir =1;
				}
				if(ball.getBottomCenter().y >= bar.y) {
					if(dupRect(new Rectangle(ball.x, ball.y , ball.width, ball.heigth),
								new Rectangle(bar.x, bar.y, bar.width, bar.heigth))) {
						dir = 2;
					}
				}
			}
		}
		public void checkColisionBlock() {
			for(int i=0; i<BLOCK_ROWS; i++) {
				for(int j=0; j<BLOCK_COLUMNS; j++) {
					Block block = blocks[i][j];
					if(block.isHidden == false) {
						if(dir == 0) { //up right
							if(dupRect(new Rectangle(ball.x, ball.y , ball.width, ball.heigth),
									new Rectangle(block.x, block.y, block.width, block.heigth))) {
								if(ball.x > block.x +2 && 
										ball.getRightCenter().x <= block.x + block.width -2) {
									dir =1;
								}else {
									dir =2;
								}
								block.isHidden = true;
								if(block.color ==0) {
									score += 10;
								}else if(block.color == 1) {
									score += 20;
								}else if(block.color == 2) {
									score += 30;
								}else if(block.color == 3) {
									score += 40;
								}else if(block.color == 4) {
									score += 50;
								}
							}
						}
						else if(dir == 1) { //down right
							if(dupRect(new Rectangle(ball.x, ball.y , ball.width, ball.heigth),
									new Rectangle(bar.x, bar.y, bar.width, bar.heigth))) {
								if(ball.x > block.x +2 && 
										ball.getRightCenter().x <= block.x + block.width -2) {
									dir =0;
								}else {
									dir =3;
								}
								block.isHidden = true;
								if(block.color ==0) {
									score += 10;
								}else if(block.color == 1) {
									score += 20;
								}else if(block.color == 2) {
									score += 30;
								}else if(block.color == 3) {
									score += 40;
								}else if(block.color == 4) {
									score += 50;
								}
							}
						}
						else if(dir == 2) {  // up lift
							if(dupRect(new Rectangle(ball.x, ball.y , ball.width, ball.heigth),
									new Rectangle(block.x, block.y, block.width, block.heigth))) {
								if(ball.x > block.x +2 && 
										ball.getRightCenter().x <= block.x + block.width -2) {
									dir =3;
								}else {
									dir =0;
								}
								block.isHidden = true;
								if(block.color ==0) {
									score += 10;
								}else if(block.color == 1) {
									score += 20;
								}else if(block.color == 2) {
									score += 30;
								}else if(block.color == 3) {
									score += 40;
								}else if(block.color == 4) {
									score += 50;
								}
							}
						}
						else if(dir == 3) {  //down lift
							if(dupRect(new Rectangle(ball.x, ball.y , ball.width, ball.heigth),
									new Rectangle(block.x, block.y, block.width, block.heigth))) {
								if(ball.x > block.x +2 && 
										ball.getRightCenter().x <= block.x + block.width -2) {
									dir =2;
								}else {
									dir =1;
								}
								block.isHidden = true;
								if(block.color ==0) {
									score += 10;
								}else if(block.color == 1) {
									score += 20;
								}else if(block.color == 2) {
									score += 30;
								}else if(block.color == 3) {
									score += 40;
								}else if(block.color == 4) {
									score += 50;
								}
							}
						}
					}
				}
			}
		}
	}

	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		new MyFrame("Block Game");
	}

}
