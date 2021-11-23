package api;

public class EdgeDataImpl implements EdgeData {
    private int src, dest, tag;
    private double weight;
    private String info;

    public EdgeDataImpl(int src, int dest, int tag, double weight, String info){
        try {
            if (weight < 0){
                this.weight = weight;
            }
        }
        catch (Exception c){
            System.out.println("ERROR: Weight got is a negative number");
        }
        this.src = src;
        this.dest = dest;
        this.tag = tag;
        this.info = info;
    }

    public EdgeDataImpl(EdgeData other){
        this.weight = other.getWeight();
        this.src = other.getSrc();
        this.dest = other.getDest();
        this.tag = other.getTag();
        this.info = other.getInfo();
    }

    @Override
    public int getSrc() {
        return this.src;
    }

    @Override
    public int getDest() {
        return this.dest;
    }

    @Override
    public double getWeight() {
        return this.weight;
    }

    @Override
    public String getInfo() {
        return this.info;
    }

    @Override
    public void setInfo(String s) {
        this.info = s;
    }

    @Override
    public int getTag() {
        return this.tag;
    }

    @Override
    public void setTag(int t) {
        this.tag = t;
    }
    public EdgeData deepCopy(EdgeDataImpl other){
        return new EdgeDataImpl(other.src, other.dest, other.tag, other.weight, other.info);
    }


}