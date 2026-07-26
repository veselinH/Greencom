document.addEventListener('DOMContentLoaded', function () {
    const canvas = document.getElementById('signature-pad');
    const modalEl = document.getElementById('signatureModal');
    let signaturePad;

    modalEl.addEventListener('shown.bs.modal', function (event) {
        const button = event.relatedTarget;
        document.getElementById('modal-target-id').value = button.getAttribute('data-id');
        document.getElementById('final-sign-form').action = button.getAttribute('data-action');

        const card = button.closest('.card');

        const checkedExtras = Array.from(card.querySelectorAll('input[name="selectedExtras"]:checked'))
            .map(cb => cb.value);

        const form = document.getElementById('final-sign-form');
        form.querySelectorAll('.temp-extra').forEach(el => el.remove());

        checkedExtras.forEach(id => {
            const input = document.createElement('input');
            input.type = 'hidden';
            input.name = 'selectedExtras';
            input.value = id;
            input.className = 'temp-extra';
            form.appendChild(input);
        });

        const ratio = Math.max(window.devicePixelRatio || 1, 1);
        canvas.width = canvas.offsetWidth * ratio;
        canvas.height = canvas.offsetHeight * ratio;
        canvas.getContext("2d").scale(ratio, ratio);

        signaturePad = new SignaturePad(canvas, {
            backgroundColor: 'rgb(255, 255, 255)',
            penColor: 'rgb(0, 0, 0)'
        });

        signaturePad.clear();
    });

    document.getElementById('clear-btn').addEventListener('click', () => {
        if (signaturePad) signaturePad.clear();
    });

    document.getElementById('confirm-btn').addEventListener('click', function() {
        if (!signaturePad || signaturePad.isEmpty()) {
            alert("Please provide a signature.");
            return;
        }
        document.getElementById('modal-signature-data').value = signaturePad.toDataURL();
        document.getElementById('final-sign-form').submit();
    });
});
