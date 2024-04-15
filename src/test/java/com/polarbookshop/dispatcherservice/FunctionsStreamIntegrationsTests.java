package com.polarbookshop.dispatcherservice;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.InputDestination;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.cloud.stream.binder.test.TestChannelBinderConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;

@SpringBootTest
@Import(TestChannelBinderConfiguration.class)
public class FunctionsStreamIntegrationsTests {
    @Autowired
    private InputDestination input; // 입력 바인딩 packlable-in-0 을 나타냄

    @Autowired
    private OutputDestination output;

    @Autowired
    private ObjectMapper objectMapper; // 자바 객체로 역질렬화하기 위해 사용

    @Test
    void whenOrderAcceptedThenDispatched() throws IOException {
        long orderId = 123;
        Message<OrderAcceptedMessage> inputMessage = MessageBuilder
                .withPayload(new OrderAcceptedMessage(orderId))
                .build();
        Message<OrderDispatchedMessage> outputMessage = MessageBuilder
                .withPayload(new OrderDispatchedMessage(orderId))
                .build();

        this.input.send(inputMessage); // 입력 채널로 메시지를 보낸다
        assertThat(objectMapper.readValue(output.receive().getPayload(),
                OrderDispatchedMessage.class))
                .isEqualTo(outputMessage.getPayload()); // 출력 채널에서 메시지를 받아서 확인
    }
}
