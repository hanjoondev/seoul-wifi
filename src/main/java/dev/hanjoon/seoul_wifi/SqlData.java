package dev.hanjoon.seoul_wifi;

public class SqlData {
    int id;
    private final String wrdofc, adr1, adr2, inout;
    private final Double lat, lnt;
    private Double dist;

    public SqlData(int id, String w, String a1, String a2, String i, Double lat, Double lnt) {
        this.id = id;
        this.wrdofc = w;
        this.adr1 = a1;
        this.adr2 = a2;
        this.inout = i;
        this.lat = lat;
        this.lnt = lnt;
    }

    public Double getLat() { return this.lat; }
    public Double getLnt() { return this.lnt; }

    public void setDist(Double distance) { this.dist = distance; }

    public Double getDist() { return this.dist; }

    public String[] getResults() {
        return new String[] { String.format("%.3fkm", this.dist), this.wrdofc, this.adr1, this.adr2, this.inout,
                String.valueOf(this.lat), String.valueOf(this.lnt)};
    }
}
