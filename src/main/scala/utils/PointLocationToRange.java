package utils;

import java.util.List;

public class PointLocationToRange {


    /**
     * 判断点是否在多边形内部
     * @param p 目标点
     * @param list 所有多边形所构成的点
     * @return
     */
    private static String rayCasting(Point p, List<Point> list) {
        double px = p.getX(), py = p.getY();
        boolean flag = false;
        //
        for (int i = 0, l = list.size(), j = l - 1; i < l; j = i, i++) {
            //取出边界的相邻两个点
            double sx = list.get(i).getX(),
                    sy = list.get(i).getY(),
                    tx = list.get(j).getX(),
                    ty = list.get(j).getY();
            // 点与多边形顶点重合
            if ((sx == px && sy == py) || (tx == px && ty == py)) {
                return "on";
            }
            // 判断线段两端点是否在射线两侧
            //思路:作p点平行于y轴的射线 作s,t的平行线直线  如果射线穿过线段，则py的值在sy和ty之间
            if ((sy < py && ty >= py) || (sy >= py && ty < py)) {
                // 线段上与射线 Y 坐标相同的点的 X 坐标 ,即求射线与线段的交点
                double x = sx + (py - sy) * (tx - sx) / (ty - sy);
                // 点在多边形的边上
                if (x == px) {
                    return "on";
                }
                // 射线穿过多边形的边界
                if (x > px) {
                    flag = !flag;
                }
            }
        }
        // 射线穿过多边形边界的次数为奇数时点在多边形内
        return flag ? "in" : "out";
    }
}
