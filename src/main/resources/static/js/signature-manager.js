$(document).ready(function() {
    let signaturePad;
    const canvas = document.getElementById('signature-pad');
    const $modal = $('#signatureModal');

    function resizeCanvas() {
        const wrapper = document.getElementById('canvas-wrapper');
        const ratio = Math.max(window.devicePixelRatio || 1, 1);
        canvas.width = wrapper.offsetWidth * ratio;
        canvas.height = wrapper.offsetHeight * ratio;
        canvas.getContext("2d").setTransform(ratio, 0, 0, ratio, 0, 0); // Cleaner scale reset
    }

    $modal.on('shown.bs.modal', function (event) {
        const button = $(event.relatedTarget);

        // Pull configuration from the button that clicked it
        const actionUrl = button.data('action'); // e.g., /mobile/voice-plan/5
        const entityId = button.data('id');     // e.g., 5

        // Store these in the form hidden fields
        $('#modal-target-id').val(entityId);
        $('#final-sign-form').attr('action', actionUrl);

        resizeCanvas();

        if (!signaturePad) {
            signaturePad = new SignaturePad(canvas, { backgroundColor: 'rgb(255, 255, 255)' });
        } else {
            signaturePad.clear();
        }
    });

    $('#clear-btn').on('click', function() {
        if (signaturePad) signaturePad.clear();
    });

    $('#confirm-btn').on('click', function() {
        if (!signaturePad || signaturePad.isEmpty()) {
            alert("Please provide a signature.");
            return;
        }

        // Inject the base64 data and submit to the URL defined by the button
        $('#modal-signature-data').val(signaturePad.toDataURL());
        $('#final-sign-form').submit();
    });
});
