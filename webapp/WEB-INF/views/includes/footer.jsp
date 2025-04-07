</div> <!-- Close container div -->

    <script>
    document.addEventListener('DOMContentLoaded', function() {
        // Generic form submission handler
        function setupFormSubmit(formId, buttonId) {
            const form = document.getElementById(formId);
            const button = document.getElementById(buttonId);
            
            if (form && button) {
                form.addEventListener('submit', function() {
                    const buttonText = button.querySelector('.button-text');
                    const spinner = button.querySelector('.spinner');
                    
                    button.setAttribute('aria-busy', 'true');
                    button.disabled = true;
                    buttonText.textContent = 'Processing...';
                    spinner.style.display = 'inline-block';
                });
            }
        }

        // Initialize all forms
        setupFormSubmit('searchForm', 'searchButton');
        setupFormSubmit('addForm', 'addButton');
    });
    </script>
</body>
</html>
