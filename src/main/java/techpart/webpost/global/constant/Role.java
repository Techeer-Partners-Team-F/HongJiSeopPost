package techpart.webpost.global.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
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

    @JsonCreator
    public static Role parsing(String inputValue) {
        for (Role role : Role.values()) {
            if (role.name.equalsIgnoreCase(inputValue)) {
                return role;
            }
        }
        log.error("No matching role for input value: {}", inputValue);
        return null;
    }
}
