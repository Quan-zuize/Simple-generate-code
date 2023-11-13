const basePrototype = BaseController.prototype;
let prototype = GiftController.prototype};
let formSelector = '#form-create'
let id = document.querySelector(formSelector).querySelector("input[type='hidden']").value;

function GiftController() {
    $(document).on('click', 'button.btn-confirm', function (e) {
        e.preventDefault();
        if ($(formSelector).valid()) {
            prototype.onclickBtnCreate();
        }
    });

    $.validator.methods.verifyCode = function (value) {
        return !/[^0-9]/.test(value);
    };

    $.validator.methods.verifyName = function (value) {
        return !/[~`!@#$%^&*()+={}<>.,/|-]/.test(value);
    };

    prototype.validateForm();

    //Set maxlength
    basePrototype.notInputLength($(`${formSelector} input[name='id']`), 50);

    //Not include space character
    basePrototype.notInputSpace($(`${formSelector} input[name='id']`));

}

prototype.validateForm = function () {
    $(formSelector).validate({
        focusInvalid: true,
        ignore: [],
        rules: {
            id: {
                required: true
            } , 
            name: {
                required: true
            } , 
            status: {
                required: true
            } , 
            url: {
                required: true
            }
        },
        messages: {
            id: {
                required: "Mã Loại Game không được để trống. Vui lòng kiểm tra lại"
            } , 
            name: {
                required: "Tên Loại Game không được để trống. Vui lòng kiểm tra lại"
            } , 
            status: {
                required: "Trạng thái không được để trống. Vui lòng kiểm tra lại"
            } , 
            url: {
                required: "Đường dẫn không được để trống. Vui lòng kiểm tra lại"
            }
        }
    });
};

prototype.onclickBtnCreate = function () {
    var url = WebContext.contextPath;
    const data = {
        id: $('#id').val().trim()  , 
        name: $('#name').val().trim()  , 
        status: $('#status').val().trim()  , 
        url: $('#url').val().trim() 
    }
    if (id != null && id !== '') {
        prototype.callApiUpdate(url, data);
    } else {
        url += 'create';
        prototype.callApiCreate(url, data);
    }
};

prototype.callApiCreate = function (url, formData) {
    AjaxService.post(
        url,
        JSON.stringify(formData),
        (data) => {
            if (data.code === ResCode.SUCCESS) {
                basePrototype.toastSuccess(message.CREATE_SUCCESS);
                prototype.clearForm();
            } else {
                basePrototype.toastError(data.message);
            }
        }, (error) => {
            console.log(error);
        }
    )
};

prototype.callApiUpdate = function (url, formData) {
    AjaxService.post(
        url,
        JSON.stringify(formData),
        (data) => {
            if (data.code === ResCode.SUCCESS) {
                basePrototype.toastSuccess(message.UPDATE_SUCCESS);
            } else {
                basePrototype.toastError(data.message);
            }
        }, (error) => {
            console.log(error);
        }
    )
};


prototype.clearForm = () => {
    $(formSelector)[0].reset();
}

var controller;
$(document).ready(function () {
    controller = new GiftController();
});
