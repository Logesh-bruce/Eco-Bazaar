import { useEffect, useState } from 'react';
import { Star, User } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import api from '../api/axios';

const ProductReviews = ({ productId }) => {
    const { user } = useAuth();
    const [reviews, setReviews] = useState([]);
    const [rating, setRating] = useState(5);
    const [comment, setComment] = useState('');
    const [submitting, setSubmitting] = useState(false);

    useEffect(() => {
        fetchReviews();
    }, [productId]);

    const fetchReviews = async () => {
        try {
            const res = await api.get(`/reviews/product/${productId}`);
            setReviews(res.data);
        } catch (error) {
            console.error("Failed to load reviews");
        }
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        if (!user) return alert("Please login to review!");
        
        setSubmitting(true);
        try {
            await api.post('/reviews', {
                userId: user.id,
                productId: parseInt(productId),
                rating: parseInt(rating),
                comment
            });
            setComment('');
            fetchReviews(); // Refresh list
        } catch (error) {
            alert("Failed to submit review");
        } finally {
            setSubmitting(false);
        }
    };

    return (
        <div className="mt-12 pt-8 border-t border-gray-100">
            <h3 className="text-2xl font-bold mb-6">Customer Reviews ({reviews.length})</h3>

            {/* --- Review Form --- */}
            {user && (
                <div className="bg-gray-50 p-6 rounded-xl mb-8">
                    <h4 className="font-bold mb-4">Write a Review</h4>
                    <form onSubmit={handleSubmit}>
                        <div className="flex items-center gap-2 mb-4">
                            <span className="text-sm text-gray-600">Rating:</span>
                            {[1, 2, 3, 4, 5].map(num => (
                                <button 
                                    type="button" 
                                    key={num}
                                    onClick={() => setRating(num)}
                                    className={`focus:outline-none transition ${rating >= num ? 'text-yellow-400' : 'text-gray-300'}`}
                                >
                                    <Star fill="currentColor" size={24} />
                                </button>
                            ))}
                        </div>
                        <textarea 
                            value={comment}
                            onChange={e => setComment(e.target.value)}
                            placeholder="Share your thoughts..."
                            className="w-full p-3 border rounded-lg mb-3 bg-white"
                            rows="3"
                            required
                        />
                        <button disabled={submitting} className="bg-gray-900 text-white px-6 py-2 rounded-lg font-bold hover:bg-eco-green transition">
                            {submitting ? 'Posting...' : 'Post Review'}
                        </button>
                    </form>
                </div>
            )}

            {/* --- Review List --- */}
            <div className="space-y-6">
                {reviews.length === 0 ? (
                    <p className="text-gray-500 italic">No reviews yet. Be the first!</p>
                ) : (
                    reviews.map(review => (
                        <div key={review.id} className="border-b border-gray-100 pb-6 last:border-0">
                            <div className="flex items-center justify-between mb-2">
                                <div className="flex items-center gap-2">
                                    <div className="bg-gray-200 p-2 rounded-full">
                                        <User size={16} className="text-gray-500" />
                                    </div>
                                    <span className="font-bold text-gray-800">{review.userName}</span>
                                    {review.verified && (
                                        <span className="text-xs text-green-600 font-bold bg-green-50 px-2 py-0.5 rounded-full">Verified Purchase</span>
                                    )}
                                </div>
                                <span className="text-xs text-gray-400">
                                    {new Date(review.createdAt).toLocaleDateString()}
                                </span>
                            </div>
                            <div className="flex text-yellow-400 mb-2">
                                {[...Array(5)].map((_, i) => (
                                    <Star key={i} size={14} fill={i < review.rating ? "currentColor" : "none"} className={i >= review.rating ? "text-gray-300" : ""} />
                                ))}
                            </div>
                            <p className="text-gray-600">{review.comment}</p>
                        </div>
                    ))
                )}
            </div>
        </div>
    );
};

export default ProductReviews;