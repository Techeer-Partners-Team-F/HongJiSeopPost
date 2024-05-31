package techpart.webpost.global.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
@RequiredArgsConstructor
public enum Role {
    ADMIN("ADMIN"),
    USER("USER");

    private final String name;
}
