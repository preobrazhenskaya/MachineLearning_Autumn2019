import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {
    public static String[] readDictionary() throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader("./src/dictionary_clean_unicue.txt"));
        String line;
        List<String> lines = new ArrayList<String>();
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
        return lines.toArray(new String[lines.size()]);
    }

    public static String[] doKGram(String str) {
        int arrayLength;
        if (str.length() % 3 != 0) {
            arrayLength = str.length() / 3 + 1;
        } else {
            arrayLength = str.length() / 3;
        }
        String kGrams[] = new String[arrayLength];
        int i = 0;
        for (int j = 0; j < str.length(); j = j + 3) {
            int end = j + 3;
            while (end > str.length()) {
                end--;
            }
            kGrams[i] = str.substring(j, end);
            i++;
        }
        return kGrams;
    }

    public static String[] searchCandidates(String[] kGrams, String[] dict) {
        List<String> candidates = new ArrayList<String>();
        for (int i = 0; i < kGrams.length; i++) {
            for (int j = 0; j < dict.length; j++) {
                if (dict[j].contains(kGrams[i])) {
                    candidates.add(dict[j]);
                }
            }
        }
        return candidates.toArray(new String[candidates.size()]);
    }

    private static int min(int n1, int n2, int n3) {
        return Math.min(Math.min(n1, n2), n3);
    }

    public static int levenstain(String str1, String str2) {
        int[] Di_1 = new int[str2.length() + 1];
        int[] Di = new int[str2.length() + 1];

        for (int j = 0; j <= str2.length(); j++) {
            Di[j] = j; // (i == 0)
        }

        for (int i = 1; i <= str1.length(); i++) {
            System.arraycopy(Di, 0, Di_1, 0, Di_1.length);

            Di[0] = i; // (j == 0)
            for (int j = 1; j <= str2.length(); j++) {
                int cost = (str1.charAt(i - 1) != str2.charAt(j - 1)) ? 1 : 0;
                Di[j] = min(Di_1[j] + 1, Di[j - 1] + 1, Di_1[j - 1] + cost);
            }
        }
        return Di[Di.length - 1];
    }

    public static void main(String args[]) throws IOException {
        Scanner in = new Scanner(System.in);
        System.out.println("Enter word:");
        String inputStr = in.nextLine();
        long startTime = System.currentTimeMillis();
        String[] kGrams = doKGram(inputStr);
        String[] dict = readDictionary();
        String[] candidates = searchCandidates(kGrams, dict);
        Map<String, Integer> levDistance = new HashMap<String, Integer>();
        for (int i = 0; i < candidates.length; i++) {
            levDistance.put(candidates[i], levenstain(candidates[i], inputStr));
        }
        List<String> wordsWithMinLevDistance = new ArrayList<String>();
        int minDistance = Integer.MAX_VALUE;
        Set<String> keys = levDistance.keySet();
        for (String key : keys) {
            if (levDistance.get(key) < minDistance) {
                minDistance = levDistance.get(key);
            }
        }
        for (String key : keys) {
            if (levDistance.get(key) == minDistance) {
                wordsWithMinLevDistance.add(key);
            }
        }
        long timeSpent = System.currentTimeMillis() - startTime;
        for (int i = 0; i < wordsWithMinLevDistance.size(); i++) {
            System.out.println(wordsWithMinLevDistance.get(i));
        }
        System.out.println("Время: " + timeSpent);
    }
}
