package me.athlaeos.lapi.utils;

import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.util.BoundingBox;
import org.bukkit.util.Transformation;
import org.bukkit.util.Vector;
import org.joml.AxisAngle4f;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class MathUtils {
    private static final Random random = new Random();

    public static Random getRandom(){
        return random;
    }

    public static double roundToMultiple(double number, double multiple){
        return multiple * Math.round(number / multiple);
    }
    
    private static final Map<Double, Double> sqrtCache = new HashMap<>();
    public static double sqrt(double value){
        if (sqrtCache.containsKey(value)) return sqrtCache.get(value);
        sqrtCache.put(value, Math.sqrt(value));
        return sqrtCache.get(value);
    }

    private static final Map<Double, Double> sinCache = new HashMap<>();
    public static double sin(double value){
        if (sinCache.containsKey(value)) return sinCache.get(value);
        sinCache.put(value, Math.sin(value));
        return sinCache.get(value);
    }

    private static final Map<Double, Double> cosCache = new HashMap<>();
    public static double cos(double value){
        if (cosCache.containsKey(value)) return cosCache.get(value);
        cosCache.put(value, Math.cos(value));
        return cosCache.get(value);
    }

    private static final Map<Double, Double> tanCache = new HashMap<>();
    public static double tan(double value){
        if (tanCache.containsKey(value)) return tanCache.get(value);
        tanCache.put(value, Math.tan(value));
        return tanCache.get(value);
    }

    public static int randomAverage(double chance){
        boolean negative = chance < 0;
        int atLeast = (negative) ? (int) Math.ceil(chance) : (int) Math.floor(chance);
        double remainingChance = chance - atLeast;
        if (getRandom().nextDouble() <= Math.abs(remainingChance)) atLeast += negative ? -1 : 1;
        return atLeast;
    }

    private static final Map<String, Double> evalCache = new HashMap<>();
    private static final MathEval math = new MathEval();
    public static double eval(String expression) {
        expression = expression.replaceAll(",", ".");
        if (evalCache.containsKey(expression)) return evalCache.get(expression);
        double result = math.evaluate(expression);
        evalCache.put(expression, result);
        return result;
    }

    public static Vector bounceVector(Vector vector, BlockFace face){
        return bounceVector(vector, face.getDirection());
    }

    public static Vector bounceVector(Vector vector, Vector normal){
        Vector temp = normal.multiply(-2 * vector.dot(normal));
        return vector.add(temp);
    }

    public static Vector normalFromBoundingBox(Entity entity, Location relative){
        BoundingBox b = entity.getBoundingBox();
        Location l = entity.getLocation();
        Location center = new Location(l.getWorld(), l.getX() + b.getCenterX(), l.getY() + b.getCenterY(), l.getZ() + b.getCenterZ());
        return relative.toVector().subtract(center.toVector());
    }

    public static Vector rotate(Vector vector, double yaw, double pitch, double roll){
        double cosA = cos(yaw);
        double cosB = cos(pitch);
        double cosC = cos(roll);
        double sinA = sin(yaw);
        double sinB = sin(pitch);
        double sinC = sin(roll);

        double Axx = cosA * cosB;
        double Axy = cosA * sinB * sinC - sinA * cosC;
        double Axz = cosA * sinB * cosC + sinA * sinC;

        double Ayx = sinA * cosB;
        double Ayy = sinA * sinB * sinC + cosA * cosC;
        double Ayz = sinA * sinB * cosC - cosA * sinC;

        double Azx = -sinB;
        double Azy = cosB * sinC;
        double Azz = cosB * cosC;

        double px = vector.getX();
        double py = vector.getY();
        double pz = vector.getZ();
        double x = Axx * px + Axy * py + Axz * pz;
        double y = Ayx * px + Ayy * py + Ayz * pz;
        double z = Azx * px + Azy * py + Azz * pz;

        vector.setX(x);
        vector.setY(y);
        vector.setZ(z);
        return vector;
    }

    public static Transformation rotateAroundX(Transformation transformation, float degrees){
        return rotateAroundAxis(transformation, degrees, new Vector3f(1, 0, 0));
    }

    public static Transformation rotateAroundY(Transformation transformation, float degrees){
        return rotateAroundAxis(transformation, degrees, new Vector3f(0, 1, 0));
    }

    public static Transformation rotateAroundZ(Transformation transformation, float degrees){
        return rotateAroundAxis(transformation, degrees, new Vector3f(0, 0, 1));
    }

    private static Transformation rotateAroundAxis(Transformation transformation, float degrees, Vector3f axis){
        Vector3f translation = new Vector3f(0, 0, 0); // transformation stays put
        AxisAngle4f axisAngleRotMat = new AxisAngle4f((float) Math.toRadians(degrees), axis);
        return new Transformation(
                translation,
                axisAngleRotMat,
                new Vector3f(1, 1, 1),
                new AxisAngle4f(0, translation)
        );
    }
}
