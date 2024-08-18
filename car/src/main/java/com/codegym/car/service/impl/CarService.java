package com.codegym.car.service.impl;

import com.codegym.car.model.entity.Car;
import com.codegym.car.service.ICarService;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

@Service
public class CarService implements ICarService {
    private static SessionFactory sessionFactory;
    private static EntityManager entityManager;

    static {
        try {
            sessionFactory = new Configuration()
                    .configure("hibernate.conf.xml")
                    .buildSessionFactory();
            entityManager = sessionFactory.createEntityManager();
        } catch (HibernateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Car> findAll() {
        String queryStr = "SELECT c FROM Car AS c";
        TypedQuery<Car> query = entityManager.createQuery(queryStr, Car.class);
        return query.getResultList();
    }

    @Override
    public Car findById(Long id) {
        String queryStr = "SELECT c FROM Car AS c WHERE c.id = :id";
        TypedQuery<Car> query = entityManager.createQuery(queryStr, Car.class);
        query.setParameter("id", id);
        return query.getSingleResult();
    }

    @Override
    public void save(Car car) {
        Transaction transaction = null;
        Car c;
        if (car.getId() == null) {
            c = new Car();
        }else {
            c = findById(car.getId());
        }

        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            c.setName(car.getName());
            c.setCode(car.getCode());
            c.setProducer( car.getProducer());
            c.setPrice(car.getPrice());
            c.setDescription(car.getDescription());
            c.setImg(car.getImg());
            session.saveOrUpdate(c);
            transaction.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }

    @Override
    public void remove(Long id) {
        Car car = findById(id);
        if (car != null) {
            Transaction transaction = null;
            try (Session session = sessionFactory.openSession()) {
                transaction = session.beginTransaction();
                session.remove(car);
                transaction.commit();
            }catch (HibernateException e) {
                e.printStackTrace();
                if (transaction != null) {
                    transaction.rollback();
                }
            }
        }
    }

    @Override
    public void update(Long id, Car car) {
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();

            Car c = session.get(Car.class, id);

            if (c != null) {
                c.setName(car.getName());
                c.setCode(car.getCode());
                c.setProducer( car.getProducer());
                c.setPrice(car.getPrice());
                c.setDescription(car.getDescription());
                c.setImg(car.getImg());

                session.update(c);

                transaction.commit();
            } else {
                System.out.println("Car with ID " + id + " not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (transaction != null) {
                transaction.rollback();
            }
        }
    }

    @Override
    public List<Car> findCarByName(String name) {
        String queryStr = "SELECT c FROM Car AS c WHERE c.name = :name";
        TypedQuery<Car> query = entityManager.createQuery(queryStr, Car.class);
        query.setParameter("name", "%" + name + "%");
        return query.getResultList();
    }
}
