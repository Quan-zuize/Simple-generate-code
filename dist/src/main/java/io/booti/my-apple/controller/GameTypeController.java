package io.booti.my-apple.controller;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import vnpay.game.common.BEException;
import vnpay.game.common.CommonResult;
import vnpay.game.common.ResCode;
import vnpay.game.model.be.SAAudit;
import io.booti.my-apple.model.GameType;
import io.booti.my-apple.service.GameTypeService;
import vnpay.game.services.FunctionService;
import vnpay.game.utils.WebUtil;
import vnpay.game.utils.auditQueue.AuditProducer;
import vnpay.game.utils.constant.GameConstant;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;


@Controller
@RequestMapping("/game_type")
public class GameTypeController {

    private static final Logger logger = LoggerFactory.getLogger(GameTypeController.class);
    private final String PATH_URL = "pages/system/gameType/";
    private static final String TABLE_NAME = "game_type";
    private static final Gson gson = new Gson();

    @Autowired
    AuditProducer auditProducer;

    @Autowired
    GameTypeService gameTypeService;

    @Autowired
    FunctionService functionService;

    @PreAuthorize("hasAnyAuthority('SUB_GAMETYPE_LIST')")
    @GetMapping
    public String list(Model model,
                       @ModelAttribute("gameTypeForm") GameTypeForm gameTypeForm,
                       @PageableDefault(size = 10) Pageable pageable,
                       HttpServletRequest request) {
        Page<GameType> gameTypes = gameTypeService.search(gameTypeForm, pageable);
        model.addAttribute("url", WebUtil.buildUrlForPaging(request, "/gameType"));
        model.addAttribute("gameTypes", gameTypes);
        model.addAttribute("total", gameTypes.getTotalElements());
        return PATH_URL + "list";
    }

    @PreAuthorize("hasAnyAuthority('SUB_GAMETYPE_ADD')")
    @GetMapping("/create")
    public String add(Model model, @ModelAttribute("gameTypeForm") GameTypeForm gameTypeForm) {
        model.addAttribute("functions", functionService.findAll());
        return PATH_URL + "create_update";
    }

    @PreAuthorize("hasAnyAuthority('SUB_GAMETYPE_ADD')")
    @PostMapping("/create")
    @ResponseBody
    public CommonResult create(@RequestBody GameTypeForm gameTypeForm, HttpServletRequest request) {
        try {
            logger.info("Create new gameType: {}", gameTypeForm.getId());
            GameType gameType = gameTypeService.findById(gameTypeForm.getId());
            if (Objects.nonNull(gameType)) throw new BEException(ResCode.GAMETYPE_ALREADY_EXISTS);
            logger.debug("Insert new gameType to db: {}", gameTypeForm.getId());
            gameType = gameTypeService.insert(gameTypeForm.toGameType());
            logger.debug("Insert new gameType successful");

            //audit information
            SAAudit audit = new SAAudit(request, "Thêm mới Loại hình Game: " + gameTypeForm.getId(),
                    GameConstant.TYPE_ADD, TABLE_NAME, "", gson.toJson(gameType));
            auditProducer.send(audit);
            //audit end
        } catch (BEException e) {
            logger.error("Error while create new gameType: {}", e.getMessage());
            return CommonResult.fail(e);
        } catch (Exception e) {
            logger.error("Internal error while create new gameType: {}", e.getMessage());
            return CommonResult.serverError();
        }
        return CommonResult.success();
    }

    @PreAuthorize("hasAnyAuthority('SUB_GAMETYPE_EDIT')")
    @GetMapping("/edit/{id}")
    public String edit(Model model,
                       @PathVariable("id") String id
    ) {
        logger.info("Edit the gameType: {}", id);
        GameType gameType = gameTypeService.findById(id);
        GameTypeForm gameTypeForm = Objects.isNull(gameType) ? new GameTypeForm() : GameTypeForm.fromGameType(gameType);
        model.addAttribute("functions", functionService.findAll());
        model.addAttribute("gameTypeForm", gameTypeForm);
        return PATH_URL + "create_update";
    }

    @PreAuthorize("hasAnyAuthority('SUB_GAMETYPE_EDIT')")
    @PostMapping("/edit/{id}")
    @ResponseBody
    public CommonResult edit(@PathVariable("id") String id,
                             @RequestBody GameTypeForm gameTypeForm,
                             HttpServletRequest request) {
        try {
            logger.info("Update gameType: {}", id);
            gameTypeForm.setId(id);
            GameType gameType = gameTypeService.update(gameTypeForm.toGameType());
            logger.info("Update gameType successful!: {}", gameTypeForm.getId());

            //audit information
            SAAudit audit = new SAAudit(request, "Chỉnh sửa Loại hình Game: " + gameTypeForm.getId(),
                    GameConstant.TYPE_EDIT, TABLE_NAME, "", gson.toJson(gameType));
            auditProducer.send(audit);
            //audit end
        } catch (BEException e) {
            logger.info("Error while update gameType: {}, e: {}", gameTypeForm.getId(), e.getMessage());
            return CommonResult.fail(e);
        } catch (Exception e) {
            logger.info("Internal error while update gameType: {}", e.getMessage());
            return CommonResult.serverError();
        }
        return CommonResult.success();
    }

    @PreAuthorize("hasAnyAuthority('SUB_GAMETYPE_DETAIL')")
    @GetMapping("/view/{id}")
    public String view(Model model, @PathVariable("id") String id) {
        logger.info("View the gameType: {}", id);
        GameType gameType = gameTypeService.findById(id);
        GameTypeForm gameTypeForm = Objects.isNull(gameType) ? new GameTypeForm() : vnpay.game.dto.GameTypeForm.fromGameType(gameType);
        model.addAttribute("functions", functionService.findAll());
        model.addAttribute("gameTypeForm", gameTypeForm);
        return PATH_URL + "detail";
    }

    @PreAuthorize("hasAnyAuthority('SUB_GAMETYPE_ACTIVE')")
    @PostMapping("/lock/{id}")
    @ResponseBody
    public CommonResult lockRole(@PathVariable("id") String id, HttpServletRequest request) {
        try {
            logger.info("Lock gameType: {}", id);
            gameTypeService.changeStatus(id, GameConstant.STATUS_LOCKED);
            logger.info("Lock gameType: {} successful", id);

            //audit information
            SAAudit audit = new SAAudit(request, "Khóa Loại hình Game: " + id,
                    GameConstant.TYPE_LOCK, TABLE_NAME, GameConstant.STATUS + GameConstant.STATUS_ACTIVE, GameConstant.STATUS + GameConstant.STATUS_LOCKED);
            auditProducer.send(audit);
            //audit end
        } catch (BEException e) {
            logger.info("Error while lock gameType: {}, e: {}", id, e.getMessage());
            return CommonResult.fail(e);
        } catch (Exception e) {
            logger.info("Internal error while lock GAMETYPE: {}", e.getMessage());
            return CommonResult.serverError();
        }
        return CommonResult.success();
    }

    @PreAuthorize("hasAnyAuthority('SUB_GAMETYPE_ACTIVE')")
    @PostMapping("/unlock/{id}")
    @ResponseBody
    public CommonResult unlock(@PathVariable("id") String id, HttpServletRequest request) {
        try {
            logger.info("Unlock gameType: {}", id);
            gameTypeService.changeStatus(id, GameConstant.STATUS_ACTIVE);
            logger.info("Unlock gameType: {} successful", id);

            //audit information
            SAAudit audit = new SAAudit(request, "Mở khóa Loại hình Game: " + id,
                    GameConstant.TYPE_UNLOCK, TABLE_NAME, GameConstant.STATUS + GameConstant.STATUS_LOCKED, GameConstant.STATUS + GameConstant.STATUS_ACTIVE);
            auditProducer.send(audit);
            //audit end
        } catch (BEException e) {
            logger.info("Error while unlock gameType: {}, e: {}", id, e.getMessage());
            return CommonResult.fail(e);
        } catch (Exception e) {
            logger.info("Internal error while unlock gameType: {}", e.getMessage());
            return CommonResult.serverError();
        }
        return CommonResult.success();
    }
}
