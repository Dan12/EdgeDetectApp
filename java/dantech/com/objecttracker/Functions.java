package dantech.com.objecttracker;

import dantech.com.objecttracker.ObjectDetector;

public class Functions {
    private static int[][] pointerNum = new int[][]{
            {1,-1},
            {1,0},
            {1,1},
            {0,1},
            {-1,1},
            {-1,0},
            {-1,-1},
            {0,-1}
    };

    public static boolean isInTolerance(int input, int[] targ){
        int[] test = getRGB(input);
        return Math.abs(test[0]-targ[0]) <= ObjectDetector.tolerance && Math.abs(test[1]-targ[1]) <= ObjectDetector.tolerance && Math.abs(test[2]-targ[2]) <= ObjectDetector.tolerance;
    }

    public static int[] getRGB(int[] input, int r, int c, int w){
        int val = input[r*w+c];
        return getRGB(val);
    }

    private static int[] getRGB(int input){
        return new int[]{(input) & 0xff, (input >> 8) & 0xff, (input >> 16) & 0xff};
    }

    public static int[] nextPos(int[] vals, int r, int c, int[] targ, int w, int h){
        boolean seenOpenPixel = false;
        int pointerNum = 0;
        int[] ret = new int[]{r,c};
        for(int i = 0; i < 8; i++){
            int[] pointer = mapPointerNum(i,0);
            if(inBounds(vals,r+pointer[1],c+pointer[0], w, h) && vals[(r+pointer[1])*w+(c+pointer[0])] != -1 && !isInTolerance(vals[(r+pointer[1])*w+(c+pointer[0])], targ)){
                pointerNum = i;
                seenOpenPixel = true;
                break;
            }
        }
        if(seenOpenPixel){
            for(int i = 0; i < 8; i++){
                int[] pointer = mapPointerNum(i,pointerNum);
                if(inBounds(vals,r+pointer[1],c+pointer[0], w, h) && vals[(r+pointer[1])*w+(c+pointer[0])] != -1 && isInTolerance(vals[(r+pointer[1])*w+(c+pointer[0])], targ)){
                    ret = new int[]{r+pointer[1],c+pointer[0]};
                    break;
                }
            }
        }
        return ret;
    }

    private static boolean inBounds(int[] input, int r, int c, int w, int h){
        return (r >= 0 && r < h && c >= 0 && c < w);
    }

    //x,y
    private static int[] mapPointerNum(int i, int s){
        int test = s+i;
        if(test > 7)
            test -= 8;
        return pointerNum[test];
    }
}
