import { useEffect, useState } from 'react';
import { Package, Calendar, MapPin, Leaf } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import api from '../api/axios';

const Orders = () => {
    const { user } = useAuth();
    const [orders, setOrders] = useState([]);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        const fetchOrders = async () => {
            try {
                const response = await api.get(`/orders/user/${user.id}`);
                // Sort orders by newest first (assuming ID increments with time)
                setOrders(response.data.reverse());
            } catch (error) {
                console.error("Failed to fetch orders", error);
            } finally {
                setLoading(false);
            }
        };

        if (user) fetchOrders();
    }, [user]);

    if (loading) return <div className="text-center py-10">Loading Orders...</div>;

    return (
        <div className="max-w-4xl mx-auto">
            <h1 className="text-3xl font-bold mb-8 flex items-center gap-2">
                <Package className="text-eco-green" /> My Orders
            </h1>

            {orders.length === 0 ? (
                <div className="text-center py-10 bg-gray-50 rounded-xl border border-dashed">
                    <p className="text-gray-500">You haven't placed any orders yet.</p>
                </div>
            ) : (
                <div className="space-y-6">
                    {orders.map(order => (
                        <div key={order.id} className="bg-white border border-gray-200 rounded-xl overflow-hidden shadow-sm hover:shadow-md transition">
                            {/* Order Header */}
                            <div className="bg-gray-50 p-4 border-b flex flex-wrap justify-between items-center gap-4">
                                <div className="flex gap-6 text-sm">
                                    <div>
                                        <p className="text-gray-500">Order Placed</p>
                                        <p className="font-bold text-gray-800">
                                            {new Date(order.createdAt).toLocaleDateString()}
                                        </p>
                                    </div>
                                    <div>
                                        <p className="text-gray-500">Total Amount</p>
                                        <p className="font-bold text-gray-800">${order.totalAmount.toFixed(2)}</p>
                                    </div>
                                    <div>
                                        <p className="text-gray-500">Total Carbon</p>
                                        <p className="font-bold text-eco-green flex items-center gap-1">
                                            <Leaf size={14} /> {order.totalCarbonFootprint.toFixed(1)} kg
                                        </p>
                                    </div>
                                </div>
                                <div className="flex items-center gap-3">
                                    <span className={`px-3 py-1 rounded-full text-xs font-bold ${
                                        order.status === 'DELIVERED' ? 'bg-green-100 text-green-700' :
                                        order.status === 'CANCELLED' ? 'bg-red-100 text-red-700' :
                                        'bg-blue-100 text-blue-700'
                                    }`}>
                                        {order.status}
                                    </span>
                                    <span className="text-xs text-gray-500">#{order.id}</span>
                                </div>
                            </div>

                            {/* Order Body */}
                            <div className="p-6">
                                {/* Items List */}
                                <div className="space-y-4 mb-6">
                                    {order.items.map((item, index) => (
                                        <div key={index} className="flex items-center justify-between">
                                            <div className="flex items-center gap-4">
                                                {/* If your Order API doesn't return images, we use a placeholder */}
                                                <div className="bg-gray-100 w-16 h-16 rounded-md flex items-center justify-center text-gray-400">
                                                    <Package size={24} />
                                                </div>
                                                <div>
                                                    <p className="font-bold text-gray-800">{item.productNameSnapshot}</p>
                                                    <p className="text-sm text-gray-500">Qty: {item.quantity}</p>
                                                </div>
                                            </div>
                                            <p className="font-medium">${item.priceSnapshot.toFixed(2)}</p>
                                        </div>
                                    ))}
                                </div>

                                {/* Delivery Info */}
                                <div className="flex items-start gap-2 text-sm text-gray-600 bg-gray-50 p-3 rounded-lg">
                                    <MapPin size={16} className="mt-0.5" />
                                    <div>
                                        <span className="font-bold">Delivery Address:</span> {order.shippingAddress}
                                    </div>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            )}
        </div>
    );
};

export default Orders;