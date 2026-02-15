import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { Plus, Edit, Trash2, Eye, Package } from 'lucide-react'; // Fixed imports
import { useAuth } from '../context/AuthContext';
import api from '../api/axios';

const SellerDashboard = () => {
    const { user } = useAuth();
    const [products, setProducts] = useState([]);
    const [loading, setLoading] = useState(true);

    // Fetch only this seller's products
    useEffect(() => {
        const fetchMyProducts = async () => {
            try {
                const response = await api.get(`/products/seller/${user.id}`);
                setProducts(response.data);
            } catch (error) {
                console.error("Error fetching seller products:", error);
            } finally {
                setLoading(false);
            }
        };

        if (user) fetchMyProducts();
    }, [user]);

    const handleDelete = async (productId) => {
        if (!window.confirm("Are you sure you want to delete this product?")) return;
        
        try {
            await api.delete(`/products/${productId}`, {
                params: { userId: user.id }
            });
            // Remove from UI immediately
            setProducts(products.filter(p => p.id !== productId));
        } catch (error) {
            alert("Failed to delete product");
        }
    };

    if (loading) return <div className="text-center py-10">Loading Dashboard...</div>;

    return (
        <div className="container mx-auto p-6">
            <div className="flex justify-between items-center mb-8">
                <h1 className="text-3xl font-bold text-gray-800">Seller Dashboard</h1>
                <Link to="/seller/add" className="bg-eco-green text-white px-4 py-2 rounded-lg flex items-center gap-2 hover:bg-eco-dark transition">
                    <Plus size={20} /> Add New Product
                </Link>
            </div>

            <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
                <table className="w-full text-left">
                    <thead className="bg-gray-50 text-gray-600 uppercase text-xs">
                        <tr>
                            <th className="p-4">Product</th>
                            <th className="p-4">Price</th>
                            <th className="p-4">Stock</th>
                            <th className="p-4">Carbon</th>
                            <th className="p-4">Status</th>
                            <th className="p-4 text-right">Actions</th>
                        </tr>
                    </thead>
                    <tbody className="divide-y divide-gray-100">
                        {products.length > 0 ? products.map(product => (
                            <tr key={product.id} className="hover:bg-gray-50 transition">
                                <td className="p-4 flex items-center gap-3">
                                    <img src={product.imageUrl} alt={product.name} className="w-12 h-12 rounded object-cover border" />
                                    <span className="font-medium text-gray-800">{product.name}</span>
                                </td>
                                <td className="p-4">${product.price}</td>
                                <td className="p-4">{product.quantity} units</td>
                                <td className="p-4 text-eco-green font-medium">{product.carbonFootprint} kg</td>
                                <td className="p-4">
                                    <span className={`px-2 py-1 rounded text-xs font-bold ${
                                        product.verified ? 'bg-green-100 text-green-700' : 'bg-yellow-100 text-yellow-700'
                                    }`}>
                                        {product.verified ? 'Verified' : 'Pending'}
                                    </span>
                                </td>
                                <td className="p-4 text-right">
                                    <div className="flex justify-end gap-2">
                                        {/* View Details */}
                                        <Link to={`/product/${product.id}`} className="p-2 text-gray-500 hover:text-blue-600 hover:bg-blue-50 rounded" title="View">
                                            <Eye size={18} />
                                        </Link>
                                        
                                        {/* Edit Button */}
                                        <Link to={`/seller/edit/${product.id}`} className="p-2 text-gray-500 hover:text-eco-green hover:bg-green-50 rounded" title="Edit">
                                            <Edit size={18} />
                                        </Link>

                                        {/* Delete Button */}
                                        <button onClick={() => handleDelete(product.id)} className="p-2 text-gray-500 hover:text-red-600 hover:bg-red-50 rounded" title="Delete">
                                            <Trash2 size={18} />
                                        </button>
                                    </div>
                                </td>
                            </tr>
                        )) : (
                            <tr>
                                <td colSpan="6" className="p-10 text-center text-gray-500">
                                    <Package className="w-12 h-12 mx-auto mb-2 text-gray-300" />
                                    <p>You haven't added any products yet.</p>
                                </td>
                            </tr>
                        )}
                    </tbody>
                </table>
            </div>
        </div>
    );
};

export default SellerDashboard;