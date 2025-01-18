import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SimpleRayTracing {

    private static final int WIDTH = 500;
    private static final int HEIGHT = 500;
    private BufferedImage canvas;
    private List<Sphere> spheres = new ArrayList<>();

    public static void main(String[] args) {
        new SimpleRayTracing().renderScene();
    }

    public SimpleRayTracing() {
        canvas = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        spheres.add(new Sphere(new Vec3(0, -1, 3), 1, Color.RED)); // Red sphere
        spheres.add(new Sphere(new Vec3(2, 0, 4), 1, Color.BLUE)); // Blue sphere
        spheres.add(new Sphere(new Vec3(-2, 0, 4), 1, Color.GREEN)); // Green sphere
    }

    public void renderScene() {
        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                Vec3 rayDirection = canvasToViewport(x, y);
                Color color = traceRay(new Vec3(0, 0, 0), rayDirection);
                canvas.setRGB(x, y, color.getRGB());
            }
        }
        saveImage("Image.png");
    }

    private Vec3 canvasToViewport(int x, int y) {
        double viewportWidth = 1;
        double viewportHeight = 1;
        double d = 1;
        return new Vec3(
                (x - WIDTH / 2.0) * viewportWidth / WIDTH,
                -(y - HEIGHT / 2.0) * viewportHeight / HEIGHT,
                d);
    }

    private Color traceRay(Vec3 origin, Vec3 direction) {
        Sphere closestSphere = null;
        double closestT = Double.MAX_VALUE;

        for (Sphere sphere : spheres) {
            double t = sphereIntersect(origin, direction, sphere);
            if (t < closestT && t > 0) {
                closestT = t;
                closestSphere = sphere;
            }
        }
        return closestSphere != null ? closestSphere.color : Color.WHITE;
    }

    private double sphereIntersect(Vec3 origin, Vec3 direction, Sphere sphere) {
        Vec3 CO = origin.subtract(sphere.center);
        double a = direction.dot(direction);
        double b = 2 * CO.dot(direction);
        double c = CO.dot(CO) - sphere.radius * sphere.radius;
        double discriminant = b * b - 4 * a * c;

        if (discriminant < 0)
            return Double.MAX_VALUE;

        double t1 = (-b + Math.sqrt(discriminant)) / (2 * a);
        double t2 = (-b - Math.sqrt(discriminant)) / (2 * a);
        return Math.min(t1, t2);
    }

    private void saveImage(String filename) {
        try {
            File output = new File(filename);
            ImageIO.write(canvas, "png", output);
            System.out.println("Image saved as " + filename);
        } catch (IOException e) {
            System.err.println("Failed to save image: " + e.getMessage());
        }
    }

    static class Vec3 {
        double x, y, z;

        public Vec3(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }

        public Vec3 subtract(Vec3 v) {
            return new Vec3(this.x - v.x, this.y - v.y, this.z - v.z);
        }

        public double dot(Vec3 v) {
            return this.x * v.x + this.y * v.y + this.z * v.z;
        }
    }

    // Sphere class to define a sphere's properties
    static class Sphere {
        Vec3 center;
        double radius;
        Color color;

        public Sphere(Vec3 center, double radius, Color color) {
            this.center = center;
            this.radius = radius;
            this.color = color;
        }
    }
}
