import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.KeyEvent;
import static java.lang.Character.*;
import java.util.*;
import javax.swing.*;
import java.io.IOException;
import java.io.PrintWriter;

public class Space extends Canvas implements MouseMotionListener, MouseWheelListener, MouseListener, KeyListener, Runnable {
	
	private Star star;
	private ArrayList<Planet> ps;
	private ArrayList<Moon> ss;
	private BufferedImage back;
	private double displaySpeed;
	private Ship ship;
	
	private long lastCurrentTime;
	private long simulationTime;
	
	private int mouseDragTempX = -1;
	private int mouseDragTempY = -1;
	
	public double zoom;
	public double viewX;
	public double viewY;
	
	public boolean locked = false;
	public Body lockedBody;
	
	boolean dragging = false;
	
	public static int VIEW_WIDTH;
	public static int VIEW_HEIGHT;
	
	public static int ACTUAL_WIDTH;
	public static int ACTUAL_HEIGHT;
	
	public static final int FRAMERATE = 30;
	
	public Space (int viewWidth, int viewHeight, int actualWidth, int actualHeight, ArrayList<Planet> ps2, Star s2, ArrayList<Moon> ss2, Ship ship2) {
		setBackground(Color.BLACK);
		
		lastCurrentTime = System.nanoTime();
		
		ps = ps2;
		star = s2;	
		ss = ss2;
		displaySpeed = 1;
		ship = ship2;
		VIEW_WIDTH = viewWidth;
		VIEW_HEIGHT = viewHeight;
		ACTUAL_WIDTH = actualWidth;
		ACTUAL_HEIGHT = actualHeight;
		zoom = 1;
		viewX = ACTUAL_WIDTH/2;
		viewY = ACTUAL_HEIGHT/2;
				
		this.addKeyListener(this);
		this.addMouseListener(this);
		this.addMouseWheelListener(this);
		this.addMouseMotionListener(this);
		new Thread(this).start();
	}
	
	public void update (Graphics window) {
		paint(window);
		long currentTime = System.nanoTime();
		long duration = (currentTime - lastCurrentTime);
		lastCurrentTime = currentTime;
		simulationTime += duration * displaySpeed;
		
		try {
			Thread.sleep(1000/FRAMERATE);
			simulationTime += (1000000 * (1000/FRAMERATE)) * displaySpeed;
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void paint (Graphics window) {
		
		Graphics2D tdg = (Graphics2D) window;
		
		if(back==null)
		   back = (BufferedImage)(createImage(getWidth(),getHeight()));
		   
		Graphics gtb = back.createGraphics();
		
		gtb.setColor(Color.BLACK);
		gtb.fillRect(0,0,VIEW_WIDTH * 2,VIEW_HEIGHT * 2);
		
		
		//MOVE
		for (Moon s: ss) {
			s.move(simulationTime);
		}
		
		for (Planet p: ps) {
			p.move(simulationTime);
		}
		
		
		//DRAW

		for (Moon s: ss) {
			s.draw(gtb, this);
		}
			
		if (locked) {
			viewX = lockedBody.getX();
			viewY = lockedBody.getY();
		}
			
		for (Planet p: ps) {
			p.draw(gtb, this);
		}
		
		
		star.draw(gtb, this);
		
		ship.move();
		try {
			ship.draw(gtb, this);
		} catch(Exception e) {}
		
		tdg.drawImage(back, null, 0, 0);
		
	}
	
	public void run() {
   		try {
	   		while(true) {
	   		   Thread.currentThread().sleep(5);
	           repaint();
	        }
      	}
      	catch(Exception e) {
      	}
  	}
  	
  	public void setDisplaySpeed(double ds) {
  		displaySpeed = ds;
  		for (int i = 0; i < ps.size(); i ++) {
  			ps.get(i).setDisplaySpeed(ds);
  		}
  		for (int i = 0; i < ss.size(); i++) {
  			ss.get(i).setDisplaySpeed(ds);
  		}
  	}
  	
  	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			ship.keys[0] = false;
		} else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			ship.keys[1] = false;
		} else if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			ship.keys[2] = false;
		} else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			ship.keys[3] = false;
		}
	}
	
	public void keyTyped(KeyEvent e) {
		
	}
	
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_UP) {
			ship.keys[0] = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
			ship.keys[1] = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_DOWN) {
			ship.keys[2] = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_LEFT) {
			ship.keys[3] = true;
		}
		if (e.getKeyCode() == KeyEvent.VK_SPACE) {
			ship.info();
		}
	}
	
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		 if (e.isControlDown()) {
	     	if (e.getWheelRotation() < 0) {
	        	//System.out.println("mouse wheel Up");
	     		zoom*=2;
	     		System.out.println(zoom + " " + ((double)VIEW_WIDTH*(star.getRadius()/(double)ACTUAL_WIDTH))*zoom);
	        } else {
	        	//System.out.println("mouse wheel Down");
	        	zoom/=2;
	        	if (zoom < 1) {
	        		zoom = 1;
	        	}
	        	System.out.println(zoom + " " + ((double)VIEW_WIDTH*(star.getRadius()/(double)ACTUAL_WIDTH))*zoom);
	        }
		 } else {
			 getParent().dispatchEvent(e);
	     }
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		Point point = e.getPoint();
		mouseDragTempX = point.x;
		mouseDragTempY = point.y;
		
		dragging = true;
		//System.out.println("dragging: " + dragging);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		Point point = e.getPoint();
		double movementX = (((double) ACTUAL_WIDTH/zoom)/VIEW_WIDTH) * (mouseDragTempX - point.x);
		double movementY = (((double) ACTUAL_HEIGHT/zoom)/VIEW_HEIGHT) * (mouseDragTempY - point.y);
		
		viewY += movementY;
		viewX += movementX;
		
		if (viewX < 0) {
			viewX = 0;
		}
		
		if (viewX > ACTUAL_WIDTH) {
			viewX = ACTUAL_WIDTH;
		}
		
		if (viewY < 0) {
			viewY = 0;
		}
		
		if (viewY > ACTUAL_HEIGHT) {
			viewY = ACTUAL_HEIGHT;
		}
		
		mouseDragTempX = -1;
		mouseDragTempY = -1;
		
		dragging = false;
	}
	
	public void mouseDragged(MouseEvent event) {
		if (dragging) {
			Point point = event.getPoint();

			double movementX = (((double) ACTUAL_WIDTH/zoom)/VIEW_WIDTH) * (mouseDragTempX - point.x);
			double movementY = (((double) ACTUAL_HEIGHT/zoom)/VIEW_HEIGHT) * (mouseDragTempY - point.y);
			
			viewY += movementY;
			viewX += movementX;
			
			//System.out.println(movementX + " " + movementY);
			
			mouseDragTempX = point.x;
			mouseDragTempY = point.y;
			
			repaint();
		}
	}
	
	public void save(PrintWriter save) {
		//simulation time
		//numplanets
		//planet1
		//planet2
		//nummoons
		//moon1
		//moon2
		save.println(simulationTime);
		save.println(ps.size());
		for(int i = 0; i < ps.size(); i++) {
			save.println(ps.get(i).save());
		}
		save.println(ss.size());
		for(int i = 0; i < ss.size(); i++) {
			save.println(ss.get(i).save());
		}
		
		save.close();
	}
	
	public void load(Scanner load) {
		simulationTime = Long.parseLong(load.next());
		load.nextLine();
		
		int numPlanets = Integer.parseInt(load.next());
		load.nextLine();
		
		for (int i = 0; i < numPlanets; i++) {
			String line = load.nextLine();
			Planet p = new Planet(line, star, displaySpeed);
			ps.add(p);
		}
		
		int numMoons = Integer.parseInt(load.next());
		load.nextLine();
		
		for (int i = 0; i < numMoons; i++) {
			String line = load.nextLine();
			String planetName = line.substring(line.lastIndexOf(" ") + 1);
			
			Planet p = ps.get(0);
			for (int j = 1; j < ps.size(); j++) {
				if (ps.get(j).getName().equals(planetName)) {
					p = ps.get(j);
				}
			}
			
			Moon m = new Moon(line.substring(0, line.lastIndexOf(" ")), p, displaySpeed);
			ss.add(m);
		}
		
		load.close();
		
		lastCurrentTime = System.nanoTime();
	}
	

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}


}