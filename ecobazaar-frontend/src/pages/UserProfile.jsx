import { useEffect, useState } from 'react';
import { Award, Leaf, TrendingUp, ShoppingBag, User } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import { Link } from 'react-router-dom';
import api from '../api/axios';

const UserProfile = () => {
    const { user, logout } = useAuth();
    const [report, setReport] = useState(null);
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        if (!user) return;

        const fetchReport = async () => {
            try {
                // Fetch the Carbon Report from backend
                const response = await api.get(`/carbon/report/${user.id}`);
                setReport(response.data);
            } catch (error) {
                console.error("Failed to load profile report", error);
            } finally {
                setLoading(false);
            }
        };

        fetchReport();
    }, [user]);

    if (loading) return <div className="text-center py-20">Loading Eco Profile...</div>;
    if (!report) return <div className="text-center py-20">Failed to load profile.</div>;

    // Helper to pick badge color
    const getBadgeColor = (badge) => {
        if (badge === 'Planet Protector') return 'bg-purple-100 text-purple-700 border-purple-200';
        if (badge === 'Low Carbon Leader') return 'bg-green-100 text-green-700 border-green-200';
        if (badge === 'Eco Enthusiast') return 'bg-blue-100 text-blue-700 border-blue-200';
        return 'bg-gray-100 text-gray-700 border-gray-200';
    };

    return (
        <div className="max-w-4xl mx-auto">
            {/* Header Section */}
            <div className="bg-white p-8 rounded-2xl shadow-sm border border-gray-100 flex flex-col md:flex-row items-center gap-8 mb-8">
                <div className="bg-gray-100 p-6 rounded-full">
                    <User size={64} className="text-gray-400" />
                </div>
                <div className="text-center md:text-left flex-grow">
                    <h1 className="text-3xl font-bold text-gray-800">{user.fullName}</h1>
                    <p className="text-gray-500 mb-4">{user.email}</p>
                    <div className={`inline-flex items-center gap-2 px-4 py-2 rounded-full border font-bold text-sm ${getBadgeColor(report.badge)}`}>
                        <Award size={18} />
                        {report.badge}
                    </div>
                </div>
                <div className="text-center bg-green-50 p-6 rounded-xl border border-green-100 min-w-[200px]">
                    <p className="text-gray-500 text-sm mb-1">Eco Score</p>
                    <p className="text-4xl font-bold text-eco-green">{report.ecoScore}</p>
                </div>
            </div>

            {/* Stats Grid */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
                <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100">
                    <div className="flex items-center gap-3 mb-2 text-eco-green">
                        <Leaf className="w-6 h-6" />
                        <h3 className="font-bold">Carbon Saved</h3>
                    </div>
                    <p className="text-3xl font-bold text-gray-800">{report.carbonSaved.toFixed(1)} kg</p>
                    <p className="text-xs text-gray-400 mt-2">Compared to average shopper</p>
                </div>

                <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100">
                    <div className="flex items-center gap-3 mb-2 text-blue-600">
                        <ShoppingBag className="w-6 h-6" />
                        <h3 className="font-bold">Green Purchases</h3>
                    </div>
                    <p className="text-3xl font-bold text-gray-800">{report.greenPurchases}</p>
                    <p className="text-xs text-gray-400 mt-2">Items with low footprint</p>
                </div>

                <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100">
                    <div className="flex items-center gap-3 mb-2 text-purple-600">
                        <TrendingUp className="w-6 h-6" />
                        <h3 className="font-bold">Total Footprint</h3>
                    </div>
                    <p className="text-3xl font-bold text-gray-800">{report.totalCarbonFootprint.toFixed(1)} kg</p>
                    <p className="text-xs text-gray-400 mt-2">Lifetime emission impact</p>
                </div>
            </div>

            {/* Actions */}
            <div className="bg-white p-6 rounded-xl shadow-sm border border-gray-100">
                <h3 className="text-lg font-bold text-gray-800 mb-4">Account Actions</h3>
                <div className="flex flex-col md:flex-row gap-4">
                    <Link to="/orders" className="flex items-center justify-center gap-2 bg-gray-900 text-white px-6 py-3 rounded-lg font-bold hover:bg-gray-700 transition">
                        View Order History
                    </Link>
                    <button onClick={logout} className="flex items-center justify-center gap-2 border border-red-200 text-red-600 px-6 py-3 rounded-lg font-bold hover:bg-red-50 transition">
                        Log Out
                    </button>
                </div>
            </div>
        </div>
    );
};

export default UserProfile;