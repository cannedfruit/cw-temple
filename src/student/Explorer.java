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
                    if (!neighbour.wasVisited()) {
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
        List<Node> path = mapBestPath(state);
        //path.stream().forEach(n -> System.out.println(n.getId()));

        if (path != null) {
            Collections.reverse(path);
            path.stream().filter(aNewPath -> aNewPath != null && !aNewPath.equals(state.getCurrentNode())).forEach(node -> moveAndFindGold(state, node));
        }else{
            System.out.println("failed to find path :(");
        }
    }

    private void moveAndFindGold(EscapeState state, Node node){
        if(state.getCurrentNode().getTile().getGold() > 0){
            state.pickUpGold();
        }
        state.moveTo(node);
    }

    private List<Node> mapBestPath(EscapeState state){
        Map<Node, Double> dist = new HashMap<>();
        PriorityQueue<Node> open = new PriorityQueueImpl<>();
        Map<Node, Node> prev = new HashMap<>();
        PriorityQueue<List<Node>> possiblePaths = new PriorityQueueImpl<>();
        Collection<Node> vertices = state.getVertices();

        vertices.stream().forEach(n -> prev.put(n, null));
        vertices.stream().forEach(n -> dist.put(n, Double.MAX_VALUE));
        vertices.stream().forEach(n -> open.add(n, dist.get(n)));

        Node current = state.getCurrentNode();
        dist.replace(current, 0.0);
        prev.replace(current, null);
        open.updatePriority(current, 0);
        //System.out.println("start: " + state.getCurrentNode().getId());
        //state.getCurrentNode().getNeighbours().stream().forEach(n-> System.out.println("neighbour: " + n.getId()));
        //System.out.println("exit: " + state.getExit().getId());

        while(open.size() > 0){
            double distance = dist.get(current) - current.getTile().getGold();
            current = open.poll();
            if (current.equals(state.getExit()) && distance <= state.getTimeRemaining()) {
                System.out.println("found the exit!");
                possiblePaths.add(reconstructPath(prev, current), -distance);
                //break;
            }

            if(possiblePaths.size() > 4){
                break;
            }

            Set<Node> neighbours = new HashSet<>();
            if (current.getNeighbours() != null && current.getNeighbours().size() != 0) {
                neighbours = current.getNeighbours();
            }
            for (Node node : neighbours) {

                double neighbourDistance = distance + current.getEdge(node).length - node.getTile().getGold();
                if(dist.get(node) > neighbourDistance){
                    try {
                        open.updatePriority(node, neighbourDistance);
                        prev.replace(node, current);
                        dist.replace(node, neighbourDistance);
                    }catch(IllegalArgumentException ignore){}
                }

            }
        }
        System.out.println("number of paths: " + possiblePaths.size());
        return possiblePaths.poll();
    }

    private List<Node> reconstructPath(Map<Node, Node> path, Node current){
        if(path.isEmpty()) System.out.println("empty path");
        List<Node> newPath = new ArrayList<>();
        newPath.add(current);
        while(path.containsKey(current)){
            current = path.get(current);
            if(current != null) {
                //System.out.println(current.getId() + " : " + path.get(current).getId());
                newPath.add(current);
            }
        }
        return newPath;
    }
}
