import java.awt.*;

public class Star extends Body {
	private double x;
	private double y;
	private double radius;
	private String type;
	
	private static final int ICON_RADIUS = 12;
	
	public Star (double wWidth, double wHeight, double radius2) {
		x = wWidth / 2;
		y = wHeight / 2;
		radius = radius2;
		type = "star";
	}
	
	public void draw (Graphics window, Space s) {
		window.setColor(new Color(239, 142, 56));
		
		double actualViewWidth = ((double) s.ACTUAL_WIDTH/s.zoom);
		double actualViewHeight = ((double) s.ACTUAL_HEIGHT/s.zoom);
		double realX = (((double)s.VIEW_WIDTH)/2) + (((x - s.viewX)/(actualViewWidth/2)) * (((double)s.VIEW_WIDTH)/2));
		double realY = (((double)s.VIEW_HEIGHT)/2) + (((y - s.viewY)/(actualViewHeight/2)) * (((double)s.VIEW_HEIGHT)/2));
		double realRadius = ((double)s.VIEW_WIDTH*(radius/(double)s.ACTUAL_WIDTH))*s.zoom;
		
		if (realRadius > 1) { //actually draw it
			window.fillOval((int) realX - (int) realRadius, (int) realY - (int) realRadius, (int) realRadius * 2, (int) realRadius * 2);
		} else { //draw icon
			window.drawOval((int) realX - ICON_RADIUS, (int) realY - ICON_RADIUS, (int) ICON_RADIUS * 2, (int) ICON_RADIUS * 2);
		}
	}
	
	public double getX () {
		return x;
	}
	
	public double getY () {
		return y;
	}
	
	public double getRadius() {
		return radius;
	}
	
	public String getType() {
		return type;
	}
	
}