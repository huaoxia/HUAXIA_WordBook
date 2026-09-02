package ch3.data;

import java.io.*;
import java.sql.Timestamp;

/**
 * CSV导入导出类（创新功能）
 * 导入格式：单词,释义,例句,发音文件名
 */
public class CsvImportExport {

    /**
     * 从CSV文件导入单词
     * @param owner 用户名
     * @param file CSV文件
     * @return 成功导入的数量
     */
    public static int importFromCsv(String owner, File file) {
        int count = 0;
        String encoding = detectEncoding(file);  // 自动检测编码（UTF-8/GBK）
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                new FileInputStream(file), encoding))) {
            String line;
            boolean firstLine = true;
            AddWord addWord = new AddWord();
            QueryOneWord query = new QueryOneWord();

            while ((line = br.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    if (line.contains("单词") || line.toLowerCase().contains("word")) continue;
                }
                String[] parts = line.split(",", -1);
                if (parts.length < 2 || parts[0].trim().isEmpty()) continue;

                Word w = new Word();
                w.setOwner(owner);
                w.setEnglishWord(parts[0].trim());
                w.setMeaning(parts.length > 1 ? parts[1].trim() : "");
                w.setSentence(parts.length > 2 ? parts[2].trim() : "");
                w.setSentenceCn(parts.length > 3 ? parts[3].trim() : "");
                w.setVoice(parts.length > 4 ? parts[4].trim() : "");
                ReviewScheduler.initNewWord(w);

                // 避免重复导入
                Word check = new Word();
                check.setOwner(owner);
                check.setEnglishWord(w.getEnglishWord());
                if (query.queryOneWord(check) == null) {
                    if (addWord.insertWord(w) > 0) count++;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return count;
    }

    /**
     * 导出单词到CSV文件
     * @param owner 用户名
     * @param file 目标文件
     * @return 导出的数量
     */
    public static int exportToCsv(String owner, File file) {
        QueryAllWord query = new QueryAllWord();
        query.setOwner(owner);
        Word[] words = query.queryAllWord();

        try (OutputStream os = new FileOutputStream(file)) {
            // 写入UTF-8 BOM，确保Excel打开不乱码
            os.write(new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF});
            PrintWriter pw = new PrintWriter(new OutputStreamWriter(os, "UTF-8"));
            pw.println("单词,释义,例句,例句翻译,发音文件,记忆强度,复习次数");
            for (Word w : words) {
                pw.printf("%s,%s,%s,%s,%s,%d,%d%n",
                        escapeCsv(w.getEnglishWord()),
                        escapeCsv(w.getMeaning()),
                        escapeCsv(w.getSentence()),
                        escapeCsv(w.getSentenceCn()),
                        escapeCsv(w.getVoice()),
                        w.getMemoryStrength(),
                        w.getReviewCount());
            }
            pw.flush();
            return words.length;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /** CSV字段转义 */
    private static String escapeCsv(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    /**
     * 自动检测CSV文件编码
     * 先检查UTF-8 BOM，再尝试UTF-8解码（检测乱码替换字符），最后回退到GBK
     */
    private static String detectEncoding(File file) {
        try {
            byte[] bytes = readFileBytes(file);
            if (bytes.length < 3) return "UTF-8";

            // 检查UTF-8 BOM (EF BB BF)
            if ((bytes[0] & 0xFF) == 0xEF && (bytes[1] & 0xFF) == 0xBB && (bytes[2] & 0xFF) == 0xBF) {
                return "UTF-8";
            }

            // 尝试UTF-8解码，检查是否有乱码替换字符
            String utf8Str = new String(bytes, "UTF-8");
            if (!utf8Str.contains("\uFFFD")) {
                // UTF-8解码无乱码，再检查中文是否正常（GBK编码的中文用UTF-8解码通常会产生替换字符）
                return "UTF-8";
            }

            // 有乱码，大概率是GBK
            return "GBK";
        } catch (Exception e) {
            return "UTF-8";
        }
    }

    /** 读取文件全部字节 */
    private static byte[] readFileBytes(File file) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] buffer = new byte[4096];
            int len;
            while ((len = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
        }
        return baos.toByteArray();
    }
}
