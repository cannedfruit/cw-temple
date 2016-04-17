package student;

import game.NodeStatus;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Ternary tree node used during explore phase
 * Created by Sarah on 2/4/2016.
 */
class TreeNode {
    private TreeNode previous;
    private long id;
    private List<TreeNode> neighbours;
    private boolean wasVisited;
    private long rating;

    TreeNode(long id){
        this.id = id;
        this.previous = null;
        wasVisited = false;
        neighbours = null;
    }

    private TreeNode(NodeStatus node, TreeNode previous, int rating){
        this.previous = previous;
        this.id = node.getId();
        this.rating = rating;
        wasVisited = false;
        neighbours = null;
    }

    void visit(Collection<NodeStatus> neighbourNodes){
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
        neighbours = neighbours.stream().sorted(TreeNode::compareRating).collect(Collectors.toList());
    }

    long getId(){
        return id;
    }

    boolean wasVisited(){
        return wasVisited;
    }

    TreeNode getPrevious(){
        return previous;
    }

    List<TreeNode> getNeighbours(){
        return neighbours;
    }

    public boolean isNew(){
        return wasVisited;
    }

    private int compareRating(TreeNode other){
        return Long.compare(rating, other.rating);
    }
}
