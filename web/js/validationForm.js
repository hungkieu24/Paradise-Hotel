/* 
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/JavaScript.js to edit this template
 */


// Đối tượng 'Validator'
function Validator(options) {
    function getParent(element, selector) {
        while (element.parentElement) {
            if (element.parentElement.matches(selector)) {
                return element.parentElement;
            }
            element = element.parentElement;
        }
    }

    var selectorRules = {};

    // Hàm thực hiện kiểm tra người dùng đã nhập trường đó hay chưa
    function  validate(inputElement, rule) {
        // Kiểm tra nội dung nhập vào của người dùng
        // inputElement.value sẽ lấy ra giá trị nhập vào của người dùng
        var errorMessage;
        var errorElement = getParent(inputElement, options.formGroupSelector).querySelector(options.errorSelector); // tìm ngược lại thẻ cha của thẻ input rồi tìm thẻ form-message
        // vì muốn tìm chính xác thẻ form-message của thẻ cần báo lỗi 
        // Ex: blur khỏi thẻ input của thẻ nhập tên khi chưa nhập gì thì chỉ báo lỗi thẻ ý

        // Lấy ra các rule của selector
        var rules = selectorRules[rule.selector];

        // Lặp qua từng rule và kiểm tra
        // Nếu có lỗi thì dừng việc kiểm tra
        for (var i = 0; i < rules.length; ++i) {
            switch (inputElement.type) {
                case 'checkbox':
                case 'radio':
                    errorMessage = rules[i](
                            formElement.querySelector(rule.selector + ':checked')
                            );
                    break;
                default:
                    errorMessage = rules[i](inputElement.value);
            }
            if (errorMessage)
                break;
        }

        if (errorMessage) {
            errorElement.innerText = errorMessage;
            getParent(inputElement, options.formGroupSelector).classList.add('invalid')
        } else {
            errorElement.innerText = '';
            getParent(inputElement, options.formGroupSelector).classList.remove('invalid')
        }

        return !errorMessage; // chuyển errorMessage sang boolean
    }

    // lấy element của form cần validate
    var formElement = document.querySelector(options.form);

    if (formElement) {

        // Khi submit form
        formElement.onsubmit = function (e) {
            e.preventDefault(); // ngăn chặn hành vi submit của thẻ form

            var isFormValid = true;

            // thực hiện lặp qua từng rule và validate
            options.rules.forEach(function (rule) {
                var inputElement = formElement.querySelector(rule.selector); // nếu quên thì console.log(inputElement) ra là rõ

                var isValid = validate(inputElement, rule);
                if (!isValid) {
                    isFormValid = false;
                }
            });

            if (isFormValid) {
                // Trường hợp submit với javascript
                if (typeof options.onsubmit === 'function') {

                    var enableInputs = formElement.querySelectorAll('[name]');

                    var formValue = Array.from(enableInputs).reduce(function (values, input) {
                        switch (input.type) {
                            case 'checkbox':
                                if (!input.matches(':checked')) {
                                    values[input.name] = '';
                                    return values;
                                }
                                

                                if (!Array.isArray(values[input.name])) {
                                    values[input.name] = [];
                                }
                                values[input.name].push(input.value);
                                break;
                            case 'radio':
                                values[input.name] = formElement.querySelector('input[name="' + input.name + '"]:checked').value;
                                break;
                            case 'file':
                                values[input.name] = input.files;
                                break;
                            default:
                                values[input.name] = input.value;
                        }
                        return values;
                    }, {});

                    options.onsubmit(formValue);
                }
                // Trường hợp submit với hành vi mặc định
                else {
                    formElement.submit();
                }
            }
        }

        // Lặp qua mỗi rule và xử lý ( lắng nghe sự kiện blur, input,.. )
        options.rules.forEach(function (rule) {

            // Lưu lại các rules cho mỗi input
            // Để có thể thêm nhiều rules cho một input
            if (Array.isArray(selectorRules[rule.selector])) {
                selectorRules[rule.selector].push(rule.test);
            } else {
                selectorRules[rule.selector] = [rule.test];
            }


            var inputElements = formElement.querySelectorAll(rule.selector); // nếu quên thì console.log(inputElement) ra là rõ

            Array.from(inputElements).forEach(function (inputElement) {
                // Xử lý trường hợp blur khỏi input
                inputElement.onblur = function () {
                    validate(inputElement, rule);
                }

                // Xử lý mỗi khi người dùng nhập vào input 
                // Khi nhập sẽ không hiện màu đỏ 
                inputElement.oninput = function () {
                    var errorElement = getParent(inputElement, options.formGroupSelector).querySelector(options.errorSelector);
                    errorElement.innerText = '';
                    getParent(inputElement, options.formGroupSelector).classList.remove('invalid')
                }
            });
        })
    }
}



// định nghĩa các rules
// Nguyên tắc của các rule 
// 1. khi có lỗi => trả ra messages lỗi
// 2. khi hợp lệ => không trả ra gì cả 
Validator.isRequired = function (selector, message) {
    return {
        selector: selector,
        test: function (value) {
            // trim() sẽ giúp loại bỏ dấu cách
            return value.trim() ? undefined : message || 'Please enter this field';
        }
    }
}

Validator.isEmail = function (selector, message) {
    return {
        selector: selector,
        test: function (value) {
            var regex = /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/;
            return regex.test(value) ? undefined : message || 'This field must be an email';
        }
    }
}

Validator.isNumber = function (selector, message) {
    return {
        selector: selector,
        test: function (value) {
            var regex = /^-?\d+(\.\d+)?$/;
            return regex.test(value) ? undefined : message || 'This field must be numeric';
        }
    }
}

Validator.minNumber = function (selector, min, message) {
    return {
        selector: selector,
        test: function (value) {
            return value >= min ? undefined : message || `Please enter > ${min}`;
        }
    }
}
Validator.minLessThanMax = function (minSelector, maxSelector, message) {
    return {
        selector: maxSelector, 
        test: function () {
            const min = parseFloat(document.querySelector(minSelector)?.value) || 0;
            const max = parseFloat(document.querySelector(maxSelector)?.value) || 0;

            return min <= max ? undefined : message || 'Maximum price must be more than or equal to minimum price';
        }
    };
};


Validator.bankAccountNumber = function (selector, message) {
    return {
        selector: selector,
        test: function (value) {
            const regex = /^[0-9]{8,14}$/;
            return regex.test(value) ? undefined : message || "Only accept 8 - 14 characters";
        }
    }
}

Validator.uppercaseOnly = function (selector, message) {
    return {
        selector: selector,
        test: function (value) {
            const regex = /^[A-Z\s]+$/;
            return regex.test(value) ? undefined : message || "Only uppercase letters accepted";
        }
    }
}

Validator.minLength = function (selector, min, message) {
    return {
        selector: selector,
        test: function (value) {
            return value.length >= min ? undefined : message || `Please enter at least ${min} characters`;
        }
    }
}

Validator.maxLength = function (selector, max, message) {
    return {
        selector: selector,
        test: function (value) {
            return value.length <= max ? undefined : message || `Please enter no more than ${max} characters`;
        }
    }
}

Validator.lengthRange = function (selector, min, max, message) {
    return {
        selector: selector,
        test: function (value) {
            if (value.length < min || value.length > max) {
                return message || `Please enter between ${min} and ${max} characters.`;
            }
            return undefined; // Hợp lệ
        }
    }
}

Validator.maxValue = function (selector, max, message) {
    return {
        selector: selector,
        test: function (value) {
            return parseFloat(value) <= max ? undefined : message || `Please enter a value less than or equal to ${max}`;
        }
    }
}

Validator.lengthRange = function (selector, min, max, message) {
    return {
        selector: selector,
        test: function (value) {
            if (value.length < min || value.length > max) {
                return message || `Please enter between ${min} and ${max} characters.`;
            }
            return undefined; // Hợp lệ
        }
    }
}

Validator.isConfirmed = function (selector, getConfirmValue, message) {
    return {
        selector: selector,
        test: function (value) {
            return value === getConfirmValue() ? undefined : message || 'The entered value is incorrect.'
        }
    }
}

Validator.isRequireChecked = function (selector, message) {
    return {
        selector: selector,
        test: function (value) {
            return value ? undefined : message || 'Please enter this field';
        }
    }
}

Validator.isPhoneNumber = function (selector, message) {
    return {
        selector: selector,
        test: function (value) {
            var regex = /^(0|\+84)[3-9][0-9]{8}$/; 
            return regex.test(value) ? undefined : message || 'Invalid phone number';
        }
    }
}

Validator.isRequiredFile = function (selector, message) {
    return {
        selector: selector,
        test: function () {
            const input = document.querySelector(selector);
            if (!input || !input.files || input.files.length === 0) {
                return message || "Please select at least one file.";
            }
            return undefined;
        }
    };
};

Validator.isImageFile = function (selector, message) {
    return {
        selector: selector,
        test: function () {
            const input = document.querySelector(selector);
            const validTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];

            for (let i = 0; i < input.files.length; i++) {
                if (!validTypes.includes(input.files[i].type)) {
                    return message || 'File must be an image (.jpg, .png, .gif, .webp)';
                }
            }

            return undefined;
        }
    };
};

Validator.maxFileCount = function (selector, maxCount, message) {
    return {
        selector: selector,
        test: function () {
            const input = document.querySelector(selector);
            if (!input || !input.files) return undefined;

            if (input.files.length > maxCount) {
                return message || `Only up to ${maxCount} images can be uploaded.`;
            }

            return undefined;
        }
    };
};

Validator.isSelectRequired = function (selector, message) {
    return {
        selector: selector,
        test: function (value) {
            return value && value !== "" ? undefined : message || 'Please choose an option';
        }
    }
}

Validator.passwordStrength = function (selector, message) {
    return {
        selector: selector,
        test: function (value) {
            const hasLetter = /[a-zA-Z]/.test(value);
            const hasDigit = /[0-9]/.test(value);

            return hasLetter && hasDigit
                ? undefined
                : message || 'Password must include at least one letter and one digit.';
        }
    };
};
