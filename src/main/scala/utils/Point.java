package utils;

public class Point{
    private float x;	    //X 经度
    private float y;	    //Y 维度
    private double arCos;	//与P0点的角度
    private int deviceId;

    public Point() {
    }

    public Point(float x, float y, int deviceId) {
        this.x = x;
        this.y = y;
        this.deviceId = deviceId;
    }

    public float getX() {
        return x;
    }
    public void setX(float x) {
        this.x = x;
    }
    public float getY() {
        return y;
    }
    public void setY(float y) {
        this.y = y;
    }
    public double getArCos() {
        return arCos;
    }
    public void setArCos(double arCos) {
        this.arCos = arCos;
    }

    public int getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(int deviceId) {
        this.deviceId = deviceId;
    }
}