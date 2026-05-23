function showLoader() {
    document.getElementById('paymentFormSection').style.display = 'none';
    document.getElementById('loadingOverlay').style.display = 'block';
}

function validateAndPay() {
    const cardNum = document.getElementById('cardNumber').value.replace(/\s+/g, '');
    const expiry = document.getElementById('cardExpiry').value;
    const cvc = document.getElementById('cardCVC').value;

    const isCardValid = /^\d{16}$/.test(cardNum);
    const isExpiryValid = /^\d{2}\/\d{2}$/.test(expiry);
    const isCVCValid = /^\d{3}$/.test(cvc);

    if (!isCardValid || !isExpiryValid || !isCVCValid) {
        alert("Please enter valid card details:\n- Card: 16 digits\n- Expiry: MM/YY\n- CVC: 3 digits");
        return;
    }

    // Date Validation
    const expirySplit = expiry.split('/');
    const inputMonth = parseInt(expirySplit[0]);
    const inputYear = parseInt("20" + expirySplit[1]);

    const now = new Date();
    const currentMonth = now.getMonth() + 1;
    const currentYear = now.getFullYear();

    // Month check
    if (inputMonth < 1 || inputMonth > 12) {
        alert("Month must be between 01 and 12.");
        return;
    }

    // Expiry check
    if (inputYear < currentYear || (inputYear === currentYear && inputMonth < currentMonth)) {
        alert("This card has expired!");
        return; // Stops the loader and prevents submission
    }

    // SUCCESS
    const form = document.getElementById('confirmPaymentForm');
    if (form) {
        showLoader();
        setTimeout(function() {
            form.submit();
        }, 3000);
    }
}

document.addEventListener('DOMContentLoaded', function() {
    const cardInput = document.getElementById('cardNumber');
    if (cardInput) {
        cardInput.addEventListener('input', function (e) {
            e.target.value = e.target.value.replace(/[^\d]/g, '').replace(/(.{4})/g, '$1 ').trim();
        });
    }
});

