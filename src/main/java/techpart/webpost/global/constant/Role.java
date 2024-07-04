package techpart.webpost.global.constant;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum Role {
    ROLE_ADMIN("ROLE_ADMIN"),
    ROLE_USER("ROLE_USER");

    private final String name;

    Role(String name) {
        this.name=name;
    }

//    @JsonCreator
    public static Role from(String role){
        for (Role value : Role.values()) {
            if(value.toString().equals(role)){
                return value;
            }
        }
        log.info("JsonCreator role error ={}",role);
        throw new IllegalArgumentException("");
    }
}
