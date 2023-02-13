import java.io.*;
import java.util.*;


class BookIndex {
    private ArrayList<String> excludeWords;
    private HashMap<String, HashSet<Integer>> index = new HashMap<>();

    public void readFiles(String[] pageFiles, String excludeFile) {
        excludeWords = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(excludeFile))) {
            String line;
            while ((line = br.readLine()) != null) {
                excludeWords.add(line.toLowerCase().trim());
            }
        } catch (IOException e) {
            System.out.println("Error reading exclude words file: " + excludeFile);
        }

        for (int i = 0; i < pageFiles.length; i++) {
            try (BufferedReader br = new BufferedReader(new FileReader(pageFiles[i]))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] words = line.toLowerCase().split("[^\\w']+");
                    for (String word : words) {
                        if (!excludeWords.contains(word) && word.length() > 0) {
                            if (!index.containsKey(word)) {
                                index.put(word, new HashSet<>());
                            }
                            index.get(word).add(i + 1);
                        }
                    }
                }
            } catch (IOException e) {
                System.out.println("Error reading page file: " + pageFiles[i]);
            }
        }
    }

    public void generateIndex() {
        List<Map.Entry<String, HashSet<Integer>>> list = new ArrayList<>(index.entrySet());
        list.sort(Map.Entry.comparingByKey());

        index.clear();
        for (Map.Entry<String, HashSet<Integer>> entry : list) {
            index.put(entry.getKey(), entry.getValue());
        }
    }

    public void writeToFile(String fileName) {
        IndexWriter indexWriter = new IndexWriter();
        indexWriter.writeToFile(index, fileName);
    }
}
class IndexWriter {
    public void writeToFile(HashMap<String, HashSet<Integer>> index, String fileName) {
        try (FileWriter fw = new FileWriter(fileName)) {
            for (Map.Entry<String, HashSet<Integer>> entry : index.entrySet()) {
                fw.write(entry.getKey() + " : " + entry.getValue().toString().replace("[", "").replace("]", "").replace(",", "") + "\n");
            }
        } catch (IOException e) {
            System.out.println("Error writing index to file: " + fileName);
        }
    }
}

public class Main {
    public static void main(String[] args) {
        BookIndex bookIndex = new BookIndex();
        bookIndex.readFiles(new String[]{"Page1.txt", "Page2.txt", "Page3.txt"}, "exclude-words.txt");
        bookIndex.generateIndex();
        bookIndex.writeToFile("index.txt");
    }
}