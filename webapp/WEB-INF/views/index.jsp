<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
  <head>
    <title>LibraryHaven - Digital Library</title>
    <link rel="stylesheet" type="text/css" href="styles.css" />
  </head>
  <body>
    <header class="header">
      <a href="index.jsp" class="logo">
        <svg
          width="24"
          height="24"
          viewBox="0 0 24 24"
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
        >
          <path
            d="M4 19.5C4 18.837 4.26339 18.2011 4.73223 17.7322C5.20107 17.2634 5.83696 17 6.5 17H20"
            stroke="#6c5ce7"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          />
          <path
            d="M6.5 2H20V22H6.5C5.83696 22 5.20107 21.7366 4.73223 21.2678C4.26339 20.7989 4 20.163 4 19.5V4.5C4 3.83696 4.26339 3.20107 4.73223 2.73223C5.20107 2.26339 5.83696 2 6.5 2Z"
            stroke="#6c5ce7"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          />
        </svg>
        LibraryHaven
      </a>
      <div class="auth-buttons">
        <a href="signup.jsp" class="btn btn-outline">Register</a>
        <a href="login.jsp" class="btn btn-primary">Login</a>
      </div>
    </header>

    <section class="hero">
      <h1>Discover a World of Digital Knowledge</h1>
      <p>
        Books, Audiobooks, Movies, and Magazines - All in One Digital Library
      </p>

      <div class="search-container">
        <form action="search" method="get">
          <input
            type="text"
            name="query"
            class="search-input"
            placeholder="Search books, authors, topics..."
          />
          <input type="hidden" name="type" value="all" />
          <button type="submit" class="btn btn-primary">Search</button>
        </form>
      </div>
    </section>

    <section class="categories">
      <div class="category-card">
        <svg
          class="category-icon"
          viewBox="0 0 24 24"
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
        >
          <path
            d="M4 19.5C4 18.837 4.26339 18.2011 4.73223 17.7322C5.20107 17.2634 5.83696 17 6.5 17H20"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          />
          <path
            d="M6.5 2H20V22H6.5C5.83696 22 5.20107 21.7366 4.73223 21.2678C4.26339 20.7989 4 20.163 4 19.5V4.5C4 3.83696 4.26339 3.20107 4.73223 2.73223C5.20107 2.26339 5.83696 2 6.5 2Z"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          />
        </svg>
        <h3>BOOKS</h3>
        <p>Discover our collection of digital books</p>
        <a href="books.jsp" class="btn btn-outline">Explore</a>
      </div>

      <div class="category-card">
        <svg
          class="category-icon"
          viewBox="0 0 24 24"
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
        >
          <path
            d="M19 3H5C3.89543 3 3 3.89543 3 5V19C3 20.1046 3.89543 21 5 21H19C20.1046 21 21 20.1046 21 19V5C21 3.89543 20.1046 3 19 3Z"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          />
          <path
            d="M7 7H17"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          />
          <path
            d="M7 12H17"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          />
          <path
            d="M7 17H17"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          />
        </svg>
        <h3>MAGAZINE</h3>
        <p>Read the latest magazines digitally</p>
        <a href="magazines.jsp" class="btn btn-outline">Start Reading</a>
      </div>

      <div class="category-card">
        <svg
          class="category-icon"
          viewBox="0 0 24 24"
          fill="none"
          xmlns="http://www.w3.org/2000/svg"
        >
          <path
            d="M23 7L16 12L23 17V7Z"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          />
          <path
            d="M14 5H3C1.89543 5 1 5.89543 1 7V17C1 18.1046 1.89543 19 3 19H14C15.1046 19 16 18.1046 16 17V7C16 5.89543 15.1046 5 14 5Z"
            stroke="currentColor"
            stroke-width="2"
            stroke-linecap="round"
            stroke-linejoin="round"
          />
        </svg>
        <h3>MEDIA</h3>
        <p>Stream digital media content</p>
        <a href="media.jsp" class="btn btn-outline">Watch Now</a>
      </div>
    </section>
  </body>
</html>
