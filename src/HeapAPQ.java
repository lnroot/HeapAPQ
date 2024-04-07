/*
 * @author Lela Root 10/17/2023 HeapAPQ
 *  An adaptable priority queue built with a minimum heap using an arraylist
 */


import java.util.Comparator;
import net.datastructures.*;

public class HeapAPQ<K,V> implements AdaptablePriorityQueue<K,V> {
	
	public ArrayList<Entry<K,V>> heap;
	
	private Comparator<K> comp;
	
	public static class DefaultComparator<K> implements Comparator<K> {
		
		// This compare method simply calls the compareTo method of the argument. 
		// If the argument is not a Comparable object, and therefore there is no compareTo method,
		// it will throw ClassCastException. 
		public int compare(K a, K b) throws IllegalArgumentException {
			if (a instanceof Comparable ) {
			   return ((Comparable <K>) a).compareTo(b);
			} else {
				throw new  IllegalArgumentException();
			}
		}
	}
	
	private static class apqEntry<K,V> implements Entry<K,V> {

		private int index;
		private K k;
		private V v;
		
		public apqEntry(K key, V value, int j) {
			k = key;
			v = value;
			index = j;
		}
		
		@Override
		public K getKey() {
			return k;
		}

		@Override
		public V getValue() {
			return v;
		}
		
		public int getIndex() {
			return index;
		}
		
		public void setIndex(int i) {
			index = i;
		}
		
		public void setKey(K key) {
			k = key;
		}
		
		public void setValue(V val) {
			v = val;
		}
		
		//returns index of parent node of the entry at input index
		public int parent(int index) {
			return (index-1) / 2;
		}
		
		//returns index of right node of the entry at input index
		public int right(int index) {
			return index * 2 + 2;
		}
	
		//returns index of left node of the entry at input index
		public int left(int index) {
			return index * 2 + 1;
		}
		
	}
	
	/* If no comparator is provided, use the default comparator. 
	 * See the inner class DefaultComparator above. 
	 * If no initial capacity is specified, use the default initial capacity.
	 */
	public HeapAPQ() {
		heap = new ArrayList<>();
		comp = new DefaultComparator<K> ();
	}
	
	/* Start the PQ with specified initial capacity */
	public HeapAPQ(int capacity) {
		heap = new ArrayList<>(capacity);
		comp = new DefaultComparator<K> ();
	}
	
	
	/* Use specified comparator */
	public HeapAPQ(Comparator<K> c) {
		comp = c;
		heap = new ArrayList<>(); 
	}
	
	/* Use specified comparator and the specified initial capacity */
	public HeapAPQ(Comparator<K> c, int capacity) {
		comp = c;
		heap = new ArrayList<>(capacity); 
	}
	
	/*
	 * Helper method that swaps two indexes
	 * Input: the integer indexes to be swapped
	 */
	private void swap(int i, int j) {
		
		Entry<K,V> temp = heap.get(i);
		heap.set(i, heap.get(j));
		heap.set(j, temp);
		
		((apqEntry<K,V>)heap.get(i)).setIndex(i);
		((apqEntry<K,V>)heap.get(j)).setIndex(j);
	}
	
	//Method that returns the size of the heap
	@Override
	public int size() {
		return heap.size();
	}

	//Method that returns whether the heap is empty
	@Override
	public boolean isEmpty() {
		return heap.isEmpty();
	}

	/*
	 * Helper method that swaps up the heap to correct the heap order after changes have been made
	 * Input: the integer index that has been changed
	 */
	private void upHeap(int index) {
		if (index == 0) {return;}//base case: reached the root
		int parent = ((apqEntry<K,V>)heap.get(index)).parent(index);		
		
		K thisKey = ((apqEntry<K,V>)heap.get(index)).getKey();
		K parentKey = ((apqEntry<K,V>)heap.get(parent)).getKey();
		
		if (comp.compare(thisKey, parentKey) > 0) { //base case: parent is smaller
			return;
		}
		
		swap(index, parent);
		upHeap(parent);
		
	}
	
	/*
	 * Helper method that swaps down the heap to correct the heap order after changes have been made
	 * Input: the integer index that has been changed
	 */
	private void downHeap(int index) {
		
		int s = index;
		int l = Integer.MAX_VALUE; 
		int r = Integer.MAX_VALUE;
		
		if (hasLeft(heap.get(index))) {
			l = ((apqEntry<K,V>)heap.get(index)).left(index);
		}
		
		if (hasRight(heap.get(index))) {
			r = ((apqEntry<K,V>)heap.get(index)).right(index);
		}
		
		//find the smallest key
		if (hasLeft(heap.get(index)) && (comp.compare(heap.get(l).getKey(), heap.get(s).getKey()) < 0)) {
			
			s = l;
		}
		
		if (hasRight(heap.get(index)) && (comp.compare(heap.get(r).getKey(), heap.get(s).getKey()) < 0)) {
			
			s = r;
		}
		
		//one of the children had a smaller key
		if (s != index) {
			swap(index, s);
			downHeap(s);
		}
		
		return; //base case: the key at the index was the smallest, or it had no children
	}
	
	/*
	 * Method to insert a new entry to the heap. The new entry added to the bottom of the heap, which can require correction of the heap order
	 * Input: The key and value of the new entry
	 * Output: The new entry
	 */
	@Override
	public Entry<K, V> insert(K key, V value) throws IllegalArgumentException {
		/* TCJ
		 * The worst case of insert would be the case where a new min is inserted (at the bottom, as always), 
		 * and the arraylist IS at capacity.
		 * This means that the arraylist would have to be copied over to a new arraylist
		 * of double capacity, which would take O(n) time.
		 * 
		 * The worst amortized case of insert would be if a new min was inserted (at the bottom, as always), 
		 * and the arraylist IS NOT at capacity.
		 * The new entry would have to be swapped all the way up the tree,
		 * which would be log n swaps.
		 */
		
		apqEntry<K,V> nEntry = new apqEntry<>(key, value, heap.size());
		heap.addLast(nEntry);
		upHeap(heap.size()-1);
		return nEntry;
	}

	/*
	 * Method that returns the minimum entry, aka the root, without removing it from the heap
	 * Output: the entry currently in the root node
	 */
	@Override
	public Entry<K, V> min() {
		/* TCJ
		 * Because the heap is location aware, we can access at index 0 (the min).
		 * Therefore, there is no looping or recursion, giving a time complexity of O(1)    
		 */
		if (heap.size() >= 1) {
			return heap.get(0);
		}
		
		return null;
	}

	/*
	 * Method to remove the node from the heap that has the smallest key, aka the root node
	 * Output: the entry that was removed
	 */
	@Override
	public Entry<K, V> removeMin() {
		/* TCJ
		 * The worst case of removing min is the case every time, because the largest key got swapped into root.
		 * It will have to be swapped all the way down the tree.
		 * This would be log n swaps.    
		 */
		
		if (heap.size() == 0) {
			return null;
		}
		
		Entry<K, V> e = heap.get(0);

		if (heap.size() == 1) {
			heap.remove(0);
			return e;
		}
		
		swap(0, heap.size()-1);
		heap.removeLast();
		downHeap(0);
		return e;
	}

	/*
	 * Method to remove a node in the heap, from anywhere, which can require correction of the heap order.
	 * Input: The entry to remove
	 */
	@Override
	public void remove(Entry<K, V> entry) throws IllegalArgumentException {
		/* TCJ
		 * The worst case of removing would be if the key being removed was the min, meaning that the largest key gets swapped into root.
		 * It will have to be swapped all the way down the tree.
		 * This would be log n swaps.    
		 */
		
		if (heap.size() == 0) {
			return;
		}
		
		if (heap.size() == 1) {
			heap.remove(((apqEntry<K,V>)entry).getIndex());
			return;
		}
		
		int index = ((apqEntry<K,V>)entry).getIndex();
		
		swap(((apqEntry<K,V>)entry).getIndex(), heap.size()-1);
		heap.removeLast();
		
		downHeap(index);
		
	}

	/*
	 * Method to replace the key stored in a node in the heap, which can require correction of the heap order 
	 * Input: The heap entry to be updated, and the new key value to update it with
	 */
	@Override
	public void replaceKey(Entry<K, V> entry, K key) throws IllegalArgumentException {
		/* TCJ
		 * Worst case of replacing the key would be similar to inserting the new min at the bottom.
		 * The replaced key would have to be swapped all the way up the tree (or down if root's key was replaced 
		 * by the largest key). This would be log n swaps.
		 */
		
		K oldKey = entry.getKey();
		((apqEntry<K,V>)entry).setKey(key);
		
		if (comp.compare(oldKey, key) > 0) {
			upHeap(((apqEntry<K,V>)entry).getIndex());
		
		} else {
			downHeap(((apqEntry<K,V>)entry).getIndex());
		}
	}

	/*
	 * Method to replace the value stored in a node in the heap.
	 * Input: The entry being updated and the value to update it with
	 */
	@Override
	public void replaceValue(Entry<K, V> entry, V value) throws IllegalArgumentException {
		/* TCJ
		 * Because the heap is location aware, we can access the specified entry directly.
		 * Additionally, changing the value of an entry requires no reordering of the heap.
		 * This means that there will be no looping or recursion, giving a time complexity of O(1) 
		 */
		
		((apqEntry<K,V>)entry).setValue(value);
		
	}
	
	/*
	 * Helper method to determine whether a node in the heap has a left child
	 * Input: the entry
	 * Output: a boolean value, being whether the left child exists
	 */
	public boolean hasLeft(Entry <K,V> entry) {
		int index = ((apqEntry <K,V>) entry).getIndex();
		return ((apqEntry <K,V>) entry).left(index) < heap.size();
	}
	
	/*
	 * Helper method to determine whether a node in the heap has a right child
	 * Input: the entry
	 * Output: a boolean value, being whether the right child exists
	 */
	public boolean hasRight(Entry<K,V> entry){
		int index = ((apqEntry <K,V>) entry).getIndex();
		return ((apqEntry <K,V>) entry).right(index) < heap.size();
	}
	


}
