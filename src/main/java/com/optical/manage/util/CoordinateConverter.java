package com.optical.manage.util;

/**
 * 坐标转换工具类
 * 支持 GCJ-02(火星坐标系) 与 WGS84 坐标系之间的转换
 * 高德地图使用 GCJ-02，PostGIS 使用 WGS84
 */
public class CoordinateConverter {

    private static final double PI = Math.PI;
    private static final double X_PI = PI * 3000.0 / 180.0;
    private static final double A = 6378245.0;  // 长半轴
    private static final double EE = 0.00669342162296594323;  // 扁率

    /**
     * GCJ-02 转 WGS84
     * 前端高德地图坐标 → 数据库存储
     *
     * @param gcjLat GCJ-02 纬度
     * @param gcjLng GCJ-02 经度
     * @return WGS84 坐标 [lat, lng]
     */
    public static double[] gcj02ToWgs84(double gcjLat, double gcjLng) {
        if (outOfChina(gcjLat, gcjLng)) {
            return new double[]{gcjLat, gcjLng};
        }
        double dLat = transformLat(gcjLng - 105.0, gcjLat - 35.0);
        double dLng = transformLng(gcjLng - 105.0, gcjLat - 35.0);
        double radLat = gcjLat / 180.0 * PI;
        double magic = Math.sin(radLat);
        magic = 1 - EE * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((A * (1 - EE)) / (magic * sqrtMagic) * PI);
        dLng = (dLng * 180.0) / (A / sqrtMagic * Math.cos(radLat) * PI);
        double wgsLat = gcjLat - dLat;
        double wgsLng = gcjLng - dLng;
        return new double[]{wgsLat, wgsLng};
    }

    /**
     * WGS84 转 GCJ-02
     * 数据库查询结果 → 前端高德地图
     *
     * @param wgsLat WGS84 纬度
     * @param wgsLng WGS84 经度
     * @return GCJ-02 坐标 [lat, lng]
     */
    public static double[] wgs84ToGcj02(double wgsLat, double wgsLng) {
        if (outOfChina(wgsLat, wgsLng)) {
            return new double[]{wgsLat, wgsLng};
        }
        double dLat = transformLat(wgsLng - 105.0, wgsLat - 35.0);
        double dLng = transformLng(wgsLng - 105.0, wgsLat - 35.0);
        double radLat = wgsLat / 180.0 * PI;
        double magic = Math.sin(radLat);
        magic = 1 - EE * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((A * (1 - EE)) / (magic * sqrtMagic) * PI);
        dLng = (dLng * 180.0) / (A / sqrtMagic * Math.cos(radLat) * PI);
        double gcjLat = wgsLat + dLat;
        double gcjLng = wgsLng + dLng;
        return new double[]{gcjLat, gcjLng};
    }

    /**
     * 批量转换 GCJ-02 坐标点为 WGS84
     *
     * @param points GCJ-02 坐标点数组，每个点为 [lat, lng]
     * @return WGS84 坐标点数组
     */
    public static double[][] gcj02PointsToWgs84(double[][] points) {
        double[][] result = new double[points.length][2];
        for (int i = 0; i < points.length; i++) {
            result[i] = gcj02ToWgs84(points[i][0], points[i][1]);
        }
        return result;
    }

    /**
     * 批量转换 WGS84 坐标点为 GCJ-02
     *
     * @param points WGS84 坐标点数组，每个点为 [lat, lng]
     * @return GCJ-02 坐标点数组
     */
    public static double[][] wgs84PointsToGcj02(double[][] points) {
        double[][] result = new double[points.length][2];
        for (int i = 0; i < points.length; i++) {
            result[i] = wgs84ToGcj02(points[i][0], points[i][1]);
        }
        return result;
    }

    /**
     * 判断是否在中国境外（境外不需要转换）
     */
    private static boolean outOfChina(double lat, double lng) {
        return lng < 72.004 || lng > 137.8347 || lat < 0.8293 || lat > 55.8271;
    }

    private static double transformLat(double x, double y) {
        double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y
                + 0.2 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * PI) + 20.0 * Math.sin(2.0 * x * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(y * PI) + 40.0 * Math.sin(y / 3.0 * PI)) * 2.0 / 3.0;
        ret += (160.0 * Math.sin(y / 12.0 * PI) + 320 * Math.sin(y * PI / 30.0)) * 2.0 / 3.0;
        return ret;
    }

    private static double transformLng(double x, double y) {
        double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y
                + 0.1 * Math.sqrt(Math.abs(x));
        ret += (20.0 * Math.sin(6.0 * x * PI) + 20.0 * Math.sin(2.0 * x * PI)) * 2.0 / 3.0;
        ret += (20.0 * Math.sin(x * PI) + 40.0 * Math.sin(x / 3.0 * PI)) * 2.0 / 3.0;
        ret += (150.0 * Math.sin(x / 12.0 * PI) + 300.0 * Math.sin(x / 30.0 * PI)) * 2.0 / 3.0;
        return ret;
    }
}
