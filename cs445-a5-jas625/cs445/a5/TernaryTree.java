package cs445.a5;

import cs445.StackAndQueuePackage.LinkedStack;
import cs445.StackAndQueuePackage.LinkedQueue;
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
public class TernaryTree<E> implements TernaryTreeInterface<E>, TreeIteratorInterface<E>, TernaryTreeBonus<E> {

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
  public TernaryTree(E data, TernaryTree<E> leftTree, TernaryTree<E> middleTree, TernaryTree<E> rightTree) {
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
  public void setTree(E rootData, TernaryTreeInterface<E> leftTree, TernaryTreeInterface<E> middleTree,
      TernaryTreeInterface<E> rightTree) {
    initTree(rootData, (TernaryTree<E>) leftTree, (TernaryTree<E>) middleTree, (TernaryTree<E>) rightTree);
  }

  /**
   * Private method that deals with the nitty-gritty of setting this tree's
   * children with new trees. It handles the case where two or three trees input
   * are the same trees, as well as the case where any of the three input trees
   * are the same object as this.
   *
   *
   * @param data       The data for the tree's root
   * @param leftTree   The left child
   * @param middleTree The middle child
   * @param rightTree  The right child
   */
  private void initTree(E data, TernaryTree<E> leftTree, TernaryTree<E> middleTree, TernaryTree<E> rightTree) {
    /**
     * We have to create a new node here for the case where one of the input trees
     * is this. If we simply set node = new TernaryNode..., and one of the input
     * trees is this, then we lose all of the subchildren of one of the input trees!
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
     * For the next three if-statements, we only clear the tree if the input tree is
     * not the same as this, otherwise we'd be clearing this!
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
   * Determines whether the tree contains the given entry. Equality is determined
   * by using the .equals() method.
   * 
   * @param elem The entry to be searched for
   * @return true if elem is in the tree, false if not
   */
  public boolean contains(E elem) {
    Iterator<E> inorder = getInorderIterator();

    while (inorder.hasNext()) {
      if (inorder.next().equals(elem)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Determines if the tree is "balanced," where a balanced tree is one where, for
   * any node in the tree, the heights of its subtrees differ by no more than one.
   * 
   * @return true if the tree is balanced, false if there is a node whose children
   *         have heights that differ by more than 1.
   */
  public boolean isBalanced() {
    return isBalanced(root);
  }

  private boolean isBalanced(TernaryNode<E> node) {
    if (node == null) {
      return true;
    }

    int leftHeight = 0, middleHeight = 0, rightHeight = 0;

    if (node.hasLeftChild()) {
      leftHeight = node.getLeftChild().getHeight();
    }

    if (node.hasMiddleChild()) {
      middleHeight = node.getMiddleChild().getHeight();
    }

    if (node.hasRightChild()) {
      rightHeight = node.getRightChild().getHeight();
    }

    boolean leftAndMiddle;
    boolean leftAndRight;
    boolean middleAndRight;

    if (node.hasLeftChild() && node.hasMiddleChild()) {
      leftAndMiddle = Math.abs(leftHeight - middleHeight) <= 1;
    } else {
      leftAndMiddle = true;
    }

    if (node.hasLeftChild() && node.hasRightChild()) {
      leftAndRight = Math.abs(leftHeight - rightHeight) <= 1;
    } else {
      leftAndRight = true;
    }

    if (node.hasMiddleChild() && node.hasRightChild()) {
      middleAndRight = Math.abs(middleHeight - rightHeight) <= 1;
    } else {
      middleAndRight = true;
    }

    boolean nodeIsBalanced = leftAndMiddle && leftAndRight && middleAndRight;

    return nodeIsBalanced && isBalanced(node.getLeftChild()) && isBalanced(node.getRightChild())
        && isBalanced(node.getMiddleChild());
  }

  /**
   * Returns a preoder iterator. Root, left, middle, right
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
    return new PostorderIterator();
  }

  /**
   * Returns an inorder iterator Left, Root, middle, right
   */
  @Override
  public Iterator<E> getInorderIterator() {
    return new InorderIterator();
  }

  /**
   * Returns a level order, or bredth first iterator
   */
  @Override
  public Iterator<E> getLevelOrderIterator() {
    return new LevelOrderIterator();
  }

  private class PreorderIterator implements Iterator<E> {
    LinkedStack<TernaryNode<E>> stack;

    private PreorderIterator() {
      stack = new LinkedStack<>();
      if (root != null) {
        stack.push(root);
      }
    }

    @Override
    public boolean hasNext() {
      return !stack.isEmpty();
    }

    @Override
    public E next() {
      TernaryNode<E> next;

      if (hasNext()) {
        next = stack.pop();

        TernaryNode<E> leftChild = next.getLeftChild();
        TernaryNode<E> middleChild = next.getMiddleChild();
        TernaryNode<E> rightChild = next.getRightChild();

        if (rightChild != null) {
          stack.push(rightChild);
        }

        if (middleChild != null) {
          stack.push(middleChild);
        }

        if (leftChild != null) {
          stack.push(leftChild);
        }
      } else {
        throw new NoSuchElementException();
      }

      return next.getData();
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

  private class PostorderIterator implements Iterator<E> {
    LinkedStack<TernaryNode<E>> stack;
    TernaryNode<E> current;

    private PostorderIterator() {
      stack = new LinkedStack<>();
      current = root;
    }

    @Override
    public boolean hasNext() {
      return !stack.isEmpty() || current != null;
    }

    @Override
    public E next() {
      TernaryNode<E> nextNode, leftChild, middleChild, rightChild = null;

      while (current != null) {
        stack.push(current);

        leftChild = current.getLeftChild();
        middleChild = current.getMiddleChild();
        rightChild = current.getRightChild();
        if (leftChild == null && middleChild != null) {
          current = middleChild;
        } else if (leftChild == null && rightChild != null) {
          current = rightChild;
        } else {
          current = leftChild;
        }
      }

      if (!stack.isEmpty()) {
        nextNode = stack.pop();

        TernaryNode<E> parent;
        if (!stack.isEmpty()) {
          parent = stack.peek();
          if (nextNode == parent.getLeftChild()) {
            if (parent.getMiddleChild() != null) {
              current = parent.getMiddleChild();
            } else {
              current = parent.getRightChild();
            }
          } else if (nextNode == parent.getMiddleChild()) {
            current = parent.getRightChild();
          } else {
            current = null;
          }
        } else {
          current = null;
        }
      } else {
        throw new NoSuchElementException();
      }

      return nextNode.getData();
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

  private class InorderIterator implements Iterator<E> {
    private LinkedStack<TernaryNode<E>> stack;
    private TernaryNode<E> currentNode;
    TernaryNode<E> nextNode;
    boolean checkLeft;

    private InorderIterator() {
      stack = new LinkedStack<>();
      nextNode = root;
      checkLeft = true;
    }

    @Override
    public boolean hasNext() {
      return nextNode != null;
    }

    @Override
    public E next() {
      currentNode = nextNode;

      while (checkLeft && currentNode.hasLeftChild()) {
        stack.push(currentNode);
        currentNode = currentNode.getLeftChild();
      }

      if (currentNode.hasRightChild() && currentNode.hasMiddleChild()) {
        stack.push(currentNode.getRightChild());
        nextNode = currentNode.getMiddleChild();
        checkLeft = true;
      } else if (currentNode.hasRightChild()) {
        nextNode = currentNode.getRightChild();
        checkLeft = true;
      } else if (currentNode.hasMiddleChild()) {
        nextNode = currentNode.getMiddleChild();
        checkLeft = true;
      } else {
        if (!stack.isEmpty()) {
          nextNode = stack.pop();
        } else {
          nextNode = null;
        }
        checkLeft = false;
      }

      return currentNode.getData();
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

  private class LevelOrderIterator implements Iterator<E> {
    LinkedQueue<TernaryNode<E>> queue;

    private LevelOrderIterator() {
      queue = new LinkedQueue<>();
      if (root != null) {
        queue.enqueue(root);
      }
    }

    @Override
    public boolean hasNext() {
      return !queue.isEmpty();
    }

    @Override
    public E next() {
      if (queue.isEmpty()) {
        throw new NoSuchElementException();
      }
      TernaryNode<E> current = queue.dequeue();

      if (current.hasLeftChild()) {
        queue.enqueue(current.getLeftChild());
      }
      if (current.hasMiddleChild()) {
        queue.enqueue(current.getMiddleChild());
      }
      if (current.hasRightChild()) {
        queue.enqueue(current.getRightChild());
      }

      return current.getData();
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

  public static void main(String[] args) {
    TernaryTree<?>[] tests = getTestTrees();
    for (TernaryTree<?> tree : tests) {
      printTreeInfo(tree);
    }

    System.out.println();

  }

  static TernaryTree[] getTestTrees() {
    TernaryTree[] result = new TernaryTree[7];
    result[0] = createTestTree1();
    result[1] = createTestTree2();
    result[2] = createTestTree3();
    result[3] = createTestTree4();
    result[4] = createTestTree5();
    result[5] = createTestTree6();
    result[6] = createTestTree7();
    return result;
  }

  static TernaryTree<?> createTestTree1() {
    TernaryTree<String> dTree = new TernaryTree<>("D");
    TernaryTree<String> fTree = new TernaryTree<>("F");
    TernaryTree<String> gTree = new TernaryTree<>("G");
    TernaryTree<String> hTree = new TernaryTree<>("H");
    TernaryTree<String> cTree = new TernaryTree<>("C", fTree, gTree, hTree);
    TernaryTree<String> iTree = new TernaryTree<>("I");
    TernaryTree<String> jTree = new TernaryTree<>("J");
    TernaryTree<String> kTree = new TernaryTree<>("K");
    TernaryTree<String> eTree = new TernaryTree<>("E", iTree, jTree, kTree);
    TernaryTree<String> bTree = new TernaryTree<>("B", null, eTree, null);
    TernaryTree<String> aTree = new TernaryTree<>("A", bTree, cTree, dTree);
    return aTree;
  }

  static TernaryTree<?> createTestTree2() {
    TernaryTree<Integer> five = new TernaryTree<>(Integer.valueOf(5));
    TernaryTree<Integer> four = new TernaryTree<>(Integer.valueOf(4), null, null, five);
    TernaryTree<Integer> three = new TernaryTree<>(Integer.valueOf(3), null, null, four);
    TernaryTree<Integer> two = new TernaryTree<>(Integer.valueOf(2), null, null, three);
    TernaryTree<Integer> one = new TernaryTree<>(Integer.valueOf(1), null, null, two);
    return one;
  }

  static TernaryTree<?> createTestTree3() {
    TernaryTree<Integer> five = new TernaryTree<>();
    five.setTree(Integer.valueOf(5));
    TernaryTree<Integer> four = new TernaryTree<>();
    four.setTree(Integer.valueOf(4), null, null, five);
    TernaryTree<Integer> three = new TernaryTree<>();
    three.setTree(Integer.valueOf(3), null, null, four);
    TernaryTree<Integer> two = new TernaryTree<>();
    two.setTree(Integer.valueOf(2), null, null, three);
    TernaryTree<Integer> one = new TernaryTree<>();
    one.setTree(Integer.valueOf(1), null, null, two);
    return one;
  }

  static TernaryTree<?> createTestTree4() {
    TernaryTree<Integer> ten = new TernaryTree<>(Integer.valueOf(10));
    TernaryTree<Integer> eleven = new TernaryTree<>(Integer.valueOf(11));
    TernaryTree<Integer> eight = new TernaryTree<>(Integer.valueOf(8), ten, null, eleven);
    TernaryTree<Integer> nine = new TernaryTree<>(Integer.valueOf(9));
    TernaryTree<Integer> six = new TernaryTree<>(Integer.valueOf(6), eight, null, nine);
    TernaryTree<Integer> seven = new TernaryTree<>(Integer.valueOf(7));
    TernaryTree<Integer> five = new TernaryTree<>(Integer.valueOf(5), six, null, seven);

    TernaryTree<Integer> four = new TernaryTree<>(Integer.valueOf(4));
    TernaryTree<Integer> two = new TernaryTree<>(Integer.valueOf(2), four, null, five);
    TernaryTree<Integer> three = new TernaryTree<>(Integer.valueOf(3));
    TernaryTree<Integer> one = new TernaryTree<>(Integer.valueOf(1), two, null, three);
    return one;
  }

  static TernaryTree<?> createTestTree5() {
    TernaryTree<Integer> eleven = new TernaryTree<>(Integer.valueOf(11));
    TernaryTree<Integer> twelve = new TernaryTree<>(Integer.valueOf(12));
    TernaryTree<Integer> ten = new TernaryTree<>(Integer.valueOf(10), eleven, twelve, null);
    TernaryTree<Integer> nine = new TernaryTree<>(Integer.valueOf(9));
    TernaryTree<Integer> eight = new TernaryTree<>(Integer.valueOf(8), nine, ten, null);
    TernaryTree<Integer> seven = new TernaryTree<>(Integer.valueOf(7));
    TernaryTree<Integer> six = new TernaryTree<>(Integer.valueOf(6), seven, eight, null);

    TernaryTree<Integer> three = new TernaryTree<>(Integer.valueOf(3), null, six, null);
    TernaryTree<Integer> five = new TernaryTree<>(Integer.valueOf(5), null, null, null);

    TernaryTree<Integer> four = new TernaryTree<>(Integer.valueOf(4));
    TernaryTree<Integer> two = new TernaryTree<>(Integer.valueOf(2), four, five, null);
    TernaryTree<Integer> one = new TernaryTree<>(Integer.valueOf(1), two, three, null);
    return one;
  }

  static TernaryTree<?> createTestTree6() {
    TernaryTree<String> i = new TernaryTree<>("I");
    TernaryTree<String> h = new TernaryTree<>("H");
    TernaryTree<String> e = new TernaryTree<>("E");
    TernaryTree<String> f = new TernaryTree<>("F", i, null, null);
    TernaryTree<String> g = new TernaryTree<>("G");
    TernaryTree<String> d = new TernaryTree<>("D", null, null, h);
    TernaryTree<String> c = new TernaryTree<>("C", null, f, g);
    TernaryTree<String> b = new TernaryTree<>("B", null, d, e);
    TernaryTree<String> a = new TernaryTree<>("A", b, null, c);

    return a;
  }

  static TernaryTree<?> createTestTree7() {
    TernaryTree<String> i = new TernaryTree<>("I");
    i.setTree("I2", i, null, null);
    TernaryTree<String> h = new TernaryTree<>("H");
    TernaryTree<String> e = new TernaryTree<>("E");
    TernaryTree<String> f = new TernaryTree<>("F", i, null, null);
    TernaryTree<String> g = new TernaryTree<>("G");
    TernaryTree<String> d = new TernaryTree<>("D", null, null, h);
    TernaryTree<String> c = new TernaryTree<>("C", null, f, g);
    TernaryTree<String> b = new TernaryTree<>("B", null, d, e);
    TernaryTree<String> a = new TernaryTree<>("A", b, null, c);

    return a;
  }

  static <T> void printTreeInfo(TernaryTree<T> aTree) {
    Iterator<T> aTreePreorderIter = aTree.getPreorderIterator();
    Iterator<T> aTreeInorderIter = aTree.getInorderIterator();
    Iterator<T> aTreePostorderIter = aTree.getPostorderIterator();
    Iterator<T> aTreeLevelOrderIter = aTree.getLevelOrderIterator();

    System.out.println("\nHeight:\t      " + aTree.getHeight());
    System.out.println("Number Nodes: " + aTree.getNumberOfNodes());
    System.out.println("Balanced:     " + aTree.isBalanced());

    StringBuilder iter = new StringBuilder();
    while (aTreePreorderIter.hasNext()) {
      iter.append(aTreePreorderIter.next().toString() + " ");
    }
    System.out.println("Preorder:     " + iter);
    iter.delete(0, iter.length());

    while (aTreeInorderIter.hasNext()) {
      iter.append(aTreeInorderIter.next().toString() + " ");
    }
    System.out.println("Inorder:      " + iter);
    iter.delete(0, iter.length());

    while (aTreePostorderIter.hasNext()) {
      iter.append(aTreePostorderIter.next().toString() + " ");
    }
    System.out.println("Postorder:    " + iter);
    iter.delete(0, iter.length());

    while (aTreeLevelOrderIter.hasNext()) {
      iter.append(aTreeLevelOrderIter.next().toString() + " ");
    }
    System.out.println("Levelorder:   " + iter);
    iter.delete(0, iter.length());
  }
}
