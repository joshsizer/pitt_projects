package cs445.a5;

import cs445.StackAndQueuePackage.LinkedStack;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * An implementation of a ternary tree. A ternary tree is similar to a binary
 * tree, except that any root has up to three children, instead of two. In
 * essense, a ternary tree is either empty, or a tree in the form of a root with
 * three child trees, which can also be empty. 
 *
 * A tree is built by specifying a root node's data, and providing up to three
 * sub trees, each of which are ternary trees. Any of the three can be null,
 * which indicates an empty child. There is no "add" method because there is no
 * obvious location to add a new element to the data structure. So, the user
 * must explicitly specify the structure. 
 *
 *
 * @author Joshua Sizer
 * @since 12.3.18
 */
public class TernaryTree<E> implements TernaryTreeInterface<E>, TreeIteratorInterface<E> {

  /**
   * The root node, which has references to the left, middle, and right nodes
   */
  TernaryNode<E> root;

  /**
   * Creates an empty ternary tree
   */
  public TernaryTree() {
    root = null;

  }

  /**
   * Creates a ternary tree with one node - the root
   * 
   * @param data The data for the root node. Can be null.
   */
  public TernaryTree(E data) {
    root = new TernaryNode<>(data);
  }

  /**
   * Creates a ternary tree with a root node with E data, and three children of
   * ternary trees.
   *
   * @param data       The data for the root node
   * @param leftTree   The left tree to add
   * @param middleTree The middle tree to add
   * @param rightTree  The right tree to add
   */
  public TernaryTree(E data, TernaryTree<E> leftTree, TernaryTree<E> middleTree,
      TernaryTree<E> rightTree) {
    initTree(data, leftTree, middleTree, rightTree);
  }

  /**
   * Sets this tree to a new tree with one node with the input data
   *
   * @param rootData The data for the new tree's root node
   */
  @Override
  public void setTree(E rootData) {
    root = new TernaryNode<>(rootData);
  }

  /**
   * Sets this tree to a new a ternary tree with a root node with E data, and
   * three children of ternary trees.
   *
   * @param rootData   The data for the root node
   * @param leftTree   The left tree to add
   * @param middleTree The middle tree to add
   * @param rightTree  The right tree to add
   */
  @Override
  public void setTree(E rootData, TernaryTreeInterface<E> leftTree,
      TernaryTreeInterface<E> middleTree, TernaryTreeInterface<E> rightTree) {
    initTree(rootData, (TernaryTree<E>) leftTree, (TernaryTree<E>) middleTree,
        (TernaryTree<E>) rightTree);
  }

  /**
   * Private method that deals with the nitty-gritty of setting this tree's
   * children with new trees. It handles the case where two or three trees input
   * are the same trees, as well as the case where any of the three input trees are the same object as this.
   *
   *
   * @param data The data for the tree's root
   * @param leftTree The left child
   * @param middleTree The middle child
   * @param rightTree The right child
   */
  private void initTree(E data, TernaryTree<E> leftTree, TernaryTree<E> middleTree,
      TernaryTree<E> rightTree) {
    /**
     * We have to create a new node here for the case where one of the input
     * trees is this. If we simply set node = new TernaryNode..., and one of the
     * input trees is this, then we lose all of the subchildren of one of the
     * input trees! 
     **/     
    TernaryNode<E> newRoot = new TernaryNode<>(data);

    // if it's empty, then why add it?
    if (leftTree != null && !leftTree.isEmpty()) {
      newRoot.setLeftChild(leftTree.root);
    }

    if (middleTree != null && !middleTree.isEmpty()) {
      // if the middle tree is the same as the left tree, then we should create
      // a copy
      if (middleTree == leftTree) {
        newRoot.setMiddleChild(leftTree.root.copy());
      } else {
        newRoot.setMiddleChild(middleTree.root);
      }
    }

    if (rightTree != null && !rightTree.isEmpty()) {
      // same idea here, except that we need to check the left and the middle
      if (rightTree == leftTree) {
        newRoot.setRightChild(leftTree.root.copy());
      } else if (rightTree == middleTree) {
        newRoot.setRightChild(middleTree.root.copy());
      } else {
        newRoot.setRightChild(rightTree.root);
      }
    }

    // okay, now we can set this tree's root to the newRoot
    root = newRoot;

    /**
     * For the next three if-statements, we only clear the tree if the input
     * tree is not the same as this, otherwise we'd be clearing this!
     */
    if (leftTree != null && leftTree != this) {
      leftTree.clear();
    }

    if (middleTree != null && middleTree != this) {
      middleTree.clear();
    }

    if (rightTree != null && rightTree != this) {
      rightTree.clear();
    }
  }

  /**
   * Returns this tree's root data
   * 
   * @return E this tree's root's data
   */
  @Override
  public E getRootData() throws EmptyTreeException {
    if (isEmpty()) {
      throw new EmptyTreeException();
    } else {
      return root.getData();
    }
  }

  /**
   * Returns the height of the tree. The height of the tree is the maximum
   * distance from any leaf to the root.
   *
   * @return int the height of the tree
   */
  @Override
  public int getHeight() {
    int height = 0;

    if (!isEmpty()) {
      height = root.getHeight();
    }

    return height;
  }

  /**
   * Returns the number of nodes in this tree.
   * 
   * @return E the number of nodes in the tree
   */
  @Override
  public int getNumberOfNodes() {
    int numNodes = 0;

    if (!isEmpty()) {
      numNodes = root.getNumberOfNodes();
    }

    return numNodes;
  }

  /**
   * Returns true if the root node is null.
   * 
   * @return true if the tree is empty, false if it has one or more nodes
   */
  @Override
  public boolean isEmpty() {
    return root == null;
  }

  /**
   * Sets the root to null
   */
  @Override
  public void clear() {
    root = null;
  }

  /**
   * Returns a preoder iterator. 
   * Root, left, middle, right
   */
  @Override
  public Iterator<E> getPreorderIterator() {
    return new PreorderIterator();
  }

  /**
   * Returns a postorder iterator Left, middle, right, root
   */
  @Override
  public Iterator<E> getPostorderIterator() {
    return null;
  }

  /**
   * Returns an inorder iterator
   * Left, Root, middle, right
   */
  @Override
  public Iterator<E> getInorderIterator() {
    return null;
  }

  /**
   * Returns a level order, or bredth first iterator
   */
  @Override
  public Iterator<E> getLevelOrderIterator() {
    return null;
  }

  private class PreorderIterator implements Iterator<E> {
    LinkedStack<TernaryNode<E>> stack;
    TernaryNode<E> next;
    TernaryNode<E> current;

    private PreorderIterator() {
      stack = new LinkedStack<>();
      next = root;
      current = null;
    }

    @Override
    public boolean hasNext() {
      return next != null;
    }

    @Override
    public E next() {
      current = next;

      if (current == null) {
        throw new NoSuchElementException();
      }

      if (current.hasLeftChild()) {
        next = current.getLeftChild();
        stack.push(current);
      } else if (current.hasMiddleChild()) {
        next = current.getMiddleChild();
        stack.push(current);
      } else if (current.hasRightChild()) {
        next = current.getRightChild();
        stack.push(current);
      } else {
        boolean foundNext = false;
        TernaryNode<E> compare = current;
        TernaryNode<E> parent;
        while (!stack.isEmpty() && !foundNext) {
          parent = stack.peek();
          if (compare == parent.getLeftChild()) {
            if (parent.hasMiddleChild()) {
              next = parent.getMiddleChild();
              foundNext = true;
            } else if (parent.hasRightChild()) {
              next = parent.getRightChild();
              foundNext = true;
            }
          } else if (compare == parent.getMiddleChild()) {
            if (parent.hasRightChild()) {
              next = parent.getRightChild();
              foundNext = true;
            }
          }  
          if (!foundNext) {
              compare = stack.pop();
          }
        }
        if (stack.isEmpty()) {
            next = null;
        }
      }

      return current.getData();
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

  public static void main(String[] args) {
    TernaryTree<String> dTree = new TernaryTree<>("D");

    TernaryTree<String> fTree = new TernaryTree<>("F");
    TernaryTree<String> gTree = new TernaryTree<>("G");
    TernaryTree<String> hTree = new TernaryTree<>("H");
    TernaryTree<String> cTree = new TernaryTree<>("C", fTree, gTree, hTree);

    TernaryTree<String> iTree = new TernaryTree<>("I");
    TernaryTree<String> jTree = new TernaryTree<>("J");
    TernaryTree<String> kTree = new TernaryTree<>("K");
    TernaryTree<String> eTree = new TernaryTree<>("E", iTree, jTree, kTree);
    TernaryTree<String> bTree = new TernaryTree<>("B", cTree, eTree, null);

    TernaryTree<String> aTree = new TernaryTree<>("A", bTree, cTree, dTree);


    System.out.println("My Tree's number of nodes: " + aTree.getNumberOfNodes());
    System.out.println("My Tree's height: " + aTree.getHeight());

    Iterator<String> preOrder = aTree.getPreorderIterator();

    while (preOrder.hasNext()) {
      System.out.println(preOrder.next());
    }

    TernaryTree<String> mTree = new TernaryTree<>("Hello!");

    Iterator<String> mTreeIter = mTree.getPreorderIterator();

    while (mTreeIter.hasNext()) {
      System.out.println(mTreeIter.next());
    }
  }
}
