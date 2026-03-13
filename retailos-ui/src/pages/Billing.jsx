import { useState } from 'react';
import { Search, Plus, Minus, Trash2, CreditCard, Banknote, BookOpen, ShoppingCart } from 'lucide-react';
import { mockProducts } from '../data/mockData';
import './Billing.css';

const formatCurrency = (amount) => `₹${amount.toLocaleString('en-IN')}`;

export default function Billing() {
  const [search, setSearch] = useState('');
  const [cart, setCart] = useState([]);
  const [paymentMode, setPaymentMode] = useState('cash');

  const filtered = search.length >= 2
    ? mockProducts.filter(p =>
        p.name.toLowerCase().includes(search.toLowerCase()) ||
        p.sku.toLowerCase().includes(search.toLowerCase())
      ).slice(0, 5)
    : [];

  const addToCart = (product) => {
    setCart(prev => {
      const exists = prev.find(i => i.id === product.id);
      if (exists) {
        return prev.map(i => i.id === product.id ? { ...i, qty: i.qty + 1 } : i);
      }
      return [...prev, { ...product, qty: 1 }];
    });
    setSearch('');
  };

  const updateQty = (id, delta) => {
    setCart(prev => prev.map(i => {
      if (i.id !== id) return i;
      const newQty = i.qty + delta;
      return newQty > 0 ? { ...i, qty: newQty } : i;
    }));
  };

  const removeItem = (id) => {
    setCart(prev => prev.filter(i => i.id !== id));
  };

  const subtotal = cart.reduce((sum, i) => sum + (i.price * i.qty), 0);
  const gst = Math.round(subtotal * 0.05);
  const total = subtotal + gst;

  return (
    <div className="page">
      {/* Header */}
      <div className="page-header animate-fade-up">
        <div>
          <h1 className="page-title">New Bill</h1>
          <p className="page-subtitle">{cart.length} items in cart</p>
        </div>
      </div>

      {/* Product Search */}
      <div className="bill-search-wrap animate-fade-up" style={{ animationDelay: '0.05s' }}>
        <div className="inv-search-input-wrap">
          <Search size={18} className="inv-search-icon" />
          <input
            className="input inv-search-input"
            placeholder="Search product or scan barcode..."
            value={search}
            onChange={e => setSearch(e.target.value)}
            id="billing-search"
          />
        </div>

        {/* Search Results Dropdown */}
        {filtered.length > 0 && (
          <div className="bill-search-results">
            {filtered.map(product => (
              <button
                key={product.id}
                className="bill-search-item"
                onClick={() => addToCart(product)}
                id={`add-${product.id}`}
              >
                <span className="bill-result-emoji">{product.image}</span>
                <div className="bill-result-info">
                  <span className="bill-result-name">{product.name}</span>
                  <span className="bill-result-sku">{product.sku}</span>
                </div>
                <span className="bill-result-price">{formatCurrency(product.price)}</span>
                <Plus size={16} />
              </button>
            ))}
          </div>
        )}
      </div>

      {/* Cart */}
      {cart.length === 0 ? (
        <div className="empty-state animate-fade-up" style={{ animationDelay: '0.1s' }}>
          <ShoppingCart size={56} />
          <p>Search and add products to start billing</p>
        </div>
      ) : (
        <>
          <div className="bill-cart stagger-children">
            {cart.map(item => (
              <div key={item.id} className="bill-cart-item glass-card-static">
                <span className="bill-cart-emoji">{item.image}</span>
                <div className="bill-cart-info">
                  <span className="bill-cart-name">{item.name}</span>
                  <span className="bill-cart-price">{formatCurrency(item.price)} × {item.qty}</span>
                </div>
                <div className="bill-cart-controls">
                  <button className="qty-btn" onClick={() => updateQty(item.id, -1)}>
                    <Minus size={14} />
                  </button>
                  <span className="qty-value">{item.qty}</span>
                  <button className="qty-btn" onClick={() => updateQty(item.id, 1)}>
                    <Plus size={14} />
                  </button>
                </div>
                <span className="bill-cart-total">{formatCurrency(item.price * item.qty)}</span>
                <button className="qty-btn qty-btn-delete" onClick={() => removeItem(item.id)}>
                  <Trash2 size={14} />
                </button>
              </div>
            ))}
          </div>

          {/* Payment Section */}
          <div className="bill-payment glass-card-static animate-fade-up">
            <h3 className="section-title" style={{ marginBottom: '12px' }}>Payment</h3>

            {/* Payment Mode */}
            <div className="payment-modes">
              <button
                className={`payment-mode-btn ${paymentMode === 'cash' ? 'payment-mode-active' : ''}`}
                onClick={() => setPaymentMode('cash')}
                id="pay-cash"
              >
                <Banknote size={18} />
                <span>Cash</span>
              </button>
              <button
                className={`payment-mode-btn ${paymentMode === 'upi' ? 'payment-mode-active' : ''}`}
                onClick={() => setPaymentMode('upi')}
                id="pay-upi"
              >
                <CreditCard size={18} />
                <span>UPI</span>
              </button>
              <button
                className={`payment-mode-btn ${paymentMode === 'khata' ? 'payment-mode-active' : ''}`}
                onClick={() => setPaymentMode('khata')}
                id="pay-khata"
              >
                <BookOpen size={18} />
                <span>Khata</span>
              </button>
            </div>

            {/* Summary */}
            <div className="bill-summary">
              <div className="bill-summary-row">
                <span>Subtotal</span>
                <span>{formatCurrency(subtotal)}</span>
              </div>
              <div className="bill-summary-row">
                <span>GST (5%)</span>
                <span>{formatCurrency(gst)}</span>
              </div>
              <div className="bill-summary-row bill-summary-total">
                <span>Total</span>
                <span>{formatCurrency(total)}</span>
              </div>
            </div>

            <button className="btn btn-accent" style={{ width: '100%', padding: '14px' }} id="create-bill-btn">
              Create Bill — {formatCurrency(total)}
            </button>
          </div>
        </>
      )}
    </div>
  );
}
