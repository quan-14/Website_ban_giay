function search() {
    var startDate = document.getElementById('startDate').value;
    var endDate = document.getElementById('endDate').value;
    var kieu = document.getElementById('searchKieu').value;
    var loai = document.getElementById('searchLoai').value;
    var trangThai = document.getElementById('searchTrangThai').value;

    var params = new URLSearchParams();
    if(startDate) params.append('startDate', startDate);
    if(endDate) params.append('endDate', endDate);
    if(kieu) params.append('kieu', kieu);
    if(loai) params.append('loai', loai);
    if(trangThai) params.append('trangThai', trangThai);

    window.location.href = '/admin/phieu-giam-gia/search?' + params.toString();
}
document.addEventListener('DOMContentLoaded', function() {
    var rows = document.querySelectorAll('#phieu-giam-gia-tbody tr');

    rows.forEach(function(row) {
        var statusCell = row.querySelector('.status-pgg');
        var label = statusCell.querySelector('.mauSacStatus');
        var status = label ? label.innerText.trim() : '';

        // Áp dụng lớp CSS tương ứng dựa trên trạng thái
        if (status === 'Kết thúc') {
            statusCell.classList.add('status-ket-thuc');
        } else if (status === 'Đang diễn ra') {
            statusCell.classList.add('status-dang-dien-ra');
        } else if (status === 'Sắp diễn ra') {
            statusCell.classList.add('status-sap-dien-ra');
        }
    });
});

function toggleStatuss(checkbox, phieuGiamGiaId) {
    var xhr = new XMLHttpRequest();
    xhr.open('POST', '/admin/phieu-giam-gia/toggle-status/' + phieuGiamGiaId, true);
    xhr.setRequestHeader('Content-Type', 'application/json');
    xhr.onreadystatechange = function() {
        if (xhr.readyState === XMLHttpRequest.DONE && xhr.status === 200) {
            var response = JSON.parse(xhr.responseText);
            var row = checkbox.closest('tr');
            var statusCell = row.querySelector('.status-pgg');
            var label = statusCell.querySelector('.mauSacStatus');

            // Kiểm tra nếu label tồn tại trước khi thay đổi innerText
            if (label) {
                label.innerText = response.newStatus;
            }

            // Xóa các lớp trạng thái cũ trước khi thêm lớp mới
            statusCell.classList.remove('status-dang-dien-ra', 'status-ket-thuc', 'status-sap-dien-ra');

            if (response.newStatus === 'Kết thúc') {
                statusCell.classList.add('status-ket-thuc');
                checkbox.checked = false;
            } else if (response.newStatus === 'Đang diễn ra') {
                statusCell.classList.add('status-dang-dien-ra');
                checkbox.checked = true;
            } else if (response.newStatus === 'Sắp diễn ra') {
                statusCell.classList.add('status-sap-dien-ra');
                checkbox.checked = true;
            }
        }
    };
    xhr.send();
}
