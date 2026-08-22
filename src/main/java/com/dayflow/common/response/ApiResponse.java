package com.dayflow.common.response;

/**
 * ApiResponse — Generic API response wrapper used across all Dayflow modules.
 *
 * <p>Every REST endpoint in Dayflow should return this wrapper for consistency:
 * <pre>
 * {
 *   "success": true,
 *   "message": "Operation completed",
 *   "data": { ... }
 * }
 * </pre>
 *
 * @param <T> the type of the data payload
 */
public class ApiResponse<T> {

    /** Indicates whether the operation succeeded */
    private boolean success;

    /** Human-readable message describing the result */
    private String message;

    /** The response payload (null for errors or void operations) */
    private T data;

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    public ApiResponse() {}

    public ApiResponse(boolean success, String message, T data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }

    // -------------------------------------------------------------------------
    // Static factory helpers
    // -------------------------------------------------------------------------

    /**
     * Builds a successful response with a data payload.
     *
     * @param message descriptive message
     * @param data    the payload
     * @param <T>     payload type
     * @return ApiResponse with success=true
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return new ApiResponse<>(true, message, data);
    }

    /**
     * Builds a successful response with no data payload.
     *
     * @param message descriptive message
     * @param <T>     phantom type
     * @return ApiResponse with success=true and null data
     */
    public static <T> ApiResponse<T> success(String message) {
        return new ApiResponse<>(true, message, null);
    }

    /**
     * Builds an error response.
     *
     * @param message error description
     * @param <T>     phantom type
     * @return ApiResponse with success=false and null data
     */
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(false, message, null);
    }

    // -------------------------------------------------------------------------
    // Getters & Setters
    // -------------------------------------------------------------------------

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}
