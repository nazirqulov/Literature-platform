// Main JavaScript for index.html

// Animate stats counter
function animateCounter(element) {
    const target = parseInt(element.getAttribute('data-count'));
    const duration = 2000;
    const increment = target / (duration / 16);
    let current = 0;

    const timer = setInterval(() => {
        current += increment;
        if (current >= target) {
            element.textContent = target.toLocaleString();
            clearInterval(timer);
        } else {
            element.textContent = Math.floor(current).toLocaleString();
        }
    }, 16);
}

//  Observer for stats animation
const statsObserver = new IntersectionObserver((entries) => {
    entries.forEach(entry => {
        if (entry.isIntersecting) {
            const counters = entry.target.querySelectorAll('.stat-number');
            counters.forEach(counter => animateCounter(counter));
            statsObserver.unobserve(entry.target);
        }
    });
});

// Load featured books
async function loadFeaturedBooks() {
    const container = document.getElementById('featuredBooks');
    if (!container) return;

    try {
        const response = await API.getFeaturedBooks();
        const books = response.data;

        if (books.length === 0) {
            container.innerHTML = '<p class="loading">Hozircha kitoblar yo\'q</p>';
            return;
        }

        container.innerHTML = books.map(book => createBookCard(book)).join('');
    } catch (error) {
        console.error('Error loading featured books:', error);
        container.innerHTML = '<p class="loading">Kitoblarni yuklab bo\'lmadi</p>';
    }
}

// Load popular books
async function loadPopularBooks() {
    const container = document.getElementById('popularBooks');
    if (!container) return;

    try {
        const response = await API.getPopularBooks();
        const books = response.data;

        if (books.length === 0) {
            container.innerHTML = '<p class="loading">Hozircha kitoblar yo\'q</p>';
            return;
        }

        container.innerHTML = books.map(book => createBookCard(book)).join('');
    } catch (error) {
        console.error('Error loading popular books:', error);
        container.innerHTML = '<p class="loading">Kitoblarni yuklab bo\'lmadi</p>';
    }
}

// Load latest books
async function loadLatestBooks() {
    const container = document.getElementById('latestBooks');
    if (!container) return;

    try {
        const response = await API.getLatestBooks();
        const books = response.data;

        if (books.length === 0) {
            container.innerHTML = '<p class="loading">Hozircha kitoblar yo\'q</p>';
            return;
        }

        container.innerHTML = books.map(book => createBookCard(book)).join('');
    } catch (error) {
        console.error('Error loading latest books:', error);
        container.innerHTML = '<p class="loading">Kitoblarni yuklab bo\'lmadi</p>';
    }
}

// Create book card HTML
function createBookCard(book) {
    const rating = book.averageRating || 0;
    const stars = '⭐'.repeat(Math.round(rating));

    return `
        <div class="book-card" onclick="window.location.href='book-details.html?id=${book.id}'">
            <img 
                src="${book.coverImage || 'https://via.placeholder.com/300x450?text=' + encodeURIComponent(book.title)}" 
                alt="${book.title}"
                class="book-cover"
            >
            <div class="book-info">
                <h3 class="book-title">${book.title}</h3>
                <p class="book-author">${book.author?.name || 'Noma\'lum'}</p>
                <div class="book-meta">
                    <span class="book-rating">${stars} ${rating.toFixed(1)}</span>
                    <span class="book-year">${book.publishedYear || ''}</span>
                </div>
            </div>
        </div>
    `;
}

// Hero search
function setup SearchHero() {
    const searchInput = document.getElementById('heroSearch');
    const searchBtn = searchInput?.nextElementSibling;

    if (!searchInput || !searchBtn) return;

    searchBtn.addEventListener('click', () => {
        const keyword = searchInput.value.trim();
        if (keyword) {
            window.location.href = `books.html?search=${encodeURIComponent(keyword)}`;
        }
    });

    searchInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') {
            const keyword = searchInput.value.trim();
            if (keyword) {
                window.location.href = `books.html?search=${encodeURIComponent(keyword)}`;
            }
        }
    });
}

// Mobile menu toggle
function setupMobileMenu() {
    const mobileMenuBtn = document.getElementById('mobileMenuBtn');
    const navMenu = document.getElementById('navMenu');

    if (!mobileMenuBtn || !navMenu) return;

    mobileMenuBtn.addEventListener('click', () => {
        navMenu.classList.toggle('active');
        mobileMenuBtn.classList.toggle('active');
    });
}

// Initialize page
document.addEventListener('DOMContentLoaded', async () => {
    // Setup event listeners
    setupHeroSearch();
    setupMobileMenu();

    // Observe stats section for animation
    const statsSection = document.querySelector('.stats-section');
    if (statsSection) {
        statsObserver.observe(statsSection);
    }

    // Load all book sections
    await Promise.all([
        loadFeaturedBooks(),
        loadPopularBooks(),
        loadLatestBooks()
    ]);
});

// Toast notification
function showToast(message, type = 'info') {
    const existingToast = document.querySelector('.toast');
    if (existingToast) existingToast.remove();

    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.textContent = message;
    document.body.appendChild(toast);

    setTimeout(() => toast.classList.add('show'), 100);

    setTimeout(() => {
        toast.classList.remove('show');
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

// Export for global use
window.showToast = showToast;
window.createBookCard = createBookCard;
