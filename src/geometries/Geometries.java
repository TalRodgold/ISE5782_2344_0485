package geometries;

import primitives.Point;
import primitives.Ray;
import scene.Scene;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class Geometries extends Intersectable{
    protected List<Intersectable> inter;

    public Geometries() {
        this.inter = new LinkedList<Intersectable>();
    }

    public Geometries(Intersectable... geometries){
        this.inter = List.of(geometries);
    }

    public void add(Intersectable... geometries){
    this.inter.addAll(Arrays.asList(geometries));
    }

    @Override
    public List<Point> findIntsersections(Ray ray) {
        List<Point> result = null; // create a list of points for the result intersections
        for(var item : inter){ // for each obj in inter run find intersections
            List<Point>  itemPoints = item.findIntsersections(ray);
            if(itemPoints != null){ // if there are intersection points in item (obj)
                if(result == null){ // if this is the first obj then create a new list
                    result = new LinkedList<>();
                }
                result.addAll(itemPoints); // add item points to result
            }
        }
        return result;
    }

    @Override
    protected List<GeoPoint> findGeoIntersectionsHelper(Ray ray) {
        List<GeoPoint> intersections = null;
        for(var item : inter){
            List<GeoPoint>  itemPoints = item.findGeoIntersections(ray);
            if(itemPoints != null){
                if(intersections == null){
                    intersections = new LinkedList();
                }
                intersections.addAll(itemPoints);
            }
        }
        return intersections;
    }
}

