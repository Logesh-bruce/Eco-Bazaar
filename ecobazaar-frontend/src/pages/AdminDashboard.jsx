import { useEffect, useState } from 'react';
import { ShieldAlert, CheckCircle, Star, Users, ShoppingBag, Leaf, Trash2 } from 'lucide-react';
import api from '../api/axios';
import { getImageSrc, handleImageError } from '../utils/imageUtils';

const AdminDashboard = () => {
    const [stats, setStats] = useState({});
    const [pendingProducts, setPendingProducts] = useState([]);
    const [allProducts, setAllProducts] = useState([]);
    const [activeTab, setActiveTab] = useState('pending'); // 'pending' | 'all'
    const [loading, setLoading] = useState(true);

    const fetchData = async () => {
        try {
            // 1. Fetch Stats
            const statsRes = await api.get('/admin/stats');
            setStats(statsRes.data || {});

            // 2. Fetch Pending Approvals
            const pendingRes = await api.get('/products/admin/pending');
            setPendingProducts(Array.isArray(pendingRes.data) ? pendingRes.data : []);

            // 3. Fetch All Products (for featured toggling)
            const allRes = await api.get('/products');
            setAllProducts(allRes.data?.products || []);

        } catch (error) {
            console.error("Admin data fetch error", error);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchData();
    }, []);

    // 1. Verify / Approve Product
    const handleVerify = async (productId) => {
        try {
            await api.put(`/products/admin/verify/${productId}`, null, {
                params: { adminId: 1 } // Hardcoded admin ID for now
            });
            alert("Product Approved & Live!");
            // Remove from pending list immediately
            setPendingProducts(pendingProducts.filter(p => p.id !== productId));
        } catch (error) {
            alert("Failed to verify product");
        }
    };

    // 2. Toggle Featured Product
    const handleToggleFeatured = async (productId) => {
        try {
            await api.put(`/products/${productId}/feature`, null, {
                params: { adminId: 1 }
            });
            // Toggle locally in state
            setAllProducts(allProducts.map(p => 
                p.id === productId ? { ...p, featured: !p.featured } : p
            ));
        } catch (error) {
            alert("Failed to update featured status");
        }
    };

    // 3. Delete Product (Admin Override)
    const handleDelete = async (productId) => {
        if(!window.confirm("Admin Action: Are you sure you want to permanently delete this product?")) return;
        try {
            await api.delete(`/products/${productId}`, {
                params: { userId: 1 } // Admin ID
            });
            setAllProducts(allProducts.filter(p => p.id !== productId));
            setPendingProducts(pendingProducts.filter(p => p.id !== productId));
        } catch (error) {
            alert("Failed to delete product");
        }
    };

    // Helper function to safely format numbers
    const formatNumber = (value) => {
        if (value === null || value === undefined || isNaN(value)) {
            return '0';
        }
        return Number(value).toFixed(1);
    };

    if (loading) return <div className="text-center py-20">Loading Admin Panel...</div>;

    return (
        <div className="max-w-7xl mx-auto">
            <h1 className="text-3xl font-bold mb-8 flex items-center gap-2 text-gray-800">
                <ShieldAlert className="text-red-600" /> Admin Control Panel
            </h1>

            {/* --- STATS CARDS --- */}
            <div className="grid grid-cols-1 md:grid-cols-4 gap-6 mb-12">
                <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100 flex items-center gap-4">
                    <div className="bg-blue-100 p-3 rounded-full text-blue-600"><Users size={24} /></div>
                    <div>
                        <p className="text-gray-500 text-sm">Total Users</p>
                        <p className="text-2xl font-bold">{stats.totalUsers || 0}</p>
                    </div>
                </div>
                <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100 flex items-center gap-4">
                    <div className="bg-purple-100 p-3 rounded-full text-purple-600"><ShoppingBag size={24} /></div>
                    <div>
                        <p className="text-gray-500 text-sm">Total Products</p>
                        <p className="text-2xl font-bold">{stats.totalProducts || 0}</p>
                    </div>
                </div>
                <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100 flex items-center gap-4">
                    <div className="bg-yellow-100 p-3 rounded-full text-yellow-600"><CheckCircle size={24} /></div>
                    <div>
                        <p className="text-gray-500 text-sm">Total Orders</p>
                        <p className="text-2xl font-bold">{stats.totalOrders || 0}</p>
                    </div>
                </div>
                <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100 flex items-center gap-4">
                    <div className="bg-green-100 p-3 rounded-full text-eco-green"><Leaf size={24} /></div>
                    <div>
                        <p className="text-gray-500 text-sm">Carbon Saved</p>
                        <p className="text-2xl font-bold text-eco-green">{formatNumber(stats.totalCarbonSaved)} kg</p>
                    </div>
                </div>
            </div>

            {/* --- TABS --- */}
            <div className="flex gap-4 mb-6 border-b border-gray-200">
                <button 
                    onClick={() => setActiveTab('pending')}
                    className={`pb-3 px-4 font-bold transition ${activeTab === 'pending' ? 'text-eco-green border-b-2 border-eco-green' : 'text-gray-500 hover:text-gray-800'}`}
                >
                    Pending Approvals ({pendingProducts.length})
                </button>
                <button 
                    onClick={() => setActiveTab('all')}
                    className={`pb-3 px-4 font-bold transition ${activeTab === 'all' ? 'text-eco-green border-b-2 border-eco-green' : 'text-gray-500 hover:text-gray-800'}`}
                >
                    Manage All Products
                </button>
            </div>

            {/* --- TAB CONTENT: PENDING --- */}
            {activeTab === 'pending' && (
                <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
                    {pendingProducts.length === 0 ? (
                        <div className="p-10 text-center text-gray-400">
                            <CheckCircle className="w-12 h-12 mx-auto mb-3 text-green-300" />
                            <p>All products are verified! Check "Manage All Products" to feature them.</p>
                        </div>
                    ) : (
                        <table className="w-full text-left">
                            <thead className="bg-gray-50 text-gray-600 uppercase text-xs">
                                <tr>
                                    <th className="p-4">Product</th>
                                    <th className="p-4">Seller ID</th>
                                    <th className="p-4">Price</th>
                                    <th className="p-4">Carbon</th>
                                    <th className="p-4 text-right">Action</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-gray-100">
                                {pendingProducts.map(product => {
                                    const imgSrc = getImageSrc(product.imageBase64 || product.image || product.imageUrl);
                                    return (
                                        <tr key={product.id} className="hover:bg-gray-50 transition">
                                            <td className="p-4 flex items-center gap-3">
                                                <img 
                                                    src={imgSrc} 
                                                    alt={product.name} 
                                                    onError={handleImageError}
                                                    className="w-10 h-10 rounded object-cover border" 
                                                />
                                                <span className="font-bold text-gray-800">{product.name}</span>
                                            </td>
                                            <td className="p-4 text-sm text-gray-600">{product.sellerId}</td>
                                            <td className="p-4 font-medium">${typeof product.price === 'number' ? product.price.toFixed(2) : product.price}</td>
                                            <td className="p-4 text-eco-green font-medium">{product.carbonFootprint} kg</td>
                                            <td className="p-4 text-right">
                                                <button 
                                                    onClick={() => handleVerify(product.id)}
                                                    className="bg-green-600 text-white px-4 py-2 rounded-lg text-sm font-bold hover:bg-green-700 transition"
                                                >
                                                    Approve
                                                </button>
                                            </td>
                                        </tr>
                                    );
                                })}
                            </tbody>
                        </table>
                    )}
                </div>
            )}

            {/* --- TAB CONTENT: ALL PRODUCTS --- */}
            {activeTab === 'all' && (
                <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
                    <table className="w-full text-left">
                        <thead className="bg-gray-50 text-gray-600 uppercase text-xs">
                            <tr>
                                <th className="p-4">Product</th>
                                <th className="p-4">Price</th>
                                <th className="p-4">Status</th>
                                <th className="p-4">Featured</th>
                                <th className="p-4 text-right">Action</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-gray-100">
                            {allProducts.map(product => {
                                const imgSrc = getImageSrc(product.imageBase64 || product.image || product.imageUrl);
                                return (
                                    <tr key={product.id} className="hover:bg-gray-50 transition">
                                        <td className="p-4 flex items-center gap-3">
                                            <img 
                                                src={imgSrc} 
                                                alt={product.name} 
                                                onError={handleImageError}
                                                className="w-10 h-10 rounded object-cover border" 
                                            />
                                            <span className="font-medium text-gray-800">{product.name}</span>
                                        </td>
                                        <td className="p-4">${typeof product.price === 'number' ? product.price.toFixed(2) : product.price}</td>
                                        <td className="p-4">
                                            <span className={`px-2 py-1 rounded text-xs font-bold ${
                                                product.verified ? 'bg-green-100 text-green-700' : 'bg-yellow-100 text-yellow-700'
                                            }`}>
                                                {product.verified ? 'Verified' : 'Pending'}
                                            </span>
                                        </td>
                                        <td className="p-4">
                                            <button 
                                                onClick={() => handleToggleFeatured(product.id)}
                                                className={`flex items-center gap-1 px-3 py-1 rounded-full text-xs font-bold transition ${
                                                    product.featured 
                                                        ? 'bg-yellow-100 text-yellow-800 border border-yellow-300' 
                                                        : 'bg-gray-100 text-gray-500 hover:bg-gray-200'
                                                }`}
                                            >
                                                <Star size={12} className={product.featured ? "fill-current text-yellow-600" : ""} />
                                                {product.featured ? 'Featured' : 'Standard'}
                                            </button>
                                        </td>
                                        <td className="p-4 text-right">
                                            <button 
                                                onClick={() => handleDelete(product.id)}
                                                className="text-red-500 hover:text-red-700 p-2"
                                                title="Delete Product"
                                            >
                                                <Trash2 size={18} />
                                            </button>
                                        </td>
                                    </tr>
                                );
                            })}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
};

export default AdminDashboard;