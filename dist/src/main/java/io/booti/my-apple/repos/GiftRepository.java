package io.booti.my-apple.repos;

import org.springframework.data.mongodb.repository.MongoRepository;
import io.booti.my-apple.model.Gift;

import java.util.List;

public interface GiftRepository extends MongoRepository<Gift, String> {
    List<Gift> findGiftsByStatusOrderByName(Integer status);
}