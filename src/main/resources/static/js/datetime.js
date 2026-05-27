document.addEventListener('DOMContentLoaded', function () {
    const dateEl = document.getElementById('nav-date');
    const timeEl = document.getElementById('nav-time');

    // 1. Clock
    function updateClock() {
        if (!dateEl || !timeEl) return;
        const now = new Date();
        dateEl.textContent = now.toLocaleDateString('en-US', { weekday: 'short', month: 'short', day: 'numeric' });
        timeEl.textContent = now.toLocaleTimeString('en-GB');
    }
    setInterval(updateClock, 1000);
    updateClock();
});
