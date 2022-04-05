package readability;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Main {
    private static final Scanner scanner = new Scanner(System.in);
    private static double score = 0;
    private static int words = 0;
    private static int sentences = 0;
    private static int characters = 0;
    private static int syllables = 0;
    private static int polysyllables = 0;
    private static double recommendedAge = 0;

    public static void main(String[] args) {
        String fileName = readFileName(args[0]);
        readFile(fileName);
        calculateScore();
    }

    public static String readFileName(String arg) {
        String[] input = arg.split(" ");
        return input[input.length - 1];
    }

    public static void readFile(String fileName) {
        File file = new File(fileName);
        System.out.println("The text is:");
        try (Scanner sc = new Scanner(file)) {
            while (sc.hasNext()) {
                String line = sc.nextLine();
                System.out.print(line);
                String[] sentencesArray = line.split("[.!?] ");
                sentences += sentencesArray.length;
                for (String sentence : sentencesArray) {
                    String[] wordsArray = sentence.split(" ");
                    words += wordsArray.length;
                    for (String word : wordsArray) {
                        characters += word.length();
                        if (word.substring(word.length() - 1).matches("\\W")) {
                            word = word.substring(0, word.length() - 1);
                        }
                        int syllablesInWord = 0;
                        for (int i = 0; i < word.length(); i++) {
                            String currentChar = word.substring(i, i + 1);
                            if (i == word.length() - 1 ? currentChar.matches("[aiouyAIOUY]") :
                                    currentChar.matches("[aeiouyAEIOUY]") &&
                                    (i == 0 || word.substring(i - 1, i).matches("[^aeiouyAEIOUY]"))) {
                                syllablesInWord++;
                            }
                        }
                        if (syllablesInWord == 0) {
                            syllablesInWord = 1;
                        }
                        syllables += syllablesInWord;
                        if (syllablesInWord > 2) {
                            polysyllables++;
                        }
                    }
                }
                characters += sentencesArray.length - 1;
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
            return;
        }
        System.out.println("\n");
        System.out.printf("Words: %d\n", words);
        System.out.printf("Sentences: %d\n", sentences);
        System.out.printf("Characters: %d\n", characters);
        System.out.printf("Syllables: %d\n", syllables);
        System.out.printf("Polysyllables: %d\n", polysyllables);
    }

    public static void calculateScore() {
        System.out.println("Enter the score you want to calculate (ARI, FK, SMOG, CL, all):");
        switch (scanner.nextLine()) {
            case "ARI":
                automatedReadabilityIndex();
                break;
            case "FK":
                fleshKincaidReadabilityTest();
                break;
            case "SMOG":
                simpleMeasureGobbledygook();
                break;
            case "CL":
                colemanLiauIndex();
                break;
            case "all":
                automatedReadabilityIndex();
                fleshKincaidReadabilityTest();
                simpleMeasureGobbledygook();
                colemanLiauIndex();
                recommendedAge /= 4;
                break;
            default:
                break;
        }
        System.out.printf("\nThis text should be understood in average by %.2f-year-olds.", recommendedAge);
    }

    public static void automatedReadabilityIndex() {
        score = 4.71 * ((double) characters / words) + 0.5 * ((double) words / sentences) - 21.43;
        int age = calculateRecommendedAge(score);
        recommendedAge += age;
        System.out.printf("Automated Readability Index: %.2f (about %d-year-olds).\n", score, age);
    }

    public static void fleshKincaidReadabilityTest() {
        score = 0.39 * ((double) words / sentences) + 11.8 * ((double) syllables / words) - 15.59;
        int age = calculateRecommendedAge(score);
        recommendedAge += age;
        System.out.printf("Flesch–Kincaid readability tests: %.2f (about %d-year-olds).\n", score, age);
    }

    public static void simpleMeasureGobbledygook() {
        score = 1.043 * Math.sqrt((double) polysyllables * 30 / sentences) + 3.1291;
        int age = calculateRecommendedAge(score);
        recommendedAge += age;
        System.out.printf("Simple Measure of Gobbledygook: %.2f (about %d-year-olds).\n", score, age);
    }

    public static void colemanLiauIndex() {
        score = 0.0588 * characters / words * 100 - 0.296 * sentences / words * 100 - 15.8;
        int age = calculateRecommendedAge(score);
        recommendedAge += age;
        System.out.printf("Coleman–Liau index: %.2f (about %d-year-olds).\n", score, age);
    }

    public static int calculateRecommendedAge(double score) {
        int roundedScore = (int) Math.ceil(score);
        return roundedScore > 12 ? 24 : roundedScore + 6;
    }
}