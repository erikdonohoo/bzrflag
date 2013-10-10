package com.murphysean.bzrflag.models;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement
@JsonIgnoreProperties(ignoreUnknown = true)
public class Obstacle{
	protected List<Point> points;
	protected Point centerPoint;
	protected float radius;

	public Obstacle(){
		points = new ArrayList<>();
	}

	public Obstacle(String serverString){
		points = new ArrayList<>();
		String[] parts = serverString.split("\\s+");
		Point point = null;
		for(int i = 1; i < parts.length; i++){
			if(i % 2 != 0){
				point = new Point();
				point.setX(Float.valueOf(parts[i]));
			}else{
				point.setY(Float.valueOf(parts[i]));
				points.add(point);
			}
		}

		centerPoint = calculateCenterPoint();
		radius = calculateMaxRadius();
	}

	public List<Point> getPoints(){
		return points;
	}

	public void setPoints(List<Point> points){
		this.points = points;
	}

	public Point getCenterPoint(){
		return centerPoint;
	}

	public float getRadius(){
		return radius;
	}

	private Point calculateCenterPoint(){
		float runningX = 0.0f;
		float runningY = 0.0f;
		for(Point corner : points){
			runningX += corner.getX();
			runningY += corner.getY();
		}

		Point center = new Point();
		center.setX(runningX / points.size());
		center.setY(runningY / points.size());
		return center;
	}

	private Float calculateMaxRadius(){
		float maxRadius = 0.0f;
		for(Point corner : points){
			//Calculate the distance from the center to each corner
			float distance = (float)Math.sqrt(Math.pow(corner.getX() - centerPoint.getX(),2) + Math.pow(corner.getY() - centerPoint.getY(),2));
			if(distance > maxRadius)
				maxRadius = distance;
		}

		return maxRadius;
	}

	//TODO Something like this
	public boolean contains(Point p){
		return false;
	}
}
