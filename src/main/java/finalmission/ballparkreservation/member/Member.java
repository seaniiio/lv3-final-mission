package finalmission.ballparkreservation.member;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String password;
    private String name;
    private int age;

    public Member(final String email, final String password, final String name, final int age) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.age = age;
    }

    public boolean isDiscountApply() {
        return age <= 19 || age >= 60;
    }

    public boolean isPasswordMatch(final String password) {
        return this.password.equals(password);
    }
}
