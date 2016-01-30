package student;

import game.*;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

public class Explorer {

    /**
     * Explore the cavern, trying to find the
     * orb in as few steps as possible. Once you find the
     * orb, you must return from the function in order to pick
     * it up. If you continue to move after finding the orb rather
     * than returning, it will not count.
     * If you return from this function while not standing on top of the orb,
     * it will count as a failure.
     * <p>
     * There is no limit to how many steps you can take, but you will receive
     * a score bonus multiplier for finding the orb in fewer steps.
     * <p>
     * At every step, you only know your current tile's ID and the ID of all
     * open neighbor tiles, as well as the distance to the orb at each of these tiles
     * (ignoring walls and obstacles).
     * <p>
     * In order to get information about the current state, use functions
     * getCurrentLocation(), getNeighbours(), and getDistanceToTarget() in ExplorationState.
     * You know you are standing on the orb when getDistanceToTarget() is 0.
     * <p>
     * Use function moveTo(long id) in ExplorationState to move to a neighboring
     * tile by its ID. Doing this will change state to reflect your new position.
     * <p>
     * A suggested first implementation that will always find the orb, but likely won't
     * receive a large bonus multiplier, is a depth-first search.
     *
     * @param state the information available at the current state
     */
    public void explore(ExplorationState state) {
        //TODO : Explore the cavern and find the orb
        Set<Long> visited = new HashSet<>();
        boolean moved;
        int stepsTaken = 0;

        //create parent for ternary search tree
        TreeNode current = new TreeNode(state.getCurrentLocation());
        current.visit(state.getNeighbours(), stepsTaken);

        //explore until at destination
        while(state.getDistanceToTarget() != 0){
            moved = false;
            visited.add(state.getCurrentLocation());
            if(current.getNeighbours() != null && current.getNeighbours().size() != 0){
                List<TreeNode> neighbours = current.getNeighbours();
                for (TreeNode neighbour : neighbours) {
                    //System.out.println("neighbour: " + neighbour.getId() + " seen: " + neighbour.isNew());
                    //if unexplored neighbours, move to one closest to destination
                    if (!neighbour.wasVisited) {
                        //System.out.println("moving to: " + neighbour.getId());
                        state.moveTo(neighbour.getId());
                        current = neighbour;
                        Collection<NodeStatus> nextNeighbours = state.getNeighbours().stream()
                                .filter(a -> !visited.contains(a.getId()))
                                .collect(Collectors.toSet());
                        stepsTaken++;
                        neighbour.visit(nextNeighbours, stepsTaken);
                        moved = true;
                        break;
                    }
                }
                //if all neighbours visited, move back
                if(!moved){
                    //System.out.println("all neighbours visited! Moving to: " + current.getPrevious().getId());
                    current = current.getPrevious();
                    state.moveTo(current.getId());
                    stepsTaken--;
                }
            }else{
                //if all neighbours visited, move back
                //System.out.println("moving back");
                current = current.getPrevious();
                //System.out.println(current.getId());
                state.moveTo(current.getId());
                stepsTaken--;
            }
        }
        System.out.println("YAY!!!!!!!!!!!!!!!!!!!");
    }

    class TreeNode{
        private TreeNode previous;
        private long id;
        private List<TreeNode> neighbours;
        private boolean wasVisited;
        private int rating;

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

        public void visit(Collection<NodeStatus> neighbourNodes, int stepsTaken){
            wasVisited = true;
            neighbours = new ArrayList<>();
            NodeStatus[] statusArray;

            statusArray = neighbourNodes
                        .stream()
                        .toArray(NodeStatus[]::new);

            for (NodeStatus aStatusArray : statusArray) {
                if (aStatusArray != null) {
                    neighbours.add(new TreeNode(aStatusArray, this, (stepsTaken + aStatusArray.getDistanceToTarget())));
                }
            }
            //sort neighbours
            neighbours.stream().sorted(TreeNode::compareTo).collect(Collectors.toList());
        }

        public long getId(){
            return id;
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
            return Integer.compare(rating, other.rating);
        }
    }

    /**
     * Escape from the cavern before the ceiling collapses, trying to collect as much
     * gold as possible along the way. Your solution must ALWAYS escape before time runs
     * out, and this should be prioritized above collecting gold.
     * <p>
     * You now have access to the entire underlying graph, which can be accessed through EscapeState.
     * getCurrentNode() and getExit() will return you Node objects of interest, and getVertices()
     * will return a collection of all nodes on the graph.
     * <p>
     * Note that time is measured entirely in the number of steps taken, and for each step
     * the time remaining is decremented by the weight of the edge taken. You can use
     * getTimeRemaining() to get the time still remaining, pickUpGold() to pick up any gold
     * on your current tile (this will fail if no such gold exists), and moveTo() to move
     * to a destination node adjacent to your current node.
     * <p>
     * You must return from this function while standing at the exit. Failing to do so before time
     * runs out or returning from the wrong location will be considered a failed run.
     * <p>
     * You will always have enough time to escape using the shortest path from the starting
     * position to the exit, although this will not collect much gold.
     *
     * @param state the information available at the current state
     */
    public void escape(EscapeState state) {
        //TODO: Escape from the cavern before time runs out
    }
}
