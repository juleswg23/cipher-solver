import structure5.*;
import java.util.Scanner;

public class Solver
{   
    LexiconTrie lex;
    String puzzleWords;
    char[] soln;

    public Solver(String words) {
        lex = new LexiconTrie();
        lex.addWordsFromFile("nytWords.txt");
        lex.addWordsFromFile("nytProperNouns.txt");

        soln = new char[26];
        for (int i = 0; i < 26; i++) {
            soln[i] = '?';
        }

        puzzleWords = setup(words);

        Vector<Character> unused = new Vector<>();
        for (int i = 0; i < 26; i++){
            unused.add(toLower(i));
        }
        
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

    public Vector<char[]> solve(char[] curSoln, String original, Vector<Character> unused, Vector<char[]> solns) {
        //build soFar
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

        //CHANGED
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

        // recursion
        for (int i = 0; i < unused.size(); i++) {
            char[] newSoln = new char[curSoln.length];
            System.arraycopy(curSoln, 0, newSoln, 0, 26); 
           
            newSoln[toInt(toReplace)] = unused.get(i);
            char replacing = unused.remove(i);

            solns = solve(newSoln, original, unused, solns);
            // newSolns = solve(newSoln, original, unused, solns);
            // if (!newSolns.equals(solns)) {
            //     solns = newSolns;
            // }
            unused.add(i, replacing);
        }

        return solns;
    }

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

    public static boolean countqs(String word) {
        int counter = 1;
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == '?')
                counter++;
        }
        return (word.length() + 1) / counter > 1;
    }

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