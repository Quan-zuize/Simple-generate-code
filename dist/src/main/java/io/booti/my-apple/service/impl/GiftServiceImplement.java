package io.booti.my-apple.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import vnpay.game.common.BEException;
import vnpay.game.common.ResCode;
import io.booti.my-apple.model.Gift;
import io.booti.my-apple.repos.GiftRepository;
import io.booti.my-apple.service.GiftService;
import vnpay.game.utils.CriterialUtils;
import vnpay.game.utils.WebUtil;
import vnpay.game.utils.constant.GameConstant;

import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

@Service
public class GiftServiceImpl implements GiftService {

    @Autowired
    GiftRepository giftRepository;
    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public Page<Gift> search(GiftForm form, Pageable pageable) {
        Query query = new Query();
        if (!StringUtils.isEmpty(form.getName())) {
            query.addCriteria(Criteria.where("name").regex(Pattern.compile(form.getName(), Pattern.CASE_INSENSITIVE)));
        }
        if (Objects.nonNull(form.getStatus())) {
            query.addCriteria(Criteria.where("status").is(form.getStatus()));
        }
        query.addCriteria(CriterialUtils.criteriaRangeTime(form.getFromDate(), form.getToDate(), "created_at"));
        long total = mongoTemplate.count(query, Gift.class);
        query.with(Sort.by(Sort.Direction.DESC, "created_at"));
        List<Gift> list = mongoTemplate.find(query.with(pageable), Gift.class);
        return new PageImpl<>(list, pageable, total);
    }

    @Override
    public Gift findById(String id) {
        return giftRepository.findById(id).orElse(null);
    }

    @Override
    public Gift insert(Gift model) {
        model.setStatus(GameConstant.STATUS_ACTIVE);
        model.setCreatedAt(new Date());
        model.setCreatedBy(WebUtil.getCurrentUsername());
        return giftRepository.save(model);
    }

    @Override
    public Gift update(Gift model) throws BEException {
        Gift oldModel = findById(model.getId());
        if (Objects.isNull(oldModel)) throw new BEException(ResCode.GRADE_NOT_EXISTS);
        oldModel.setName(model.getName());
        oldModel.setModifiedAt(new Date());
        oldModel.setModifiedBy(WebUtil.getCurrentUsername());
        return giftRepository.save(oldModel);
    }

    @Override
    public List<Gift> getAllGiftActive() {
        return giftRepository.findGiftsByStatusOrderByName(GameConstant.STATUS_ACTIVE);
    }

    @Override
    public String getName(String id) {
        Gift model = findById(id);
        return Objects.nonNull(model) ? model.getName() : "";
    }

    @Override
    public void changeStatus(String id, Integer status) throws BEException {
        Gift model = findById(id);
        if (Objects.isNull(model)) throw new BEException(ResCode.GRADE_NOT_EXISTS);
        model.setStatus(status);
        giftRepository.save(model);
    }
}
