import pacsim.*;

import java.awt.Point;
import java.util.HashSet;
import java.util.ArrayList;

public class Node {

    public ArrayList<PacCell> path;
    public HashSet<PacCell> points;


    public Node(PacCell initial, ArrayList<PacCell> goodies) {
        path = new ArrayList<PacCell>();
        points = new HashSet<PacCell>();
        for (PacCell pc : goodies)
            points.add(pc);
        path.add(initial);
    }


    public Node(PacCell newCell, Node oldNode) {
        path = new ArrayList<PacCell>(oldNode.path);
        path.add(newCell);
        points = new HashSet<PacCell>();
        for (PacCell pc : oldNode.points)
            points.add(pc);
        points.remove(newCell);
    }


    public boolean isEqual(Node other) {
        if (path.get(path.size()-1) != other.path.get(other.path.size()-1))
            return false;
        if (!points.equals(other.points))
            return false;
        return true;
    }


    public ArrayList<Node> expand(PacCell[][] grid) {
        ArrayList<Node> lst = new ArrayList<Node>();
        Point p = path.get(path.size()-1).getLoc();
        if (grid[p.x-1][p.y] instanceof WallCell == false)
            lst.add(new Node(grid[p.x-1][p.y], this));
        if (grid[p.x+1][p.y] instanceof WallCell == false)
            lst.add(new Node(grid[p.x+1][p.y], this));
        if (grid[p.x][p.y-1] instanceof WallCell == false)
            lst.add(new Node(grid[p.x][p.y-1], this));
        if (grid[p.x][p.y+1] instanceof WallCell == false)
            lst.add(new Node(grid[p.x][p.y+1], this));
        return lst;
    }

}
