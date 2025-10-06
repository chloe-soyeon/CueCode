package kopo.userservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MailDTO {

    private String toMail;    // 받는 사람
    private String title;     // 메일 제목
    private String contents;  // 메일 내용
}
