package techpart.webpost.global.constant;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum Role {
    ADMIN,
    USER;

    @JsonCreator
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
