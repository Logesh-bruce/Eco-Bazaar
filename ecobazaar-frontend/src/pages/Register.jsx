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
    const [loading, setLoading] = useState(false);
    const { register } = useAuth();
    const navigate = useNavigate();

    const handleChange = (e) => {
        setFormData({ ...formData, [e.target.name]: e.target.value });
    };

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        setLoading(true);

        try {
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
                setError(result.message || 'Registration failed. Please try again.');
            }
        } catch (err) {
            setError(err?.message || 'An unexpected error occurred.');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="flex justify-center items-center min-h-[80vh]">
            <div className="bg-white p-8 rounded-xl shadow-lg w-full max-w-md border border-gray-100">
                <h2 className="text-3xl font-extrabold text-center text-eco-green mb-2">Create Account</h2>
                <p className="text-gray-500 text-center text-sm mb-6">Join EcoBazaar to start sustainable shopping</p>
                
                {error && (
                    <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 mb-6 rounded-lg text-sm flex items-start justify-between">
                        <span>{error}</span>
                        <button 
                            type="button" 
                            onClick={() => setError('')} 
                            className="text-red-500 hover:text-red-700 font-bold ml-2 leading-none"
                            aria-label="Dismiss error"
                        >
                            &times;
                        </button>
                    </div>
                )}
                
                <form onSubmit={handleSubmit} className="space-y-4">
                    <div>
                        <label className="block text-gray-700 text-sm font-semibold mb-1">Full Name</label>
                        <input 
                            name="fullName" 
                            value={formData.fullName}
                            onChange={handleChange} 
                            placeholder="John Doe"
                            className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-eco-green focus:border-transparent transition" 
                            required 
                            disabled={loading}
                        />
                    </div>
                    <div>
                        <label className="block text-gray-700 text-sm font-semibold mb-1">Email Address</label>
                        <input 
                            name="email" 
                            type="email" 
                            value={formData.email}
                            onChange={handleChange} 
                            placeholder="you@example.com"
                            className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-eco-green focus:border-transparent transition" 
                            required 
                            disabled={loading}
                        />
                    </div>
                    <div>
                        <label className="block text-gray-700 text-sm font-semibold mb-1">Password</label>
                        <input 
                            name="password" 
                            type="password" 
                            value={formData.password}
                            onChange={handleChange} 
                            placeholder="••••••••"
                            className="w-full p-3 border border-gray-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-eco-green focus:border-transparent transition" 
                            required 
                            disabled={loading}
                        />
                    </div>
                    <div>
                        <label className="block text-gray-700 text-sm font-semibold mb-1">I am a...</label>
                        <select 
                            name="role" 
                            value={formData.role}
                            onChange={handleChange} 
                            className="w-full p-3 border border-gray-300 rounded-lg bg-white focus:outline-none focus:ring-2 focus:ring-eco-green focus:border-transparent transition"
                            disabled={loading}
                        >
                            <option value="USER">Shopper (Buy Products)</option>
                            <option value="SELLER">Seller (Sell Products)</option>
                        </select>
                    </div>
                    <button 
                        type="submit" 
                        disabled={loading}
                        className={`w-full py-3 px-4 rounded-lg text-white font-bold transition duration-200 flex items-center justify-center shadow-md ${
                            loading
                                ? 'bg-gray-400 cursor-not-allowed'
                                : 'bg-eco-green hover:bg-eco-dark active:scale-[0.99]'
                        }`}
                    >
                        {loading ? (
                            <>
                                <svg className="animate-spin -ml-1 mr-3 h-5 w-5 text-white" xmlns="http://www.w3.org/2000/svg" fill="none" viewBox="0 0 24 24">
                                    <circle className="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" strokeWidth="4"></circle>
                                    <path className="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                                </svg>
                                Creating Account...
                            </>
                        ) : (
                            'Register'
                        )}
                    </button>
                </form>
                <p className="mt-6 text-center text-sm text-gray-600">
                    Already have an account? <Link to="/login" className="text-eco-green font-bold hover:underline">Login</Link>
                </p>
            </div>
        </div>
    );
};

export default Register;