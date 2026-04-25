package com.computershop.exception;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Global exception handler for the application.
 * Handles exceptions across all controllers and provides user-friendly error messages.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles ResourceNotFoundException.
     * Redirects to an error page or returns an error view.
     *
     * @param ex the exception
     * @param model the model to add error attributes
     * @return the error view name
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public String handleResourceNotFound(ResourceNotFoundException ex, Model model) {
        model.addAttribute("errorTitle", "Resource Not Found");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/404"; // Create this error page if needed
    }

    /**
     * Handles BusinessException.
     * Redirects back to the previous page with an error message.
     *
     * @param ex the exception
     * @param redirectAttributes the redirect attributes
     * @return redirect URL
     */
    @ExceptionHandler(BusinessException.class)
    public String handleBusinessException(BusinessException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/"; // Redirect to home or previous page
    }

    /**
     * Handles generic exceptions.
     * Catches all unexpected errors and displays a generic error message.
     *
     * @param ex the exception
     * @param model the model to add error attributes
     * @return the error view name
     */
    @ExceptionHandler(Exception.class)
    public String handleGenericException(Exception ex, Model model) {
        model.addAttribute("errorTitle", "System Error");
        // Build full stack trace string for debugging
        StringBuilder sb = new StringBuilder();
        sb.append(ex.getClass().getName()).append(": ").append(ex.getMessage());
        Throwable cause = ex.getCause();
        while (cause != null) {
            sb.append(" | CAUSE: ").append(cause.getClass().getSimpleName()).append(": ").append(cause.getMessage());
            cause = cause.getCause();
        }
        for (StackTraceElement el : ex.getStackTrace()) {
            if (el.getClassName().startsWith("com.computershop")) {
                sb.append(" | AT: ").append(el.toString());
                break;
            }
        }
        model.addAttribute("errorMessage", sb.toString());
        ex.printStackTrace();
        return "error/500";
    }

    /**
     * Handles IllegalArgumentException.
     *
     * @param ex the exception
     * @param model the model
     * @return the error view
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleIllegalArgument(IllegalArgumentException ex, Model model) {
        model.addAttribute("errorTitle", "Invalid Request");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error/400";
    }
}
