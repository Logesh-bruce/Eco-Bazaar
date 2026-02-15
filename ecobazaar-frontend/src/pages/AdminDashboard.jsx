import { useEffect, useState } from 'react';
import { Users, ShoppingBag, Leaf, CheckCircle, ShieldAlert, Star, Trash2 } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import api from '../api/axios';

const AdminDashboard = () => {
    const { user } = useAuth();
    const navigate = useNavigate();
    
    // UI State
    const [activeTab, setActiveTab] = useState('pending'); // 'pending' or 'all'

    // Data State
    const [stats, setStats] = useState({
        totalUsers: 0,
        totalProducts: 0,
        totalOrders: 0,
        totalCarbonSaved: 0
    });
    const [pendingProducts, setPendingProducts] = useState([]);
    const [allProducts, setAllProducts] = useState([]); // <--- NEW: Store all products
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        if (user && user.role !== 'ADMIN') {
            navigate('/');
            return;
        }

        const fetchData = async () => {
            try {
                // 1. Fetch Stats
                const statsRes = await api.get('/carbon/admin/summary');
                setStats(statsRes.data || {});

                // 2. Fetch Pending Products
                const pendingRes = await api.get('/products/admin/pending');
                setPendingProducts(pendingRes.data || []);

                // 3. Fetch ALL Products (for Feature management)
                // We ask for a larger size to see more items
                const allRes = await api.get('/products?size=100');
                // The search API returns { products: [...], totalPages: ... }
                setAllProducts(allRes.data.products || []);

            } catch (error) {
                console.error("Admin Load Error:", error);
            } finally {
                setLoading(false);
            }
        };

        if (user) fetchData();
    }, [user, navigate]);

    // --- Actions ---

    const handleVerify = async (productId) => {
        try {
            await api.put(`/products/admin/verify/${productId}?adminId=${user.id}`);
            // Remove from pending list
            setPendingProducts(pendingProducts.filter(p => p.id !== productId));
            // Refresh the "All Products" list to show it's verified
            const refreshRes = await api.get('/products?size=100');
            setAllProducts(refreshRes.data.products || []);
            alert("Product Verified!");
        } catch (error) {
            alert("Failed to verify product.");
        }
    };

    const handleToggleFeature = async (productId) => {
        try {
            // Call the toggle endpoint
            await api.put(`/products/${productId}/feature?adminId=${user.id}`);
            
            // Update UI instantly (Optimistic update)
            setAllProducts(allProducts.map(p => 
                p.id === productId ? { ...p, featured: !p.featured } : p
            ));
        } catch (error) {
            console.error(error);
            alert("Failed to update feature status");
        }
    };

    const handleDelete = async (productId) => {
        if(!window.confirm("Delete this product permanently?")) return;
        try {
            await api.delete(`/products/${productId}?userId=${user.id}`);
            setAllProducts(allProducts.filter(p => p.id !== productId));
        } catch (error) {
            alert("Delete failed");
        }
    };

    // Helper
    const formatNumber = (num) => (num ? num.toFixed(1) : '0.0');

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
                                {pendingProducts.map(product => (
                                    <tr key={product.id} className="hover:bg-gray-50 transition">
                                        <td className="p-4 flex items-center gap-3">
                                            <img src={product.imageUrl} alt={product.name} className="w-10 h-10 rounded object-cover border" />
                                            <span className="font-bold text-gray-800">{product.name}</span>
                                        </td>
                                        <td className="p-4 text-sm text-gray-600">{product.sellerId}</td>
                                        <td className="p-4 font-medium">${product.price}</td>
                                        <td className="p-4 text-eco-green font-medium">{product.carbonFootprint} kg</td>
                                        <td className="p-4 text-right">
                                            <button 
                                                onClick={() => handleVerify(product.id)}
                                                className="bg-green-600 text-white px-4 py-2 rounded-lg text-sm font-bold hover:bg-green-700 transition"
                                            >
                                                Verify
                                            </button>
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    )}
                </div>
            )}

            {/* --- TAB CONTENT: ALL PRODUCTS (FEATURE MANAGEMENT) --- */}
            {activeTab === 'all' && (
                <div className="bg-white rounded-xl shadow-sm border border-gray-200 overflow-hidden">
                    <table className="w-full text-left">
                        <thead className="bg-gray-50 text-gray-600 uppercase text-xs">
                            <tr>
                                <th className="p-4">Product</th>
                                <th className="p-4">Status</th>
                                <th className="p-4 text-center">Featured?</th>
                                <th className="p-4 text-right">Actions</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-gray-100">
                            {allProducts.map(product => (
                                <tr key={product.id} className="hover:bg-gray-50 transition">
                                    <td className="p-4 flex items-center gap-3">
                                        <img src={product.imageUrl} alt={product.name} className="w-10 h-10 rounded object-cover border" />
                                        <div>
                                            <span className="font-bold text-gray-800 block">{product.name}</span>
                                            <span className="text-xs text-gray-500">{product.category}</span>
                                        </div>
                                    </td>
                                    <td className="p-4">
                                        <span className={`px-2 py-1 rounded text-xs font-bold ${
                                            product.verified ? 'bg-green-100 text-green-700' : 'bg-yellow-100 text-yellow-700'
                                        }`}>
                                            {product.verified ? 'Verified' : 'Pending'}
                                        </span>
                                    </td>
                                    <td className="p-4 text-center">
                                        <button 
                                            onClick={() => handleToggleFeature(product.id)}
                                            className={`p-2 rounded-full transition ${
                                                product.featured 
                                                ? 'bg-yellow-100 text-yellow-500 hover:bg-yellow-200' 
                                                : 'bg-gray-100 text-gray-400 hover:bg-gray-200'
                                            }`}
                                            title="Toggle Featured Status"
                                        >
                                            <Star size={20} fill={product.featured ? "currentColor" : "none"} />
                                        </button>
                                    </td>
                                    <td className="p-4 text-right">
                                        <button 
                                            onClick={() => handleDelete(product.id)}
                                            className="p-2 text-gray-400 hover:text-red-600 hover:bg-red-50 rounded"
                                            title="Delete Product"
                                        >
                                            <Trash2 size={18} />
                                        </button>
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                </div>
            )}
        </div>
    );
};

export default AdminDashboard;