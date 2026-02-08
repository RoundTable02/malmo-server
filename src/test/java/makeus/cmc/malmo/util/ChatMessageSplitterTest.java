package makeus.cmc.malmo.util;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ChatMessageSplitter 클래스 테스트")
class ChatMessageSplitterTest {

    @Test
    @DisplayName("문장부호 기준으로 문장을 분할한다")
    void splitIntoSentences_문장부호_기준_분할() {
        // given
        String text = "첫 번째 문장입니다. 두 번째 문장입니다! 세 번째 문장입니다?";

        // when
        List<String> sentences = ChatMessageSplitter.splitIntoSentences(text);

        // then
        assertThat(sentences).hasSize(3);
        assertThat(sentences.get(0)).isEqualTo("첫 번째 문장입니다.");
        assertThat(sentences.get(1)).isEqualTo(" 두 번째 문장입니다!");
        assertThat(sentences.get(2)).isEqualTo(" 세 번째 문장입니다?");
    }

    @Test
    @DisplayName("문장부호 하나만 나와도 문장 종결로 간주한다")
    void splitIntoSentences_문장부호_하나만_나와도_종결() {
        // given
        String text = "문장입니다. 또 다른 문장! 마지막 문장?";

        // when
        List<String> sentences = ChatMessageSplitter.splitIntoSentences(text);

        // then
        assertThat(sentences).hasSize(3);
    }

    @Test
    @DisplayName("세 문장씩 그룹화한다")
    void splitIntoGroups_세_문장씩_그룹화() {
        // given
        String text = "문장1. 문장2! 문장3? 문장4. 문장5! 문장6? 문장7.";

        // when
        List<String> groups = ChatMessageSplitter.splitIntoGroups(text);

        // then
        assertThat(groups).hasSize(3);
        assertThat(groups.get(0)).isEqualTo("문장1. 문장2! 문장3?");
        assertThat(groups.get(1)).isEqualTo(" 문장4. 문장5! 문장6?");
        assertThat(groups.get(2)).isEqualTo(" 문장7.");
    }

    @Test
    @DisplayName("10문장인 경우 4개의 그룹으로 생성한다")
    void splitIntoGroups_10문장_4개_그룹() {
        // given
        String text = "1. 2! 3? 4. 5! 6? 7. 8! 9? 10.";

        // when
        List<String> groups = ChatMessageSplitter.splitIntoGroups(text);

        // then
        assertThat(groups).hasSize(4);
        assertThat(groups.get(0)).isEqualTo("1. 2! 3?");
        assertThat(groups.get(1)).isEqualTo(" 4. 5! 6?");
        assertThat(groups.get(2)).isEqualTo(" 7. 8! 9?");
        assertThat(groups.get(3)).isEqualTo(" 10.");
    }

    @Test
    @DisplayName("정확히 3의 배수 문장인 경우 그룹화한다")
    void splitIntoGroups_3의_배수_문장() {
        // given
        String text = "문장1. 문장2! 문장3? 문장4. 문장5! 문장6?";

        // when
        List<String> groups = ChatMessageSplitter.splitIntoGroups(text);

        // then
        assertThat(groups).hasSize(2);
        assertThat(groups.get(0)).isEqualTo("문장1. 문장2! 문장3?");
        assertThat(groups.get(1)).isEqualTo(" 문장4. 문장5! 문장6?");
    }

    @Test
    @DisplayName("문장이 1개인 경우 1개 그룹으로 생성한다")
    void splitIntoGroups_문장_1개() {
        // given
        String text = "단일 문장입니다.";

        // when
        List<String> groups = ChatMessageSplitter.splitIntoGroups(text);

        // then
        assertThat(groups).hasSize(1);
        assertThat(groups.get(0)).isEqualTo("단일 문장입니다.");
    }

    @Test
    @DisplayName("문장이 2개인 경우 1개 그룹으로 생성한다")
    void splitIntoGroups_문장_2개() {
        // given
        String text = "첫 번째 문장. 두 번째 문장!";

        // when
        List<String> groups = ChatMessageSplitter.splitIntoGroups(text);

        // then
        assertThat(groups).hasSize(1);
        assertThat(groups.get(0)).isEqualTo("첫 번째 문장. 두 번째 문장!");
    }

    @Test
    @DisplayName("문장부호가 없는 경우 전체를 하나의 그룹으로 처리한다")
    void splitIntoGroups_문장부호_없음() {
        // given
        String text = "문장부호가 없는 텍스트입니다";

        // when
        List<String> groups = ChatMessageSplitter.splitIntoGroups(text);

        // then
        assertThat(groups).hasSize(1);
        assertThat(groups.get(0)).isEqualTo("문장부호가 없는 텍스트입니다");
    }

    @Test
    @DisplayName("빈 문자열인 경우 빈 리스트를 반환한다")
    void splitIntoGroups_빈_문자열() {
        // given
        String text = "";

        // when
        List<String> groups = ChatMessageSplitter.splitIntoGroups(text);

        // then
        assertThat(groups).isEmpty();
    }

    @Test
    @DisplayName("공백만 있는 경우 빈 리스트를 반환한다")
    void splitIntoGroups_공백만_있음() {
        // given
        String text = "   ";

        // when
        List<String> groups = ChatMessageSplitter.splitIntoGroups(text);

        // then
        assertThat(groups).isEmpty();
    }

    @Test
    @DisplayName("다양한 문장부호 조합을 처리한다")
    void splitIntoGroups_다양한_문장부호_조합() {
        // given
        String text = "문장1. 문장2! 문장3? 문장4. 문장5!";

        // when
        List<String> groups = ChatMessageSplitter.splitIntoGroups(text);

        // then
        assertThat(groups).hasSize(2);
        assertThat(groups.get(0)).isEqualTo("문장1. 문장2! 문장3?");
        assertThat(groups.get(1)).isEqualTo(" 문장4. 문장5!");
    }

    @Test
    @DisplayName("문장부호 뒤에 공백이 없는 경우도 처리한다")
    void splitIntoGroups_문장부호_뒤_공백_없음() {
        // given
        String text = "문장1.문장2!문장3?";

        // when
        List<String> groups = ChatMessageSplitter.splitIntoGroups(text);

        // then
        assertThat(groups).hasSize(1);
        assertThat(groups.get(0)).contains("문장1.");
        assertThat(groups.get(0)).contains("문장2!");
        assertThat(groups.get(0)).contains("문장3?");
    }

    @Test
    @DisplayName("긴 텍스트도 올바르게 처리한다")
    void splitIntoGroups_긴_텍스트() {
        // given
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= 15; i++) {
            sb.append("문장").append(i).append(".");
            if (i < 15) {
                sb.append(" ");
            }
        }
        String text = sb.toString();

        // when
        List<String> groups = ChatMessageSplitter.splitIntoGroups(text);

        // then
        assertThat(groups).hasSize(5); // 15문장 / 3 = 5그룹
    }
}
