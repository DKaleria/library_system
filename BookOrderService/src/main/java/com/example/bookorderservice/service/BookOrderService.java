package com.example.bookorderservice.service;

import com.example.bookorderservice.controller.request.BookOrderCreateRequest;
import com.example.bookorderservice.controller.request.BookOrderUpdateRequest;
import com.example.bookorderservice.model.BookOrderModel;
import java.util.List;

public interface BookOrderService {
    List<BookOrderModel> getAllOrders();

    BookOrderModel createOrder(BookOrderCreateRequest createRequest);

    BookOrderModel updateOrder(Long orderId, BookOrderUpdateRequest updateRequest);

    void deleteOrder(Long orderId);
}
