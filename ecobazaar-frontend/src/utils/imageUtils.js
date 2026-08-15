// =========================================================
// Image URL Normalization & Fallback Utility
// =========================================================

export const DEFAULT_PLACEHOLDER = 'https://placehold.co/100x100?text=Product';

/**
 * Normalizes any image string into a safe browser image source.
 * Handles HTTP/HTTPS URLs, full data:image URIs, raw Base64 strings,
 * and falls back to a clean placeholder.
 */
export const formatImageSrc = (img) => {
    if (!img || typeof img !== 'string') {
        return DEFAULT_PLACEHOLDER;
    }
    const trimmed = img.trim();
    if (!trimmed || trimmed === 'null' || trimmed === 'undefined') {
        return DEFAULT_PLACEHOLDER;
    }
    if (trimmed.startsWith('http://') || trimmed.startsWith('https://')) {
        return trimmed;
    }
    if (trimmed.startsWith('data:image/')) {
        // Strip any unexpected whitespace or newlines inside the data URL
        return trimmed.replace(/[\r\n\s]+/g, '');
    }
    if (trimmed.startsWith('/')) {
        return trimmed;
    }
    // Clean raw base64 string and prepend data URI scheme
    const cleanBase64 = trimmed.replace(/[\r\n\s]+/g, '');
    return `data:image/jpeg;base64,${cleanBase64}`;
};

// Backwards compatibility alias
export const getImageSrc = formatImageSrc;

/**
 * Image error handler to gracefully fallback to placeholder on broken links/malformed data.
 */
export const handleImageError = (e) => {
    if (e?.currentTarget && e.currentTarget.src !== DEFAULT_PLACEHOLDER) {
        e.currentTarget.src = DEFAULT_PLACEHOLDER;
    }
};
