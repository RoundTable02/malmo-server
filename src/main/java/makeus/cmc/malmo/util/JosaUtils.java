package makeus.cmc.malmo.util;

import java.util.HashSet;
import java.util.Set;

public class JosaUtils {

    // 받침처럼 간주할 영어 알파벳
    private static final Set<Character> EN_NUM_CONSONANT_LIKE = new HashSet<>();

    static {
        for (char c : "bcgklmnprtxzBCGKLMNPRTXZ013678".toCharArray()) {
            EN_NUM_CONSONANT_LIKE.add(c);
        }
    }

    /**
     * 조사 자동 선택
     * @param word 대상 단어
     * @param josaWith 받침 있는 경우 조사 (예: 을, 은, 이)
     * @param josaWithout 받침 없는 경우 조사 (예: 를, 는, 가)
     * @return 단어 + 조사
     */
    public static String appendJosa(String word, String josaWith, String josaWithout) {
        if (word == null || word.isEmpty()) return "";

        char lastChar = word.charAt(word.length() - 1);

        boolean hasBatchim = false;

        if (isHangul(lastChar)) {
            int code = lastChar - 0xAC00;
            int jongseong = code % 28;
            hasBatchim = jongseong != 0;
        } else {
            hasBatchim = EN_NUM_CONSONANT_LIKE.contains(lastChar);
        }

        return word + (hasBatchim ? josaWith : josaWithout);
    }

    private static boolean isHangul(char ch) {
        return ch >= 0xAC00 && ch <= 0xD7A3;
    }

    // 조사 편의 메서드
    public static String 아야(String word) {
        return appendJosa(word, "아", "야");
    }
}

