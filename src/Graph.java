import pacsim.PacCell;
import pacsim.PacFace;
import pacsim.PacUtils;
import pacsim.WallCell;

import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * Created by chris on 9/10/15.
 */
public class Graph {
    public Node start;
    public Paths paths;

    public Graph(Node start){
        this.start = new Node(start);
        paths = new Paths();
    }


    @Override
    public boolean equals(Object object)
    {
        Graph other = (Graph) object;

        return this.start.equals(other.start);
    }


    public void expandFrontier(PriorityQueue<Graph> open, ArrayList<Graph> closed, ArrayList<Graph> graphs ) {



    }


}
