package game.brickbraker;

import game.brickbraker.display.Display;
import game.brickbraker.input.KeyManager;
import game.brickbraker.map.Map;

import java.awt.*;
import java.awt.image.BufferStrategy;

public class Game implements Runnable {

    private String title;
    private int width, height;
    private Display display;
    private Thread thread;
    private boolean isRunning = false;
    private BufferStrategy bs;
    private Graphics g;
    // key manager
    private KeyManager keyManager;
    // map
    private Map map;

    public Game(String title, int width, int height){
        this.title = title;
        this.width = width;
        this.height = height;
        keyManager = new KeyManager();
        displayInit();
        map = new Map(this, "res/maps/map1.txt");
    }

    private void displayInit(){
        display = new Display(title, width, height);
        display.getFrame().addKeyListener(keyManager);
    }

    public void tick(){
        map.tick();
        keyManager.tick();
    }


    public void render(){
        bs = display.getCanvas().getBufferStrategy();
        if(bs == null){
            display.getCanvas().createBufferStrategy(3);
            return;
        }
        g = bs.getDrawGraphics();
        g.clearRect(0,0,width, height);
        // draw
        map.render(g);
        // end draw
        bs.show();
        g.dispose();
    }

    @Override
    public void run() {
        int fps = 60;
        double timePerTick = 1000000000 / (double)fps;
        double delta = 0;
        long now;
        long lastTime = System.nanoTime();
        long timer = 0;
        int ticks = 0;

        while(isRunning){
            now = System.nanoTime();
            delta += (now - lastTime) / timePerTick;
            timer += now - lastTime;
            lastTime = now;

            if(delta >= 1){
                tick();
                render();
                ticks++;
                delta--;
            }

            if(timer >= 1000000000){
                //System.out.println("FPS: " + ticks);
                ticks = 0;
                timer = 0;
            }
        }
        stop();
    }

    public synchronized void start(){
        if(isRunning) return;
        isRunning = true;
        thread = new Thread(this);
        thread.start();
    }

    public synchronized void stop(){
        if(!isRunning) return;
        isRunning = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Display getDisplay(){
        return display;
    }

    public KeyManager getKeyManager(){
        return keyManager;
    }

    public Map getMap(){
        return map;
    }

}
