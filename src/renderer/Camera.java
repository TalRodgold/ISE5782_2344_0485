package renderer;

import primitives.Color;
import primitives.Point;
import primitives.Ray;
import primitives.Vector;

import java.util.LinkedList;
import java.util.List;
import java.util.MissingResourceException;

import static primitives.Util.*;

public class Camera {
    private Point p0; // Camera's field's
    private Vector vTo; // camera to
    private Vector vUp; // camera up
    private Vector vRight; // camera right
    private double width; // width
    private double height; // height
    private double distance; //distance
    private ImageWriter imageWriter;
    private RayTracerBasic rayTracerBasic;

    public Camera(Point p01, Vector vTo1, Vector vUp1) { // C-tor for camera
        if (vTo1.dotProduct(vUp1) != 0) { // if they are not orthogonal
        throw new IllegalArgumentException("Two vectors are not orthogonal");
        }
        this.p0 = p01;
        this.vTo = vTo1.normalize(); // normalizing the vectors
        this.vUp = vUp1.normalize();
        this.vRight = vTo1.crossProduct(vUp1).normalize();
    }

    public Point getP0() {
        return p0;
    }  // getters

    public Vector getvTo() {
        return vTo;
    }

    public Vector getvUp() {
        return vUp;
    }

    public Vector getvRight() {
        return vRight;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public double getDistance() {
        return distance;
    }
    public Camera setVPSize(double width1, double height1){  // setters
        this.width = width1;
        this.height = height1;
        return this;
    }
    public Camera setVPDistance(double distance1){ // calculate the distance
        this.distance = distance1;
        return this;
    }

    public void setP0(Point p0) {
        this.p0 = p0;
    }

    public void setvTo(Vector vTo) {
        this.vTo = vTo;
    }

    public void setvUp(Vector vUp) {
        this.vUp = vUp;
    }

    public void setvRight(Vector vRight) {
        this.vRight = vRight;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public Camera setImageWriter(ImageWriter imageWriter) {   // func returning this camera so we can concatenate the object
        this.imageWriter = imageWriter;
        return this;
    }

    public Camera setRayTracer(RayTracerBasic rayTracerBasic) {
        this.rayTracerBasic = rayTracerBasic;
        return this;
    }

    public Camera renderImage(int antialiasing){
        if (p0 == null)
            throw new MissingResourceException("p0 is null","Camera","p0");
        if (vTo == null)
            throw new MissingResourceException("vTo is null","Camera","vTo");
        if (vUp == null)
            throw new MissingResourceException("vUp is null","Camera","vUp");
        if (vRight == null)
            throw new MissingResourceException("vRight is null","Camera","vRight");
        if (imageWriter == null)
            throw new MissingResourceException("imageWriter is null","Camera","imageWriter");
        if (rayTracerBasic == null)
            throw new MissingResourceException("rayTracerBase is null","Camera","rayTracerBase");
        int Nx = imageWriter.getNx();
        int Ny = imageWriter.getNy();
        for (int i = 0; i < Ny; ++i){
            for (int j = 0; j < Nx; ++j){
                castRay(Nx, Ny, j, i, antialiasing);
            }
        }
        return this;
    }

    public void printGrid(int interval, primitives.Color color){
        if (imageWriter == null)
            throw new MissingResourceException("imageWriter is null","Camera","imageWriter");
        // define resolution
        int Nx = imageWriter.getNx(), Ny = imageWriter.getNy();
        for(int i = 0; i < Nx; i++){ // create grid
            for(int j = 0; j < Ny; j++){
                if(i % interval == 0 || j % interval == 0){
                    imageWriter.writePixel(j, i, color);
                }
            }
        }
    }

    public void writeToImage(){
        if (imageWriter == null) // if im
            throw new MissingResourceException("imageWriter is null","Camera","imageWriter");
        imageWriter.writeToImage();
    }

    private Color castRay(int nX, int nY, int col, int row, int antialiasing) {
        List<Ray> ray = constructRay(nX, nY, col, row, antialiasing);// castRay func will create a ray and will figure the color using traceRay func
        Color avgColor = rayTracerBasic.traceRay(ray.get(0));
        for (int p = 1; p < ray.size(); p++)
            avgColor = avgColor.add(rayTracerBasic.traceRay(ray.get(p)));
        avgColor = avgColor.scale(1.0/ray.size());
        imageWriter.writePixel(col, row, avgColor); // write the colored pixel
        return avgColor;
    }
    public List<Ray> constructRay(int nX, int nY, int j, int i, int antialiasing) {

        double rY = alignZero(height / nY); //  ratio of height of pixel
        double rX = alignZero(width / nX); // ratio of width of pixel
        double xJ = alignZero((j - ((nX - 1d) / 2d)) * rX); // according to slideshow 4
        double yI = alignZero((i - ((nY - 1d) / 2d)) * rY);

        // for 1 ray
        Point pIJ = p0.add(vTo.scale(distance));
        if (!isZero(xJ)) {                 // if not 0 then scale and add
            pIJ = pIJ.add(vRight.scale(xJ));
        }
        if (!isZero(yI)) {
            pIJ = pIJ.add(vUp.scale(-yI));
        }
        Vector vIJ = pIJ.subtract(p0); // direction of ray to pixel
        List <Ray> rayList = new LinkedList<>();
        rayList.add(new Ray(p0, new Vector(vIJ.getX(), vIJ.getY(), vIJ.getZ()))); // create ray for specific pixel

        // for multiple rays
        double divNx = rX / 2; // divide pixel width by 2
        double divNy = rY / 2; // divide pixel height by 2
        Point center = pIJ; // save pixel center
        for (int k = 1; k < antialiasing; k++) { // create antialiasing times rays
            pIJ = pIJ.add(vRight.scale(random(-divNx, divNx)));
            pIJ = pIJ.add(vUp.scale(random(-divNy, divNy)));
            vIJ = pIJ.subtract(p0);
            rayList.add(new Ray(p0, new Vector(vIJ.getX(), vIJ.getY(), vIJ.getZ()))); // create ray for each new ray in same pixel
            pIJ = center; // reset center
        }
       /* if ( antialiasing > 1){
            double jumpX = rX / antialiasing;
            double jumpY = rY / antialiasing;
            for (double k = -divNx; k <= divNx; k += jumpX) { // create antialiasing times rays
                for (double l = -divNy; l <= divNy; l += jumpY) {
                    pIJ = pIJ.add(vRight.scale(jumpX * k));
                    pIJ = pIJ.add(vUp.scale(jumpY * l));
                    vIJ = pIJ.subtract(p0);
                    rayList.add(new Ray(p0, new Vector(vIJ.getX(), vIJ.getY(), vIJ.getZ()))); // create ray for each new ray in same pixel
                    pIJ = center; // reset center
                }

            }

        }
       */



    return rayList;
    }
}
