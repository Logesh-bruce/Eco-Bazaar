// =========================================================
// Image URL Normalization & Fallback Utility
// =========================================================

export const DEFAULT_PLACEHOLDER = 'https://images.unsplash.com/photo-1542601906990-b4d3fb778b09?w=500&auto=format&fit=crop&q=60';

/**
 * Safely resolves any product object, base64 string, or remote image URL.
 * Prevents treating raw base64 data (which often starts with '/9j/') as a relative URL,
 * completely eliminating HTTP 414 (URI Too Long) errors.
 */
export const getProductImage = (item) => {
    if (!item) return DEFAULT_PLACEHOLDER;

    // Case 1: item is a string (e.g. imageBase64 or imageUrl directly)
    if (typeof item === 'string') {
        const trimmed = item.trim();
        if (!trimmed || trimmed === 'null' || trimmed === 'undefined') {
            return DEFAULT_PLACEHOLDER;
        }
        if (trimmed.startsWith('http://') || trimmed.startsWith('https://')) {
            return trimmed;
        }
        if (trimmed.startsWith('data:image/') || trimmed.startsWith('data:')) {
            return trimmed.replace(/[\r\n\s]+/g, '');
        }
        // Genuine relative asset path (short filename ending in image extension)
        if (trimmed.startsWith('/') && (trimmed.endsWith('.png') || trimmed.endsWith('.jpg') || trimmed.endsWith('.jpeg') || trimmed.endsWith('.svg') || trimmed.endsWith('.webp')) && trimmed.length < 150) {
            return trimmed;
        }
        // Treat as raw base64 data
        const cleanBase64 = trimmed.replace(/[\r\n\s]+/g, '');
        return `data:image/jpeg;base64,${cleanBase64}`;
    }

    // Case 2: item is a product object
    const rawBase64 = item.imageBase64;
    if (rawBase64 && typeof rawBase64 === 'string') {
        const trimmed = rawBase64.trim();
        if (trimmed && trimmed !== 'null' && trimmed !== 'undefined') {
            if (trimmed.startsWith('http://') || trimmed.startsWith('https://')) {
                return trimmed;
            }
            if (trimmed.startsWith('data:image/') || trimmed.startsWith('data:')) {
                return trimmed.replace(/[\r\n\s]+/g, '');
            }
            if (trimmed.startsWith('/') && (trimmed.endsWith('.png') || trimmed.endsWith('.jpg') || trimmed.endsWith('.jpeg') || trimmed.endsWith('.svg') || trimmed.endsWith('.webp')) && trimmed.length < 150) {
                return trimmed;
            }
            const clean = trimmed.replace(/[\r\n\s]+/g, '');
            return `data:image/jpeg;base64,${clean}`;
        }
    }

    const rawUrl = item.imageUrl || item.image;
    if (rawUrl && typeof rawUrl === 'string') {
        const trimmed = rawUrl.trim();
        if (trimmed && trimmed !== 'null' && trimmed !== 'undefined') {
            if (trimmed.startsWith('http://') || trimmed.startsWith('https://')) {
                return trimmed;
            }
            if (trimmed.startsWith('data:image/') || trimmed.startsWith('data:')) {
                return trimmed.replace(/[\r\n\s]+/g, '');
            }
            if (trimmed.startsWith('/') && (trimmed.endsWith('.png') || trimmed.endsWith('.jpg') || trimmed.endsWith('.jpeg') || trimmed.endsWith('.svg') || trimmed.endsWith('.webp')) && trimmed.length < 150) {
                return trimmed;
            }
            const clean = trimmed.replace(/[\r\n\s]+/g, '');
            return `data:image/jpeg;base64,${clean}`;
        }
    }

    return DEFAULT_PLACEHOLDER;
};

// Aliases for backwards compatibility
export const formatImageSrc = getProductImage;
export const getImageSrc = getProductImage;

/**
 * Image error handler to gracefully fallback to placeholder on broken links/malformed data.
 */
export const handleImageError = (e) => {
    if (e?.currentTarget && e.currentTarget.src !== DEFAULT_PLACEHOLDER) {
        e.currentTarget.src = DEFAULT_PLACEHOLDER;
    }
};

