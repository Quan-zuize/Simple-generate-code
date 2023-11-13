package io.booti.my-apple.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vnpay.game.common.BEException;
import io.booti.my-apple.model.Gift;
import java.util.List;

public interface GiftService {

    Page<Gift> search(GiftForm form, Pageable pageable);

    Gift findById(String id);

    Gift insert(Gift model);

    Gift update(Gift model) throws BEException;

    void changeStatus(String id, Integer status) throws BEException;

    List<Gift> getAllGiftActive();

    String getName(String id);
}