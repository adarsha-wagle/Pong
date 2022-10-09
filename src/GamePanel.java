import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

public class GamePanel extends JPanel implements Runnable {

    /*
    This class does:
    1.sets width and height of the game panel
    2.Instantiate two paddles:paddle1 and paddle 2
    3.Instantiate ball
    4.creates fps
    5.responsible for detecting key pressed and key released and sends event to the paddle class
     */
    static final int GAME_WIDTH =1000;
    static final int GAME_HEIGHT=(int)(GAME_WIDTH*(0.5555));
    static final Dimension SCREEN_SIZE = new Dimension(GAME_WIDTH,GAME_HEIGHT );

    static final int PADDLE_HEIGHT = 100;
    static final int PADDLE_WIDTH = 25;
    static final int BALL_DIAMETER = 20;
    Thread gameThread;
    Image image;
    Graphics graphics;
    Random random;
    Paddle paddle1;
    Paddle paddle2;
    Ball ball;
    Score score;
    GamePanel()
    {
    newPaddle();
    newBall();
    score = new Score(GAME_WIDTH,GAME_HEIGHT);
    this.setFocusable(true);
    this.addKeyListener(new MyActionListener());
    this.setPreferredSize(SCREEN_SIZE);
    gameThread = new Thread(this);
    gameThread.start();
    }
    public void newBall ()
    {
        random = new Random();
        ball = new Ball((GAME_WIDTH/2)-(BALL_DIAMETER/2),random.nextInt(GAME_HEIGHT-BALL_DIAMETER),BALL_DIAMETER,BALL_DIAMETER);
    }
    public void newPaddle()
    {
        paddle1 = new Paddle(0,(GAME_HEIGHT/2)-(PADDLE_HEIGHT/2),PADDLE_WIDTH,PADDLE_HEIGHT,1);
        paddle2 = new Paddle(GAME_WIDTH-PADDLE_WIDTH,(GAME_HEIGHT/2)-(PADDLE_HEIGHT/2),PADDLE_WIDTH,PADDLE_HEIGHT,2);
    }
    public void paint(Graphics g)
    {
        image = createImage(getWidth(),getHeight());
        graphics = image.getGraphics();
        draw(graphics);
        g.drawImage(image,0,0,this);
    }
    public void draw(Graphics g)
    {
        paddle1.draw(g);
        paddle2.draw(g);
        ball.draw(g);
        score.draw(g);
        Toolkit.getDefaultToolkit().sync();
    }
    public void move()
    {
        paddle1.move();
        paddle2.move();
        ball.move();
    }
    public void checkCollision()//stops paddles at window edges
    {
        //limiting paddle movement that goes off the screen
        if(paddle1.y<=0)
            paddle1.y = 0;
        if(paddle1.y>=(GAME_HEIGHT-PADDLE_HEIGHT))
            paddle1.y = GAME_HEIGHT-PADDLE_HEIGHT;
        if(paddle2.y<=0)
            paddle2.y=0;
        if(paddle2.y >= (GAME_HEIGHT-PADDLE_HEIGHT))
            paddle2.y = GAME_HEIGHT-PADDLE_HEIGHT;


        //bounce ball off top and bottom windows edges
        if(ball.y<=0)
        {
            ball.setYDirection(-ball.yVelocity);
        }
        if(ball.y >= GAME_HEIGHT-BALL_DIAMETER)
        {
        ball.setYDirection(-ball.yVelocity);
        }

        //bounce ball if it hits paddles
        if(ball.intersects(paddle1))//if ball collides with paddles then reverse the velocity of the ball
        {
            ball.xVelocity = Math.abs(ball.xVelocity);
            ball.xVelocity++;//increasing the velocity of the ball if ball collides with paddle
            if(ball.yVelocity>0)
                ball.yVelocity++;//optional for more difficulty
            else
                ball.yVelocity--;
            ball.setXDirection(ball.xVelocity);
            ball.setYDirection(ball.yVelocity);
        }if(ball.intersects(paddle2))//if ball collides with paddles then reverse the velocity of the ball
        {
            ball.xVelocity = Math.abs(ball.xVelocity);
            ball.xVelocity++;//increasing the velocity of the ball if ball collides with paddle
            if(ball.yVelocity>0)
                ball.yVelocity++;//optional for more difficulty
            else
                ball.yVelocity--;
            ball.setXDirection(-ball.xVelocity);
            ball.setYDirection(ball.yVelocity);
        }


        //give a player 1 point and creates new paddles and ball
        if(ball.x<=0)
        {
            score.player2++;
            newPaddle();
            newBall();
            System.out.println("Player2: "+score.player2);
        } if(ball.x >=GAME_WIDTH - BALL_DIAMETER)
        {
            score.player1++;
            newPaddle();
            newBall();
            System.out.println("Player1: "+score.player1);
        }

    }
    public void run()//game loop //creating frame
    {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000_000_000/amountOfTicks;
        double delta  = 0;
        while(true)
        {
            long now = System.nanoTime();
//            System.out.println(now);
            delta+=(now-lastTime)/ns;
//            System.out.println(delta);
            lastTime = now;
            if(delta>=1)
            {
                move();
                checkCollision();
                repaint();
                delta--;

            }
        }
    }
    public class MyActionListener implements KeyListener
    {

        @Override
        public void keyTyped(KeyEvent e) {

        }

        @Override
        public void keyPressed(KeyEvent e) {
            paddle1.keyPressed(e);
            paddle2.keyPressed(e);
        }

        @Override
        public void keyReleased(KeyEvent e) {
        paddle1.keyReleased(e);
        paddle2.keyReleased(e);
        }
    }
}
