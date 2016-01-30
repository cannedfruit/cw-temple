package student;

import game.*;

import java.lang.reflect.Array;
import java.util.*;

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

        PriorityQueue<Long> known = new PriorityQueueImpl<>();
        Stack<Long> previous = new Stack<>();
//
        int amountTravelled = 0;
//
//
//        System.out.println("start: " + state.getDistanceToTarget());
//        while (state.getDistanceToTarget() != 0) {
//            System.out.println("current: " + state.getCurrentLocation());
//
            Collection<NodeStatus> neighbours = state.getNeighbours();
            System.out.println(neighbours.size());
//
//            previous.add(state.getCurrentLocation());
//
//            addNeighbours(neighbours, previous, known, amountTravelled);
//
//            Long next = getNext(known, previous);
//            if(next == -1) break;
//
//            try {
//                System.out.println("best next: " + next);
//                if (next == state.getCurrentLocation()) {
//                    System.out.println("last node: " + previous.peek());
//                    next = known.poll();}
//                try {
//                    System.out.println("moving to: " + next);
//                    state.moveTo(next);
//                    amountTravelled++;
//                } catch (IllegalArgumentException ex) {
//                    //TODO
//                    System.out.println(ex.getMessage());
//                    final Long finalNext1 = next;
//                    while(!state.getNeighbours().stream().anyMatch(n -> n.getId() == finalNext1)){
//                        if(previous.peek() == state.getCurrentLocation()) previous.pop();
//                        System.out.println(previous.peek());
//                        state.moveTo(previous.pop());
//                        amountTravelled--;
//                    }
//                    previous.add(state.getCurrentLocation());
//                    addNeighbours(neighbours, previous, known, amountTravelled);
//                    try {
//                        state.moveTo(next);
//                    }catch(IllegalArgumentException iae){
//                        state.moveTo(state.getNeighbours().stream().findFirst().get().getId());
//                    }
//                }
//            }catch(PriorityQueueException pqe){
//                //TODO
//                System.out.println(pqe.getMessage());
//                final long finalNext = next;
//                while(!state.getNeighbours().stream().anyMatch(n -> n.getId() == finalNext)){
//                    state.moveTo(previous.pop());
//                }
//                state.moveTo(next);
//            }
//        }

    }

    class TreeNode{
        private long id;
        private int distance;
        private TreeNode left;
        private TreeNode mid;
        private TreeNode right;
        private boolean wasVisited;

        public TreeNode(NodeStatus node){
            this.id = node.getId();
            this.distance = node.getDistanceToTarget();
            left = null;
            mid = null;
            right = null;
            wasVisited = false;
        }

        public void visit(Set<NodeStatus> neighbourNodes){
            wasVisited = true;
            NodeStatus[] neighbours = neighbourNodes.toArray(new NodeStatus[3]);

            for(int i = 0; i < neighbours.length; i++){
                if(left == null){
                    left = new TreeNode(neighbours[i]);
                }else if(mid == null){
                    mid = new TreeNode(neighbours[i]);
                }else{
                    right = new TreeNode(neighbours[i]);
                }
            }
        }

        public Node getNode(){
            return node;
        }

        public TreeNode getLeft(){
            return left;
        }

        public TreeNode getForward(){
            return mid;
        }

        public TreeNode getRight(){
            return right;
        }


    }

    private long getNext(PriorityQueue<Long> known, Stack<Long> previous){
        try{
            return known.poll();
        }catch(PriorityQueueException pqe){
            return -1;
        }
    }

    private void addNeighbours(Collection<NodeStatus> neighbours, Stack<Long> previous, PriorityQueue<Long> known, int amountTravelled){
        for (NodeStatus n : neighbours) {
            System.out.println(n.getId() + " : " + n.getDistanceToTarget() + " travelled: " + amountTravelled);
            try {
                if (!previous.contains(n.getId())) known.add(n.getId(), (n.getDistanceToTarget() + amountTravelled));
            }catch(IllegalArgumentException iae){
                //do nothing
            }
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
