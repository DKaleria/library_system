package com.example.bookorderservice.mapper;

import com.example.bookorderservice.controller.request.BookOrderCreateRequest;
import com.example.bookorderservice.controller.request.BookOrderUpdateRequest;
import com.example.bookorderservice.model.BookOrderModel;
import com.example.bookorderservice.repository.entity.BookOrderEntity;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper
public interface BookOrderMapper {

    BookOrderEntity toEntity(BookOrderModel model);

    List<BookOrderEntity> toEntities(List<BookOrderModel> models);

    BookOrderModel toModel(BookOrderEntity entity);

    List<BookOrderModel> toModels(List<BookOrderEntity> models);

    BookOrderModel toModel(BookOrderCreateRequest createRequest);

    BookOrderModel toModel(BookOrderUpdateRequest updateRequest);
}
