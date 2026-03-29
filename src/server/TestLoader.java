package server;

public class TestLoader {
    public static void main(String[] args) throws Exception {

        // تحميل القاموس
        DictionaryLoader loader = new DictionaryLoader("dictionary.json");

        // اختبار كلمات
        String[] testWords = {"python", "socket", "algorithm", "zzzzz"};

        for (String word : testWords) {
            String result = loader.search(word);
            if (result != null) {
                System.out.println("✅ " + word + " → " + result);
            } else {
                System.out.println("❌ Word not found: " + word);
            }
        }

        System.out.println("\nTotal words: " + loader.getSize());
    }
}
