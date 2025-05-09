package kr.sonss.lab.backend.common.util;

public class CustomUtils {

    /**
     * 앞뒤 공백 + 중간 공백 + 줄바꿈 + 탭 + 특수문자 제거
     * @param input 사용자 입력 문자열
     * @return 정리된 문자열
     * String raw = "  Hello@#$ World 12 34 !! \n \t ";==>HelloWorld1234
     */
    public static String removeAllClean(String input) {
        if (input == null) return "";
        return input
                .replaceAll("\\s+", "")          // 모든 공백 제거
                .replaceAll("[^a-zA-Z0-9]", ""); // 특수문자 제거
    }

    /**
     * 앞뒤 공백 + 중간 공백 + 줄바꿈 + 탭 제거
     * @param input 사용자 입력 문자열
     * @return 정리된 문자열
     * String raw = "  Hello@#$ World 12 34 !! \n \t ";==>Hello@#$World1234!!
     */
    public static String removeAllSpace(String input) {
        if (input == null) return "";
        return input.replaceAll("\\s+", "");
    }

    /**
     * 특수문자 제거 ("  Hello@#$ World 12 34 !! \n \t " → "HelloWorld1234")
     * 문자/숫자만 남김
     */
    public static String removeSpecChars(String input) {
        if (input == null) return "";
        return input.replaceAll("[^a-zA-Z0-9]", "");
    }

    /**
     * 영문 + 한글만 남기고 제거("홍길동abc123!@#" → "홍길동abc")
     */
    public static String strCharsOnly(String input) {
        if (input == null) return "";
        return input.replaceAll("[^A-Za-z가-힣]", "");
    }

    /**
     * 숫자만 남기고 모두 제거 (전화번호 등에서 사용)
     */
    public static String numbersOnly(String input) {
        if (input == null) return "";
        return input.replaceAll("[^0-9]", "");
    }

    /**
     * 영문자만 남기고 제거("abc123!@#" → "abc")
     */
    public static String strEngOnly(String input) {
        if (input == null) return "";
        return input.replaceAll("[^A-Za-z]", "");
    }

    /**
     * 한글만 남기고 제거("홍길동123abc!" → "홍길동")
     */
    public static String strKorOnly(String input) {
        if (input == null) return "";
        return input.replaceAll("[^가-힣]", "");
    }

    /**
     * null-safe trim 처리
     * @param input 문자열
     * @return null 이면 "", 아니면 trim
     * String raw = "  Hello@#$ World 12 34 !! \n \t ";
     * System.out.println(CustomUtils.safeTrim(raw)); ==>Hello@#$ World 12 34 !!
     */
    public static String safeTrim(String input) {
        return input == null ? "" : input.trim();
    }

    /**
     * HTML 태그 제거 (XSS 방지)
     */
    public static String stripHtmlTags(String input) {
        if (input == null) return "";
        return input.replaceAll("<[^>]*>", "");
    }

}
