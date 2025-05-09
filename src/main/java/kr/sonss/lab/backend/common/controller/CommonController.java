package kr.sonss.lab.backend.common.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/*
    공통 콘트롤러
 */

@RestController
@RequestMapping("/common")
public class CommonController {

    /*
        로그인 세션 체크
    */
    @GetMapping("/sessionCheck")
    public ResponseEntity<?> loginCheck(HttpSession session) {
        String sessionId = session.getId();
        String seqId = (String) session.getAttribute("seqId");
        String email = (String) session.getAttribute("userEmail");
        String userName = (String) session.getAttribute("userName");
        if (email != null && sessionId != null) {
            return ResponseEntity.ok(Map.of("isLoggedIn", true, "sessionId", sessionId, "userName", userName, "seqId", seqId));
        } else {
            return ResponseEntity.ok(Map.of("isLoggedIn", false, "sessionId", "", "userName", "", "seqId", ""));
        }
    }

}
