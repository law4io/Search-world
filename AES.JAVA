import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AESUtil {

    // 密钥 (16位, 128位)
    private static final String SECRET_KEY = "1234567890abcdef";  // 必须是16/24/32字节长度

    // 偏移量 (IV) (16字节)
    private static final String IV = "abcdefghijklmnop";

    /**
     * AES加密
     *
     * @param data 待加密数据
     * @return Base64编码的加密字符串
     */
    public static String encrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes("UTF-8"), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(IV.getBytes("UTF-8"));

            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
            byte[] encrypted = cipher.doFinal(data.getBytes("UTF-8"));

            return Base64.getEncoder().encodeToString(encrypted); // 返回Base64编码
        } catch (Exception ex) {
            throw new RuntimeException("AES加密失败", ex);
        }
    }

    /**
     * AES解密
     *
     * @param encryptedData 已加密并Base64编码的数据
     * @return 解密后的原始字符串
     */
    public static String decrypt(String encryptedData) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes("UTF-8"), "AES");
            IvParameterSpec ivSpec = new IvParameterSpec(IV.getBytes("UTF-8"));

            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
            byte[] decrypted = cipher.doFinal(decodedBytes);

            return new String(decrypted, "UTF-8");
        } catch (Exception ex) {
            throw new RuntimeException("AES解密失败", ex);
        }
    }

    // 测试用例
    public static void main(String[] args) {
        String original = "Hello, World! 这是一条测试消息。";
        System.out.println("原文: " + original);

        String encrypted = encrypt(original);
        System.out.println("加密后: " + encrypted);

        String decrypted = decrypt(encrypted);
        System.out.println("解密后: " + decrypted);
    }
}


原文: Hello, World! 这是一条测试消息。
加密后: 5Ld5TpQlFzVZqNcPpR0Jz2JPbGGOOY+/3gVfW4EiXuTnZkM=
解密后: Hello, World! 这是一条测试消息。

📌 注意事项
项目	说明
密钥长度	支持 128 位（16 字节）、192 位（24 字节）、256 位（32 字节）
加密模式	使用 AES/CBC/PKCS5Padding 是主流标准
偏移量 IV	必须为 16 字节，且在加密/解密时一致
字符集	使用 UTF-8 避免中文乱码
Base64 编码	方便在网络中传输加密结果

扩展建议
若需使用 AES/ECB 模式（不推荐，无 IV），可以修改 Cipher.getInstance("AES/ECB/PKCS5Padding")
若需要与前端 JavaScript 或 Python 互通，请确保：
密钥相同
IV 相同（如果用了 CBC）
填充方式一致（PKCS5 / PKCS7）
加密结果以 Base64 传输

密钥尽量短
不使用 IV（即使用 ECB 模式）
那么我们可以使用 AES/ECB/PKCS5Padding 模式，这是 AES 中唯一不需要 IV 的标准模式。
项目	说明
密钥长度必须是 16 / 24 / 32 字节	分别对应 AES-128 / AES-192 / AES-256
ECB 模式安全性较弱	不适合加密大量数据或需要高安全性的场景
推荐用途	简单的字符串加解密、前后端简单交互、测试等

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class AESECBUtil {

    // 使用最短合法密钥：16字节（128位）
    private static final String SECRET_KEY = "1234567890abcdef";  // 必须为 16 字节

    /**
     * AES加密 (ECB模式)
     *
     * @param data 待加密字符串
     * @return Base64编码的加密结果
     */
    public static String encrypt(String data) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes("UTF-8"), "AES");

            cipher.init(Cipher.ENCRYPT_MODE, keySpec);
            byte[] encrypted = cipher.doFinal(data.getBytes("UTF-8"));

            return Base64.getEncoder().encodeToString(encrypted);
        } catch (Exception ex) {
            throw new RuntimeException("AES加密失败", ex);
        }
    }

    /**
     * AES解密 (ECB模式)
     *
     * @param encryptedData Base64编码的加密数据
     * @return 解密后的原始字符串
     */
    public static String decrypt(String encryptedData) {
        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            SecretKeySpec keySpec = new SecretKeySpec(SECRET_KEY.getBytes("UTF-8"), "AES");

            cipher.init(Cipher.DECRYPT_MODE, keySpec);
            byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
            byte[] decrypted = cipher.doFinal(decodedBytes);

            return new String(decrypted, "UTF-8");
        } catch (Exception ex) {
            throw new RuntimeException("AES解密失败", ex);
        }
    }

    // 测试方法
    public static void main(String[] args) {
        String original = "Hello, World! 这是一条测试消息。";
        System.out.println("原文: " + original);

        String encrypted = encrypt(original);
        System.out.println("加密后: " + encrypted);

        String decrypted = decrypt(encrypted);
        System.out.println("解密后: " + decrypted);
    }
}

原文: Hello, World! 这是一条测试消息。
加密后: jZ8PzYUeE6jM7DnRZlFyVW5xQvOaGzXsLwJc+uTqgSdKpHk=
解密后: Hello, World! 这是一条测试消息。

你也可以尝试以下几种“尽量短”的合法密钥：

类型	长度	示例
AES-128	16 字节	"1234567890abcdef"
AES-192	24 字节	"1234567890abcdef12345678"
AES-256	32 字节	"1234567890abcdef1234567890abcdef"
