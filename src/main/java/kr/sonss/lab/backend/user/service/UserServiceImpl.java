package kr.sonss.lab.backend.user.service;

import kr.sonss.lab.backend.common.util.Aes256KeyProvider;
import kr.sonss.lab.backend.common.util.Aes256Util;
import kr.sonss.lab.backend.user.dto.UserDTO;
import kr.sonss.lab.backend.user.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
    UserServiceImpl 은 UserService 를 구현한 서비스 클래스입니다.
 */

@Service("userService")
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    private Aes256Util aes256Util;

    @Autowired
    private Aes256KeyProvider keyProvider;

    public UserServiceImpl(UserMapper mapper, PasswordEncoder passwordEncoder) {
        this.mapper = mapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDTO selectUserLogin(UserDTO data) {

        UserDTO user = mapper.selectUserLogin(data);
        if (user == null) {
            return null;
        }
        //DB에 저장된 사용자 비밀번호
        String userPwd_DB_Val = user.getUserPwd();
        //사용자가 입력한 비밀번호
        String userPwd_IN_Val = data.getUserPwd();

        if (passwordEncoder.matches(userPwd_IN_Val, userPwd_DB_Val)) {
            //passwordEncoder.matches(입력한_평문_비밀번호, DB에_저장된_암호문)
            return user;
        } else {
            //비밀번호 불일치
            log.info("############ 서비스임플 selectUserLogin 비밀번호 불일치 ");
            return null;
        }
    }

    @Override
    public int updateLogInOut(UserDTO data) {
        return mapper.updateLogInOut(data);
    }

    @Override
    public List<UserDTO> getUserList() {
        return mapper.findAll();
    }

    @Override
    public UserDTO selectUserInfo(int seqId) {
        UserDTO userDto = mapper.selectUserInfo(seqId);
        //생년월일 복호화
        try {
            String birthday = userDto.getBirthday();
            System.out.println("############ 생년월일 원문: " + birthday);
            if (birthday != null && birthday.length() > 9) {
                SecretKey key = keyProvider.getSecretKey();
                String decryptBirthday = aes256Util.decrypt(birthday, key);
                System.out.println("############ 복호화: " + decryptBirthday);
                userDto.setBirthday(decryptBirthday);
            }
        } catch (Exception e) {
            log.error("Birthday 복호화 오류", e);
            throw new RuntimeException(e);
        }

        return userDto;
    }

    @Override
    public int insertUser(UserDTO userDto) {

        String birthday = userDto.getBirthday();
        String gender = userDto.getGender();
        if ("M".equals(gender)) {
            userDto.setGender("1");
            if ("2".equals(birthday.substring(0, 1))) {
                userDto.setGender("3");
            }
        }

        if ("F".equals(gender)) {
            userDto.setGender("2");
            if ("2".equals(birthday.substring(0, 1))) {
                userDto.setGender("4");
            }
        }

        String userPwd = userDto.getUserPwd();
        if ("".equals(userPwd) || userPwd == "" || userPwd == null) {
            String telNumber = userDto.getTelNumber();
            userPwd = "!" + telNumber;
        }
        userPwd = passwordEncoder.encode(userPwd);

        String memStatus = userDto.getMemStatus();
        if ("".equals(memStatus) || memStatus == "" || memStatus == null) {
            userDto.setMemStatus("ACTIVE");
        }
        userDto.setUserPwd(userPwd);

        return mapper.insertUser(userDto);
    }

    @Override
    public int updateUser(UserDTO userDto) {

        String birthday = userDto.getBirthday();
        String lastRrnNo = birthday.substring(0, 1);
        String gender = userDto.getGender();
        if ("M".equals(gender)) {
            userDto.setGender("2".equals(lastRrnNo) ? "3" : "1");
        }
        if ("F".equals(gender)) {
            userDto.setGender("2".equals(lastRrnNo) ? "4" : "2");
        }

        //암호화 저장
        try {
            SecretKey key = keyProvider.getSecretKey();
            String encryptBirthday = aes256Util.encrypt(birthday, key);
            userDto.setBirthday(encryptBirthday);
            System.out.println("############ 생년월일 원문: " + birthday);
            System.out.println("############ 암호화: " + encryptBirthday);
        } catch (Exception e) {
            log.error("Birthday 암호화 오류", e);
            throw new RuntimeException(e);
        }

        return mapper.updateUser(userDto);
    }

    @Override
    public int deleteUser(int seqId) {
        return mapper.deleteUser(seqId);
    }

    @Override
    public int updateUserPwdReset(int seqId) {

        SecureRandom RANDOM = new SecureRandom();
        List<Character> passwordChars = new ArrayList<>();

        String upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lower = "abcdefghijklmnopqrstuvwxyz";
        String number = "0123456789";
        String special = "!@#$%^&*()-_=+[]{}|;:,.<>?/";

        int pwdMinLength = 12;  //최소 12자리
        String allChars = upper + lower + number + special;

        // 필수 구성 요소 각각 1개씩 보장 (각 1개만 add)
        passwordChars.add(upper.charAt(RANDOM.nextInt(upper.length())));
        passwordChars.add(lower.charAt(RANDOM.nextInt(lower.length())));
        passwordChars.add(number.charAt(RANDOM.nextInt(number.length())));
        passwordChars.add(special.charAt(RANDOM.nextInt(special.length())));

        // 나머지 자리수 채우기
        for (int i = 4; i < pwdMinLength; i++) {
            char randChar = allChars.charAt(RANDOM.nextInt(allChars.length()));
            passwordChars.add(randChar);
        }
        // 순서 무작위 섞기
        Collections.shuffle(passwordChars, RANDOM);
        // 최종 문자열로 변환
        StringBuilder password = new StringBuilder();
        for (char c : passwordChars) {
            password.append(c);
        }

        System.out.println("############ 서비스임플 updateUserPwdReset password.toString(): " + password.toString());

        //String encUserPwd = password.toString();
        String encUserPwd = passwordEncoder.encode("q1234567890-P");

        System.out.println("############ 서비스임플 updateUserPwdReset encUserPwd: " + encUserPwd);

        UserDTO userDto = new UserDTO();
        userDto.setSeqId(seqId);
        userDto.setUserPwd(encUserPwd);

        return mapper.updateUserPwdReset(userDto);
    }

    @Override
    public int selectEmailDupCheck(String email) {
        return mapper.selectEmailDupCheck(email);
    }

}
