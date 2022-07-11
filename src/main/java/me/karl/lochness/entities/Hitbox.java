package me.karl.lochness.entities;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.util.Vector;

public class Hitbox {

    private Location loc;
    private double width, height, depth;

    public Hitbox(Location loc, double width, double height, double depth) {
        this.loc = loc;
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    public void display() {
        loc.getWorld().spawnParticle(Particle.COMPOSTER, loc.clone().add(new Vector(0, 0, 0)), 1);
        loc.getWorld().spawnParticle(Particle.COMPOSTER, loc.clone().add(new Vector(width, 0, 0)), 1);
        loc.getWorld().spawnParticle(Particle.COMPOSTER, loc.clone().add(new Vector(0, 0, depth)), 1);
        loc.getWorld().spawnParticle(Particle.COMPOSTER, loc.clone().add(new Vector(width, 0, depth)), 1);
        loc.getWorld().spawnParticle(Particle.COMPOSTER, loc.clone().add(new Vector(0, height, 0)), 1);
        loc.getWorld().spawnParticle(Particle.COMPOSTER, loc.clone().add(new Vector(width, height, 0)), 1);
        loc.getWorld().spawnParticle(Particle.COMPOSTER, loc.clone().add(new Vector(0, height, depth)), 1);
        loc.getWorld().spawnParticle(Particle.COMPOSTER, loc.clone().add(new Vector(width, height, depth)), 1);
    }

    /**
     * @param line
     * @return if Line, described by Location-object is intersecting with hitbox
     */
    public boolean isLineIntersecting(Location line) {
        return getIntersectionPoint(line) != null;
    }

    /**
     * @param line
     * @return exact point, where Line, described by Location-object is intersecting
     * with Hitbox
     */
    public Location getIntersectionPoint(Location line) {
        Location intersection = null;

        // left face has to be calculated
        if(line.getX() < loc.getX() && line.getDirection().getX() > 0) {
            intersection = line.add(line.getDirection().multiply(((double) loc.getX() - (double) line.getX()) / (double) line.getDirection().getX()));
            if(isPointInRect(intersection.getY(), intersection.getZ(), loc.getY(), loc.getZ(), height, depth)) {
                intersection.getWorld().spawnParticle(Particle.COMPOSTER, intersection, 1);
                return intersection;
            }
        }

        // right face has to be calculated
        if(line.getX() > (loc.getX() + width) && line.getDirection().getX() < 0) {
            intersection = line.add(line.getDirection().multiply((((double) loc.getX() + (double) width) - (double) line.getX()) / (double) line.getDirection().getX()));
            if(isPointInRect(intersection.getY(), intersection.getZ(), loc.getY(), loc.getZ(), height, depth)) {
                intersection.getWorld().spawnParticle(Particle.COMPOSTER, intersection, 1);
                return intersection;
            }
        }

        // lower face has to be calculated
        if(line.getY() < loc.getY() && line.getDirection().getY() > 0) {
            intersection = line.add(line.getDirection().multiply(((double) loc.getY() - (double) line.getY()) / (double) line.getDirection().getY()));
            if(isPointInRect(intersection.getX(), intersection.getZ(), loc.getX(), loc.getZ(), width, depth)) {
                intersection.getWorld().spawnParticle(Particle.COMPOSTER, intersection, 1);
                return intersection;
            }
        }

        // upper face has to be calculated
        if(line.getY() > (loc.getY() + height) && line.getDirection().getY() < 0) {
            intersection = line.add(line.getDirection().multiply((((double) loc.getY() + (double) height) - (double) line.getY()) / (double) line.getDirection().getY()));
            if(isPointInRect(intersection.getX(), intersection.getZ(), loc.getX(), loc.getZ(), width, depth)) {
                intersection.getWorld().spawnParticle(Particle.COMPOSTER, intersection, 1);
                return intersection;
            }
        }

        // forward face has to be calculated
        if(line.getZ() < loc.getZ() && line.getDirection().getZ() > 0) {
            intersection = line.add(line.getDirection().multiply(((double) loc.getZ() - (double) line.getZ()) / (double) line.getDirection().getZ()));
            if(isPointInRect(intersection.getX(), intersection.getY(), loc.getX(), loc.getY(), width, height)) {
                intersection.getWorld().spawnParticle(Particle.COMPOSTER, intersection, 1);
                return intersection;
            }
        }

        // backward face has to be calculated
        if(line.getZ() > (loc.getZ() + depth) && line.getDirection().getZ() < 0) {
            intersection = line.add(line.getDirection().multiply((((double) loc.getZ() + depth) - (double) line.getZ()) / (double) line.getDirection().getZ()));
            if(isPointInRect(intersection.getX(), intersection.getY(), loc.getX(), loc.getY(), width, height)) {
                intersection.getWorld().spawnParticle(Particle.COMPOSTER, intersection, 1);
                return intersection;
            }
        }

        return null;
    }

    public double getIntersectionDistance(Location line) {
        Location intersectionPoint = getIntersectionPoint(line.clone());
        if(intersectionPoint == null)
            return -1;
        return line.toVector().distance(intersectionPoint.toVector());
    }

    /**
     *
     * @param p1 points x-pos
     * @param p2 points y-pos
     * @param r1 rectangles x-pos
     * @param r2 rectangles y-pos
     * @param r3 rectangles width
     * @param r4 rectangles height
     * @return weather point is in rectangle
     */
    public boolean isPointInRect(double p1, double p2, double r1, double r2, double r3, double r4) {
        if(p1 > r1 && p1 < (r1 + r3))
            if(p2 > r2 && p2 < (r2 + r4))
                return true;
        return false;
    }

    /**
     * set Location
     */
    public void setLocation(Location loc) {
        this.loc = loc;
    }

}
