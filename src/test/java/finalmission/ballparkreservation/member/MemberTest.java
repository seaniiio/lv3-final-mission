package finalmission.ballparkreservation.member;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

class MemberTest {

    @DisplayName("할인이 적용되는 연령대인지 확인할 수 있다.")
    @Test
    void checkAgeForDiscount() {
        // given
        Member member = new Member("may@gamil.com", "1234", "메이", 13);

        // when & then
        assertThat(member.isDiscountApply()).isTrue();
    }

    @DisplayName("비밀번호가 일치하는지 확인할 수 있다.")
    @Test
    void checkPasswordMatch() {
        // given
        Member member = new Member("may@gamil.com", "1234", "메이", 13);

        // when & then
        assertAll(
                () -> assertThat(member.isPasswordMatch("1234")).isTrue(),
                () -> assertThat(member.isPasswordMatch("abcd")).isFalse()
        );
    }
}
