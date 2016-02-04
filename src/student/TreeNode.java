package student;

import game.NodeStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Sarah on 2/4/2016.
 */
public class TreeNode {
    private TreeNode previous;
    private long id;
    private List<TreeNode> neighbours;
    private boolean wasVisited;
    private long rating;

    public TreeNode(long id){
        this.id = id;
        this.previous = null;
        wasVisited = false;
        neighbours = null;
    }

    public TreeNode(NodeStatus node, TreeNode previous, int rating){
        this.previous = previous;
        this.id = node.getId();
        this.rating = rating;
        wasVisited = false;
        neighbours = null;
    }

    public void visit(Collection<NodeStatus> neighbourNodes){
        wasVisited = true;
        neighbours = new ArrayList<>();
        NodeStatus[] statusArray;

        statusArray = neighbourNodes
                .stream()
                .toArray(NodeStatus[]::new);

        for (NodeStatus aStatusArray : statusArray) {
            if (aStatusArray != null) {
                neighbours.add(new TreeNode(aStatusArray, this, (aStatusArray.getDistanceToTarget())));
            }
        }
        //sort neighbours
        neighbours = neighbours.stream().sorted(TreeNode::compareTo).collect(Collectors.toList());
    }

    public long getId(){
        return id;
    }

    public boolean wasVisited(){
        return wasVisited;
    }

    public TreeNode getPrevious(){
        return previous;
    }

    public List<TreeNode> getNeighbours(){
        return neighbours;
    }

    public boolean isNew(){
        return wasVisited;
    }

    public int compareTo(TreeNode other){
        return Long.compare(rating, other.rating);
    }
}
