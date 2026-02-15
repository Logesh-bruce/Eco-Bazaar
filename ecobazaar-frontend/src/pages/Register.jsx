import { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate, Link } from 'react-router-dom';

const Register = () => {
    const [formData, setFormData] = useState({
        fullName: '',
        email: '',
        password: '',
        role: 'USER' // Default to Shopper
    });
    const [error, setError] = useState('');
    const { register } = useAuth();
    const navigate = useNavigate();

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');

        const result = await register(
            formData.email, 
            formData.password, 
            formData.fullName, 
            formData.role
        );

        if (result.success) {
            alert('Registration successful! Please login.');
            navigate('/login');
        } else {
            setError(result.message);
        }
    };

    return (
        <div className="flex justify-center items-center h-screen bg-gray-100">
            <div className="bg-white p-8 rounded-lg shadow-md w-96">
                <h2 className="text-2xl font-bold text-center text-eco-green mb-6">Create Account</h2>
                
                {error && <div className="bg-red-100 text-red-700 p-2 mb-4 rounded">{error}</div>}
                
                <form onSubmit={handleSubmit}>
                    <div className="mb-4">
                        <label className="block text-gray-700 text-sm font-bold mb-2">Full Name</label>
                        <input name="fullName" onChange={handleChange} className="w-full p-2 border rounded" required />
                    </div>
                    <div className="mb-4">
                        <label className="block text-gray-700 text-sm font-bold mb-2">Email</label>
                        <input name="email" type="email" onChange={handleChange} className="w-full p-2 border rounded" required />
                    </div>
                    <div className="mb-4">
                        <label className="block text-gray-700 text-sm font-bold mb-2">Password</label>
                        <input name="password" type="password" onChange={handleChange} className="w-full p-2 border rounded" required />
                    </div>
                    <div className="mb-6">
                        <label className="block text-gray-700 text-sm font-bold mb-2">I am a...</label>
                        <select name="role" onChange={handleChange} className="w-full p-2 border rounded bg-white">
                            <option value="USER">Shopper (Buy Products)</option>
                            <option value="SELLER">Seller (Sell Products)</option>
                        </select>
                    </div>
                    <button type="submit" className="w-full bg-eco-green text-white p-2 rounded hover:bg-eco-dark transition">
                        Register
                    </button>
                </form>
                <p className="mt-4 text-center text-sm">
                    Already have an account? <Link to="/login" className="text-eco-green font-bold">Login</Link>
                </p>
            </div>
        </div>
    );
};

export default Register;