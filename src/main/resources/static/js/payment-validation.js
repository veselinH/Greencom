/**
 * payment.js - Handles Bootstrap 5 Modal Payment Flow
 */
document.addEventListener('DOMContentLoaded', function () {
    const modalEl = document.getElementById('penaltyModal');
    const form = document.getElementById('confirmPaymentForm');
    const cardInput = document.getElementById('cardNumber');
    const expiryInput = document.getElementById('cardExpiry');

    if (!modalEl) return;

    // 1. AUTO-OPEN CHECK
    // We read the 'data-show' attribute set by Thymeleaf in the HTML
    const shouldShow = modalEl.getAttribute('data-show') === 'true';
    if (shouldShow) {
        const myModal = new bootstrap.Modal(modalEl);
        myModal.show();
    }

    // 2. REAL-TIME FORMATTING
    cardInput?.addEventListener('input', (e) => {
        e.target.value = e.target.value.replace(/\D/g, '').replace(/(.{4})/g, '$1 ').trim();
    });

    expiryInput?.addEventListener('input', (e) => {
        let val = e.target.value.replace(/\D/g, '');
        if (val.length >= 2) e.target.value = val.substring(0, 2) + '/' + val.substring(2, 4);
    });

// 3. VALIDATION & SUBMISSION
    form?.addEventListener('submit', function (e) {
        e.preventDefault();

        const cardNum = cardInput.value.replace(/\s+/g, '');
        const expiry = expiryInput.value;
        const cvc = document.getElementById('cardCVC').value;

        // Validate Card Number
        if (!/^\d{16}$/.test(cardNum)) {
            alert("Please enter a valid 16-digit card number.");
            return;
        }

        // Validate Expiry Format
        if (!/^\d{2}\/\d{2}$/.test(expiry)) {
            alert("Expiry must be in MM/YY format.");
            return;
        }

        // Validate CVC
        if (!/^\d{3}$/.test(cvc)) {
            alert("CVC must be 3 digits.");
            return;
        }

        // Validate Date Logic (The "13" month check)
        const [m, y] = expiry.split('/').map(Number);
        const now = new Date();
        const currentMonth = now.getMonth() + 1;
        const currentYear = now.getFullYear() % 100;

        if (m < 1 || m > 12) {
            alert("Month must be between 01 and 12.");
            return;
        }

        if (y < currentYear || (y === currentYear && m < currentMonth)) {
            alert("This card has expired.");
            return;
        }

        // If all pass, proceed...
        document.getElementById('paymentFormSection').style.display = 'none';
        document.getElementById('loadingOverlay').style.display = 'block';
        setTimeout(() => form.submit(), 1500);
    });

});
