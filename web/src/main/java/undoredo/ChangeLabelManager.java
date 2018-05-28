package undoredo;

import com.google.gson.JsonArray;

/**
 * 
 * Manages a Queue of Changables to perform undo and/or redo operations. Clients
 * can add implementations of the Changeable
 * 
 * class to this class, and it will manage undo/redo as a Queue.
 * 
 * 
 * 
 * @author Greg Cope
 *
 * 
 * 
 */

public class ChangeLabelManager {
 

    // the current index node

    private Node currentIndex = null;

    // the parent node far left node.

    private Node parentNode = new Node();

    /**
     * 
     * Creates a new ChangeManager object which is initially empty.
     * 
     */

    public ChangeLabelManager() {

        currentIndex = parentNode;

    }

    /**
     * 
     * Creates a new ChangeManager which is a duplicate of the parameter in both
     * contents and current index.
     * 
     * @param manager
     * 
     */

    public ChangeLabelManager(ChangeLabelManager manager) {

        this();

        currentIndex = manager.currentIndex;

    }

    /**
     * 
     * Clears all Changables contained in this manager.
     * 
     */

    public void clear() {

        currentIndex = parentNode;

    }

    /**
     * 
     * Adds a Changeable to manage.
     * 
     * @param changeable
     * 
     */

    public void addChangeable(Changeable changeable) {

        Node node = new Node(changeable);

        currentIndex.right = node;

        node.left = currentIndex;

        currentIndex = node;

    }

    /**
     * 
     * Determines if an undo can be performed.
     * 
     * @return
     * 
     */

    public boolean canUndo() {

        return currentIndex.left != parentNode;

    }

    /**
     * 
     * Determines if a redo can be performed.
     * 
     * @return
     * 
     */

    public boolean canRedo() {

        return currentIndex.right != null;

    }

    /**
     * 
     * Undoes the Changeable at the current index.
     * 
     * @throws IllegalStateException
     *             if canUndo returns false.
     * 
     */

    public JsonArray undo() {

        // validate

        if (!canUndo()) {
           // i++;
            throw new IllegalStateException("Cannot undo. Index is out of range.");

        }

        JsonArray changes = (JsonArray)currentIndex.changeable.undo();

        // set index

        moveLeft();
        System.out.println("=======ADDING=====");
        return changes;

    }



    /**
     * 
     * Moves the internal pointer of the backed linked list to the left.
     * 
     * @throws IllegalStateException
     *             If the left index is null.
     * 
     */

    private void moveLeft() {

        if (currentIndex.left == null) {

            throw new IllegalStateException("Internal index set to null.");

        }

        currentIndex = currentIndex.left;

    }

    /**
     * 
     * Moves the internal pointer of the backed linked list to the right.
     * 
     * @throws IllegalStateException
     *             If the right index is null.
     * 
     */

    private void moveRight() {

        if (currentIndex.right == null) {

            throw new IllegalStateException("Internal index set to null.");

        }

        currentIndex = currentIndex.right;

    }

    /**
     * 
     * Redoes the Changable at the current index.
     * 
     * @throws IllegalStateException
     *             if canRedo returns false.
     * 
     */

    public JsonArray redo() {

        // validate

        if (!canRedo()) {

            throw new IllegalStateException("Cannot redo. Index is out of range.");

        }

        // reset index

        moveRight();

        // redo
        System.out.println("=======ADDING=====");
        return (JsonArray) currentIndex.changeable.redo();

    }

    public JsonArray getLables () {
        return (JsonArray) currentIndex.changeable.get();
    }

    private class Node {

        private Node left = null;

        private Node right = null;

        private final Changeable changeable;

        public Node(Changeable c) {

            changeable = c;

        }

        public Node() {

            changeable = null;

        }
    }
}