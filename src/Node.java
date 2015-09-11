/**
 * 
 */

import pacsim.PacCell;
import pacsim.PacFace;
import pacsim.PacUtils;
import pacsim.WallCell;

import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * @author chris
 *
 */
public class Node extends PacCell implements Comparable{

	private Node parent;
	private float G;
	public PacFace action;
	public ArrayList<Node> path;

	public Node(PacCell newpc) {
		super(newpc.getX(),newpc.getY());
		this.G = 0;
		super.cost=newpc.getCost();
		this.path = new ArrayList<Node>();
	}


	public Node(PacCell newpc, Node curNode) {
		super(newpc.getX(),newpc.getY());
		this.G = 0;
		super.cost=newpc.getCost();
		this.path = new ArrayList<Node>(curNode.path);
	}

	/**
	 * @return the parent
	 */
	public Node getParent() {
		return parent;
	}

	/**
	 * @param parent
	 *            the parent to set
	 */
	public void setParent(Node parent) {
		this.parent = parent;
	}

	@Override
	public boolean equals(Object object)
	{
        Node other = (Node) object;
		boolean sameSame = false;

		if (object != null)
		{
            if((this.getX()==other.getX()) && (this.getY()==other.getY()))
			    sameSame = true;
		}

		return sameSame;
	}

	@Override
	public int compareTo(Object arg0) {
		Node of = (Node) arg0;
		Float tmp = this.G;
		Float tmp2 = of.G;
		//if(x==of.x && y==of.y) return 0;//it is the same node
		return tmp.compareTo(tmp2);

		//if (getG() < of.getG()) {
		//	return -1;
		//} else if (getG() > of.getG()) {
		//	return 1;
		//} else {
		//	return 0;
		//}
	}

	public Node copy() {
		Node node = new Node(this.clone());
		node.setG(this.G);
		if(this.parent!=null)
			node.setParent(this.parent.copy());
		else
			node.setParent(null);
		if(this.action!=null)
			node.action=this.action;

		node.path = (ArrayList<Node>) this.path.clone();
		return node;
	}

	public Node copy(float g) {
		Node node = new Node(this.clone());
		node.setG(g);
		node.setParent(null);
		if(this.action!=null)
			node.action=this.action;
		node.path = (ArrayList<Node>) this.path.clone();
		return node;
	}

	public void setG(float f) {
		this.G=f;
		
	}
	public float getG() {
		return this.G;

	}
	public ArrayList<Node> path(){
		ArrayList<Node> open = new ArrayList<Node>();
		Node tmp = this;
		do {
			open.add(0,tmp.copy());
			tmp = tmp.getParent();

		} while (tmp != null);

		return open;
	}

	public ArrayList<Node> path(Node start){
		ArrayList<Node> open = new ArrayList<Node>();
		Node tmp = this.copy();

		do {
			open.add(0,tmp);
			tmp = tmp.getParent().copy();
			if(tmp !=null && tmp.equals(start)) {
				return open;
			}

		} while (tmp != null);
		if(open.size()<10)
			printRoute(open);
		return open;
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


	public void expandFrontier(PriorityQueue<Node> open, ArrayList<Node> closed, PacCell[][] grid) {
		for (PacFace face : PacFace.values()) {
			PacCell npc = PacUtils.neighbor(face, this, grid);

			// bounds checking: in grid, not wall , not current node
			if ((npc != null) && (!( npc instanceof WallCell))){
				Node tmp = new Node(npc,this);

				//already in closed list
				if (closed.contains(tmp)) {
					continue;
				}
				if(open.contains(tmp)) {
					continue;      //all costs one, if its here path is already shorter.
				}
				tmp.setParent(this);
				tmp.setG(this.getG() + 1);
				tmp.action = face;
				tmp.path.add(tmp);
				open.add(tmp);
			}
		}
	}
}
