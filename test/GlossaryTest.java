import static org.junit.Assert.assertEquals;

import org.junit.Test;

import components.set.Set;
import components.set.Set1L;

public class GlossaryTest {
    //The following files are used in the tests
    public final String INPUT = "C:/Users/Arunima/Desktop/OsuCseWsTemplate/workspace/Glossary/test/input.txt";
    public final String OUTPUT = "C:/Users/Arunima/Desktop/OsuCseWsTemplate/workspace/Glossary/test/output";

    public static void generateElements(String str, Set<Character> strSet) {
        for (int i = 0; i < str.length(); i++) {
            if (!strSet.contains(str.charAt(i))) {
                strSet.add(str.charAt(i));
            }
        }
    }

    public static Set<Character> specialCharacters() {
        Set<Character> spCharsSet = new Set1L<>();
        String specialChars = ".,/\":;-!><(){}[] ";
        generateElements(specialChars, spCharsSet);
        return spCharsSet;
    }

    @Test
    public void nextWordOrSeparatorTest1() {
        String test = "...Hello...World";
        String result = "Hello";
        assertEquals(result,
                Glossary.nextWordOrSeparator(test, 3, specialCharacters()));
    }

    @Test
    public void nextWordOrSeparator2() {
        String test = "...Hello...World";
        String result = "...";
        assertEquals(result,
                Glossary.nextWordOrSeparator(test, 0, specialCharacters()));
    }

    @Test
    public void InputExtractionTest() {
        Glossary.createFiles(this.INPUT, this.OUTPUT);

    }
}
