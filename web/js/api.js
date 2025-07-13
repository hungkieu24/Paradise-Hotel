const host = "http://provinces.open-api.vn/api/";

const createLocationSelector = (config) => {
    const { provinceId, districtId, wardId, addressId } = config;

    const fetchData = async (url) => {
        try {
            const response = await fetch(url);
            return await response.json();
        } catch (error) {
            console.error("Lỗi khi tải dữ liệu:", error);
        }
    };

    const renderOptions = (data, elementId, placeholder) => {
        const select = document.getElementById(elementId);
        select.innerHTML = `<option value="">${placeholder}</option>`;
        data.forEach(item => {
            select.innerHTML += `<option value="${item.code}">${item.name}</option>`;
        });
    };

    const printResult = () => {
        const province = document.getElementById(provinceId).selectedOptions[0]?.text || "";
        const district = document.getElementById(districtId).selectedOptions[0]?.text || "";
        const ward = document.getElementById(wardId).selectedOptions[0]?.text || "";

        // Giữ nguyên logic kiểm tra
        if (
            province !== "Choose province" && 
            district !== "Choose district" && 
            ward !== "Choose ward"
        ) {
            document.getElementById(addressId).value = `${ward}, ${district}, ${province}`;
        } else {
            document.getElementById(addressId).value = "";
        }
    };

    document.getElementById(provinceId).addEventListener("change", async (e) => {
        const code = e.target.value;
        if (code) {
            const data = await fetchData(`${host}p/${code}?depth=2`);
            renderOptions(data.districts, districtId, "Choose district");
            document.getElementById(wardId).innerHTML = '<option value="">Choose ward</option>';
        } else {
            document.getElementById(districtId).innerHTML = '<option value="">Choose district</option>';
            document.getElementById(wardId).innerHTML = '<option value="">Choose ward</option>';
        }
        printResult();
    });

    document.getElementById(districtId).addEventListener("change", async (e) => {
        const code = e.target.value;
        if (code) {
            const data = await fetchData(`${host}d/${code}?depth=2`);
            renderOptions(data.wards, wardId, "Choose ward");
        } else {
            document.getElementById(wardId).innerHTML = '<option value="">Choose ward</option>';
        }
        printResult();
    });

    document.getElementById(wardId).addEventListener("change", printResult);

    (async () => {
        const data = await fetchData(`${host}?depth=1`);
        renderOptions(data, provinceId, "Choose province");
    })();
};

//    
//    if (province !== "Choose province"){
//       document.getElementById("provinceInput").value = `${province}`;
//    } else {
//        document.getElementById("provinceInput").value = "";
//    }
//    if (district !== "Choose district"){
//       document.getElementById("districtInput").value = `${district}`;
//    } else {
//        document.getElementById("districtInput").value = "";
//    }
//    if (ward !== "Choose ward"){
//       document.getElementById("wardInput").value = `${ward}`;
//    } else {
//        document.getElementById("wardInput").value = "";
//    }



// Dùng với 1 đống class khi 1 trang nhiều chỗ cần call API:
const createLocationSelectorByClass = (config) => {
    const { provinceClass, districtClass, wardClass, addressClass } = config;

    const fetchData = async (url) => {
        try {
            const response = await fetch(url);
            return await response.json();
        } catch (error) {
            console.error("Lỗi khi tải dữ liệu:", error);
        }
    };

    const renderOptions = (data, selectElement, placeholder) => {
        selectElement.innerHTML = `<option value="">${placeholder}</option>`;
        data.forEach(item => {
            selectElement.innerHTML += `<option value="${item.code}">${item.name}</option>`;
        });
    };

    const printResult = (provinceSelect, districtSelect, wardSelect, addressInput) => {
        const province = provinceSelect.selectedOptions[0]?.text || "";
        const district = districtSelect.selectedOptions[0]?.text || "";
        const ward = wardSelect.selectedOptions[0]?.text || "";

        // Giữ nguyên logic kiểm tra
        if (
            province !== "Choose province" && 
            district !== "Choose district" && 
            ward !== "Choose ward"
        ) {
            addressInput.value = `${ward}, ${district}, ${province}`;
        } else {
            addressInput.value = "";
        }
    };

    const initializeSelector = async (provinceSelect, districtSelect, wardSelect, addressInput) => {
        const data = await fetchData(`${host}?depth=1`);
        renderOptions(data, provinceSelect, "Choose province");

        provinceSelect.addEventListener("change", async (e) => {
            const code = e.target.value;
            if (code) {
                const districtData = await fetchData(`${host}p/${code}?depth=2`);
                renderOptions(districtData.districts, districtSelect, "Choose district");
                wardSelect.innerHTML = '<option value="">Choose ward</option>';
            } else {
                districtSelect.innerHTML = '<option value="">Choose district</option>';
                wardSelect.innerHTML = '<option value="">Choose ward</option>';
            }
            printResult(provinceSelect, districtSelect, wardSelect, addressInput);
        });

        districtSelect.addEventListener("change", async (e) => {
            const code = e.target.value;
            if (code) {
                const wardData = await fetchData(`${host}d/${code}?depth=2`);
                renderOptions(wardData.wards, wardSelect, "Choose ward");
            } else {
                wardSelect.innerHTML = '<option value="">Choose ward</option>';
            }
            printResult(provinceSelect, districtSelect, wardSelect, addressInput);
        });

        wardSelect.addEventListener("change", () => {
            printResult(provinceSelect, districtSelect, wardSelect, addressInput);
        });
    };

    // Áp dụng cho từng nhóm phần tử có cùng class
    const provinceElements = document.querySelectorAll(`.${provinceClass}`);
    const districtElements = document.querySelectorAll(`.${districtClass}`);
    const wardElements = document.querySelectorAll(`.${wardClass}`);
    const addressElements = document.querySelectorAll(`.${addressClass}`);

    provinceElements.forEach((provinceSelect, index) => {
        const districtSelect = districtElements[index];
        const wardSelect = wardElements[index];
        const addressInput = addressElements[index];

        if (provinceSelect && districtSelect && wardSelect && addressInput) {
            initializeSelector(provinceSelect, districtSelect, wardSelect, addressInput);
        }
    });
};


