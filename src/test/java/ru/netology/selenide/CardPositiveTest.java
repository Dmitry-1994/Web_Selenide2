package ru.netology.selenide;

import com.codeborne.selenide.Configuration;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class CardPositiveTest {
    private String getCurrectDate(int countDay, String format) {
        return LocalDate.now().plusDays(countDay).format(DateTimeFormatter.ofPattern(format));
    }

    private String getLocalDate(String format) {
        return LocalDate.now().format(DateTimeFormatter.ofPattern(format));
    }

    @ParameterizedTest
    @CsvSource({
            "Ек, Екатеринбург, 7, Дмитрий, +79122518775",
            "мо, Москва, 7, Дмитрий Тарасов, +12345678910",
            "пе, Санкт-Петербург, 7, Дмитрий-Тарасов, +00000000000",
            "на, Ростов-на-Дону, 7, Дмитрий-Тарасов Алексеевич, +99999999999"
    })
    void shouldRegisterAccount(String cityShort, String cityFull, int correctDate, String name, String phone) {
        String currentDay = getCurrectDate(correctDate, "dd");
        //Configuration.holdBrowserOpen = true;
        open("http://localhost:9999/");
        $("[data-test-id=city] input").setValue(cityShort);
        $x("//*[@class='menu-item__control'][contains(text(), '" + cityFull + "')]")
                .shouldBe(visible)
                .click();

        $("[data-test-id=date] .icon").click();
        if (getLocalDate("yyyy").equals(getCurrectDate(correctDate, "yyyy"))) {
            if (getLocalDate("MM").equals(getCurrectDate(correctDate, "MM"))) {
                $x("//*[@class='calendar__day'][contains(text(), '" + currentDay + "')]")
                        .shouldBe(visible)
                        .click();
            } else if (!getLocalDate("MM").equals(getCurrectDate(correctDate, "MM"))) {
                $(".calendar__arrow[data-step='1']").click();
                $x("//*[@class='calendar__day'][contains(text(), '" + currentDay + "')]")
                        .shouldBe(visible)
                        .click();
            }
        } else {
            $(".calendar__arrow[data-step='12']").click();
            $x("//*[@class='calendar__day'][contains(text(), '" + currentDay + "')]")
                    .shouldBe(visible)
                    .click();
        }

        $("[data-test-id=name] input").setValue(name);
        $("[data-test-id=phone] input").setValue(phone);
        $(".checkbox__box").click();
        $(".button__text").click();
        $("[data-test-id=notification]").shouldBe(visible, Duration.ofSeconds(15));
        $("[data-test-id=notification] .notification__title").shouldHave(text("Успешно!"));
        $("[data-test-id=notification] .notification__content").shouldHave(text("Встреча успешно забронирована на " + currentDay));
    }
}