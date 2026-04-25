/**
 * Admin Sidebar Toggle Script
 * Handles collapsible sidebar functionality across all admin pages
 */

document.addEventListener('DOMContentLoaded', function() {
    const sidebar = document.getElementById('adminSidebar');
    const sidebarToggle = document.getElementById('sidebarToggle');
    const mainContent = document.querySelector('.admin-main');

    // Sidebar toggle functionality
    if (sidebarToggle && sidebar) {
        sidebarToggle.addEventListener('click', function() {
            sidebar.classList.toggle('collapsed');
            if (mainContent) {
                mainContent.classList.toggle('sidebar-collapsed');
            }
            sidebarToggle.classList.toggle('collapsed');

            // Save state to localStorage
            const isCollapsed = sidebar.classList.contains('collapsed');
            localStorage.setItem('sidebarCollapsed', isCollapsed);
        });

        // Restore sidebar state from localStorage
        const savedState = localStorage.getItem('sidebarCollapsed');
        if (savedState === 'true') {
            sidebar.classList.add('collapsed');
            if (mainContent) {
                mainContent.classList.add('sidebar-collapsed');
            }
            sidebarToggle.classList.add('collapsed');
        }
    }

    // Mobile menu toggle
    const mobileMenuToggle = document.getElementById('mobileMenuToggle');
    if (mobileMenuToggle && sidebar) {
        mobileMenuToggle.addEventListener('click', function() {
            sidebar.classList.toggle('mobile-open');
        });

        // Close sidebar when clicking outside on mobile
        document.addEventListener('click', function(e) {
            if (window.innerWidth <= 768 &&
                !sidebar.contains(e.target) &&
                !mobileMenuToggle.contains(e.target)) {
                sidebar.classList.remove('mobile-open');
            }
        });
    }
});
