package com.codegym.car.service;

import com.codegym.car.model.entity.Car;

import java.util.List;

public interface ICarService {
    List<Car> findAll();

    Car findById(Long id);

    void save(Car car);

    void remove(Long id);

    void update(Long id, Car car);

    List<Car> findCarByName(String name);
}
