package makeus.cmc.malmo.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 채팅 메시지를 문장 단위로 분할하고 그룹화하는 유틸리티 클래스
 */
public class ChatMessageSplitter {

    /**
     * 문장 종결 부호 패턴 (규칙 변경 시 이 값만 수정하면 됨)
     */
    private static final Pattern SENTENCE_END_PATTERN = Pattern.compile("[.!?]");

    /**
     * 그룹당 문장 수 (규칙 변경 시 이 값만 수정하면 됨)
     */
    private static final int SENTENCES_PER_GROUP = 3;

    /**
     * 텍스트를 문장부호 기준으로 문장 단위로 분할합니다.
     * 문장부호(. ! ?) 하나만 나와도 문장 종결로 간주합니다.
     *
     * @param text 분할할 텍스트
     * @return 문장 단위로 분할된 리스트 (문장부호 포함)
     */
    public static List<String> splitIntoSentences(String text) {
        if (text == null || text.trim().isEmpty()) {
            return new ArrayList<>();
        }

        List<String> sentences = new ArrayList<>();
        Matcher matcher = SENTENCE_END_PATTERN.matcher(text);
        
        int lastIndex = 0;
        while (matcher.find()) {
            int endIndex = matcher.end();
            String sentence = text.substring(lastIndex, endIndex);
            // 첫 번째 문장만 앞뒤 공백 제거, 나머지는 앞 공백 유지
            if (sentences.isEmpty()) {
                sentence = sentence.trim();
            } else {
                // 앞 공백은 유지하되, 전체가 공백만 있는 경우는 제외
                sentence = sentence.trim().isEmpty() ? sentence : sentence;
            }
            if (!sentence.trim().isEmpty()) {
                sentences.add(sentence);
            }
            lastIndex = endIndex;
        }

        // 마지막 문장부호 이후의 텍스트 처리
        if (lastIndex < text.length()) {
            String remaining = text.substring(lastIndex);
            if (!remaining.trim().isEmpty()) {
                // 첫 번째 문장이 아니면 앞 공백 유지
                if (!sentences.isEmpty()) {
                    sentences.add(remaining);
                } else {
                    sentences.add(remaining.trim());
                }
            }
        }

        return sentences;
    }

    /**
     * 텍스트를 문장 단위로 분할한 후, 세 문장씩 그룹화합니다.
     * 예: 10문장인 경우 4개의 그룹으로 생성됩니다.
     *
     * @param text 그룹화할 텍스트
     * @return 세 문장씩 그룹화된 텍스트 리스트
     */
    public static List<String> splitIntoGroups(String text) {
        List<String> sentences = splitIntoSentences(text);
        
        if (sentences.isEmpty()) {
            return new ArrayList<>();
        }

        List<String> groups = new ArrayList<>();
        StringBuilder currentGroup = new StringBuilder();

        for (int i = 0; i < sentences.size(); i++) {
            if (i > 0 && i % SENTENCES_PER_GROUP == 0) {
                // 세 문장이 모였으면 그룹 완성
                groups.add(currentGroup.toString());
                currentGroup = new StringBuilder();
            }

            // sentences는 이미 적절한 공백을 포함하고 있으므로 그대로 추가
            currentGroup.append(sentences.get(i));
        }

        // 마지막 남은 문장들 처리
        if (currentGroup.length() > 0) {
            groups.add(currentGroup.toString());
        }

        return groups;
    }
}
