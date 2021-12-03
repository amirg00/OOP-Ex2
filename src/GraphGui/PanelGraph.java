package GraphGui;

import api.DirectedWeightedGraph;
import api.NodeData;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

public class PanelGraph extends JPanel {
    private DirectedWeightedGraph graph;
    private java.util.List<GraphPoint> points;
    private List<GraphEdge> edges;
    private int radius = 10;
    private double Phi = Math.toRadians(40);
    private double virtualScale = 1.0;

    private Point2D minRange;
    private Point2D maxRange;

    PanelGraph(DirectedWeightedGraph graph) {
        this.setPreferredSize(new Dimension(1000, 1000));
        this.points = new ArrayList<>();
        this.graph = graph;
        pointInit();
        EdgeInit();
        setMinMaxRange();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(500, 500);
    }
    
    /********************************************************************************************************
    * <paintComponent>
    * paintComponent method draw the all edges and points in the graph.
    * </paintComponent>
    *
    *********************************************************************************************************/

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();
        FontMetrics fm = g2d.getFontMetrics();
        double insets = fm.getHeight() + radius;

        for (GraphEdge ed : edges) {
            ArrayList<GraphPoint> p = ed.getPoints();
            paintLine(g2d, p.get(0), p.get(1), insets, ed.getWeight());
        }

        for (GraphPoint gp : points) {
            paintPoint(g2d, gp, insets);
        }
        g2d.dispose();
    }

    private boolean CheckEdge(GraphPoint from, GraphPoint to){
        return graph.getEdge(Integer.parseInt(from.getId()), Integer.parseInt(to.getId())) != null;
    }

    protected double angleBetween(Point2D from, Point2D to) {
        double x = from.getX();
        double y = from.getY();
        double deltaX = to.getX() - x;
        double deltaY = to.getY() - y;
        double rotation = -Math.atan2(deltaX, deltaY);
        rotation = Math.toRadians(Math.toDegrees(rotation) + 180);
        return rotation;
    }

    protected Point2D getPointOnCircle(Point2D center, double radians) {
        double x = center.getX();
        double y = center.getY();
        radians = radians - Math.toRadians(90.0);
        double xPosy = Math.round((float) (x + Math.cos(radians) * radius));
        double yPosy = Math.round((float) (y + Math.sin(radians) * radius));
        return new Point2D.Double(xPosy, yPosy);
    }

    /********************************************************************************************************
     * @name: paintLine Method take the graphics2D, 2 points and weight.
     * we calculate the scale of the points in the graph because we want the graph accuracy.
     * we calculate the angel between two point in the graph and draw line to the tip of the point.
     *********************************************************************************************************/

    private void paintLine(Graphics2D g2d, GraphPoint from, GraphPoint to, double insets, String weight) {
        boolean flag = CheckEdge(to,from);
        Point2D fromPoint = translate(from, insets);
        Point2D toPoint = translate(to, insets);
        g2d.setColor(Color.RED);
        double fromp = angleBetween(fromPoint, toPoint);
        double top = angleBetween(toPoint, fromPoint);
        Point2D pointFrom = getPointOnCircle(fromPoint, fromp);
        Point2D pointTo = getPointOnCircle(toPoint, top);
        Line2D line = new Line2D.Double(pointFrom, pointTo);
        drawArrowHead(g2d, pointTo, pointFrom, Color.RED);
        g2d.draw(line);
        StringWeight(g2d, weight, to, from, insets, flag);
    }

    private void StringWeight(Graphics2D g2d, String weight, GraphPoint to, GraphPoint from, double insets,boolean check) {
        Point2D translated = translate(from, insets);
        Point2D translated2 = translate(to, insets);

        double xPos = translated.getX();
        double yPos = translated.getY();
        double xPos2 = translated2.getX();
        double yPos2 = translated2.getY();
        double m_Segment = (yPos2-yPos)/(xPos2-xPos);
        double x_center = (xPos + xPos2) / 2;
        double y_center = (yPos + (yPos2)) / 2;
        double[][] points_ver = Verticle(x_center,y_center,m_Segment,0);
        g2d.setPaint(Color.black);
        if (weight.length() > 13) weight = weight.substring(0, weight.length() - 12);
        g2d.setPaint(Color.black);
        g2d.setFont(new Font("Cabin", Font.PLAIN, 13));
        if(check) {
            g2d.drawString(weight, (float) points_ver[0][0], (float) points_ver[0][1]);
        }
        else{
            g2d.drawString(weight, (float) points_ver[1][0], (float) points_ver[1][1]);
        }
    }

    private double[][] Verticle(double x1, double y1, double m_Segment, int length){
        double m = -1/m_Segment;
        // y = mx - mx1 + y1
        double k = (1/Math.pow(m,2)) + 1;
        double c = -2*y1*(((x1/m) + y1)) + Math.pow(y1,2) + Math.pow(x1,2) - Math.pow(length,2);

        double x1_ans = x1 + ((Math.sqrt((x1*x1*k*k)-(k*c)))/k);
        double x2_ans = -x1_ans;

        // finds the y for the resulted points.
        double y1_ans = m*x1_ans - m*x1 + y1;
        double y2_ans = m*x2_ans - m*x1 + y1;

        return new double[][]{{x1_ans,y1_ans},{x2_ans,y2_ans}};
    }

    private void paintPoint(Graphics2D g2d, GraphPoint gp, double insets) {
        Graphics2D g2 = (Graphics2D) g2d.create();

        Point2D translated = translate(gp, insets);

        double xPos = translated.getX();
        double yPos = translated.getY();

        double offset = radius;

        g2.translate(xPos - offset, yPos - offset);
        g2.setPaint(Color.blue);
        g2.fill(new Ellipse2D.Double(0, 0, offset * 2, offset * 2));

        FontMetrics fm = g2d.getFontMetrics();
        String text = gp.getId();
        double x = xPos - (fm.stringWidth(text) / 2);
        double y = (yPos - radius - fm.getHeight()) + fm.getAscent();
        g2d.setPaint(Color.black);
        g2d.setFont(new Font("Cabin", Font.BOLD, 14));
        g2d.drawString(text, (float) x, (float) y);

        g2.dispose();
    }
    
    /********************************************************************************************************
    * <drawArrowHead>
    * this function for each line we are add a arrow head to the tip of the point just to "touch" the point,
    * so we can see the direction for each point.
    *</drawArrowHead>
    *********************************************************************************************************/

    private void drawArrowHead(Graphics2D g2, Point2D tip, Point2D tail, Color color) {
        int ArrowSize = 10;
        g2.setPaint(color);
        double dy = tip.getY() - tail.getY();
        double dx = tip.getX() - tail.getX();
        double theta = Math.atan2(dy, dx);
        double x, y, rho = theta + Phi;
        for (int j = 0; j < 2; j++) {
            x = (tip.getX() - ArrowSize * Math.cos(rho));
            y = (tip.getY() - ArrowSize * Math.sin(rho));
            g2.draw(new Line2D.Double(tip.getX(), tip.getY(), x, y));
            rho = theta - Phi;
        }
    }

    protected Point2D translate(GraphPoint gp, double insets) {
        double xRange = maxRange.getX() - minRange.getX();
        double yRange = maxRange.getY() - minRange.getY();

        double offset = insets;
        double width = getWidth() - (offset * 2);
        double height = getHeight() - (offset * 2);

        double xScale = width / xRange;
        double yScale = height / yRange;

        Point2D original = gp.getPoint();

        double x = offset + ((original.getX() - minRange.getX()) * xScale);
        double y = offset + ((original.getY() - minRange.getY()) * yScale);

        System.out.println(gp.getId() + " " + x + " x " + y);

        return new Point2D.Double(x, y);
    }

    /*
     * the for loop is init all the edges existed in the graph and take each point already exists,
     * make a new arraylist of points and know to the correct edges between them.
     * TODO: need to make a arrow direction to the line, take it from the last edit in my project
     * */
    public void EdgeInit() {
        edges = new ArrayList<>();
        for (int i = 0; i < graph.nodeSize(); i++) {
            for (int j = 0; j < graph.nodeSize(); j++) {
                if (i == j) continue;
                ArrayList<GraphPoint> temp = new ArrayList<>();
                if (graph.getEdge(i, j) != null) {
                    temp.add(new GraphPoint(String.valueOf(graph.getNode(graph.getEdge(i, j).getSrc()).getKey()), new Point2D.Double(graph.getNode(graph.getEdge(i, j).getSrc()).getLocation().x(), graph.getNode(graph.getEdge(i, j).getSrc()).getLocation().y())));
                    temp.add(new GraphPoint(String.valueOf(graph.getNode(graph.getEdge(i, j).getDest()).getKey()), new Point2D.Double(graph.getNode(graph.getEdge(i, j).getDest()).getLocation().x(), graph.getNode(graph.getEdge(i, j).getDest()).getLocation().y())));
                    edges.add(new GraphEdge(String.valueOf(graph.getEdge(i, j).getWeight()), temp));
                }
            }
        }
    }

    /*
     * the for loop is init all the points in the graph.
     * By given a name of the vertex and the positions x,y.
     * */
    public void pointInit() {
        for (int i = 0; i < graph.nodeSize(); i++) {
            NodeData curr = graph.getNode(i);
            if (curr != null) {
                String currKey = String.valueOf(graph.getNode(i).getKey());
                double currPosX = graph.getNode(i).getLocation().x();
                double currPosY = graph.getNode(i).getLocation().y();
                points.add(new GraphPoint(currKey, new Point2D.Double(currPosX, currPosY)));
            }
        }
    }

    /*
     * search if the point is equal to the string id  and get the point from the points objects.
     * draw each line have edges.
     */
    public void setMinMaxRange() {

        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;

        for (GraphPoint gp : points) {
            minX = Math.min(minX, gp.getPoint().getX());
            maxX = Math.max(maxX, gp.getPoint().getX());
            minY = Math.min(minY, gp.getPoint().getY());
            maxY = Math.max(maxY, gp.getPoint().getY());
        }

        minRange = new Point2D.Double(minX, minY);
        maxRange = new Point2D.Double(maxX, maxY);
    }
}



