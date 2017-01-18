package com.hilaltanriverdi.analysis;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import static java.lang.Math.sqrt;

public class Summary {

    private int n;
    private double sx;
    private double sy;
    private double sxx;
    private double sxy;
    private double syy;
    private double sse;
    private double sst;

    public int getN() {
        return n;
    }

    public void setN(int n) {
        this.n = n;
    }

    public double getSx() {
        return sx;
    }

    public void setSx(double sx) {
        this.sx = sx;
    }

    public double getSy() {
        return sy;
    }

    public void setSy(double sy) {
        this.sy = sy;
    }

    public double getSxx() {
        return sxx;
    }

    public void setSxx(double sxx) {
        this.sxx = sxx;
    }

    public double getSxy() {
        return sxy;
    }

    public void setSxy(double sxy) {
        this.sxy = sxy;
    }

    public double getSyy() {
        return syy;
    }

    public void setSyy(double syy) {
        this.syy = syy;
    }

    public double getXm() {
        return sx / n;
    }

    public double getYm() {
        return sy / n;
    }

    public double getSse() {
        return sse;
    }

    public void setSse(double sse) {
        this.sse = sse;
    }

    public double getSst() {
        return sst;
    }

    public void setSst(double sst) {
        this.sst = sst;
    }

    public double getR() {

        return (n * sxy - sx * sy)
                / (sqrt(n * sxx - sx * sx)
                * sqrt(n * syy - sy * sy));
    }

    public double getRSquare() {
        return getR() * getR();
    }

    public double getLa() {
        return (sy * sxx - sx * sxy) / (n * sxx - sx * sx);
    }

    public double getLb() {
        return (n * sxy - sx * sy) / (n * sxx - sx * sx);
    }

    public static void save(String summaryPath, Summary... summaries) throws IOException {
        FileWriter fw = new FileWriter(summaryPath);
        BufferedWriter bw = new BufferedWriter(fw);

        bw.write("DATA SIZE\tSUM X\tSUM Y\tSUM X^2\tSUM X*Y\tSUM Y^2\tX AVG\tY AVG\tSSE\tSST\tLine A\tLine B\tR Square\r\n");
        for (Summary s : summaries) {
            bw.write(s.getN() + "\t "
                    + String.format("%9.10f", s.getSx()) + "\t "
                    + String.format("%9.10f", s.getSy()) + "\t "
                    + String.format("%9.10f", s.getSxx()) + "\t "
                    + String.format("%9.10f", s.getSxy()) + "\t "
                    + String.format("%9.10f", s.getSyy()) + "\t "
                    + String.format("%9.10f", s.getXm()) + "\t "
                    + String.format("%9.10f", s.getYm()) + "\t "
                    + String.format("%9.10f", s.getSse()) + "\t "
                    + String.format("%9.10f", s.getSst()) + "\t "
                    + String.format("%9.10f", s.getLa()) + "\t "
                    + String.format("%9.10f", s.getLb()) + "\t "
                    + String.format("%9.10f", s.getR() * s.getR()) + "\r\n");
        }

        bw.close();
    }
}
