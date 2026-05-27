document.addEventListener('DOMContentLoaded', function () {
    const canvas = document.getElementById('signature-pad');
    const modalEl = document.getElementById('signatureModal');
    let signaturePad; // Keep it globally accessible within this scope

    modalEl.addEventListener('shown.bs.modal', function (event) {
        // 1. First, handle the ID and Action logic
        const button = event.relatedTarget;
        document.getElementById('modal-target-id').value = button.getAttribute('data-id');
        document.getElementById('final-sign-form').action = button.getAttribute('data-action');

        // Find the card where this button lives
        const card = button.closest('.card');

        // Get all checked 'selectedExtras' in THIS card
        const checkedExtras = Array.from(card.querySelectorAll('input[name="selectedExtras"]:checked'))
            .map(cb => cb.value);

        // Clear any old dynamically added hidden inputs in the modal to avoid duplicates
        const form = document.getElementById('final-sign-form');
        form.querySelectorAll('.temp-extra').forEach(el => el.remove());

        // Append a hidden input for EACH selected ID so Spring can map it to a Set/List
        checkedExtras.forEach(id => {
            const input = document.createElement('input');
            input.type = 'hidden';
            input.name = 'selectedExtras'; // Must match controller @RequestParam
            input.value = id;
            input.className = 'temp-extra';
            form.appendChild(input);
        });

        // 2. CRITICAL: Resize the canvas FIRST
        const ratio = Math.max(window.devicePixelRatio || 1, 1);
        canvas.width = canvas.offsetWidth * ratio;
        canvas.height = canvas.offsetHeight * ratio;
        canvas.getContext("2d").scale(ratio, ratio);

        // 3. RE-INITIALIZE the Pad here.
        // This forces the library to recalculate the coordinates based on the new size.
        signaturePad = new SignaturePad(canvas, {
            backgroundColor: 'rgb(255, 255, 255)',
            penColor: 'rgb(0, 0, 0)'
        });

        signaturePad.clear(); // Ensure it's fresh
    });

    // Clear Button
    document.getElementById('clear-btn').addEventListener('click', () => {
        if (signaturePad) signaturePad.clear();
    });

    // Confirm Button
    document.getElementById('confirm-btn').addEventListener('click', function() {
        if (!signaturePad || signaturePad.isEmpty()) {
            alert("Please provide a signature.");
            return;
        }
        document.getElementById('modal-signature-data').value = signaturePad.toDataURL();
        document.getElementById('final-sign-form').submit();
    });
});






// $(document).ready(function() {
//     let signaturePad;
//     const canvas = document.getElementById('signature-pad');
//     const $modal = $('#signatureModal');
//
//     function resizeCanvas() {
//         const wrapper = document.getElementById('canvas-wrapper');
//         const ratio = Math.max(window.devicePixelRatio || 1, 1);
//         canvas.width = wrapper.offsetWidth * ratio;
//         canvas.height = wrapper.offsetHeight * ratio;
//         canvas.getContext("2d").setTransform(ratio, 0, 0, ratio, 0, 0); // Cleaner scale reset
//     }
//
//     $modal.on('shown.bs.modal', function (event) {
//         const button = $(event.relatedTarget);
//
//         // Find the card that contains the button
//         const $card = button.closest('.card-body');
//
//         // Find all checked 'selectedExtras' inside THIS card
//         const selectedIds = $card.find('input[name="selectedExtras"]:checked')
//             .map(function() { return this.value; })
//             .get()
//             .join(','); // Creates "1,2,3"
//
//         // Store the list of IDs in your hidden field
//         $('#hiddenSelectedExtras').val(selectedIds);
//
//         // Pull configuration from the button that clicked it
//         const actionUrl = button.data('action'); // e.g., /mobile/voice-plan/5
//         const entityId = button.data('id');     // e.g., 5
//
//         // Store these in the form hidden fields
//         $('#modal-target-id').val(entityId);
//         $('#final-sign-form').attr('action', actionUrl);
//
//         resizeCanvas();
//
//         if (!signaturePad) {
//             signaturePad = new SignaturePad(canvas, { backgroundColor: 'rgb(255, 255, 255)' });
//         } else {
//             signaturePad.clear();
//         }
//     });
//
//     $('#clear-btn').on('click', function() {
//         if (signaturePad) signaturePad.clear();
//     });
//
//     $('#confirm-btn').on('click', function() {
//         if (!signaturePad || signaturePad.isEmpty()) {
//             alert("Please provide a signature.");
//             return;
//         }
//
//         // Inject the base64 data and submit to the URL defined by the button
//         $('#modal-signature-data').val(signaturePad.toDataURL());
//         $('#final-sign-form').submit();
//     });
// });
