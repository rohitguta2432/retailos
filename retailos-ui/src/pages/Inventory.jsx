import { useState } from 'react';
import { Search, SlidersHorizontal, Plus, MapPin, X } from 'lucide-react';
import { mockProducts, mockCategories } from '../data/mockData';
import './Inventory.css';

const formatCurrency = (amount) => `₹${amount.toLocaleString('en-IN')}`;

function getStockStatus(stock) {
  if (stock === 0) return { label: 'Out of Stock', class: 'badge-danger' };
  if (stock <= 5) return { label: `${stock} left`, class: 'badge-warning' };
  return { label: 'In Stock', class: 'badge-success' };
}

const emojis = ['📦', '🛒', '🏷️', '🧴', '🧹', '🍫', '🥤', '🧃', '🍪', '🧂'];

export default function Inventory() {
  const [products, setProducts] = useState(mockProducts);
  const [search, setSearch] = useState('');
  const [category, setCategory] = useState('All');
  const [showAddModal, setShowAddModal] = useState(false);
  const [newProduct, setNewProduct] = useState({
    name: '', sku: '', category: 'Essentials', price: '', stock: '', location: ''
  });

  const handleSaveProduct = () => {
    if (!newProduct.name || !newProduct.price || !newProduct.stock) return;

    const product = {
      id: Date.now(),
      name: newProduct.name,
      sku: newProduct.sku || newProduct.name.substring(0, 3).toUpperCase() + '-' + Math.floor(Math.random() * 900 + 100),
      category: newProduct.category,
      price: Number(newProduct.price),
      stock: Number(newProduct.stock),
      location: newProduct.location || 'Unassigned',
      image: emojis[Math.floor(Math.random() * emojis.length)],
    };

    setProducts(prev => [product, ...prev]);
    setNewProduct({ name: '', sku: '', category: 'Essentials', price: '', stock: '', location: '' });
    setShowAddModal(false);
  };

  const filtered = products.filter(p => {
    const matchSearch = p.name.toLowerCase().includes(search.toLowerCase()) ||
                        p.sku.toLowerCase().includes(search.toLowerCase());
    const matchCategory = category === 'All' || p.category === category;
    return matchSearch && matchCategory;
  });

  return (
    <div className="page">
      {/* Header */}
      <div className="page-header animate-fade-up">
        <div>
          <h1 className="page-title">Inventory</h1>
          <p className="page-subtitle">{products.length} products</p>
        </div>
        <button className="btn btn-primary btn-sm" onClick={() => setShowAddModal(true)} id="add-product-btn">
          <Plus size={16} />
          Add
        </button>
      </div>

      {/* Search */}
      <div className="inv-search-bar animate-fade-up" style={{ animationDelay: '0.05s' }}>
        <div className="inv-search-input-wrap">
          <Search size={18} className="inv-search-icon" />
          <input
            className="input inv-search-input"
            placeholder="Search products, SKU..."
            value={search}
            onChange={e => setSearch(e.target.value)}
            id="inventory-search"
          />
        </div>
      </div>

      {/* Category Filter */}
      <div className="inv-categories animate-fade-up" style={{ animationDelay: '0.1s' }}>
        {mockCategories.map(cat => (
          <button
            key={cat}
            className={`inv-cat-chip ${category === cat ? 'inv-cat-chip-active' : ''}`}
            onClick={() => setCategory(cat)}
          >
            {cat}
          </button>
        ))}
      </div>

      {/* Products List */}
      <div className="inv-products stagger-children">
        {filtered.map(product => {
          const status = getStockStatus(product.stock);
          return (
            <div key={product.id} className="inv-product glass-card-static" id={`product-${product.id}`}>
              <span className="inv-product-emoji">{product.image}</span>
              <div className="inv-product-info">
                <span className="inv-product-name">{product.name}</span>
                <div className="inv-product-meta">
                  <span className="inv-product-sku">{product.sku}</span>
                  <span className="inv-product-dot">·</span>
                  <span className="inv-product-loc">
                    <MapPin size={10} />
                    {product.location}
                  </span>
                </div>
              </div>
              <div className="inv-product-right">
                <span className="inv-product-price">{formatCurrency(product.price)}</span>
                <span className={`badge ${status.class}`}>{status.label}</span>
              </div>
            </div>
          );
        })}
        {filtered.length === 0 && (
          <div className="empty-state">
            <Search size={48} />
            <p>No products found matching your search</p>
          </div>
        )}
      </div>

      {/* Add Product Modal */}
      {showAddModal && (
        <div className="modal-overlay" onClick={() => setShowAddModal(false)}>
          <div className="modal-content" onClick={e => e.stopPropagation()}>
            <div className="modal-header">
              <h2 className="modal-title">Add Product</h2>
              <button className="btn-icon" onClick={() => setShowAddModal(false)} id="close-add-modal">
                <X size={20} />
              </button>
            </div>

            <div className="form-group">
              <label className="form-label">Product Name *</label>
              <input className="input" placeholder="e.g. Parle-G Biscuit" value={newProduct.name}
                onChange={e => setNewProduct({ ...newProduct, name: e.target.value })} id="product-name-input" />
            </div>

            <div className="form-row">
              <div className="form-group">
                <label className="form-label">SKU Code</label>
                <input className="input" placeholder="e.g. PLG-100" value={newProduct.sku}
                  onChange={e => setNewProduct({ ...newProduct, sku: e.target.value })} />
              </div>
              <div className="form-group">
                <label className="form-label">Category</label>
                <select className="input" value={newProduct.category}
                  onChange={e => setNewProduct({ ...newProduct, category: e.target.value })}>
                  {mockCategories.filter(c => c !== 'All').map(c => (
                    <option key={c} value={c}>{c}</option>
                  ))}
                </select>
              </div>
            </div>

            <div className="form-row">
              <div className="form-group">
                <label className="form-label">Price (₹) *</label>
                <input className="input" type="number" placeholder="0" value={newProduct.price}
                  onChange={e => setNewProduct({ ...newProduct, price: e.target.value })} />
              </div>
              <div className="form-group">
                <label className="form-label">Stock Qty *</label>
                <input className="input" type="number" placeholder="0" value={newProduct.stock}
                  onChange={e => setNewProduct({ ...newProduct, stock: e.target.value })} />
              </div>
            </div>

            <div className="form-group">
              <label className="form-label">Location Label</label>
              <input className="input" placeholder="e.g. Shelf A-1" value={newProduct.location}
                onChange={e => setNewProduct({ ...newProduct, location: e.target.value })} />
            </div>

            <button className="btn btn-primary" style={{ width: '100%', marginTop: '8px' }} onClick={handleSaveProduct} id="save-product-btn">
              Save Product
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
