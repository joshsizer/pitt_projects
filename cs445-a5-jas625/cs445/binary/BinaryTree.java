package cs445.binary;

import java.util.Iterator;
import java.util.NoSuchElementException;

import cs445.StackAndQueuePackage.*; // Needed by tree iterators

/**
 * A class that implements the ADT binary tree.
 * @author Frank M. Carrano
 * @author Timothy M. Henry
 * @author William C. Garrison
 * @version 4.5
 */
public class BinaryTree<E> implements BinaryTreeInterface<E> {
    private BinaryNode<E> root;

    public BinaryTree() {
        root = null;
    }

    public BinaryTree(E rootData) {
        root = new BinaryNode<>(rootData);
    }

    public BinaryTree(E rootData, BinaryTree<E> leftTree,
                      BinaryTree<E> rightTree) {
        privateSetTree(rootData, leftTree, rightTree);
    }

    public void setTree(E rootData) {
        root = new BinaryNode<>(rootData);
    }

    public void setTree(E rootData, BinaryTreeInterface<E> leftTree,
                        BinaryTreeInterface<E> rightTree) {
        privateSetTree(rootData, (BinaryTree<E>)leftTree,
                       (BinaryTree<E>)rightTree);
    }

    private void privateSetTree(E rootData, BinaryTree<E> leftTree,
                                BinaryTree<E> rightTree) {
        BinaryNode<E> newRoot = new BinaryNode<>(rootData);

        if ((leftTree != null) && !leftTree.isEmpty()) {
            root.setLeftChild(leftTree.root);
        }

        if ((rightTree != null) && !rightTree.isEmpty()) {
            if (rightTree != leftTree) {
                root.setRightChild(rightTree.root);
            } else {
                root.setRightChild(rightTree.root.copy());
            }
        }

        root = newRoot;

        if ((leftTree != null) && (leftTree != this)) {
            leftTree.clear();
        }

        if ((rightTree != null) && (rightTree != this)) {
            rightTree.clear();
        }
    }

    public E getRootData() {
        if (isEmpty()) {
            throw new EmptyTreeException();
        } else {
            return root.getData();
        }
    }

    public boolean isEmpty() {
        return root == null;
    }

    public void clear() {
        root = null;
    }

    protected void setRootData(E rootData) {
        root.setData(rootData);
    }

    protected void setRootNode(BinaryNode<E> rootNode) {
        root = rootNode;
    }

    protected BinaryNode<E> getRootNode() {
        return root;
    }

    public int getHeight() {
        int height = 0;
        if (!isEmpty()) {
            height = root.getHeight();
        }
        return height;
    }

    public int getNumberOfNodes() {
        int numberOfNodes = 0;
        if (!isEmpty()) {
            numberOfNodes = root.getNumberOfNodes();
        }
        return numberOfNodes;
    }

    public Iterator<E> getPreorderIterator() {
        return new PreorderIterator();
    }

    public Iterator<E> getInorderIterator() {
        return new InorderIterator();
    }

    public Iterator<E> getPostorderIterator() {
        return new PostorderIterator();
    }

    public Iterator<E> getLevelOrderIterator() {
        return new LevelOrderIterator();
    }

    private class PreorderIterator implements Iterator<E> {
        private StackInterface<BinaryNode<E>> nodeStack;

        public PreorderIterator() {
            nodeStack = new LinkedStack<>();
            if (root != null) {
                nodeStack.push(root);
            }
        }

        public boolean hasNext() {
            return !nodeStack.isEmpty();
        }

        public E next() {
            BinaryNode<E> nextNode;
            
            if (hasNext()) {
                nextNode = nodeStack.pop();
                BinaryNode<E> leftChild = nextNode.getLeftChild();
                BinaryNode<E> rightChild = nextNode.getRightChild();

                // Push into stack in reverse order of recursive calls
                if (rightChild != null) {
                    nodeStack.push(rightChild);
                }

                if (leftChild != null) {
                    nodeStack.push(leftChild);
                }
            } else {
                throw new NoSuchElementException();
            }

            return nextNode.getData();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private class InorderIterator implements Iterator<E> {
        private StackInterface<BinaryNode<E>> nodeStack;
        private BinaryNode<E> currentNode;

        public InorderIterator() {
            nodeStack = new LinkedStack<>();
            currentNode = root;
        }

        public boolean hasNext() {
            return !nodeStack.isEmpty() || (currentNode != null);
        }

        public E next() {
            BinaryNode<E> nextNode = null;

            // Find leftmost node with no left child
            while (currentNode != null) {
                nodeStack.push(currentNode);
                currentNode = currentNode.getLeftChild();
            }

            // Get leftmost node, then move to its right subtree
            if (!nodeStack.isEmpty()) {
                nextNode = nodeStack.pop();
                assert nextNode != null; // Since nodeStack was not empty
                // before the pop
                currentNode = nextNode.getRightChild();
            } else {
                throw new NoSuchElementException();
            }

            return nextNode.getData();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private class PostorderIterator implements Iterator<E> {
        private StackInterface<BinaryNode<E>> nodeStack;
        private BinaryNode<E> currentNode;

        public PostorderIterator() {
            nodeStack = new LinkedStack<>();
            currentNode = root;
        }

        public boolean hasNext() {
            return !nodeStack.isEmpty() || (currentNode != null);
        }
        public E next() {
            boolean foundNext = false;
            BinaryNode<E> leftChild, rightChild, nextNode = null;

            // Find leftmost leaf
            while (currentNode != null) {
                nodeStack.push(currentNode);
                leftChild = currentNode.getLeftChild();
                if (leftChild == null) {
                    currentNode = currentNode.getRightChild();
                } else {
                    currentNode = leftChild;
                }
            }

            // Stack is not empty either because we just pushed a node, or
            // it wasn'E empty to begin with since hasNext() is true.
            // But Iterator specifies an exception for next() in case
            // hasNext() is false.

            if (!nodeStack.isEmpty()) {
                nextNode = nodeStack.pop();
                // nextNode != null since stack was not empty before pop

                BinaryNode<E> parent = null;
                if (!nodeStack.isEmpty()) {
                    parent = nodeStack.peek();
                    if (nextNode == parent.getLeftChild()) {
                        currentNode = parent.getRightChild();
                    } else {
                        currentNode = null;
                    }
                } else {
                    currentNode = null;
                }
            } else {
                throw new NoSuchElementException();
            }

            return nextNode.getData();
        }


        public void remove() {
            throw new UnsupportedOperationException();
        }
    }

    private class LevelOrderIterator implements Iterator<E> {
        private QueueInterface<BinaryNode<E>> nodeQueue;

        public LevelOrderIterator() {
            nodeQueue = new LinkedQueue<>();
            if (root != null) {
                nodeQueue.enqueue(root);
            }
        }

        public boolean hasNext() {
            return !nodeQueue.isEmpty();
        }

        public E next() {
            BinaryNode<E> nextNode;

            if (hasNext()) {
                nextNode = nodeQueue.dequeue();
                BinaryNode<E> leftChild = nextNode.getLeftChild();
                BinaryNode<E> rightChild = nextNode.getRightChild();

                // Add to queue in order of recursive calls
                if (leftChild != null) {
                    nodeQueue.enqueue(leftChild);
                }

                if (rightChild != null) {
                    nodeQueue.enqueue(rightChild);
                }
            } else {
                throw new NoSuchElementException();
            }

            return nextNode.getData();
        }

        public void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

