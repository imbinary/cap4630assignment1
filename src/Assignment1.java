/**
 * Created by chris on 8/31/15.
 */
import java.awt.Point;
import java.io.File;
import javax.swing.JFileChooser;

import pacsim.PacAction;
import pacsim.PacCell;
import pacsim.PacFace;
import pacsim.PacSim;
import pacsim.PacUtils;
import pacsim.PacmanCell;

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
        //System.out.println("Action");
        // make sure Pacman is in this game

        if(pc != null) {
            if (apath == null) {
                apath = new UCS(grid);
            }

            newFace = apath.getNextMove(pc);
        }
        return newFace;
    }
}
