package ir.s_tag.musicplayer.DataModel;

import android.graphics.Point;

public class Indicator extends MainDataModel {
    private float deg;
    private float radius;
    private Point offset = new Point(0,0);


    private float touchX ;
    private float touchY ;


    public float getTouchX1(){
        return (this.offset.x - this.radius ) - 24;
    }

    public float getTouchY1(){
        return (this.offset.y - this.radius ) - 24;
    }

    public float getTouchX2(){
        return (this.offset.x + this.radius ) + 24;
    }

    public float getTouchY2(){
        return (this.offset.y + this.radius ) + 24;
    }

    public float getDeg() {
        return deg;
    }

    public void setDeg(float deg) {
        this.deg = deg;
    }

    public float getRadius() {
        return radius;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public Point getOffset() {
        return offset;
    }

    public void setOffset(Point offset) {
        this.offset = offset;
    }
}
