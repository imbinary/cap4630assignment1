import pacsim.*;

import java.awt.*;
import java.util.*;

/**
 * Created by Chris on 9/2/2015.
 */
public class UCS {

    private ArrayList<Node> initgoals;
    private PacCell[][] grid;
    private int expanded = 0;
    private ArrayList<Node> bestroute;
    private ArrayList<Graph> graphs = new ArrayList<>();


    public UCS(Object map) {
        grid = (PacCell[][]) map;
        initGoals();
        bestroute = new ArrayList<>();

        graphs.add(ucGraph(new Node(PacUtils.findPacman(grid)), new ArrayList<Node>(initgoals)));
        for (Node start : initgoals)
            graphs.add(ucGraph(new Node(start), new ArrayList<Node>(initgoals)));
//        uniformCostSearch(state);
//        pathToGoal(new Node(PacUtils.findPacman(grid)), initgoals);
        System.out.println("total expanded: " + expanded);
        printRoute(bestroute);
        System.out.println("moves: " + bestroute.size());

    }

    private void initGoals() {
        initgoals = new ArrayList<>();
        for(PacCell[] x : grid)
            for(PacCell y : x)
                if (y instanceof FoodCell) {
                    Node tmp = new Node(y);
                    initgoals.add(tmp);
                }

    }


    @SuppressWarnings("unchecked")
    public Graph ucGraph(Node start, ArrayList<Node> goals) {
        PriorityQueue<Node> open = new PriorityQueue<>();
        ArrayList<Node> closed = new ArrayList<>();
        open.add(start); //add start node
        Graph graph = new Graph(start);
        goals.remove(start);
        //start frontier expansion

        while (!open.isEmpty()) {
            //add closest closed
            Node curNode = open.poll();
            closed.add(curNode);            //add to closed
            //reached a goal?

            if (goals.contains(curNode)) {
                graph.paths.add(new Path(curNode.path));
            }
                expandFrontier(open, closed, curNode);
                if ((expanded != 0) && (expanded % 10000) == 0)
                    System.out.println("expanded: " + expanded);


            }
        return graph;
        }


    @SuppressWarnings("unchecked")
    public void uniformCostSearch(ArrayList<Graph> graphs, ArrayList<Node> goals) {
            PriorityQueue<Graph> open = new PriorityQueue<>();
            ArrayList<Graph> closed = new ArrayList<>();

            open.add(graphs.remove(0)); //add start node
            //start frontier expansion

            while (!open.isEmpty()) {
                //add closest closed
                Graph curNode = open.poll();
                closed.add(curNode);            //add to closed
                //reached a goal?

                if (goals.contains(curNode)) {
                    if(curNode.start.path.containsAll(initgoals)){
                        printRoute(curNode.start.path);
                    }
                }
                else
                    curNode.expandFrontier(open, closed, graphs);
                expanded++;
                if ((expanded != 0) && (expanded % 10000) == 0)
                    System.out.println("expanded: " + expanded);


            }
        //return state;
        //no path found
        //System.out.println("No path found");
    }


    public void expandFrontier(PriorityQueue<Node> open, ArrayList<Node> closed, Node curNode) {
        expanded++;
        for (PacFace face : PacFace.values()) {
            PacCell npc = PacUtils.neighbor(face, curNode, grid);

            // bounds checking: in grid, not wall , not current node
            if ((npc != null) && (!( npc instanceof WallCell))){
                Node tmp = new Node(npc,curNode);

                //already in closed list
                if (closed.contains(tmp)) {
                    continue;
                }
                if(open.contains(tmp)) {
                    continue;      //all costs one, if its here path is already shorter.
                }
                tmp.setParent(curNode);
                tmp.setG(curNode.getG() + 1);
                tmp.action = face;
                tmp.path.add(tmp);
                open.add(tmp);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void pathToGoal(Node start, ArrayList<Node> goalList) {
        PriorityQueue<Node> open = new PriorityQueue<>();
        //ArrayList<Node> open = new ArrayList<>();
        ArrayList<Node> closed = new ArrayList<>();
        ArrayList<Node> goals = (ArrayList<Node>) goalList.clone();
        open.add(start.copy()); //add start node

        //start frontier expansion
        while (!open.isEmpty()) {
            //add closest closed

            Node curNode = open.poll();

            closed.add(curNode);            //add to closed
            //reached a goal?
            if(goalList.size()==9)
                printRoute(curNode.path());
            if (goals.contains(curNode)) {
                goals.remove(curNode);
                if (!goals.isEmpty()) {
                    //not the last goal, find next
                   pathToGoal(curNode, (ArrayList<Node>) goals.clone());
                } else {
                    //last goal
                    if(pathLength(curNode)<27)
                        printRoute(curNode.path());

                    return;
                }

            }
            expand(open, closed, curNode);
            if ((expanded != 0) && (expanded % 1000) == 0)
                System.out.println("expanded: " + expanded);

        }
        //no path found
        System.out.println("No path found");
    }

    public int pathLength(Node node) {
        if (node == null) {
            return 100000000;
        }
        int count;
        ArrayList<Node> open;
        open = node.path();
        open.remove(0);
        count = open.size();
        //printRoute(open);
        if(open.containsAll(initgoals)){
            //printRoute(open);
            if (bestroute.size() == 0 || count <= bestroute.size()) {
                bestroute = (ArrayList<Node>) open.clone();
                //printRoute(open);
            }
        }
        return count;
    }



    public void expand(PriorityQueue<Node> open, ArrayList<Node> closed, Node curNode) {
        expanded++;
        for (PacFace face : PacFace.values()) {
            PacCell npc = PacUtils.neighbor(face, curNode, grid);

            // bounds checking: in grid, not wall , not current node
            if ((npc != null) && (!( npc instanceof WallCell))){
                Node tmp = new Node(npc);

                //already in closed list
                if (closed.contains(tmp)) {
                    continue;
                }
                if(open.contains(tmp)) {
                    continue;
                }
                tmp.setParent(curNode);
                tmp.setG(curNode.getG() + 1);
                tmp.action = face;
                open.add(tmp);
            }
        }


    }

    public void printRoute(ArrayList<Node> path) {

        if (path == null || path.isEmpty()) {
            return;
        }
        for (Node temp : path) {
            System.out.print(temp.getX() + "," + temp.getY() + " " );
        }
        System.out.println(path.size());
    }

    public PacFace getNextMove(PacmanCell pc) {

        if (bestroute == null || bestroute.isEmpty()) {
            bestroute = null;
            return null;
        }
        PacFace newFace;
        Node newNode = bestroute.remove(0);
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


}

