package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.core.io.ClassPathResource;
import ru.practicum.shareit.user.dto.UserDto;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoJsonTest {

    @Autowired
    JacksonTester<UserDto> jacksonTester;

    @Test
    void serializationTest() throws Exception {
        UserDto userDto = new UserDto("user", "user@mail");

        JsonContent<UserDto> jsonContent = jacksonTester.write(userDto);

        assertThat(jsonContent).extractingJsonPathStringValue("$.name").isEqualTo("user");
        assertThat(jsonContent).extractingJsonPathStringValue("$.email").isEqualTo("user@mail");
    }

    @Test
    void deserializationTest() throws Exception {
        UserDto userDto = jacksonTester.read(new ClassPathResource("userDto.json")).getObject();

        assertThat(userDto.getName()).isEqualTo("user");
        assertThat(userDto.getEmail()).isEqualTo("user@mail");
    }
}
