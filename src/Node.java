import pacsim.*;

import java.util.HashSet;
import java.util.ArrayList;
import static pacsim.PacUtils.reverse;

public class Node extends PacCell {

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
