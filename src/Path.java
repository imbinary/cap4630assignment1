import java.util.ArrayList;

/**
 * Created by chris on 9/5/15.
 */
public class Path {
    public Node start;
    public Node stop;
    public ArrayList<Node> path;
    public int cost;

    public Path(Node start){
        this.start = start;
        this.cost = 0;
        this.stop = start;
        this.path = new ArrayList<>();
    }

    public Path(){
        //this.start = start;
        this.cost = 0;
        this.stop = null;
        this.path = new ArrayList<>();
    }

    public Path(ArrayList<Node> nodes){
        //this.start = start;
        this.cost = nodes.size();
        this.path = new ArrayList<>(nodes);
        if(nodes.size()>0)
            this.stop = path.get(nodes.size()-1);
    }

    public void add(Node next){


        stop=next;
        if(start == null)
            start = next;
       // else {
            path.add(next);
            cost++;
        //}
    }

    public Path copy(){
        Path tmp = new Path();
        tmp.start = this.start.copy();
        tmp.stop = this.stop.copy();
        tmp.path = new ArrayList<Node>(this.path);
        tmp.cost=this.cost;
        return tmp;
    }

    public void addAll(ArrayList<Node> nodes){
        if (nodes == null)
            return;
        for (Node next : nodes) {
            if (start == null) {
                start = next;
            }else {
                cost++;
                path.add(next);
            }
            stop = next;

        }
    }

    public void removeLast(){

    }


    public void empty(){
        if(path == null)
            return;
        path.clear();
        start = null;
        stop = null;
        cost=0;
    }

    public Path reverse(){
        Path tmp = new Path();
        tmp.start = this.stop;
        tmp.stop = this.start;
        tmp.cost = this.cost;
        tmp.path = new ArrayList<Node>();
        for (Node next : this.path) {
            tmp.path.add(next.copy());
        }
        return tmp;
    }

    public void printRoute() {

        if (this.path == null || this.path.isEmpty()) {
            return;
        }
        for (Node temp : this.path) {
            System.out.print(temp.getX() + "," + temp.getY() + " ");
        }
        System.out.println(path.size());
    }

}
