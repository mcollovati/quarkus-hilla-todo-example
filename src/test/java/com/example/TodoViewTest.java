package com.example;

import java.lang.management.ManagementFactory;
import java.time.Duration;
import java.util.List;
import java.util.function.Supplier;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.junit5.BrowserPerTestStrategyExtension;
import io.quarkus.test.TestTransaction;
import io.quarkus.test.common.http.TestHTTPResource;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.codeborne.selenide.CollectionCondition.anyMatch;
import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.CollectionCondition.sizeGreaterThanOrEqual;
import static com.codeborne.selenide.Condition.checked;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.Condition.not;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.shadowCss;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;


@QuarkusTest
@TestTransaction
@ExtendWith({BrowserPerTestStrategyExtension.class})
class TodoViewTest {

    @TestHTTPResource()
    private String baseURL;

    @BeforeEach
    void setup() {
        Configuration.headless = runHeadless();
        System.setProperty("chromeoptions.args", "--remote-allow-origins=*");
    }

    @Test
    void rootPath_todoViewDisplayed() {
        openAndWait(() -> $("div.todos"));
        $("vaadin-text-field").shouldBe(visible);
        $$("vaadin-button").filter(Condition.text("Add"))
                .first().shouldBe(visible);
        $$("div.todos div.todo vaadin-checkbox")
                .shouldHave(size(4));
    }

    @Test
    void todoView_addTask() {
        openAndWait(() -> $("div.todos"));

        SelenideElement textField = $("vaadin-text-field").shouldBe(visible);
        SelenideElement button = $$("vaadin-button")
                .filter(Condition.text("Add"))
                .first().shouldBe(visible);

        ElementsCollection tasks = $$("div.todos div.todo")
                .shouldHave(sizeGreaterThanOrEqual(4));
        int currentTasks = tasks.size();

        String itemText = "Test if adding items works";
        textField.$("input").setValue(itemText);
        button.click();

        tasks.shouldHave(size(currentTasks + 1));
        tasks.get(currentTasks).$("span").shouldHave(exactText(itemText));
        tasks.get(currentTasks).$("input[type=checkbox]")
                .shouldNotBe(checked);
    }

    @Test
    void todoView_markTaskAsCompleted() {
        openAndWait(() -> $("div.todos"));

        ElementsCollection tasks = $$("div.todos div.todo")
                .shouldHave(sizeGreaterThanOrEqual(4))
                .shouldHave(anyMatch("Expected at least one item not to be checked",
                        el -> $(el).$("input[type=checkbox]").is(not(checked))));

        List<SelenideElement> uncheckedItems = tasks.asFixedIterable().stream()
                .filter(el -> el.$("input[type=checkbox]").is(not(checked)))
                .toList();
        uncheckedItems.forEach(item -> item.$("vaadin-checkbox").click());

        tasks.asFixedIterable().forEach(el ->
                el.$("input[type=checkbox]").shouldBe(checked));

        // Reload page and verify all items are checked
        openAndWait(() -> $("div.todos"));
        $$("div.todos div.todo")
                .asFixedIterable().forEach(el ->
                        el.$("input[type=checkbox]").shouldBe(checked));
    }


    protected void openAndWait(Supplier<SelenideElement> selector) {
        openAndWait(baseURL, selector);
    }

    protected void openAndWait(String url, Supplier<SelenideElement> selector) {
        Selenide.open(url);
        waitForDevServer();
        selector.get().shouldBe(Condition.visible, Duration.ofSeconds(10));
        $(shadowCss("div.dev-tools.error", "vaadin-dev-tools")).shouldNot(Condition.exist);
        $(shadowCss("main", "vite-plugin-checker-error-overlay")).shouldNot(Condition.exist);
    }

    protected void waitForDevServer() {
        Selenide.Wait()
                .withTimeout(Duration.ofMinutes(20))
                .until(d -> !Boolean.TRUE.equals(Selenide.executeJavaScript(
                        "return window.Vaadin && window.Vaadin.Flow && window.Vaadin.Flow.devServerIsNotLoaded;")));
    }

    protected boolean runHeadless() {
        return !isJavaInDebugMode();
    }

    static boolean isJavaInDebugMode() {
        return ManagementFactory.getRuntimeMXBean()
                .getInputArguments()
                .toString()
                .contains("jdwp");
    }

}
