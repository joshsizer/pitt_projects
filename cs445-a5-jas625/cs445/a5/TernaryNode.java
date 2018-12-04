package cs445.a5;

class TernaryNode<T> {

  private T data;
  private TernaryNode<T>[] children;

  public TernaryNode(T data) {
    this(data, null, null, null);
  }

  public TernaryNode(T data, TernaryNode<T> leftChild, TernaryNode<T> middleChild,
      TernaryNode<T> rightChild) {
    this.data = data;
    children = (TernaryNode<T>[]) new TernaryNode<?>[3];
    children[0] = leftChild;
    children[1] = middleChild;
    children[2] = rightChild;
  }

  public T getData() {
    return data;
  }

  public void setData(T data) {
    this.data = data;
  }

  public TernaryNode<T> getLeftChild() {
    return children[0];
  }

  public void setLeftChild(TernaryNode<T> leftChild) {
    children[0] = leftChild;
  }

  public boolean hasLeftChild() {
    return children[0] != null;
  }

  public TernaryNode<T> getMiddleChild() {
    return children[1];
  }

  public void setMiddleChild(TernaryNode<T> middleChild) {
    children[1] = middleChild;
  }

  public boolean hasMiddleChild() {
    return children[1] != null;
  }

  public TernaryNode<T> getRightChild() {
    return children[2];
  }

  public void setRightChild(TernaryNode<T> rightChild) {
    children[2] = rightChild;
  }

  public boolean hasRightChild() {
    return children[2] != null;
  }

  public boolean isLeaf() {
    return (children[0] == null) && (children[1] == null) && (children[2] == null);
  }

  public int getNumberOfNodes() {
    int leftNum = 0;
    int middleNum = 0;
    int rightNum = 0;

    if (children[0] != null) {
      leftNum = children[0].getNumberOfNodes();
    }

    if (children[1] != null) {
      middleNum = children[1].getNumberOfNodes();
    }

    if (children[2] != null) {
      rightNum = children[2].getNumberOfNodes();
    }

    return 1 + leftNum + middleNum + rightNum;
  }

  public int getHeight() {
    return getHeight(this);
  }

  private int getHeight(TernaryNode<T> node) {
    int height = 0;

    if (node != null) {
      height = 1 + Math.max(getHeight(node.getLeftChild()),
          Math.max(getHeight(node.getMiddleChild()), getHeight(node.getRightChild())));
    }

    return height;
  }

  public TernaryNode<T> copy() {
    TernaryNode<T> copy = new TernaryNode<T>(this.data);

    if (children[0] != null) {
      copy.setLeftChild(children[0].copy());
    }

    if (children[1] != null) {
      copy.setMiddleChild(children[1].copy());
    }

    if (children[2] != null) {
      copy.setRightChild(children[2].copy());
    }

    return copy;
  }
}
