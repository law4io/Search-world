
不同列之间不会重叠。
Header1 Header2 Header3
Value1        Value3
Value4 Value5
Value7 Value8  Value9


Header1 Header2 Header3
Value1          Value3
Value4  Value5
Value7  Value8  Value9


Header1 Header2 Header3
Value1  Value2    Value3
Value4    Value5 Value6
Value7 Value8       Value9


Header1     Header2    Header3
Value1      Value2     Value3
   Value4            Value6
Value7  Value8

import java.util.*;

class ColumnBoundary {
    int headerStart;
    int headerEnd;
    String headerName;

    public ColumnBoundary(int headerStart, int headerEnd, String headerName) {
        this.headerStart = headerStart;
        this.headerEnd = headerEnd;
        this.headerName = headerName;
    }
}

public class TableParser {

    // 解析一行数据的方法
    public static Map<String, String> extractRowData(String line, List<ColumnBoundary> columnBoundaries) {
        Map<String, String> rowData = new HashMap<>();

        for (int i = 0; i < columnBoundaries.size(); i++) {
            ColumnBoundary current = columnBoundaries.get(i);
            int start = findStart(line, current.headerStart, i == 0 ? 0 : columnBoundaries.get(i - 1).headerEnd, current.headerEnd);
            int end = findEnd(line, current.headerEnd, i == columnBoundaries.size() - 1 ? line.length() : columnBoundaries.get(i + 1).headerStart, current.headerStart);

            if (start < end && start >= 0 && end <= line.length()) { // 确保索引在有效范围内
                String value = line.substring(start, end).trim();
                rowData.put(current.headerName, value);
            } else {
                rowData.put(current.headerName, "");
            }
        }

        return rowData;
    }

    private static int findStart(String line, int currentHeaderStart, int previousHeaderEnd, int currentHeaderEnd) {
        // 提取潜在数据值
        String potentialValue = line.substring(currentHeaderStart, Math.min(currentHeaderEnd, line.length())).trim();

        // 找到潜在数据中的第一个非空字符的位置
        int firstNonWhitespaceIndex = -1;
        for (int i = 0; i < potentialValue.length(); i++) {
            if (!Character.isWhitespace(potentialValue.charAt(i))) {
                firstNonWhitespaceIndex = i;
                break;
            }
        }

        if (firstNonWhitespaceIndex == -1) {
            return currentHeaderStart; // 如果没有非空字符，返回当前header起始位置
        }

        int adjustedStart = currentHeaderStart + firstNonWhitespaceIndex;

        // 向前查找直到遇到连续两个空格或达到前一个header的结束位置或行的开始位置
        while (adjustedStart > previousHeaderEnd && !(line.charAt(adjustedStart - 1) == ' ' && (adjustedStart - 2 >= previousHeaderEnd && line.charAt(adjustedStart - 2) == ' '))) {
            adjustedStart--;
        }

        // 如果adjustedStart已经到达上一个header的结束位置或行的开始位置，则不需要调整
        if (adjustedStart <= previousHeaderEnd || adjustedStart == 0) {
            return Math.max(adjustedStart, 0);
        } else {
            // 调整到非空格字符的位置
            while (line.charAt(adjustedStart) == ' ') {
                adjustedStart++;
            }
            return adjustedStart;
        }
    }

    private static int findEnd(String line, int currentHeaderEnd, int nextHeaderStart, int currentHeaderStart) {
        // 提取潜在数据值
        String potentialValue = line.substring(currentHeaderStart, Math.min(nextHeaderStart, line.length())).trim();

        // 找到潜在数据中的第一个非空字符的位置
        int firstNonWhitespaceIndex = -1;
        for (int i = 0; i < potentialValue.length(); i++) {
            if (!Character.isWhitespace(potentialValue.charAt(i))) {
                firstNonWhitespaceIndex = i;
                break;
            }
        }

        if (firstNonWhitespaceIndex == -1) {
            return currentHeaderEnd; // 如果没有非空字符，返回当前header结束位置
        }

        int adjustedEnd = currentHeaderStart + firstNonWhitespaceIndex + potentialValue.length();

        // 向后查找直到遇到连续两个空格或达到下一个header的起始位置或行的结束位置
        while (adjustedEnd + 1 < nextHeaderStart && adjustedEnd + 1 < line.length() && !(line.charAt(adjustedEnd) == ' ' && line.charAt(adjustedEnd + 1) == ' ')) {
            adjustedEnd++;
        }

        // 调整到非空格字符的位置
        while (adjustedEnd > 0 && line.charAt(adjustedEnd - 1) == ' ') {
            adjustedEnd--;
        }

        return Math.min(adjustedEnd, nextHeaderStart);
    }

    public static void main(String[] args) {
        List<ColumnBoundary> columnBoundaries = Arrays.asList(
            new ColumnBoundary(0, 10, "Header1"),
            new ColumnBoundary(15, 25, "Header2"),
            new ColumnBoundary(30, 40, "Header3")
        );

        String line = "Value1  Value2    Value3"; // 示例数据行

        Map<String, String> parsedData = extractRowData(line, columnBoundaries);
        for (Map.Entry<String, String> entry : parsedData.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}
