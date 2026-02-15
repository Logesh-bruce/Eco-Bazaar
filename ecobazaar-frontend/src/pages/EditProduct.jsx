import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Save, ArrowLeft, Image as ImageIcon } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import api from '../api/axios';

const EditProduct = () => {
    const { id } = useParams();
    const { user } = useAuth();
    const navigate = useNavigate();
    const [loading, setLoading] = useState(true);
    const [saving, setSaving] = useState(false);
    
    const [formData, setFormData] = useState({
        name: '',
        description: '',
        price: '',
        quantity: '',
        category: '',
        imageBase64: '' // We will keep the old image unless they upload a new one
    });

    // 1. Fetch Existing Data
    useEffect(() => {
        const fetchProduct = async () => {
            try {
                const response = await api.get(`/products/${id}`);
                const product = response.data;
                
                // Security Check: Ensure only the owner (or Admin) can edit
                if (product.sellerId !== user.id && user.role !== 'ADMIN') {
                    alert("Unauthorized!");
                    navigate('/seller/dashboard');
                    return;
                }

                setFormData({
                    name: product.name,
                    description: product.description,
                    price: product.price,
                    quantity: product.quantity,
                    category: product.category,
                    imageBase64: '' // We don't fetch the base64 string back, we just keep it empty unless changed
                });
            } catch (error) {
                console.error("Failed to load product", error);
                alert("Product not found.");
                navigate('/seller/dashboard');
            } finally {
                setLoading(false);
            }
        };

        if (user) fetchProduct();
    }, [id, user, navigate]);

    // 2. Handle Input Changes
    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    // 3. Handle New Image Upload
    const handleImageUpload = (e) => {
        const file = e.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onloadend = () => {
                const base64String = reader.result.replace("data:", "").replace(/^.+,/, "");
                setFormData({ ...formData, imageBase64: base64String });
            };
            reader.readAsDataURL(file);
        }
    };

    // 4. Submit Updates
    const handleSubmit = async (e) => {
        e.preventDefault();
        setSaving(true);

        const payload = {
            userId: user.id,
            ...formData,
            price: parseFloat(formData.price),
            quantity: parseInt(formData.quantity)
        };

        try {
            await api.put(`/products/${id}`, payload);
            alert('Product Updated Successfully!');
            navigate('/seller/dashboard');
        } catch (error) {
            console.error(error);
            alert('Failed to update product: ' + (error.response?.data?.error || error.message));
        } finally {
            setSaving(false);
        }
    };

    if (loading) return <div className="text-center py-20">Loading Product Data...</div>;

    return (
        <div className="max-w-2xl mx-auto my-10">
            <button onClick={() => navigate(-1)} className="flex items-center text-gray-500 hover:text-eco-green mb-6">
                <ArrowLeft size={20} className="mr-1" /> Cancel
            </button>

            <div className="bg-white p-8 rounded-xl shadow-md border border-gray-100">
                <h2 className="text-2xl font-bold text-gray-800 mb-6 flex items-center gap-2">
                    <Save className="text-eco-green" /> Edit Product
                </h2>

                <form onSubmit={handleSubmit} className="space-y-6">
                    {/* Image Section */}
                    <div className="border border-gray-200 rounded-lg p-4 flex items-center gap-4 bg-gray-50">
                        <div className="bg-white p-2 rounded border">
                            <ImageIcon className="text-gray-400 w-8 h-8" />
                        </div>
                        <div className="flex-grow">
                            <label className="block text-sm font-medium text-gray-700 mb-1">Update Image (Optional)</label>
                            <input type="file" accept="image/*" onChange={handleImageUpload} className="text-sm text-gray-500" />
                        </div>
                    </div>

                    <div className="grid grid-cols-2 gap-4">
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">Product Name</label>
                            <input name="name" value={formData.name} onChange={handleChange} className="w-full p-2 border rounded-lg" required />
                        </div>
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">Category</label>
                            <select name="category" value={formData.category} onChange={handleChange} className="w-full p-2 border rounded-lg bg-white">
                                <option>Electronics</option>
                                <option>Clothing & Apparel</option>
                                <option>Home & Kitchen</option>
                                <option>Food & Beverages</option>
                                <option>Beauty & Personal Care</option>
                            </select>
                        </div>
                    </div>

                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Description</label>
                        <textarea name="description" rows="3" value={formData.description} onChange={handleChange} className="w-full p-2 border rounded-lg" required />
                    </div>

                    <div className="grid grid-cols-2 gap-4">
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">Price ($)</label>
                            <input name="price" type="number" step="0.01" value={formData.price} onChange={handleChange} className="w-full p-2 border rounded-lg" required />
                        </div>
                        <div>
                            <label className="block text-sm font-medium text-gray-700 mb-1">Stock Quantity</label>
                            <input name="quantity" type="number" value={formData.quantity} onChange={handleChange} className="w-full p-2 border rounded-lg" required />
                        </div>
                    </div>

                    <button disabled={saving} className="w-full bg-gray-900 text-white py-3 rounded-lg font-bold hover:bg-eco-green transition">
                        {saving ? 'Saving Changes...' : 'Save Update'}
                    </button>
                </form>
            </div>
        </div>
    );
};

export default EditProduct;