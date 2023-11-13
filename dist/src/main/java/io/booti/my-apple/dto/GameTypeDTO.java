package io.booti.my-apple.dto;

import lombok.Getter;
import lombok.Setter;
import vnpay.game.dto.BaseRequest;
import org.springframework.beans.BeanUtils;
import java.util.Date;
import io.booti.my-apple.model.GameType;

@Setter
@Getter
public class GameTypeForm extends BaseRequest {

    private String id;
    private String id;
    private String name;
    private boolean status;
    private String createdBy;
    private Date createdAt;
    private Date modifiedAt;
    private String modifiedBy;

    public GameType toGameType() {
        GameType GameType = new GameType();
        BeanUtils.copyProperties(this, GameType);
        return GameType;
    }

    public static GameTypeForm fromGameType(GameType model) {
        GameTypeForm form = new GameTypeForm();
        BeanUtils.copyProperties(model, form);
        return form;
    }
}
