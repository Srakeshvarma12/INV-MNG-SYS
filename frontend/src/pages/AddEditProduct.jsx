import React, { useState, useEffect } from 'react';
import { useNavigate, useParams, Link } from 'react-router-dom';
import API from '../api/api';

const AddEditProduct = () => {
  const { id } = useParams();
  const navigate = useNavigate();
  const isEditMode = !!id;

  const [formData, setFormData] = useState({
    name: '',
    sku: '',
    categoryId: '',
    supplierId: '',
    price: '',
    quantity: '0',
    minQuantity: '5'
  });

  const [categories, setCategories] = useState([]);
  const [suppliers, setSuppliers] = useState([]);
  const [loading, setLoading] = useState(false);
  const [dataFetching, setDataFetching] = useState(isEditMode);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchMetadata = async () => {
      try {
        const [categoriesRes, suppliersRes] = await Promise.all([
          API.get('/categories'),
          API.get('/suppliers')
        ]);
        setCategories(categoriesRes.data?.data || []);
        setSuppliers(suppliersRes.data?.data || []);
      } catch (err) {
        console.error('Error fetching form metadata:', err);
        setError('Failed to load categories/suppliers list');
      }
    };

    const fetchProduct = async () => {
      try {
        const response = await API.get(`/products/${id}`);
        const product = response.data?.data;
        if (product) {
          setFormData({
            name: product.name,
            sku: product.sku,
            categoryId: product.category?.id || '',
            supplierId: product.supplier?.id || '',
            price: product.price,
            quantity: String(product.quantity),
            minQuantity: String(product.minQuantity)
          });
        }
      } catch (err) {
        console.error('Error fetching product for edit:', err);
        setError('Failed to load product details');
      } finally {
        setDataFetching(false);
      }
    };

    fetchMetadata().then(() => {
      if (isEditMode) {
        fetchProduct();
      }
    });
  }, [id, isEditMode]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (!formData.name.trim() || !formData.sku.trim() || !formData.categoryId || !formData.price) {
      setError('Please fill in all required fields (Name, SKU, Category, and Price)');
      return;
    }

    const priceNum = parseFloat(formData.price);
    if (isNaN(priceNum) || priceNum < 0) {
      setError('Price must be a valid non-negative number');
      return;
    }

    const qtyNum = parseInt(formData.quantity);
    if (isNaN(qtyNum) || qtyNum < 0) {
      setError('Quantity must be a valid non-negative integer');
      return;
    }

    const minQtyNum = parseInt(formData.minQuantity);
    if (isNaN(minQtyNum) || minQtyNum < 0) {
      setError('Min quantity must be a valid non-negative integer');
      return;
    }

    setLoading(true);
    try {
      const payload = {
        name: formData.name,
        sku: formData.sku,
        categoryId: parseInt(formData.categoryId),
        supplierId: formData.supplierId ? parseInt(formData.supplierId) : null,
        price: priceNum,
        quantity: qtyNum,
        minQuantity: minQtyNum
      };

      if (isEditMode) {
        await API.put(`/products/${id}`, payload);
      } else {
        await API.post('/products', payload);
      }
      navigate('/products');
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to save product details');
    } finally {
      setLoading(false);
    }
  };

  if (dataFetching) {
    return (
      <div className="spinner-container">
        <div className="spinner"></div>
      </div>
    );
  }

  return (
    <div className="container" style={{ maxWidth: '700px' }}>
      <div className="page-header">
        <h1 className="page-title">
          &gt;_ {isEditMode ? 'EDIT_PRODUCT_SKU' : 'CREATE_PRODUCT_SKU'}
        </h1>
        <p className="page-subtitle">
          {isEditMode ? `MODIFY SKU RECORD #${id}` : 'PROVISION NEW SKU RESOURCE IN CATALOG'}
        </p>
      </div>

      {error && (
        <div className="alert alert-error">
          [ERROR]: {error}
        </div>
      )}

      <form onSubmit={handleSubmit} className="form-container">
        <div className="grid-2">
          {/* Name */}
          <div className="form-group">
            <label className="form-label">
              Product Name <span style={{ color: 'var(--danger-color)' }}>*</span>
            </label>
            <input
              type="text"
              name="name"
              value={formData.name}
              onChange={handleChange}
              disabled={loading}
              className="input-field"
              placeholder="e.g. Mechanical Keyboard"
            />
          </div>

          {/* SKU */}
          <div className="form-group">
            <label className="form-label">
              SKU Identifier <span style={{ color: 'var(--danger-color)' }}>*</span>
            </label>
            <input
              type="text"
              name="sku"
              value={formData.sku}
              onChange={handleChange}
              disabled={loading}
              className="input-field"
              placeholder="e.g. ELEC-KEY-002"
            />
          </div>

          {/* Category */}
          <div className="form-group">
            <label className="form-label">
              Category Mapping <span style={{ color: 'var(--danger-color)' }}>*</span>
            </label>
            <select
              name="categoryId"
              value={formData.categoryId}
              onChange={handleChange}
              disabled={loading}
              className="select-field"
            >
              <option value="">SELECT CATEGORY</option>
              {categories.map(c => (
                <option key={c.id} value={c.id}>{c.name.toUpperCase()}</option>
              ))}
            </select>
          </div>

          {/* Supplier */}
          <div className="form-group">
            <label className="form-label">Supplier Connection</label>
            <select
              name="supplierId"
              value={formData.supplierId}
              onChange={handleChange}
              disabled={loading}
              className="select-field"
            >
              <option value="">SELECT SUPPLIER (OPTIONAL)</option>
              {suppliers.map(s => (
                <option key={s.id} value={s.id}>{s.name.toUpperCase()}</option>
              ))}
            </select>
          </div>

          {/* Price */}
          <div className="form-group">
            <label className="form-label">
              Price ($ USD) <span style={{ color: 'var(--danger-color)' }}>*</span>
            </label>
            <input
              type="number"
              step="0.01"
              name="price"
              value={formData.price}
              onChange={handleChange}
              disabled={loading}
              className="input-field"
              placeholder="e.g. 29.99"
            />
          </div>

          {/* Min Quantity */}
          <div className="form-group">
            <label className="form-label">Low-Stock Alert Level</label>
            <input
              type="number"
              name="minQuantity"
              value={formData.minQuantity}
              onChange={handleChange}
              disabled={loading}
              className="input-field"
              placeholder="e.g. 5"
            />
          </div>

          {/* Quantity (Add Only, disabled on edit) */}
          {!isEditMode && (
            <div className="form-group">
              <label className="form-label">Initial Stock Quantity</label>
              <input
                type="number"
                name="quantity"
                value={formData.quantity}
                onChange={handleChange}
                disabled={loading}
                className="input-field"
                placeholder="e.g. 10"
              />
            </div>
          )}
        </div>

        {/* Form Actions */}
        <div className="flex" style={{ justifyContent: 'flex-end', gap: '15px', borderTop: '1px solid var(--border-color)', paddingTop: '20px', marginTop: '10px' }}>
          <Link to="/products" className="btn" style={{ textDecoration: 'none' }}>
            Cancel
          </Link>
          <button
            type="submit"
            disabled={loading}
            className="btn btn-primary"
          >
            {loading ? 'SAVING...' : 'COMMIT RECORD'}
          </button>
        </div>
      </form>
    </div>
  );
};

export default AddEditProduct;
