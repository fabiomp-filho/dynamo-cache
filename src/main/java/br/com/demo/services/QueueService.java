package br.com.demo.services;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class QueueService {

    private final SqsClient sqsClient;

    public QueueService(SqsClient sqsClient) {
        this.sqsClient = sqsClient;
        criarFilaSeNaoExistir();
    }

    private final String NOME_FILA = "demo-queue";
    private String queueUrl;

    private void criarFilaSeNaoExistir() {
        try {
            GetQueueUrlResponse urlResponse = sqsClient.getQueueUrl(GetQueueUrlRequest.builder()
                    .queueName(NOME_FILA)
                    .build());
            queueUrl = urlResponse.queueUrl();
        } catch (QueueDoesNotExistException e) {
            CreateQueueResponse response = sqsClient.createQueue(CreateQueueRequest.builder()
                    .queueName(NOME_FILA)
                    .build());
            queueUrl = response.queueUrl();
        }
    }

    public void sendMessage(String message) {
        SendMessageRequest request = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody(message)
                .build();

        sqsClient.sendMessage(request);
    }

    public void deleteQueue(String queueName) {
        try {
            GetQueueUrlResponse urlResponse = sqsClient.getQueueUrl(
                    GetQueueUrlRequest.builder().queueName(queueName).build()
            );
            String queueUrl = urlResponse.queueUrl();

            sqsClient.deleteQueue(DeleteQueueRequest.builder().queueUrl(queueUrl).build());

        } catch (QueueDoesNotExistException e) {
            throw new RuntimeException("Fila '" + queueName + "' não existe.");
        }
    }

    public List<String> getQueueMessages(String name) {
        try {
            GetQueueUrlResponse urlResponse = sqsClient.getQueueUrl(
                    GetQueueUrlRequest.builder().queueName(name).build()
            );
            String queueUrl = urlResponse.queueUrl();

            ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .maxNumberOfMessages(10)
                    .waitTimeSeconds(1)
                    .build();

            List<Message> messages = sqsClient.receiveMessage(request).messages();

            for (Message message : messages) {
                sqsClient.deleteMessage(DeleteMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .receiptHandle(message.receiptHandle())
                        .build());
            }

            return messages.stream()
                    .map(Message::body)
                    .collect(Collectors.toList());

        } catch (QueueDoesNotExistException e) {
            throw new RuntimeException("Fila '" + name + "' não existe.");
        }
    }
}
