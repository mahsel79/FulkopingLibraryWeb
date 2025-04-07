<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${pageTitle}"/></title>
    <link rel="stylesheet" type="text/css" href="/styles.css">
    <link rel="stylesheet" type="text/css" href="/shared.css">
    <script src="/js/form-validation.js" defer></script>
    <style>
        @media (max-width: 768px) {
            .container {
                padding: 1rem;
            }
            .form-row {
                flex-direction: column;
            }
            .form-group {
                width: 100%;
                margin-bottom: 1rem;
            }
            .table-responsive {
                overflow-x: auto;
                -webkit-overflow-scrolling: touch;
            }
            .btn-primary {
                width: 100%;
                padding: 0.75rem;
                margin-top: 0.5rem;
            }
            .search-section {
                margin-bottom: 1.5rem;
            }
            .error-message {
                font-size: 0.9rem;
                margin-top: 0.25rem;
            }
        }
        @media (min-width: 769px) and (max-width: 1024px) {
            .form-group {
                width: 48%;
            }
        }
    </style>
</head>
<body>
<div class="container">
