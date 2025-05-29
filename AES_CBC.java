import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.util.Base64;
import java.security.SecureRandom;

public class AES_CBC_NoExplicitIV {
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/CBC/PKCS5Padding";
    private static final String KEY = "ThisIsASecretKey"; // 16字节密钥

    public static String encrypt(String plaintext) throws Exception {
        // 生成随机IV
        SecureRandom secureRandom = new SecureRandom();
        byte[] iv = new byte[16]; // AES块大小为16字节
        secureRandom.nextBytes(iv);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        // 准备密钥
        SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), ALGORITHM);

        // 初始化加密器
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivParameterSpec);

        // 执行加密
        byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes());

        // 将IV和密文组合（前16字节是IV，后面是密文）
        ByteBuffer byteBuffer = ByteBuffer.allocate(iv.length + encryptedBytes.length);
        byteBuffer.put(iv);
        byteBuffer.put(encryptedBytes);
        byte[] combined = byteBuffer.array();

        // Base64编码结果
        return Base64.getEncoder().encodeToString(combined);
    }

    public static String decrypt(String ciphertext) throws Exception {
        // Base64解码
        byte[] combined = Base64.getDecoder().decode(ciphertext);

        // 提取IV
        byte[] iv = new byte[16];
        System.arraycopy(combined, 0, iv, 0, iv.length);
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

        // 提取密文
        byte[] encryptedBytes = new byte[combined.length - 16];
        System.arraycopy(combined, 16, encryptedBytes, 0, encryptedBytes.length);

        // 准备密钥
        SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), ALGORITHM);

        // 初始化解密器
        Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivParameterSpec);

        // 执行解密
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes);
    }

    public static void main(String[] args) {
        try {
            String plaintext = "Hello, CBC mode encryption without explicit IV!";
            System.out.println("原始明文: " + plaintext);
            
            String encrypted = encrypt(plaintext);
            System.out.println("加密后: " + encrypted);
            
            String decrypted = decrypt(encrypted);
            System.out.println("解密后: " + decrypted);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
