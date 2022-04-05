import structure5.*;
import java.util.Iterator;
import java.util.Scanner;

public class LexiconTrie implements Lexicon {
	
	LexiconNode root;
	private int wordCount;

	/**
	 * The constructor initializes a newly allocated lexicon to
	 * represent an empty word list.
	 */
	public LexiconTrie() {
		root = new LexiconNode(' ', false);
		wordCount = 0;
	}
    
	/**
	 * This member function adds the specified word to this
	 * lexicon.  It returns true if the word was added
	 * (i.e. previously did not appear in this lexicon) and false
	 * otherwise. The word is expected to contain only lowercase
	 * letters.
	 */
	public boolean addWord(String word) {
		return addWordRecursive(word, root);
	}

	private boolean addWordRecursive(String remaining, LexiconNode curNode) {
		
		//base case
		if (remaining.equals("")) {
			if (curNode.isWord()) {
				return false;
			} else {
				curNode.setWord(true);
				wordCount++;
				return true;
			}
		}
		
		char ch = remaining.charAt(0);
		curNode.addChild(new LexiconNode(ch, false));
		return addWordRecursive(remaining.substring(1), curNode.getChild(ch));
	}

	/**
	 * This member function opens the specified file, expecting a
	 * text file containing one word per line, and adds each word
	 * to this lexicon. The value returned is the count of new
	 * words that were added. If the file doesn't exist or was
	 * unable to be opened, the function returns 0.
	 */
	public int addWordsFromFile(String filename) {
		int wordsAdded = 0;

		Scanner input = new Scanner(new FileStream(filename));
		while (input.hasNext()) {
			addWord(input.next().toLowerCase());
			wordsAdded++;
		}

		return wordsAdded;
	}
    
	/**
	 * This member function attempts to remove a specified word
	 * from this lexicon.
	 *
	 * If the word appears in the lexicon, it is removed and true is
	 *    returned.
	 * If the word was not contained in the lexicon, the lexicon is
	 *    unchanged and false is returned.  
	 * 
	 * NOT FINISHED
	 */
	public boolean removeWordOld(String word) {
		Assert.pre(word.length() > 0, "Word must not have a length of 0.");
		LexiconNode endingNode = getEndingNode(word);
		
		if (endingNode != null && endingNode.isWord()) {
			endingNode.setWord(false);
			wordCount--;

			// for loop does x
			for (int i = word.length(); i > 0; i--) {
				LexiconNode nodeToCheck = getEndingNode(word.substring(0, i));
				if (nodeToCheck.hasChildren() || nodeToCheck.isWord()) {
					break;
				} else {
					getEndingNode(word.substring(0, i - 1)).removeChild(word.charAt(i - 1));
				}
			}

			return true;

		} else {
			return false;
		}	

	}

	public boolean removeWord(String word) { 
		return removeRecurse(word, root);
	}

	private boolean removeRecurse(String remaining, LexiconNode lastNode) {
		LexiconNode curNode = lastNode.getChild(remaining.charAt(0));

		// If our progression found a null node then we cannt not remove the word
		if (curNode == null) {
			return false;
		}

		if (remaining.length() > 1) { //If we arent at the last letter keep going
			if (removeRecurse(remaining.substring(1), curNode)) { //do the recursive call
				if (!curNode.hasChildren() && !curNode.isWord() ) {
					lastNode.removeChild(curNode.getLetter()); //remove curNode
				}
				return true;
			}
			return false; // if curNode was null the word wasn't there to begin with
		}

		//remaining.length() == 1, ie base case
		//set word to false
		if (curNode.isWord()) {
			curNode.setWord(false);
			wordCount--;
			if (!curNode.hasChildren()) {
				lastNode.removeChild(curNode.getLetter()); //remove curNode
			}
			return true; //curNode was a word
		}
		
		return false; //curNode was not a word
	}

	private LexiconNode getEndingNode (String seq) {
		return getEndingHelper(root, seq);
	}

	private LexiconNode getEndingHelper (LexiconNode curNode, String remaining) {
		if (remaining.equals("")) {
			return curNode;
		}

		curNode = curNode.getChild(remaining.charAt(0));
		if (curNode == null) {
			return null;
		}		
		return getEndingHelper(curNode, remaining.substring(1));
	}
    
	/**
	 * This member function returns the number of words contained
	 * in this lexicon.
	 */
	public int numWords() {
		return wordCount;
	}
    
	/**
	 * This member function returns true if the specified word
	 * exists in this lexicon, false otherwise.
	 */
	public boolean containsWord(String word){
		LexiconNode node = getEndingNode(word);
		return node != null && node.isWord();
	}
    
	/**
	 * This member function returns true if any words in the
	 * lexicon begin with the specified prefix, false otherwise. A
	 * word is defined to be a prefix of itself and the empty
	 * string is a prefix of everything.
	 */
	public boolean containsPrefix(String prefix){
		LexiconNode node = getEndingNode(prefix);
		return node != null;
	}
    
	/**
	 * This member function returns an iterator over all
	 * words contained in the lexicon. Accessing the
	 * words from the iterator will retrieve them in
	 * alpahbetical order.
	 *
	 * (Hint: You may wish to build up a data structure that
	 * contains the words in your trie, and then simply return
	 * it's iterator... don't reinvent the wheel!)
	 */
	public Iterator<String> iterator() {
		Vector<String> allWords = getWordsFromNode(new Vector<String>(), root, "");
		return allWords.iterator();
	}

	private Vector<String> getWordsFromNode(Vector<String> allWords, LexiconNode curNode, String build) {
		if (curNode != root) {
			build += curNode.getLetter();
		}

		// add this word to wordlist if it's a word
		if (curNode.isWord()) {
			allWords.add(build);
		}

		// if at a leaf node return
		if (!curNode.hasChildren()) {
			return allWords;
		}

		// recurse for all children
		Iterator<LexiconNode> it = curNode.iterator();
		while (it.hasNext()) {
			allWords = getWordsFromNode(allWords, it.next(), build);
		}

		return allWords;
	}

	/**
	 * (OPTIONAL!)
	 *
	 * This member function returns a pointer to a set of strings,
	 * where each entry is a suggested correction for the target.
	 * All words in the lexicon with a distance to the target that
	 * is less than or equal to the parameter distance should be
	 * in the returned set.
	 */
	public SetVector<String> suggestCorrections(String target, int maxDistance) {
		SetVector<String> corrections = correctionsHelper(target, maxDistance, "", maxDistance, root, new SetVector<>());
		return corrections;
	}

	public SetVector<String> correctionsHelper(String target, int maxDistance, String build, int distanceLeft, LexiconNode curNode, SetVector<String> result) {
		if (curNode != root) {
			build += curNode.getLetter();
		}
		
		if (build.length() == target.length()) {
			if (curNode.isWord() && distanceLeft < maxDistance) {
				result.add(build);
			}
			return result;
		}
		char nextChar = target.charAt(build.length());

		if (distanceLeft == 0) {
			curNode = curNode.getChild(nextChar);
			if (curNode != null)
				return correctionsHelper(target, maxDistance, build, distanceLeft, curNode, result);
			return result;
		}

		Iterator<LexiconNode> it = curNode.iterator();
		while (it.hasNext()) {
			LexiconNode nextNode = it.next();
			if (nextNode.getLetter() != nextChar) {
				result = correctionsHelper(target, maxDistance, build, distanceLeft - 1, nextNode, result);
			} else {
				result = correctionsHelper(target, maxDistance, build, distanceLeft, nextNode, result);
			}
		}
		return result;
		
	}
    
	/**
	 * (OPTIONAL!)
	 *
	 * This member function returns a pointer to a set of strings,
	 * where each entry is match for the regular expression
	 * pattern.  All words in the lexicon that match the pattern
	 * should be in the returned set.
	 */
	public SetVector<String> matchRegex(String pattern){
		return matchHelper(pattern, "", false, root, new SetVector<String>());
	}

	public SetVector<String> matchHelper(String patternLeft, String build, boolean addtoBuild, LexiconNode curNode, SetVector<String> result) {
		if (addtoBuild) {
			build += curNode.getLetter();
		}

		if (patternLeft.equals("")) {
			if (curNode.isWord()) {
				result.add(build);
			}
			return result;
		}

		char nextChar = patternLeft.charAt(0);

		if (Character.isLetter(nextChar)) {
			curNode = curNode.getChild(nextChar);
			if (curNode != null)
				return matchHelper(patternLeft.substring(1), build, true, curNode, result);
		}

		if (nextChar == '?') {
			Iterator<LexiconNode> it = curNode.iterator();
			while (it.hasNext()) {
				LexiconNode nextNode = it.next();
				result = matchHelper(patternLeft.substring(1), build, true, nextNode, result);
			}
		}

		if (nextChar == '*') {
			result = matchHelper(patternLeft.substring(1), build, false, curNode, result);

			Iterator<LexiconNode> it = curNode.iterator();
			while (it.hasNext()) {
				LexiconNode nextNode = it.next();
				result = matchHelper(patternLeft.substring(1), build, true, nextNode, result);
				result = matchHelper(patternLeft, build, true, nextNode, result);
			}
		}
		return result;
		
	}

	public static void main(String[] args) {
		LexiconTrie lex = new LexiconTrie();
		lex.addWordsFromFile("ospd2.txt");

		String pattern = "fern";
		System.out.println("Words that match pattern " + pattern );
        System.out.println("-----------------------------------" );
        Set<String> matches = lex.matchRegex(pattern);
		System.out.println(matches + "\n");

		// String pattern = "??ti?ist";
		// System.out.println("Words that match pattern " + pattern );
        // System.out.println("-----------------------------------" );
		// Set<String> matches = lex.matchRegex(pattern);
		// for (String match : matches) {
		// 	if (match.charAt(2) == match.charAt(7) && match.charAt(3) == match.charAt(5)
		// 		&& match.charAt(4) != 'c') {
		// 		System.out.println(match);
		// 	}
		// }

		// pattern = "???";
		// System.out.println("Words that match pattern " + pattern );
        // System.out.println("-----------------------------------" );
		// matches = lex.matchRegex(pattern);
		// for (String match : matches) {
		// 	if (match.charAt(0) == match.charAt(2) && match.charAt(1) != match.charAt(2)) {
		// 		System.out.println(match);
		// 	}
		// }

		// pattern = "i";
		// System.out.println("Words that match pattern " + pattern );
        // System.out.println("-----------------------------------" );
		// matches = lex.matchRegex(pattern);
		// for (String match : matches) {
		// 	// if (match.charAt(0) != match.charAt(3) && match.charAt(0) != match.charAt(4) && match.charAt(0) != match.charAt(5) &&
		// 	// 	match.charAt(1) != match.charAt(3) && match.charAt(1) != match.charAt(4) && match.charAt(1) != match.charAt(5) &&
		// 	// 	match.charAt(2) != match.charAt(3) && match.charAt(2) != match.charAt(4) && match.charAt(2) != match.charAt(5) ) {
		// 		System.out.println(match);
		// 	//}
		// }
	}
}
