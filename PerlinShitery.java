/**
 * Java implementation of:
 * https://rtouti.github.io/graphics/perlin-noise-algorithm
 *
 * No rights reserved, reserving rights is for neeeeerds!
 */

package sample;

import java.util.Random;

public class PerlinShitery {
    private static final int WORLD_WIDTH = 512;
    private static final int WORLD_HEIGHT = 512;

    static int p[] = new int[512];
    static int permutation[] = { 151,160,137,91,90,15,
            131,13,201,95,96,53,194,233,7,225,140,36,103,30,69,142,8,99,37,240,21,10,23,
            190, 6,148,247,120,234,75,0,26,197,62,94,252,219,203,117,35,11,32,57,177,33,
            88,237,149,56,87,174,125,20,136,171,168, 68,175,74,165,71,134,139,48,27,166,
            77,146,158,231,83,111,229,122,60,211,133,230,220,105,92,41,55,46,245,40,244,
            102,143,54, 65,25,63,161, 100,216,80,73,209,76,132,187,208, 89,18,169,200,196,
            135,130,116,188,159,86,164,1,109,198,173,186, 3,64,52,217,226,250,124,123,
            5,202,38,147,118,126,255,82,85,212,207,206,59,221,47,16,58,17,182,189,28,42,
            223,183,170,213,119,248,152, 2,44,154,163, 70,227,153,101,155,167, 43,172,9,
            129,22,39,253, 19,98,108,110,79,113,224,232,178,185, 112,104,218,246,97,228,
            251,34,242,193,238,210,144,12,191,179,162,241, 81,51,145,235,249,14,239,107,
            49,192,214, 31,181,199,106,157,184, 84,204,176,115,121,50,45,127, 4,150,254,
            138,236,205,93,222,114,67,29,24,72,243,141,128,195,78,66,215,61,156,180
    };


    private static void putInPermutation(){
        for (int i = 0; i < 256 ; i++) p[256+i] = p[i] = permutation[i];
    }

    public static void main(String[] args) {
    }

    public static void shuffle(){
        long startTime = System.nanoTime();
        System.out.println("    Suffling...");
        int temp;
        for (int i = 0; i < p.length; i++){
            Random random = new Random();
            int pos1 = random.nextInt(permutation.length);
            int pos2 = random.nextInt(permutation.length/2);
            temp = permutation[pos1];
            permutation[pos2] = permutation[pos1];
            permutation[pos1] = temp;
        }
        putInPermutation();
        long endTime = System.nanoTime();
        System.out.println("    Finished Shuffling " + (endTime - startTime) / 1000000 + "ms");
    }

    public static Double[][] getPerlinArray(){
        long startTime = System.nanoTime();
        System.out.println("    Getting Perlin Array...");

        Double[][] perlinArray = new Double[WORLD_WIDTH][WORLD_HEIGHT];

        //Fill with 0s
        for (int width = 0; width < WORLD_WIDTH; width++) {
            for (int height = 0; height < WORLD_HEIGHT; height++){
                perlinArray[width][height] = 0.0;
            }
        }

        for(int x = 0; x < WORLD_WIDTH; x++){
            for (int y = 0; y < WORLD_HEIGHT; y++){
                double n = noise2D(x*0.03, y*0.03);

                perlinArray[x][y] = n + perlinArray[x][y];
            }
        }

        long endTime = System.nanoTime();
        System.out.println("    Got Perlin Array" + (endTime - startTime) / 1000000 + "ms");
        shuffle();
        return perlinArray;
    }

    private static double noise2D(double x, double y) {
        int X = (int)Math.floor(x) & 255;//255 is the wrap value
        int Y = (int)Math.floor(y) & 255;

        double xf = x - Math.floor(x);
        double yf = y - Math.floor(y);

        Vector2D topRight = new Vector2D(xf - 1.0, yf - 1.0);
        Vector2D topLeft = new Vector2D(xf, yf - 1.0);
        Vector2D bottomRight = new Vector2D(xf - 1.0, yf);
        Vector2D bottomLeft = new Vector2D(xf, yf);

        int valueTopRight = p[p[X+1]+Y+1];
        int valueTopLeft = p[p[X]+Y+1];
        int valueBottomRight = p[p[X+1]+Y];
        int valueBottomLeft = p[p[X]+Y];


        //Dot products of each corner
        double dotTopRight = topRight.dot(getConstantVector(valueTopRight));
        double dotTopLeft = topLeft.dot(getConstantVector(valueTopLeft));
        double dotBottomRight = bottomRight.dot(getConstantVector(valueBottomRight));
        double dotBottomLeft = bottomLeft.dot(getConstantVector(valueBottomLeft));

        double u = fade(xf);
        double v = fade(yf);


        return Lerp(u,
                Lerp(v, dotBottomLeft, dotTopLeft),
                Lerp(v, dotBottomRight, dotTopRight)
        );
    }

    private static double fade(double t) {//t is Interpolation value;
        return ((6*t - 15) * t + 10) * t * t * t;
    }

    private static double Lerp(double t, double a1, double a2){// Linear Interpolation
        return a1 + t*(a2-a1);
    }

    private static Vector2D getConstantVector(double v) {
        int h = (int) v & 3;
        switch (h) {
            case 0:
                return new Vector2D(1.0, 1.0);
            case 1:
                return new Vector2D(-1.0, 1.0);
            case 2:
                return new Vector2D(-1.0, -1.0);
            default:
                return new Vector2D(1.0, -1.0);
        }
    }
}
