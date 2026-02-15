import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import api from '../api/axios';
import { CreditCard, Truck, CheckCircle } from 'lucide-react';

const Checkout = () => {
    const { user } = useAuth();
    const navigate = useNavigate();
    const [loading, setLoading] = useState(false);
    const [success, setSuccess] = useState(false);
    const [orderId, setOrderId] = useState(null);

    const [formData, setFormData] = useState({
        address: '',
        phone: '',
        paymentMethod: 'Credit Card'
    });

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);

        try {
            // Call the Order API
            const response = await api.post(`/orders/${user.id}`, formData);
            
            if (response.data.success) {
                setSuccess(true);
                setOrderId(response.data.orderId);
            }
        } catch (error) {
            console.error("Checkout failed", error);
            alert("Failed to place order. Please try again.");
        } finally {
            setLoading(false);
        }
    };

    if (success) {
        return (
            <div className="max-w-md mx-auto mt-20 text-center p-8 bg-white rounded-xl shadow-lg border border-green-100">
                <div className="bg-green-100 w-20 h-20 rounded-full flex items-center justify-center mx-auto mb-6">
                    <CheckCircle className="text-eco-green w-10 h-10" />
                </div>
                <h2 className="text-3xl font-bold text-gray-800 mb-2">Order Placed!</h2>
                <p className="text-gray-500 mb-6">
                    Thank you for shopping sustainably. Your order <span className="font-bold text-gray-800">#{orderId}</span> has been confirmed.
                </p>
                <div className="space-y-3">
                    <button onClick={() => navigate('/')} className="w-full bg-gray-900 text-white py-3 rounded-lg font-bold hover:bg-gray-800">
                        Continue Shopping
                    </button>
                    {/* We will build this page next */}
                    <button onClick={() => navigate('/orders')} className="w-full bg-white border border-gray-300 text-gray-700 py-3 rounded-lg font-bold hover:bg-gray-50">
                        View My Orders
                    </button>
                </div>
            </div>
        );
    }

    return (
        <div className="max-w-2xl mx-auto">
            <h1 className="text-3xl font-bold mb-8 flex items-center gap-2">
                <Truck className="text-gray-800" /> Checkout
            </h1>

            <div className="bg-white p-8 rounded-xl shadow-sm border border-gray-100">
                <form onSubmit={handleSubmit} className="space-y-6">
                    {/* Shipping Section */}
                    <div>
                        <h3 className="text-lg font-bold text-gray-700 mb-4 border-b pb-2">Shipping Information</h3>
                        <div className="grid gap-4">
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Full Address</label>
                                <textarea 
                                    name="address" 
                                    rows="3" 
                                    placeholder="123 Green Street, Earth City..."
                                    className="w-full p-3 border rounded-lg focus:ring-2 focus:ring-eco-green focus:outline-none"
                                    onChange={handleChange}
                                    required
                                />
                            </div>
                            <div>
                                <label className="block text-sm font-medium text-gray-700 mb-1">Phone Number</label>
                                <input 
                                    name="phone" 
                                    type="tel"
                                    placeholder="+1 234 567 8900"
                                    className="w-full p-3 border rounded-lg focus:ring-2 focus:ring-eco-green focus:outline-none"
                                    onChange={handleChange}
                                    required
                                />
                            </div>
                        </div>
                    </div>

                    {/* Payment Section */}
                    <div>
                        <h3 className="text-lg font-bold text-gray-700 mb-4 border-b pb-2">Payment Method</h3>
                        <div className="grid grid-cols-2 gap-4">
                            <label className={`border rounded-xl p-4 cursor-pointer flex flex-col items-center gap-2 transition ${formData.paymentMethod === 'Credit Card' ? 'border-eco-green bg-green-50 ring-1 ring-eco-green' : 'hover:border-gray-300'}`}>
                                <input 
                                    type="radio" 
                                    name="paymentMethod" 
                                    value="Credit Card" 
                                    checked={formData.paymentMethod === 'Credit Card'}
                                    onChange={handleChange}
                                    className="hidden"
                                />
                                <CreditCard className={formData.paymentMethod === 'Credit Card' ? 'text-eco-green' : 'text-gray-400'} />
                                <span className="font-semibold text-sm">Credit Card</span>
                            </label>

                            <label className={`border rounded-xl p-4 cursor-pointer flex flex-col items-center gap-2 transition ${formData.paymentMethod === 'COD' ? 'border-eco-green bg-green-50 ring-1 ring-eco-green' : 'hover:border-gray-300'}`}>
                                <input 
                                    type="radio" 
                                    name="paymentMethod" 
                                    value="COD" 
                                    checked={formData.paymentMethod === 'COD'}
                                    onChange={handleChange}
                                    className="hidden"
                                />
                                <Truck className={formData.paymentMethod === 'COD' ? 'text-eco-green' : 'text-gray-400'} />
                                <span className="font-semibold text-sm">Cash on Delivery</span>
                            </label>
                        </div>
                    </div>

                    <button 
                        type="submit" 
                        disabled={loading}
                        className="w-full bg-eco-green text-white py-4 rounded-xl font-bold text-lg hover:bg-eco-dark transition shadow-lg shadow-green-200"
                    >
                        {loading ? 'Processing Order...' : 'Place Order'}
                    </button>
                </form>
            </div>
        </div>
    );
};

export default Checkout;