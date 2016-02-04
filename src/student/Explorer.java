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

    private Set<Node> knownNodes = new HashSet<>();

    public void explore(ExplorationState state) {
        //TODO : Explore the cavern and find the orb
        Set<Long> visited = new HashSet<>();
        boolean moved;

        //create parent for ternary search tree
        TreeNode current = new TreeNode(state.getCurrentLocation());
        current.visit(state.getNeighbours());

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
                        neighbour.visit(nextNeighbours);
                        moved = true;
                        break;
                    }
                }
                //if all neighbours visited, move back
                if(!moved){
                    //System.out.println("all neighbours visited! Moving to: " + current.getPrevious().getId());
                    current = current.getPrevious();
                    state.moveTo(current.getId());
                }
            }else{
                //if all neighbours visited, move back
                //System.out.println("moving back");
                current = current.getPrevious();
                //System.out.println(current.getId());
                state.moveTo(current.getId());
            }
        }
        System.out.println("YAY!!!!!!!!!!!!!!!!!!!");
    }

    class TreeNode{
        private TreeNode previous;
        private long id;
        private List<TreeNode> neighbours;
        private boolean wasVisited;
        private long rating;
        private Node node;

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

        public TreeNode(Node node, TreeNode previous, long rating){
            this.previous = previous;
            this.id = node.getId();
            this.rating = rating;
            wasVisited = false;
            neighbours = null;
            this.node = node;
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
//                    try{
//                        knownNodes.add(aStatusArray.getId());
//                    }catch(IllegalArgumentException ignore){}
                }
            }
            //sort neighbours
            neighbours = neighbours.stream().sorted(TreeNode::compareTo).collect(Collectors.toList());
        }

        public void exploreAgain(Set<Node> neighbourNodes, Node exit, EscapeState state){
            wasVisited = true;
            neighbours = new ArrayList<>();

            neighbours.addAll(neighbourNodes.stream().filter(n -> n != null && !knownNodes.contains(n)).map(n -> new TreeNode(n, this, (state.getCurrentNode().getEdge(n).length + n.getId() - exit.getId()))).collect(Collectors.toList()));
            neighbours.stream().forEach(n -> System.out.println(n.id + " : " + n.rating));
            //sort neighbours
//            if(((state.getTimeRemaining() * 100)/state.getVertices().size()) > 300){
//                System.out.println("going for gold " + state.getTimeRemaining()* 100/state.getVertices().size());
//                neighbours = neighbours.stream().sorted(TreeNode::hasGold).collect(Collectors.toList());
//            }else {
                //System.out.println("going for the exit " + state.getTimeRemaining()*100/state.getVertices().size());
                neighbours = neighbours.stream().sorted(TreeNode::compareTo).collect(Collectors.toList());
//            }
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

        public Node getNode() {
            return node;
        }

        public boolean isNew(){
            return wasVisited;
        }

        public int compareTo(TreeNode other){
            return (int) Long.compare(rating, other.rating);
        }

        public int hasGold(TreeNode other){
            return Integer.compare(node.getTile().getGold(), other.node.getTile().getGold());
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
    private List<Node> reconstructPath(Map<Node, Node> path, Node current){
        if(path.isEmpty()) System.out.println("empty path");
        List<Node> newPath = new ArrayList<>();
        newPath.add(current);
        while(path.containsKey(current)){
            //System.out.println(current.getId() + " : " + path.get(current).getId());
            Node temp = path.get(current);
            path.remove(current, temp);
            current = temp;
            newPath.add(current);
        }
        return newPath;
    }

    public void escape(EscapeState state) {
        //TODO: Escape from the cavern before time runs out
        Map<Node, Double> closed = new HashMap<>();
        PriorityQueue<Node> open = new PriorityQueueImpl<>();
        Map<Node, Node> path = new HashMap<>();
        List<Node> newPath = null;

        Node current = state.getCurrentNode();
        open.add(current, 0);
        System.out.println("start: " + state.getCurrentNode().getId());
        state.getCurrentNode().getNeighbours().stream().forEach(n-> System.out.println(n.getId()));
        System.out.println("exit: " + state.getExit().getId());

        while(open.size() > 0){
            double distance = 0;
            if(closed.containsKey(current)){
                distance = closed.get(current);
            }
            Node previous = current;
            current = open.poll();
            //System.out.println(current.getId());
            if (current.equals(state.getExit())) {
                System.out.println("found the exit!");
                path.put(current, previous);
                newPath = reconstructPath(path, current);
                break;
            }
            Set<Node> neighbours = new HashSet<>();
            if (current.getNeighbours() != null && current.getNeighbours().size() != 0) {
                neighbours = current.getNeighbours();
            }
            for (Node node : neighbours) {
                double neighbourDistance = distance + current.getEdge(node).length;
                if (!closed.containsKey(node)) {
                    try {
                        open.add(node, neighbourDistance + (state.getExit().getId() - node.getId()));
                    }catch(IllegalArgumentException ignore){
                        //ignore this node
                    }
                    closed.put(current, neighbourDistance);
                    path.put(node, current);
                }else if(closed.get(node) > neighbourDistance){
                    //System.out.println("found a better way " + node.getId() + " : " + current.getId());
                    path.replace(node, current);
                    closed.replace(node, neighbourDistance);
                }

            }
        }
        System.out.println("done while loop");

        if(newPath != null){
            if (newPath.isEmpty()){
                System.out.println("failed to find path :(");
            }else {
                System.out.println(":)");
                newPath.stream().forEach(n -> System.out.println(n.getId()));
            }
        }else{
            System.out.println(":(");
        }



//        Node node = state.getCurrentNode();
//        Node exit = state.getExit();
////        System.out.println("current: " + node.getId());
////        System.out.println("exit: " + state.getExit().getId());
////        state.getCurrentNode().getNeighbours().stream().forEach(a -> System.out.println("Neighbour: " + a.getId()));
////        System.out.println("number of vertices: " + state.getVertices().size());
//
//        Set<Long> visited = new HashSet<>();
//        boolean moved;
//
//        TreeNode current = new TreeNode(node.getId());
//        current.exploreAgain(node.getNeighbours(), exit, state);
//
//        while(!node.equals(state.getExit())) {
//            moved = false;
//            visited.add(state.getCurrentNode().getId());
//            node = state.getCurrentNode();
//            //if there is gold on the tile, pick it up
//            if (node.getTile().getGold() != 0) {
//                state.pickUpGold();
//                //System.out.println("GOLD");
//            }
//            if(current.getNeighbours() != null && current.getNeighbours().size() != 0){
//                List<TreeNode> neighbours = current.getNeighbours();
//
//                //TreeNode neighbour = neighbours.stream().findFirst().get();
//                for (TreeNode neighbour : neighbours) {
//                    //if unexplored neighbours, move to one closest to destination
//                    if (!neighbour.wasVisited) {
//                        //System.out.println("time remaining: " + state.getTimeRemaining() + " distance: " + state.getCurrentNode().getEdge(neighbour.getNode()).length);
//                        //System.out.println("moving to: " + neighbour.getId());
//                        state.moveTo(neighbour.getNode());
//                        current = neighbour;
//                        node = state.getCurrentNode();
//                        Set<Node> nextNeighbours = node.getNeighbours().stream()
//                                .filter(a -> !visited.contains(a.getId()))
//                                .collect(Collectors.toSet());
//                        current.exploreAgain(nextNeighbours, exit, state);
//                        moved = true;
//                        break;
//                    }
//                }
//                if(!moved){
//                    //if all neighbours visited, move back
//                    backUp(state, current);
//                    node = state.getCurrentNode();
//                    Set<Node> nextNeighbours = node.getNeighbours().stream()
//                            .filter(a -> !visited.contains(a.getId()))
//                            .collect(Collectors.toSet());
//                    current.exploreAgain(nextNeighbours, exit, state);
//                }
//            }else{
//                //if all neighbours visited, move back
//                backUp(state, current);
//            }
//        }
    }

    private void backUp(EscapeState state, TreeNode current){
        //if all neighbours visited, move back
        final long previous = current.getPrevious().getId();
        if(current.getPrevious().getNode() != null && (state.getCurrentNode().getNeighbours().stream().filter(a -> a.getId() == previous).count() != 0)) {
            //System.out.println(current.getId() + " backing up to " + current.getPrevious().getNode().getId());
            current = current.getPrevious();
            state.moveTo(current.getNode());
            current.exploreAgain(state.getCurrentNode().getNeighbours(), state.getExit(), state);
        }else{
            current.exploreAgain(state.getCurrentNode().getNeighbours(), state.getExit(), state);
        }
    }
}
