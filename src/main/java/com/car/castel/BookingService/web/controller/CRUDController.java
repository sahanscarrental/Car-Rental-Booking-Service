package com.car.castel.BookingService.web.controller;

import com.car.castel.BookingService.web.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;


public interface CRUDController<T> {
    /**
     * method for create a T entity
     * @param t instance of T
     * @return ResponseEntity
     * @throws Exception throws Exception
     */
    ResponseEntity<ApiResponse> create(@RequestBody T t) throws Exception;

    /**
     * method for update a T entity
     * @param id id of the entity
     * @param t instance of T
     * @return ResponseEntity
     * @throws Exception throws Exception
     */
    ResponseEntity<ApiResponse> update(@PathVariable UUID id, @RequestBody T t) throws Exception;

    /**
     * method for get a T entity
     * @param uuid id of the entity
     * @return ResponseEntity
     * @throws Exception throws Exception
     */
    ResponseEntity<ApiResponse> get(@PathVariable UUID uuid) throws Exception;

    /**
     * method for delete a T entity
     * @param uuid id of the entity
     * @return ResponseEntity
     * @throws Exception throws Exception
     */
    ResponseEntity delete(@PathVariable UUID uuid) throws Exception;

    /**
     * method for create list of T entity
     * @param list List of T
     * @return ResponseEntity
     * @throws Exception throws Exception
     */
    ResponseEntity<ApiResponse> createAll(@RequestBody List<T> list) throws Exception;

    /**
     * method for get list of T entity
     * @return ResponseEntity
     * @throws Exception throws Exception
     */
    ResponseEntity<ApiResponse> getAll() throws Exception;
}
