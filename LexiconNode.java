import structure5.*;
import java.util.Iterator;

/*
 * 
 */
class LexiconNode implements Comparable<LexiconNode> {

	/* single letter stored in this node */
	protected char letter;

	/* true if this node ends some path that defines a valid word */
	protected boolean isWord;

	/* stores the children of the node*/
	protected Vector<LexiconNode> children;

	/* Vector size, should play with this... are most children lists empty? */
	private static final int DEFAULT_CHILDREN_SIZE = 1;

	/**
	 * Constructor
	 */
	LexiconNode(char letter, boolean isWord) {
		this.letter = letter;
		this.isWord = isWord;
		children = new Vector<LexiconNode>(DEFAULT_CHILDREN_SIZE);
	}

	/**
	 * Compare this LexiconNode to another.
	 *
	 * (You should just compare the characters stored at the
	 * Lexicon Nodes.)
	 */
	public int compareTo(LexiconNode o) {
		return this.letter - o.getLetter();
	}

	/**
	 * Add LexiconNode child to correct position in child
	 * data structure
	 */
	public void addChild(LexiconNode ln) {

		// Try to avoid calling char location... does this speed things up?
		// if (children.size() == 0) {
		// 	children.add(ln);
		// 	return;
		// }

		int index = charLocation(ln.getLetter());
		if (index == children.size() || ln.getLetter() != children.get(index).getLetter()) {
			children.add(index, ln);
		}
	}

	/**
	 * Get LexiconNode child for 'ch' out of child data
	 * structure
	 */
	public LexiconNode getChild(char ch) {
		int index = charLocation(ch);
		if (index < children.size() && ch == (children.get(index).getLetter())) {
			return children.get(index);
		} else {
			return null;
		}
	}

	/**
	 * Remove LexiconNode child for 'ch' from child data structure
	 */
	public void removeChild(char ch) {
		int index = charLocation(ch);
		if (index < children.size() && ch == (children.get(index).getLetter())) {
			children.remove(index);
		}
	}

	private int charLocation(char ch) {
		int max = children.size();
		int min = 0;
		int mid = max/2;

		while (min < max) {
			if (ch - children.get(mid).getLetter() > 0) {
				min = mid + 1;
			} else {
				max = mid;
			}
			mid = (min + max)/2;
		}
		return min;
	}

	public boolean isWord() {
		return isWord;
	}

	public void setWord(boolean isWord) {
		this.isWord = isWord;
	}

	public boolean hasChildren() {
		return children.size() != 0;
	}

	public Vector<LexiconNode> getChildren() {
		return children;
	}

	/**
	 * Create an Iterator that iterates over children in
	 * alphabetical order.
	 *
	 *(Hint: Depending on your data structure choice, and whether
	 * you keep your nodes in alphabetical order, you may be able
	 * to simply return it's iterator... don't reinvent the wheel!)
	 */
	public Iterator<LexiconNode> iterator() {
		return children.iterator();
	}

	public char getLetter() {
		return letter;
	}

	public String toString() {
		return letter + ", " + children;
	}

}
