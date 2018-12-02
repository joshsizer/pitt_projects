package cs445.a5;

import java.util.Iterator;

public class TernaryTree<E> implements TernaryTreeInterface<E>,
        TreeIteratorInterface<E> {
    TernaryNode<E> root;

    /**
     * Creates an empty ternary tree
     */
    public TernaryTree() {
        root = null;
    }

    /**
     * Creates a ternary tree with one node - the root
     * @param data The data for the root node. Can be null.
     */
    public TernaryTree(E data) {
        root = new TernaryNode<>(data);
    }

    /**
     * Creates a ternary tree with a root node with E data, and three
     * children of ternary trees.
     *
     * @param data The data for the root node
     * @param leftTree The left tree to add
     * @param middleTree The middle tree to add
     * @param rightTree The right tree to add
     */
    public TernaryTree(E data,
                       TernaryTree<E> leftTree,
                       TernaryTree<E> middleTree,
                       TernaryTree<E> rightTree) {
        initTree(data, leftTree, middleTree, rightTree);
    }

    /**
     * Sets this tree to a new tree with one node with the input data
     *
     * @param rootData  The data for the new tree's root node
     */
    @Override
    public void setTree(E rootData) {
        root = new TernaryNode<>(rootData);
    }

    /**
     * Sets this tree to a new a ternary tree with a root node with E data,
     * and three children of ternary trees.
     *
     * @param rootData The data for the root node
     * @param leftTree The left tree to add
     * @param middleTree The middle tree to add
     * @param rightTree The right tree to add
     */
    @Override
    public void setTree(E rootData,
                        TernaryTreeInterface<E> leftTree,
                        TernaryTreeInterface<E> middleTree,
                        TernaryTreeInterface<E> rightTree) {
        initTree(rootData,
                (TernaryTree<E>) leftTree,
                (TernaryTree<E>) middleTree,
                (TernaryTree<E>) rightTree);
    }

    /**
     * Private method that deals with the nitty-gritty of setting this tree's
     * children with new trees. It 
     *
     *
     * @param data
     * @param leftTree
     * @param middleTree
     * @param rightTree
     */
    private void initTree(E data,
                          TernaryTree<E> leftTree,
                          TernaryTree<E> middleTree,
                          TernaryTree<E> rightTree) {

    }

    @Override
    public E getRootData() throws EmptyTreeException {
        if (isEmpty()){
            throw new EmptyTreeException();
        }  else {
            return root.getData();
        }
    }

    @Override
    public int getHeight() {
        int height = 0;

        if (!isEmpty()) {
            height = root.getHeight();
        }

        return height;
    }

    @Override
    public int getNumberOfNodes() {
        int numNodes = 0;

        if (!isEmpty()) { 
            numNodes = root.getNumberOfNodes();
        } 
        
        return numNodes;
    }

    @Override
    public boolean isEmpty() {
        return root == null;
    }

    @Override
    public void clear() {
        root = null;
    }

    @Override
    public Iterator<E> getPreorderIterator() {
        return null;
    }

    @Override
    public Iterator<E> getPostorderIterator() {
        return null;
    }

    @Override
    public Iterator<E> getInorderIterator() {
        return null;
    }

    @Override
    public Iterator<E> getLevelOrderIterator() {
        return null;
    }


}
