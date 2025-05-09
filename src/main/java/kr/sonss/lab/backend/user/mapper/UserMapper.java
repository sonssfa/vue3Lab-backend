package kr.sonss.lab.backend.user.mapper;

import kr.sonss.lab.backend.user.dto.UserDTO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/*
    UserMapper 는 MyBatis 인터페이스로, 데이터베이스와 직접적인 연동을 담당합니다.
 */
@Mapper
public interface UserMapper {

    UserDTO selectUserLogin(UserDTO data);

    int updateLogInOut(UserDTO data);

    List<UserDTO> findAll();

    UserDTO selectUserInfo(int seqId);

    int insertUser(UserDTO userDto);

    int updateUser(UserDTO userDto);

    int deleteUser(int seqId);

    int updateUserPwdReset(UserDTO userDto);

    int selectEmailDupCheck(String email);
}
