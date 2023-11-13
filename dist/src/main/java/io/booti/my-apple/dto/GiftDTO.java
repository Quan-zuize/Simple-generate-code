package io.booti.my-apple.dto;

import lombok.Getter;
import lombok.Setter;
import vnpay.game.dto.BaseRequest;
import org.springframework.beans.BeanUtils;
import java.util.Date;
import io.booti.my-apple.model.Gift;

@Setter
@Getter
public class GiftForm extends BaseRequest {

    private String id;
    private String id;
    private String name;
    private String status;
    private String url;
    private String createdBy;
    private Date createdAt;
    private Date modifiedAt;
    private String modifiedBy;

    public Gift toGift() {
        Gift Gift = new Gift();
        BeanUtils.copyProperties(this, Gift);
        return Gift;
    }

    public static GiftForm fromGift(Gift model) {
        GiftForm form = new GiftForm();
        BeanUtils.copyProperties(model, form);
        return form;
    }
}
