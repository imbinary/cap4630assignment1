import java.util.ArrayList;

/**
 * Created by chris on 9/5/15.
 */
public class Paths extends ArrayList<Path> {

    public Paths(){
        super();
    }

    public boolean addIf(Path path) {
        boolean found = false;
        for(Path tmp : this){
            if(tmp.start.equals(path.start) && tmp.stop.equals(path.stop)){
                found = true;
                if(tmp.cost > path.cost)
                    return super.add(path);
            }
        }
        if(!found)
            return super.add(path);
        return false;
    }

    public Path shortest(){
        Path shorty = null;
        for(Path tmp : this) {
            if((shorty==null)||(tmp.cost<shorty.cost))
                shorty=tmp;
        }
        return shorty;
    }
}
