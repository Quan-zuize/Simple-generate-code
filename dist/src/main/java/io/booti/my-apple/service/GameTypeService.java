package io.booti.my-apple.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vnpay.game.common.BEException;
import io.booti.my-apple.model.GameType;
import java.util.List;

public interface GameTypeService {

    Page<GameType> search(GameTypeForm form, Pageable pageable);

    GameType findById(String id);

    GameType insert(GameType model);

    GameType update(GameType model) throws BEException;

    void changeStatus(String id, Integer status) throws BEException;

    List<GameType> getAllGameTypeActive();

    String getName(String id);
}