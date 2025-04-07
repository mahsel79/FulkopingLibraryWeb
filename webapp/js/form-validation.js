class FormValidator {
    static clearError(element) {
        const errorElement = document.getElementById(`${element.id}-error`);
        if (errorElement) {
            errorElement.textContent = '';
            errorElement.style.display = 'none';
        }
        element.classList.remove('is-invalid');
    }

    static showError(element, message) {
        const errorElement = document.getElementById(`${element.id}-error`);
        if (errorElement) {
            errorElement.textContent = message;
            errorElement.style.display = 'block';
        }
        element.classList.add('is-invalid');
    }

    static showLoading(form) {
        const submitButton = form.querySelector('button[type="submit"]');
        if (submitButton) {
            submitButton.disabled = true;
            const originalText = submitButton.dataset.originalText || submitButton.textContent;
            submitButton.dataset.originalText = originalText;
            submitButton.innerHTML = `<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span> Processing...`;
        }
    }

    static validateBooksForm(form) {
        let isValid = true;
        const fields = ['title', 'author', 'isbn', 'publisher', 'year'];

        fields.forEach(fieldId => {
            const field = document.getElementById(fieldId);
            this.clearError(field);

            if (!field.value.trim()) {
                this.showError(field, 'This field is required');
                isValid = false;
            }
        });

        // Validate ISBN format
        const isbn = document.getElementById('isbn');
        if (!isbn.checkValidity()) {
            this.showError(isbn, 'Please enter a valid ISBN');
            isValid = false;
        }

        // Validate year format
        const year = document.getElementById('year');
        if (!year.checkValidity()) {
            this.showError(year, 'Please enter a valid year (1900-2099)');
            isValid = false;
        }

        return isValid;
    }
}

// Initialize form validation for search form
document.addEventListener('DOMContentLoaded', () => {
    const searchForm = document.getElementById('searchForm');
    if (searchForm) {
        searchForm.addEventListener('submit', function(e) {
            e.preventDefault();
            const searchType = document.getElementById('searchType');
            const searchQuery = document.getElementById('searchQuery');
            let isValid = true;

            FormValidator.clearError(searchType);
            FormValidator.clearError(searchQuery);

            if (!searchType.value) {
                FormValidator.showError(searchType, 'Please select a search type');
                isValid = false;
            }

            if (searchQuery.value.length < 2) {
                FormValidator.showError(searchQuery, 'Search query must be at least 2 characters long');
                isValid = false;
            }

            if (isValid) {
                FormValidator.showLoading(this);
                this.submit();
            }
        });
    }

    // Initialize form validation for add book form
    const addBookForm = document.getElementById('addBookForm');
    if (addBookForm) {
        addBookForm.addEventListener('submit', function(e) {
            e.preventDefault();
            if (FormValidator.validateBooksForm(this)) {
                FormValidator.showLoading(this);
                this.submit();
            }
        });
    }
});
