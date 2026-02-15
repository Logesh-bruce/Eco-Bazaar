import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { useCart } from '../context/CartContext';
import { ShoppingCart, LogOut, Store, PlusCircle, Heart } from 'lucide-react';

const Navbar = () => {
    const { user, logout } = useAuth();
    const { cartCount } = useCart();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
        <nav className="bg-white shadow-md sticky top-0 z-50">
            <div className="container mx-auto px-4 py-3 flex justify-between items-center">
                {/* Logo */}
                <Link to="/" className="text-2xl font-bold text-eco-green flex items-center gap-2">
                    <Store className="w-8 h-8" />
                    EcoBazaar
                </Link>

                {/* Navigation Links */}
                <div className="flex items-center gap-6 text-gray-700 font-medium">
                    <Link to="/" className="hover:text-eco-green transition">Home</Link>
                    <Link to="/products" className="hover:text-eco-green transition">Shop</Link>

                    {user ? (
                        <>
                            {/* --- SELLER LINKS --- */}
                            {user.role === 'SELLER' && (
                                <>
                                    <Link to="/seller/dashboard" className="hover:text-eco-green">Dashboard</Link>
                                    <Link to="/seller/add" className="flex items-center gap-1 text-eco-green hover:text-eco-dark">
                                        <PlusCircle size={18} /> Add Product
                                    </Link>
                                </>
                            )}

                            {/* --- SHOPPER LINKS --- */}
                            {user.role === 'USER' && (
                                <>
                                    {/* Wishlist Link */}
                                    <Link to="/wishlist" className="hover:text-eco-green relative" title="Wishlist">
                                        <Heart size={24} />
                                    </Link>

                                    {/* Cart Link with Badge */}
                                    <Link to="/cart" className="relative hover:text-eco-green">
                                        <ShoppingCart size={24} />
                                        {/* Dynamic Badge */}
                                        {cartCount > 0 && (
                                            <span className="absolute -top-2 -right-2 bg-red-500 text-white text-xs rounded-full h-5 w-5 flex items-center justify-center animate-bounce">
                                                {cartCount}
                                            </span>
                                        )}
                                    </Link>
                                </>
                            )}

                            {/* --- ADMIN LINKS --- */}
                            {user.role === 'ADMIN' && (
                                <Link to="/admin" className="text-red-600 hover:text-red-800">Admin Panel</Link>
                            )}

                            {/* --- PROFILE & LOGOUT --- */}
                            <div className="flex items-center gap-3 border-l pl-4 ml-2">
                                {/* Clickable Profile Link */}
                                <Link to="/profile" className="flex flex-col text-right hidden md:block cursor-pointer hover:opacity-80">
                                    <span className="text-xs text-gray-500">Welcome,</span>
                                    <span className="text-sm font-bold leading-none">{user.fullName}</span>
                                </Link>
                                
                                <button 
                                    onClick={handleLogout} 
                                    className="text-gray-500 hover:text-red-500 transition"
                                    title="Logout"
                                >
                                    <LogOut size={20} />
                                </button>
                            </div>
                        </>
                    ) : (
                        /* --- GUEST LINKS --- */
                        <div className="flex gap-4">
                            <Link to="/login" className="hover:text-eco-green">Login</Link>
                            <Link to="/register" className="bg-eco-green text-white px-4 py-2 rounded-md hover:bg-eco-dark transition">
                                Sign Up
                            </Link>
                        </div>
                    )}
                </div>
            </div>
        </nav>
    );
};

export default Navbar;