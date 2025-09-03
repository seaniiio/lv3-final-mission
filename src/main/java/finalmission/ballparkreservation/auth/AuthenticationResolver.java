package finalmission.ballparkreservation.auth;

import finalmission.ballparkreservation.auth.dto.LoginMember;
import finalmission.ballparkreservation.exception.customexception.UnauthenticatedException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Arrays;
import java.util.Objects;

@Component
@RequiredArgsConstructor
public class AuthenticationResolver implements HandlerMethodArgumentResolver {

    private final JwtProvider jwtProvider;

    @Override
    public boolean supportsParameter(final MethodParameter parameter) {
        return parameter.hasParameterAnnotation(MemberAuthentication.class);
    }

    @Override
    public LoginMember resolveArgument(final MethodParameter parameter, final ModelAndViewContainer mavContainer, final NativeWebRequest webRequest, final WebDataBinderFactory binderFactory) throws Exception {
        final String token = extractToken(webRequest);
        validateToken(token);

        String memberId = jwtProvider.getSubjectByToken(token);
        try {
            return LoginMember.createLoginMemberBySubject(memberId);
        } catch (NumberFormatException e) {
            throw new UnauthenticatedException();
        }
    }

    private String extractToken(final NativeWebRequest request) {
        final HttpServletRequest nativeRequest = request.getNativeRequest(HttpServletRequest.class);
        final Cookie[] cookies = nativeRequest.getCookies();
        validateExistsCookies(cookies);

        return Arrays.stream(cookies)
                .filter(cookie -> Objects.equals(cookie.getName(), "token"))
                .map(Cookie::getValue)
                .findAny()
                .orElseThrow(UnauthenticatedException::new);
    }

    private void validateToken(final String token) {
        if (!jwtProvider.isValidToken(token)) {
            throw new UnauthenticatedException();
        }
    }

    private void validateExistsCookies(final Cookie[] cookies) {
        if (cookies == null) {
            throw new UnauthenticatedException();
        }
    }
}
