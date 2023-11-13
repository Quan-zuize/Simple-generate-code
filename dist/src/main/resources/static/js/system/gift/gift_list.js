let prototype = GiftController.prototype;
let basePrototype = BaseController.prototype;
const lockElement = $("#modal_lock #element_lock_id");
const unlockElement = $("#modal_unlock #element_unlock_id");
const contentLockElement = document.getElementById('content_lock');
const contentUnlockElement = document.getElementById('content_unlock');

function GiftController() {
    basePrototype.paginateInit();
    basePrototype.initDateTime();

    $("#modal_lock .btn-confirm").on('click', () => {
        let id = lockElement.val();
        prototype.callApiChangeStatus(`gift/lock/${id}`);
    })

    $("#modal_unlock .btn-confirm").on('click', () => {
        let id = unlockElement.val();
        prototype.callApiChangeStatus(`gift/unlock/${id}`);
    })

    $(document).on('click', 'a.btn-lock', function (e) {
        const id = $(this).parent().find("input[type='hidden']").val();
        lockElement.val(id);
    })

    $(document).on('click', 'a.btn-unlock', function (e) {
        const id = $(this).parent().find("input[type='hidden']").val();
        unlockElement.val(id);
    })

    //Only input number
    basePrototype.onlyInputNumber($("#id"));
    basePrototype.inputAlphanumericAndUnderscore($("#giftName"));

}

prototype.callApiChangeStatus = function (url) {
    AjaxService.post(
        url,
        null,
        (data) => {
            if (data.code === ResCode.SUCCESS) {
                basePrototype.toastSuccess(message.UPDATE_STATUS_SUCCESS);
                location.reload();
            } else {
                basePrototype.toastError(data.message);
            }
        }, (error) => {
            console.log(error);
        }
    )
};

var controller;
$(document).ready(function () {
    controller = new GiftController();
});