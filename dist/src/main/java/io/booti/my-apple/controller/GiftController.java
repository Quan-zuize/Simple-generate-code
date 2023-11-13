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
import io.booti.my-apple.model.Gift;
import io.booti.my-apple.service.GiftService;
import vnpay.game.services.FunctionService;
import vnpay.game.utils.WebUtil;
import vnpay.game.utils.auditQueue.AuditProducer;
import vnpay.game.utils.constant.GameConstant;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;


@Controller
@RequestMapping("/gift")
public class GiftController {

    private static final Logger logger = LoggerFactory.getLogger(GiftController.class);
    private final String PATH_URL = "pages/system/gift/";
    private static final String TABLE_NAME = "gm_gift";
    private static final Gson gson = new Gson();

    @Autowired
    AuditProducer auditProducer;

    @Autowired
    GiftService giftService;

    @Autowired
    FunctionService functionService;

    @PreAuthorize("hasAnyAuthority('SUB_GIFT_LIST')")
    @GetMapping
    public String list(Model model,
                       @ModelAttribute("giftForm") GiftForm giftForm,
                       @PageableDefault(size = 10) Pageable pageable,
                       HttpServletRequest request) {
        Page<Gift> gifts = giftService.search(giftForm, pageable);
        model.addAttribute("url", WebUtil.buildUrlForPaging(request, "/gift"));
        model.addAttribute("gifts", gifts);
        model.addAttribute("total", gifts.getTotalElements());
        return PATH_URL + "list";
    }

    @PreAuthorize("hasAnyAuthority('SUB_GIFT_ADD')")
    @GetMapping("/create")
    public String add(Model model, @ModelAttribute("giftForm") GiftForm giftForm) {
        model.addAttribute("functions", functionService.findAll());
        return PATH_URL + "create_update";
    }

    @PreAuthorize("hasAnyAuthority('SUB_GIFT_ADD')")
    @PostMapping("/create")
    @ResponseBody
    public CommonResult create(@RequestBody GiftForm giftForm, HttpServletRequest request) {
        try {
            logger.info("Create new gift: {}", giftForm.getId());
            Gift gift = giftService.findById(giftForm.getId());
            if (Objects.nonNull(gift)) throw new BEException(ResCode.GIFT_ALREADY_EXISTS);
            logger.debug("Insert new gift to db: {}", giftForm.getId());
            gift = giftService.insert(giftForm.toGift());
            logger.debug("Insert new gift successful");

            //audit information
            SAAudit audit = new SAAudit(request, "Thêm mới Loại quà tặng: " + giftForm.getId(),
                    GameConstant.TYPE_ADD, TABLE_NAME, "", gson.toJson(gift));
            auditProducer.send(audit);
            //audit end
        } catch (BEException e) {
            logger.error("Error while create new gift: {}", e.getMessage());
            return CommonResult.fail(e);
        } catch (Exception e) {
            logger.error("Internal error while create new gift: {}", e.getMessage());
            return CommonResult.serverError();
        }
        return CommonResult.success();
    }

    @PreAuthorize("hasAnyAuthority('SUB_GIFT_EDIT')")
    @GetMapping("/edit/{id}")
    public String edit(Model model,
                       @PathVariable("id") String id
    ) {
        logger.info("Edit the gift: {}", id);
        Gift gift = giftService.findById(id);
        GiftForm giftForm = Objects.isNull(gift) ? new GiftForm() : GiftForm.fromGift(gift);
        model.addAttribute("functions", functionService.findAll());
        model.addAttribute("giftForm", giftForm);
        return PATH_URL + "create_update";
    }

    @PreAuthorize("hasAnyAuthority('SUB_GIFT_EDIT')")
    @PostMapping("/edit/{id}")
    @ResponseBody
    public CommonResult edit(@PathVariable("id") String id,
                             @RequestBody GiftForm giftForm,
                             HttpServletRequest request) {
        try {
            logger.info("Update gift: {}", id);
            giftForm.setId(id);
            Gift gift = giftService.update(giftForm.toGift());
            logger.info("Update gift successful!: {}", giftForm.getId());

            //audit information
            SAAudit audit = new SAAudit(request, "Chỉnh sửa Loại quà tặng: " + giftForm.getId(),
                    GameConstant.TYPE_EDIT, TABLE_NAME, "", gson.toJson(gift));
            auditProducer.send(audit);
            //audit end
        } catch (BEException e) {
            logger.info("Error while update gift: {}, e: {}", giftForm.getId(), e.getMessage());
            return CommonResult.fail(e);
        } catch (Exception e) {
            logger.info("Internal error while update gift: {}", e.getMessage());
            return CommonResult.serverError();
        }
        return CommonResult.success();
    }

    @PreAuthorize("hasAnyAuthority('SUB_GIFT_DETAIL')")
    @GetMapping("/view/{id}")
    public String view(Model model, @PathVariable("id") String id) {
        logger.info("View the gift: {}", id);
        Gift gift = giftService.findById(id);
        GiftForm giftForm = Objects.isNull(gift) ? new GiftForm() : vnpay.game.dto.GiftForm.fromGift(gift);
        model.addAttribute("functions", functionService.findAll());
        model.addAttribute("giftForm", giftForm);
        return PATH_URL + "detail";
    }

    @PreAuthorize("hasAnyAuthority('SUB_GIFT_ACTIVE')")
    @PostMapping("/lock/{id}")
    @ResponseBody
    public CommonResult lockRole(@PathVariable("id") String id, HttpServletRequest request) {
        try {
            logger.info("Lock gift: {}", id);
            giftService.changeStatus(id, GameConstant.STATUS_LOCKED);
            logger.info("Lock gift: {} successful", id);

            //audit information
            SAAudit audit = new SAAudit(request, "Khóa Loại quà tặng: " + id,
                    GameConstant.TYPE_LOCK, TABLE_NAME, GameConstant.STATUS + GameConstant.STATUS_ACTIVE, GameConstant.STATUS + GameConstant.STATUS_LOCKED);
            auditProducer.send(audit);
            //audit end
        } catch (BEException e) {
            logger.info("Error while lock gift: {}, e: {}", id, e.getMessage());
            return CommonResult.fail(e);
        } catch (Exception e) {
            logger.info("Internal error while lock GIFT: {}", e.getMessage());
            return CommonResult.serverError();
        }
        return CommonResult.success();
    }

    @PreAuthorize("hasAnyAuthority('SUB_GIFT_ACTIVE')")
    @PostMapping("/unlock/{id}")
    @ResponseBody
    public CommonResult unlock(@PathVariable("id") String id, HttpServletRequest request) {
        try {
            logger.info("Unlock gift: {}", id);
            giftService.changeStatus(id, GameConstant.STATUS_ACTIVE);
            logger.info("Unlock gift: {} successful", id);

            //audit information
            SAAudit audit = new SAAudit(request, "Mở khóa Loại quà tặng: " + id,
                    GameConstant.TYPE_UNLOCK, TABLE_NAME, GameConstant.STATUS + GameConstant.STATUS_LOCKED, GameConstant.STATUS + GameConstant.STATUS_ACTIVE);
            auditProducer.send(audit);
            //audit end
        } catch (BEException e) {
            logger.info("Error while unlock gift: {}, e: {}", id, e.getMessage());
            return CommonResult.fail(e);
        } catch (Exception e) {
            logger.info("Internal error while unlock gift: {}", e.getMessage());
            return CommonResult.serverError();
        }
        return CommonResult.success();
    }
}
