import React, { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import API from '../api/api';

const ProductList = () => {
  const { user } = useAuth();
  const [products, setProducts] = useState([]);
  const [categories, setCategories] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [success, setSuccess] = useState('');

  // Search & Filter state
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('');
  const [stockFilter, setStockFilter] = useState('all'); // all, normal, low

  // Sorting state
  const [sortField, setSortField] = useState('name');
  const [sortOrder, setSortOrder] = useState('asc'); // asc, desc

  const fetchData = async () => {
    setLoading(true);
    setError('');
    try {
      const [productsRes, categoriesRes] = await Promise.all([
        API.get('/products'),
        API.get('/categories')
      ]);
      setProducts(productsRes.data?.data || []);
      setCategories(categoriesRes.data?.data || []);
    } catch (err) {
      setError('Failed to fetch inventory catalog');
      console.error('Error fetching catalog data:', err);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchData();
  }, []);

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this product?')) return;
    setError('');
    setSuccess('');
    try {
      await API.delete(`/products/${id}`);
      setSuccess('Product deleted successfully');
      setProducts(products.filter(p => p.id !== id));
      setTimeout(() => setSuccess(''), 3000);
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to delete product');
    }
  };

  const handleSort = (field) => {
    if (sortField === field) {
      setSortOrder(sortOrder === 'asc' ? 'desc' : 'asc');
    } else {
      setSortField(field);
      setSortOrder('asc');
    }
  };

  const filteredProducts = products
    .filter(product => {
      const matchesSearch =
        product.name.toLowerCase().includes(searchTerm.toLowerCase()) ||
        product.sku.toLowerCase().includes(searchTerm.toLowerCase());
      
      const matchesCategory =
        !selectedCategory || product.category?.id === parseInt(selectedCategory);

      let matchesStock = true;
      if (stockFilter === 'low') {
        matchesStock = product.quantity <= product.minQuantity;
      } else if (stockFilter === 'normal') {
        matchesStock = product.quantity > product.minQuantity;
      }

      return matchesSearch && matchesCategory && matchesStock;
    })
    .sort((a, b) => {
      let valueA, valueB;

      switch (sortField) {
        case 'sku':
          valueA = a.sku;
          valueB = b.sku;
          break;
        case 'price':
          valueA = Number(a.price);
          valueB = Number(b.price);
          break;
        case 'quantity':
          valueA = a.quantity;
          valueB = b.quantity;
          break;
        case 'category':
          valueA = a.category?.name || '';
          valueB = b.category?.name || '';
          break;
        case 'name':
        default:
          valueA = a.name.toLowerCase();
          valueB = b.name.toLowerCase();
          break;
      }

      if (valueA < valueB) return sortOrder === 'asc' ? -1 : 1;
      if (valueA > valueB) return sortOrder === 'asc' ? 1 : -1;
      return 0;
    });

  const renderSortIndicator = (field) => {
    if (sortField !== field) return ' ⇅';
    return sortOrder === 'asc' ? ' ▲' : ' ▼';
  };

  return (
    <div className="container">
      <div className="flex justify-between items-center" style={{ marginBottom: '30px' }}>
        <div className="page-header" style={{ marginBottom: 0 }}>
          <h1 className="page-title">&gt;_ PRODUCT_CATALOG</h1>
          <p className="page-subtitle">STOCK LEVEL AND CATALOG MANAGEMENT</p>
        </div>
        {user.role === 'ADMIN' && (
          <Link to="/products/new" className="btn btn-primary" style={{ textDecoration: 'none' }}>
            + Create New SKU
          </Link>
        )}
      </div>

      {error && (
        <div className="alert alert-error">
          [ERROR]: {error}
        </div>
      )}

      {success && (
        <div className="alert alert-success">
          [SUCCESS]: {success}
        </div>
      )}

      {/* Filters Toolbar */}
      <div className="toolbar">
        <div className="toolbar-item">
          <label className="form-label">Search SKU / Name</label>
          <input
            type="text"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="input-field"
            placeholder="Type search terms..."
          />
        </div>

        <div className="toolbar-item">
          <label className="form-label">Filter Category</label>
          <select
            value={selectedCategory}
            onChange={(e) => setSelectedCategory(e.target.value)}
            className="select-field"
          >
            <option value="">ALL CATEGORIES</option>
            {categories.map(c => (
              <option key={c.id} value={c.id}>{c.name.toUpperCase()}</option>
            ))}
          </select>
        </div>

        <div className="toolbar-item">
          <label className="form-label">Stock Level Alert</label>
          <select
            value={stockFilter}
            onChange={(e) => setStockFilter(e.target.value)}
            className="select-field"
          >
            <option value="all">ALL LEVELS</option>
            <option value="normal">NORMAL STOCK ONLY</option>
            <option value="low">LOW STOCK WARNING ONLY</option>
          </select>
        </div>

        <div className="toolbar-stats">
          <p className="form-label" style={{ marginBottom: '2px' }}>FILTERED ITEMS</p>
          <p style={{ fontSize: '1.2rem', fontWeight: 'bold', color: '#fff' }}>
            {filteredProducts.length} / {products.length}
          </p>
        </div>
      </div>

      {/* Products Table */}
      <div className="table-container">
        {loading ? (
          <div className="spinner-container">
            <div className="spinner"></div>
          </div>
        ) : (
          <table className="console-table">
            <thead>
              <tr>
                <th onClick={() => handleSort('sku')}>SKU {renderSortIndicator('sku')}</th>
                <th onClick={() => handleSort('name')}>Product Name {renderSortIndicator('name')}</th>
                <th onClick={() => handleSort('category')}>Category {renderSortIndicator('category')}</th>
                <th>Supplier</th>
                <th onClick={() => handleSort('price')} style={{ textAlign: 'right' }}>Price {renderSortIndicator('price')}</th>
                <th onClick={() => handleSort('quantity')} style={{ textAlign: 'center' }}>Quantity {renderSortIndicator('quantity')}</th>
                <th style={{ textAlign: 'center' }}>Status</th>
                {user.role === 'ADMIN' && <th style={{ textAlign: 'right' }}>Actions</th>}
              </tr>
            </thead>
            <tbody>
              {filteredProducts.length === 0 ? (
                <tr>
                  <td colSpan={user.role === 'ADMIN' ? 8 : 7} style={{ textAlign: 'center', py: '30px', color: 'var(--text-muted)' }}>
                    No matching inventory records found
                  </td>
                </tr>
              ) : (
                filteredProducts.map((product) => {
                  const isLowStock = product.quantity <= product.minQuantity;
                  return (
                    <tr key={product.id}>
                      <td style={{ color: 'var(--text-muted)', fontWeight: '600' }}>{product.sku}</td>
                      <td style={{ color: '#fff', fontWeight: '700' }}>{product.name}</td>
                      <td>
                        <span className="badge" style={{ backgroundColor: 'var(--bg-color)', border: '1px solid var(--border-color)', color: 'var(--text-main)' }}>
                          {product.category?.name}
                        </span>
                      </td>
                      <td>{product.supplier?.name || '-'}</td>
                      <td style={{ textAlign: 'right', fontWeight: '700' }}>${Number(product.price).toFixed(2)}</td>
                      <td style={{ textAlign: 'center', fontWeight: '700' }}>
                        <span style={{ color: isLowStock ? 'var(--danger-color)' : 'var(--text-main)' }}>
                          {product.quantity}
                        </span>
                        <span style={{ fontSize: '0.65rem', color: 'var(--text-muted)', fontWeight: 'normal' }}> (min: {product.minQuantity})</span>
                      </td>
                      <td style={{ textAlign: 'center' }}>
                        {isLowStock ? (
                          <span className="badge badge-low">Low Stock</span>
                        ) : (
                          <span className="badge badge-ok">In Stock</span>
                        )}
                      </td>
                      {user.role === 'ADMIN' && (
                        <td style={{ textAlign: 'right' }}>
                          <div className="flex gap-2" style={{ justifyContent: 'flex-end' }}>
                            <Link to={`/products/${product.id}/edit`} className="btn btn-sm" style={{ textDecoration: 'none' }}>
                              Edit
                            </Link>
                            <button onClick={() => handleDelete(product.id)} className="btn btn-danger btn-sm">
                              Delete
                            </button>
                          </div>
                        </td>
                      )}
                    </tr>
                  );
                })
              )}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
};

export default ProductList;
