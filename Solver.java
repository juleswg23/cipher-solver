import structure5.*;
import java.util.Scanner;

/*
 * This program is dependent on structure5, a library written to
 * make data structures more accessible.
 * 
 * It can be accessed at
 * http://www.cs.williams.edu/~bailey/JavaStructures/doc/structure5/structure5/package-summary.html
 * 
 */   


public class Solver
{   
    LexiconTrie lex;
    String puzzleWords;
    char[] soln;

    public Solver(String words) {
        lex = new LexiconTrie();
        lex.addWordsFromFile("nytWords.txt");
        lex.addWordsFromFile("nytProperNouns.txt");

        // make an empty array of possible solution ciphers
        soln = new char[26];
        for (int i = 0; i < 26; i++) {
            soln[i] = '?';
        }

        puzzleWords = setup(words);

        // record all the letters that haven't been used in the cipher
        Vector<Character> unused = new Vector<>();
        for (int i = 0; i < 26; i++){
            unused.add(toLower(i));
        }
        
        // create a vector of possible solutions, and run program by calling the solve method
        Vector<char[]> solns = solve(soln, puzzleWords, unused, new Vector<char[]>());
        if (solns.size() == 0) {
            System.out.println("\nThere were no solutions found.");
        } else if (solns.size() == 1) {
            System.out.println("\nThe solution is:");
        } else {
            System.out.println("\nThe solutions are:");
        }

        for (char[] soln : solns)
            System.out.println(solveWith(soln, words));
    }

    /*
     * The main method in this program, takes as input a current cipher solution,
     * the original string, a vector of unused characters in the cipher, and
     * a vector of the solutions found so far
    */
    
    public Vector<char[]> solve(char[] curSoln, String original, Vector<Character> unused, Vector<char[]> solns) {
        //build soFar, which is the decoded message, to test if we've found words
        String soFar = "";
        int charInt;
        for (int i = 0; i < original.length(); i++) {
            charInt = toInt(original.charAt(i));
            if (charInt == -1 || curSoln[charInt] == '?') {
                soFar += original.charAt(i);
            } else {
                soFar += curSoln[charInt];
            }
        }

        // base case
        if (soFar.toLowerCase().equals(soFar)) {
            if (areWords(soFar)) {
                solns.add(curSoln);
                return solns;
            }
            return solns;
        }

        //check before proceed
        if (!matchPhrase(soFar)){
            return solns;
        }

        // there are still uppercase letters
        // find index of first uppercase
        int finger = 0;
        while (finger < soFar.length()) {
            if (Character.isUpperCase(soFar.charAt(finger)))
                break;
            finger++;   
        }

        char toReplace = soFar.charAt(finger);
        System.out.println(soFar);
        Vector<char[]> newSolns;

        // recursive step, call solve for a letter at each position in the cipher
        for (int i = 0; i < unused.size(); i++) {
            // duplicate the current solution so we don't edit this memory in the
            // recursive call
            char[] newSoln = new char[curSoln.length];
            System.arraycopy(curSoln, 0, newSoln, 0, 26); 
           
            // put the new letter in the cipher solutions, and remove it from unused
            newSoln[toInt(toReplace)] = unused.get(i);
            char replacing = unused.remove(i);

            // make recursive call
            solns = solve(newSoln, original, unused, solns);

            // if recursive call fails, add the letter back to the unused array
            unused.add(i, replacing);
        }

        return solns;
    }

    // check if all the words in a text are valid words
    public boolean areWords(String text) {
        text = text.trim() + " ";
        while (text.contains(" ")) {
            if (!lex.containsWord(text.substring(0, text.indexOf(" ")))) {
                return false;
            }
            text = text.substring(text.indexOf(" ") + 1);
        }
        return true;
    }

    // check is a text can be matched to words, given the proper cipher
    public boolean matchPhrase(String text) {
        text = text.trim() + " ";
        while (text.contains(" ")) {
            String word = text.substring(0, text.indexOf(" "));
            word = makeRegex(word);           

            if (countqs(word) && lex.matchRegex(word).isEmpty()) {
                return false;
            }
            text = text.substring(text.indexOf(" ") + 1);

        }
        return true;
    }

    // sort words by shortest length for efficiency later on
    public static String setup(String sentence) {
        sentence = sentence.toUpperCase();
        //String setup = "";
        String[] words = sentence.split(" ");
        
        for (int i = 0; i < words.length; i++) {
            for (int j = words.length - 1; j > i; j--) {
                if (words[j].length() < words[j - 1].length()) {
                    String temp = words[j];
                    words[j] = words[j - 1];
                    words[j - 1] = temp;
                }
            }
        }
        return String.join(" ", words);
    }

    // decrypt a string with the cipher
    public static String solveWith(char[] soln, String encrypted) {
        String str = "";
        for (int i = 0; i < encrypted.length(); i++) {
            if (toInt(encrypted.charAt(i)) != -1) {
                str += soln[toInt(encrypted.charAt(i))];
            } else {
                str += encrypted.charAt(i);
            }
        }
        return str;
    }

    // check that there are still question marks in a word
    public static boolean countqs(String word) {
        int counter = 1;
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == '?')
                counter++;
        }
        return (word.length() + 1) / counter > 1;
    }

    // convert a word to regex for the lexicon
    public static String makeRegex(String word) {
        String str = "";
        int i = 0;
        while (i < word.length()) {
            if (Character.isUpperCase(word.charAt(i))) {
                str += "?";
            } else {
                str += word.charAt(i);
            }
            i++;
        }
        return str;
    }

    public static int toInt(char c) {
        if (Character.isLowerCase(c)) {
            return "abcdefghijklmnopqrstuvwxyz".indexOf(c);
        } else if (Character.isUpperCase(c)) {
            return "ABCDEFGHIJKLMNOPQRSTUVWXYZ".indexOf(c);
        } else return -1;
    } 

    public static Character toUpper(int i) {
        return "ABCDEFGHIJKLMNOPQRSTUVWXYZ".charAt(i);
    }

    public static Character toLower(int i) {
        return "abcdefghijklmnopqrstuvwxyz".charAt(i);
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(new FileStream("puzzle.txt"));
        String puzzleWords = input.nextLine(); 
        new Solver(puzzleWords);
    }

}