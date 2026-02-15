import { useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate, Link } from 'react-router-dom';

const Login = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState('');
    const { login } = useAuth();
    const navigate = useNavigate();

    const handleSubmit = async (e) => {
        e.preventDefault();
        setError('');
        
        const result = await login(email, password);
        
        if (result.success) {
            // Redirect based on role
            // We'll fix these routes later, but this is the logic:
            navigate('/');
        } else {
            setError(result.message);
        }
    };

    return (
        <div className="flex justify-center items-center h-screen bg-gray-100">
            <div className="bg-white p-8 rounded-lg shadow-md w-96">
                <h2 className="text-2xl font-bold text-center text-eco-green mb-6">Welcome Back</h2>
                
                {error && <div className="bg-red-100 text-red-700 p-2 mb-4 rounded">{error}</div>}
                
                <form onSubmit={handleSubmit}>
                    <div className="mb-4">
                        <label className="block text-gray-700 text-sm font-bold mb-2">Email</label>
                        <input 
                            type="email" 
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            className="w-full p-2 border rounded focus:outline-none focus:border-eco-green"
                            required
                        />
                    </div>
                    <div className="mb-6">
                        <label className="block text-gray-700 text-sm font-bold mb-2">Password</label>
                        <input 
                            type="password" 
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            className="w-full p-2 border rounded focus:outline-none focus:border-eco-green"
                            required
                        />
                    </div>
                    <button type="submit" className="w-full bg-eco-green text-white p-2 rounded hover:bg-eco-dark transition">
                        Login
                    </button>
                </form>
                <p className="mt-4 text-center text-sm">
                    Don't have an account? <Link to="/register" className="text-eco-green font-bold">Sign Up</Link>
                </p>
            </div>
        </div>
    );
};

export default Login;