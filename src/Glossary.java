import java.io.File;

import components.map.Map;
import components.map.Map1L;
import components.queue.Queue;
import components.queue.Queue1L;
import components.set.Set;
import components.set.Set1L;
import components.simplereader.SimpleReader;
import components.simplereader.SimpleReader1L;
import components.simplewriter.SimpleWriter;
import components.simplewriter.SimpleWriter1L;

/**
 * This program creates a glossary by reading an input text file containing
 * numerous terms and definitions and writes a group of HTML files, inclusive of
 * the following: 1. a top level index which lists each term in the glossary
 * (clicking on a term in the index takes the user to the page for that term and
 * its associated definition) 2. separate pages for each of the terms in the
 * glossary with their definitions (if a definition contains a term that appears
 * in the glossary, clicking on that term will take the user to the page for
 * that term and it's associated definition) (a link at the bottom is present
 * that redirects the user back to the index) All the created HTML files are
 * stored in a destination specified by the user
 *
 * @author Aishwarya Srivastava
 *
 */
public final class Glossary {

    private Glossary() {
    }

    /**
     * Extracts terms and their definitions from input text file and stores them
     * in a map of type <String, String> where the KEY is the TERM and the VALUE
     * is the DEFINITION
     *
     * @param fileName
     *            name of the input file
     *
     * @return a map of terms and definitions
     *
     * @requires filname is not empty
     * @Ensures entries(terms) = entries(fileName.txt)
     *
     */
    public static Map<String, String> addFromInputFile(String fileName) {
        assert !fileName.equals("") : "Violation of: fileName is not null";
        Map<String, String> terms = new Map1L<>();
        SimpleReader input = new SimpleReader1L(fileName);
        /*
         * The following while loop goes through each term-definition pair in
         * the input file instead of going over each line. It treats the first
         * line in the pair as the term, goes over to the next line, and treats
         * that as the definition. Then it goes to the next line, checks if that
         * line is empty or not. If it is, or the end of the file has been
         * reached, it adds the term and definition to the map, and starts a new
         * line. if that line is not empty, that means that the definition is
         * continuing. it concatenates that line to the definition, and performs
         * the same check for the next line, till an empty line or the end of
         * the file is reached
         */

        while (!input.atEOS()) {
            String term = input.nextLine();
            String definition = input.nextLine();
            if (!input.atEOS()) {
                String str = input.nextLine();
                while (!str.equals("") && !input.atEOS()) {
                    definition = definition + " " + str;
                    str = input.nextLine();
                }
            }
            terms.add(term, definition);
        }
        input.close();
        return terms;
    }

    /**
     * Arranges the terms in the map alphabetically and stores them in a queue
     *
     * @param terms
     *            map containing terms and their respective definitions
     * @return a queue containing the map pairs sorted in alphabetical order
     * @requires map is not empty
     * @clears terms
     * @ensures entries(termsQueue) = entries(termsMap)
     */

    public static Queue<Map.Pair<String, String>> arrangeByAlphabet(
            Map<String, String> terms) {
        assert terms != null : "Violation of: terms is not null";
        Queue<Map.Pair<String, String>> termsQueue = new Queue1L<Map.Pair<String, String>>();

        if (terms.size() == 1) {
            termsQueue.enqueue(terms.removeAny());
        } else {
            Map.Pair<String, String> randomPair = terms.removeAny();
            String first = randomPair.key();
            terms.add(first, randomPair.value());
            for (Map.Pair<String, String> x : terms) {
                if (x.key().compareTo(first) < 0) {
                    first = x.key();
                }
            }

            termsQueue.enqueue(terms.remove(first));
            termsQueue.append(arrangeByAlphabet(terms));

        }
        return termsQueue;
    }

    /**
     * Creates an index html page containing a list of terms, linked to their
     * own definition page
     *
     * @param termsQueue
     *            a queue of map.pairs containing terms and definitions,
     *            arranged in alphabetical order
     * @param folderName
     *            name of the destination folder where the index page should be
     *            stored
     * @requires <pre>
     * folederName is not empty
     *          termsQueue is not empty
     * </pre>
     * @ensures <pre>
     * created file is a well formatted HTML page with the title "Glossary",
     *          followed by a horizontal line, followed by the heading "Index", and a list of all the
     *          terms in the glossary, arranged in alphabetical order, linked to their own separate definition
     *          page
     * </pre>
     */

    public static void createIndex(Queue<Map.Pair<String, String>> termsQueue,
            String folderName)

    {
        assert !folderName.equals("") : "Violation of: folderName is not null";
        assert termsQueue != null : "Violation of: termsQueue is not null";
        String outputFile = folderName + "/index.html";
        Queue<Map.Pair<String, String>> temp = termsQueue.newInstance();
        temp.transferFrom(termsQueue);
        SimpleWriter out = new SimpleWriter1L(outputFile);
        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
        out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
        out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
        out.println("<head>");
        out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
        out.println("<title>Index</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>Glossary</h1>");
        out.println("<hr>");
        out.println("<h1>Index</h1>");
        out.println("<ul>");
        while (temp.length() > 0) {
            Map.Pair<String, String> element = temp.dequeue();
            termsQueue.enqueue(element);
            out.println("<li>");
            out.println("<a href = \"" + folderName + "/" + element.key()
                    + ".html" + "\">" + element.key() + "</a>");
            out.println("</li>");
        }
        out.close();

    }

    /**
     * Creates an html page containing the term and the definition of a term
     *
     * @param term
     *            term whose page is being made
     * @param folderName
     *            name of the folder where the html file should be stored
     * @param terms
     *            a set of strings containing all the terms
     * @requires <pre>
     * folderName is not empty
     *          Map.Pair term is not empty
     *          Set of terms is not empty
     * </pre>
     * @ensures <pre>
     * a well formated HTML page with the term as the heading of the page in red italics,
     *          followed by the definition of the term. If any of the terms in the Set<String> terms appears
     *          in the definition, it is linked to the definition page of that particular term.
     *          Definition is followed by a horizontal line, below which there is a link that redirects the
     *          user back to the index page
     * </pre>
     */
    public static void termPage(Map.Pair<String, String> term,
            String folderName, Set<String> terms) {

        assert !folderName.equals("") : "Violation of: folderName is not null";
        assert term != null : "Violation of: term is not empty";
        assert terms.size() != 0 : "Violation of: terms are not empty";

        String outputFile = folderName + "/" + term.key() + ".html";
        SimpleWriter out = new SimpleWriter1L(outputFile);
        out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
        out.println("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Strict//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd\">");
        out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
        out.println("<head>");
        out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />");
        out.println("<title>" + term.key() + "</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1 style=\"color:red;font-style:italic\">" + term.key()
                + "</h1>");
        printDefinition(term.value(), terms, folderName, out);
        out.println("<hr>");
        out.println("<p>Return to ");
        out.println("<a href = \"" + folderName + "/index.html"
                + "\">index</a>");

    }

    /**
     * Returns the first "word" (maximal length string of characters not in
     * {@code separators}) or "separator string" (maximal length string of
     * characters in {@code separators}) in the given {@code text} starting at
     * the given {@code position}.
     *
     * @param text
     *            the {@code String} from which to get the word or separator
     *            string
     * @param position
     *            the starting index
     * @param separators
     *            the {@code Set} of separator characters
     * @return the first word or separator string found in {@code text} starting
     *         at index {@code position}
     * @requires 0 <= position < |text|
     * @ensures <pre>
     * nextWordOrSeparator =
     *   text[position, position + |nextWordOrSeparator|)  and
     * if entries(text[position, position + 1)) intersection separators = {}
     * then
     *   entries(nextWordOrSeparator) intersection separators = {}  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      intersection separators /= {})
     * else
     *   entries(nextWordOrSeparator) is subset of separators  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      is not subset of separators)
     * </pre>
     */
    public static String nextWordOrSeparator(String text, int position,
            Set<Character> separators) {
        String next = "";
        if (separators.contains(text.charAt(position))) {
            while ((position < text.length())
                    && (separators.contains(text.charAt(position)))) {
                next = next + text.charAt(position);
                position++;
            }
        } else {
            while ((position < text.length())
                    && (!separators.contains(text.charAt(position)))) {
                next = next + text.charAt(position);
                position++;
            }
        }

        return next;
    }

    /**
     * Generates the set of characters in the given {@code String} into the
     * given {@code Set}.
     *
     * @param str
     *            the given {@code String}
     * @param strSet
     *            the {@code Set} to be replaced
     * @replaces strSet
     * @ensures strSet = entries(str)
     */
    public static void generateElements(String str, Set<Character> strSet) {
        for (int i = 0; i < str.length(); i++) {
            if (!strSet.contains(str.charAt(i))) {
                strSet.add(str.charAt(i));
            }
        }
    }

    /**
     * @return a set containing all the special characters likely to appear in a
     *         sentance
     */

    public static Set<Character> specialCharacters() {
        Set<Character> spCharsSet = new Set1L<>();
        String specialChars = ".,/\":;-!><(){}[] ";
        generateElements(specialChars, spCharsSet);
        return spCharsSet;
    }

    /**
     * prints the definition of the term in the term page
     *
     * @param term
     *            Term whose definition is to be printed
     * @param terms
     *            Set of strings containing all terms
     * @param folderName
     *            Name of the folder where the term page is saved
     * @param out
     *            SimpleWriter stream using which the term page is
     *            created/modified
     * @requires <pre>
     * term is not an empty string
     *                 terms is not an empty set
     *                 folderName is not an empty string
     *                 out is an empty stream
     *                 the term is present in the glossary
     * </pre>
     * @ensures <pre>
     * prints the definition of the term
     *          if the definition contains any of the other terms in the glossary,
     *          those terms are linked to their own separate definition page
     * </pre>
     *
     */
    public static void printDefinition(String term, Set<String> terms,
            String folderName, SimpleWriter out) {
        assert out.isOpen() : "Violation of: out stream is open";
        assert !term.equals("") : "Violation of: term is not an empty string";
        assert terms.size() != 0 : "Violation of: Set of terms is not empty";
        //assert terms.contains(term) : "Violation of: term is present in the glossary";

        while (term.length() > 0) {
            String word = nextWordOrSeparator(term, 0, specialCharacters());
            term = term.substring(term.indexOf(word) + word.length());
            boolean contains = false;
            for (String x : terms) {
                String link = "";
                String highlighted = "";
                if (word.contains(x)) {
                    contains = true;
                    link = x;
                    highlighted = word;
                }

                if (contains) {
                    out.println("<a href = \"" + folderName + "/" + link
                            + ".html" + "\">" + highlighted + "</a>");
                }
            }
            if (!contains) {
                out.print(word);
            }
        }

    }

    /**
     * creates an index file, along with a separate page for each term
     *
     * @param input
     *            name of the text file containing the terms and definitions
     * @param output
     *            name of the output folder where all the files will be saved
     * @requires <pre>
     * input filename is not empty and input text file exists
     *               output foldername is not empty and output folder exists
     * </pre>
     * @ensures <pre>
     * creates one index page listing all the terms on the glossary, linked to
     *          their own definition pages and creates all the definition pages as well
     * </pre>
     *
     */
    public static void createFiles(String input, String output) {
        assert !input.equals("") : "Violation of: name of the input txt file is not empty";
        assert !output.equals("") : "Violation of: destination folder is specified";
        //assert (new File(input)).exists() : "Violation of: input.txt exists";
        assert (new File(output)).isDirectory() : "Violation of: output folder exists";

        Set<String> termsSet = new Set1L<>();
        for (Map.Pair<String, String> x : addFromInputFile(input)) {
            termsSet.add(x.key());
        }
        Queue<Map.Pair<String, String>> terms = arrangeByAlphabet(addFromInputFile(input));
        createIndex(terms, output);
        while (terms.length() > 0) {
            termPage(terms.dequeue(), output, termsSet);
        }
    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
        SimpleReader in = new SimpleReader1L();
        SimpleWriter out = new SimpleWriter1L();
        out.println("Enter an input file: ");
        String input = in.nextLine();
        out.println("Enter a destination folder: ");
        String output = in.nextLine();
        createFiles(input, output);
        out.println("Your files have been stored in the specified destination.");
        out.println("Good bye!");
        in.close();
        out.close();
    }

}
