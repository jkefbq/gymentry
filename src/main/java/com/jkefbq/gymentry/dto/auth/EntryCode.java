package com.jkefbq.gymentry.dto.auth;

import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class EntryCode {
    @NotNull
    @Size(min = 6, max = 6, message = "строка должна содержать ровно 6 символов")
    private String code;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public EntryCode(String code) {
        this.code = code;
    }

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    public EntryCode(int code) {
        this.code = String.valueOf(code);
    }
}
