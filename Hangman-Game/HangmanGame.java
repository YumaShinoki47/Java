import java.util.Scanner;
import java.util.Random;
import java.util.Arrays;

public class HangmanGame {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();
        
        // 単語のリスト
        String[] words = {
            "JAVA", "PYTHON", "COMPUTER", "PROGRAMMING", "GAME",
            "DEVELOPER", "CODE", "ALGORITHM", "DATABASE", "NETWORK",
            "SECURITY", "DESIGN", "PROJECT", "SOFTWARE", "HARDWARE"
        };
        
        // ゲーム設定
        String targetWord = words[random.nextInt(words.length)];
        char[] guessedWord = new char[targetWord.length()];
        boolean[] guessedLetters = new boolean[26]; // A-Zの26文字
        int maxMistakes = 6;
        int mistakes = 0;
        boolean hasWon = false;
        
        // 初期化：すべて'_'で埋める
        Arrays.fill(guessedWord, '_');
        
        System.out.println("=== ハングマンゲーム ===");
        System.out.println("単語を当ててください！（アルファベット大文字で入力）");
        System.out.println("最大" + maxMistakes + "回まで間違えることができます。");
        System.out.println();
        
        // メインゲームループ
        while (mistakes < maxMistakes && !hasWon) {
            // 現在の状態を表示
            displayGameState(guessedWord, mistakes, maxMistakes, guessedLetters);
            
            // プレイヤーの入力
            System.out.print("文字を入力してください: ");
            String input = scanner.nextLine().toUpperCase();
            
            // 入力検証
            if (input.length() != 1) {
                System.out.println("1文字だけ入力してください！");
                continue;
            }
            
            char guess = input.charAt(0);
            
            // アルファベットかチェック
            if (guess < 'A' || guess > 'Z') {
                System.out.println("アルファベット（A-Z）を入力してください！");
                continue;
            }
            
            // 既に推測済みかチェック
            int letterIndex = guess - 'A'; // 'A'=0, 'B'=1, ...
            if (guessedLetters[letterIndex]) {
                System.out.println("その文字は既に推測済みです！");
                continue;
            }
            
            // 推測済みとしてマーク
            guessedLetters[letterIndex] = true;
            
            // 文字が単語に含まれているかチェック
            boolean found = false;
            for (int i = 0; i < targetWord.length(); i++) {
                if (targetWord.charAt(i) == guess) {
                    guessedWord[i] = guess;
                    found = true;
                }
            }
            
            if (found) {
                System.out.println("正解！文字 '" + guess + "' が含まれています。");
                
                // 勝利条件チェック
                if (Arrays.equals(guessedWord, targetWord.toCharArray())) {
                    hasWon = true;
                }
            } else {
                mistakes++;
                System.out.println("残念！文字 '" + guess + "' は含まれていません。");
            }
            
            System.out.println();
        }
        
        // ゲーム終了処理
        displayGameState(guessedWord, mistakes, maxMistakes, guessedLetters);
        
        if (hasWon) {
            System.out.println("★ おめでとうございます！単語を当てました！");
            System.out.println("答えは「" + targetWord + "」でした。");
            System.out.println("間違い: " + mistakes + "/" + maxMistakes);
            
            // 成績評価
            if (mistakes == 0) {
                System.out.println("◎ パーフェクト！素晴らしい！");
            } else if (mistakes <= 2) {
                System.out.println("○ 優秀です！");
            } else if (mistakes <= 4) {
                System.out.println("△ よく頑張りました！");
            } else {
                System.out.println("△ ギリギリでしたが、クリアです！");
            }
        } else {
            System.out.println("× ゲームオーバー！");
            System.out.println("正解は「" + targetWord + "」でした。");
        }
        
        scanner.close();
    }
    
    // ゲーム状態を表示するメソッド
    public static void displayGameState(char[] guessedWord, int mistakes, int maxMistakes, boolean[] guessedLetters) {
        // ハングマンの絵を表示
        displayHangman(mistakes);
        
        // 現在の単語状態
        System.out.print("単語: ");
        for (char c : guessedWord) {
            System.out.print(c + " ");
        }
        System.out.println();
        
        // 間違い回数
        System.out.println("間違い: " + mistakes + "/" + maxMistakes);
        
        // 使用済み文字
        System.out.print("使用済み文字: ");
        for (int i = 0; i < guessedLetters.length; i++) {
            if (guessedLetters[i]) {
                System.out.print((char)('A' + i) + " ");
            }
        }
        System.out.println();
        System.out.println("------------------------");
    }
    
    // ハングマンの絵を表示するメソッド
    public static void displayHangman(int mistakes) {
        String[] hangman = {
            "  +---+",
            "  |   |",
            "      |",
            "      |",
            "      |",
            "      |",
            "========="
        };
        
        // 間違い回数に応じて絵を更新
        switch (mistakes) {
            case 1:
                hangman[2] = "  O   |"; // 頭
                break;
            case 2:
                hangman[2] = "  O   |"; // 頭
                hangman[3] = "  |   |"; // 体
                break;
            case 3:
                hangman[2] = "  O   |"; // 頭
                hangman[3] = " /|   |"; // 体+左腕
                break;
            case 4:
                hangman[2] = "  O   |"; // 頭
                hangman[3] = " /|\\  |"; // 体+両腕
                break;
            case 5:
                hangman[2] = "  O   |"; // 頭
                hangman[3] = " /|\\  |"; // 体+両腕
                hangman[4] = " /    |"; // 左足
                break;
            case 6:
                hangman[2] = "  O   |"; // 頭
                hangman[3] = " /|\\  |"; // 体+両腕
                hangman[4] = " / \\  |"; // 両足
                break;
        }
        
        // 絵を表示
        for (String line : hangman) {
            System.out.println(line);
        }
        System.out.println();
    }
}