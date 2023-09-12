package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import org.springframework.core.io.ClassPathResource;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.time.LocalDateTime;
import java.time.Month;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class BookingDtoJsonTest {

    @Autowired
    JacksonTester<BookingDto> jacksonTester;

    @Test
    void serializationTest() throws Exception {

        BookingDto bookingDto = new BookingDto(LocalDateTime.of(2023, Month.SEPTEMBER, 7, 0, 0),
                LocalDateTime.of(2023, Month.SEPTEMBER, 8, 0, 0),
                1L);

        JsonContent<BookingDto> jsonContent = jacksonTester.write(bookingDto);

        assertThat(jsonContent).extractingJsonPathStringValue("$.start").isEqualTo("2023-09-07T00:00:00");
        assertThat(jsonContent).extractingJsonPathStringValue("$.end").isEqualTo("2023-09-08T00:00:00");
        assertThat(jsonContent).extractingJsonPathNumberValue("$.itemId").isEqualTo(1);
        assertThat(jsonContent).isEqualToJson(new ClassPathResource("bookingDto.json"));
    }

    @Test
    void deserializationTest() throws Exception {
        BookingDto bookingDto = jacksonTester.read(new ClassPathResource("bookingDto.json")).getObject();

        assertThat(bookingDto.getStart()).isEqualTo("2023-09-07T00:00:00");
        assertThat(bookingDto.getEnd()).isEqualTo("2023-09-08T00:00:00");
        assertThat(bookingDto.getItemId()).isEqualTo(1L);

    }
}
