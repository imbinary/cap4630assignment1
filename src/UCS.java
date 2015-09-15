import pacsim.*;

import java.awt.*;
import java.util.*;


public class UCS {

    private PacCell[][] grid;
    private int expanded = 0;
    private ArrayList<PacCell> bestroute;
    private ArrayList<PacCell> goals;
    private PriorityQueue<Node> que;


    // Constructor
    public UCS(PacCell[][] grid) {
        this.grid = grid;
        PacCell pc = PacUtils.findPacman(grid);
        initGoals();
        Node n = new Node(pc, goals);
        que = new PriorityQueue<Node>(new Comparator<Node>() {
            public int compare(Node n1, Node n2) {
                return n1.path.size()- n2.path.size();
            }
        });
        que.add(n);
        solve();
    }

    
    // Locates the food dots on the initial board
    private void initGoals() {
        goals = new ArrayList<PacCell>();
        for(int x = 0; x < grid.length; x++) 
            for(int y = 0; y < grid[x].length; y++) 
                if (grid[x][y] instanceof FoodCell)
                    goals.add(grid[x][y]);
    }


    // Runs uniform cost search
    public void solve() {
        HashSet<ExplorePairs> explored = new HashSet<ExplorePairs>();
        while (que.size() > 0) {
            // Pop the stack
            Node node = que.poll();
            
            // Check for solution
            if (node.points.size() == 0) {
                System.out.println("Expaned: " + expanded);
                bestroute = node.path;
                bestroute.remove(0);
                return;
            }

            //Add node to explored
            explored.add(new ExplorePairs(node));

            expanded++;
            if (expanded%1000 == 0)
                System.out.println(expanded);

            for (Node n : node.expand(grid)) {
                if (!explored.contains(new ExplorePairs(n))) {
                    boolean inQue = false;
                    for (Node nd : que) {
                        if (nd.isEqual(n)) {
                            inQue = true;
                            if (n.path.size() < nd.path.size()) {
                                que.remove(nd);
                                que.add(n);
                            }
                            break;
                        }
                    }
                    if (!inQue) 
                        que.add(n);
                }
            }
        }
    }


    public PacFace getNextMove(PacmanCell pc) {
        PacFace newFace;
        PacCell newNode = bestroute.remove(0);
        System.out.println("moving to: " + newNode.getX() + " " + newNode.getY());
        if (newNode.getX() < pc.getX())
            newFace = PacFace.W;
        else if (newNode.getX() > pc.getX())
            newFace = PacFace.E;
        else if (newNode.getY() < pc.getY())
            newFace = PacFace.N;
        else
            newFace = PacFace.S;
        return newFace;
    }


    // Subclass made to be able to place already visited nodes into HashSet
    class ExplorePairs {
        int remainingLen;
        int length;
        Point p;

        public ExplorePairs(Node n) {
            this.length = n.path.size();
            remainingLen = n.points.size();
            this.p = n.path.get(n.path.size()-1).getLoc();
        }
    }
}

