package cs445.binary;

import java.util.Iterator;

/**
 * An interface of iterators for the ADT tree. Note that these iterators are
 * required to be efficient. That is, they may not traverse the entire tree in
 * advance and cache the order, nor may they restart the iteration at each call
 * to next(). They are not required to support remove().
 * @author Frank M. Carrano
 * @author Timothy M. Henry
 * @author William C. Garrison
 * @version 4.5
 */
public interface TreeIteratorInterface<E> {
   /** Creates an iterator to traverse the tree in preorder fashion
    *  @return  the iterator */
    public Iterator<E> getPreorderIterator();

   /** Creates an iterator to traverse the tree in postorder fashion
    *  @return  the iterator */
    public Iterator<E> getPostorderIterator();

   /** Creates an iterator to traverse the tree in inorder fashion
    *  @return  the iterator */
    public Iterator<E> getInorderIterator();

   /** Creates an iterator to traverse the tree in level-order fashion
    *  @return  the iterator */
    public Iterator<E> getLevelOrderIterator();
}

