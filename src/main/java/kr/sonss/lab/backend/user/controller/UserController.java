package kr.sonss.lab.backend.user.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import kr.sonss.lab.backend.user.dto.UserDTO;
import kr.sonss.lab.backend.user.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/*
    UserController 는 API 요청을 처리하고 클라이언트에게 응답을 반환합니다.
 */
@RestController
@RequestMapping("/user")
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @GetMapping("/sessionCheck")
    public ResponseEntity<?> loginCheck(HttpSession session) {
        String email = (String) session.getAttribute("userEmail");
        if (email != null) {
            return ResponseEntity.ok(Map.of("login", true, "email", email));
        } else {
            return ResponseEntity.ok(Map.of("login", false));
        }
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody Map<String, String> params,
                                HttpServletResponse res, HttpServletRequest req, HttpSession session) {

        String email = StringUtils.defaultIfBlank(params.get("email"), "");
        String password = StringUtils.defaultIfBlank(params.get("password"), "");
        String rememberMe = params.get("rememberMe");

        //StringUtils.defaultString(str);	//null만	null이면 ""
        //StringUtils.defaultIfEmpty(str, def);	//null, ""	빈 문자열까지 대체
        //StringUtils.defaultIfBlank(str, def);	//null, "", " "	공백 포함까지 대체 ← 가장 강력

        UserDTO data = new UserDTO();
        data.setEmail(email);
        data.setUserPwd(password);

        try {
            String resultMsg = "";
            Map<String, String> userMap = new HashMap<>();

            UserDTO userDto = new UserDTO();
            userDto = userService.selectUserLogin(data);

            if (userDto == null) {
                System.out.println(">>>>>> 일치하는 회원가입 정보가 없습니다.");
                resultMsg = "일치하는 회원가입 정보가 없습니다.";

                userMap.put("userName", "");
                userMap.put("memStatus", "");
                userMap.put("resultMsg", resultMsg);
                return ResponseEntity.ok(Map.of("login", false, "userMap", userMap));
            } else {
                int seqId = userDto.getSeqId();
                String memStatus = Objects.requireNonNull(userDto).getMemStatus();
                String loginYn = Objects.toString(userDto.getLoginYn(), "");

                log.info(">>>>>> login userDto.getUserName(): " + userDto.getUserName());
                log.info(">>>>>> login memStatus: " + memStatus);
                log.info(">>>>>> login loginYn: " + loginYn);

                if ("Y".equals(loginYn)) {
                    //memStatus = "0";
                    //resultMsg = "다른 기기에서 로그인 중입니다. \n이전 연결을 끊고 다시 로그인할까요?";
                }

                if ("BLOCKED".equals(memStatus)) {
                    resultMsg = "로그인이 잠겨있는 계정입니다.\n고객센터로 문의하여 주시기 바랍니다.";
                } else if ("WITHDRAWN".equals(memStatus)) {//탈퇴회원
                    resultMsg = "회원 가입 정보가 없습니다. ";
                }

                if (("ACTIVE").equals(memStatus) || ("DANGER").equals(memStatus) || ("WARNING").equals(memStatus)) {
                    memStatus = "ACTIVE";
                    resultMsg = "로그인 되었습니다. ";
                    //세션 할당
                    String userName = userDto.getUserName();

                    session.setAttribute("seqId", String.valueOf(seqId));
                    session.setAttribute("userEmail", email);
                    session.setAttribute("userName", userName);

                    data.setSeqId(seqId);
                    data.setEmail(email);
                    data.setLoginYn("Y");

                    int result = userService.updateLogInOut(data);
                    System.out.println(">>>>>> 회원 업데이트 result : " + result);
                }

                if ("true".equals(rememberMe)) {
                    //쿠키설정
                    Cookie cookie = new Cookie("rememberMe", email);
                    cookie.setPath("/");
                    cookie.setHttpOnly(false);  // Vue에서 JS로 읽을 수 있도록
                    cookie.setMaxAge(7 * 24 * 60 * 60);//7일간 유지
                    res.addCookie(cookie);

                    System.out.println(">>>>>> 쿠키 설정: " + email);
                } else {
                    //미체크시 기존 쿠키 삭제
                    Cookie cookie = new Cookie("rememberMe", null);
                    cookie.setPath("/");
                    cookie.setMaxAge(0);
                    cookie.setHttpOnly(false);
                    res.addCookie(cookie);

                    System.out.println(">>>>>> 쿠키 삭제: " + email);
                }

                Cookie[] cookies = req.getCookies();
                if (cookies != null) {
                    for (Cookie cookie : cookies) {
                        if ("rememberMe".equals(cookie.getName())) {
                            System.out.println(">>>>>> 쿠키: " + cookie.getValue() + ", cookie.getName: " + cookie.getName());
                        }
                    }
                }

                userMap.put("userName", userDto.getUserName());
                userMap.put("memStatus", memStatus);
                userMap.put("resultMsg", resultMsg);

                //응답 바디에 id를 담아서 HTTP 200 OK로 응답
                //즉, 클라이언트가 userMap 값을 응답 본문에서 받을 수 있음
                //return ResponseEntity.ok(userMap);
                return ResponseEntity.ok(Map.of("login", true, "userMap", userMap));
            }
        } catch (Exception e) {
            //throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("login", false));
        }
    }

    @GetMapping("/logout")
    public ResponseEntity logout(HttpSession session) {

        UserDTO userDto = new UserDTO();

        String email = (String) session.getAttribute("userEmail");
        System.out.println("=============== > Controller logout email " + email);

        String seqIdStr = (String) session.getAttribute("seqIdStr");
        int seqId = Integer.parseInt(seqIdStr);
        System.out.println("=============== > Controller logout seqId " + seqId);

        userDto.setSeqId(seqId);
        userDto.setEmail(email);
        userDto.setLoginYn("N");

        try {
            int result = userService.updateLogInOut(userDto);
            session.invalidate(); // 세션 전체 제거
            return ResponseEntity.ok(Map.of("logout", true));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("logout", false));
        }
    }

    @GetMapping("/userList")
    public List<UserDTO> getAllUsers() {

        System.out.println("########### getAllUsers");

        List<UserDTO> userlist = userService.getUserList();

        int listCnt = userlist.size();

        log.info("@@@@@@@@@@@@@ listCnt:{}", listCnt);
        System.out.println("############ userlist.toString: " + userlist.get(0).getUserName());

        return userlist;
    }

    @GetMapping("/userInfo")
    public ResponseEntity<?> userInfo(@RequestParam("seqId") int seqId) {
        //public ResponseEntity<?> userInfo(...) 리턴 타입이 무엇이든 될 수 있다. 조금 더 명확하게 "타입 미정" 표현
        //public ResponseEntity userInfo(...)    타입 명시 안 함. 컴파일러가 알아서 Object로 처리
        //public ResponseEntity<Map<String, Object>> userInfo(...) Map 타입 명시, 코드 읽는 사람이 더 이해하기 쉽다.

        try {
            UserDTO data = userService.selectUserInfo(seqId);
            return ResponseEntity.ok(Map.of("resultYn", "Y", "userInfo", data));
        } catch (Exception e) {
            e.printStackTrace(); // 에러 원인 콘솔에 출력
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("resultYn", "N", "error", e.getMessage()));
        }
    }

    @PostMapping("/userSave")
    public ResponseEntity userSave(@RequestBody UserDTO userDto) {
        String resultYn = "N";
        int seqId = userDto.getSeqId();
        try {
            int result = 0;
            if (seqId > 0) {
                result = userService.updateUser(userDto);
            } else {
                result = userService.insertUser(userDto);
            }

            if (result == 1) {
                resultYn = "Y";
            }

            return ResponseEntity.ok(Map.of("resultYn", resultYn));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("resultYn", resultYn, "error", e.getMessage()));
        }
    }

    @GetMapping("/userDelete")
    public ResponseEntity<?> deleteUser(@RequestParam("seqId") int seqId) {
        String resultYn = "N";
        try {
            int result = userService.deleteUser(seqId);
            if (result == 1) {
                resultYn = "Y";
            }
            return ResponseEntity.ok(Map.of("resultYn", resultYn));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("resultYn", resultYn, "error", e.getMessage()));
        }
    }

    //사용자 비밀번호 초기화
    @GetMapping("/userPwdReset")
    public ResponseEntity<?> updateUserPwdReset(@RequestParam("seqId") int seqId) {
        String resultYn = "N";
        try {
            int result = userService.updateUserPwdReset(seqId);
            if (result == 1) {
                resultYn = "Y";
            }
            return ResponseEntity.ok(Map.of("resultYn", "Y"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("resultYn", resultYn, "error", e.getMessage()));
        }
    }

    //이메일 중복체크
    @GetMapping("emailDupCheck")
    public ResponseEntity<?> selectEmailDupCheck(@RequestParam("email") String email) {
        try {
            int result = userService.selectEmailDupCheck(email);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
