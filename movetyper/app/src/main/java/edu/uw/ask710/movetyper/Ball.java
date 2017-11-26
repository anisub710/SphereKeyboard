package edu.uw.ask710.movetyper;
/**
 * A simple struct to hold a shape
 */
public class Ball {

    public float cx; //center
    public float cy;
    public float radius; //radius
    public float dx; //velocity
    public float dy;

    public Ball(float cx, float cy, float radius) {
        this.cx = cx;
        this.cy = cy;
        this.radius = radius;
        this.dx = 0;
        this.dy = 0;
    }

    public float getRadius() {
        return this.radius;
    }

    public void setRadius(float radius){
        this.radius = radius;
    }

    public void setX(float cx){
        this.cx = cx;
    }

    public float getX(){
        return this.cx;
    }

    public void setY(float cy){
        this.cy = cy;
    }

    public float getY() {
        return this.cy;
    }
}
