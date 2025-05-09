package kr.sonss.lab.backend.user.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/*
    UserDTO 는 클라이언트에게 응답할 때 사용할 데이터 객체입니다. (Entity 와 유사하지만 응답 데이터를 제어하는 용도로 사용)

    DTO 사용:
        DTO 는 데이터베이스에서 가져온 데이터를 애플리케이션으로 전달하는 데 사용되는 객체입니다.
        엔티티와 달리, DTO 는 데이터베이스 구조와는 독립적입니다.
        따라서, 특정 쿼리 결과에 맞는 DTO 를 정의하여 사용하면, 애플리케이션 로직에 따라 데이터를 가공하여 사용할 수 있습니다.
    Entity 사용: (보통 JPA 사용시 구성)
        엔티티는 데이터베이스 테이블과 매핑되는 Java 클래스를 의미합니다.
        엔티티를 사용하면, MyBatis 가 쿼리 결과를 자동으로 엔티티 객체로 변환할 수 있습니다.
        또한, 엔티티에 대한 JPA (Java Persistence API)를 활용하여 데이터베이스 작업을 수행할 수도 있습니다.
    MyBatis 역할:
        MyBatis 는 SQL 쿼리를 실행하고 결과를 객체로 매핑하는 역할을 수행합니다.
        엔티티나 DTO 를 사용하여 쿼리 결과를 매핑할 수 있습니다.
    Lombok 라이브러리 :
        Lombok 은 Java 클래스에서 반복적으로 작성되는 코드를 자동으로 생성해주는 라이브러리입니다.
        예를 들어, Getter, Setter, 생성자, toString(), equals(), hashCode() 등의 메서드를
        어노테이션 기반으로 자동으로 생성하여 코드 양을 줄이고 가독성을 높일 수 있습니다
 */
@Getter
@Setter
public class UserDTO {
    private int seqId;
    private String userName;
    private String telNumber;
    private String email;
    private String userPwd;
    private String birthday;
    private String gender;
    private String genderNm;
    private String zipCode;
    private String address1;
    private String address2;
    private String memStatus;
    private String memStatusNm;
    private String memStatusBadge;
    private String loginYn;
    private LocalDateTime leaveDate;
    private LocalDateTime regDate;
    private String regId;
    private LocalDateTime updDate;
    private String updId;
}
