package com.example.bookorderservice.service.impl;

import com.example.bookorderservice.AuthUserServiceGrpc;
import com.example.bookorderservice.controller.request.BookOrderCreateRequest;
import com.example.bookorderservice.controller.request.BookOrderUpdateRequest;
import com.example.bookorderservice.enums.BookOrderStatus;
import com.example.bookorderservice.mapper.BookOrderMapper;
import com.example.bookorderservice.model.BookOrderModel;
import com.example.bookorderservice.repository.BookOrderRepository;
import com.example.bookorderservice.repository.entity.BookOrderEntity;
import com.example.bookorderservice.service.BookOrderService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import java.util.List;
import static java.lang.String.format;
import net.devh.boot.grpc.client.inject.GrpcClient;

@Service
@RequiredArgsConstructor
public class BookOrderServiceImpl implements BookOrderService {

    private final BookOrderRepository bookOrderRepository;
    private final BookOrderMapper bookOrderMapper;
    private final WebClient.Builder webClientBuilder;

    @GrpcClient("authUserService")
    private final AuthUserServiceGrpc.AuthUserServiceBlockingStub userServiceStub;

    @Override
    public List<BookOrderModel> getAllOrders() {
        return bookOrderMapper.toModels(bookOrderRepository.findAll());
    }

    @Override
    public BookOrderModel createOrder(BookOrderCreateRequest createRequest) {
        checkIfBookExists(createRequest.bookId());
        checkIfUserExists(createRequest.userId());

        BookOrderEntity bookOrderEntity = BookOrderEntity.builder()
                .userId(createRequest.userId())
                .bookId(createRequest.bookId())
                .status(BookOrderStatus.valueOf(createRequest.status()))
                .quantity(createRequest.quantity())
                .orderDate(createRequest.orderDate())
                .build();

        bookOrderEntity = bookOrderRepository.save(bookOrderEntity);
        return bookOrderMapper.toModel(bookOrderEntity);
    }

    @Override
    public BookOrderModel updateOrder(Long orderId, BookOrderUpdateRequest updateRequest) {
        BookOrderEntity bookOrderEntity = bookOrderRepository.findById(orderId).orElseThrow(
                () -> new RuntimeException(format("Заказ с id %d отсутствует в базе", orderId)));

        bookOrderEntity.setUserId(updateRequest.userId());
        bookOrderEntity.setBookId(updateRequest.bookId());
        bookOrderEntity.setStatus(BookOrderStatus.valueOf(updateRequest.status()));
        bookOrderEntity.setQuantity(updateRequest.quantity());
        bookOrderEntity.setOrderDate(updateRequest.orderDate());

        bookOrderEntity = bookOrderRepository.save(bookOrderEntity);
        return bookOrderMapper.toModel(bookOrderEntity);
    }

    @Override
    public void deleteOrder(Long orderId) {
        bookOrderRepository.deleteById(orderId);
    }

    @CircuitBreaker(name = "BookService")
    public void checkIfBookExists(Long bookId) {
        try {
            webClientBuilder.build().get()
                    .uri("http://localhost:8083/books/{book_id}", bookId)
                    .retrieve()
                    .onStatus(status -> status.is4xxClientError() || status.is5xxServerError(),
                            response -> Mono.error(
                                    new RuntimeException("Ошибка при получении книги: "
                                            + response.statusCode())))
                    .bodyToMono(Void.class)
                    .block();

        } catch (WebClientResponseException e) {
            throw new RuntimeException("Книга не найдена: " + e.getMessage());
        }
    }


    public void checkIfUserExists(Long userId) {
        AuthUser.CheckUserRequest request = CheckUserRequest.newBuilder().setUserId(userId).build();
        CheckUserResponse response;

        try {
            response = userServiceStub.checkUserExists(request);
            if (!response.getExists()) {
                throw new RuntimeException("Пользователь не найден");
            }
        } catch (StatusRuntimeException e) {
            throw new RuntimeException("Ошибка при обращении к AuthUserService: " + e.getStatus().getDescription());
        }
    }
}
