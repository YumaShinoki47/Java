import java.util.Scanner;
import java.util.Random;

public class NumberGuessingGame {
    public static void main(String[] args) {
        // スキャナーとランダムオブジェクトを作成
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();
        
        // ゲームの設定
        int targetNumber = random.nextInt(100) + 1; // 1-100の乱数
        int attempts = 0; // 試行回数
        int maxAttempts = 7; // 最大試行回数
        boolean hasWon = false;
        
        // ゲーム開始メッセージ
        System.out.println("=== 数当てゲーム ===");
        System.out.println("1から100までの数字を当ててください！");
        System.out.println("最大" + maxAttempts + "回まで挑戦できます。");
        System.out.println();
        
        // メインゲームループ
        while (attempts < maxAttempts && !hasWon) {
            attempts++;
            System.out.print("第" + attempts + "回目の予想: ");
            
            // プレイヤーの入力を取得
            int guess;
            try {
                guess = scanner.nextInt();
            } catch (Exception e) {
                System.out.println("数字を入力してください！");
                scanner.next(); // 無効な入力をクリア
                attempts--; // 試行回数を戻す
                continue;
            }
            
            // 入力範囲チェック
            if (guess < 1 || guess > 100) {
                System.out.println("1から100の範囲で入力してください！");
                attempts--; // 試行回数を戻す
                continue;
            }
            
            // 予想と正解を比較
            if (guess == targetNumber) {
                hasWon = true;
                System.out.println("★ 正解です！おめでとうございます！");
                System.out.println("答えは " + targetNumber + " でした。");
                System.out.println(attempts + "回で正解しました！");
            } else if (guess < targetNumber) {
                System.out.println("↑ もっと大きい数字です！");
                System.out.println("残り" + (maxAttempts - attempts) + "回");
            } else {
                System.out.println("↓ もっと小さい数字です！");
                System.out.println("残り" + (maxAttempts - attempts) + "回");
            }
            System.out.println();
        }
        
        // ゲーム終了処理
        if (!hasWon) {
            System.out.println("× ゲームオーバー！");
            System.out.println("正解は " + targetNumber + " でした。");
        }
        
        // プレイヤーの成績評価
        if (hasWon) {
            if (attempts <= 3) {
                System.out.println("◎ 素晴らしい！天才的な直感です！");
            } else if (attempts <= 5) {
                System.out.println("○ よくできました！なかなかの腕前です！");
            } else {
                System.out.println("△ ギリギリでしたが、よく頑張りました！");
            }
        }
        
        System.out.println("ゲームを終了します。ありがとうございました！");
        scanner.close();
    }
}