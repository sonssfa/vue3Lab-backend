package kr.sonss.lab.backend.user.service;

import kr.sonss.lab.backend.user.dto.UserDTO;

import java.util.List;

/*
    UserService 는 비즈니스 로직을 처리하는 서비스 계층입니다.
 */
public interface UserService {

    UserDTO selectUserLogin(UserDTO data);

    int updateLogInOut(UserDTO data);

    List<UserDTO> getUserList();

    UserDTO selectUserInfo(int seqId);

    int insertUser(UserDTO userDto);

    int updateUser(UserDTO userDto);

    int deleteUser(int seqId);

    int updateUserPwdReset(int seqId);

    int selectEmailDupCheck(String email);


}
