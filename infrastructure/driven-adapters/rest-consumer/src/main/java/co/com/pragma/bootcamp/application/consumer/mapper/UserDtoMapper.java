package co.com.pragma.bootcamp.application.consumer.mapper;


import co.com.pragma.bootcamp.application.consumer.dto.UserRegistrationResponse;
import co.com.pragma.bootcamp.application.model.user.User;

public class UserDtoMapper {

    public static User toUser(UserRegistrationResponse response) {
        return User.builder()
                .id(response.id())
                .firstName(response.firstName())
                .lastName(response.lastName())
                .birthDate(response.birthDate())
                .address(response.address())
                .phone(response.phone())
                .identificationNumber(response.identificationNumber())
                .email(response.email())
                .baseSalary(response.baseSalary())
                .build();
    }
}
