import java.awt.*;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;
import javax.swing.JFrame;
import java.util.ArrayList;
import javax.swing.JLabel;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Ship {
	private double x;
	private double y;
	private double speed;
	public boolean[] keys;
	private double length;
	private Image image;
	private Image prevImage;
	private ArrayList<Planet> planets;
	private ArrayList<Moon> moons;
	
	private JFrame frame;
	private JFrame mainFrame;
	
	private boolean line;
	private boolean sel;
	private Planet selPlanet;
	private Moon selMoon;
	
	private JLabel n, name, r, radius, o, orbitDistance, s, orbitSpeed, pp, planet;
	
	public Ship(ArrayList<Planet> planets2, ArrayList<Moon> moons2, JFrame mainFrame2) {
		x = 50;
		y = 50;
		speed = 5;
		keys = new boolean[4];
		length = 40;
		try {
			image = ImageIO.read(new File("ships\\shipD.png"));
			prevImage = image;
		} catch(Exception e) {
		}
		planets = planets2;
		moons = moons2;
		line = false;
		mainFrame = mainFrame2;
	}
	
	public void draw(Graphics window, Space s) throws IOException {
		prevImage = image;
		image = whichImage();
		window.drawImage(image, (int)x - (int)length/2, (int)y - (int)length/2, (int)length, (int)length, null);
		if (line) {
			window.drawLine(selX(), selY(), frame.getX() - mainFrame.getX(), frame.getY() - mainFrame.getY());
		}
	}
	
	public int selX() {
		if (sel) {
			return (int) selPlanet.getX();
		} else {
			return (int) selMoon.getX();
		}
	}
	
	public int selY() {
		if (sel) {
			return (int) selPlanet.getY();
		} else {
			return (int) selMoon.getX();
		}
	}
	
	public void move() {
		if (keys[0] && y > 0 + length/2)
			y -= speed;
		if (keys[1] && x < PSP.WIDTH - length)
			x += speed;
		if (keys[2] && y < PSP.HEIGHT - length)
			y += speed;
		if (keys[3] && x > 0 + length/2) 
			x-= speed;
	}
	
	public void info() {
		if (moons.size() > 0 || planets.size() > 0) {
			double g1 = 0;
			boolean welche = false;
			int p = -1;
			int m = -1;
			for (int i = 0; i < planets.size(); i++) {
				double distance = Math.sqrt(Math.pow(planets.get(i).getX() - x, 2) + Math.pow(planets.get(i).getY() - y, 2));
				if (distance > g1) {
					g1 = distance;
					welche = true;
					p = i;
				}
			}
			for (int i = 0; i < moons.size(); i++) {
				double distance = Math.sqrt(Math.pow(moons.get(i).getX() - x, 2) + Math.pow(moons.get(i).getY() - y, 2));
				if (distance > g1) {
					g1 = distance;
					welche = false;
					m = i;
				}
			}
			
			if (welche)
				frame = new JFrame("Planet Info");
			else
				frame = new JFrame("Moon Info");
			frame.addWindowListener(new WindowAdapter(){
                public void windowClosing(WindowEvent e){
                    line = false;
                }
            });
			frame.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			c.fill = GridBagConstraints.HORIZONTAL;
			c.insets = new Insets(10,30,10,30);
			
			n = new JLabel();
			name = new JLabel();
			r = new JLabel();
			radius = new JLabel();
			o = new JLabel();
			orbitDistance = new JLabel();
			s = new JLabel();
			orbitSpeed = new JLabel();
			pp = new JLabel();
			planet = new JLabel();
			
			if (welche) {
				c.gridx = 0;
				c.gridy = 0;
				n.setText("Planet Name: ");
				frame.add(n, c);
				
				c.gridx = 1;
				c.gridy = 0;
				name.setText(planets.get(p).getName());
				frame.add(name, c);
				
				c.gridx = 0;
				c.gridy = 1;
				r.setText("Radius: ");
				frame.add(r, c);
				
				c.gridx = 1;
				c.gridy = 1;
				radius.setText("" + planets.get(p).getRadius());
				frame.add(radius, c);
				
				c.gridx = 0;
				c.gridy = 2;
				o.setText("Orbit Distance: ");
				frame.add(o, c);
				
				c.gridx = 1;
				c.gridy = 2;
				orbitDistance.setText("" + planets.get(p).getOrbitDistance());
				frame.add(orbitDistance, c);
				
				c.gridx = 0;
				c.gridy = 3;
				s.setText("Orbit Speed: ");
				frame.add(s, c);
				
				c.gridx = 1;
				c.gridy = 3;
				orbitSpeed.setText("" + (planets.get(p).getOrbitSpeed() * 100));
				frame.add(orbitSpeed, c);
				
				sel = true;
				selPlanet = planets.get(p);
			} else {
				c.gridx = 0;
				c.gridy = 0;
				n.setText("Moon Name: ");
				frame.add(n, c);
				
				c.gridx = 1;
				c.gridy = 0;
				name.setText(moons.get(m).getName());
				frame.add(name, c);
				
				c.gridx = 0;
				c.gridy = 1;
				r.setText("Radius: ");
				frame.add(r, c);
				
				c.gridx = 1;
				c.gridy = 1;
				radius.setText("" + moons.get(m).getRadius());
				frame.add(radius, c);
				
				c.gridx = 0;
				c.gridy = 2;
				o.setText("Orbit Distance: ");
				frame.add(o, c);
				
				c.gridx = 1;
				c.gridy = 2;
				orbitDistance.setText("" + moons.get(m).getOrbitDistance());
				frame.add(orbitDistance, c);
				
				c.gridx = 0;
				c.gridy = 3;
				s.setText("Orbit Speed: ");
				frame.add(s, c);
				
				c.gridx = 1;
				c.gridy = 3;
				orbitSpeed.setText("" + (moons.get(m).getOrbitSpeed() * 100));
				frame.add(orbitSpeed, c);
				
				c.gridx = 0;
				c.gridy = 4;
				pp.setText("Planet: ");
				frame.add(pp, c);
				
				c.gridx = 1;
				c.gridy = 4;
				planet.setText(moons.get(m).getPlanetName());
				frame.add(planet, c);
				
				sel = false;
				selMoon = moons.get(m);
			}
			
			line = true;
			
			frame.pack();
			frame.setVisible(true);
		}
	}
	
	public Image whichImage() throws IOException {
		if (keys[0] && keys[1])
			return ImageIO.read(new File("ships\\shipUR.png"));
		else if (keys[0] && keys[3])
			return ImageIO.read(new File("ships\\shipUL.png"));
		else if (keys[2] && keys[1])
			return ImageIO.read(new File("ships\\shipDR.png"));
		else if (keys[2] && keys[3])
			return ImageIO.read(new File("ships\\shipDL.png"));
		else if (keys[0])
			return ImageIO.read(new File("ships\\shipU.png"));
		else if (keys[1])
			return ImageIO.read(new File("ships\\shipR.png"));
		else if (keys[2])
			return ImageIO.read(new File("ships\\shipD.png"));
		else if (keys[3])
			return ImageIO.read(new File("ships\\shipL.png"));
		return prevImage;
	}
}