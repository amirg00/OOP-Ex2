package GraphGui;

import api.DirectedWeightedGraph;
import api.DirectedWeightedGraphAlgorithms;
import api.DirectedWeightedGraphAlgorithmsImpl;
import api.NodeData;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Dijkstra extends JFrame implements ActionListener {
    private JButton cancel;
    private JButton OK;
    private JTextField src_node_id;
    private JTextField dest_node_id;
    private JPanel Dijkstra;
    private PanelGraph panel;
    private DirectedWeightedGraph graph;
    private DirectedWeightedGraphAlgorithms algo;
    private FrameGraph frame;
    private Timer timer, timer_2;
    private int delay = 1000;
    private int cnt = 0, cnt_2 = 0;
    private ArrayList<ArrayList<NodeData>> adjacentVerts;


    public Dijkstra() {
        this.setContentPane(Dijkstra);

        //this.setPreferredSize(new Dimension(500,500));
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // exit the app
        this.pack();
        this.setTitle("Vertex Editor"); // title
        this.setResizable(false); // prevent this to resize
        this.setVisible(true);
        this.timer = new Timer(10, null);
        cancel.addActionListener(this);
        OK.addActionListener(this);
    }

    public Dijkstra(DirectedWeightedGraph graph, FrameGraph frame, PanelGraph panel) {
        this.setContentPane(Dijkstra);
        this.graph = graph;
        this.frame = frame;
        this.panel = panel;
        this.algo = new DirectedWeightedGraphAlgorithmsImpl();
        this.algo.init(graph);
        this.adjacentVerts = new ArrayList<>();
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // exit the app
        this.pack();
        this.setTitle("Vertex Editor"); // title
        this.setResizable(false); // prevent this to resize
        this.setVisible(true);
        cancel.addActionListener(this);
        OK.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (e.getSource() == cancel) {this.dispose();}

        if (e.getSource() == OK) {
            int src = Integer.parseInt(src_node_id.getText());
            int dest = Integer.parseInt(dest_node_id.getText());
            ArrayList<NodeData> optPath = (ArrayList<NodeData>) algo.shortestPath(src,dest);
            int optPathLentgh = optPath.size();
            if (graph.getNode(src) == null || graph.getNode(dest) == null) {
                new Invalid_Vertex_UI();
            } else {
                timer = new Timer(delay, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        cnt++;
                        if (cnt == optPathLentgh-1){timer.stop();}
                        drawOptPath(src,dest, cnt, optPath);
                    }
                });
                timer.start();
            }
        }





    }


//                timer = new Timer(delay, new ActionListener() {
//                    @Override
//                    public void actionPerformed(ActionEvent e) {
//                        System.out.println("sdsdsdsd");
//                        if (cnt%2 == 0){
//                             panel.setPointColor(0,Color.gray);
//                             panel.repaint();
//                        }
//                        if (cnt%2 == 1){
//                            panel.setPointColor(0,Color.blue);
//                            panel.repaint();
//                        }
//                        cnt++;
//                    }
//                });

                        //timer.start();



    public static void main(String[] args) {
        new Dijkstra();
    }


    public void drawOptPath(int src, int dest, int cnt, ArrayList<NodeData>optPath){
         int prevNode = optPath.get(cnt-1).getKey();
         int curr = optPath.get(cnt).getKey();
         if (graph.getEdge(curr,prevNode)!=null){
             panel.setEdgeColor(curr,prevNode,Color.green, frame.getBackground());
         }
         if (prevNode == src){
             panel.setPointColor(prevNode,Color.green, Color.green);
             //panel.setPointColor(0,Color.gray);
             panel.setPointColor(curr,Color.lightGray,Color.green);
             panel.setEdgeColor(prevNode,curr,Color.green,Color.green);

         }
         else if (curr == dest){
             panel.setPointColor(curr,Color.green,Color.green);
             //panel.setPointColor(0,Color.gray);
             panel.setPointColor(prevNode,Color.lightGray,Color.green);

             panel.setEdgeColor(prevNode,curr,Color.green,Color.green);
         }
         else{
             panel.setPointColor(curr,Color.darkGray,Color.green);
             //panel.setPointColor(0,Color.gray);
             panel.setPointColor(prevNode,Color.lightGray,Color.green);
             panel.setEdgeColor(prevNode,curr,Color.green,Color.green);
         }
        panel.repaint();
    }

}
