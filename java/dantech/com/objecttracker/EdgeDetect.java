package dantech.com.objecttracker;

import java.util.ArrayList;

public class EdgeDetect {

        public static ArrayList<ShapeRectangle> runRoutine(int[] vals, int[] targ, int w, int h) {
            ArrayList<ShapeRectangle> ret = new ArrayList<ShapeRectangle>();
            //System.out.println("Running edge detect routine...");
            //System.out.println("Target RGB: " + targ[0] + "," + targ[1] + "," + targ[2]);
            int r = 0;
            int c = 0;
            while (r < h) {
                if (vals[r * w + c] != -1) {
                    if (Functions.isInTolerance(vals[r * w + c], targ)) {
                        int[] initEdge = new int[]{r, c};
                        int[] curPos = new int[]{r, c};
                        ShapeRectangle temp = new ShapeRectangle();
                        temp.newPoint(curPos);
                        boolean validShape = true;
                        while (true) {
                            ///System.out.println(curPos[0]+","+curPos[1]);
                            int[] newPos = Functions.nextPos(vals, curPos[0], curPos[1], targ, w, h);
                            if (newPos[0] == initEdge[0] && newPos[1] == initEdge[1]) {
                                //System.out.println("Success");
                                break;
                            }
                            if (newPos[0] == curPos[0] && newPos[1] == curPos[1]) {
                                //System.out.println("Didn't Close");
                                break;
                            }
                            curPos = newPos;
                            vals[curPos[0] * w + curPos[1]] = -1;
                            temp.newPoint(curPos);
                        }
                        int density = 0;
                        for (int rr = temp.getMinY(); rr <= temp.getMaxY(); rr++) {
                            for (int cc = temp.getMinX(); cc <= temp.getMaxX(); cc++) {
                                if (vals[rr * w + cc] != -1 && Functions.isInTolerance(vals[rr * w + cc], targ))
                                    density++;
                                vals[rr * w + cc] = -1;
                            }
                        }
                        temp.setDensity(density);
                        if (validShape && temp.isValid()) {
                            //System.out.println("Added");
                            ret.add(temp);
                        }
                    }
                }
                c++;
                if (c >= w) {
                    r++;
                    c = 0;
                }
            }

            return ret;
        }

}