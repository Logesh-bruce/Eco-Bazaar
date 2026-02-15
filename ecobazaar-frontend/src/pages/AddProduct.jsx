import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Upload, Leaf } from 'lucide-react';
import { useAuth } from '../context/AuthContext';
import api from '../api/axios';

const AddProduct = () => {
    const { user } = useAuth();
    const navigate = useNavigate();
    const [loading, setLoading] = useState(false);
    
    const [formData, setFormData] = useState({
        name: '',
        description: '',
        price: '',
        quantity: '',
        category: 'Electronics',
        imageBase64: '' // We will store the long string here
    });

    // 1. Handle Text Inputs
    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    // 2. Handle Image File -> Base64 Conversion
    const handleImageUpload = (e) => {
        const file = e.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onloadend = () => {
                // The result is "data:image/jpeg;base64,....."
                // Your backend expects just the Base64 part, or handles the prefix.
                // Your Product model handles the prefix, so sending the whole string is usually fine,
                // but let's strip the prefix just to be safe if your backend is picky.
                const base64String = reader.result.replace("data:", "").replace(/^.+,/, "");
                setFormData({ ...formData, imageBase64: base64String });
            };
            reader.readAsDataURL(file);
        }
    };

    // 3. Submit to Backend
    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);

        const payload = {
            userId: user.id,
            ...formData,
            // Convert numbers
            price: parseFloat(formData.price),
            quantity: parseInt(formData.quantity)
        };

        try {
            await api.post('/products/add', payload);
            alert('Product Created Successfully!');
            navigate('/seller/dashboard');
        } catch (error) {
            console.error(error);
            alert('Failed to create product: ' + (error.response?.data?.error || error.message));
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="max-w-2xl mx-auto bg-white p-8 rounded-xl shadow-md my-10 border border-gray-100">
            <h2 className="text-2xl font-bold text-gray-800 mb-6 flex items-center gap-2">
                <Leaf className="text-eco-green" /> Add Sustainable Product
            </h2>

            <form onSubmit={handleSubmit} className="space-y-6">
                {/* Image Upload */}
                <div className="border-2 border-dashed border-gray-300 rounded-lg p-6 text-center hover:bg-gray-50 transition">
                    <input type="file" id="imgUpload" className="hidden" accept="image/*" onChange={handleImageUpload} />
                    <label htmlFor="imgUpload" className="cursor-pointer flex flex-col items-center">
                        {formData.imageBase64 ? (
                            <img src={`data:image/jpeg;base64,${formData.imageBase64}`} className="h-40 object-contain mb-2" alt="Preview" />
                        ) : (
                            <Upload className="text-gray-400 w-12 h-12 mb-2" />
                        )}
                        <span className="text-eco-green font-medium">Click to upload image</span>
                        <span className="text-gray-400 text-sm mt-1">Supports JPG, PNG (Max 5MB)</span>
                    </label>
                </div>

                <div className="grid grid-cols-2 gap-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Product Name</label>
                        <input name="name" onChange={handleChange} className="w-full p-2 border rounded-lg" required />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Category</label>
                        <select name="category" onChange={handleChange} className="w-full p-2 border rounded-lg bg-white">
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
                    <textarea name="description" rows="3" onChange={handleChange} className="w-full p-2 border rounded-lg" required />
                </div>

                <div className="grid grid-cols-2 gap-4">
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Price ($)</label>
                        <input name="price" type="number" step="0.01" onChange={handleChange} className="w-full p-2 border rounded-lg" required />
                    </div>
                    <div>
                        <label className="block text-sm font-medium text-gray-700 mb-1">Stock Quantity</label>
                        <input name="quantity" type="number" onChange={handleChange} className="w-full p-2 border rounded-lg" required />
                    </div>
                </div>

                <div className="bg-green-50 p-4 rounded-lg text-sm text-green-800">
                    <strong>Note:</strong> Carbon Footprint data will be automatically calculated based on the category if left empty.
                </div>

                <button disabled={loading} className="w-full bg-eco-green text-white py-3 rounded-lg font-bold hover:bg-eco-dark transition shadow-md">
                    {loading ? 'Processing...' : 'Publish Product'}
                </button>
            </form>
        </div>
    );
};

export default AddProduct;