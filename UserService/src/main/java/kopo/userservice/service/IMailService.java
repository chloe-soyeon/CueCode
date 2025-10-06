package kopo.userservice.service;

import kopo.userservice.dto.MailDTO;

public interface IMailService {
    // 메일 발송
    int doSendMail(MailDTO pDTO);
}
