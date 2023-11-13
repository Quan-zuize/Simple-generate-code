package io.booti.my-apple.repos;

import org.springframework.data.mongodb.repository.MongoRepository;
import io.booti.my-apple.model.GameType;

import java.util.List;

public interface GameTypeRepository extends MongoRepository<GameType, String> {
    List<GameType> findGameTypesByStatusOrderByName(Integer status);
}