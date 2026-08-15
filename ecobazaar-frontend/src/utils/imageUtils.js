// =========================================================
// Image URL Normalization & Fallback Utility
// =========================================================

export const DEFAULT_PLACEHOLDER =
    "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' width='300' height='300'%3E%3Crect fill='%23f3f4f6' width='300' height='300'/%3E%3Ctext fill='%239ca3af' x='50%25' y='50%25' dominant-baseline='middle' text-anchor='middle' font-family='sans-serif' font-size='16'%3ENo Image%3C/text%3E%3C/svg%3E";

/**
 * Normalizes any image string into a valid browser image source.
 * Handles HTTP/HTTPS URLs, full data:image URIs, raw Base64 strings,
 * and falls back to a clean SVG placeholder.
 */
export const getImageSrc = (img) => {
    if (!img || typeof img !== 'string') {
        return DEFAULT_PLACEHOLDER;
    }
    const trimmed = img.trim();
    if (!trimmed || trimmed === 'null' || trimmed === 'undefined') {
        return DEFAULT_PLACEHOLDER;
    }
    if (
        trimmed.startsWith('http://') ||
        trimmed.startsWith('https://') ||
        trimmed.startsWith('data:image') ||
        trimmed.startsWith('/')
    ) {
        return trimmed;
    }
    return `data:image/png;base64,${trimmed}`;
};

/**
 * Image error handler to gracefully fallback to placeholder on broken links/malformed data.
 */
export const handleImageError = (e) => {
    if (e?.currentTarget && e.currentTarget.src !== DEFAULT_PLACEHOLDER) {
        e.currentTarget.src = DEFAULT_PLACEHOLDER;
    }
};
