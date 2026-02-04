// Authentication utilities
function isAuthenticated() {
    return localStorage.getItem('token') !== null;
}

function getUser() {
    const userStr = localStorage.getItem('user');
    return userStr ? JSON.parse(userStr) : null;
}

function logout() {
    localStorage.removeItem('token');
    localStorage.removeItem('user');
    window.location.href = 'index.html';
}

function checkAuth(redirectPage) {
    if (!isAuthenticated()) {
        showToast('Tizimga kirish talab qilinadi', 'warning');
        setTimeout(() => {
            window.location.href = 'login.html';
        }, 1500);
        return false;
    }

    if (redirectPage) {
        window.location.href = `${redirectPage}.html`;
    }
    return true;
}

// Update navigation based on auth status
function updateNav() {
    const loginBtn = document.getElementById('loginBtn');
    const registerBtn = document.getElementById('registerBtn');
    const userMenu = document.getElementById('userMenu');

    if (!loginBtn || !registerBtn || !userMenu) return;

    if (isAuthenticated()) {
        const user = getUser();

        loginBtn.style.display = 'none';
        registerBtn.style.display = 'none';
        userMenu.style.display = 'block';

        const userName = document.getElementById('userName');
        const userAvatar = document.getElementById('userAvatar');

        if (userName) userName.textContent = user.username;
        if (userAvatar) {
            userAvatar.src = user.profileImage || 'https://ui-avatars.com/api/?name=' + encodeURIComponent(user.fullName || user.username);
        }
    } else {
        loginBtn.style.display = 'inline-flex';
        registerBtn.style.display = 'inline-flex';
        userMenu.style.display = 'none';
    }
}

// Call on page load
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', updateNav);
} else {
    updateNav();
}

// Export functions
window.isAuthenticated = isAuthenticated;
window.getUser = getUser;
window.logout = logout;
window.checkAuth = checkAuth;
window.updateNav = updateNav;
