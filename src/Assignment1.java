/**
 * Created by chris on 8/31/15.
 */

import java.awt.Point;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;

import pacsim.*;

import static pacsim.PacUtils.reverse;

public class Assignment1 implements PacAction {

    private Point target;
    private UCS apath;
    public Assignment1(String fname) {
        PacSim sim = new PacSim(fname);
        sim.init(this);
    }

    public static void main(String[] args) {
        String fname = args[0];
        new Assignment1(fname);
    }

    @Override
    public void init() {
        target = null;
        apath = null;
    }

    @Override
    public PacFace action(Object state) {

        PacCell[][] grid = (PacCell[][]) state;
        PacFace newFace = null;
        PacmanCell pc = PacUtils.findPacman(grid);

        if(pc != null) {
            if (apath == null) {
                apath = new UCS(grid);
            }

            newFace = apath.getNextMove(pc);
        }
        return newFace;
    }
}
class UCS {

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
            if (node.goals.size() == 0) {
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
        PacFace newFace = null;
        PacCell newNode = bestroute.remove(0);
        System.out.println("moving to: " + newNode.getX() + " " + newNode.getY());
        if (newNode.getX() < pc.getX())
            newFace = PacFace.W;
        else if (newNode.getX() > pc.getX())
            newFace = PacFace.E;
        else if (newNode.getY() < pc.getY())
            newFace = PacFace.N;
        else if (newNode.getY() > pc.getY())
            newFace = PacFace.S;
        return newFace;
    }






}

// Subclass made to be able to place already visited nodes into HashSet
class ExplorePairs {
    int remainingLen;
    int length;
    Point p;

    public ExplorePairs(Node n) {
        this.length = n.path.size();
        remainingLen = n.goals.size();
        this.p = n.path.get(n.path.size()-1).getLoc();
    }
}

class Node extends PacCell {

    public ArrayList<PacCell> path;
    public HashSet<PacCell> goals;


    public Node(PacCell initial, ArrayList<PacCell> goodies) {
        super(initial.getX(),initial.getY());
        super.cost=initial.getCost();
        path = new ArrayList<PacCell>();
        goals = new HashSet<PacCell>();
        for (PacCell pc : goodies)
            goals.add(pc);
        path.add(initial);
    }


    public Node(PacCell newCell, Node oldNode) {
        super(newCell.getX(),newCell.getY());
        super.cost=newCell.getCost();
        path = new ArrayList<PacCell>(oldNode.path);
        path.add(newCell);
        goals = new HashSet<PacCell>();
        for (PacCell pc : oldNode.goals)
            goals.add(pc);
        goals.remove(newCell);
    }


    public boolean isEqual(Node other) {
        if (path.get(path.size()-1) != other.path.get(other.path.size()-1))
            return false;
        if (!goals.equals(other.goals))
            return false;
        return true;
    }

    // add all in direction until branch
    public Node expandDirection(Node start, PacFace dir, PacCell[][] grid) {

        Node prev=start;
        //check to see if branch
        while(true) {
            int open = 0;
            Node next = prev;
            if((  grid[next.getX()][next.getY()] instanceof FoodCell))
                return next;
            for (PacFace face : PacFace.values()) {
                PacCell npc = PacUtils.neighbor(face, prev, grid);
                if ((npc != null) && (!(npc instanceof WallCell))) {
                    if (!dir.equals(reverse(face))) {
                        next = new Node(npc, next);
                        open++;
                    }
                }
                if(open >1){
                    //it's a branch return this node
                    return prev;
                }

            }
            //start node...return
            if (next.isEqual(start) || (  grid[next.getX()][next.getY()] instanceof FoodCell) ){
                return next;
            }

            if (open == 0) {
                //reverse
                dir = reverse(dir);
            }
            prev = next;
        }

    }

    public ArrayList<Node> expand(PacCell[][] grid) {
        ArrayList<Node> lst = new ArrayList<Node>();

        for (PacFace face : PacFace.values()) {
            PacCell npc = PacUtils.neighbor(face, this, grid);
            // bounds checking: in grid, not wall , not current node
            if ((npc != null) && (!( npc instanceof WallCell))){
                //lst.add(new Node(npc, this));
                lst.add(expandDirection(new Node(npc, this),face,grid));
            }
        }

        return lst;
    }

}
