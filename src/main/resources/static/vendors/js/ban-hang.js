function toggleStatus(checkbox, hoaDonID) {
    var status = checkbox.checked ? !null : null;
    var xhr = new XMLHttpRequest();
    xhr.open('POST', '/admin/ban-hang/updategh/' + hoaDonID, true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.onreadystatechange = function () {
        if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
            // Reload lại trang mà không render lại trang
            location.reload(); // Tải lại trang
        }
    };
    xhr.send(JSON.stringify({
        status: status
    }));
}

document.querySelectorAll('.price').forEach(function (el) {
    var number = parseFloat(el.textContent);
    el.textContent = number.toLocaleString('vi-VN');
});
$(document).ready(function () {
    $('#province').change(function () {
        var provinceId = $(this).val();
        if (provinceId) {
            $.ajax({
                url: '/admin/districts',
                type: 'GET',
                data: {provinceId: provinceId},
                success: function (data) {
                    console.log('Districts Data:', data); // Kiểm tra dữ liệu nhận được
                    var districtSelect = $('#district');
                    districtSelect.empty();
                    districtSelect.append('<option value="">Chọn Quận/Huyện</option>');
                    $.each(data, function (index, district) {
                        districtSelect.append('<option value="' + district.DistrictID + '">' + district.DistrictName + '</option>');
                    });
                    $('#ward').empty().append('<option value="">Chọn Xã/Phường</option>');
                },
                error: function (xhr, status, error) {
                    console.error('AJAX Error:', status, error);
                }
            });
        } else {
            $('#district').empty().append('<option value="">Chọn Quận/Huyện</option>');
            $('#ward').empty().append('<option value="">Chọn Xã/Phường</option>');
        }
    });

    $('#district').change(function () {
        var districtId = $(this).val();
        if (districtId) {
            $.ajax({
                url: '/admin/wards',
                type: 'GET',
                data: {districtId: districtId},
                success: function (data) {
                    console.log('Wards Data:', data); // Kiểm tra dữ liệu nhận được
                    var wardSelect = $('#ward');
                    wardSelect.empty();
                    wardSelect.append('<option value="">Chọn Xã/Phường</option>');
                    $.each(data, function (index, ward) {
                        wardSelect.append('<option value="' + ward.WardCode + '">' + ward.WardName + '</option>');
                    });
                },
                error: function (xhr, status, error) {
                    console.error('AJAX Error:', status, error);
                }
            });
        } else {
            $('#ward').empty().append('<option value="">Chọn Xã/Phường</option>');
        }
    });
});
document.addEventListener('DOMContentLoaded', function () {

    document.querySelector("#add form").addEventListener("submit", function (event) {
        console.log("JavaScript loaded");
        const ten = document.getElementById("ten").value;
        const sdt = document.getElementById("sdt").value;
        const tinhthanhpho = document.getElementById("province").value;
        const xaphuong = document.getElementById("district").value;
        const quanhuyen = document.getElementById("ward").value;
        const dccuthe = document.getElementById("dccuthe").value;
        const email = document.getElementById("email").value;
        let valid = true;
        if (ten.trim() === "") {
            document.getElementById("tenerror").textContent = "Tên không được trống";
            valid = false;
        } else {
            document.getElementById("tenerror").textContent = "";
        }
        if (email.trim() === "") {
            document.getElementById("emailerror").textContent = "Email không được trống";
            valid = false;
        } else {
            document.getElementById("emailerror").textContent = "";
        }
        if (!email.includes("@")) {
            document.getElementById("emailerror").textContent = "Email phải có @";
            valid = false;
        } else {
            document.getElementById("emailerror").textContent = "";
        }

        if (sdt.trim() === "") {
            document.getElementById("sdterror").textContent = "Số điện thoại không được trống";
            valid = false;
        } else {
            document.getElementById("sdterror").textContent = "";
        }
        if (!/^\d+$/.test(sdt)) {
            document.getElementById("sdterror").textContent = "Số điện thoại phải là số";
            valid = false;
        } else {
            document.getElementById("sdterror").textContent = "";
        }

        if (tinhthanhpho.trim() === "") {
            document.getElementById("tinhthanhphoerror").textContent = "Tỉnh thành phố không được trống";
            valid = false;
        } else {
            document.getElementById("tinhthanhphoerror").textContent = "";
        }

        if (quanhuyen.trim() === "") {
            document.getElementById("quanhuyenerror").textContent = "Quận huyện không được trống";
            valid = false;
        } else {
            document.getElementById("quanhuyenerror").textContent = "";
        }

        if (xaphuong.trim() === "") {
            document.getElementById("phuongxaerror").textContent = "Xã phường không được trống";
            valid = false;
        } else {
            document.getElementById("phuongxaerror").textContent = "";
        }

        if (dccuthe.trim() === "") {
            document.getElementById("diachicutheerror").textContent = "Địa chỉ cụ thể không được trống";
            valid = false;
        } else {
            document.getElementById("diachicutheerror").textContent = "";
        }

        console.log("Validation completed");

        if (!valid) {
            event.preventDefault();
        }
    });

    // Validation for update for
});


$(document).ready(function () {
    $('#province1').change(function () {
        var provinceId = $(this).val();
        if (provinceId) {
            $.ajax({
                url: '/admin/districts',
                type: 'GET',
                data: {provinceId: provinceId},
                success: function (data) {
                    console.log('Districts Data:', data); // Kiểm tra dữ liệu nhận được
                    var districtSelect = $('#district1');
                    districtSelect.empty();
                    districtSelect.append('<option value="">Chọn Quận/Huyện</option>');
                    $.each(data, function (index, district) {
                        districtSelect.append('<option value="' + district.DistrictID + '">' + district.DistrictName + '</option>');
                    });
                    $('#ward1').empty().append('<option value="">Chọn Xã/Phường</option>');
                },
                error: function (xhr, status, error) {
                    console.error('AJAX Error:', status, error);
                }
            });
        } else {
            $('#district1').empty().append('<option value="">Chọn Quận/Huyện</option>');
            $('#ward1').empty().append('<option value="">Chọn Xã/Phường</option>');
        }
    });

    $('#district1').change(function () {
        var districtId = $(this).val();
        if (districtId) {
            $.ajax({
                url: '/admin/wards',
                type: 'GET',
                data: {districtId: districtId},
                success: function (data) {
                    console.log('Wards Data:', data); // Kiểm tra dữ liệu nhận được
                    var wardSelect = $('#ward1');
                    wardSelect.empty();
                    wardSelect.append('<option value="">Chọn Xã/Phường</option>');
                    $.each(data, function (index, ward) {
                        wardSelect.append('<option value="' + ward.WardCode + '">' + ward.WardName + '</option>');
                    });
                },
                error: function (xhr, status, error) {
                    console.error('AJAX Error:', status, error);
                }
            });
        } else {
            $('#ward1').empty().append('<option value="">Chọn Xã/Phường</option>');
        }
    });
});