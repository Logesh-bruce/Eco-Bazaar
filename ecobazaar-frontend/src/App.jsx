import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import { CartProvider } from './context/CartContext'; // <--- Import
import Navbar from './components/Navbar';
import Login from './pages/Login';
import Register from './pages/Register';
import Home from './pages/Home';
import SellerDashboard from './pages/SellerDashboard';
import AdminDashboard from './pages/AdminDashboard';
import AddProduct from './pages/AddProduct';
import ProductDetails from './pages/ProductDetails';
import EditProduct from './pages/EditProduct';
import Cart from './pages/Cart';
import Checkout from './pages/Checkout';
import Orders from './pages/Orders';
import Shop from './pages/Shop';
import Wishlist from './pages/Wishlist';
import UserProfile from './pages/UserProfile';

function App() {
  return (
    <AuthProvider>
      <CartProvider> {/* <--- Wrap here */}
        <Router>
          <Navbar />
          <div className="container mx-auto px-4 py-8">
            <Routes>
              <Route path="/" element={<Home />} />
              <Route path="/login" element={<Login />} />
              <Route path="/register" element={<Register />} />
              <Route path="/products" element={<Shop />} />
              <Route path="/seller/dashboard" element={<SellerDashboard />} />
              <Route path="/seller/add" element={<AddProduct />} />
              <Route path="/seller/edit/:id" element={<EditProduct />} />
              <Route path="/cart" element={<Cart />} />
              <Route path="/checkout" element={<Checkout />} />
              <Route path="/orders" element={<Orders />} />
              <Route path="/product/:id" element={<ProductDetails />} />
              <Route path="/wishlist" element={<Wishlist />} />
              <Route path="/profile" element={<UserProfile />} />
              <Route path="/admin" element={<AdminDashboard />} />
            </Routes>
          </div>
        </Router>
      </CartProvider>
    </AuthProvider>
  );
}

export default App;